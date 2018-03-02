package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

public class Scheme {

    @Expose
    @SerializedName("1")
    private List<Picker> pickers;

    public List<Picker> getPickers() {
        return pickers;
    }

    public void setPickers(List<Picker> pickers) {
        this.pickers = pickers;
    }
}
