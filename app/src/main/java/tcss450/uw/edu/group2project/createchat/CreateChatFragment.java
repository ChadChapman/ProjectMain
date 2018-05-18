package tcss450.uw.edu.group2project.createchat;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.FriendProfileFragment;
import tcss450.uw.edu.group2project.model.ChatContact;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.model.FeedItem;
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
    private String mUserMemberIDStr;
    private Uri mNewChatUri;
    private View v;
    private List<String> mNewChatIncludedUsernamesList;

    public CreateChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_create_chat, container, false);
       mRecyclerView = v.findViewById(R.id.recycler_view);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       mNewChatUri = buildHerokuNewChatUri();
       progressBar = v.findViewById(R.id.progress_bar);
       loadVerifiedContacts();

       return v;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    public void loadVerifiedContacts() {
        JSONObject jsonObject = createVerifiedContactsRequestObject();
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mNewChatUri.toString(), jsonObject)
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
        //return mContactFeedItemList;
    }

    //on post exec should be -> handle successful contacts query
    public void handleContactsQueryResponseOnPostExec(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            TextView usernamesTextView = v.findViewById(R.id.createChatUsernamesDisplay);
            if (success) {
                //Query was successful
                progressBar.setVisibility(View.GONE);
                mNewChatIncludedUsernamesList = new ArrayList<>();
                //need to populate the contacts list before passing it to the adapter
                parseHerokuResult(result);
                //added from here
                adapter = new MyRecyclerViewAdapter(getContext(), mContactFeedItemList);
                mRecyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onContactItemClick(ContactFeedItem item) {
                        //toggle the contact's display card
                        //if the friend has already been added to the list for a new chat:
                        //on the second click, they get removed
                        if (item.isSelected()) {
                            if(mNewChatIncludedUsernamesList.contains(item.getUsername())) {
                                mNewChatIncludedUsernamesList.remove(item.getUsername());
                                //Text views don't have a way to just redraw themselves?
                                //guess I will have to iterate through this entire list?
                                //usernamesTextView.setText("");
                                StringBuilder sb = new StringBuilder();
                                for (String s : mNewChatIncludedUsernamesList) {
                                    sb.append(s);
                                    sb.append("\n");
                                }
                                usernamesTextView.setText(sb.toString());

                            }
                            // do the color changes later?
                        //some number of clicks where: (n % 2 = 1), so they get added to the list
                        //of friends to have in a new chat
                        } else {
                            mNewChatIncludedUsernamesList.add(item.getUsername());
                            usernamesTextView.append(item.getUsername());
                        }
                        //Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_LONG).show();
//                        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.fragmentContainer, new FriendProfileFragment(item), "friend")
//                                .addToBackStack(null);
                        // Commit the transaction
                        //transaction.commit();
                    }
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

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    public JSONObject createVerifiedContactsRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberID);
        } catch (JSONException e) {
            Log.wtf("CREATE NEW CHAT OBJECT", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    public JSONObject createNewChatRequestObject() {
        JSONObject msg = new JSONObject();
        JSONArray usersArr = new JSONArray(mNewChatIncludedUsernamesList);
        try {
            msg.put("memberid", mUserMemberID);
            msg.put("members_in_chat", usersArr);
        } catch (JSONException e) {
            Log.wtf("CREATE NEW CHAT OBJECT", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    private Uri buildHerokuNewChatUri(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chat))
                .appendPath(getString(R.string.ep_create_new))
                .build();
        return uri;
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
