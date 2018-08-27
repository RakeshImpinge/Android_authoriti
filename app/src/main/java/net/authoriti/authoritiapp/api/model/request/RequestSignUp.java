package net.authoriti.authoritiapp.api.model.request;

import net.authoriti.authoritiapp.api.model.AccountID;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class RequestSignUp {

    @SerializedName("password")
    private String password;

    @SerializedName("key")
    private String key;

    @SerializedName("salt")
    private String salt;

    @SerializedName("code")
    private String code;

    @SerializedName("account")
    private List<AccountID> accountIDs;

    public RequestSignUp(String password, String key, String salt, String code, List<AccountID> accountIDs){
        this.password = password;
        this.key = key;
        this.salt = salt;
        this.code = code;
        this.accountIDs = accountIDs;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
