package net.authoriti.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

    private String encryptKey;

    private String encryptPrivateKey;

    private String encryptSalt;

    private String encryptPassword;

    private boolean fingerPrintAuthEnabled = false;

    // enabled - Enabled by user
    // enabled - Disabled by user
    // blank - Enabled by user
    private String fingerPrintAuthStatus = "";


    private boolean isChaseType = false;

    private List<AccountID> unconfirmedAccountIDs;


    public User() {

    }

    public boolean getChaseType() {
        return isChaseType;
    }

    public void setChaseType(boolean chaseType) {
        isChaseType = chaseType;
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

    public AccountID getAccountFromID(String id) {
        for (AccountID account : accountIDs) {
            if (account.getIdentifier().equals(id)) {
                return account;
            }
        }
        return null;
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

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getEncryptSalt() {
        return encryptSalt;
    }

    public void setEncryptSalt(String encryptSalt) {
        this.encryptSalt = encryptSalt;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public List<AccountID> getUnconfirmedAccountIDs() {
        return unconfirmedAccountIDs;
    }

    public void setUnconfirmedAccountIDs(List<AccountID> unconfirmedAccountIDs) {
        this.unconfirmedAccountIDs = unconfirmedAccountIDs;
    }

    public String getEncryptPrivateKey() {
        return encryptPrivateKey;
    }

    public void setEncryptPrivateKey(String encryptPrivateKey) {
        this.encryptPrivateKey = encryptPrivateKey;
    }

    public boolean isFingerPrintAuthEnabled() {
        return fingerPrintAuthEnabled;
    }

    public void setFingerPrintAuthEnabled(boolean fingerPrintAuthEnabled) {
        this.fingerPrintAuthEnabled = fingerPrintAuthEnabled;
    }

    private List<String> downloadedWalletIDList = new ArrayList<>();

    public List<String> getDownloadedWalletIDList() {
        return downloadedWalletIDList;
    }

    public void setDownloadedWalletIDList(List<String> downloadedWalletIDList) {
        this.downloadedWalletIDList = downloadedWalletIDList;
    }

    public String getFingerPrintAuthStatus() {
        return fingerPrintAuthStatus;
    }

    public void setFingerPrintAuthStatus(String fingerPrintAuthStatus) {
        this.fingerPrintAuthStatus = fingerPrintAuthStatus;
    }
}
