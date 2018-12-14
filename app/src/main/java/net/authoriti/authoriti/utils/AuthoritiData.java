package net.authoriti.authoriti.utils;

import android.content.Context;
import android.content.SharedPreferences;

import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.DefaultValue;
import net.authoriti.authoriti.api.model.Order;
import net.authoriti.authoriti.api.model.Picker;
import net.authoriti.authoriti.api.model.Purpose;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.utils.crypto.Crypto;
import net.authoriti.authoriti.utils.crypto.CryptoKeyPair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 12/1/17.
 */

@EBean(scope = EBean.Scope.Singleton)
public class AuthoritiData {

    @Pref
    AuthoritiPref_ pref;

    // Auth Processing Temp Data
    public String inviteCode;
    public boolean showSkip;
    public boolean ignoreAcuant;
    public String password = "";
    public byte[] iv = {-100, 34, 63, 23, -111, 30, -11, -45, 40, 96, -100, 73, 63, 12, -124, 23};
    public List<AccountID> accountIDs;
    public boolean defaultAccountSelected;
    public int defaultAccountIndex;

    private int selectedAccountIndex;
    private int selectedIndustryIndex;
    private int selectedCountryIndex;
    private int selectedTimeIndex;
    private int selectedGeoIndex;
    private int selectedRequestIndex;
    private int selectedDataTypeIndex;

    private boolean accountIndexSelected;
    private boolean industryIndexSelected;
    private boolean countryIndexSelected;
    private boolean timeIndexSelected;
    private boolean geoIndexSelected;
    private boolean requestIndexSelected;
    private boolean dataTypeIndexSelected;

    private HashMap<String, List<Value>> selectedValuesForDataType;

    public void setInactiveTime(String timeStamp) {

        pref.edit().inactiveTime().put(timeStamp).apply();
    }

    public String getInactiveTime() {

        return pref.inactiveTime().get();

    }

    public void setAuthLogin(AuthLogIn login) {
        if (login != null) {
            Gson gson = new Gson();
            pref.edit().loginJson().put(gson.toJson(login)).apply();
        } else {
            pref.edit().loginJson().remove().apply();
        }
    }

    public AuthLogIn loginStatus() {
        Gson gson = new Gson();
        return gson.fromJson(pref.loginJson().get(), AuthLogIn.class);
    }

    public void setUser(User user) {
        if (user != null) {
            Gson gson = new Gson();
            pref.edit().userJson().put(gson.toJson(user)).apply();

        } else {
            pref.edit().userJson().remove().apply();
        }
    }

    public void setUserJson(JSONObject user) {
        pref.edit().userJson().put(user.toString()).apply();
    }

    public User getUser() {
        Gson gson = new Gson();
        return gson.fromJson(pref.userJson().get(), User.class);
    }

    public void setScheme(Map<String, List<Picker>> scheme) {
        if (scheme != null) {
            Gson gson = new Gson();
            pref.edit().schemeJson().put(gson.toJson(scheme)).apply();
        } else {
            pref.edit().schemeJson().remove().apply();
        }
    }

    public Map<String, List<Picker>> getScheme() {
        Gson gson = new Gson();
        String storedHashMapString = pref.schemeJson().get();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, List<Picker>>>() {
        }.getType();
        return gson.fromJson(storedHashMapString, type);
    }

    public void setDefaultValues(Map<String, HashMap<String, DefaultValue>> scheme) {
        if (scheme != null) {
            Gson gson = new Gson();
            pref.edit().schemeDefaultJson().put(gson.toJson(scheme)).apply();
        } else {
            pref.edit().schemeDefaultJson().remove().apply();
        }
    }

    public void updateDefaultValues(int schemaIndex, String picker, DefaultValue defaultValue) {
        Map<String, HashMap<String, DefaultValue>> defaultValueListHashMap = getDefaultValues();
        HashMap<String, DefaultValue> defaultValueHashMap = defaultValueListHashMap.get("" +
                schemaIndex);
        defaultValueHashMap.put(picker, defaultValue);
        defaultValueListHashMap.put("" + schemaIndex, defaultValueHashMap);
        setDefaultValues(defaultValueListHashMap);
    }


