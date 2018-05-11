package tcss450.uw.edu.group2project.chatApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;
import tcss450.uw.edu.group2project.model.Credentials;
import tcss450.uw.edu.group2project.registerLoging.LoginFragment;
import tcss450.uw.edu.group2project.registerLoging.RegisterFragment;
import tcss450.uw.edu.group2project.registerLoging.StartActivity;
//import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LandingFragment.OnLandingFragmentInteractionListener {

    private static SQLiteDatabase mAppDB;
    private String mUserMemberID;
    //private int mUserMemberIDInt;
    private ArrayList<ChatContact> mChatContactsArrList;
    //private String mUsername;

    Bundle mContactsBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (extras != null){
            mUserMemberID = extras.getString("userMemberID");
        }
        //let's just make an sqlite db and be done with it
        //mAppDB = openOrCreateDatabase("rabbitChatDB", MODE_PRIVATE, null);
//        setupDeviceDatabase();



       }

       @Override
       public void onStart(){
        super.onStart();
           //grab the memberid from the intent that got us here
           //this may need to go into onCreate?

            //use memberid to update contacts on device
          // asyncContactsDBQuery(mUserMemberID);
           //create an array to pass to contacts activity to populate it
//           mContactsBundle = createChatContactsBundle(mUserMemberID);

       }

//}

    private void loadFragment(Fragment frag,String tag){
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            loadFragment(new ChatFragment(),getString(R.string.keys_fragment_chat));
        } else if (id == R.id.nav_contacts) { //switch to Contacts Activity
            //loadFragment(new ContactFragment(),getString(R.string.keys_fragment_contacts)); original
            //quickest thing for the contacts activity to use will be an array?
            //use method to get contacts from device?
            //need to have a separate action here that updates the contacts on the device with the contacts from the backend, should run in oncreate
            //mUserMemberIDInt = getMemberIDFromUsername(mUsername);
            loadContactsActivity();
        } else if (id == R.id.nav_profile) {
            loadFragment(new ProfileFragment(),getString(R.string.keys_fragment_profile));
        } else if (id == R.id.nav_settings) {
            loadFragment(new SettingFragment(),getString(R.string.keys_fragment_settings));
        }else if (id == R.id.nav_logout) {
            onLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadContactsActivity(){
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.putExtra("mUserMemberID", mUserMemberID);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);
    }

    @Override
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

    public void setupDeviceDatabase() {

//        mAppDB = openOrCreateDatabase("RabbitChatDB", MODE_PRIVATE, null);
//        mAppDB.beginTransaction();
//        mAppDB.execSQL(getString(R.string.sqlite_create_table_ChatContact));
//        //add whatever other tables need to be setup here
//        mAppDB.endTransaction();

    }

    private void handleChatContactsAsyncQueryResult(String result) {

        //parameter string is from async call to web service, should have most up to date contacts
        try {
            JSONObject resultsJSON = new JSONObject(result);
            //make sure to include success field in returned object
            Boolean success = resultsJSON.getBoolean("success");
            Boolean contactsChanged = resultsJSON.getBoolean("changed");
            Log.e("QUERY SUCCESS ON CONTACTS FROM HEROKU DB: ", success.toString());
            Log.e("CHANGES TO CONTACTS FROM HEROKU DB: ", contactsChanged.toString());
            if (success && contactsChanged) {
                //query was successful and there are changes to update db with

            } else { //Query was unsuccessful or there was no changes to update with
                if (!success) {
                    Toast.makeText(ChatActivity.this, "Failed to fetch new chat contacts data!"
                            , Toast.LENGTH_LONG).show();
                }
                if (!contactsChanged) {
                    Toast.makeText(ChatActivity.this, "No new chat contacts updates!"
                            , Toast.LENGTH_LONG).show();
                }
            }

        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
        //TODO now need to parse result and get it into the db
        //compare to contacts on device?  or write if does not exist?
        mAppDB.beginTransaction();
        //get a cursor object from the query
        //query for all records in ChatContact table
        mAppDB.endTransaction();
        //compare and update db if needed
    }

//    public void asyncContactsDBQuery(String memberid) {
//        //build the web service URL
//        Uri uri = new Uri.Builder()
//                .scheme("https")
//                .appendPath(getString(R.string.ep_base_url))
//                .appendPath(getString(R.string.ep_contacts))
//                .appendPath(getString(R.string.ep_contacts_query_ny_memberid))
//                .build();
//
//        //build the JSONObject
//        JSONObject msg = new JSONObject();
//        try {
//             msg.put("memberid", memberid);
//            } catch (JSONException e) {
//                Log.wtf("CONTACTS QUERY BY MEMBERID", "Error creating JSON: " + e.getMessage());
//            }
//
//        //instantiate and execute the AsyncTask.
//        //Feel free to add a handler for onPreExecution so that a progress bar
//        //is displayed or maybe disable buttons. You would need a method in
//        //LoginFragment to perform this.
//        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                .onPostExecute(this::handleChatContactsAsyncQueryResult)
//                .onCancelled(this::handleErrorsInTask)
//                .build().execute();
//    }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    private Bundle createChatContactsBundle(String mUserMemberID) {
        String[] columnsToMatch = {mUserMemberID};
        CancellationSignal cancellationSignal = new CancellationSignal();
        //what to do with cancellation signal?
        //get a cursor from the db
//        mAppDB.beginTransaction();
        //SQLiteCursor cursor = mAppDB.query("ChatContact", columnsToReturn, );
        Cursor cursor = mAppDB.rawQuery(
                "SELECT Username, FName, LName, created_at, last_modified, verified, " +
                        "image_link, display_color " +
                    "FROM ChatContact " +
                    "WHERE memberid = ?"
                        , columnsToMatch
                        , cancellationSignal);
        mAppDB.endTransaction();
        int columnCount = cursor.getColumnCount();
        int cursorCount = cursor.getCount();
        Bundle contactsBundle = new Bundle(cursorCount);

        if (cursorCount > 1) {
            //create a new bundle to add the query info to
            contactsBundle = new Bundle(columnCount);
            //now iterate with cursor and create a new chat contact from it
             Integer rowsCounter = 1;
             ChatContact queryContact;
             for (cursor.isFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                 ChatContact.Builder queryContactBuilder =
                         new ChatContact.Builder(cursor.getString(0)
                         , cursor.getString(1)
                         , cursor.getString(2)
                         , cursor.getString(3)
                         , cursor.getString(4)
                         , cursor.getString(5));

                       if (!cursor.getString(6).equals("none")) {
                           queryContactBuilder.addImageLink(cursor.getString(6));
                       }
                       if (!cursor.getString(7).equals("none")) {
                           queryContactBuilder.addColor(cursor.getString(7));
                       }
                       queryContact = queryContactBuilder.build();
                       contactsBundle.putSerializable("contactNo:" + rowsCounter, queryContact);
                       rowsCounter++;
             }
        }
        return contactsBundle;
    }


    //should make one database to pass around, but only if we have to
    public static SQLiteDatabase getmAppDB() {
        return mAppDB;
    }
}
