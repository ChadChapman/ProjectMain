package tcss450.uw.edu.group2project.model;

public class MessageFeedItem {

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
