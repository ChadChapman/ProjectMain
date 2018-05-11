package tcss450.uw.edu.group2project.model;

public class ContactFeedItem {

        private String title;
        private String thumbnail;

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

        @Override
    public String toString() {
            return getTitle() + " " + getThumbnail();
        }
    }

