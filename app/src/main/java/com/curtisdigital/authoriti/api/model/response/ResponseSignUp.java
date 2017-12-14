package com.curtisdigital.authoriti.api.model.response;

import com.curtisdigital.authoriti.api.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/14/17.
 */

public class ResponseSignUp {

    @SerializedName("userId")
    private String userId;

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
