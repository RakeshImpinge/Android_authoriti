package net.authoriti.authoritiapp.api.model.response;

import com.google.gson.annotations.SerializedName;

import net.authoriti.authoritiapp.api.model.AccountID;
import net.authoriti.authoritiapp.api.model.Value;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class ResponseSignUpChase {

    @SerializedName("id")
    private String id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("accounts")
    private List<AccountID> accounts;


    @SerializedName("accountName")
    private String accountName;

    @SerializedName("token")
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<AccountID> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountID> accounts) {
        this.accounts = accounts;
    }
}
