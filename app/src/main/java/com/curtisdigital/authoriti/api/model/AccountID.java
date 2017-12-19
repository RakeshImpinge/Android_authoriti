package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/13/17.
 */

public class AccountID {

    @SerializedName("type")
    private String type;
    @SerializedName("value")
    private String identifier;

    private boolean confirmed = true;

    public AccountID(){
        this.type = "";
        this.identifier = "";
        confirmed = true;
    }

    public AccountID(String type, String identifier){
        this.type = type;
        this.identifier = identifier;
        confirmed = true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
