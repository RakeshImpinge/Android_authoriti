package com.curtisdigital.authoriti.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;

import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Value;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 12/1/17.
 */

@EBean(scope = EBean.Scope.Singleton)
public class AuthoritiUtils implements Constants{

    public String getPickerTitle(String identifier){

        String title = "";

        switch (identifier){
            case PICKER_ACCOUNT:
                title = "AccountId : ";
                break;
            case PICKER_INDUSTRY:
                title = "Industry : ";
                break;
            case PICKER_LOCATION_STATE:
                title = "Location : ";
                break;
            case PICKER_TIME:
                title = "Time : ";
                break;
            default:
                title = "";
                break;

        }

        return title;

    }

    public int getPickerSelectedIndex(Context context, String identifier){

        int index = 0;

        switch (identifier){
            case PICKER_ACCOUNT:
                index = AuthoritiData_.getInstance_(context).getSelectedAccountIndex();
                break;
            case PICKER_INDUSTRY:
                index = AuthoritiData_.getInstance_(context).getSelectedIndustryIndex();
                break;
            case PICKER_LOCATION_STATE:
                index = AuthoritiData_.getInstance_(context).getSelectedCountryIndex();
                break;
            case PICKER_TIME:
                index = AuthoritiData_.getInstance_(context).getSelectedTimeIndex();
                break;
            default:
                index = 0;
                break;
        }

        return index;
    }

    public int getPickerPreSelectedIndex(Context context, String identifier){

        int index = 0;

        switch (identifier){
            case PICKER_ACCOUNT:
                index = AuthoritiData_.getInstance_(context).getPreSelectedAccountIndex();
                break;
            case PICKER_INDUSTRY:
                index = AuthoritiData_.getInstance_(context).getPreSelectedIndustryIndex();
                break;
            case PICKER_LOCATION_STATE:
                index = AuthoritiData_.getInstance_(context).getPreSelectedCountryIndex();
                break;
            case PICKER_TIME:
                index = AuthoritiData_.getInstance_(context).getPreSelectedTimeIndex();
                break;
            default:
                index = 0;
                break;
        }

        return index;
    }

    public Picker getPicker(Context context, String identifier){

        Picker picker = null;

        switch (identifier){
            case PICKER_ACCOUNT:
                picker = AuthoritiData_.getInstance_(context).getAccountPicker();
                break;
            case PICKER_INDUSTRY:
                picker = AuthoritiData_.getInstance_(context).getIndustryPicker();
                break;
            case PICKER_LOCATION_STATE:
                picker = AuthoritiData_.getInstance_(context).getLocationPicker();
                break;
            case PICKER_TIME:
                picker = AuthoritiData_.getInstance_(context).getTimePicker();
                break;
            default:
                picker = null;
                break;
        }

        return picker;
    }

    public Picker getDefaultTimePicker(Picker picker){

        Picker timePicker = new Picker();

        timePicker.setPicker(picker.getPicker());
        timePicker.setBytes(picker.getBytes());
        timePicker.setTitle(picker.getTitle());

        List<Value> values = new ArrayList<>();
        values.add(new Value("", "2 Days"));
        values.add(new Value("", "5 Days"));
        values.add(new Value("", "2 Weeks"));
        values.add(new Value("", "1 Month"));
        values.add(new Value("", "Custom"));

        timePicker.setValues(values);

        return timePicker;
    }

    public void addValueToAccountPicker(Context context, Value value){

        Picker picker = AuthoritiData_.getInstance_(context).getAccountPicker();
        picker.getValues().add(value);

        AuthoritiData_.getInstance_(context).setAccountPicker(picker);
    }

    public SpannableString getSpannableStringForEditTextError(String error, Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Oswald_Regular.ttf");
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan("", typeface);
        SpannableString spannableString = new SpannableString(error);
        spannableString.setSpan(typefaceSpan, 0, error.length(), 0);
        return spannableString;
    }
}
