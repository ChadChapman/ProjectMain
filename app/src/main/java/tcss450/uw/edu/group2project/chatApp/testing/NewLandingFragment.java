package tcss450.uw.edu.group2project.chatApp.testing;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tcss450.uw.edu.group2project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewLandingFragment extends Fragment {


    public NewLandingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_new_landing, container, false);
        //Search for zip
        EditText search = v.findViewById(R.id.search_zip_textview);
        search.setOnEditorActionListener(this::pressedDone);

        return v;
    }
    private boolean pressedDone(TextView exampleView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            example_confirm();//match this behavior to your 'Send' (or Confirm) button
        }
        return true;
    }

    private void example_confirm() {
        int duration = Toast.LENGTH_SHORT;
        Context context = getActivity().getBaseContext();
        Toast toast = Toast.makeText(context, "you searched", duration);
        toast.show();
        //Log.e("","this");
    }
}
