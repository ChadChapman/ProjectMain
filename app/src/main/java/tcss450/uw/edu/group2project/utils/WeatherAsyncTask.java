package tcss450.uw.edu.group2project.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import tcss450.uw.edu.group2project.WeatherDisplay.NetworkUtils;

public class WeatherAsyncTask extends AsyncTask<URL, Void, String> {
    private Consumer<String> mOnPost;

    public WeatherAsyncTask(Consumer<String> mOnPost) {
        this.mOnPost = mOnPost;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL weatherUrl = urls[0];
        String weatherSearchResults = null;
        try {
            weatherSearchResults = NetworkUtils.getResponseFromHttpUrl(weatherUrl);
        } catch (IOException e) {

        }
        return weatherSearchResults;
    }

    @Override
    protected void onPostExecute(String weatherSearchResults) {
        mOnPost.accept(weatherSearchResults);
    }

}
