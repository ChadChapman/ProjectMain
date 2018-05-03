package tcss450.uw.edu.group2project.chatApp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tcss450.uw.edu.group2project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    //query for all the contacts associated with this person
    //get those contacts into a list
    //decide which fields to display from the list
    //display a list of all contacts' information
    //edit a contact
    //start a chat with a contact
    //see if a contact is online right now
    //store some contacts locally? to make it quicker to get them?
        //then compare to see any changes with async thread?
    //see contact details
    //see details about last interactions with that contact



    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }


}
