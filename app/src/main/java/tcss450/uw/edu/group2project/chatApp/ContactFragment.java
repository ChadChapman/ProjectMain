package tcss450.uw.edu.group2project.chatApp;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private ProgressBar progressBar;
    private List<ContactFeedItem> mContactFeedItemList;
    private String mUserMemberIDStr;
    private Uri mContactsUri;
    private View v;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contact, container, false);
        // Inflate the layout for this fragment
        mUserMemberIDStr = getArguments().getString("memberID");
        mRecyclerView = (RecyclerView) (v.findViewById(R.id.try_recycle_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = (ProgressBar) (v.findViewById(R.id.try_progress_bar));
        mContactsUri = buildHerokuAddress(getString(R.string.ep_contacts_verified));

        //Load the list of friends
        loadContacts();

        //Set up each buttons in fragment
        RadioButton rb = (RadioButton) v.findViewById(R.id.friends_radioButton);
        rb.setOnClickListener(this::friendsRadioButton);
        rb = (RadioButton) v.findViewById(R.id.pending_radioButton);
        rb.setOnClickListener(this::friendsRadioButton);
        rb = (RadioButton) v.findViewById(R.id.sent_radioButton);
        rb.setOnClickListener(this::friendsRadioButton);
        Button add = (Button) v.findViewById(R.id.add_button);
        add.setOnClickListener(this::onAddButtonClicked);
        return v;
    }

    //add a friend
    private void onAddButtonClicked(View view) {
        mContactsUri = buildHerokuAddress(getString(R.string.ep_contacts_add));
        String friend = ((EditText) (getActivity().findViewById(R.id.add_editText))).getText().toString();
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberIDStr);
            msg.put("username_b", friend);

        } catch (JSONException e) {
            Log.wtf("CONTACTS VERIFIED ALL", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(mContactsUri.toString(), msg)
                .onPostExecute(this::handleAddOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    //switch between list
    private void friendsRadioButton(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.friends_radioButton:
                if (checked)
                    mContactsUri = buildHerokuAddress(getString(R.string.ep_contacts_verified));
                getActivity().findViewById(R.id.add_editText).setVisibility(View.GONE);
                getActivity().findViewById(R.id.add_button).setVisibility(View.GONE);
                break;
            case R.id.pending_radioButton:
                if (checked)
                    mContactsUri = buildHerokuAddress(getString(R.string.ep_contacts_pending_requests));
                getActivity().findViewById(R.id.add_editText).setVisibility(View.GONE);
                getActivity().findViewById(R.id.add_button).setVisibility(View.GONE);
                break;
            case R.id.sent_radioButton:
                if (checked)
                    mContactsUri = buildHerokuAddress(getString(R.string.ep_contacts_sent_requests));
                getActivity().findViewById(R.id.add_editText).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.add_button).setVisibility(View.VISIBLE);
                break;
        }
        loadContacts();
    }

    //Load the contacts in each list friends,pending,sent.
    //Depending on what was clicked
    public void loadContacts() {
        JSONObject jsonObject = createVerifiedContactsRequestObject();
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mContactsUri.toString(), jsonObject)
                .onPostExecute(this::handleContactsQueryResponseOnPostExec)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    //create a friend request to another user
    public JSONObject createVerifiedContactsRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberIDStr);
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

    //build a heroku url with a passed on ep
    public Uri buildHerokuAddress(String ep) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(ep)
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
                //create a recycler adapter
                adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.list_row, null);
                        return new CustomViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, int i) {
                        //FeedItem feedItem = feedItemList.get(i);
                        ContactFeedItem feedItem = mContactFeedItemList.get(i);

                        //Render image using Picasso library
                        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
                            Picasso.with(getContext()).load(feedItem.getThumbnail())
                                    .error(R.drawable.contacts_image_error)
                                    .placeholder(R.drawable.contacts_image_placeholder)
                                    .into(((CustomViewHolder) customViewHolder).imageView);
                        }

                        //Setting text view title
                        ((CustomViewHolder) customViewHolder).textView.setText(feedItem.getTitle());

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onContactItemClick(feedItem);
                            }
                        };


                        ((CustomViewHolder) customViewHolder).imageView.setOnClickListener(listener);
                        ((CustomViewHolder) customViewHolder).textView.setOnClickListener(listener);
                    }

                    @Override
                    public int getItemCount() {
                        return (null != mContactFeedItemList ? mContactFeedItemList.size() : 0);
                    }
                };
                mRecyclerView.setAdapter(adapter);
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
            JSONArray posts = response.optJSONArray(getString(R.string.contacts));
            mContactFeedItemList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                ContactFeedItem item = new ContactFeedItem();
                item.setTitle(post.optString(getString(R.string.username)));
                item.setThumbnail(imgAddress);
                item.setFname(post.optString(getString(R.string.firstname)));
                item.setLname(post.optString(getString(R.string.lastname)));
                mContactFeedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return mContactFeedItemList;
    }

    //
    public void handleAddOnPost(String result) {

        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                Toast.makeText(getContext(), "Sent", Toast.LENGTH_SHORT).show();
                loadContacts();
            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }


    private void onContactItemClick(ContactFeedItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("fname", item.getFname());
        bundle.putString("lname", item.getLname());
        bundle.putString("title", item.getTitle());

        Fragment friend = new FriendProfileFragment();
        friend.setArguments(bundle);


        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, friend, "friend")
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * begin internal class for the viewholder
     */
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
