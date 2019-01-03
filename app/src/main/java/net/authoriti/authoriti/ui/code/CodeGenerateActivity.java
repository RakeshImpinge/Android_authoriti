package net.authoriti.authoriti.ui.code;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.request.RequestComplete;
import net.authoriti.authoriti.api.model.response.ResponseComplete;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.crypto.Crypto;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/2/17.
 */

@EActivity(R.layout.activity_code_generate)
public class CodeGenerateActivity extends BaseActivity {

    public static final int CODE = 234;
    private Crypto crypto;

    @Extra
    String schemaIndex = "";

    @Extra
    boolean isPollingRequest = false;

    @Extra
    ArrayList<HashMap<String, String>> finalPickersList = new ArrayList<>();

    @Bean
    AuthoritiData dataManager;

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.ivQRCode)
    ImageView ivQRCode;

    @ViewById(R.id.tvCode)
    TextView tvCode;

    String userIndentifier = "";

    @AfterViews
    void callAfterViewInjection() {
        String code = generateCode();

        int width = ivQRCode.getMeasuredWidth();
        int height = ivQRCode.getMeasuredHeight();
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        ivQRCode.setImageBitmap(QRCode.from(code).withSize(800, 800).bitmap());

        tvCode.setText(utils.fromHtml(generateHTMLString(code)));
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("permission code", code);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show();

        if (isPollingRequest) {
            completePollingRequest(userIndentifier, code);
        }
    }

    private void completePollingRequest(String accountID, String permissionCode) {
        RequestComplete requestComplete = new RequestComplete(accountID, permissionCode);
        AuthoritiAPI.APIService().completePollingRequest(requestComplete).enqueue
                (new Callback<ResponseComplete>() {
                    @Override
                    public void onResponse(Call<ResponseComplete> call,
                                           Response<ResponseComplete>
                                                   response) {
                        if (response.isSuccessful()) {
                            dismissProgressDialog();
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComplete> call, Throwable t) {
                        dismissProgressDialog();
                    }
                });
    }


    private String generateHTMLString(String code) {
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (Character.isDigit(c)) {
                html.append("<font color='#DB381B'>").append(c).append("</font>");
            } else {
                html.append("<font color='##465156'>").append(c).append("</font>");
            }
        }
        return html.toString();
    }

    private String generateCode() {

        AesCbcWithIntegrity.SecretKeys keys;
        String keyStr = dataManager.getUser().getEncryptKey();
        String privateKey = "";
        try {
            keys = AesCbcWithIntegrity.keys(keyStr);
            AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity.CipherTextIvMac
                    (dataManager.getUser().getEncryptPrivateKey());
            privateKey = AesCbcWithIntegrity.decryptString(civ, keys);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        crypto = new Crypto();
        Crypto.PayloadGenerator payloadGenerator = null;
        for (HashMap<String, String> hashMap : finalPickersList) {
            // Skip if key is blank
            if (hashMap.get("key").equals("")) continue;
            String key_root = hashMap.get("picker");
            if (key_root.equals(PICKER_ACCOUNT)) {
                payloadGenerator = crypto.init(hashMap.get("value"), schemaIndex, privateKey);
                userIndentifier = hashMap.get("value");
            } else if (key_root.equals(PICKER_TIME)) {
                Calendar newCalendar = timeFormat(hashMap.get("value"));
                try {
                    payloadGenerator.addTime(newCalendar.getTime().getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (key_root.equals(PICKER_DATA_TYPE)) {
                String data[] = hashMap.get("value").split("\\s*,\\s*");
                payloadGenerator.addDataType(Integer.valueOf(hashMap.get("key")), data);
            } else if (key_root.equals(PICKER_DATA_INPUT_TYPE)) {
                payloadGenerator.addInput(hashMap.get("key"), hashMap.get("value"));
            } else if (key_root.equals(PICKER_REQUEST)) {
                payloadGenerator.add(key_root, hashMap.get("value"));
            } else {
                payloadGenerator.add(key_root, hashMap.get("value"));
            }
        }

        final String code = payloadGenerator.generate();

//        code = crypto.sign(payload, privateKey);

        return code;
    }

    private Calendar timeFormat(String value) {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        int day = 0;
        int hour = 0;
        int minute = 0;
        long minutes;
        switch (value) {

            case TIME_15_MINS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar
                                .HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE) + 15);

                break;

            case TIME_1_HOUR:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar
                                .HOUR_OF_DAY) + 1, newCalendar.get(Calendar.MINUTE));
                break;

            case TIME_4_HOURS:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH), newCalendar.get(Calendar
                                .HOUR_OF_DAY) + 4, newCalendar.get(Calendar.MINUTE));
                break;

            case TIME_1_DAY:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH) + 1, newCalendar.get(Calendar
                                .HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE));
                break;

            case TIME_1_WEEK:
                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH) + 7, newCalendar.get(Calendar
                                .HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE));
                break;

            case TIME_CUSTOM_TIME:

                minutes = Long.parseLong(value);

                day = (int) (minutes / (24 * 60));
                hour = (int) (minutes % (24 * 60) / 60);
                minute = (int) (minutes % (24 * 60) % 60);

                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH) + day, newCalendar.get(Calendar
                                .HOUR_OF_DAY) + hour, newCalendar.get(Calendar.MINUTE) + minute);

                break;

            case TIME_CUSTOM_DATE:

                minutes = Long.parseLong(value);

                day = (int) (minutes / (24 * 60));
                hour = (int) (minutes % (24 * 60) / 60);
                minute = (int) (minutes % (24 * 60) % 60);

                newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH) + day, newCalendar.get(Calendar
                                .HOUR_OF_DAY) + hour, newCalendar.get(Calendar.MINUTE) + minute);

                break;

        }
