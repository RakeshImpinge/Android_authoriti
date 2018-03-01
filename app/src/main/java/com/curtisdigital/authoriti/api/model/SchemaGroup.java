package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by movdev on 3/1/18.
 */

public class SchemaGroup {

    @SerializedName("schema")
    private Scheme scheme;

    @SerializedName("data_type")
    private DataType dataType;


    public Scheme getScheme() {
        return scheme;
    }

    public DataType getDataType() {
        return dataType;
    }
}
