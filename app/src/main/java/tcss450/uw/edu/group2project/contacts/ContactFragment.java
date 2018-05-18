package tcss450.uw.edu.group2project.contacts;


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
//import android.R.layout.simple_list_item_1;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;
//import tcss450.uw.edu.group2project.utils.MyChatContactsAdapter;
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

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_main);
//        //populateContactsList();
//        //mAppDB = ChatActivity.getmAppDB();
//        //mContactList = loadContactsListFromSQLite();
//
////        ArrayAdapter<ChatContact> adapter = new ArrayAdapter<ChatContact>(this, R.layout.fragment_contact, R.id.ChatContactsTextView, mContactList);
////        ListView lv= (ListView) getActivity().findViewById(R.id.ChatContactsListView);
////        lv.setAdapter(adapter);
////        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.ChatContactRecyclerView);
////
////        // use this setting to improve performance if you know that changes
////        // in content do not change the layout size of the RecyclerView
////        //mRecyclerView.setHasFixedSize(true);
////
////        // use a linear layout manager
////        mLayoutManager = new LinearLayoutManager(getContext());
////        mRecyclerView.setLayoutManager(mLayoutManager);
////
////        // specify an adapter (see also next example)
////        mAdapter = new MyChatContactsAdapter(myDataset);
////        mRecyclerView.setAdapter(mAdapter);
//
//    }
//}
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.ChatContactRecyclerView);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        //mRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)
//        mAdapter = new MyChatContactsAdapter(myDataset);
//        mRecyclerView.setAdapter(mAdapter);
        return inflater.inflate(R.layout.fragment_contact, container, false);

    }

    public void setupRecyclerView() {
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.ChatContactRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
       // mAdapter = new MyChatContactsAdapter(myDataset);
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

    private void populateContactsList() {
//        ChatContact contact1 = new ChatContact.Builder("tester1")
//                .addFirstName("test1")
//                .addLastName("lastname")
//                .build();
//        ChatContact contact2 = new ChatContact.Builder("tester2")
//                .addFirstName("test1")
//                .addLastName("lastname")
//                .build();
//        ChatContact contact3 = new ChatContact.Builder("tester3")
//                .addFirstName("test1")
//                .addLastName("lastname")
//                .build();
//        mContactList.add(contact1);
//        mContactList.add(contact2);
//        mContactList.add(contact3);
    }

    public void saveContactsListToSQLite() throws JSONException {
        //JSONObject jsonContacts = new JSONObject();
        //jsonContacts.put("chatContactsList", new JSONArray(mContactList));
        JSONArray jsonArray = new JSONArray(mContactList);
        //String jsonContactsString = jsonContacts.toString();
        ContentValues jsonStringContentValues = new ContentValues();
//        for (ChatContact cc : mContactList) {
//            jsonStringContentValues.put();
//        }
        jsonStringContentValues.put("jsonChatContacts", jsonArray.toString());
        mAppDB.beginTransaction();
        mAppDB.insert("ChatContacts", "JSONContacts", jsonStringContentValues);
        mAppDB.endTransaction();
    }


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
