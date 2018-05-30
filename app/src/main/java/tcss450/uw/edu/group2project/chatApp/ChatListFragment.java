package tcss450.uw.edu.group2project.chatApp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
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
//import tcss450.uw.edu.group2project.model.Feeders.MessageFeedItem;
import tcss450.uw.edu.group2project.model.MessageFeedItem;
//import tcss450.uw.edu.group2project.model.Feeders.MessageFeedItem;
import tcss450.uw.edu.group2project.createchat.CreateChatFragment;
import tcss450.uw.edu.group2project.model.MessageFeedItem;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;
import tcss450.uw.edu.group2project.utils.UITheme;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressBar progressBar;
    private List<MessageFeedItem> messageFeedItemList;

    private String mUserMemberIDStr;
    private int mUserMemberID;
    private Uri mContactsUri;
    private View v;
    private ImageButton mCreateNewChatBtn;

    public ChatListFragment() {
        // Required empty public constructor
    }
    //probably need to get rid of this
    @SuppressLint("ValidFragment")
    public ChatListFragment(String mID) {
        // Required empty public constructor
        mUserMemberID = new Integer(mID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        mRecyclerView = (RecyclerView) (v.findViewById(R.id.message_recycler_view));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mCreateNewChatBtn = v.findViewById(R.id.createNewChatButtonChatList);
        setupFragmentView();
        RecyclerView rView = v.findViewById(R.id.message_recycler_view);

        TextView currentChats = v.findViewById(R.id.current_chats_textview);


        loadMessages();





        if(ChatActivity.mTheme == 1){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            currentChats.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            currentChats.setTextSize(36);
            currentChats.setText("Current Chats");
        }else if(ChatActivity.mTheme == 2){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            currentChats.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            currentChats.setTextColor(getResources().getColor(R.color.colorPrimaryDark2));
            currentChats.setTextSize(36);
            currentChats.setText("Current Chats");

        }else if(ChatActivity.mTheme == 3){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));
            currentChats.setBackgroundColor(getResources().getColor(R.color.colorAccent3));
            currentChats.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
            currentChats.setTextSize(36);
            currentChats.setText("Current Chats");
        }else if(ChatActivity.mTheme == 4){
            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary4));
            currentChats.setBackgroundColor(getResources().getColor(R.color.colorAccent4));
            currentChats.setTextColor(getResources().getColor(R.color.colorPrimaryDark4));
            currentChats.setTextSize(36);
            currentChats.setText("Current Chats");

        }



        return v;
    }

    private void setupFragmentView() {
        mCreateNewChatBtn.setOnClickListener(frag -> loadFragment(new CreateChatFragment()
                , getString(R.string.keys_fragment_create_new_chat)));

        progressBar = (ProgressBar) (v.findViewById(R.id.message_progressBar));
        mContactsUri = buildHerokuAddress(getString(R.string.ep_get_recent_chat));
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //mThisUsername = prefs.getString("username", "USERNAME NOT FOUND IN PREFS!");
        mUserMemberIDStr = prefs.getString("mymemberid", "MEMBERID NOT FOUND IN PREFS");


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
            msg.put("memberid", mUserMemberIDStr);
        } catch (JSONException e) {
            Log.wtf("CONTACTS VERIFIED ALL", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    /**
     * Heroku parser
     */
    private void parseHerokuResult(String result) {
        try {
            Log.e(",c", result);
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(getString(R.string.contacts));
            messageFeedItemList = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                MessageFeedItem item = new MessageFeedItem();
                item.setChatName(post.optString("name"));
                item.setChatid(post.optString(getString(R.string.chatid)));
                item.setMessage(post.optString(getString(R.string.message)));
                messageFeedItemList.add(item);
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
                adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.chats_list_rows, null);
                        return new CustomViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, int i) {
                        //FeedItem feedItem = feedItemList.get(i);
                        MessageFeedItem feedItem = messageFeedItemList.get(i);


                        //Setting text view title
                        ((CustomViewHolder) customViewHolder).chatName.setText(feedItem.getChatName());
                        ((CustomViewHolder) customViewHolder).message.setText(feedItem.getMessage());

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onMsgItemClick(feedItem);
                            }
                        };
                        ((CustomViewHolder) customViewHolder).chatName.setOnClickListener(listener);
                        ((CustomViewHolder) customViewHolder).message.setOnClickListener(listener);
                    }
                    @Override
                    public int getItemCount() {
                        return (null != messageFeedItemList ? messageFeedItemList.size() : 0);
                    }
                };
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }//to here from tut GH
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

    private void onMsgItemClick(MessageFeedItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("chatID", item.getChatid());
        bundle.putString("chatName",item.getChatName() );
        Fragment chats = new ChatFragment();
        chats.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, chats, getString(R.string.keys_fragment_chat))
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView chatName;
        protected TextView message;
        protected CardView cView;

        public CustomViewHolder(View view) {
            super(view);
            this.chatName = (TextView) view.findViewById(R.id.chat_username_textview);
            this.message = (TextView) view.findViewById(R.id.chat_message_textview);
            this.cView = (CardView) view.findViewById(R.id.list_card_view);
            //this.cView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void loadFragment(Fragment frag, String tag) {
       // Log.e("MADE IT TO lOADFRAGMENT() IN LANDING FRAG", tag);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}
