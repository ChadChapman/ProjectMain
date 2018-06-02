package tcss450.uw.edu.group2project.registerLoging;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import tcss450.uw.edu.group2project.R;

/**
 * Allows user to reset password
 * @author Charles Bryan
 * @author Chad Chapman
 * @author Khoa Doan
 * @author Ifor Kalezic
 * @author Josh Lansang
 * @author Raymond Schooley
 * @version 1.0
 */
public class PasswordChangeFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private EditText mUsername;
    private EditText mPassword;

    public PasswordChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_change, container, false);
        mUsername = (EditText) view.findViewById(R.id.change_password_username);
        mPassword = (EditText) view.findViewById(R.id.change_password_password);
        Button b = (Button) view.findViewById(R.id.change_password_submit_button);
        b.setOnClickListener(this::initiateReset);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public void initiateReset(View view) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        mListener.onPasswordReset(username, password);
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
        void onPasswordReset(String username, String password);
    }
}
