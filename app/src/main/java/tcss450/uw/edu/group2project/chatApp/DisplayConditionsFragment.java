package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;
import tcss450.uw.edu.group2project.WeatherDisplay.Weather;
import tcss450.uw.edu.group2project.utils.GetPostAsyncTask;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;
import tcss450.uw.edu.group2project.utils.WeatherAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayConditionsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private Marker mMarker;
    private Weather curWeather;
    private TextView location;
    private TextView fahrenheit;
    private ProgressBar mProgressBar;
    private TextView mWeatherDesc;
    private Button mAdd;
    private Button mDel;
    private String mUserMemberID;

    public DisplayConditionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_display_conditions, container, false);
        mWeatherDesc = mView.findViewById(R.id.WeatherText_textView);
        location = mView.findViewById(R.id.locDisp_textview);
        fahrenheit = mView.findViewById(R.id.fahrenheit_textView);
        mProgressBar = mView.findViewById(R.id.display_progressbar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mAdd = mView.findViewById(R.id.add_loc_button);
        mAdd.setOnClickListener(this::addLoc);
        mDel = mView.findViewById(R.id.delete_loc_button);
        mDel.setOnClickListener(this::delLoc);
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        mUserMemberID = prefs.getString(getString(R.string.keys_prefs_my_memberid), "MEMBERID NOT FOUND IN PREFS");
        if (getArguments() != null) {
            curWeather = (Weather) getArguments().getSerializable("weather");
            seeIfLocIsSaved();
            new WeatherAsyncTask(this::onPostSearchLoc).execute(NetworkUtils.buildUrlForCurr(curWeather.getCity()));
        }

        return mView;
    }

    private void delLoc(View view) {
        sendTask(buildHerokuAddress("deleteLoc").toString());
    }

    private void addLoc(View view) {
        sendTask(buildHerokuAddress("addLoc").toString());
    }

    private void sendTask(String url) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid", mUserMemberID);
            msg.put("city", curWeather.getCity());
            msg.put("lat", curWeather.getLat());
            msg.put("long", curWeather.getLon());
        } catch (JSONException e) {
            Log.e("Location", e.toString());
        }
        new SendPostAsyncTask.Builder(url, msg).
                onPostExecute(this::redrawButtons)
                .build()
                .execute();
    }

    private void redrawButtons(String s) {
        seeIfLocIsSaved();
    }


    /**
     * For building a url address.
     */
    public Uri buildHerokuAddress(String ep) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_locations))
                .appendPath(ep)
                .build();
        return uri;
    }

    private void seeIfLocIsSaved() {
        String myMsg = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_locations))
                .appendPath("savedLoc")
                .appendQueryParameter("memberid", mUserMemberID)
                .appendQueryParameter("long", String.valueOf(curWeather.getLon()))
                .appendQueryParameter("lat", String.valueOf(curWeather.getLat()))
                .appendQueryParameter("city", curWeather.getCity())
                .build()
                .toString();

        JSONObject messageJson = new JSONObject();
        new GetPostAsyncTask.Builder(myMsg, messageJson)
                .onPostExecute(this::getLoc)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void getLoc(String result) {
        try {
            Log.e("result", String.valueOf(new JSONObject(result).getBoolean("success")));
            if (new JSONObject(result).getBoolean("success")) {
                mDel.setVisibility(Button.VISIBLE);
                mAdd.setVisibility(Button.GONE);
            } else {
                mAdd.setVisibility(Button.VISIBLE);
                mDel.setVisibility(Button.GONE);
            }
        } catch (JSONException e) {
            Log.e("result", e.toString());
        }
    }


    private void onPostSearchLoc(String result) {
        if (result != null && !result.equals("")) {
            try {
                JSONArray temp = new JSONObject(result).getJSONArray("data");
                parseResult(temp.getJSONObject(0));
                loadWeatherInfo();
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            } catch (JSONException e) {
                Log.e("", "ERR" + e.getMessage());
            }

        }
    }

    private void loadWeatherInfo() {
        location.setText(curWeather.getCityState());
        mWeatherDesc.setText(curWeather.getCurrWeather());
        fahrenheit.setText(curWeather.getFarTemp());
    }

    private void parseResult(JSONObject result) {
        try {
            curWeather.setCityState(result.getString("city_name") + "," + result.getString("state_code"));
            curWeather.setFarTemp(result.getString("temp"));
            curWeather.setCurrWeather(result.getJSONObject("weather").getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map_fragment);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnMapLongClickListener(this);
        if (curWeather != null) {
            mMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(curWeather.getLat(), curWeather.getLon())).title(curWeather.getCity()).snippet(curWeather.getState()));
            CameraPosition here = CameraPosition.builder().target(new LatLng(curWeather.getLat(), curWeather.getLon())).zoom(15).bearing(0).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(here));
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (curWeather == null)
            curWeather = new Weather();


        Geocoder geo = new Geocoder(getActivity());
        try {
            Address addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
            curWeather.setLat(latLng.latitude);
            curWeather.setLon(latLng.longitude);
            curWeather.setCity(addresses.getLocality());
            if (mMarker == null)
                mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(curWeather.getLat(), curWeather.getLon())).title(curWeather.getCity()).snippet(curWeather.getState()));
            mMarker.setPosition(latLng);
            CameraPosition here = CameraPosition.builder().target(latLng).zoom(15).bearing(0).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(here));
            seeIfLocIsSaved();
            new WeatherAsyncTask(this::onPostSearchLoc).execute(NetworkUtils.buildUrlForCurr(curWeather.getCity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("clicked", "long Clicked");
    }
}
