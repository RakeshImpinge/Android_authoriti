package net.authoriti.authoriti.api.model.response;

import com.google.gson.annotations.SerializedName;

import net.authoriti.authoriti.api.model.AccountID;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class ResponseSync {

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("accounts")
    private List<AccountID> accounts;

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<AccountID> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountID> accounts) {
        this.accounts = accounts;
    }
}
