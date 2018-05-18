package tcss450.uw.edu.group2project.chatApp;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendProfileFragment extends Fragment {

    private final String TAG = "FriendProfileFragment";

    private ContactFeedItem item;
    private int mContactStatus;
    private int mUserMemberID;

    public FriendProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public FriendProfileFragment(ContactFeedItem item) {
        this.item = item;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend_profile, container, false);

        mContactStatus = getArguments().getInt("mContactStatus");
        mUserMemberID = getArguments().getInt("mUserMemberID");
        System.out.println("Contact Status = " + mContactStatus);
        ((TextView)v.findViewById(R.id.friends_name_textView)).setText(item.getTitle());
        ((TextView)v.findViewById(R.id.fname_textView)).setText(item.getFname());
        ((TextView)v.findViewById(R.id.lname_textView)).setText(item.getLname());

        Button b;
        //handle pending requests
        if (mContactStatus == 2) {

            b = (Button) v.findViewById(R.id.friend_accept_button);
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(this::acceptRequest);
            b = (Button) v.findViewById(R.id.friend_deny_button);
            //b.setVisibility(View.VISIBLE);
            b.setOnClickListener(this::denyRequest);
        //handle sent requests
        } else if (mContactStatus == 3){
            b = (Button) v.findViewById(R.id.friend_accept_button);
            b.setVisibility(View.GONE);
            b = (Button) v.findViewById(R.id.friend_deny_button);
            b.setText("Delete Request");
            //b.setVisibility(View.VISIBLE);
            b.setOnClickListener(this::denyRequest);
        } else {
            b = (Button) v.findViewById(R.id.friend_accept_button);
            b.setVisibility(View.GONE);
            b = (Button) v.findViewById(R.id.friend_deny_button);
           // b.setVisibility(View.GONE);
            b.setText("Delete Contact");
            b.setOnClickListener(this::denyRequest);
        }

        return v;
    }

    public void acceptRequest(View theButton) {
        sendRequest(1);
    }

    public void denyRequest(View theButton) {
        sendRequest(-9);
    }

    public void sendRequest(int val) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath("updateContactOrRequest")
                .build();

        JSONObject msg = new JSONObject();
        try {
            msg.put("ida", mUserMemberID);
            msg.put("idb", item.getMemberID());
            msg.put("value", val);
            System.out.println("ida = " + mUserMemberID);
            System.out.println("idb = " + item.getMemberID());
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleUpdateContactOnPost)
                    .build().execute();
        } catch (JSONException e) {
            Log.wtf(TAG, "FAILED TO CALL ENDPOINT");
        }
    }

    public void handleUpdateContactOnPost(String result) {
        System.out.println(result);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            System.out.println(success);
            if (success) {
                Toast.makeText(getActivity(), "Udated contact", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getActivity(), "Failed to udate contact", Toast.LENGTH_SHORT);
            }
        } catch (JSONException e) {
            Log.wtf(TAG, "FAILED TO UPDATE CONTACT STATUS");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
