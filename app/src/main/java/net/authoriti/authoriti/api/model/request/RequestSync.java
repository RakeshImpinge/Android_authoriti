package net.authoriti.authoriti.api.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/14/17.
 */

public class RequestSync {

    @SerializedName("userId")
    private List<String> userId;

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }
}
