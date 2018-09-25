package net.authoriti.authoriti.core;

import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.ui.alert.TouchIDAlert;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

/**
 * Created by mac on 12/29/17.
 */

public class SecurityActivity extends BaseActivity implements FingerPrintAuthCallback, TouchIDAlert.TouchIDAlertDialogListener {

    public boolean fingerPrintHardwareNotDetected = false;
    public boolean fingerPrintNotRegistered = false;
    public boolean isBelowMarshmallow = false;

    public FingerPrintAuthHelper mFingerPrintAuthHelper;

    TouchIDAlert touchIDAlert;
    AlertDialog alertDialog;
    TouchIDEnableAlertListener listener;

    public void setListener(TouchIDEnableAlertListener listener){
        this.listener = listener;
    }

    public void removeListener(){
        this.listener = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);

    }

    public void showTouchIdAlert(){
        if (touchIDAlert == null){
            touchIDAlert = new TouchIDAlert(this, R.style.FullScreenDialogStyle);
            touchIDAlert.setListener(this);
        }
        touchIDAlert.show();
    }

    public void dismissTouchIDAlert(){

        if (touchIDAlert != null){

            touchIDAlert.dismiss();
            touchIDAlert = null;

        }

    }

    public void updateTouchIDAlert(String body){

        if (touchIDAlert != null){

            if (touchIDAlert.getTvBody() != null){

                touchIDAlert.getTvBody().setText(body);

            }

        }

    }

    public void showTouchIDEnableAlert(){

        if(!isFinishing()) {

            if (alertDialog == null){

                alertDialog = new AlertDialog.Builder(this).create();

            }

            alertDialog.setTitle("");
            alertDialog.setMessage("Do you want to allow Authoriti to use Fingerprints?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (listener != null){
                        listener.allowButtonClicked();
                    }

                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Don't allow", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (listener != null){
                        listener.dontAllowButtonClicked();
                    }

                }
            });

            try {
                alertDialog.show();
            } catch (Exception e) {

            }
        }
    }

    public void hideTouchIDEnabledAlert(){

        if (alertDialog != null){

            alertDialog.dismiss();
            alertDialog = null;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            isBelowMarshmallow = false;

            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);

            if (!fingerprintManager.isHardwareDetected()) {

                // Device doesn't support fingerprint authentication

                fingerPrintHardwareNotDetected = true;

            } else if (!fingerprintManager.hasEnrolledFingerprints()) {

                // User hasn't enrolled any fingerprints to authenticate with

                fingerPrintNotRegistered = true;

            } else {

                fingerPrintHardwareNotDetected = false;
                fingerPrintNotRegistered = false;

            }

        } else {

            isBelowMarshmallow = true;
        }
    }

    @Override
    public void onNoFingerPrintHardwareFound() {

        Log.e("FingerPrint Hardware- ", "Not Founded");
        fingerPrintHardwareNotDetected = true;

    }

    @Override
    public void onNoFingerPrintRegistered() {

        Log.e("FingerPrint - ", "Not Registered");
        fingerPrintNotRegistered = true;


    }

    @Override
    public void onBelowMarshmallow() {

        Log.e("Device is bellow - ", "Marshmallow");
        isBelowMarshmallow = true;

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {

    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void touchIDAlertDialogCancelButtonClicked() {

    }

    public interface TouchIDEnableAlertListener{

        void allowButtonClicked();
        void dontAllowButtonClicked();

    }
}
