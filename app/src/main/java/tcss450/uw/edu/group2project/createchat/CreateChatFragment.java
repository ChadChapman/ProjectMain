package tcss450.uw.edu.group2project.createchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.ChatFragment;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.utils.MyRecyclerViewAdapter;
import tcss450.uw.edu.group2project.utils.OnItemClickListener;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;


public class CreateChatFragment extends Fragment {
    private static final String TAG = "create_chat_fragment";
    //private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private List<ContactFeedItem> mContactFeedItemList;
    private int mUserMemberID;
    private String mNewChatIDStr;
    private Uri mNewChatUri;
    private Uri mContactsUri;
    private View v;
    private List<String> mNewChatIncludedUsernamesList;
    private ImageButton createButton;
    private TextView mUsernamesDisplayTextView;
    private int mNewChatIDFromResponse;


    public CreateChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       v = inflater.inflate(R.layout.fragment_create_chat, container, false);
       mRecyclerView = v.findViewById(R.id.create_chat_recycle_view);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       mNewChatUri = buildHerokuNewChatUri();
       mContactsUri = buildHerokuVerifiedContactsUri();
       progressBar = v.findViewById(R.id.create_chat_progress_bar);
       mUsernamesDisplayTextView = v.findViewById(R.id.createChatUsernamesDisplay);
//       Log.e("CURRENTLY INTHE TEXTVIEW: ", mUsernamesDisplayTextView.getText().toString());
       Bundle bundle = this.getArguments();
       if (bundle != null) {
           mUserMemberID = Integer.parseInt(bundle.getString("memberid"));
       }
       loadVerifiedContacts();
       createButton = v.findViewById(R.id.createNewChatFragNewChatButton);
       createButton.setOnClickListener(view -> {
            sendNewChatRequest();
       });

