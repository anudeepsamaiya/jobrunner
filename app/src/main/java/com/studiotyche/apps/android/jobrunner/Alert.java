package com.studiotyche.apps.android.jobrunner;

/**
 * Created by harish on 24/9/15.
 */
public class Alert {
    public long id;
    public String title;
    public String desc;
    public String link;
    public String timestamp;

    public Alert(long id, String title, String description, String link, String timestamp) {
        this.id = id;
        this.title = title;
        this.desc = description;
        this.link = link;
        this.timestamp = timestamp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
