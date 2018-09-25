package net.authoriti.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by movdev on 3/1/18.
 */

public class Group {

    @SerializedName("label")
    private String label;

    @SerializedName("schema")
    private int schemaIndex;

    @SerializedName("picker")
    private String picker;

    @SerializedName("value")
    private String value;

    public String getPickerName() {
        return picker;
    }

    public void setPickerName(String picker) {
        this.picker = picker;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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
}
