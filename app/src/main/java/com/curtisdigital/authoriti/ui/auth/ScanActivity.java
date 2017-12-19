package com.curtisdigital.authoriti.ui.auth;

import com.acuant.mobilesdk.AcuantAndroidMobileSDKController;
import com.acuant.mobilesdk.Card;
import com.acuant.mobilesdk.LicenseDetails;
import com.acuant.mobilesdk.WebServiceListener;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_scan)
public class ScanActivity extends BaseActivity implements WebServiceListener {

    AcuantAndroidMobileSDKController acuantAndroidMobileSDKControllerInstance = null;

    @AfterViews
    void callAfterViewInjection(){

        initializeSDK();

    }

    private void initializeSDK(){

        acuantAndroidMobileSDKControllerInstance = AcuantAndroidMobileSDKController.getInstance(this, ACUANT_LICENSE_KEY);
        acuantAndroidMobileSDKControllerInstance.setWebServiceListener(this);
        acuantAndroidMobileSDKControllerInstance.setWatermarkText("", 0, 0, 30, 0);
        acuantAndroidMobileSDKControllerInstance.setFacialRecognitionTimeoutInSeconds(20);

    }

    @Click(R.id.cameraFront)
    void captureFront(){

        System.gc();
        System.runFinalization();

    }

    @Click(R.id.cameraBack)
    void captureBack(){

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.tvSkip)
    void skipButtonClicked(){
        AccountManagerActivity_.intent(mContext).start();
    }


    // WebServiceListener
    @Override
    public void processImageServiceCompleted(Card card) {

    }

    @Override
    public void validateLicenseKeyCompleted(LicenseDetails licenseDetails) {

    }
}
