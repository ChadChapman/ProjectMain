package tcss450.uw.edu.group2project.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import tcss450.uw.edu.group2project.R;

public class ChatFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIDService";
    public static final String GOT_UPDATE = "GOT NEW FIREASE UPDATE";
    private String mUserMemberIDStr;
   // private String mUserIIDToken;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
     //   mUserIIDToken = refreshedToken;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit()
                .putString(getString(R.string.firebase_iidtoken), refreshedToken)
                .apply();
        mUserMemberIDStr = preferences.getString(getString(R.string.keys_prefs_my_memberid), "-1");
        //sendRegistrationToServer(refreshedToken);
//        Intent i = new Intent(GOT_UPDATE);
//        i.putExtra("KEY", refreshedToken);
//        sendBroadcast(i);
        //or send to webserver here ->
        updateBackendWithRefreshedToken(refreshedToken);
    }

    private void updateBackendWithRefreshedToken(String newIIDToken) {

        URL backendURL = createRefreshInstanceTokenURL(newIIDToken);
        JSONObject msgObject = createJSONTokenRefreshObject(newIIDToken);
        kickOffBackendTokenUpdate(backendURL, msgObject);

    }

    private void kickOffBackendTokenUpdate(URL backendEP, JSONObject reqObject) {

        HttpURLConnection urlConnection = null;
        StringBuilder sb = new StringBuilder();
        try {

            urlConnection = (HttpURLConnection) backendEP.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outputStreamWriter.write(reqObject.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            InputStream responseContent = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseContent));
            String responseString = "";
            while((responseString = bufferedReader.readLine()) != null) {
                sb.append(responseString);
            }
            Log.e("UPDATE FIREBASE TOKEN RESPONSE :", sb.toString());

        } catch (Exception e) {
            Log.e("KICKOFF BACKEND TOKEN UPDATE EXCEPTION: ", e.getMessage());
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private JSONObject createJSONTokenRefreshObject(String newIIDToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(getString(R.string.keys_prefs_my_memberid), mUserMemberIDStr);
            jsonObject.put(getString(R.string.firebase_token), newIIDToken);
        } catch (JSONException e) {
            Log.e("CREATE JSON TOKEN REFRESH OBJECT FAILED : ", e.getMessage());
        }

        return jsonObject;
    }

    private URL createRefreshInstanceTokenURL(String newIIDToken) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_update_iidtoken))
                .build();
        URL retURL;
        try {
            retURL = new URL(uri.toString());
            return retURL;
        } catch (MalformedURLException e) {
            Log.e("REFRESH TOKEN URL BUILD :", "FAILED");
        }
        return null;
    }

}