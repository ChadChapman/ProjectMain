package tcss450.uw.edu.group2project;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class LandingFragment extends Fragment {

    OnLandingFragmentInteractionListener mListener;

    public LandingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();


        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false)) {
            getView().findViewById(R.id.landing_button_logout)
                    .setOnClickListener(v -> mListener.onLogout());
        } else {
            getView().findViewById(R.id.landing_button_logout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLandingFragmentInteractionListener) {
            mListener = (OnLandingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnLandingFragmentInteractionListener {
        void onLogout();
    }
}
