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
            RecyclerView.Adapter<tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter.CustomViewHolder> {

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
        public tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter.CustomViewHolder
            onCreateViewHolder(ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_row, null);

                tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter.CustomViewHolder viewHolder
                        = new tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter.CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(
                tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter.CustomViewHolder
                        customViewHolder, int i) {

            //FeedItem feedItem = feedItemList.get(i);
            MessageFeedItem feedItem = messageFeedItemList.get(i);

            //Render image using Picasso library
            if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
                Picasso.with(mContext).load(feedItem.getThumbnail())
                        .error(R.drawable.contacts_image_error)
                        .placeholder(R.drawable.contacts_image_placeholder)
                        .into(customViewHolder.imageView);
            }

            //Setting text view title
            customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMsgClickListener.onMsgItemClick(feedItem);
                }
            };
            customViewHolder.imageView.setOnClickListener(listener);
            customViewHolder.textView.setOnClickListener(listener);
        }

        public OnMsgClickListener getOnItemClickListener() {
            return onMsgClickListener;
        }

        public void setOnMsgClickListener(OnItemClickListener onItemClickListener) {
            this.onMsgClickListener = onMsgClickListener;
        }

        @Override
        public int getItemCount() {
            return (null != messageFeedItemList ? messageFeedItemList.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imageView;
            protected TextView textView;

            public CustomViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
                this.textView = (TextView) view.findViewById(R.id.title);
            }
        }

    }
//}
