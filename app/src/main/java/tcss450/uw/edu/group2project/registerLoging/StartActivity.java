/**
 * merged so far: Igor, Josh, Raymond
 */

package tcss450.uw.edu.group2project.registerLoging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.group2project.chatApp.ChatActivity;
import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.LandingFragment;
import tcss450.uw.edu.group2project.model.Credentials;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

public class StartActivity extends AppCompatActivity
        implements LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    private Credentials mCredentials;
    //private int mUserMemberID;
    private String mUserMemberIDStr;
    private int mUserMemberIDInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        if (savedInstanceState == null) {
            if (findViewById(R.id.start_constraint_layout) != null) {
                SharedPreferences prefs =
                        getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);

                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in),
                        false)) {
                    loadLandingFragment();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.start_constraint_layout,
                                    new LoginFragment(),
                                    getString(R.string.keys_fragment_login))
                            .commit();
                }
            }
        }
    }


    private void loadLandingFragment() {
//        LandingFragment landingFragment = new LandingFragment();
//
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.start_constraint_layout, landingFragment, getString(R.string.keys_fragment_landing));
//        // Commit the transaction
//        transaction.commit();

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userMemberID", mUserMemberIDStr);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);
    }

    /*
    --------------------------Register fragment interface---------------------------
     */
    @Override
    public void onRegisterAttempt(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

      /*
    --------------------------Login fragment interface-------------------------------
     */

    @Override
    public void onLoginAttempt(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    @Override
    public void onRegisterClicked() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.start_constraint_layout, new RegisterFragment(), getString(R.string.keys_fragment_register))
                .addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

     /*
    --------------------------Async handlers-------------------------------
     */


    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mUserMemberIDInt = resultsJSON.getInt("message");
        //        Log.e("MEMBERID WAS: ", mUserMemberIDStr);
                checkStayLoggedIn();
                loadLandingFragment();
            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                LoginFragment frag =
                        (LoginFragment) getSupportFragmentManager()
                                .findFragmentByTag(
                                        getString(R.string.keys_fragment_login));
                frag.setError("Log in unsuccessful");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                getSupportFragmentManager().popBackStack();
                loadLandingFragment();
            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                RegisterFragment frag =
                        (RegisterFragment) getSupportFragmentManager()
                                .findFragmentByTag(
                                        getString(R.string.keys_fragment_register));
                frag.setError("Register unsuccessful");
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }



    /*
    --------------------------Shared Preferences Handling-------------------------------
     */


    private void checkStayLoggedIn() {
        if (((CheckBox) findViewById(R.id.login_check_box_stay_logged_in)).isChecked()) {
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //save the username for later usage
            prefs.edit().putString(
                    getString(R.string.keys_prefs_username),
                    mCredentials.getUsername())
                    .apply();
            //save the users “want” to stay logged in
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    true)
                    .apply();
        }
    }


}
