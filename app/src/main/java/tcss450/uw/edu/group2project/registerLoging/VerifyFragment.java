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
import android.widget.Toast;

import tcss450.uw.edu.group2project.R;

public class VerifyFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    private int mVCode;
    private EditText mEditText;

    public VerifyFragment() {
       
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify, container, false);

        mVCode = getArguments().getInt(getString(R.string.keys_bundle_vcode));

        mEditText = (EditText) v.findViewById(R.id.verifyEditText);
        Button b = (Button) v.findViewById(R.id.verifyButton);
        b.setOnClickListener(this::onClick);
        return v;
    }

    @Override
    public void onClick(View theButton) {
        if (new Integer(mVCode).equals(Integer.parseInt(mEditText.getText().toString()))) {
            Toast.makeText(getContext(), "Succes!!", Toast.LENGTH_SHORT).show();
            mListener.onFragmentInteraction();
        } else {
            System.out.println("mVCode: " + mVCode);
            System.out.println(Integer.parseInt(mEditText.getText().toString()));

            Toast.makeText(getActivity(), "Incorrect, try again", Toast.LENGTH_SHORT);
        }
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
}
