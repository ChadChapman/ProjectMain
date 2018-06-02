package tcss450.uw.edu.group2project.model;

/**
 * Gives some basic info about a ChatMember
 * @author Charles Bryan
 * @author Chad Chapman
 * @author Khoa Doan
 * @author Ifor Kalezic
 * @author Josh Lansang
 * @author Raymond Schooley
 * @version 1.0
 */

public class ChatFeedItem {

    private String username;
    private String message;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String thumbnail) {
        this.message = thumbnail;
    }

    @Override
    public String toString() {
        return getUsername() + " " + getMessage();
    }

}
