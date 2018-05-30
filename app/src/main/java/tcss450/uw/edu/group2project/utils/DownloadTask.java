package tcss450.uw.edu.group2project.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import tcss450.uw.edu.group2project.contacts.ContactsActivity;
import tcss450.uw.edu.group2project.model.ContactFeedItem;

import static android.support.constraint.Constraints.TAG;

public class DownloadTask extends AsyncTask<String, Void, Integer> {

   // private ProgressBar mProgressBar = new ProgressBar(this.getContext());
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;
    private List<ContactFeedItem> mContactFeedItemList;

    @Override
    protected void onPreExecute() {
       // mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(String... params) {
        Integer result = 0;
        //String resultString;
        StringBuilder response = new StringBuilder();
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            int statusCode = urlConnection.getResponseCode();

            // 200 represents HTTP OK
            if (statusCode == 200) {
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }
                parseResult(response.toString());
                //parseHerokuResult(response.toString());
                result = 1; // Successful
            } else {
                result = 0; //"Failed to fetch data!";
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result; //"Failed to fetch data!";
    }

    @Override
    protected void onPostExecute(Integer result) {
      //  mProgressBar.setVisibility(View.GONE);

        //this assumes it is a ContactsActivity so I commented it out, otherwise is correct
        if (result == 1) {
            //adapter = new MyRecyclerViewAdapter(ContactsActivity.this, feedsList);
//            mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(
//                    ContactsActivity.this, mContactFeedItemList);
//            mRecyclerView.setAdapter(adapter);
        } else {
//            Toast.makeText(ContactsActivity.this
//                    , "Failed to fetch data!", Toast.LENGTH_SHORT).show();
        }
    }

    public void parseResult(String result) {
        //this should load a list or whatever with the JSON response object
        //for contacts would be filling this list => mContactFeedItemList

    }
}//end class DT
