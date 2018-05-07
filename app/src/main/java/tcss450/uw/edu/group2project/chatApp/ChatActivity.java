package tcss450.uw.edu.group2project.chatApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.RadioButton;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.registerLoging.LoginFragment;
import tcss450.uw.edu.group2project.registerLoging.RegisterFragment;
import tcss450.uw.edu.group2project.registerLoging.StartActivity;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LandingFragment.OnLandingFragmentInteractionListener,
        SettingFragment.OnSettingFragmentInteractionListener {
    private static SQLiteDatabase mAppDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setAppTheme();
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
        //let's just make an sqlite db and be done with it
        mAppDB = openOrCreateDatabase("rabbitChatDB", MODE_PRIVATE, null);
//        mRecyclerView = (RecyclerView) findViewById(R.id.ChatContactRecyclerView);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)
//        mAdapter = new MyChatContactsAdapter(myDataset);
//        mRecyclerView.setAdapter(mAdapter);
    }

//}

    private void setAppTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.keys_shared_setting_prefs), Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            if (sharedPreferences.getBoolean("radio_button_1", true)) {
                setTheme(R.style.AppTheme);
            } else if (sharedPreferences.getBoolean("radio_button_2", false)) {
                setTheme(R.style.AppTheme2);
            } else if (sharedPreferences.getBoolean("radio_button_3", false)) {
                setTheme(R.style.AppTheme3);
            } else if (sharedPreferences.getBoolean("radio_button_4", false)) {
                setTheme(R.style.AppTheme4);
            }
        }
    }

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

    @Override
    public void onSettingSaveButtonClicked(View view) {
        setAppTheme();

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        //let's just make an sqlite db and be done with it
        mAppDB = openOrCreateDatabase("rabbitChatDB", MODE_PRIVATE, null);
    }

    @Override
    public void onSettingCancelButtonClicked() {
        loadFragment(new LandingFragment(), "landingFragmentTag");
    }

    //should make one database to pass around
    public static SQLiteDatabase getmAppDB() {
        return mAppDB;
    }
}
