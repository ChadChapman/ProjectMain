package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
//import tcss450.uw.edu.group2project.model.Feeders.ChatFeedItem;
//import tcss450.uw.edu.group2project.model.Feeders.MessageFeedItem;
import tcss450.uw.edu.group2project.model.ChatFeedItem;
import tcss450.uw.edu.group2project.utils.GetPostAsyncTask;
import tcss450.uw.edu.group2project.utils.ListenManager;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private String mUserChatIDStr;
    private String mUsername;
    private String mSendUrl;
    private ListenManager mListenManager;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<ChatFeedItem> messageFeedItemList;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        v.findViewById(R.id.trychatSendButton).setOnClickListener(this::sendMessage);
        messageFeedItemList = new ArrayList<>();

        messageFeedItemList = new ArrayList<>();
        if (getArguments() != null) {
            mUserChatIDStr = getArguments().getString("chatID");
        }
        mRecyclerView = (RecyclerView) (v.findViewById(R.id.trychat_recyclerview));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);





        return v;
    }

    private void setupChatFragment() {


    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");


        mSendUrl = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_send_message))
                .build()
                .toString();


        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", mUserChatIDStr)
                .build();


        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();

        }
        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.messaging_list_rows, null);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, int i) {
                //FeedItem feedItem = feedItemList.get(i);
                ChatFeedItem feedItem = messageFeedItemList.get(i);

                //Setting text view title
                ((CustomViewHolder) customViewHolder).chatid.setText(feedItem.getUsername());
                ((CustomViewHolder) customViewHolder).message.setText(feedItem.getMessage());

//                        View.OnClickListener listener = new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                onMsgItemClick(feedItem);
//                            }
//                        };
                //((CustomViewHolder) customViewHolder).chatid.setOnClickListener(listener);
                //((CustomViewHolder) customViewHolder).message.setOnClickListener(listener);
            }

            @Override
            public int getItemCount() {
                return (null != messageFeedItemList ? messageFeedItemList.size() : 0);
            }
        };
        mRecyclerView.setAdapter(adapter);
        getMessages();
    }

    private void handleError(Exception e) {
        Log.e("LISTEN ERROR", e.getMessage());
    }


    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = mListenManager.stopListening();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //save the most recent message timestamp
        prefs.edit().putString(getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }

    private void sendMessage(final View theButton) {


        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) getView().findViewById(R.id.trychatInputEditText))
                .getText().toString();


        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), mUserChatIDStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }


    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }


    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ((EditText) getView().findViewById(R.id.trychatInputEditText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void publishProgress(JSONObject messages) {
        if (messages.has(getString(R.string.keys_json_messages))) {
            List<ChatFeedItem> temp = new ArrayList<>();
            try {

                JSONArray jMessages =
                        messages.getJSONArray((getString(R.string.keys_json_messages)));
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    ChatFeedItem item = new ChatFeedItem();
                    item.setUsername(msg.optString(getString(R.string.username)));
                    item.setMessage(msg.optString(getString(R.string.message)));
                    temp.add(item);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            getActivity().runOnUiThread(() -> {
                for (ChatFeedItem msg : temp) {
//                    mOutputTextView.append(msg);
//                    mOutputTextView.append(System.lineSeparator());
                    messageFeedItemList.add(msg);
                    adapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
                }
            });

        }
    }

    //retrieve the previous messages
    private void getMessages() {
        String myMsg = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", mUserChatIDStr)
                .appendQueryParameter("after", "2010-05-15 21:43:05.407269+00")
                .build()
                .toString();

        JSONObject messageJson = new JSONObject();
        new GetPostAsyncTask.Builder(myMsg, messageJson)
                .onPostExecute(this::getReport)
                .onCancelled(this::handleError)
                .build().execute();


    }

    private void getReport(String messages) {

        try {
            publishProgress(new JSONObject(messages));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView chatid;
        protected TextView message;

        public CustomViewHolder(View view) {
            super(view);
            this.chatid = (TextView) view.findViewById(R.id.username);
            this.message = (TextView) view.findViewById(R.id.message);
        }
    }
}