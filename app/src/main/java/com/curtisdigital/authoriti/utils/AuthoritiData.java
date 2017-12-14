package com.curtisdigital.authoriti.utils;

import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Scheme;
import com.curtisdigital.authoriti.api.model.User;
import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

@EBean(scope = EBean.Scope.Singleton)
public class AuthoritiData {

    @Pref
    AuthoritiPref_ pref;

    // Auth Processing Temp Data
    public String inviteCode;
    public String password;
    public String key;
    public String salt;
    public List<AccountID> accountIDs;
    public List<String> accountsChase;


    private int selectedAccountIndex;
    private int selectedIndustryIndex;
    private int selectedCountryIndex;
    private int selectedTimeIndex;

    private int preSelectedAccountIndex;
    private int preSelectedIndustryIndex;
    private int preSelectedCountryIndex;
    private int preSelectedTimeIndex;

    public void setUser(User user){
        if (user != null){
            Gson gson = new Gson();
            pref.edit().userJson().put(gson.toJson(user)).apply();

        } else {
            pref.edit().userJson().remove().apply();
        }
    }

    public User getUser(){
        Gson gson = new Gson();
        return gson.fromJson(pref.userJson().get(), User.class);
    }

    public void setScheme(Scheme scheme){
        if (scheme != null){
            Gson gson = new Gson();
            pref.edit().schemeJson().put(gson.toJson(scheme)).apply();
        } else {
            pref.edit().schemeJson().remove().apply();
        }
    }

    public Scheme getScheme(){
        Gson gson = new Gson();
        return gson.fromJson(pref.schemeJson().get(), Scheme.class);
    }

    public Picker getAccountPicker(){
        Gson gson = new Gson();
        return gson.fromJson(pref.accountPickerJson().get(), Picker.class);
    }

    public void setAccountPicker(Picker picker) {
        if (picker != null){
            Gson gson = new Gson();
            pref.edit().accountPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().accountPickerJson().remove().apply();
        }
    }

    public Picker getIndustryPicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.industryPickerJson().get(), Picker.class);
    }

    public void setIndustryPicker(Picker picker) {
        if (picker != null){
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
        if (picker != null){
            Gson gson = new Gson();
            pref.edit().locationPickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().locationPickerJson().remove().apply();
        }
    }

    public Picker getTimePicker() {
        Gson gson = new Gson();
        return gson.fromJson(pref.timePickerJson().get(), Picker.class);
    }

    public void setTimePicker(Picker picker) {
        if (picker != null){
            Gson gson = new Gson();
            pref.edit().timePickerJson().put(gson.toJson(picker)).apply();
        } else {
            pref.edit().timePickerJson().remove().apply();
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

    public int getPreSelectedAccountIndex() {
        return preSelectedAccountIndex;
    }

    public void setPreSelectedAccountIndex(int preSelectedAccountIndex) {
        this.preSelectedAccountIndex = preSelectedAccountIndex;
    }

    public int getPreSelectedIndustryIndex() {
        return preSelectedIndustryIndex;
    }

    public void setPreSelectedIndustryIndex(int preSelectedIndustryIndex) {
        this.preSelectedIndustryIndex = preSelectedIndustryIndex;
    }

    public int getPreSelectedCountryIndex() {
        return preSelectedCountryIndex;
    }

    public void setPreSelectedCountryIndex(int preSelectedCountryIndex) {
        this.preSelectedCountryIndex = preSelectedCountryIndex;
    }

    public int getPreSelectedTimeIndex() {
        return preSelectedTimeIndex;
    }

    public void setPreSelectedTimeIndex(int preSelectedTimeIndex) {
        this.preSelectedTimeIndex = preSelectedTimeIndex;
    }
}
