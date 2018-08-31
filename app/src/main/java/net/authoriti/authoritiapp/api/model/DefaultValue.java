package net.authoriti.authoritiapp.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mac on 12/1/17.
 */

public class DefaultValue implements Serializable {


    String title;

    String value;

    boolean isDefault;

    public DefaultValue(String title, String value, boolean isDefault) {
        this.title = title;
        this.value = value;
        this.isDefault = isDefault;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
