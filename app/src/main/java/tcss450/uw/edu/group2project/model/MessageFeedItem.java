package tcss450.uw.edu.group2project.model;

/**
 * Gives some basic info about a Message
 * @author Charles Bryan
 * @author Chad Chapman
 * @author Khoa Doan
 * @author Ifor Kalezic
 * @author Josh Lansang
 * @author Raymond Schooley
 * @version 1.0
 */


public class MessageFeedItem {

    private String chatid;
    private String message;

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    private String chatName;
    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String thumbnail) {
        this.message = thumbnail;
    }

    @Override
    public String toString() {
        return getChatid() + " " + getMessage();
    }

}