    public Map<String, HashMap<String, DefaultValue>> getDefaultValues() {
        Gson gson = new Gson();
        String storedHashMapString = pref.schemeDefaultJson().get();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String,
                DefaultValue>>>() {
        }.getType();
        return gson.fromJson(storedHashMapString, type);
    }


    public void setDataType(JsonObject dataType) {
        if (dataType != null) {
            Gson gson = new Gson();
            pref.edit().dataTypeJson().put(gson.toJson(dataType)).apply();
        } else {
            pref.edit().dataTypeJson().remove().apply();
        }
    }

    public JsonObject getDataType() {
        Gson gson = new Gson();
        return gson.fromJson(pref.dataTypeJson().get(), JsonObject.class);
    }

    public void setDataTypeKeys(List<String> keys) {
        if (keys != null) {
            Gson gson = new Gson();
            pref.edit().dataTypeKeysJson().put(gson.toJson(keys)).apply();
        } else {
            pref.edit().dataTypeKeysJson().remove().apply();
        }
    }

    public List<String> getDataTypeKeys() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(pref.dataTypeKeysJson().get(), type);
    }

    public List<Value> getValuesFromDataType(int schemaIndex) {
        List<Value> values = new ArrayList<>();
        JsonObject jsonObject = getDataType();
        String key = "";
        if (schemaIndex == 6) {
            key = "x";
        } else if (schemaIndex < 10) {
            key = "0" + schemaIndex;
        } else {
            key = "" + schemaIndex;
        }
        if (jsonObject != null) {
            if (jsonObject.get(key) != null) {
                JsonArray jsonArray = jsonObject.get(key).getAsJsonArray();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Value>>() {
                }.getType();
                values = gson.fromJson(jsonArray, type);
            }
        }
        return values;
    }

    boolean parseInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Value> getValuesFromDataType(String key) {
        if (key.equals("6")) {
            key = "x";
        } else if (parseInteger(key)) {
            if (Integer.parseInt(key) < 10) {
                key = "0" + Integer.parseInt(key);
            } else {
                key = "" + key;
            }
        } else {
            key = key;
        }

        List<Value> values = new ArrayList<>();
        JsonObject jsonObject = getDataType();
        if (jsonObject != null) {
            if (jsonObject.get(key) != null) {
                JsonArray jsonArray = jsonObject.get(key).getAsJsonArray();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Value>>() {
                }.getType();
                values = gson.fromJson(jsonArray, type);
            }
        }
        return values;
    }

    public void setSelectedValuesForDataType(int index, List<Value> values) {

        List<String> keys = getDataTypeKeys();
        if (keys != null && keys.size() > index) {

            String key = keys.get(index);

            if (selectedValuesForDataType == null) {
                selectedValuesForDataType = new HashMap<>();
            }

            if (selectedValuesForDataType.containsKey(key)) {
                selectedValuesForDataType.remove(key);
            }

            selectedValuesForDataType.put(key, values);
        }
    }

    public void initSelectedValuesForDataType() {
        if (selectedValuesForDataType == null) {
            selectedValuesForDataType = new HashMap<>();
        }
        selectedValuesForDataType.clear();
    }

    public List<Value> getSelectedValuesForDataType(int index) {

        List<Value> values = new ArrayList<>();

        List<String> keys = getDataTypeKeys();
        if (keys != null && keys.size() > index) {

            String key = keys.get(index);

            if (selectedValuesForDataType != null && selectedValuesForDataType.containsKey(key)) {
                values = selectedValuesForDataType.get(key);
            }

        }

        return values;
    }

    public void saveDefaultValuesForDataType(Context context, int index, List<Value> values) {
        List<String> keys = getDataTypeKeys();
        if (keys != null && keys.size() > index) {
            String key = keys.get(index);
            Gson gson = new Gson();
            String jsonValues = gson.toJson(values);
            SharedPreferences preferences = context.getSharedPreferences("Authoriti", Context
                    .MODE_PRIVATE);
            preferences.edit().putString(key, jsonValues).apply();
        }
    }

    public List<Value> getDefaultValuesForDataType(Context context, int index) {

        List<Value> values = new ArrayList<>();

        List<String> keys = getDataTypeKeys();
        if (keys != null && keys.size() > index) {

            String key = keys.get(index);

            SharedPreferences preferences = context.getSharedPreferences("Authoriti", Context
                    .MODE_PRIVATE);
            String jsonValue = preferences.getString(key, "");

            if (!jsonValue.equals("")) {

                Gson gson = new Gson();
                Type type = new TypeToken<List<Value>>() {
                }.getType();

                values = gson.fromJson(jsonValue, type);
            }

        }

        return values;
    }

    public Picker getAccountPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.accountPickerJson().get(), Picker.class);
    }

    public void setAccountPicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().accountPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().accountPickerJson().remove().apply();
        }
    }

    public void setDefaultAccountID(Value value) {
        if (value != null) {
            Gson gson = new Gson();
            pref.edit().defaultAccountID().put(gson.toJson(value)).apply();
        } else {
            pref.edit().defaultAccountID().remove().apply();
        }
    }

    public Value getDefaultAccountID() {
        Gson gson = new Gson();
        Value value = gson.fromJson(pref.defaultAccountID().get(), Value.class);
        if (value == null) {
            value = new Value("", "");
        }
        return value;
    }

    public Picker getIndustryPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.industryPickerJson().get(), Picker.class);
    }

    public void setIndustryPicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().industryPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().industryPickerJson().remove().apply();
        }
    }

    public Picker getLocationPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.locationPickerJson().get(), Picker.class);
    }

    public void setLocationPicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().locationPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().locationPickerJson().remove().apply();
        }
    }

    public Picker getCountryPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.countryPickerJson().get(), Picker.class);
    }

    public void setCountryPicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().countryPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().countryPickerJson().remove().apply();
        }
    }

    public Picker getTimePicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.timePickerJson().get(), Picker.class);
    }

    public void setTimePicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().timePickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().timePickerJson().remove().apply();
        }
    }

    public Order getPickerOrder() {
        Gson gson = new Gson();
        return gson.fromJson(pref.pickerOrderJson().get(), Order.class);
    }

    public void setPickerOrder(Order order) {
        if (order != null) {
            Gson gson = new Gson();
            pref.edit().pickerOrderJson().put(gson.toJson(order)).apply();
        } else {
            pref.edit().pickerOrderJson().remove().apply();
        }
    }

    public Order getPickerOrder2() {
        Gson gson = new Gson();
        return gson.fromJson(pref.pickerOrder2Json().get(), Order.class);
    }

    public void setPickerOrder2(Order order) {
        if (order != null) {
            Gson gson = new Gson();
            pref.edit().pickerOrder2Json().put(gson.toJson(order)).apply();
        } else {
            pref.edit().pickerOrder2Json().remove().apply();
        }
    }

    public Picker getGeoPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.getPickerJson().get(), Picker.class);
    }

    public void setGeoPicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().getPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().getPickerJson().remove().apply();
        }
    }

    public Picker getRequestPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.requestPickerJson().get(), Picker.class);
    }

    public void setRequestPicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().requestPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().requestPickerJson().remove().apply();
        }
    }

    public Picker getDataTypePicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.dataTypePickerJson().get(), Picker.class);
    }

    public void setDataTypePicker(Picker picker) {
        if (picker != null) {
            Gson gson = new Gson();
            pref.edit().dataTypePickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().dataTypePickerJson().remove().apply();
        }
    }

    public int getSelectedAccountIndex() {
        return selectedAccountIndex;
    }

    public void setSelectedAccountIndex(int selectedAccountIndex) {
        this.selectedAccountIndex = selectedAccountIndex;
    }

    public int getSelectedIndustryIndex() {
        return selectedIndustryIndex;
    }

    public void setSelectedIndustryIndex(int selectedIndustryIndex) {
        this.selectedIndustryIndex = selectedIndustryIndex;
    }

    public int getSelectedCountryIndex() {
        return selectedCountryIndex;
    }

    public void setSelectedCountryIndex(int selectedCountryIndex) {
        this.selectedCountryIndex = selectedCountryIndex;
    }

    public int getSelectedTimeIndex() {
        return selectedTimeIndex;
    }

    public void setSelectedTimeIndex(int selectedTimeIndex) {
        this.selectedTimeIndex = selectedTimeIndex;
    }

    public boolean isAccountIndexSelected() {
        return accountIndexSelected;
    }

    public void setAccountIndexSelected(boolean accountIndexSelected) {
        this.accountIndexSelected = accountIndexSelected;
    }

    public boolean isIndustryIndexSelected() {
        return industryIndexSelected;
    }

    public void setIndustryIndexSelected(boolean industryIndexSelected) {
        this.industryIndexSelected = industryIndexSelected;
    }

    public boolean isCountryIndexSelected() {
        return countryIndexSelected;
    }

    public void setCountryIndexSelected(boolean countryIndexSelected) {
        this.countryIndexSelected = countryIndexSelected;
    }

    public boolean isTimeIndexSelected() {
        return timeIndexSelected;
    }

    public void setTimeIndexSelected(boolean timeIndexSelected) {
        this.timeIndexSelected = timeIndexSelected;
    }

    public void wipeSetting(Context context) {

        setUser(null);

        setScheme(null);

        setAccountPicker(null);
        setIndustryPicker(null);
        setLocationPicker(null);
        setCountryPicker(null);
        setTimePicker(null);
        setPickerOrder(null);
        setGeoPicker(null);
        setRequestPicker(null);
        setDataTypePicker(null);

        accountIDs = null;
        defaultAccountSelected = false;
        defaultAccountIndex = 0;

        setSelectedAccountIndex(0);
        setSelectedIndustryIndex(0);
        setSelectedCountryIndex(0);
        setSelectedTimeIndex(0);
        setSelectedGeoIndex(0);
        setSelectedRequestIndex(0);
        setSelectedDataTypeIndex(0);

        setAccountIndexSelected(false);
        setIndustryIndexSelected(false);
        setCountryIndexSelected(false);
        setTimeIndexSelected(false);
        setGeoIndexSelected(false);
        setRequestIndexSelected(false);
        setDataTypeIndexSelected(false);

        SharedPreferences preferences = context.getSharedPreferences("Authoriti", Context
                .MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public CryptoKeyPair getCryptoKeyPair(String password, String salt) {
        Crypto crypto = new Crypto();
        return crypto.generateKeyPair(password, salt);

    }

    public String getValidString(String origin) {

        return origin.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

    }

    public void setDefaultPurposeIndex(int index) {

        pref.edit().defaultPurposeIndex().put(String.valueOf(index)).apply();
    }

    public int getDefaultPurposeIndex() {

        String index = pref.defaultPurposeIndex().getOr("-1");

        return Integer.parseInt(index);
    }

    public void setPurposes(List<Purpose> purposes) {
        if (purposes != null) {
            Gson gson = new Gson();
            pref.edit().purposesJson().put(gson.toJson(purposes)).apply();
        } else {
            pref.edit().purposesJson().remove().apply();
        }
    }

    public List<Purpose> getPurposes() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Purpose>>() {
        }.getType();
        return gson.fromJson(pref.purposesJson().get(), type);
    }

    public boolean isGeoIndexSelected() {
        return geoIndexSelected;
    }

    public void setGeoIndexSelected(boolean geoIndexSelected) {
        this.geoIndexSelected = geoIndexSelected;
    }

    public boolean isRequestIndexSelected() {
        return requestIndexSelected;
    }

    public void setRequestIndexSelected(boolean requestIndexSelected) {
        this.requestIndexSelected = requestIndexSelected;
    }

    public boolean isDataTypeIndexSelected() {
        return dataTypeIndexSelected;
    }

    public void setDataTypeIndexSelected(boolean dataTypeIndexSelected) {
        this.dataTypeIndexSelected = dataTypeIndexSelected;
    }

    public int getSelectedGeoIndex() {
        return selectedGeoIndex;
    }

    public void setSelectedGeoIndex(int selectedGeoIndex) {
        this.selectedGeoIndex = selectedGeoIndex;
    }

    public int getSelectedRequestIndex() {
        return selectedRequestIndex;
    }

    public void setSelectedRequestIndex(int selectedRequestIndex) {
        this.selectedRequestIndex = selectedRequestIndex;
    }

    public int getSelectedDataTypeIndex() {
        return selectedDataTypeIndex;
    }

    public void setSelectedDataTypeIndex(int selectedDataTypeIndex) {
        this.selectedDataTypeIndex = selectedDataTypeIndex;
    }
}