       return v;
    }

    public void loadVerifiedContacts() {
        JSONObject jsonObject = createVerifiedContactsRequestObject();
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mContactsUri.toString(), jsonObject)
                .onPostExecute(this::handleContactsQueryResponseOnPostExec)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void parseHerokuResult(String result) {
        //String imgAddress = "https://www.logoground.com/uploads/2017108832017-04-203705844rabbitchat.jpg";
        //maybe add an array of images?
        String imgAddress = "http://2.bp.blogspot.com/-BvXcUdArvGk/UK54mxYSUOI/AAAAAAAAbg8/XycJSQH_IrU/s640/funny-animal-captions-005-020.jpg";
        //String imgAddress = "http://ajax.googleapis.com/ajax/services/search/images?q=%s&v=1.0&rsz=large&start=1";
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(getString(R.string.contacts));
            Integer jsonArrSize = posts.length();
            Log.e("SIZE OF RETURNED JSON ARRAY", jsonArrSize.toString());
            mContactFeedItemList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                ContactFeedItem item = new ContactFeedItem();
                item.setTitle(post.optString(getString(R.string.username)));
                item.setThumbnail(imgAddress);
                item.setFname(post.optString(getString(R.string.firstname)));
                item.setLname(post.optString(getString(R.string.lastname)));
                mContactFeedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //on post exec should be -> handle successful contacts query
    public void handleContactsQueryResponseOnPostExec(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Query was successful
                progressBar.setVisibility(View.GONE);
                mNewChatIncludedUsernamesList = new ArrayList<>();
                //need to populate the contacts list before passing it to the adapter
                parseHerokuResult(result);
                Integer numContacts = mContactFeedItemList.size();
                Log.e("NUMBER OF CONTACTS RETURNED: ", numContacts.toString());

                adapter = new MyRecyclerViewAdapter(getContext(), mContactFeedItemList);
                mRecyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(item -> {
                    handleChatMembersAddRemove(item);
                });

            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }


    private void handleChatMembersAddRemove(ContactFeedItem item) {

        StringBuilder sb = new StringBuilder();

        //toggle the contact's display card
        //if the friend has already been added to the list for a new chat:
        //on the second click, they get removed

        if (item.isSelected()) {

            mNewChatIncludedUsernamesList.remove(item.getTitle());
            item.setSelected(false);

        } else {

                mNewChatIncludedUsernamesList.add(item.getTitle());
                item.setSelected(true);
            }

            Integer users = mNewChatIncludedUsernamesList.size();
            Log.e("SIZE OF FRIENDS LIST FOR NEW CHAT:", users.toString());
            for (String s : mNewChatIncludedUsernamesList) {
                sb.append(s);
                sb.append(" ");
            }

            Integer sbsize = sb.length();
            Log.e("STRING BUILDER SIZE: ", sbsize.toString());
            mUsernamesDisplayTextView.setText(sb.toString());

        }


    public JSONObject createVerifiedContactsRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberID);
            Integer id = mUserMemberID;
            Log.e("MEMBER ID BEING PASSED TO BACK END: ", id.toString());
        } catch (JSONException e) {
            Log.wtf("CREATE NEW CHAT OBJECT", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    private Uri buildHerokuVerifiedContactsUri() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_verified))
                .build();
        return uri;
    }

    //-------------------------------------------------------------------------
    //-----------------------END LOAD CONTACTS---------------------------------
    //-------------------------------------------------------------------------
    //-----------------------BEGIN CREATE NEW CHAT-----------------------------
    //-------------------------------------------------------------------------

    private Uri buildHerokuNewChatUri() {
        Uri uri = new Uri.Builder().scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chat))
                .appendPath(getString(R.string.ep_new_chat))
                .build();
        return uri;
    }

    public JSONObject createNewChatRequestObject() {
        JSONObject msg = new JSONObject();

        if (mNewChatIncludedUsernamesList.size() < 1) { //nobody added to the list
            Toast.makeText(this.getContext()
                    , "PLEASE ADD AT LEAST ONE PERSON TO CHAT WITH"
                    ,Toast.LENGTH_LONG );
            return msg;
        } else { //make the chat name from names of all members, like we agreed on
            StringBuilder sb = new StringBuilder();
            for (String s : mNewChatIncludedUsernamesList) {
                sb.append(s);
                sb.append('+');
            }
            //SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String thisUsername = prefs.getString("username", "USERNAME NOT FOUND IN PREFS!");
            if (!mNewChatIncludedUsernamesList.contains(thisUsername)) {
                //should be added last so easy to remove
                mNewChatIncludedUsernamesList.add(thisUsername);
                sb.append(thisUsername);
            } else {
                Log.e("ADDING THIS USERNAME TO CHAT ERROR:", "USERNAME WAS ALREADY IN THE CHATNAME");
            }

            Log.e("NEW CHAT WILL BE NAMED: ", sb.toString());
            try {
                //msg.put("memberid", mUserMemberID);
                msg.put("chatname", sb.toString());
            } catch (JSONException e) {
                Log.wtf("CREATE NEW CHAT OBJECT", "Error creating JSON: " + e.getMessage());
            }

        }
        return msg;
    }

    private void sendNewChatRequest() {
        JSONObject requestObject = createNewChatRequestObject();
        new SendPostAsyncTask.Builder(mNewChatUri.toString(), requestObject)
                .onPostExecute(this::handleNewChatCreatedOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    //-------------------------------------------------------------------------
    //-----------------------END CREATE NEW CHAT REQUEST-----------------------
    //-------------------------------------------------------------------------
    //----------------BEGIN HANDLING THE RESPONSE AND INCLUDING MEMBERS--------
    //-------------------------------------------------------------------------

    //make response handler method init the int for the newly created chatid
    //notify? add all members in the chat to the chat, use the arraylist of chosen members

    //on post exec should be -> handle successful new chat creation
    public void handleNewChatCreatedOnPost(String result) {
        try {

            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {

                //chat creation was successful
                progressBar.setVisibility(View.GONE);

                //inform user a new chat was created
                Toast.makeText(this.getContext(), "NEW RABBIT CHAT CREATED!", Toast.LENGTH_SHORT).show();
                //mNewChatIDFromResponse = resultsJSON.getInt("chatid");
                //Integer chatid = mNewChatIDFromResponse;
                //Log.e("LOG ID IS: ", chatid.toString());
                mNewChatIDStr = resultsJSON.getString("message");
                //Log.e("LOG ID IS: ", chatid.toString());
                Log.e("LOG ID IS: ", mNewChatIDStr);
                //may need to pass params in to here later? not sure yet
                kickOffNewChat();

            } else {

                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();

            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * New Chat has been created from selected members.  This method should handle any logic
     * associated with any further actions, eg: going to a different fragment, sending out notifications,
     * writing to the internal db, etc.
     */
    public void kickOffNewChat() {
        Log.e("KICK OFF NEW CHAT: ", "TRUE");
        //loadNewChatFrag(new ChatFragment(), getString(R.string.keys_fragment_chat));
        //now need a way to make sure all members are added ot this chat
//>>>   stopped here
    }

    private void loadNewChatFrag(Fragment frag, String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    //-------------------------------------------------------------------------
    //-----------------------END CREATE NEW CHAT-------------------------------
    //-------------------------------------------------------------------------
    //-----------------------BEGIN UTILITY METHODS-----------------------------
    //-------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }


}
