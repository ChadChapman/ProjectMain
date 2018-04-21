package tcss450.uw.edu.group2project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    EditText regEdit;
    EditText logEdit;


    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        regEdit = (EditText) view.findViewById(R.id.login_edit_text_username);
        logEdit = (EditText) view.findViewById(R.id.login_edit_text_password);
        Button b = (Button) view.findViewById(R.id.login_button_login);
        b.setOnClickListener(this);
        b = (Button) view.findViewById(R.id.login_button_register);
        b.setOnClickListener(this);

        return view;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }



    @Override
    public void onClick(View v) {
        if (logEdit.getText().toString().length() < 1
                || regEdit.getText().toString().length() < 1) {
            //Display Toast
            Toast.makeText(getActivity(), "Username and Password field cannot be empty"
            , Toast.LENGTH_LONG).show();

        }
    }
}

