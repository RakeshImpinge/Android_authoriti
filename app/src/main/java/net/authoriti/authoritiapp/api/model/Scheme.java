package net.authoriti.authoritiapp.api.model;

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

    @Expose
    @SerializedName("2")
    private List<Picker> pickers2;


    public List<Picker> getPickers() {
        return pickers;
    }

    public List<Picker> getPickers2() {
        return pickers2;
    }

}
