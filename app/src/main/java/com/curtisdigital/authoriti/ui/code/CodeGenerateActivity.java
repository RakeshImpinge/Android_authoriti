package com.curtisdigital.authoriti.ui.code;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.crypto.Crypto;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.glxn.qrgen.android.QRCode;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by mac on 12/2/17.
 */

@EActivity(R.layout.activity_code_generate)
public class CodeGenerateActivity extends BaseActivity {

    @Bean
    AuthoritiData dataManager;

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.ivQRCode)
    ImageView ivQRCode;

    @ViewById(R.id.tvCode)
    TextView tvCode;

    @AfterViews
    void callAfterViewInjection(){

        ivQRCode.setImageBitmap(QRCode.from(generateCode()).bitmap());
        tvCode.setText(generateCode());

    }

    private String generateInitialPayload(){

        StringBuilder payload = new StringBuilder();

        if (dataManager.getPickerOrder() != null && dataManager.getPickerOrder().getPickers() != null && dataManager.getPickerOrder().getPickers().size() > 0){

            for (String picker : dataManager.getPickerOrder().getPickers()){

                switch (picker){

                    case PICKER_ACCOUNT:

                        payload.append(accountPayload());

                        break;

                    case PICKER_INDUSTRY:

                        if (dataManager.getIndustryPicker() != null){

                            payload.append(getIndividualPayload(dataManager.getIndustryPicker()));

                        }

                        break;

                    case PICKER_LOCATION_COUNTRY:

                        payload.append(countryPayload());

                        break;

                    case PICKER_LOCATION_STATE:

                        if (dataManager.getLocationPicker() != null){


                            payload.append(getIndividualPayload(dataManager.getLocationPicker()));

                        }

                        break;

                    case PICKER_TIME:

                        if (dataManager.getTimePicker() != null){

                            payload.append(timePayload(dataManager.getTimePicker()));
                        }

                        break;


                }

            }

        }

        Log.e("Initial Payload", payload.toString());

        return payload.toString();

    }

    private String generatePayload(){

        String payload = "";

        Value value = getValueForPicker(dataManager.getAccountPicker());
        String trimValue = dataManager.getValidString(value.getValue());

        Crypto crypto = new Crypto();

        payload = crypto.addAccountNumberToPayload(generateInitialPayload(), trimValue);

        Log.e("Payload", payload);

        return payload;

    }

    private String generateCode(){

        String code = "";

        Crypto crypto = new Crypto();

        AesCbcWithIntegrity.SecretKeys keys;
        String keyStr = dataManager.getUser().getEncryptKey();

        try {

            keys = AesCbcWithIntegrity.keys(keyStr);
            AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity.CipherTextIvMac(dataManager.getUser().getEncryptPrivateKey());

            try {

                String privateKey = AesCbcWithIntegrity.decryptString(civ, keys);
                code = crypto.sign(generatePayload(), privateKey);

                Log.e("Code", code);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


        return code;
    }

    private String accountPayload(){

        return "1";
    }

    private String getIndividualPayload(Picker picker){

        Value value = getValueForPicker(picker);

        return value.getValue();

    }

    private String timePayload(Picker picker){

        String timePayload = "";
        Calendar newCalendar = Calendar.getInstance();

        int day = 0;
        int hour = 0;
        int minute = 0;

        long minutes;

        Value value = getValueForPicker(picker);

        switch (value.getTitle()){

            case TIME_2_DAYS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + 2);

                break;

            case TIME_5_DAYS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + 5);
                break;

            case TIME_2_WEEKS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + 14);
                break;

            case  TIME_1_MONTH:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH) + 1, newCalendar.get(Calendar.DAY_OF_MONTH));
                break;

            case TIME_CUSTOM_TIME:

                minutes = Long.parseLong(value.getValue());

                day = (int) (minutes / (24 * 60));
                hour = (int) (minutes % (24 * 60)/ 60);
                minute = (int) (minutes % (24 * 60) % 60);

                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + day, newCalendar.get(Calendar.HOUR_OF_DAY) + hour, newCalendar.get(Calendar.MINUTE) + minute);

                break;

            case TIME_CUSTOM_DATE:

                minutes = Long.parseLong(value.getValue());

                day = (int) (minutes / (24 * 60));
                hour = (int) (minutes % (24 * 60)/ 60);
                minute = (int) (minutes % (24 * 60) % 60);

                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + day, newCalendar.get(Calendar.HOUR_OF_DAY) + hour, newCalendar.get(Calendar.MINUTE) + minute);

                break;

        }

        Log.e("HOUR", String.valueOf(newCalendar.get(Calendar.HOUR_OF_DAY)));

        newCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        Log.e("HOUR", String.valueOf(newCalendar.get(Calendar.HOUR_OF_DAY)));

        Crypto crypto = new Crypto();
        try {
            timePayload = crypto.getTimeString(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timePayload;

    }

    private String countryPayload(){

        return "1";
    }

    private Value getValueForPicker(Picker picker){
        int selectedIndex;

        if (utils.presentSelectedIndex(this, picker.getPicker())) {

            selectedIndex = utils.getPickerSelectedIndex(this, picker.getPicker());

        } else {

            if (picker.isEnableDefault() && picker.getDefaultIndex() != -1){

                selectedIndex = utils.getPickerDefaultIndex(this, picker.getPicker());

            } else {

                selectedIndex = utils.getPickerSelectedIndex(this, picker.getPicker());

            }
        }

        return  picker.getValues().get(selectedIndex);
    }

    @Click({R.id.ivClose, R.id.cvGotIt})
    void closeButtonClicked(){
        finish();
    }

}
