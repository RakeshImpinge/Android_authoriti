package net.authoriti.authoriti.api.model.request;

import net.authoriti.authoriti.api.model.AccountID;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class RequestSignUp {

    @SerializedName("key")
    private String key;

    @SerializedName("code")
    private String code;

    @SerializedName("account")
    private List<AccountID> accountIDs;


    public RequestSignUp(String key, String code, List<AccountID> accountIDs) {
        this.key = key;
        this.code = code;
        this.accountIDs = accountIDs;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AccountID> getAccountIDs() {
        return accountIDs;
    }

    public void setAccountIDs(List<AccountID> accountIDs) {
        this.accountIDs = accountIDs;
    }

}
