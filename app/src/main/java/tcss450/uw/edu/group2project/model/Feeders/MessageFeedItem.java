package tcss450.uw.edu.group2project.model.Feeders;

public class MessageFeedItem {

    private String chatid;
    private String message;

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
