package tcss450.uw.edu.group2project.chatApp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;
import tcss450.uw.edu.group2project.utils.WeatherAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayConditionsFragment extends Fragment {
    private String zip;
    private TextView weather;
    private TextView location;
    private TextView celsius;
    private TextView fahrenheit;
    private ProgressBar mProgressBar;
    public DisplayConditionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_conditions, container, false);
        zip = getArguments().getString("zip");
        weather = v.findViewById(R.id.WeatherText_textView);
        location = v.findViewById(R.id.locDisp_textview);
        celsius = v.findViewById(R.id.celcius_textView);
        fahrenheit = v.findViewById(R.id.fahrenheit_textView);
        mProgressBar = v.findViewById(R.id.display_progressbar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        //new WeatherAsyncTask(this::onPostGetLoc).execute(NetworkUtils.buildUrlForLocation(zip));
        return v;
    }
    private void onPostGetLoc(String result) {
        if(result != null && !result.equals("")){
            try{
                JSONArray rootObject = new JSONArray(result);
                JSONObject thisArr = rootObject.getJSONObject(0);
                String locKey = thisArr.getString("Key");
                URL currCondURL = NetworkUtils.buildUrlForCurr(locKey);
                String city = thisArr.getString("LocalizedName");
                String state = thisArr.getJSONObject("AdministrativeArea").getString("ID");
                location.setText(city + ","+state+"\n"+zip);
                new WeatherAsyncTask(this::onPostSearchLoc).execute(currCondURL);
            } catch (JSONException e) {
                Log.e("", "ERR"+e.getMessage()  );
            }

        }
    }
    private void onPostSearchLoc(String result) {
        if(result != null && !result.equals("")){
            try{
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                JSONObject rootObject = new JSONArray(result).getJSONObject(0);
                weather.setText(rootObject.getString("WeatherText"));
                celsius.setText(rootObject.getJSONObject("Temperature").getJSONObject("Metric").getString("Value"));
                fahrenheit.setText(rootObject.getJSONObject("Temperature").getJSONObject("Imperial").getString("Value"));

            } catch (JSONException e) {
                Log.e("", "ERR"+e.getMessage()  );
            }

        }
    }




}
