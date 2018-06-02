/**
 * Start Activity handles all async tasks and fragments pertaining to registering, logging in
 * and verify your account.
 * @author Charles Bryan
 * @author Chad Chapman
 * @author Khoa Doan
 * @author Ifor Kalezic
 * @author Josh Lansang
 * @author Raymond Schooley
 * @version 1.0
 */

package tcss450.uw.edu.group2project.registerLoging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import tcss450.uw.edu.group2project.chatApp.ChatActivity;
import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.chatApp.LandingFragment;
import tcss450.uw.edu.group2project.model.Credentials;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

public class StartActivity extends AppCompatActivity
        implements LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        VerifyFragment.OnFragmentInteractionListener,
        PasswordChangeFragment.OnFragmentInteractionListener{
    /**Account info*/
    private Credentials mCredentials;
    private String mUserMemberIDStr;
    private int mUserMemberIDInt;


    /**
     * App starts here.  You'll go to login screen or straight to the main landing
     * page if you've selected stay logged in.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Retrieve your preferences
        if (savedInstanceState == null) {
            if (findViewById(R.id.start_constraint_layout) != null) {
                SharedPreferences prefs =
                        getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                //Check if you've select stay logged in
                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in),
                        false)) {
                    loadVerifiedUserLandingActivity();
                //Go to login
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


    /**
     * Once you are verified load the ChatAvtivity and landing screen.
     */
    void loadVerifiedUserLandingActivity() {
        //If you're verified then the memberid should be in sharedPrefs.
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mUserMemberIDStr = prefs.getString(getString(R.string.keys_prefs_my_memberid), "MEMBERID NOT FOUND IN PREFS");
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userMemberID", mUserMemberIDStr);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);

    }

    private void sendEmail() {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .appendPath(getString(R.string.ep_email))
                .build();

        //email end point just need a memberid, pack that into a json object
        JSONObject msg = new JSONObject();

        try {
            msg.put("memberid", mUserMemberIDInt);
        } catch (JSONException e) {
            Log.e("StartActivity", "Email problem");
        }
        //notify user that email is being sent and send it
        Toast.makeText(this, "Sending new email", Toast.LENGTH_SHORT).show();
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .build().execute();
    }

    /*
    --------------------------Register fragment interface---------------------------
     */

    /**
     * When register button is click from the register fragment we need to hit the endpoint
     * and check if it was successful.
     * @param credentials Current users login credentials
     */
    @Override
    public void onRegisterAttempt(Credentials credentials) {
        //build the register web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();

        //build the JSONObject containing all credential info
        mCredentials = credentials;
        JSONObject msg = mCredentials.asJSONObject();



        //instantiate and execute the AsyncTask.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

      /*
    --------------------------Login fragment interface-------------------------------
     */

    /**
     * When login button is click from the login fragment we need to hit the endpoint
     * and check if it was successful.
     * @param credentials Current users login credentials
     */
    @Override
    public void onLoginAttempt(Credentials credentials) {

        //build the login web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                //              .encodedAuthority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        //build the JSONObject containing username and password
        mCredentials = credentials;
        JSONObject msg = mCredentials.asJSONObject();




        //instantiate and execute the AsyncTask.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * When clicking the reset password button in the login screen load the password reset
     * fragment.
     */
    @Override
    public void onPasswordClicked() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.start_constraint_layout, new PasswordChangeFragment(), getString(R.string.keys_fragment_password))
                .addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * When clicking the button to reset password and send new email containing new verification
     * code.
     * @param username Current user's handle
     * @param password Current user's new password
     */
    @Override
    public void onPasswordReset(String username, String password) {
        //build the reset password web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .appendPath(getString(R.string.ep_changePassword))
                .build();

        JSONObject msg = new JSONObject();
        //put username and password into json object
        try {
            msg.put("username", username);
            msg.put("password", password);
        } catch (JSONException e) {
            Log.e("StartActivity", "Reset Password problem");
        }
        //notify user and hit the reset password endpoint
        Toast.makeText(this, "Sending new email", Toast.LENGTH_SHORT).show();
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handlePasswordReset)
                .build().execute();
    }

    /**
     * When pressing the register button from the login screen just load the register fragment.
     */
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
     * Handle onPostExecute of Login task. The result from our login ep is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {

        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Login was successful.  Retrieve the memberid and check the verification
                // code coming back.
                mUserMemberIDStr = resultsJSON.getString("message");
                Log.e("MEMBERID WAS: ", mUserMemberIDStr);
                //record stay logged in choice
                checkStayLoggedIn();
                //check verification code
                int vCode = resultsJSON.getInt("code");
                //verified
                if (vCode == 1) {
                    loadVerifiedUserLandingActivity();
                } else if (vCode == 0) {
                    sendEmail();
                    LoginFragment fragment = new LoginFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.start_constraint_layout, fragment, "Login");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //frag.setError("Log in unsuccessful");
                //Unverifed user load the verfiation fragment
                } else {
                    Log.d("LoggingTest", "vCode = " + vCode);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.keys_bundle_vcode), vCode);

                    VerifyFragment fragment = new VerifyFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.start_constraint_layout, fragment, "VerifyFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
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

    /**
     * Handle onPostExecute of register task. The result from our register ep is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //save memberid
                mUserMemberIDInt = resultsJSON.getInt("memberid");
                //now we return to login and let the user know to check email
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.start_constraint_layout,
                                new LoginFragment())
                        .addToBackStack(null)
                        .commit();


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
            Log.e("JSON_PARSE_ERROR Register", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Handle onPostExecute of password reset task. The result from our password reset ep is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    public void handlePasswordReset(String result) {
        //No matter what we are returning to login from here
        LoginFragment frag =
                (LoginFragment) getSupportFragmentManager()
                        .findFragmentByTag(
                                getString(R.string.keys_fragment_login));
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //inform user to check email
                Toast.makeText(this, "Password updated.  Check your email, " +
                        "you will need a new verifictation code", Toast.LENGTH_SHORT).show();
            } else {
                //Password change was unsuccessful. Go back to login and inform the user
                frag.setError("Failed to change password, try again");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR Change Password", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.start_constraint_layout,
                        frag, getString(R.string.keys_fragment_login))
                .addToBackStack(null)
                .commit();
    }



    /*
    --------------------------Shared Preferences Handling-------------------------------
     */


    /**
     * Update stay logged in preference
     */
    private void checkStayLoggedIn() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //save the username for later usage
        prefs.edit().putString(
                getString(R.string.keys_prefs_my_memberid),
                mUserMemberIDStr)
                .apply();
        if (((CheckBox) findViewById(R.id.login_check_box_stay_logged_in)).isChecked()) {


            //save the users “want” to stay logged in
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    true)
                    .apply();
        }
    }

    /**
     * When the verify button is pressed in the verify fragment and it matches what came back
     * when the user tried to log, this will exucute.
     */
    @Override
    public void onFragmentInteraction() {
        //The user has already entered the correct code so now we just want to update the db
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .appendPath(getString(R.string.ep_verify))
                .build();

        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberIDStr);
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .build().execute();
        } catch (JSONException e) {
            Log.wtf("Verify", "Error creating JSON " + e.getMessage());
        }
        //Go to landing activity.
        loadVerifiedUserLandingActivity();
    }


}
