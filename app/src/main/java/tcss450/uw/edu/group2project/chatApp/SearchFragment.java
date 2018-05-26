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

        Button b = (Button) v.findViewById(R.id.search_button_search_by_username);
        b.setOnClickListener(this::onSearchUsernameButtonClicked);

        b = (Button) v.findViewById(R.id.search_button_search_by_email);
        b.setOnClickListener(this::onSearchEmailButtonClicked);

        b = (Button) v.findViewById(R.id.search_button_search_by_name);
        b.setOnClickListener(this::onSearchNameButtonClicked);

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
