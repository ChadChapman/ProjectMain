package tcss450.uw.edu.group2project.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tcss450.uw.edu.group2project.R;

import tcss450.uw.edu.group2project.model.ChatFeedItem;


public class MyChatRecyclerViewAdapter extends
        RecyclerView.Adapter<MyChatRecyclerViewAdapter.CustomViewHolder> {
    private List<ChatFeedItem> messageFeedItemList;
    private Context mContext;
    private OnChatClickListener onChatClickListener;

    //this constructor is for contacts
    public MyChatRecyclerViewAdapter(Context context, List<ChatFeedItem> feedItemList) {
        this.messageFeedItemList = feedItemList;
        this.mContext = context;
        Log.e("MESSAGES ADAPTER CREATED FROM: ", "SUCCESS");
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chats_list_rows, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(
            CustomViewHolder customViewHolder, int i) {

        //FeedItem feedItem = feedItemList.get(i);
        ChatFeedItem feedItem = messageFeedItemList.get(i);

        //Setting text view title
        customViewHolder.username.setText(Html.fromHtml(feedItem.getUsername()));
        customViewHolder.message.setText(Html.fromHtml(feedItem.getMessage()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChatClickListener.OnChatClickListener(feedItem);
            }
        };
        customViewHolder.username.setOnClickListener(listener);
        customViewHolder.message.setOnClickListener(listener);
    }

    public OnChatClickListener getOnItemClickListener() {
        return onChatClickListener;
    }

    public void setOnChatClickListener(OnChatClickListener onItemClickListener) {
        this.onChatClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return (null != messageFeedItemList ? messageFeedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView username;
        protected TextView message;

        public CustomViewHolder(View view) {
            super(view);
            this.username = (TextView) view.findViewById(R.id.chat_username_textview);
            this.message = (TextView) view.findViewById(R.id.chat_message_textview);
        }
    }

}
//}
