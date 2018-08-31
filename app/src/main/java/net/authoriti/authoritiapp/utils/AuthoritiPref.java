package net.authoriti.authoritiapp.utils;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by mac on 12/1/17.
 */

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface AuthoritiPref {

    String loginJson();
    String userJson();
    String schemeJson();
    String schemeDefaultJson();
    String accountPickerJson();
    String industryPickerJson();
    String locationPickerJson();
    String countryPickerJson();
    String timePickerJson();
    String pickerOrderJson();
    String inactiveTime();

    String purposesJson();
    String defaultPurposeIndex();

    String pickerOrder2Json();
    String getPickerJson();
    String requestPickerJson();
    String dataTypePickerJson();
    String dataTypeJson();
    String dataTypeKeysJson();
}
