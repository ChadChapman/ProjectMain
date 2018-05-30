package tcss450.uw.edu.group2project.chatApp;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.WeatherDisplay.Weather;
import tcss450.uw.edu.group2project.utils.GetPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherLocationList extends Fragment {
    private View v;
    private String mUserMemberID;
    private RecyclerView mRecyclerView;
    private List<Weather> savedLocs;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    public WeatherLocationList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_weather_location_list, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        mUserMemberID = prefs.getString(getString(R.string.keys_prefs_my_memberid),
                "MEMBERID NOT FOUND IN PREFS");

        mRecyclerView = v.findViewById(R.id.weather_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedLocs = new ArrayList<>();

        getSavedLocs();
        return v;
    }
    /*----------------------Async Task to get Locations----------------------*/
    private void getSavedLocs() {
        String myMsg = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_locations))
                .appendPath("getAllLoc")
                .appendQueryParameter("memberid", mUserMemberID)
                .build()
                .toString();

        JSONObject messageJson = new JSONObject();
        new GetPostAsyncTask.Builder(myMsg, messageJson)
                .onPostExecute(this::getLocs)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void getLocs(String result) {
        try {
            Log.e("result", new JSONObject(result).toString());
            JSONArray obj = new JSONObject(result).getJSONArray("locations");
            for (int i = 0; i < obj.length(); i++) {
                savedLocs.add(newWeather(obj.getJSONObject(i)));
                Log.e("locs", String.valueOf(savedLocs.get(i).getCity()));
                Log.e("locs", String.valueOf(savedLocs.get(i).getLat()));
                Log.e("locs", String.valueOf(savedLocs.get(i).getLon()));
            }


            adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.weather_list_holder, null);
                    return new CustomViewHolder(view);
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder customViewHolder, int i) {
                    Weather feedItem = savedLocs.get(i);


                    ((CustomViewHolder) customViewHolder).city.setText(feedItem.getCity());
                    ((CustomViewHolder) customViewHolder).lat.setText(String.valueOf(feedItem.getLat()));
                    ((CustomViewHolder) customViewHolder).lng.setText(String.valueOf(feedItem.getLon()));

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("weather", feedItem);
                            Fragment disp = new DisplayConditionsFragment();
                            disp.setArguments(bundle);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainer, disp, getString(R.string.keys_fragment_condition))
                                    .addToBackStack(null);
                            // Commit the transaction
                            transaction.commit();

                        }
                    };
                    ((CustomViewHolder) customViewHolder).v.setOnClickListener(listener);
//                    ((CustomViewHolder) customViewHolder).city.setOnClickListener(listener);
//                    ((CustomViewHolder) customViewHolder).lat.setOnClickListener(listener);
//                    ((CustomViewHolder) customViewHolder).lng.setOnClickListener(listener);

                }

                @Override
                public int getItemCount() {
                    return (null != savedLocs ? savedLocs.size() : 0);
                }
            };
            mRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.e("result", e.toString());
        }
    }

    private Weather newWeather(JSONObject obj) {
        Weather temp = new Weather();
        try {
            temp.setCity(obj.getString("nickname"));
            temp.setLat(Double.parseDouble(obj.getString("lat")));
            temp.setLon(Double.parseDouble(obj.getString("long")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }
    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    /**
     * begin internal class for the viewholder
     */
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final TextView city;
        private final TextView lat;
        private final TextView lng;
        private final CardView v;
        public CustomViewHolder(View view) {
            super(view);
            v = view.findViewById(R.id.cardView);
            city = view.findViewById(R.id.weather_list_city);
            lat = view.findViewById(R.id.weather_list_lat);
            lng = view.findViewById(R.id.weather_list_lng);
        }
    }
}
