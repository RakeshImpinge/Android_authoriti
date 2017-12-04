package com.curtisdigital.authoriti.utils;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by mac on 12/1/17.
 */

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface AuthoritiPref {

    String schemeJson();
    String accountPickerJson();
    String industryPickerJson();
    String locationPickerJson();
    String timePickerJson();
}
