package net.authoriti.authoriti.api.model.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac on 12/21/17.
 */

public class RequestComplete {

    @SerializedName("accountId")
    private String accountId;

    @SerializedName("pc")
    private String pc;

    public RequestComplete(String accountId, String pc) {
        this.accountId = accountId;
        this.pc = pc;
    }
}
