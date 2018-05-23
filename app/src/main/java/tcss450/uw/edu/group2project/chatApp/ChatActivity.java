package tcss450.uw.edu.group2project.chatApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;
import tcss450.uw.edu.group2project.registerLoging.StartActivity;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;
import tcss450.uw.edu.group2project.utils.UITextSize;
import tcss450.uw.edu.group2project.utils.UITheme;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchFragment.OnSearchFragmentInteractionListener,
        SettingFragment.OnSettingFragmentInteractionListener {
    private static SQLiteDatabase mAppDB;
    private String mUserMemberID;
    //private int mUserMemberIDInt;
    private ArrayList<ChatContact> mChatContactsArrList;
    //private String mUsername;

    Bundle mContactsBundle;

    public static int mTheme = UITheme.THEME_ONE;
    public static int mTextSize = UITextSize.SIZE_MEDIUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Update theme color
        setTheme(UITheme.getThemeId(mTheme));

        // Update text size
        setTheme(UITextSize.getSizeId(mTextSize));

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //uncomment when we decide what to do with the floating action button
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer,
                        new LandingFragment(),
                        getString(R.string.keys_fragment_landing))
                .commit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserMemberID = extras.getString("userMemberID");
        }
        //let's just make an sqlite db and be done with it
        //mAppDB = openOrCreateDatabase("rabbitChatDB", MODE_PRIVATE, null);
//        setupDeviceDatabase();


    }

    @Override
    public void onStart() {
        super.onStart();
        //grab the memberid from the intent that got us here
        //this may need to go into onCreate?

        //use memberid to update contacts on device
        // asyncContactsDBQuery(mUserMemberID);
        //create an array to pass to contacts activity to populate it
//           mContactsBundle = createChatContactsBundle(mUserMemberID);

    }

//}

    private void loadFragment(Fragment frag, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadFragment(new SettingFragment(), getString(R.string.keys_fragment_settings));
        }else if(id == R.id.action_logout){
            onLogout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            loadFragment(new ChatListFragment(mUserMemberID), getString(R.string.keys_fragment_chat_list));
        } else if (id == R.id.nav_contacts) { //switch to Contacts Activity
            //loadFragment(new ContactFragment(),getString(R.string.keys_fragment_contacts)); original
            //loadContactsActivity();
            loadFragment(new TryContactFragment(mUserMemberID), getString(R.string.keys_fragment_contacts));
        } else if (id == R.id.nav_profile) {
            loadFragment(new ProfileFragment(), getString(R.string.keys_fragment_profile));
            loadInfo();
        } else if (id == R.id.nav_search) {
            loadFragment(new SearchFragment(), getString(R.string.keys_fragment_search));
        } else if (id == R.id.nav_settings) {
            loadFragment(new SettingFragment(), getString(R.string.keys_fragment_settings));
        } else if (id == R.id.nav_logout) {
            onLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onLogout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_prefs_username));

        prefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_logged_in),
                false)
                .apply();
        Intent intent = new Intent(this, StartActivity.class);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);
    }


    //should make one database to pass around
    public static SQLiteDatabase getmAppDB() {
        return mAppDB;
    }

    @Override
    public void onSettingThemeButtonClicked(int color) {
        switch (color) {
            case 1:
                changeTheme(UITheme.THEME_ONE);
                break;
            case 2:
                changeTheme(UITheme.THEME_TWO);
                break;
            case 3:
                changeTheme(UITheme.THEME_THREE);
                break;
            case 4:
                changeTheme(UITheme.THEME_FOUR);
                break;
        }
    }

    public void changeTheme(final int theme) {
        // Handles theme changes to activity
        mTheme = theme;
        setTheme(mTheme);

        ChatActivity.this.recreate();

        int duration = Toast.LENGTH_SHORT;
        Context context = this.getBaseContext();
        Toast toast = Toast.makeText(context, "Changed to Theme " + theme, duration);
        toast.show();
    }

    @Override
    public void onSettingTextSizeButtonClicked(int size) {
        switch (size) {
            case 1:
                changeTextSize(UITextSize.SIZE_SMALL);
                break;
            case 2:
                changeTextSize(UITextSize.SIZE_MEDIUM);
                break;
            case 3:
                changeTextSize(UITextSize.SIZE_LARGE);
                break;
        }
    }

    public void changeTextSize(final int size) {
        // Handles theme changes to activity
        mTextSize = size;
        setTheme(mTextSize);

        ChatActivity.this.recreate();

        int duration = Toast.LENGTH_SHORT;
        Context context = this.getBaseContext();
        Toast toast = Toast.makeText(context, "Changed to Size " + size, duration);
        toast.show();
    }

    // Handle searches
    @Override
    public void onSearchByEmailButtonClicked(String email) {
        Log.e("ChatActivity", "Search by email");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject emailJSON = new JSONObject();

        try {
            emailJSON.put(getString(R.string.keys_json_email), email);
            Log.e("ChatActivity", "Put email to json" );
        } catch (JSONException theException) {
            Log.e("ChatActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), emailJSON)
                .onPostExecute(this::handleSearchResult).build().execute();
    }

    @Override
    public void onSearchByUsernameButtonClicked(String username) {
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject usernameJSON = new JSONObject();
        Log.e("ChatActivity", username);
        try {
            usernameJSON.put(getString(R.string.keys_json_username), username);
            Log.e("ChatActivity", "Put usernamer to json" );
        } catch (JSONException theException) {
            Log.e("ChatActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), usernameJSON)
                .onPostExecute(this::handleSearchResult).build().execute();
    }

    @Override
    public void onSearchByNameButtonClicked(String firstname, String lastname) {
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject nameJSON = new JSONObject();

        try {
            nameJSON.put(getString(R.string.keys_json_firstname), firstname);
            nameJSON.put(getString(R.string.keys_json_lastname), lastname);
        } catch (JSONException theException) {
            Log.e("ChatActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), nameJSON)
                .onPostExecute(this::handleSearchResult).build().execute();
    }

    //load the profile info
    private void loadInfo() {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getinfo))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("ID", mUserMemberID);
        } catch (JSONException e) {
            Log.wtf("username", "Error creating JSON: " + e.getMessage());
        }


        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleOnGetInfoPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleSearchResult(String result) {

        try {
            JSONObject responseJSON = new JSONObject(result);
            boolean success = responseJSON.getBoolean(getString(R.string.keys_json_success));
            TextView resultFirstName = findViewById(R.id.search_text_view_result_first_name);
            TextView resultLastName = findViewById(R.id.search_text_view_result_last_name);

            if (success) {
                resultFirstName.setText(responseJSON.getString("firstname"));
                resultLastName.setText(responseJSON.getString("lastname"));

            } else {
                Log.e("ChatActivity", "User not found");
            }
        } catch (JSONException e) {
            Log.e("ChatActivity", "JSON parse error" + e.getMessage());
        }
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
    private void handleOnGetInfoPost(String result) {
        try {
            Log.e("",result);
            JSONObject msg = new JSONObject(result);
            ((TextView) findViewById(R.id.profile_text_view_username)).setText(msg.getString("username"));
            ((TextView) findViewById(R.id.profile_text_view_firstname)).setText(msg.getString("firstname"));
            ((TextView) findViewById(R.id.profile_text_view_lastname)).setText(msg.getString("lastname"));
            ((TextView) findViewById(R.id.profile_text_view_email)).setText(msg.getString("email"));
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }
}
