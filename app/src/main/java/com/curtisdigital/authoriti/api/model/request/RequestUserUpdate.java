package com.curtisdigital.authoriti.api.model.request;

import com.curtisdigital.authoriti.api.model.AccountID;
import com.google.gson.annotations.SerializedName;

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
