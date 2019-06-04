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

    @SerializedName("ignoreAcuant")
    private boolean ignoreAcuant;

    @SerializedName("callAuth")
    private boolean callAuth;

    @SerializedName("callAuthNumber")
    private String callAuthNumber;

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

    public boolean ignoreAcuant() {
        return ignoreAcuant;
    }

    public void setSkipDLV(boolean skipDLV) {
        this.skipDLV = skipDLV;
    }

    public boolean isIgnoreAcuant() {
        return ignoreAcuant;
    }

    public void setIgnoreAcuant(boolean ignoreAcuant) {
        this.ignoreAcuant = ignoreAcuant;
    }

    public boolean isCallAuth() {
        return callAuth;
    }

    public void setCallAuth(boolean callAuth) {
        this.callAuth = callAuth;
    }

    public String getCallAuthNumber() {
        return callAuthNumber;
    }

    public void setCallAuthNumber(String callAuthNumber) {
        this.callAuthNumber = callAuthNumber;
    }
}
