package tcss450.uw.edu.group2project.chatApp;


import android.annotation.SuppressLint;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.contacts.ContactsActivity;
import tcss450.uw.edu.group2project.model.ChatContact;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.model.FeedItem;
import tcss450.uw.edu.group2project.utils.MyRecyclerViewAdapter;
import tcss450.uw.edu.group2project.utils.OnItemClickListener;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class TryContactFragment extends Fragment {
    private static final String TAG = "RecyclerViewExample";
    private List<FeedItem> feedsList;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private List<ChatContact> mContactsList;
    private List<ContactFeedItem> mContactFeedItemList;
    private int mUserMemberID;
    private String mUserMemberIDStr;
    private Uri mContactsUri;
    private View v;

    public TryContactFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public TryContactFragment(String mID) {
        // Required empty public constructor
        mUserMemberID = new Integer(mID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_try_contact, container, false);
        // Inflate the layout for this fragment

        mUserMemberIDStr = Integer.toString(mUserMemberID);
        mRecyclerView = (RecyclerView) (v.findViewById(R.id.try_recycle_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = (ProgressBar) (v.findViewById(R.id.try_progress_bar));
        mContactsUri = buildHerokuAddress(); //TODO start using this uri instead
        //mContactsUri = buildHerokuAddress(); //TODO start using this uri instead
        //mContactFeedItemList = new ArrayList<>(); done in parseHerokuResult
        //String url = "http://stacktips.com/?json=get_category_posts&slug=news&count=30";
        //new DownloadTask().execute(url);
        //new DownloadTask().execute(mContactsUri.toString()); //commented out trying to get away from tut
        loadVerifiedContacts();
        return v;
    }



    public void loadVerifiedContacts(){
        JSONObject jsonObject = createVerifiedContactsRequestObject();
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mContactsUri.toString(), jsonObject)
                .onPostExecute(this::handleContactsQueryResponseOnPostExec)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    public JSONObject createVerifiedContactsRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberID);

        } catch (JSONException e) {
            Log.wtf("CONTACTS VERIFIED ALL", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    public Uri buildHerokuAddress(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_verified))
                .build();
        return uri;
    }

    public Uri buildLocalAddress(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath("localhost:5000")
                .build();
        return uri;
    }

    //on post exec should be -> handle successful contacts query
    public void handleContactsQueryResponseOnPostExec(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Query was successful
                progressBar.setVisibility(View.GONE);

                //need to populate the contacts list before passing it to the adapter
                parseHerokuResult(result);
                //added from here
                adapter = new MyRecyclerViewAdapter(getContext(), mContactFeedItemList);
                mRecyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onContactItemClick(ContactFeedItem item) {
                        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_LONG).show();

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
        } catch(JSONException e){
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }


    //on click should be -> load detail fag of that contact

    //on pending button press -> new query for all pending requests others sent

    //on sent button press -> new query for all pending requests others sent

    //search for new contacts

    //sned new contact request

    //delete contact request

    //accept contact request

    private void parseHerokuResult(String result) {
        //String imgAddress = "https://www.logoground.com/uploads/2017108832017-04-203705844rabbitchat.jpg";
        //maybe add an array of images?
        //String imgAddress = "http://2.bp.blogspot.com/-BvXcUdArvGk/UK54mxYSUOI/AAAAAAAAbg8/XycJSQH_IrU/s640/funny-animal-captions-005-020.jpg";
        String imgAddress = "http://ajax.googleapis.com/ajax/services/search/images?q=%s&v=1.0&rsz=large&start=1";
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("contacts");
            mContactFeedItemList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                ContactFeedItem item = new ContactFeedItem();
                item.setTitle(post.optString("username"));
                item.setThumbnail(imgAddress);
                mContactFeedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CONTACTS BODY PARSED, LIST [0]: ", mContactFeedItemList.get(0).toString());
        //return mContactFeedItemList;
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");
            feedsList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setTitle(post.optString("title"));
                item.setThumbnail(post.optString("thumbnail"));
                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
