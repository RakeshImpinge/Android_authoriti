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


    private byte[] encryptKey;

    private byte[] encryptSalt;

    private byte[] encryptPassword;

    private byte[] encryptionIV;

    private List<AccountID> unconfirmedAccountIDs;


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

    public byte[] getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(byte[] encryptKey) {
        this.encryptKey = encryptKey;
    }

    public byte[] getEncryptSalt() {
        return encryptSalt;
    }

    public void setEncryptSalt(byte[] encryptSalt) {
        this.encryptSalt = encryptSalt;
    }

    public byte[] getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(byte[] encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public List<AccountID> getUnconfirmedAccountIDs() {
        return unconfirmedAccountIDs;
    }

    public void setUnconfirmedAccountIDs(List<AccountID> unconfirmedAccountIDs) {
        this.unconfirmedAccountIDs = unconfirmedAccountIDs;
    }

    public byte[] getEncryptionIV() {
        return encryptionIV;
    }

    public void setEncryptionIV(byte[] encryptionIV) {
        this.encryptionIV = encryptionIV;
    }
}
