package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;
import tcss450.uw.edu.group2project.WeatherDisplay.Weather;
import tcss450.uw.edu.group2project.utils.GetPostAsyncTask;
import tcss450.uw.edu.group2project.utils.SendPostAsyncTask;
import tcss450.uw.edu.group2project.utils.WeatherAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayConditionsFragment extends Fragment {
    private String zip;
    private Weather curWeather;
    private TextView location;
    private TextView fahrenheit;
    private ProgressBar mProgressBar;
    private String city;
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
        View v = inflater.inflate(R.layout.fragment_display_conditions, container, false);
        curWeather = (Weather) getArguments().getSerializable("weather");
        mWeatherDesc = v.findViewById(R.id.WeatherText_textView);
        location = v.findViewById(R.id.locDisp_textview);
        fahrenheit = v.findViewById(R.id.fahrenheit_textView);
        mProgressBar = v.findViewById(R.id.display_progressbar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mAdd = v.findViewById(R.id.add_loc_button);
        mAdd.setOnClickListener(this::addLoc);
        mDel = v.findViewById(R.id.delete_loc_button);
        mDel.setOnClickListener(this::delLoc);
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        mUserMemberID = prefs.getString(getString(R.string.keys_prefs_my_memberid), "MEMBERID NOT FOUND IN PREFS");

        seeIfLocIsSaved();
        city = curWeather.getCity();
        new WeatherAsyncTask(this::onPostSearchLoc).execute(NetworkUtils.buildUrlForCurr(city));
        return v;
    }

    private void delLoc(View view) {
        sendTask(buildHerokuAddress("deleteLoc").toString());
    }
    private void addLoc(View view) {
        sendTask(buildHerokuAddress("addLoc").toString());
    }
    private void sendTask(String url){
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid",mUserMemberID);
            msg.put("city",curWeather.getCity());
            msg.put("lat",curWeather.getLat());
            msg.put("long",curWeather.getLon());
        } catch (JSONException e) {
            Log.e("Location", e.toString());
        }
        new SendPostAsyncTask.Builder(url,msg).
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
                .appendQueryParameter("lat",String.valueOf(curWeather.getLat()))
                .appendQueryParameter("city",curWeather.getCity())
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
            if(new JSONObject(result).getBoolean("success")){
                mDel.setVisibility(Button.VISIBLE);
                mAdd.setVisibility(Button.GONE);
            }else{
                mAdd.setVisibility(Button.VISIBLE);
                mDel.setVisibility(Button.GONE);
            }
        } catch (JSONException e) {
            Log.e("result",e.toString());
        }
    }


    private void onPostSearchLoc(String result) {
        if (result != null && !result.equals("")) {
            try {
                JSONArray temp = new JSONObject(result).getJSONArray("data");
                parseResulte(temp.getJSONObject(0));
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

    private void parseResulte(JSONObject result) {
        try {
            curWeather.setCityState(result.getString("city_name") + "," + result.getString("state_code"));
            curWeather.setFarTemp(result.getString("temp"));
            curWeather.setCurrWeather(result.getJSONObject("weather").getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

}
