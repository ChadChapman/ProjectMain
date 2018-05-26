package tcss450.uw.edu.group2project.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import tcss450.uw.edu.group2project.model.ChatContact;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RabbitChat.db";

    /**members table*/
    public static final String MEMBERS_TABLE_NAME = "Members";
    public static final String MEMBERS_COLUMN_ID = "MemberID";
    public static final String MEMBERS_COLUMN_FIRSTNAME = "FirstName";
    public static final String MEMBERS_COLUMN_LASTNAME = "LastName";
    public static final String MEMBERS_COLUMN_USERNAME = "Username";
    public static final String MEMBERS_COLUMN_EMAIL = "Email";
    public static final String MEMBERS_COLUMN_VERIFICATION = "Verification";

    /**Contacts table*/
    public static final String CONTACTS_TABLE_NAME = "Contacts";
    public static final String CONTACTS_COLUMN_ID = "PrimaryKey";
    public static final String CONTACTS_COLUMN_MEMBER_A = "MemberID_A";
    public static final String CONTACTS_COLUMN_MEMBER_B = "MemberID_B";
    public static final String CONTACTS_COLUMN_VERIFIED = "Verified";

    /**Chats table*/
    public static final String CHATS_TABLE_NAME = "Chats";
    public static final String CHATS_COLUMN_ID = "ChatID";
    public static final String CHATS_COLUMN_NAME = "Name";

    /**ChatMembers table*/
    public static final String CHATMEMBERS_TABLE_NAME = "ChatMembers";
    public static final String CHATMEMBERS_COLUMN_ID = "ChatID";
    public static final String CHATMEMBERS_COLUMN_MEMBERID = "MemberID";

    /**Messages table*/
    public static final String MESSAGES_TABLE_NAME = "Messages";
    public static final String MESSAGES_COLUMN_ID = "PrimaryKey";
    public static final String MESSAGES_COLUMN_CHATID = "ChatID";
    public static final String MESSAGES_COLUMN_MESSAGE = "Message";
    public static final String MESSAGES_COLUMN_MEMBERID = "MemberID";
    public static final String MESSAGES_COLUMN_TIMESTAMP = "TimeStamp";

    /**Locations table*/
    public static final String LOCATIONS_TABLE_NAME = "Locations";
    public static final String LOCATIONS_COLUMN_ID = "PrimaryKey";
    public static final String LOCATIONS_COLUMN_MEMBERID = "MemberID";
    public static final String LOCATIONS_COLUMN_NICKNAME = "Nickname";
    public static final String LOCATIONS_COLUMN_LAT = "Lat";
    public static final String LOCATIONS_COLUMN_LONG = "Long";
    public static final String LOCATIONS_COLUMN_ZIP = "ZIP";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Members Table
        db.execSQL("CREATE TABLE IF NOT EXISTS Members (MemberID SERIAL PRIMARY KEY," +
                "                      FirstName VARCHAR(255) NOT NULL," +
                "                      LastName VARCHAR(255) NOT NULL," +
                "                      Username VARCHAR(255) NOT NULL UNIQUE," +
                "                      Email VARCHAR(255) NOT NULL UNIQUE," +
                "                      Password VARCHAR(255) NOT NULL," +
                "                      SALT VARCHAR(255)," +
                "                      Verification INT DEFAULT 0, " +
                "                      Changed INT DEFAULT 0)");

        //Create Contacts table
        db.execSQL("CREATE TABLE IF NOT EXISTS Contacts(PrimaryKey SERIAL PRIMARY KEY," +
                "                      MemberID_A INT NOT NULL," +
                "                      MemberID_B INT NOT NULL," +
                "                      Verified INT DEFAULT 0," +
                                       "Changed INT DEFAULT 0," +
                "                      FOREIGN KEY(MemberID_A) REFERENCES Members(MemberID)," +
                "                      FOREIGN KEY(MemberID_B) REFERENCES Members(MemberID))");

        //Create Chats table
        db.execSQL("CREATE TABLE IF NOT EXISTS Chats (ChatID SERIAL PRIMARY KEY," +
                "                    Name VARCHAR(255)," +
                "                    Changed INT DEFAULT 0)");

        //Create ChatMembers table
        db.execSQL("CREATE TABLE IF NOT EXISTS ChatMembers (ChatID INT NOT NULL," +
                "                          MemberID INT NOT NULL," +
                                           "Changed INT DEFAULT 0," +
                "                          FOREIGN KEY(MemberID) REFERENCES Members(MemberID)," +
                "                          FOREIGN KEY(ChatID) REFERENCES Chats(ChatID))");



        //Create Messages table
        db.execSQL("CREATE TABLE IF NOT EXISTS Messages (PrimaryKey SERIAL PRIMARY KEY," +
                "                       ChatID INT," +
                "                       Message VARCHAR(255)," +
                "                       MemberID INT," +
                                       "Changed INT DEFAULT 0," +
                                       " TimeStamp TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp," +
                "                       FOREIGN KEY(MemberID) REFERENCES Members(MemberID)," +
                "                       FOREIGN KEY(ChatID) REFERENCES Chats(ChatID),)");

        //Create Locations Table
        db.execSQL("CREATE TABLE IF NOT EXISTS Locations (PrimaryKey SERIAL PRIMARY KEY," +
                "                        MemberID INT," +
                "                        Nickname VARCHAR(255)," +
                "                        Lat DECIMAL," +
                "                        Long DECIMAL," +
                "                        ZIP INT," +
                                        "Changed INT DEFAULT 0," +
                "                        FOREIGN KEY(MemberID) REFERENCES Members(MemberID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Members");
        db.execSQL("DROP TABLE IF EXISTS Contacts");
        db.execSQL("DROP TABLE IF EXISTS Chats");
        db.execSQL("DROP TABLE IF EXISTS ChatMembers");
        db.execSQL("DROP TABLE IF EXISTS Messages");
        db.execSQL("DROP TABLE IF EXISTS Locations");
        onCreate(db);
    }

    public void insertContacts(List<ChatContact> theContacts){
        SQLiteDatabase db = this.getWritableDatabase();
        for (ChatContact contact: theContacts) {
            ContentValues contentValues = new ContentValues();
            contentValues.put()
        }
    }
}
