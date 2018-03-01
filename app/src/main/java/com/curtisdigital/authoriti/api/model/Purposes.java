package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class Purposes {

    @SerializedName("purposes")
    private List<Purpose> purposes;

    public List<Purpose> getPurposes() {
        return purposes;
    }

    public void setPurposes(List<Purpose> purposes) {
        this.purposes = purposes;
    }
}
