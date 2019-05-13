package net.authoriti.authoriti.ui.code;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.request.RequestComplete;
import net.authoriti.authoriti.api.model.response.ResponseCallAuthentication;
import net.authoriti.authoriti.api.model.response.ResponseComplete;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
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
    public static final int PERMISSIONS_REQUEST_CALL = 1;

    private Crypto crypto;

    @Extra
    String schemaIndex = "";

    @Extra
    Boolean callAuthorization = false;

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
    String permissionCode = "";

    @ViewById(R.id.ivCall)
    ImageButton ivCall;

    HashMap<String, String> pickerValues = new HashMap();

    Boolean isCallAuthorizationRequestSent = false;

    @AfterViews
    void callAfterViewInjection() {

        permissionCode = generateCode();

        int width = ivQRCode.getMeasuredWidth();
        int height = ivQRCode.getMeasuredHeight();
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        ivQRCode.setImageBitmap(QRCode.from(permissionCode).withSize(800, 800).bitmap());

        tvCode.setText(utils.fromHtml(generateHTMLString(permissionCode)));
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("permission code", permissionCode);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show();

        if (isPollingRequest) {
            completePollingRequest(userIndentifier, permissionCode);
        }

        if (callAuthorization) {
            ivCall.setVisibility(View.VISIBLE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_CALL);
            }
        } else {
            ivCall.setVisibility(View.GONE);
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

            if (!key_root.equals(PICKER_TIME)) {
                if (key_root.equals(PICKER_DATA_INPUT_TYPE)) {
                    pickerValues.put(hashMap.get("key"), hashMap.get("value"));
                } else {
                    pickerValues.put(key_root, hashMap.get("value"));
                }
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


    TelephonyManager tm;


    @Click(R.id.ivCall)
    void callButtonClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + Constants.AUTHORIZE_CALL_NUMBER));
            startActivity(callIntent);

            if (tm == null) {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            }

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_CALL);
        } else {
            Toast.makeText(mContext, "Please allow call permission from setting", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isConnected;
    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isConnected = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isConnected = true;
                    Log.e("callAuthorization", "CALL_STATE_OFFHOOK");
                    callAuthorizationAPI();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    isConnected = false;
                    if (isCallAuthorizationRequestSent) {
                        isCallAuthorizationRequestSent = false;
                        callAuthorizationDeleteRequest(userIndentifier);
                    }
                    break;

            }
        }
    };

    android.os.Handler handler;

    private void callAuthorizationAPI() {
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isConnected && !isCallAuthorizationRequestSent) {
                        Log.e("callAuthorization", "Is Call established : " + isConnected);
                        isCallAuthorizationRequestSent = true;
                        callAuthorizationRequest(userIndentifier, permissionCode);
                    }
                    handler = null;
                }
            }, 2000);
        }
    }

    private void callAuthorizationRequest(String accountID, String permissionCode) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("cid", accountID);
        hashMap.put("permissionCode", permissionCode);
        hashMap.put("schemaVersion", schemaIndex);
        if (dataManager.getUser().getAccountFromID(accountID) != null && dataManager.getUser().getAccountFromID(accountID).getCustomer() != null) {
            hashMap.put("customerName", dataManager.getUser().getAccountFromID(accountID).getCustomer());
        } else {
            hashMap.put("customerName", "");
        }
        hashMap.putAll(pickerValues);
        Log.e("callAuthorization", hashMap.toString());
        AuthoritiAPI.APIService().callAuthorization(hashMap).enqueue
                (new Callback<ResponseCallAuthentication>() {
                    @Override
                    public void onResponse(Call<ResponseCallAuthentication> call,
                                           Response<ResponseCallAuthentication>
                                                   response) {
                        Log.e("callAuthorization", "" + response.isSuccessful());

                        if (response.isSuccessful()) {

                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCallAuthentication> call, Throwable t) {
                        Log.e("callAuthorization", "" + t.getMessage());
                    }
                });
    }

    private void callAuthorizationDeleteRequest(String accountID) {
        AuthoritiAPI.APIService().callAuthorizationDelete(accountID).enqueue
                (new Callback<ResponseCallAuthentication>() {
                    @Override
                    public void onResponse(Call<ResponseCallAuthentication> call,
                                           Response<ResponseCallAuthentication>
                                                   response) {
                        Log.e("callDeleteRequest", "" + response.isSuccessful());

                        if (response.isSuccessful()) {

                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCallAuthentication> call, Throwable t) {
                        Log.e("callDeleteRequest", "" + t.getMessage());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
