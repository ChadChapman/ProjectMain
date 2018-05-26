package tcss450.uw.edu.group2project.model;

import android.text.Editable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Class to encapsulate fields for a contact in this app, not a contact from the device.
 * <p>
 * Optional fields include an image to display with the contact and a chosen color
 * to associate with the contact.
 * <p>
 * @author Chad Chapman
 * @author Charles Bryan
 * @version 5 May 2018
 */
public class ChatContact implements Serializable {

    private static final long serialVersionUID = -1634677417576883813L;

    private String mMemberID;
    private final String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mCreatedAt;
    private String mLastModified;
    private String mVerified;
    private String mContactInitiator;
    private String mImageLink;
    private String mDisplayColor;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private String mMemberID;
        private final String mUsername;
        private String mFirstName = "";
        private String mLastName = "";
        private String mEmail;
        private String mCreatedAt = "";
        private String mLastModified = "";
        private String mVerified;
        private String mContactInitiator = "";
        private String mImageLink = "";//can randomize this
        private String mDisplayColor = "";//can randomize this

        /**
         * Constructs a new Builder.
         * <p>
         * Password field is never stored as a String object.
         *
         * @param username the username
         */
        public Builder(String memberid, String username, String fname, String lname,
                       String email, String createdAt, String lastMod, String initiator) {
            mMemberID = memberid;
            mUsername = username;
            mFirstName = fname;
            mLastName = lname;
            mEmail = email;
            mCreatedAt = createdAt;
            mLastModified = lastMod;
            mVerified = "0"; //still have to wait on a response from new contact
            mContactInitiator = initiator;


        }

        /**
         * Add an optional image to display in contact.
         *
         * @param imgLink an optional first name
         * @return
         */
        public Builder addImageLink(final String imgLink) {
            mImageLink = imgLink;
            return this;
        }

        /**
         * Add an optional color to use on contact card.
         *
         * @param colorStr an optional last name
         * @return
         */
        public Builder addColor(final String colorStr) {
            mDisplayColor = colorStr;
            return this;
        }

        public ChatContact build() {
            return new ChatContact(this);
        }
    }

    /**
     * Construct a Credentials internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private ChatContact(final Builder builder) {
        mMemberID = builder.mMemberID;
        mUsername = builder.mUsername;
        mFirstName = builder.mFirstName;
        mLastName = builder.mLastName;
        mEmail = builder.mEmail;
        mCreatedAt = builder.mCreatedAt;
        mLastModified = builder.mLastModified;
        mVerified = builder.mVerified;
        mContactInitiator = builder.mContactInitiator;
        mImageLink = builder.mImageLink;
        mDisplayColor = builder.mDisplayColor;

    }

    /**
     * Get the Username.
     *
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }


    /**
     * Get the first name or the empty string if no first name was provided.
     *
     * @return the first name or the empty string if no first name was provided.
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Get the last name or the empty string if no first name was provided.
     *
     * @return the last name or the empty string if no first name was provided.
     */
    public String getLastName() {
        return mLastName;
    }

    public String getmCreatedAt() {
        return mCreatedAt;
    }

    public String getmLastModified() {
        return mLastModified;
    }

    public String getmVerified() {
        return mVerified;
    }

    public String getmContactInitiator() {
        return mContactInitiator;
    }

    public String getmImageLink() {
        return mImageLink;
    }

    public String getmDisplayColor() {
        return mDisplayColor;
    }


    /**
     * Get all of the fields in a single JSON object. Note, if no values were provided for the
     * optional fields via the Builder, the JSON object will include the empty string for those
     * fields.
     * <p>
     * Keys: username, password, first, last, email
     *
     * @return all of the fields in a single JSON object
     */
    public JSONObject asJSONObject() {
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("username", getUsername());
            msg.put("firstname", getFirstName());
            msg.put("lastname", getLastName());
            msg.put("lastmodified", getmLastModified());
            msg.put("createdat", getmCreatedAt());
            msg.put("verified", getmVerified());
            msg.put("initiator", getmContactInitiator());
            msg.put("imagelink", getmImageLink());
            msg.put("color", getmDisplayColor());

        } catch (JSONException e) {
            Log.wtf("ChatContact", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

}
