package tcss450.uw.edu.group2project.model.Feeders;

public class ContactFeedItem {

    private String title;
    private String thumbnail;
    private String fname;
    private String lname;
    private int memberID;

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

    public String getLname() {return lname;    }

    public void setLname(String lname) {this.lname = lname; }

    public int getMemberID(){return memberID;}
    public void setMemberID(int theID){memberID = theID;}

    @Override
    public String toString() {
        return getTitle() + " " + getThumbnail();
    }
}

