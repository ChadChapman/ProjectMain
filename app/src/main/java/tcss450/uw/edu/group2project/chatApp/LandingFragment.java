package tcss450.uw.edu.group2project.chatApp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;
import tcss450.uw.edu.group2project.WeatherDisplay.Weather;
import tcss450.uw.edu.group2project.createchat.CreateChatFragment;
import tcss450.uw.edu.group2project.utils.ChatFirebaseInstanceIDService;
import tcss450.uw.edu.group2project.utils.ListenManager;

import tcss450.uw.edu.group2project.model.MessageFeedItem;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;
import tcss450.uw.edu.group2project.utils.WeatherAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class LandingFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "MyLocationsActivity";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<MessageFeedItem> messageFeedItemList;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private TextView mLocationTextView;
    private URL myLocURL;
    private TextView celTextView;
    private TextView fahTextView;
    private TextView weatherTextView;
    private String myCity = "";
    private JSONObject myCurrInfo;
    private ProgressBar mProgressBar;
    private ChatFirebaseInstanceIDService fbID;
    private Weather curWeather;
    private ImageButton mNewChatButton;
    private Button mLocButton;
    private Uri mContactsUri;
    private String mUserMemberID;

    public LandingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_landing, container, false);
        //Search for zip
        EditText search = v.findViewById(R.id.search_zip_textview);
        search.setOnEditorActionListener(this::pressedDone);
        mLocationTextView = v.findViewById(R.id.location_textview);
        celTextView = v.findViewById(R.id.cel_textview);
        fahTextView = v.findViewById(R.id.fah_textview);
        weatherTextView = v.findViewById(R.id.weather_textview);
        mProgressBar = v.findViewById(R.id.landing_progressbar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        curWeather = new Weather();
        mRecyclerView = v.findViewById(R.id.landing_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationRequest = LocationRequest.create();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        mUserMemberID = prefs.getString(getString(R.string.keys_prefs_my_memberid), "MEMBERID NOT FOUND IN PREFS");
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mNewChatButton = v.findViewById(R.id.createNewChatFragNewChatButton);
        v.findViewById(R.id.saved_loc_button).setOnClickListener(this::openLocList);
        v.findViewById(R.id.landing_map_button).setOnClickListener(this::openMap);
        v.findViewById(R.id.io_imageview).setOnClickListener(this::openURL);
        setupNewChatButton(savedInstanceState);
        loadMessages();
        return v;
    }
    /**
     * For loading all messages
     */
    public void loadMessages() {
        JSONObject jsonObject = createMessagesRequestObject();
        mContactsUri = buildHerokuAddress(getString(R.string.ep_get_recent_chat));
        //now json obj is built, time ot send it off
        new SendPostAsyncTask.Builder(mContactsUri.toString(), jsonObject)
                .onPostExecute(this::handleMessageQueryResponseOnPostExec)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * For building a url address.
     */
    public Uri buildHerokuAddress(String ep) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chat))
                .appendPath(ep)
                .build();
        return uri;
    }
    //-----------------------------------------------------------------------------------------
    //--------------------------------------Handlers-------------------------------------------
    //-----------------------------------------------------------------------------------------
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }
    public void handleMessageQueryResponseOnPostExec(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Query was successful

                //need to populate the contacts list before passing it to the adapter
                parseHerokuResult(result);
                //added from here
                adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.chats_list_rows, null);
                        return new CustomViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, int i) {
                        //FeedItem feedItem = feedItemList.get(i);
                        MessageFeedItem feedItem = messageFeedItemList.get(i);


                        //Setting text view title
                        ((CustomViewHolder) customViewHolder).chatName.setText(feedItem.getChatName());
                        ((CustomViewHolder) customViewHolder).message.setText(feedItem.getMessage());

                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onMsgItemClick(feedItem);
                            }
                        };
                        ((CustomViewHolder) customViewHolder).chatName.setOnClickListener(listener);
                        ((CustomViewHolder) customViewHolder).message.setOnClickListener(listener);
                    }
                    @Override
                    public int getItemCount() {
                        return (null != messageFeedItemList ? messageFeedItemList.size() : 0);
                    }
                };
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }//to here from tut GH
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void onMsgItemClick(MessageFeedItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("chatID", item.getChatid());
        bundle.putString("chatName",item.getChatName() );
        Fragment chats = new ChatFragment();
        chats.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, chats, getString(R.string.keys_fragment_chat))
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }
    /**
     * Heroku parser
     */
    private void parseHerokuResult(String result) {
        try {
            Log.e(",c", result);
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray(getString(R.string.contacts));
            messageFeedItemList = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                MessageFeedItem item = new MessageFeedItem();
                item.setChatName(post.optString("name"));
                item.setChatid(post.optString(getString(R.string.chatid)));
                item.setMessage(post.optString(getString(R.string.message)));
                messageFeedItemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return mContactFeedItemList;
    }
    /**
     * For message JSonObj. (need args)
     */
    public JSONObject createMessagesRequestObject() {
        JSONObject msg = new JSONObject();
        try {
            //Todo need args for json
            msg.put("memberid", mUserMemberID);
        } catch (JSONException e) {
            Log.wtf("CONTACTS VERIFIED ALL", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }
    private void openURL(View view) {
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.weatherbit.io/"));
        startActivity(browserIntent);
    }

    private void openMap(View view) {
        loadFragment(new DisplayConditionsFragment(),"DisplayConditionsFragment");
    }

    private void openLocList(View view) {
        loadFragment(new WeatherLocationList(),"WeatherLocationListFrag");
    }

    private boolean pressedDone(TextView exampleView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {


            if (exampleView.getText().toString().length() == 5) {
                Weather cond = new Weather();
                Geocoder geo = new Geocoder(getActivity());
                try {

                    Address addresses = geo.getFromLocationName(exampleView.getText().toString(), 1).get(0);
                    cond.setZip(addresses.getPostalCode());
                    cond.setLat(addresses.getLatitude());
                    cond.setLon(addresses.getLongitude());
                    cond.setCity(addresses.getLocality());
                } catch (IndexOutOfBoundsException e) {
                    //TODO do some error message on zipcode search edittext
                    Log.e("ZIPSS","Wrong zip");
                    return  false;
                } catch (IOException e) {
                    Log.e("ZIP","Wrong zip");
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("weather", cond);
                Fragment disp = new DisplayConditionsFragment();
                disp.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, disp, getString(R.string.keys_fragment_condition))
                        .addToBackStack(null);
                // Commit the transaction
                transaction.commit();

            } else {
                Log.e("fail", "fail" + exampleView.getText().toString());
            }
        }
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            Log.i(TAG, "Connection suspended");
        mGoogleApiClient.disconnect();
        myCity = "";
    }

    private void setupNewChatButton(Bundle paramBundle) {

        fbID = new ChatFirebaseInstanceIDService();
        fbID.onTokenRefresh();
        mNewChatButton.setOnClickListener(frag -> loadFragment(new CreateChatFragment()
                , getString(R.string.keys_fragment_create_new_chat)));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Connection Connects" + myCity.isEmpty() + (mLocationRequest == null));
            myCity = "";
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            Log.i(TAG, "Connection suspended");
            myCity = "";
        }
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        if (mCurrentLocation == null) {
            Log.i(TAG, "connected");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
                    Log.i(TAG, mCurrentLocation.toString());
                }
                Log.i(TAG, "start loc");

            }
        }
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.i(TAG, mCurrentLocation.toString());
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            myLocURL = NetworkUtils.buildUrlForCurr(city);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            Log.e("address",addresses.get(0).toString());
            if (!myCity.equals(city)) {
                myCity = city;
                new WeatherAsyncTask(this::onPostSearchLoc).execute(myLocURL);
            } else{
                loadWeatherInfo();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWeatherInfo() {
        weatherTextView.setText(curWeather.getCurrWeather());
        fahTextView.setText(curWeather.getFarTemp());
        mLocationTextView.setText(curWeather.getCityState());
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void onPostSearchLoc(String result) {
        if (result != null && !result.equals("")) {
            try {
                JSONArray temp = new JSONObject(result).getJSONArray("data");
                parseResulte(temp.getJSONObject(0));
                loadWeatherInfo();
            } catch (JSONException e) {
                Log.e(TAG, "ERR" + e.getMessage());
            }

        }
    }

    private void parseResulte(JSONObject result) {
        try {
            curWeather.setCityState(result.getString("city_name") + "," + result.getString("state_code"));
            curWeather.setFarTemp(result.getString("temp"));
            curWeather.setCurrWeather(result.getJSONObject("weather").getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void loadFragment(Fragment frag, String tag) {
        Log.e("MADE IT TO lOADFRAGMENT() IN LANDING FRAG", tag);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView chatName;
        protected TextView message;
        protected CardView cView;

        public CustomViewHolder(View view) {
            super(view);
            this.chatName = (TextView) view.findViewById(R.id.chat_username_textview);
            this.message = (TextView) view.findViewById(R.id.chat_message_textview);
            this.cView = (CardView) view.findViewById(R.id.list_card_view);
            //this.cView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
