package com.curtisdigital.authoriti.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
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

    public Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

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

    public int getPickerDefaultIndex(Context context, String identifier){

        int index = 0;

        switch (identifier){
            case PICKER_ACCOUNT:
                index = AuthoritiData_.getInstance_(context).getAccountPicker().getDefaultIndex();
                break;
            case PICKER_INDUSTRY:
                index = AuthoritiData_.getInstance_(context).getIndustryPicker().getDefaultIndex();
                break;
            case PICKER_LOCATION_STATE:
                index = AuthoritiData_.getInstance_(context).getLocationPicker().getDefaultIndex();
                break;
            case PICKER_TIME:
                index = AuthoritiData_.getInstance_(context).getTimePicker().getDefaultIndex();;
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

    public void setSelectedPickerIndex(Context context, String identifier, int index){

        AuthoritiData dataManager = AuthoritiData_.getInstance_(context);

        switch (identifier){
            case PICKER_ACCOUNT:
                dataManager.setSelectedAccountIndex(index);
                break;
            case PICKER_INDUSTRY:
                dataManager.setSelectedIndustryIndex(index);
                break;
            case PICKER_LOCATION_STATE:
                dataManager.setSelectedCountryIndex(index);
                break;
            case PICKER_TIME:
                dataManager.setSelectedTimeIndex(index);
                break;
            default:

                break;
        }

    }

    public void setDefaultPickerItemIndex(Context context, String identifier, int index){

        AuthoritiData dataManager = AuthoritiData_.getInstance_(context);
        Picker picker = null;

        switch (identifier){
            case PICKER_ACCOUNT:
                picker = dataManager.getAccountPicker();
                picker.setEnableDefault(true);
                picker.setDefaultIndex(index);
                dataManager.setAccountPicker(picker);
                break;
            case PICKER_INDUSTRY:
                picker = dataManager.getIndustryPicker();
                picker.setEnableDefault(true);
                picker.setDefaultIndex(index);
                dataManager.setIndustryPicker(picker);
                break;
            case PICKER_LOCATION_STATE:
                picker = dataManager.getLocationPicker();
                picker.setEnableDefault(true);
                picker.setDefaultIndex(index);
                dataManager.setLocationPicker(picker);
                break;
            case PICKER_TIME:
                picker = dataManager.getTimePicker();
                picker.setEnableDefault(true);
                picker.setDefaultIndex(index);
                dataManager.setTimePicker(picker);
                break;
            default:

                break;
        }

    }

    public void setIndexSelected(Context context, String identifier, boolean selected){

        AuthoritiData dataManager = AuthoritiData_.getInstance_(context);

        switch (identifier){
            case PICKER_ACCOUNT:
                dataManager.setAccountIndexSelected(selected);
                break;
            case PICKER_INDUSTRY:
                dataManager.setIndustryIndexSelected(selected);
                break;
            case PICKER_LOCATION_STATE:
                dataManager.setCountryIndexSelected(selected);
                break;
            case PICKER_TIME:
                dataManager.setTimeIndexSelected(selected);
                break;
            default:
                break;
        }

    }

    public boolean presentSelectedIndex(Context context, String identifier){

        boolean selected;
        AuthoritiData dataManager = AuthoritiData_.getInstance_(context);

        switch (identifier){
            case PICKER_ACCOUNT:
                selected = dataManager.isAccountIndexSelected();
                break;
            case PICKER_INDUSTRY:
                selected = dataManager.isIndustryIndexSelected();
                break;
            case PICKER_LOCATION_STATE:
                selected = dataManager.isCountryIndexSelected();
                break;
            case PICKER_TIME:
                selected = dataManager.isTimeIndexSelected();
                break;
            default:
                selected = false;
                break;
        }

        return selected;

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
