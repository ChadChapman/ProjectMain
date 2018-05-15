package tcss450.uw.edu.group2project.chatApp;


import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.model.FeedItem;
import tcss450.uw.edu.group2project.model.MessageFeedItem;
import tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter;
import tcss450.uw.edu.group2project.utils.MyRecyclerViewAdapter;
import tcss450.uw.edu.group2project.utils.OnItemClickListener;
import tcss450.uw.edu.group2project.utils.OnMsgClickListener;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private List<FeedItem> feedsList;
    private RecyclerView mRecyclerView;
    private MyMsgRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private List<ChatContact> mContactsList;
    private List<MessageFeedItem> mContactFeedItemList;
    private int mUserMemberID;
    private String mUserMemberIDStr;
    private Uri mContactsUri;
    private View v;
    private Button mNewChatButton;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ChatListFragment(String mID) {
        // Required empty public constructor
        mUserMemberID = new Integer(mID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        mUserMemberIDStr = Integer.toString(mUserMemberID);
        mRecyclerView = (RecyclerView) (v.findViewById(R.id.message_recycler_view));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressBar = (ProgressBar) (v.findViewById(R.id.message_progressBar));
        mContactsUri = buildHerokuAddress(getString(R.string.ep_get_recent_chat));
        loadMessages();

        mNewChatButton = v.findViewById(R.id.buttonStartNewChat);

        mNewChatButton.setOnClickListener(button -> {
            startNewChat(mNewChatButton);
        });
        return v;
    }

    public void startNewChat(Button paramButton) {
        //start a new chat with at least one other person
        //get the other memberID
        //hit endpoint to create a new chat
        //on success load a fragment for a new chat
            //on fail, return to this frag and give a long toast that signals failure
        

    }

    /**
     * For building a url address.
     */
    public Uri buildHerokuAddress(String ep) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chat))
                .appendPath(ep)
                .build();
        return uri;
    }

    /**
     * For loading all messages
     */
    public void loadMessages() {
        JSONObject jsonObject = createMessagesRequestObject();
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mContactsUri.toString(), jsonObject)
                .onPostExecute(this::handleMessageQueryResponseOnPostExec)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * For message JSonObj. (need args)
     */
    public JSONObject createMessagesRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            //Todo need args for json
            msg.put("memberid", mUserMemberID);
        } catch (JSONException e) {
            Log.wtf("CONTACTS VERIFIED ALL", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    /**
     * Heroku parser
     */
    private void parseHerokuResult(String result) {
        //String imgAddress = "https://www.logoground.com/uploads/2017108832017-04-203705844rabbitchat.jpg";
        //maybe add an array of images?
        //String imgAddress = "http://2.bp.blogspot.com/-BvXcUdArvGk/UK54mxYSUOI/AAAAAAAAbg8/XycJSQH_IrU/s640/funny-animal-captions-005-020.jpg";
        //String imgAddress = "http://ajax.googleapis.com/ajax/services/search/images?q=%s&v=1.0&rsz=large&start=1";
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(getString(R.string.contacts));
            mContactFeedItemList = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                MessageFeedItem item = new MessageFeedItem();
                item.setChatid(post.optString(getString(R.string.chatid)));
                item.setMessage(post.optString(getString(R.string.message)));
                mContactFeedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return mContactFeedItemList;
    }

    //-----------------------------------------------------------------------------------------
    //--------------------------------------Handlers-------------------------------------------
    //-----------------------------------------------------------------------------------------
    public void handleMessageQueryResponseOnPostExec(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Query was successful
                progressBar.setVisibility(View.GONE);

                //need to populate the contacts list before passing it to the adapter
                parseHerokuResult(result);
                //added from here
                adapter = new MyMsgRecyclerViewAdapter(getContext(), mContactFeedItemList);
                mRecyclerView.setAdapter(adapter);
                adapter.setOnMsgClickListener(new OnMsgClickListener() {
                    @Override
                    public void onMsgItemClick(MessageFeedItem item) {
                        //Toast.makeText(getContext(), item.getChatid(), Toast.LENGTH_LONG).show();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainer, new ChatFragment(new Integer(item.getChatid())), getString(R.string.keys_fragment_chat))
                                .addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                });

            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }//to here from tut GH

//                adapter = new MyRecyclerViewAdapter(
//                            ContactsActivity.this, mContactFeedItemList);
//                    mRecyclerView.setAdapter(adapter);
//          } else {
//                    Toast.makeText(ContactsActivity.this
//                            , "Failed to fetch data!", Toast.LENGTH_SHORT).show();
//            } //commented out for testing
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

}
