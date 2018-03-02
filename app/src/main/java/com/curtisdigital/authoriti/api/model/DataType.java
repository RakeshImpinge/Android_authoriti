package com.curtisdigital.authoriti.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class DataType {

    @SerializedName("01")
    private List<Value> type1;

    @SerializedName("02")
    private List<Value> type2;

    @SerializedName("03")
    private List<Value> type3;

    @SerializedName("04")
    private List<Value> type4;

    @SerializedName("05")
    private List<Value> type5;

    @SerializedName("06")
    private List<Value> type6;

    public List<Value> getType1() {
        return type1;
    }

    public List<Value> getType2() {
        return type2;
    }

    public List<Value> getType3() {
        return type3;
    }

    public List<Value> getType4() {
        return type4;
    }

    public List<Value> getType5() {
        return type5;
    }

    public List<Value> getType6() {
        return type6;
    }


    private int preSelectedTypeIndex;
    private int selectedTypeIndex;
    private List<Value> selectedValues;

    public List<Value> getType(int index){

        switch (index){
            case 0:
                return type1;
            case 1:
                return type2;
            case 2:
                return type3;
            case 3:
                return type4;
            case 4:
                return type5;
            case 5:
                return type6;
            default:
                return null;
        }

    }

    public int getPreSelectedTypeIndex() {
        return preSelectedTypeIndex;
    }

    public void setPreSelectedTypeIndex(int preSelectedTypeIndex) {
        this.preSelectedTypeIndex = preSelectedTypeIndex;
    }

    public int getSelectedTypeIndex() {
        return selectedTypeIndex;
    }

    public void setSelectedTypeIndex(int selectedTypeIndex) {
        this.selectedTypeIndex = selectedTypeIndex;
    }

    public List<Value> getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(List<Value> selectedValues) {
        this.selectedValues = selectedValues;
    }

}
