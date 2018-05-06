package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import tcss450.uw.edu.group2project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private View v;
    private OnSettingFragmentInteractionListener mListener;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_setting, container, false);


        Button b = (Button) v.findViewById(R.id.setting_button_cancel);
        b.setOnClickListener(this::onCancelButtonClicked);

        b = (Button) v.findViewById(R.id.setting_button_save);
        b.setOnClickListener(this::onSaveButtonClicked);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingFragmentInteractionListener) {
            mListener = (OnSettingFragmentInteractionListener) context;
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

    private void onCancelButtonClicked(View v) {
        if (mListener != null) {
            mListener.onSettingCancelButtonClicked();
        }
    }

    private void onSaveButtonClicked(View view) {
        if (mListener != null) {
            mListener.onSettingSaveButtonClicked(v);
        }
    }



    public interface OnSettingFragmentInteractionListener {
        void onSettingSaveButtonClicked(View v);
        void onSettingCancelButtonClicked();
    }


}
