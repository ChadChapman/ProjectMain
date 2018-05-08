package tcss450.uw.edu.group2project.chatApp;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.R.layout.simple_list_item_1;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;
//import tcss450.uw.edu.group2project.utils.MyAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<ChatContact> mContactList = new ArrayList<>();
    private SQLiteDatabase mAppDB;

    private String[] myDataset = {"Test", "Contacts", "List"};
    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_contact, container, false);

    }

    /**
     * not sure this belongds here but i am going to leave it and make sure this push isn't borked
     */
    public void setupRecyclerView() {
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.ChatContactRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyChatContactsAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            saveContactsListToSQLite();
        } catch (JSONException e) {
            //TODO give signal contacts were not saved on app exit
        }
    }


    /**
     *
     * Write all information in the current list of ChatContacts (still strings right now) to
     * an internal database.
     * This way even if the internet isn't working we can still populate a list of contacts from
     * an internal db query.
     * Should iterate through list and insert all into the sqlite db.  Currently does not iterate
     * through shit.
     *
     * @throws JSONException - tried to make a JSONArray out of the list, it didn't work
     */
    public void saveContactsListToSQLite() throws JSONException {

        JSONArray jsonArray = new JSONArray(mContactList);

        ContentValues jsonStringContentValues = new ContentValues();
//        for (ChatContact cc : mContactList) { //left this to demonstrate what i'm trying to do here
//            jsonStringContentValues.put();
//        }
        jsonStringContentValues.put("jsonChatContacts", jsonArray.toString());
        mAppDB.beginTransaction();
        mAppDB.insert("ChatContacts", "JSONContacts", jsonStringContentValues);
        mAppDB.endTransaction();
    }

    /**
     * Fetch all ChatContact rows from the internal sqlite db and return them as a list.
     *
     * Rather than wait on an internet connection, we can just load info for contacts from
     * this query's list.  The list may not be the most up to date info.
     * As as aside, the contacts in the db will be updated with an async call.
     * This method prevents having to wait on HTTP actions to create the contacts ativity
     * or the contacts frag.
     *
     * @return - a list of ChatContacts created from a query to the internal db, contains all
     * contacts found on the device for this user.
     *
     */

    public List<ChatContact> loadContactsListFromSQLite() {
        List<ChatContact> retList = new ArrayList<>();
        String[] queryColumns = new String[1];
        queryColumns[0] = "JSONContacts";
        Cursor dbCursor;
        String jsonArrString;
        mAppDB.beginTransaction();
        dbCursor = mAppDB.query("ChatContacts", queryColumns, null, null, null, null, null);
        jsonArrString = dbCursor.getString(0);
        mAppDB.endTransaction();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonArrString);
        } catch (JSONException e) {
            //TODO again need ot handle JSOn parsing failing
            jsonArray = new JSONArray();
        }
        int arrLength = jsonArray.length();
        try {
            for (int i = 0; i < arrLength; ++i) {
                retList.add((ChatContact)jsonArray.get(i));
            }
        } catch (JSONException e) {
            //TODO still need to be handling these!
        }
        return retList;
    }
}
