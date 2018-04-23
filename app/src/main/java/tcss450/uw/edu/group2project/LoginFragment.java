package tcss450.uw.edu.group2project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tcss450.uw.edu.group2project.model.Credentials;


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


    private OnLoginFragmentInteractionListener mListener;

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
        b.setOnClickListener(this::attemptLogin);
        b = (Button) view.findViewById(R.id.login_button_register);
        b.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void attemptLogin(View view) {
        if (mListener != null) {
            EditText username = getActivity().findViewById(R.id.login_edit_text_username);
            EditText password = getActivity().findViewById(R.id.login_edit_text_password);
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (usernameText.equals("")) {
                username.setError("Username cannot be empty");
            } else if (passwordText.equals("")) {
                password.setError("Password cannot be empty");
            } else {
                Credentials.Builder builder = new Credentials.Builder(usernameText, new SpannableStringBuilder(passwordText));
                mListener.onLoginAttempt(builder.build());
            }
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

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
        //Log in unsuccessful for reason: err. Try again.
        //you may want to add error stuffs for the user here.
        ((TextView) getView().findViewById(R.id.login_edit_text_username))
                .setError("Login Unsuccessful");
    }

    public interface OnLoginFragmentInteractionListener {
        void onLoginAttempt(Credentials credentials);
        void onRegisterClicked();
    }
}

