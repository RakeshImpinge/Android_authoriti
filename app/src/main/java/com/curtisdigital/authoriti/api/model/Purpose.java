package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by movdev on 3/1/18.
 */

public class Purpose {

    @SerializedName("label")
    private String label;

    @SerializedName("schema")
    private int schemaIndex;

    @SerializedName("picker")
    private String pickerName;

    @SerializedName("value")
    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSchemaIndex() {
        return schemaIndex;
    }

    public void setSchemaIndex(int schemaIndex) {
        this.schemaIndex = schemaIndex;
    }

    public String getPickerName() {
        return pickerName;
    }

    public void setPickerName(String pickerName) {
        this.pickerName = pickerName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
