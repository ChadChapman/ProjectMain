package tcss450.uw.edu.group2project.chatApp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tcss450.uw.edu.group2project.R;

/**
 * A simple {@link Fragment} subclass.
 * This class will display all the details of a contact
 */
public class ContactDetailsFragment extends Fragment {


    public ContactDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

}
