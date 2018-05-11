package tcss450.uw.edu.group2project.contacts;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.model.FeedItem;
import tcss450.uw.edu.group2project.model.MessageFeedItem;
import tcss450.uw.edu.group2project.utils.MyMsgRecyclerViewAdapter;
import tcss450.uw.edu.group2project.utils.MyRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * This class will display all the details of a contact
 */
public class ContactDetailsFragment extends Fragment {

    private static final String TAG = "RecyclerViewExample";
    private List<FeedItem> feedsList;
    private RecyclerView mRecyclerView;
    private MyMsgRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private List<ChatContact> mContactsList;
    private List<MessageFeedItem> mMessageList;
    private List<ContactFeedItem> mContactFeedItemList;
    private Uri mContactMessagesUri;
    ChatContact mContact;

    public ContactDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View retView;
        mContact = (ChatContact) savedInstanceState.getSerializable("member");
        Log.e("ChatContact INFO :", mContact.toString());
        //adapter =new MyRecyclerViewAdapter(this, feedsList);
        adapter =new MyMsgRecyclerViewAdapter(this.getContext(), mMessageList);
        //mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        retView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        mContactMessagesUri = buildHerokuAddress(); //TODO start using this uri instead
        //mContactsUri = buildHerokuAddress(); //TODO start using this uri instead
        //mContactFeedItemList = new ArrayList<>(); done in parseHerokuResult
        //String url = "http://stacktips.com/?json=get_category_posts&slug=news&count=30";
        //new DownloadTask().execute(url);
        //new DownloadTask().execute(mContactsUri.toString()); //commented out trying to get away from tut
        loadContactMessages();
        //return inflater.inflate(R.layout.fragment_contact_details, container, false);
        return retView;
    }

    public Uri buildHerokuAddress() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_get_message))
                .build();

        return uri;
    }

    public void loadContactMessages() {
        JSONObject jsonObject = createContactMessagesRequestObject();
    }

    public JSONObject createContactMessagesRequestObject() {

    }



}
