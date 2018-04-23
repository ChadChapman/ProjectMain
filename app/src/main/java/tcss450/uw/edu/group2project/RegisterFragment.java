package tcss450.uw.edu.group2project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import tcss450.uw.edu.group2project.model.Credentials;

public class RegisterFragment extends Fragment {
    //min size for any textfield
    private final int mMinSizeText = 6;
    private LoginFragment.OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Button b = (Button) v.findViewById(R.id.registerButton);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goSuccess(view);
            }
        });
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.OnFragmentInteractionListener) {
            mListener = (LoginFragment.OnFragmentInteractionListener) context;
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


    public void goSuccess(View view){
        if(mListener != null){
            String email = ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_email)).getText().toString();
            String fname = ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_firstname)).getText().toString();
            String lname = ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_lastname)).getText().toString();
            String nickname = ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_nickname)).getText().toString();
            String password1 = ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).getText().toString();
            String password2 = ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password2)).getText().toString();

            int indAt = email.indexOf('@');
            //Minimum checks for the fields....
            if (indAt > 0 && indAt < email.length() - 1) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_email)).setError("Invalid Email!");
            } else if (email.equals("")) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_email)).setError("Email cannot be empty!");
            } else if (fname.equals("")) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_firstname)).setError("First name cannot be empty!");
            } else if (lname.equals("")) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_lastname)).setError("Last name cannot be empty!");
            } else if (nickname.equals("")) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_nickname)).setError("Username cannot be empty");
            } else if (password1.equals("")) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).setError("Password cannot be empty");
            } else if (password2.equals("")) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password2)).setError("Confirm Password cannot be empty");
            } else if (!(password1.equals(password2))) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).setError("Password do not match!");
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password2)).setError("Password do not match!");
            } else if (password1.length() < mMinSizeText) {
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).setError("Password do not match!");
                ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password2)).setError("Password do not match!");
            } else {
                //we need another method to verify email then run this code... but for now we
                //dont verify email.
                Editable pwd = (Editable) ((TextView) getActivity().findViewById(R.id.registerfrag_edittext_password1)).getText();
                /*mListener.function(new Credentials.Builder(nickname, pwd)
                        .addEmail(email)
                        .addFirstName(fname)
                        .addLastName(lname)
                        .build());*/
            }

        }
    }


}