package com.curtisdigital.authoriti.ui.code;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Order;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Purpose;
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
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by mac on 12/2/17.
 */

@EActivity(R.layout.activity_code_generate)
public class CodeGenerateActivity extends BaseActivity {

    private Order order;;
    private Purpose purpose;
    private Crypto crypto;

    @Extra
    int purposeIndex;

    @Extra
    String codeExtra = "";

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

        purpose = dataManager.getPurposes().get(purposeIndex);
        if (purpose.getSchemaIndex() == 1){
            order = dataManager.getPickerOrder();
        } else if (purpose.getSchemaIndex() == 2){
            order = dataManager.getPickerOrder2();
        }

        crypto = new Crypto();

        String code = generateCode();

        ivQRCode.setImageBitmap(QRCode.from(code).bitmap());
        tvCode.setText(utils.fromHtml(generateHTMLString(code)));

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("permission code", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this,"Code copied to clipboard", Toast.LENGTH_SHORT).show();

    }

    private String generateHTMLString(String code){

        StringBuilder html = new StringBuilder();


        for (int i = 0 ; i < code.length() ; i ++){

            char c = code.charAt(i);

            if (Character.isDigit(c)){

                html.append("<font color='#DB381B'>").append(c).append("</font>");

            } else {

                html.append("<font color='##465156'>").append(c).append("</font>");

            }

        }

        return html.toString();
    }

    private String generateInitialPayload(){

        StringBuilder payload = new StringBuilder();

        if (order != null && order.getPickers() != null && order.getPickers().size() > 0){

            for (String picker : order.getPickers()){

                switch (picker){

                    case PICKER_ACCOUNT:

                        payload.append(accountPayload());

                        break;

                    case PICKER_INDUSTRY:

                        payload.append(industryPayload());

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

                    case PICKER_REQUEST:

                        if (dataManager.getRequestPicker() != null){

                            payload.append(getIndividualPayload(dataManager.getRequestPicker()));
                        }

                        break;

                    case PICKER_GEO:

                        if (dataManager.getGeoPicker() != null){

                            String tempPayload = crypto.encodeGeo(getIndividualPayload(dataManager.getGeoPicker()), payload.toString());
                            payload = new StringBuilder(tempPayload);
                        }
                        break;

                    case PICKER_DATA_TYPE:

                        if (dataManager.getDataTypePicker() != null){

                            String tempPayload = crypto.encodeDataTypes(dataTypePayload(), payload.toString());
                            payload = new StringBuilder(tempPayload);
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

        if (purpose.getSchemaIndex() == 2){
            payload = crypto.addIdentifierToAccountId(dataManager.getValidString(codeExtra), trimValue);
            payload = crypto.addAccountNumberToPayload(generateInitialPayload(), payload);
        } else {
            payload = crypto.addAccountNumberToPayload(generateInitialPayload(), trimValue);

        }

        Log.e("Payload", payload);

        return payload;

    }

    private String generateCode(){

        String code = "";

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

    private String industryPayload(){

        if (purpose.getPickerName() != null && purpose.getValue() != null){

            return purpose.getValue();

        } else {

            if (dataManager.getIndustryPicker() != null){

                return getIndividualPayload(dataManager.getIndustryPicker());

            } else {
                return "";
            }

        }
    }

    private String dataTypePayload(){

        List<Value> values = dataManager.getDataType().getType(utils.getPickerSelectedIndex(this, PICKER_REQUEST));

        StringBuilder bitMask = new StringBuilder();

        if (values != null){

            for (Value value : values){

                if (checkDataType(value)){
                    bitMask.append("1");
                } else {
                    bitMask.append("0");
                }
            }

        }

        Log.e("DataType Payload - ", bitMask.toString());

        return bitMask.toString();
    }

    private boolean checkDataType(Value value){

        List<Value> selectedValues = dataManager.getDataType().getSelectedValues();
        if (selectedValues != null){

            for (Value value1 : selectedValues){

                if (value.getValue().equals(value1.getValue()) && value.getTitle().equals(value1.getTitle())){
                    return true;
                }
            }

        }

        return false;
    }

    private String getIndividualPayload(Picker picker){

        Value value = getValueForPicker(picker);

        Log.e("Selected Value - ", value.getTitle());
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

            case TIME_15_MINS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE) + 15);

                break;

            case TIME_1_HOUR:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar.HOUR_OF_DAY) + 1, newCalendar.get(Calendar.MINUTE));
                break;

            case TIME_4_HOURS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar.HOUR_OF_DAY) + 4, newCalendar.get(Calendar.MINUTE));
                break;

            case  TIME_1_DAY:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + 1, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE));
                break;

            case  TIME_1_WEEK:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH) + 7, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE));
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
