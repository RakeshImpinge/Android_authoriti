package net.authoriti.authoriti.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mac on 12/1/17.
 */

public class Value implements Serializable{

    @Expose
    @SerializedName("value")
    private String value;

    @Expose
    @SerializedName("title")
    private String title;


    private boolean isCustomDate = false;

    public Value(String value, String title){
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCustomDate() {
        return isCustomDate;
    }

    public void setCustomDate(boolean customDate) {
        isCustomDate = customDate;
    }

}
