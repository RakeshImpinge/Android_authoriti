package net.authoriti.authoriti.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

public class Picker implements Serializable {

    @Expose
    @SerializedName("picker")
    private String picker;

    @Expose
    @SerializedName("ui")
    private Boolean ui;

    @Expose
    @SerializedName("bytes")
    private int bytes;

    @Expose
    @SerializedName("values")
    private List<Value> values;

    @Expose
    @SerializedName("label")
    private String label;

    @Expose
    @SerializedName("title")
    private String title;


    @Expose
    @SerializedName("input")
    private String input;

    private boolean enableDefault = false;
    private int defaultIndex = -1;

    public Picker() {

    }

    public Picker(String picker, int bytes, List<Value> values, String title, String label) {

        this.picker = picker;
        this.bytes = bytes;
        this.values = values;
        this.title = title;
        this.label = label;

        enableDefault = false;
        defaultIndex = -1;
    }

    public String getPicker() {
        return picker;
    }

    public void setPicker(String picker) {
        this.picker = picker;
    }

    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnableDefault() {
        return enableDefault;
    }

    public void setEnableDefault(boolean enableDefault) {
        this.enableDefault = enableDefault;
    }

    public int getDefaultIndex() {
        return defaultIndex;
    }

    public void setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getUi() {
        if (ui == null) {
            ui = true;
        }
        return ui;
    }

    public void setUi(Boolean ui) {
        this.ui = ui;
    }


    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
