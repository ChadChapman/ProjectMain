package tcss450.uw.edu.group2project.chatApp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450.uw.edu.group2project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendProfileFragment extends Fragment {

    public FriendProfileFragment() {
        // Required empty public constructor
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
        ((TextView)getActivity().findViewById(R.id.friends_name_textView)).setText(getArguments().getString("fname"));
        ((TextView)getActivity().findViewById(R.id.fname_textView)).setText(getArguments().getString("lname"));
        ((TextView)getActivity().findViewById(R.id.lname_textView)).setText(getArguments().getString("title"));
    }

}
