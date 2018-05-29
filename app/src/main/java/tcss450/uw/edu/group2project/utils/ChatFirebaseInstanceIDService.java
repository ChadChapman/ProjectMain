package tcss450.uw.edu.group2project.utils;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class ChatFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIDService";
    public static final String GOT_UPDATE = "GOT NEW FIREASE UPDATE";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //sendRegistrationToServer(refreshedToken);
//        Intent i = new Intent(GOT_UPDATE);
//        i.putExtra("KEY", refreshedToken);
//        sendBroadcast(i);

        //or send to webserver here ->


    }
}