package net.authoriti.authoriti.api.model;

import java.io.Serializable;

/**
 * Created by mac on 12/1/17.
 */

public class DefaultValue implements Serializable {


    String title;

    String value;

    boolean isDefault;

    String customer="";

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


    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
