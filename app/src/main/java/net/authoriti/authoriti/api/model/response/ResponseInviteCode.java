package net.authoriti.authoriti.api.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/14/17.
 */

public class ResponseInviteCode {

    @SerializedName("valid")
    private boolean valid;

    @SerializedName("customer")
    private String customer;

    @SerializedName("skipDLV")
    private boolean skipDLV;

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

    public boolean isSkipDLV() {
        return skipDLV;
    }

    public void setSkipDLV(boolean skipDLV) {
        this.skipDLV = skipDLV;
    }
}
