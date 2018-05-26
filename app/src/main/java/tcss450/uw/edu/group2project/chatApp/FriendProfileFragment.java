package tcss450.uw.edu.group2project.chatApp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ContactFeedItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendProfileFragment extends Fragment {
    private ContactFeedItem item;

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

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((TextView)getActivity().findViewById(R.id.friends_name_textView)).setText(item.getTitle());
        ((TextView)getActivity().findViewById(R.id.fname_textView)).setText(item.getFname());
        ((TextView)getActivity().findViewById(R.id.lname_textView)).setText(item.getLname());
    }

}
