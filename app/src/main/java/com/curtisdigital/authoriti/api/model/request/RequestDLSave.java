package com.curtisdigital.authoriti.api.model.request;

import com.curtisdigital.authoriti.api.model.Event;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/21/17.
 */

public class RequestDLSave {

    @SerializedName("TOKEN")
    private String token;

    @SerializedName("events")
    private List<Event> events;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
