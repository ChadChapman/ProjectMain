package tcss450.uw.edu.group2project.model;

import android.text.Editable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Class to encapsulate credentials fields. Building an Object requires a username and password.
 * <p>
 * Optional fields include email, first and last name.
 * <p>
 * Password field is never stored as a String object. The method getPassword allows only one access
 * and clears the password field after the initial access.
 * <p>
 * Accessing the fields using the asJSONObject method does not clear the password field. Repeated
 * calls to asJSONObject continue to include the password. However, calls to asJSONObject after
 * getPassword has been used will result in an empty password in the resulting JSON object.
 *
 * @author Charles Bryan
 * @version 14 April 2018
 */
public class ChatContact implements Serializable {


    private final String mUsername;

    private String mFirstName;
    private String mLastName;
    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mUsername;


        private String mFirstName = "";
        private String mLastName = "";


        /**
         * Constructs a new Builder.
         * <p>
         * Password field is never stored as a String object.
         *
         * @param username the username
         */
        public Builder(String username) {
            mUsername = username;

        }


        /**
         * Add an optional first name.
         *
         * @param val an optional first name
         * @return
         */
        public Builder addFirstName(final String val) {
            mFirstName = val;
            return this;
        }

        /**
         * Add an optional last name.
         *
         * @param val an optional last name
         * @return
         */
        public Builder addLastName(final String val) {
            mLastName = val;
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
        mUsername = builder.mUsername;

        mFirstName = builder.mFirstName;
        mLastName = builder.mLastName;

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

            msg.put("first", getFirstName());
            msg.put("last", getLastName());

        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

}
