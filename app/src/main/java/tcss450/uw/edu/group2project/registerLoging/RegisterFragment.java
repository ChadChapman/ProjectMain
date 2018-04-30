package tcss450.uw.edu.group2project.registerLoging;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.Credentials;

public class RegisterFragment extends Fragment {
    //min size for any textfield
    private final int mMinSizeText = 6;
    private RegisterFragment.OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button b = (Button) v.findViewById(R.id.registerfrag_button_register);
        b.setOnClickListener(this::goSuccess);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragment.OnFragmentInteractionListener) {
            mListener = (RegisterFragment.OnFragmentInteractionListener) context;
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


    public void goSuccess(View view) {
        int errors = 0;
        if (mListener != null) {
            EditText emailField = ((EditText) getActivity().findViewById(R.id.registerfrag_edittext_email));
            String email = emailField.getText().toString();
            EditText fNameField = ((EditText) getActivity().findViewById(R.id.registerfrag_edittext_firstname));
            String fName = fNameField.getText().toString();
            EditText lNameField = ((EditText) getActivity().findViewById(R.id.registerfrag_edittext_lastname));
            String lName = lNameField.getText().toString();
            EditText uNameField = ((EditText) getActivity().findViewById(R.id.registerfrag_edittext_username));
            String uName = uNameField.getText().toString();
            EditText pass1Field = ((EditText) getActivity().findViewById(R.id.registerfrag_edittext_password1));
            String pass1 = pass1Field.getText().toString();
            EditText pass2Field = ((EditText) getActivity().findViewById(R.id.registerfrag_edittext_password2));
            String pass2 = pass2Field.getText().toString();


            int indAt = email.indexOf('@');
            //Minimum checks for the fields....

            if (indAt < 0 && indAt < email.length() - 1) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_email)).setError("Invalid Email!");
                errors += 1;
            }
            if (email.equals("")) {
                emailField.setError("Email cannot be empty!");
                errors += 1;
            }
            if (!isValidEmail(email)) {
                emailField.setError("Must be valid email!");
                errors += 1;
            }
            if (fName.equals("")) {
                fNameField.setError("First name cannot be empty!");
                errors += 1;
            }
            if (lName.equals("")) {
                lNameField.setError("Last name cannot be empty!");
                errors += 1;
            }
            if (uName.equals("")) {
                uNameField.setError("Username cannot be empty");
                errors += 1;
            }
            if (pass1.equals("")) {
                pass1Field.setError("Password cannot be empty");
                errors += 1;
            }
            if (pass2.equals("")) {
                pass2Field.setError("Confirm Password cannot be empty");
                errors += 1;
            }
            if (!(pass1.equals(pass2))) {
                pass1Field.setError("Passwords do not match!");
                errors += 1;
            }
            if (pass1.length() < mMinSizeText) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).setError("Password is too short!");
                errors += 1;
            }
            if (errors == 0) {
                //we need another method to verify email then run this code... but for now we
                //dont verify email.
                Editable pwd = (Editable) ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).getText();
                mListener.onRegisterAttempt(new Credentials.Builder(uName, pwd)
                        .addEmail(email)
                        .addFirstName(fName)
                        .addLastName(lName)
                        .build());
            }

        }
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public boolean isValidEmail(String email) {
        String expression = "^[\\w]+@([\\w]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
        ((TextView) getView().findViewById(R.id.registerfrag_edittext_username))
                .setError("Login Unsuccessful");
    }

    public interface OnFragmentInteractionListener {
        void onRegisterAttempt(Credentials credentials);
    }
}