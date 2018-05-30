package tcss450.uw.edu.group2project.chatApp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import tcss450.uw.edu.group2project.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnSearchFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends Fragment {

    private View v;
    private OnSearchFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);

        TextView uName = v.findViewById(R.id.search_text_view_username);
        TextView eMail = v.findViewById(R.id.search_text_view_email);
        TextView fName = v.findViewById(R.id.search_text_view_first_name);
        TextView lName = v.findViewById(R.id.search_text_view_last_name);

        Button b = (Button) v.findViewById(R.id.search_button_search_by_username);
        b.setOnClickListener(this::onSearchUsernameButtonClicked);

        Button b1 = (Button) v.findViewById(R.id.search_button_search_by_email);
        b1.setOnClickListener(this::onSearchEmailButtonClicked);

        Button b2 = (Button) v.findViewById(R.id.search_button_search_by_name);
        b2.setOnClickListener(this::onSearchNameButtonClicked);


        if(ChatActivity.mTheme == 1){
            b.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            b1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            uName.setTextColor(getResources().getColor(R.color.colorPrimary));
            eMail.setTextColor(getResources().getColor(R.color.colorPrimary));
            fName.setTextColor(getResources().getColor(R.color.colorPrimary));
            lName.setTextColor(getResources().getColor(R.color.colorPrimary));

        }else if(ChatActivity.mTheme == 2){
            b.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            b1.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
            b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));

            uName.setTextColor(getResources().getColor(R.color.colorPrimary2));
            eMail.setTextColor(getResources().getColor(R.color.colorPrimary2));
            fName.setTextColor(getResources().getColor(R.color.colorPrimary2));
            lName.setTextColor(getResources().getColor(R.color.colorPrimary2));
        }else if(ChatActivity.mTheme == 3){
            b.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));
            b1.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));
            b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));

            uName.setTextColor(getResources().getColor(R.color.colorPrimary3));
            eMail.setTextColor(getResources().getColor(R.color.colorPrimary3));
            fName.setTextColor(getResources().getColor(R.color.colorPrimary3));
            lName.setTextColor(getResources().getColor(R.color.colorPrimary3));
        }else if(ChatActivity.mTheme == 4){
            b.setBackgroundColor(getResources().getColor(R.color.colorPrimary4));
            b1.setBackgroundColor(getResources().getColor(R.color.colorPrimary4));
            b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary4));

            uName.setTextColor(getResources().getColor(R.color.colorPrimary4));
            eMail.setTextColor(getResources().getColor(R.color.colorPrimary4));
            fName.setTextColor(getResources().getColor(R.color.colorPrimary4));
            lName.setTextColor(getResources().getColor(R.color.colorPrimary4));
        }

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFragmentInteractionListener) {
            mListener = (OnSearchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onSearchUsernameButtonClicked(View view) {
        if (mListener != null) {
            String username = ((EditText) v.findViewById(R.id.search_edit_text_username)).getText().toString();
            mListener.onSearchByUsernameButtonClicked(username);
        }
    }

    private void onSearchEmailButtonClicked(View view) {
        if (mListener != null) {
            String email = ((EditText) v.findViewById(R.id.search_edit_text_email)).getText().toString();
            mListener.onSearchByEmailButtonClicked(email);
        }
    }

    private void onSearchNameButtonClicked(View view) {
        if (mListener != null) {
            String firstName = ((EditText) v.findViewById(R.id.search_edit_text_first_name)).getText().toString();
            String lastName = ((EditText) v.findViewById(R.id.search_edit_text_last_name)).getText().toString();
            mListener.onSearchByNameButtonClicked(firstName, lastName);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSearchFragmentInteractionListener {
        void onSearchByEmailButtonClicked(String email);
        void onSearchByUsernameButtonClicked(String username);
        void onSearchByNameButtonClicked(String firstname, String lastname);
    }
}
