package net.authoriti.authoriti.api.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/14/17.
 */

public class ResponsePolling {

    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
