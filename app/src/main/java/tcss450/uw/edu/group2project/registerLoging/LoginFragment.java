package tcss450.uw.edu.group2project.registerLoging;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    EditText userEdit;
    EditText passEdit;


    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        userEdit = (EditText) view.findViewById(R.id.login_edit_text_username);
        passEdit = (EditText) view.findViewById(R.id.login_edit_text_password);
        Button b = (Button) view.findViewById(R.id.login_button_login);
        b.setOnClickListener(this::attemptLogin);
        b = (Button) view.findViewById(R.id.login_button_register);
        b.setOnClickListener(this::registerOpen);
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

    private void registerOpen(View view) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
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
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     *
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
