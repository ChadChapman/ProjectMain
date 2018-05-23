package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

        SharedPreferences sharedPreferences = this.getActivity()
                .getSharedPreferences(getString(R.string.keys_shared_setting_prefs), Context.MODE_PRIVATE);

        /* THEME COLOR BUTTONS */
        Button b = (Button) v.findViewById(R.id.setting_button_theme_1);
        b.setOnClickListener(this::onThemeOneButtonClicked);

        b = (Button) v.findViewById(R.id.setting_button_theme_2);
        b.setOnClickListener(this::onThemeTwoButtonClicked);

        b = (Button) v.findViewById(R.id.setting_button_theme_3);
        b.setOnClickListener(this::onThemeThreeButtonClicked);

        b = (Button) v.findViewById(R.id.setting_button_theme_4);
        b.setOnClickListener(this::onThemeFourButtonClicked);

        /* TEXT SIZE BUTTONS */
        b = (Button) v.findViewById(R.id.setting_button_text_small);
        b.setOnClickListener(this::onSmallTextSizeButtonClicked);

        b = (Button) v.findViewById(R.id.setting_button_text_medium);
        b.setOnClickListener(this::onMediumTextSizeButtonClicked);

        b = (Button) v.findViewById(R.id.setting_button_text_large);
        b.setOnClickListener(this::onLargeTextSizeButtonClicked);

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

    private void onThemeOneButtonClicked(View v) {
        if (mListener != null) {
            mListener.onSettingThemeButtonClicked(1);
        }
    }

    private void onThemeTwoButtonClicked(View v) {
        if (mListener != null) {
            mListener.onSettingThemeButtonClicked(2);
        }
    }

    private void onThemeThreeButtonClicked(View v) {
        if (mListener != null) {
            mListener.onSettingThemeButtonClicked(3);
        }
    }

    private void onThemeFourButtonClicked(View v) {
        if (mListener != null) {
            mListener.onSettingThemeButtonClicked(4);
        }
    }

    private void onSmallTextSizeButtonClicked(View v) {
        if (mListener != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            mListener.onSettingTextSizeButtonClicked(1);
        }
    }

    private void onMediumTextSizeButtonClicked(View v) {
        if (mListener != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            mListener.onSettingTextSizeButtonClicked(2);
        }
    }

    private void onLargeTextSizeButtonClicked(View v) {
        if (mListener != null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            mListener.onSettingTextSizeButtonClicked(3);
        }
    }

    public interface OnSettingFragmentInteractionListener {
        void onSettingThemeButtonClicked(int color);
        void onSettingTextSizeButtonClicked(int size);
    }


}
