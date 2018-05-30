package tcss450.uw.edu.group2project.WeatherDisplay;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class NetworkUtils {
    private static final String TAG = "NewtorkUtils";
    private final static String WEATHER_BASE_URL =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/331423";
    private final static String API_KEY = "QpnGCFMmidZOuQtwNIbt3ZnFknggEqRj";

    // newest -2    private final static String API_KEY = "QpnGCFMmidZOuQtwNIbt3ZnFknggEqRj";
// newest -1    private final static String API_KEY = "28ibHBD6xBSpomybED2dEQgaAgc0pDh1";
//  private final static String API_KEY = "BoeoxMyWQyO3S2Q6VnEkuWua46XzIPRu"; //newest:
// newest -3    private final static String API_KEY = "Y6CvnHQU9pAkGKGCh8NsVldTvsfA0oub";
// newest +1    private final static String API_KEY = "Gs6baRcWtkDDxmBUYIGR4415NFsQfc0Z";
    private final static String API_KEY = "01b805016496462e89548686b49d261f";

    private final static String PARAM_API_KEY = "key";
    private final static String PARAM_CITY = "city";
    private final static String PARAM_units = "units";
    private final static String PARAM_units_i = "I";


//    public static URL buildUrlForCurr(String key) {
//        Uri builtUri = Uri.parse(Curr_BASE_URL + key).buildUpon()
//                .appendQueryParameter(PARAM_API_KEY, API_KEY)
//                .build();
//
//        URL url = null;
//        try {
//            url = new URL(builtUri.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return url;
//    }
    public static URL buildUrlForCurr(String city) {
        Uri builtUri = Uri.parse(Curr_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_CITY,city)
                .appendQueryParameter(PARAM_units,PARAM_units_i)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static String getResponseFromHttpUrl(URL url)throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
