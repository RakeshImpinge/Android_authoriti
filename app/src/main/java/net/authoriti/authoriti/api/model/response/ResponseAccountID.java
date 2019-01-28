package net.authoriti.authoriti.api.model.response;

import com.google.gson.annotations.SerializedName;

public class ResponseAccountID {
    @SerializedName("userId")
    private String userId;

    @SerializedName("type")
    private String type;

    @SerializedName("number")
    private String identifier;

    private boolean confirmed = true;

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    private String Customer = "";

    public ResponseAccountID() {
        this.type = "";
        this.identifier = "";
        confirmed = true;
    }

    public ResponseAccountID(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
        confirmed = true;
    }

    public ResponseAccountID(String type, String identifier, String Customer) {
        this.type = type;
        this.identifier = identifier;
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
