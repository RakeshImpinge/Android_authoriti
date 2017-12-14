package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/13/17.
 */

public class User {

    @SerializedName("id")
    private String userId;
    @SerializedName("password")
    private String password;
    @SerializedName("invite_code")
    private String inviteCode;
    @SerializedName("accounts")
    private List<AccountID> accountIDs;
    @SerializedName("token")
    private String token;
    @SerializedName("salt")
    private String salt;
    @SerializedName("key")
    private String privateKey;

    public User(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public List<AccountID> getAccountIDs() {
        return accountIDs;
    }

    public void setAccountIDs(List<AccountID> accountIDs) {
        this.accountIDs = accountIDs;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
