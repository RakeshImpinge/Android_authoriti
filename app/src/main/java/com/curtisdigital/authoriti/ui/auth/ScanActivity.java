package com.curtisdigital.authoriti.ui.auth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.acuant.mobilesdk.AcuantAndroidMobileSDKController;
import com.acuant.mobilesdk.AcuantErrorListener;
import com.acuant.mobilesdk.Card;
import com.acuant.mobilesdk.CardCroppingListener;
import com.acuant.mobilesdk.CardType;
import com.acuant.mobilesdk.LicenseDetails;
import com.acuant.mobilesdk.ProcessImageRequestOptions;
import com.acuant.mobilesdk.Region;
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
public class ScanActivity extends BaseActivity implements WebServiceListener, CardCroppingListener, AcuantErrorListener {

    AcuantAndroidMobileSDKController acuantAndroidMobileSDKControllerInstance = null;

    @ViewById(R.id.ivFront)
    ImageView ivFront;

    @ViewById(R.id.ivBackward)
    ImageView ivBackward;

    private boolean capturedFront = false;
    private boolean capturedBack = false;
    private Card processedCard;

    @AfterViews
    void callAfterViewInjection(){

//        initializeSDK();

    }

    private void initializeSDK(){

        acuantAndroidMobileSDKControllerInstance = AcuantAndroidMobileSDKController.getInstance(this, ACUANT_LICENSE_KEY);
        acuantAndroidMobileSDKControllerInstance.setWebServiceListener(this);
        acuantAndroidMobileSDKControllerInstance.setWatermarkText("", 0, 0, 30, 0);
        acuantAndroidMobileSDKControllerInstance.setFacialRecognitionTimeoutInSeconds(20);
        acuantAndroidMobileSDKControllerInstance.setFlashlight(false);
        acuantAndroidMobileSDKControllerInstance.setCardCroppingListener(this);
        acuantAndroidMobileSDKControllerInstance.setAcuantErrorListener(this);
    }

    private void showCardDetails(Card card){

        if (card == null || card.isEmpty()){



        }

    }

    @Click(R.id.cameraFront)
    void captureFront(){

        System.gc();
        System.runFinalization();

//        acuantAndroidMobileSDKControllerInstance.showManualCameraInterface(this, CardType.DRIVERS_LICENSE, Region.REGION_UNITED_STATES, false);

    }

    @Click(R.id.cameraBack)
    void captureBack(){
        System.gc();
        System.runFinalization();

//        acuantAndroidMobileSDKControllerInstance.showManualCameraInterface(this, CardType.DRIVERS_LICENSE, Region.REGION_UNITED_STATES, true);
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.tvSkip)
    void skipButtonClicked(){
        AccountManagerActivity_.intent(mContext).start();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){

        if (!capturedFront){

            showAlert("", "Please provide a front image.");

        } else if (!capturedBack){

            showAlert("", "Please provide a back image.");

        } else {

            ProcessImageRequestOptions options = ProcessImageRequestOptions.getInstance();
            options.autoDetectState = true;
            options.stateID = -1;
            options.reformatImage = true;
            options.reformatImageColor = 0;
            options.DPI = 150;
            options.cropImage = false;
            options.faceDetec = true;
            options.signDetec = false;
            options.iRegion = Region.REGION_UNITED_STATES;
            options.acuantCardType = CardType.DRIVERS_LICENSE;


            BitmapDrawable drawable_front = (BitmapDrawable) ivFront.getDrawable();
            Bitmap bitmap_front = drawable_front.getBitmap();

            BitmapDrawable drawable_back = (BitmapDrawable) ivBackward.getDrawable();
            Bitmap bitmap_back = drawable_back.getBitmap();

            acuantAndroidMobileSDKControllerInstance.callProcessImageServices(bitmap_front, bitmap_back, "", this, options);

        }



    }

    // WebServiceListener
    @Override
    public void processImageServiceCompleted(Card card) {

        processedCard = card;
        showCardDetails(card);

    }

    @Override
    public void validateLicenseKeyCompleted(LicenseDetails licenseDetails) {

    }


    @Override
    public void onCardCroppingStart(Activity activity) {

        System.gc();
        System.runFinalization();

        Log.e("CARD Cropping ", "Started");
        displayProgressDialog(activity, "Cropping Image...");

    }

    @Override
    public void onCardCroppingFinish(Bitmap card_bitmap, int detectedCardType) {

        dismissProgressDialog();
        Log.e("CARD Cropping - ", "Finished");

//        ivFront.setImageBitmap(card_bitmap);


    }

    @Override
    public void onCardCroppingFinish(Bitmap bitmapCropped, boolean scanBackSide, int detectedCardType) {

        dismissProgressDialog();
        Log.e("CARD Cropping ", "Finished");

        if (bitmapCropped == null){

            showAlert("", "Unable to detect ID, Please retry.");

            if (scanBackSide){

                ivFront.setImageBitmap(null);
                capturedFront = false;

            } else {

                ivBackward.setImageBitmap(null);
                capturedBack = false;

            }

        } else {

            if (scanBackSide){

                ivFront.setImageBitmap(bitmapCropped);
                capturedFront = true;

            } else {

                ivBackward.setImageBitmap(bitmapCropped);
                capturedBack = true;

            }


        }


    }

    @Override
    public void onPDF417Finish(String result) {

    }

    @Override
    public void onOriginalCapture(Bitmap bitmapOriginal) {

    }

    @Override
    public void onCancelCapture(Bitmap croppedImageOnCancel, Bitmap originalImageonCancel) {

    }

    @Override
    public void onBarcodeTimeOut(Bitmap croppedImageOnTimeout, Bitmap originalImageOnTimeout) {

    }

    @Override
    public void onCardImageCaptured() {

        Log.e("CARD Image Captured ", "TRUE");

    }

    @Override
    public void didFailWithError(int code, String message) {
        Log.e(message, String.valueOf(code));
    }
}
