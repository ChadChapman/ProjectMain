package tcss450.uw.edu.group2project.model;

public class ContactFeedItem {

    private String title;
    private String thumbnail;
    private String fname;
    private String lname;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {this.fname = fname; }
    public String getLname() {return fname;    }

    public void setLname(String lname) {this.fname = lname; }

    @Override
    public String toString() {
        return getTitle() + " " + getThumbnail();
    }
}

