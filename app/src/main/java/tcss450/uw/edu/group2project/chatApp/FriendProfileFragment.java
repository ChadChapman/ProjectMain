package tcss450.uw.edu.group2project.chatApp;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.annotation.SuppressLint;
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
import tcss450.uw.edu.group2project.model.ContactFeedItem;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendProfileFragment extends Fragment {
    private ContactFeedItem item;

    private final String TAG = "FriendProfileFragment";

    private int mContactStatus;
    private String mUserMemberID;
    private int friendMemID;

    public FriendProfileFragment() {
        // Required empty public constructor
    }

    //need ot get rid of this
    @SuppressLint("ValidFragment")
    public FriendProfileFragment(ContactFeedItem item) {
        this.item = item;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend_profile, container, false);
        Log.e("","Contact Status = uo" + mContactStatus);
        mContactStatus = getArguments().getInt("mContactStatus");
        mUserMemberID = getArguments().getString("mUserMemberID");
        friendMemID = getArguments().getInt("memberID");
        ((TextView)v.findViewById(R.id.friends_name_textView)).setText(getArguments().getString("fname"));
        ((TextView)v.findViewById(R.id.fname_textView)).setText(getArguments().getString("lname"));
        ((TextView)v.findViewById(R.id.lname_textView)).setText(getArguments().getString("title"));
        Log.e("","Contact Status = " + mContactStatus);
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
        Log.e("","aaida = " + mUserMemberID);
        Log.e("","aaidb = " + friendMemID);
        sendRequest(1);
    }

    public void denyRequest(View theButton) {
        Log.e("","ida = " + mUserMemberID);
        Log.e("","idb = " + friendMemID);
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
            msg.put("idb", friendMemID);
            msg.put("value", val);
            Log.e("","ida = " + mUserMemberID);
            Log.e("","idb = " + friendMemID);
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
        ((TextView)getActivity().findViewById(R.id.friends_name_textView)).setText(item.getTitle());
        ((TextView)getActivity().findViewById(R.id.fname_textView)).setText(item.getFname());
        ((TextView)getActivity().findViewById(R.id.lname_textView)).setText(item.getLname());
    }

}