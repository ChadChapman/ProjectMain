package tcss450.uw.edu.group2project.chatApp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.List;
import java.util.Locale;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;
import tcss450.uw.edu.group2project.WeatherDisplay.Weather;
import tcss450.uw.edu.group2project.WeatherDisplay.WeatherAdapter;
import tcss450.uw.edu.group2project.createchat.CreateChatFragment;
import tcss450.uw.edu.group2project.utils.ListenManager;

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

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private TextView mLocationTextView;
    private URL myLocURL;
    private TextView celTextView;
    private TextView fahTextView;
    private TextView weatherTextView;
    private String myZip = "";
    private JSONObject myCurrInfo;
    private ProgressBar mProgressBar;

    private ImageButton mNewChatButton;

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

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mNewChatButton = v.findViewById(R.id.createNewChatFragNewChatButton);
        setupNewChatButton(savedInstanceState);

        return v;
    }

    private boolean pressedDone(TextView exampleView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {


            if (exampleView.getText().toString().length() >= 5) {
                Bundle bundle = new Bundle();
                bundle.putString("zip", exampleView.getText().toString());
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
        myZip = "";
    }
    private void setupNewChatButton(Bundle paramBundle){

        mNewChatButton.setOnClickListener(frag -> loadFragment(new CreateChatFragment()
                , getString(R.string.keys_fragment_create_new_chat)));
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.i(TAG, "Connection Connects" + myZip.isEmpty() + (mLocationRequest == null));
            myZip = "";
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
            Log.i(TAG, "Connection suspended");
            myZip = "";
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
            final String postalCode = addresses.get(0).getPostalCode();
            mLocationTextView.setText(city + ",\n" + postalCode);
            myLocURL = NetworkUtils.buildUrlForLocation(postalCode);
            Log.i(TAG, myLocURL.toString());
            if (!myZip.equals(postalCode)) {
                myZip = postalCode;
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                new WeatherAsyncTask(this::onPostGetLoc).execute(myLocURL);
            } else if (myCurrInfo != null) {
                loadWeatherInfo();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWeatherInfo() {
        try {
            weatherTextView.setText(myCurrInfo.getString("WeatherText"));
            celTextView.setText(myCurrInfo.getJSONObject("Temperature").getJSONObject("Metric").getString("Value"));
            fahTextView.setText(myCurrInfo.getJSONObject("Temperature").getJSONObject("Imperial").getString("Value"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onPostSearchLoc(String result) {
        Log.e(TAG, result);
        if (result != null && !result.equals("")) {
            try {
                myCurrInfo = new JSONArray(result).getJSONObject(0);
                loadWeatherInfo();
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            } catch (JSONException e) {
                Log.e(TAG, "ERR" + e.getMessage());
            }

        }
    }

    private void onPostGetLoc(String result) {
        if (result != null && !result.equals("")) {
            Log.e(TAG, "POST");
            try {
                JSONArray rootObject = new JSONArray(result);
                JSONObject thisArr = rootObject.getJSONObject(0);
                String locKey = thisArr.getString("Key");
                URL currCondURL = NetworkUtils.buildUrlForCurr(locKey);
                new WeatherAsyncTask(this::onPostSearchLoc).execute(currCondURL);
            } catch (JSONException e) {
                Log.e(TAG, "ERR" + e.getMessage());
            }

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
}
