package net.authoriti.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/21/17.
 */

public class Event {

    @SerializedName("metadata")
    private String metaData;

    @SerializedName("event")
    private String event;

    @SerializedName("time")
    private String time;

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
