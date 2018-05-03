package tcss450.uw.edu.group2project.chatApp;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tcss450.uw.edu.group2project.R;
import tcss450.uw.edu.group2project.model.ChatContact;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    List<ChatContact> mContactList = new ArrayList<>();
    SQLiteDatabase mAppDB;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        populateContactsList();
        mAppDB = ChatActivity.getmAppDB();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);

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
        ChatContact contact1 = new ChatContact.Builder("tester1")
                .addFirstName("test1")
                .addLastName("lastname")
                .build();
        ChatContact contact2 = new ChatContact.Builder("tester2")
                .addFirstName("test1")
                .addLastName("lastname")
                .build();
        ChatContact contact3 = new ChatContact.Builder("tester3")
                .addFirstName("test1")
                .addLastName("lastname")
                .build();
        mContactList.add(contact1);
        mContactList.add(contact2);
        mContactList.add(contact3);
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
        
        return retList;
    }
}
