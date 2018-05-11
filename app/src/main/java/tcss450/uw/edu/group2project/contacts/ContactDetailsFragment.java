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
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

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
    private String mUserMemberId;

    public ContactDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View retView;
        if (savedInstanceState != null) {
            mContact = (ChatContact) savedInstanceState.getSerializable("member");
            Log.e("ChatContact INFO :", mContact.toString());
            mUserMemberId = savedInstanceState.getString("memberid");
        }
        //adapter =new MyRecyclerViewAdapter(this, feedsList);
        adapter =new MyMsgRecyclerViewAdapter(this.getContext(), mMessageList);
        //mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        retView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        mContactMessagesUri = buildHerokuAddress(); //TODO start using this uri instead
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

        new SendPostAsyncTask.Builder(mContactMessagesUri.toString(), jsonObject)
                .onPostExecute(this::handleContactMessagesResponse)
                .onCancelled(this::handleErrorsInTask)
                .build()
                .execute();
    }

    public JSONObject createContactMessagesRequestObject() {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("memberid_b", mUserMemberId);
        } catch (JSONException e) {
            Log.e("CONTACT MESSAGES REQUEST OBJECT :",
                    "Error in creating object " + e.getMessage());
        }
        return requestObject;
    }

    private void handleContactMessagesResponse(String result) {
            //String imgAddress = "https://www.logoground.com/uploads/2017108832017-04-203705844rabbitchat.jpg";
            //maybe add an array of images?
            //String imgAddress = "http://2.bp.blogspot.com/-BvXcUdArvGk/UK54mxYSUOI/AAAAAAAAbg8/XycJSQH_IrU/s640/funny-animal-captions-005-020.jpg";
            String imgAddress = "http://ajax.googleapis.com/ajax/services/search/images?q=%s&v=1.0&rsz=large&start=1";
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("messages");
                mMessageList = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    MessageFeedItem item = new MessageFeedItem();
                    item.setTitle(post.optString("msgContent"));
                    item.setThumbnail(imgAddress);
                    mMessageList.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("MESSAGES RESPONSE BODY PARSED, LIST [0]: ", mMessageList.get(0).toString());
        }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }



}
