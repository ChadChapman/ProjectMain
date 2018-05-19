package tcss450.uw.edu.group2project.WeatherDisplay;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = "NewtorkUtils";
    private final static String WEATHER_BASE_URL =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/331423";
    //private final static String API_KEY = "QpnGCFMmidZOuQtwNIbt3ZnFknggEqRj";
    private final static String API_KEY = "Y6CvnHQU9pAkGKGCh8NsVldTvsfA0oub";


    private final static String PARAM_API_KEY = "apikey";


    public static URL buildUrlForWeather(){
        Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
    public static String getResponseFromHttpUrl(URL url)throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }else{
                return null;
            }
        }finally{
            urlConnection.disconnect();
        }
    }
}
