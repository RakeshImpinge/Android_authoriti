package com.curtisdigital.authoriti.api.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/14/17.
 */

public class ResponseInviteCode {

    @SerializedName("valid")
    private boolean valid;

    @SerializedName("customer")
    private String customer;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
