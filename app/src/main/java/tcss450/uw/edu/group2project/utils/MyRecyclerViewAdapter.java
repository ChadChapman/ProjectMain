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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.model.FeedItem;
import tcss450.uw.edu.group2project.model.MessageFeedItem;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder> {
    private List<ContactFeedItem> feedItemList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    //this constructor is for contacts
    public MyRecyclerViewAdapter(Context context, List<ContactFeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        Log.e("CONTACTS ADAPTER CREATED FROM: ", "SUCCESS");
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        //FeedItem feedItem = feedItemList.get(i);
        ContactFeedItem feedItem = feedItemList.get(i);

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
                //onItemClickListener.onContactItemClick(feedItem);
                onItemClickListener.onContactItemClick(feedItem);


            }
        };

        customViewHolder.imageView.setOnClickListener(listener);
        customViewHolder.textView.setOnClickListener(listener);
    }



    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    /**
     * begin internal class for the viewholder
     */
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;
        //protected RelativeLayout rowRelativeLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.thumbnail);
            this.textView = view.findViewById(R.id.title);
            //this.rowRelativeLayout = view.findViewById(R.id.listRowRelativeLayout);
        }
    }//end internal class CVH

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}//end class MRVA