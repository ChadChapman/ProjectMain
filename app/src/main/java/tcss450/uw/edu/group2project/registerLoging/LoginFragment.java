package tcss450.uw.edu.group2project.registerLoging;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.Credentials;


/**
 * Accepts user login credentials and interacts with StartActivity to hit the login ep.
 * Makes sure username and password are not empty.
 * @author Charles Bryan
 * @author Chad Chapman
 * @author Khoa Doan
 * @author Ifor Kalezic
 * @author Josh Lansang
 * @author Raymond Schooley
 * @version 1.0
 */
public class LoginFragment extends Fragment {

    /**UI elements*/
    EditText userEdit;
    EditText passEdit;
    ProgressBar mProgressBar;
    Button b;

    /**StartActivity implements interface to hit eps and navigate fragment changes*/
    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Inflate view, save references to ui elements and set onClick listeners for buttons.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        userEdit = (EditText) view.findViewById(R.id.login_edit_text_username);
        passEdit = (EditText) view.findViewById(R.id.login_edit_text_password);
        mProgressBar = (ProgressBar)view.findViewById(R.id.loginProgressBar);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        b = (Button) view.findViewById(R.id.login_button_login);
        b.setOnClickListener(this::attemptLogin);
        Button c = (Button) view.findViewById(R.id.login_button_register);
        c.setOnClickListener(this::registerOpen);
        c = (Button) view.findViewById(R.id.login_button_reset_password);
        c.setOnClickListener(this::changePassword);
        return view;
    }

    /**
     * Save instance of StartActivity as fragment listener
     * @param context
     */
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

    /**
     * Load PasswordChangeFragment when clicking password reset button.
     * @param view
     */
    private void changePassword(View view) {
        if (mListener != null) {
            mListener.onPasswordClicked();
        }
    }

    /**
     * Load RegisterFragment when clicking register button.
     * @param view
     */
    private void registerOpen(View view) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
    }

    /**
     * Take in user info and call Activity method to hit login ep.
     * @param view
     */
    public void attemptLogin(View view) {
        if (mListener != null) {
            //Start progress bar so user knows something is happening
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            //Disable button so user can't press it
            b.setEnabled(false);
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            //Retrieve strings entered into edit texts
            EditText username = getActivity().findViewById(R.id.login_edit_text_username);

            EditText password = getActivity().findViewById(R.id.login_edit_text_password);
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            //make sure fields are non empty
            if (usernameText.equals("")) {
                username.setError("Username cannot be empty");
            } else if (passwordText.equals("")) {
                password.setError("Password cannot be empty");
            } else {
                //Update username in shared prefs
                prefs.edit().putString(
                        getString(R.string.keys_prefs_username),
                        usernameText)
                        .apply();
                //build new credential and call endpoint
                Credentials.Builder builder = new Credentials.Builder(usernameText, new SpannableStringBuilder(passwordText));
                mListener.onLoginAttempt(builder.build());
            }
        }
    }


    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     *
     * @param err the error message to display.
     */
    public void setError(String err) {
        //Log in unsuccessful for reason: err. Try again.
        //you may want to add error stuffs for the user here.
        //Reset everything so user can try again.
        b.setEnabled(true);
        mProgressBar.setVisibility(ProgressBar.GONE);
        ((TextView) getActivity().findViewById(R.id.login_edit_text_username))
                .setError("Login Unsuccessful");
    }

    /**
     * Interface for StartActivity to implement.  Facilitates fragments changes and calls to
     * endpoints.
     */
    public interface OnLoginFragmentInteractionListener {
        void onLoginAttempt(Credentials credentials);

        void onRegisterClicked();

        void onPasswordClicked();
    }
}

