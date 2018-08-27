package net.authoriti.authoritiapp.api.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

public class SchemaGroup {

    @SerializedName("schema")
    private Scheme scheme;

    @SerializedName("data_type")
    private JsonObject dataType;

    @SerializedName("data_type_keys")
    private List<String> dataTypeKeys;


    public Scheme getScheme() {
        return scheme;
    }

    public JsonObject getDataType() {
        return dataType;
    }

    public List<String> getDataTypeKeys() {
        return dataTypeKeys;
    }
}
