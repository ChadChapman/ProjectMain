package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;
import tcss450.uw.edu.group2project.WeatherDisplay.Weather;
import tcss450.uw.edu.group2project.WeatherDisplay.WeatherAdapter;
import tcss450.uw.edu.group2project.createchat.CreateChatFragment;
import tcss450.uw.edu.group2project.utils.ListenManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class LandingFragment extends Fragment {

    private OnLandingFragmentInteractionListener mListener;
    private TextView weatherView;
    private Uri retrieve;

    private static final String TAG = LandingFragment.class.getSimpleName();
    private ArrayList<Weather> weatherArrayList = new ArrayList<>();

    private ListView listView;

    private ListenManager mListenManager;

    private ImageButton mNewChatButton;

    public LandingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_landing, container, false);
        weatherView = v.findViewById(R.id.weatherTextView);

        listView = v.findViewById(R.id.idListView);

        URL weatherUrl = NetworkUtils.buildUrlForWeather();
        new FetchWeatherDetails().execute(weatherUrl);
       Log.i(TAG, "onCreate: weatherURL: " + weatherUrl);
        mNewChatButton = v.findViewById(R.id.createNewChatFragNewChatButton);
        setupNewChatButton(savedInstanceState);
        return v;
    }

    private void setupNewChatButton(Bundle paramBundle){

        mNewChatButton.setOnClickListener(frag -> loadFragment(new CreateChatFragment()
                , getString(R.string.keys_fragment_create_new_chat)));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void handleError(Exception e) {
        Log.e("LISTEN ERROR", e.getMessage());
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLandingFragmentInteractionListener {
        void onLogout();
    }

    private class FetchWeatherDetails extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(URL... urls) {
            URL weatherUrl = urls[0];
            String weatherSearchResults = null;
            try{
                weatherSearchResults = NetworkUtils.getResponseFromHttpUrl(weatherUrl);
            }catch(IOException e){
                e.printStackTrace();
            }
            Log.i(TAG, "doInBackground: weatherSearchResults: " + weatherSearchResults );
            return weatherSearchResults;
        }
        @Override
        protected void onPostExecute(String weatherSearchResults){
            if(weatherSearchResults != null && !weatherSearchResults.equals("")){
                weatherArrayList = parseJSON(weatherSearchResults);

            }
            super.onPostExecute(weatherSearchResults);
        }

    }
    private ArrayList<Weather> parseJSON(String weatherSearchResults){
        if(weatherArrayList != null){
            weatherArrayList.clear();
        }
        if(weatherSearchResults != null){
            try{
                JSONObject rootObject = new JSONObject(weatherSearchResults);
                JSONArray results = rootObject.getJSONArray(("DailyForecasts"));
                for(int i = 0; i < results.length(); i++){
                    Weather weather = new Weather();
                    JSONObject resultsObj = results.getJSONObject(i);
                    String date = resultsObj.getString("Date");

                    weather.setDate(date);


                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                    weather.setMinTemp(minTemperature);


                    String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");
                    weather.setMaxTemp(maxTemperature);

                    String link = resultsObj.getString("Link");
                    weather.setLink(link);

                    weatherArrayList.add(weather);
                }
                if(weatherArrayList != null){
                    WeatherAdapter weatherAdapter = new WeatherAdapter(getContext(), weatherArrayList);
                    listView.setAdapter(weatherAdapter);
                }
                return weatherArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }//end pJ

    private void loadFragment(Fragment frag, String tag) {
        Log.e("MADE IT TO lOADFRAGMENT() IN LANDING FRAG", tag);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}//end class LF
