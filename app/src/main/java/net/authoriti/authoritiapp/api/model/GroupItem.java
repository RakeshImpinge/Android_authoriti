package net.authoriti.authoritiapp.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class GroupItem {

    private int isHeading = 0;
    private String label;
    private int schemaIndex;
    private String pickerName;
    private String value;

    int indexGroup = -1;
    int indexItem = -1;

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

    public int isHeading() {
        return isHeading;
    }

    public void setHeading(int heading) {
        isHeading = heading;
    }

    public int getIsHeading() {
        return isHeading;
    }

    public void setIsHeading(int isHeading) {
        this.isHeading = isHeading;
    }

    public int getIndexGroup() {
        return indexGroup;
    }

    public void setIndexGroup(int indexGroup) {
        this.indexGroup = indexGroup;
    }

    public int getIndexItem() {
        return indexItem;
    }

    public void setIndexItem(int indexItem) {
        this.indexItem = indexItem;
    }
}
