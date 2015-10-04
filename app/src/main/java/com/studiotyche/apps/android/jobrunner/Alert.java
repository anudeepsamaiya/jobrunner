package com.studiotyche.apps.android.jobrunner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by harish on 24/9/15.
 */
public class Alert implements Parcelable {
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

    protected Alert(Parcel in) {
        id = in.readLong();
        title = in.readString();
        desc = in.readString();
        link = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel in) {
            return new Alert(in);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(link);
        dest.writeString(timestamp);
    }
}
