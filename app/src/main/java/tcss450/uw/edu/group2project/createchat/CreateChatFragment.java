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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.ChatActivity;
import tcss450.uw.edu.group2project.chatApp.ChatFragment;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.utils.MyRecyclerViewAdapter;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;


public class CreateChatFragment extends Fragment {
    private static final String TAG = "create_chat_fragment";

    private String mThisUsername;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private List<ContactFeedItem> mContactFeedItemList;
    private String mUserMemberID;
    private String mNewChatIDStr;
    private String mNewChatNameStr;
    private Uri mNewChatUri;
    private Uri mContactsUri;
    private View v;
    private List<String> mNewChatIncludedUsernamesList;
    private ImageButton createButton;
    private TextView mUsernamesDisplayTextView;

    public CreateChatFragment() {
        // Required empty public constructor
    }

    /**
     * Setup the RecyclerView, basically.
     *
     * @param inflater Android standard
     * @param container Android standard
     * @param savedInstanceState Android standard
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate and setup the layout for this fragment
       v = inflater.inflate(R.layout.fragment_create_chat, container, false);
       mRecyclerView = v.findViewById(R.id.create_chat_recycle_view);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       setupCreateNewChatFrag(savedInstanceState);

        if(ChatActivity.mTheme == 1){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        }else if(ChatActivity.mTheme == 2){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));


        }else if(ChatActivity.mTheme == 3){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));

        }else if(ChatActivity.mTheme == 4){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary4));


        }


       return v;
    }

    private void setupCreateNewChatFrag(Bundle setupSavedInstanceState) {

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mThisUsername = prefs.getString("username", "USERNAME NOT FOUND IN PREFS!");
        mUserMemberID = prefs.getString("mymemberid", "MEMBERID NOT FOUND IN PREFS");
        mNewChatUri = buildHerokuNewChatUri();
        mContactsUri = buildHerokuVerifiedContactsUri();
        progressBar = v.findViewById(R.id.create_chat_progress_bar);
        mUsernamesDisplayTextView = v.findViewById(R.id.createChatUsernamesDisplay);

        loadVerifiedContacts();
        createButton = v.findViewById(R.id.createNewChatFragNewChatButton);
        createButton.setOnClickListener(view -> {
            sendNewChatRequest();
        });
    }

    public void loadVerifiedContacts() {
        JSONObject jsonObject = createVerifiedContactsRequestObject();
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mContactsUri.toString(), jsonObject)
                .onPostExecute(this::handleContactsQueryResponseOnPostExec)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void parseHerokuContactsResult(String result) {

        String imgAddress = "http://2.bp.blogspot.com/-BvXcUdArvGk/UK54mxYSUOI/AAAAAAAAbg8/XycJSQH_IrU/s640/funny-animal-captions-005-020.jpg";

        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(getString(R.string.contacts));

            Integer jsonArrSize = posts.length();
            Log.e("SIZE OF RETURNED JSON ARRAY", jsonArrSize.toString());

            mContactFeedItemList = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                if (!mThisUsername.equals(post.optString("username"))) {
                    ContactFeedItem item = new ContactFeedItem();
                    item.setTitle(post.optString(getString(R.string.username)));
                    //item.setThumbnail(imgAddress);
                    item.setFname(post.optString(getString(R.string.firstname)));
                    item.setLname(post.optString(getString(R.string.lastname)));
                    mContactFeedItemList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void handleContactsQueryResponseOnPostExec(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Query was successful
                progressBar.setVisibility(View.GONE);
                mNewChatIncludedUsernamesList = new ArrayList<>();

                //need to populate the contacts list before passing it to the adapter
                parseHerokuContactsResult(result);
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
            //Log.e("STRING BUILDER SIZE: ", sbsize.toString());
            mUsernamesDisplayTextView.setText(sb.toString());

        }


    public JSONObject createVerifiedContactsRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberID);
            Log.e("MEMBER ID BEING PASSED TO BACK END: ", mUserMemberID);
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

    private Uri buildLocalVerifiedContactsUri() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                //.appendPath(getString(R.string.ep_base_localhost_5000))
                .encodedAuthority(getString(R.string.ep_base_localhost_5000))
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

    private Uri buildLocalNewChatUri() {
        Uri uri = new Uri.Builder().scheme("https")
                //.appendPath(getString(R.string.ep_base_localhost_5000))
                .encodedAuthority(getString(R.string.ep_base_localhost_5000))
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

            if (!mNewChatIncludedUsernamesList.contains(mThisUsername)) {//could be possible?
                //should be added last so easy to remove?
                mNewChatIncludedUsernamesList.add(mThisUsername);
                sb.append(mThisUsername);
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
//>>> idea for duplicate chats: a list of hashsets which contain members' usernames,
        //iterate through to check for a set that contains all usernames
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
                //log results
                mNewChatIDStr = resultsJSON.getString("chatid");
                mNewChatNameStr = resultsJSON.getString("chatname");
                Log.e("CHATID IS: ", mNewChatIDStr);

                //may need to pass params in to here later? not sure yet
                kickOffNewChat();
            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * New Chat has been created from selected members with usernames as an id.
     * This method should handle any logic associated with any further actions,
     * eg: adding all members to the chat, going to a different fragment,
     * sending out notifications, writing to the internal db, etc.
     */
    private void kickOffNewChat() {
        Log.e("KICK OFF NEW CHAT: ", "TRUE");
        //now to get all members added to this chat
        JSONObject requestObject = createNewChatAddMembersRequestObject();
        Uri addNewChatMembersUri = buildHerokuAddNewChatMembersUri();
        sendNewChatAddAllMembersRequest(requestObject, addNewChatMembersUri);
    }
    private JSONObject createNewChatAddMembersRequestObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("chatname", mNewChatNameStr);
            jsonObject.put("chatid", mNewChatIDStr);
        } catch (JSONException e) {
            Log.e("JSON ERROR ADDING FIELDS TO: ", "ADD NEW CHAT MEMBERS OBJECT");
        }
        Log.e("CHATNAME FOR NEW CHAT : ", mNewChatNameStr);
        Log.e("CHATID FOR NEW CHAT : ", mNewChatIDStr);
        Log.e("JSON REQUEST OBJECT : ", jsonObject.toString());

        return jsonObject;
    }

    private void sendNewChatAddAllMembersRequest(JSONObject jsonObject, Uri paramUri) {
        //send a json array to be parsed and processed on backend, esp since we need to have memberids
        //in order to add members to chatmembers table
        new SendPostAsyncTask.Builder(paramUri.toString(), jsonObject)
                .onPostExecute(this::handleNewChatAllMembersAddedOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleNewChatAllMembersAddedOnPost(String result) {
        try {

            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) { //yay, all members were added, let's go to the new chat
                //TODO now add all members of this chat to the list of sets of usernames fo chats
                Log.e("ADD ALL MEMBERS RETURNED :", "SUCCESS!");
                loadNewChatInfoIntoPrefs(mNewChatIDStr);
                ChatFragment newChatFrag = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("chatID", mNewChatIDStr);
                newChatFrag.setArguments(bundle);
                loadNewChatFrag(newChatFrag, getString(R.string.keys_fragment_chat));

            } else {
                //need to determine what ot return if not successful
                //maybe an array of the members who did not get added, then try them again later?
                Log.e("ADD ALL MEMBERS RETURNED :", "FAILED!");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private Uri buildHerokuAddNewChatMembersUri(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chat))
                .appendPath(getString(R.string.ep_new_chat_add_all_members))
                .build();
        return uri;
    }

    private Uri buildLocalAddNewChatMembersUri() {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .encodedAuthority(getString(R.string.ep_base_localhost_5000))
                .appendPath(getString(R.string.ep_chat))
                .appendPath(getString(R.string.ep_new_chat_add_all_members))
                .build();
        return uri;
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

    private void loadNewChatInfoIntoPrefs(String chatID) {
        //prefs has a string set type it will take, about the best we can do for storing chat info
        //for now i think.
        //both chatid and chatname will have their own set so we can check chatids easier

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //first add the chatname and chatid for passing around to fragments
        prefs.edit()
                .putString("newchatid", mNewChatIDStr);
        prefs.edit()
                .putString("newchatname", mNewChatNameStr);

        //see if this is first chat or not
        if (prefs.getStringSet("chatidset", null) == null) {
            //make a string set, add this chatid, write it to prefs
            Set<String> chatidset = new HashSet<String>();
            chatidset.add(mNewChatIDStr);
            prefs.edit()
                    .putStringSet("chatidset", chatidset)
                    .apply();
        } else {
            Set<String> chatidset = prefs.getStringSet("chatidset", null);
            chatidset.add(mNewChatIDStr);
            prefs.edit()
                    .putStringSet("chatidset", chatidset)
                    .apply();
        }
        //now for the chatname
        if (prefs.getStringSet("chatnameset", null) == null) {
            //make a string set, add this chatid, write it to prefs
            Set<String> chatnameset = new HashSet<String>();
            chatnameset.add(mNewChatNameStr);
            prefs.edit()
                    .putStringSet("chatnameset", chatnameset)
                    .apply();

        } else {
            Set<String> chatnameset = prefs.getStringSet("chatnameset", null);
            chatnameset.add(mNewChatNameStr);
            prefs.edit()
                    .putStringSet("chatnameset", chatnameset)
                    .apply();
        }
    }

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
