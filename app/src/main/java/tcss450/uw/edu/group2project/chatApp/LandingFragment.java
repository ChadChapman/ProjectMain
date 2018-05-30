package tcss450.uw.edu.group2project.chatApp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ImageButton;
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
import tcss450.uw.edu.group2project.createchat.CreateChatFragment;

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
    private TextView fahTextView;
    private TextView weatherTextView;
    private String myCity = "";
    private JSONObject myCurrInfo;
    private ProgressBar mProgressBar;
    private Weather curWeather;
    private ImageButton mNewChatButton;
    private Button mLocButton;

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
        fahTextView = v.findViewById(R.id.fah_textview);
        weatherTextView = v.findViewById(R.id.weather_textview);
        mProgressBar = v.findViewById(R.id.landing_progressbar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        curWeather = new Weather();
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
        v.findViewById(R.id.saved_loc_button).setOnClickListener(this::openLocList);
        v.findViewById(R.id.landing_map_button).setOnClickListener(this::openMap);
        v.findViewById(R.id.io_imageview).setOnClickListener(this::openURL);
        setupNewChatButton(savedInstanceState);

        return v;
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
}
