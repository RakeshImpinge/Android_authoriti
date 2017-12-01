package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

public class Picker {

    @Expose
    @SerializedName("picker")
    private String picker;

    @Expose
    @SerializedName("bytes")
    private int bytes;

    @Expose
    @SerializedName("values")
    private List<Value> values;

    @Expose
    @SerializedName("title")
    private String title;

    public String getPicker() {
        return picker;
    }

    public void setPicker(String picker) {
        this.picker = picker;
    }

    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
