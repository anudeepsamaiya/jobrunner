package com.studiotyche.apps.android.jobrunner;

/**
 * Created by harish on 24/9/15.
 */
public class Alert {
    public String title;
    public String desc;
    public String link;
    public String timestamp;
    private String timeStamp;

    public Alert(String title, String description, String link, String timestamp) {
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

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
