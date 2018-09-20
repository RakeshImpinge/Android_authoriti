package net.authoriti.authoriti.api.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by movdev on 3/1/18.
 */

public class SchemaGroup {

    @SerializedName("schema")
    private Map<String, List<Picker>> schema;

    @SerializedName("data_type")
    private JsonObject dataType;

    @SerializedName("data_type_keys")
    private List<String> dataTypeKeys;

//    public Scheme getScheme() {
//        return scheme;
//    }


    public Map<String, List<Picker>> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, List<Picker>> schema) {
        this.schema = schema;
    }

    public JsonObject getDataType() {
        return dataType;
    }

    public List<String> getDataTypeKeys() {
        return dataTypeKeys;
    }
}
