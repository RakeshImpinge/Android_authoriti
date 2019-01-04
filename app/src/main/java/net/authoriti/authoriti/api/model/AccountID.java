package net.authoriti.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/13/17.
 */

public class AccountID {

    @SerializedName("type")
    private String type;
    @SerializedName("value")
    private String identifier;
    @SerializedName("hashed")
    private Boolean hashed;

    private boolean confirmed = true;

    public String getCustomer() {

        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    private String Customer = "";
    private String Customer_ID = "";

    public String getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(String customer_ID) {
        Customer_ID = customer_ID;
    }

    public AccountID() {
        this.type = "";
        this.identifier = "";
        this.hashed = false;
        confirmed = true;
    }

    public AccountID(String type, String identifier, Boolean hashed) {
        this.type = type;
        this.identifier = identifier;
        this.hashed = hashed;
        confirmed = true;
    }

    public AccountID(String type, String identifier, Boolean hashed, String Customer) {
        this.type = type;
        this.identifier = identifier;
        this.hashed = hashed;
        confirmed = true;
        this.Customer = Customer;
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

    public Boolean getHashed() {
        return this.hashed;
    }

    public void setHashed(boolean hashed) {
        this.hashed = hashed;
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