//        newCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Log.i("PAYLOAD_GENERATOR: ", "Expires At: " + newCalendar.getTime().getTime());
        return newCalendar;
    }

    @Click({R.id.ivClose, R.id.cvGotIt})
    void closeButtonClicked() {
        finish();
        Log.e("GoBack", "from here");
    }

//
//    private String generateInitialPayload() {
//
//        StringBuilder payload = new StringBuilder();
//
//        if (order != null && order.getPickers() != null && order.getPickers().size() > 0) {
//
//            for (String picker : order.getPickers()) {
//
//                switch (picker) {
//
//                    case PICKER_ACCOUNT:
//
//                        payload.append(accountPayload());
//
//                        break;
//
//                    case PICKER_INDUSTRY:
//
//                        payload.append(industryPayload());
//
//                        break;
//
//                    case PICKER_LOCATION_COUNTRY:
//
//                        payload.append(countryPayload());
//
//                        break;
//
//                    case PICKER_LOCATION_STATE:
//
//                        if (dataManager.getLocationPicker() != null) {
//
//
//                            payload.append(getIndividualPayload(dataManager.getLocationPicker()));
//
//                        }
//
//                        break;
//
//                    case PICKER_TIME:
//
//                        if (dataManager.getTimePicker() != null) {
//
//                            payload.append(timePayload(dataManager.getTimePicker()));
//                        }
//
//                        break;
//
//                    case PICKER_REQUEST:
//
//                        if (dataManager.getRequestPicker() != null) {
//
//                            payload.append(getIndividualPayload(dataManager.getRequestPicker()));
//                        }
//
//                        break;
//
//                    case PICKER_GEO:
//
//                        if (dataManager.getGeoPicker() != null) {
//
//                            String tempPayload = crypto.encodeGeo(getIndividualPayload
//                                    (dataManager.getGeoPicker()), payload.toString());
//                            payload = new StringBuilder(tempPayload);
//                        }
//                        break;
//
//                    case PICKER_DATA_TYPE:
//
//                        if (dataManager.getDataTypePicker() != null) {
//
//                            String tempPayload = crypto.encodeDataTypes(dataTypePayload(),
//                                    payload.toString());
//                            payload = new StringBuilder(tempPayload);
//                        }
//
//                        break;
//                }
//
//            }
//
//        }
//
//        Log.e("Initial Payload", payload.toString());
//
//        return payload.toString();
//
//    }
//
//    private String generatePayload() {
//
//        String payload = "";
//
//        Value value = getValueForPicker(dataManager.getAccountPicker());
//        String trimValue = dataManager.getValidString(value.getValue());
//
//        if (purpose.getSchemaIndex() == 2) {
//            payload = crypto.addIdentifierToAccountId(dataManager.getValidString(codeExtra),
//                    trimValue);
//            payload = crypto.addAccountNumberToPayload(generateInitialPayload(), payload);
//        } else {
//            payload = crypto.addAccountNumberToPayload(generateInitialPayload(), trimValue);
//
//        }
//
//        Log.e("Payload", payload);
//
//        return payload;
//
//    }
//
//    private String generateCode() {
//
//        String code = "";
//
//        AesCbcWithIntegrity.SecretKeys keys;
//        String keyStr = dataManager.getUser().getEncryptKey();
//
//        try {
//
//            keys = AesCbcWithIntegrity.keys(keyStr);
//            AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity.CipherTextIvMac
//                    (dataManager.getUser().getEncryptPrivateKey());
//
//            try {
//
//                String privateKey = AesCbcWithIntegrity.decryptString(civ, keys);
//                code = crypto.sign(generatePayload(), privateKey);
//
//                Log.e("Code", code);
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (GeneralSecurityException e) {
//                e.printStackTrace();
//            }
//
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        }
//
//
//        return code;
//    }
//
//    private String accountPayload() {
//        return String.valueOf(purpose.getSchemaIndex());
//    }
//
//    private String industryPayload() {
//        if (purpose.getPickerName() != null && purpose.getValue() != null) {
//            return purpose.getValue();
//        } else {
//            if (dataManager.getIndustryPicker() != null) {
//                return getIndividualPayload(dataManager.getIndustryPicker());
//            } else {
//                return "";
//            }
//        }
//    }
//
//    private String dataTypePayload() {
//
//        List<Value> values = dataManager.getValuesFromDataType(utils.getPickerSelectedIndex(this,
//                PICKER_REQUEST));
//
//        StringBuilder bitMask = new StringBuilder();
//
//        if (values != null) {
//
//            for (Value value : values) {
//
//                if (checkDataType(value)) {
//                    bitMask.append("1");
//                } else {
//                    bitMask.append("0");
//                }
//            }
//
//        }
//
//        Log.e("DataType Payload - ", bitMask.toString());
//
//        return bitMask.toString();
//    }
//
//    private boolean checkDataType(Value value) {
//
//        List<Value> selectedValues = dataManager.getSelectedValuesForDataType(utils
//                .getPickerSelectedIndex(this, PICKER_REQUEST));
//        if (selectedValues != null) {
//
//            for (Value value1 : selectedValues) {
//
//                if (value.getValue().equals(value1.getValue()) && value.getTitle().equals
//                        (value1.getTitle())) {
//                    return true;
//                }
//            }
//
//        }
//
//        return false;
//    }
//
//    private String getIndividualPayload(Picker picker) {
//
//        Value value = getValueForPicker(picker);
//
//        Log.e("Selected Value - ", value.getTitle());
//        return value.getValue();
//
//    }
//
//
//    private String countryPayload() {
//
//        return "1";
//    }
//
//    private Value getValueForPicker(Picker picker) {
//        int selectedIndex;
//
//        if (utils.presentSelectedIndex(this, picker.getPicker())) {
//
//            selectedIndex = utils.getPickerSelectedIndex(this, picker.getPicker());
//
//        } else {
//
//            if (picker.isEnableDefault() && picker.getDefaultIndex() != -1) {
//
//                selectedIndex = utils.getPickerDefaultIndex(this, picker.getPicker());
//
//            } else {
//
//                selectedIndex = utils.getPickerSelectedIndex(this, picker.getPicker());
//
//            }
//        }
//
//        return picker.getValues().get(selectedIndex);
//    }


}
