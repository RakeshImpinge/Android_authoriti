package net.authoriti.authoriti.api.model.request;

import com.google.gson.annotations.SerializedName;

import net.authoriti.authoriti.api.model.AccountID;

import java.util.List;

/**
 * Created by mac on 12/17/17.
 */

public class RequestUserUpdate {
    @SerializedName("accounts")
    private List<AccountID> accountIDs;

    public List<AccountID> getAccountIDs() {
        return accountIDs;
    }

    public void setAccountIDs(List<AccountID> accountIDs) {
        this.accountIDs = accountIDs;
    }
}
