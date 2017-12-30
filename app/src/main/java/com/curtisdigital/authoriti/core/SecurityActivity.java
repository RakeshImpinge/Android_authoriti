package com.curtisdigital.authoriti.core;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.ui.alert.TouchIDAlert;
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
}
