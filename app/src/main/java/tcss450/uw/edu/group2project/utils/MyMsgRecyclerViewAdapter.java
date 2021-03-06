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

import tcss450.uw.edu.group2project.model.MessageFeedItem;


public class MyMsgRecyclerViewAdapter extends
        RecyclerView.Adapter<MyMsgRecyclerViewAdapter.CustomViewHolder> {
    private List<MessageFeedItem> messageFeedItemList;
    private Context mContext;
    private OnMsgClickListener onMsgClickListener;

    //this constructor is for contacts
    public MyMsgRecyclerViewAdapter(Context context, List<MessageFeedItem> feedItemList) {
        this.messageFeedItemList = feedItemList;
        this.mContext = context;
        Log.e("MESSAGES ADAPTER CREATED FROM: ", "SUCCESS");
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messaging_list_rows, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(
            CustomViewHolder customViewHolder, int i) {

        //FeedItem feedItem = feedItemList.get(i);
        MessageFeedItem feedItem = messageFeedItemList.get(i);

        //Setting text view title
        customViewHolder.chatid.setText(Html.fromHtml(feedItem.getChatid()));
        customViewHolder.message.setText(Html.fromHtml(feedItem.getMessage()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMsgClickListener.onMsgItemClick(feedItem);
            }
        };
        customViewHolder.chatid.setOnClickListener(listener);
        customViewHolder.message.setOnClickListener(listener);
    }

    public OnMsgClickListener getOnItemClickListener() {
        return onMsgClickListener;
    }

    public void setOnMsgClickListener(OnMsgClickListener onItemClickListener) {
        this.onMsgClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return (null != messageFeedItemList ? messageFeedItemList.size() : 0);
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
//}
