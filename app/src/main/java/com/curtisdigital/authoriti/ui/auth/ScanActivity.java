package com.curtisdigital.authoriti.ui.auth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.acuant.mobilesdk.AcuantAndroidMobileSDKController;
import com.acuant.mobilesdk.AcuantErrorListener;
import com.acuant.mobilesdk.Card;
import com.acuant.mobilesdk.CardCroppingListener;
import com.acuant.mobilesdk.CardType;
import com.acuant.mobilesdk.ConnectWebserviceListener;
import com.acuant.mobilesdk.DriversLicenseCard;
import com.acuant.mobilesdk.FacialData;
import com.acuant.mobilesdk.LicenseActivationDetails;
import com.acuant.mobilesdk.LicenseDetails;
import com.acuant.mobilesdk.Permission;
import com.acuant.mobilesdk.ProcessImageRequestOptions;
import com.acuant.mobilesdk.Region;
import com.acuant.mobilesdk.WebServiceListener;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.Event;
import com.curtisdigital.authoriti.api.model.request.RequestDLSave;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_scan)
public class ScanActivity extends BaseActivity implements WebServiceListener, CardCroppingListener, AcuantErrorListener, ConnectWebserviceListener {

    public  String sPdf417String = "";
    AcuantAndroidMobileSDKController acuantAndroidMobileSdkControllerInstance = null;

    private int cardRegion;


    private String assureIDUsername;
    private String assureIDPassword;
    private String assureIDSubscription;
    private String assureIDURL;
    private boolean isConnect = false;
    private boolean capturedsPdf417String = false;

    @ViewById(R.id.ivFront)
    ImageView ivFront;

    @ViewById(R.id.ivBackward)
    ImageView ivBackward;

    private boolean capturedFront = false;
    private boolean capturedBack = false;
    private boolean isBack = false;

    private boolean isSkip = false;
    private boolean isNext = false;


    @AfterViews
    void callAfterViewInjection(){

        initializeSDK();

    }

    private void initializeSDK(){


        cardRegion = Region.REGION_UNITED_STATES;
        isConnect = isConnectWS();

        if (isConnect){

            acuantAndroidMobileSdkControllerInstance = AcuantAndroidMobileSDKController.getInstance(this,assureIDUsername,assureIDPassword,assureIDSubscription,assureIDURL);
            acuantAndroidMobileSdkControllerInstance.setConnectWebServiceListener(this);

        } else {

            acuantAndroidMobileSdkControllerInstance = AcuantAndroidMobileSDKController.getInstance(this, ACUANT_LICENSE_KEY);

        }


        acuantAndroidMobileSdkControllerInstance.setWebServiceListener(this);
        acuantAndroidMobileSdkControllerInstance.setWatermarkText("Powered By Acuant", 0, 0, 30, 0);
        acuantAndroidMobileSdkControllerInstance.setFacialRecognitionTimeoutInSeconds(20);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        acuantAndroidMobileSdkControllerInstance.setAcuantErrorListener(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int minLength = (int) (Math.min(width,height)*0.9);
        int maxLength = (int) (minLength*1.5);
        final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        Typeface currentTypeFace =   textPaint.getTypeface();
        Typeface bold = Typeface.create(currentTypeFace, Typeface.BOLD);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(bold);

        Paint subtextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        subtextPaint.setColor(Color.RED);
        subtextPaint.setTextSize(25);
        subtextPaint.setTextAlign(Paint.Align.LEFT);
        subtextPaint.setTypeface(Typeface.create(subtextPaint.getTypeface(), Typeface.BOLD));

        final String instrunctionStr = "Get closer until Red Rectangle appears and Blink";
        final String subInstString = "Analyzing...";
        Rect bounds = new Rect();
        textPaint.getTextBounds(instrunctionStr, 0, instrunctionStr.length(), bounds);
        int top = (int)(height*0.05);
        int left = (width-bounds.width())/2;

        textPaint.getTextBounds(subInstString, 0, subInstString.length(), bounds);
        int subLeft = (width-bounds.width())/2;

        acuantAndroidMobileSdkControllerInstance.setInstructionText(instrunctionStr, left,top,textPaint);
        acuantAndroidMobileSdkControllerInstance.setSubInstructionText(subInstString, subLeft,top+30,subtextPaint);

        acuantAndroidMobileSdkControllerInstance.setFlashlight(false);
        acuantAndroidMobileSdkControllerInstance.setCropBarcode(false);
        acuantAndroidMobileSdkControllerInstance.setCaptureOriginalCapture(false);
        acuantAndroidMobileSdkControllerInstance.setCardCroppingListener(this);
        acuantAndroidMobileSdkControllerInstance.setAcuantErrorListener(this);

    }

    private boolean isConnectWS(){
        boolean retValue = false;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        assureIDSubscription = sharedPref.getString("AssureID_Subscription","");
        assureIDUsername = sharedPref.getString("AssureID_Username","");
        assureIDPassword = sharedPref.getString("AssureID_Password","");
        assureIDURL = sharedPref.getString("AssureID_Cloud_URL","");
        boolean enabled = sharedPref.getBoolean("AssureID_Enable",false);


        if(enabled && assureIDSubscription!=null && !assureIDSubscription.trim().equalsIgnoreCase("")
                && assureIDUsername!=null && !assureIDUsername.trim().equalsIgnoreCase("")
                && assureIDPassword!=null && !assureIDPassword.trim().equalsIgnoreCase("")
                && assureIDURL!=null && !assureIDURL.trim().equalsIgnoreCase("")){

            retValue = true;

        }

        return retValue;
    }

    private void showCameraInterface(){

        if (!isBack){

            acuantAndroidMobileSdkControllerInstance.showManualCameraInterface(this, CardType.DRIVERS_LICENSE, cardRegion, true);
        } else {

            acuantAndroidMobileSdkControllerInstance.showCameraInterfacePDF417(this, CardType.DRIVERS_LICENSE, cardRegion);
        }

    }

    private void showCardDetails(Card card){

        if (card == null || card.isEmpty()){

            showAlert("", "No data found for this license card.");

        } else {

            DriversLicenseCard licenseCard = (DriversLicenseCard) card;
            Log.e("First Name ", licenseCard.getNameFirst());
            Log.e("Middle Name", licenseCard.getNameMiddle());
            Log.e("Last Name ", licenseCard.getNameLast());
            Log.e("Name Suffix ", licenseCard.getNameSuffix());
            Log.e("ID ", licenseCard.getLicenceID());
            Log.e("License ", licenseCard.getLicense());
            Log.e("DOB Long ", licenseCard.getDateOfBirth4());
            Log.e("DOB Short", licenseCard.getDateOfBirth());
            Log.e("Date Of Birth Local ", licenseCard.getDateOfBirthLocal());
            Log.e("Issue Date Long ", licenseCard.getIssueDate4());
            Log.e("Issue Date Short ", licenseCard.getIssueDate());
            Log.e("Issue Date Local ", licenseCard.getIssueDateLocal());
            Log.e("Expiration Date Long ", licenseCard.getExpirationDate4());
            Log.e("Expiration Date Short ", licenseCard.getExpirationDate());
            Log.e("Eye Color ", licenseCard.getEyeColor());
            Log.e("Hari Color ", licenseCard.getHair());
            Log.e("Height " , licenseCard.getHeight());
            Log.e("Weight ", licenseCard.getWeight());
            Log.e("Address ", licenseCard.getAddress());
            Log.e("Address 2 ", licenseCard.getAddress2());
            Log.e("Address 3 ", licenseCard.getAddress3());
            Log.e("Address 4 ", licenseCard.getAddress4());
            Log.e("Address 5 ", licenseCard.getAddress5());
            Log.e("Address 6 ", licenseCard.getAddress6());
            Log.e("City ", licenseCard.getCity());
            Log.e("Zip ", licenseCard.getZip());
            Log.e("State ", licenseCard.getState());
            Log.e("Country ", licenseCard.getCounty());
            Log.e("Country Short ", licenseCard.getCountryShort());
            Log.e("Country Long ", licenseCard.getIdCountry());
            Log.e("Class ", licenseCard.getLicenceClass());
            Log.e("Restriction ", licenseCard.getRestriction());
            Log.e("Sex", licenseCard.getSex());
            Log.e("Audit, ", licenseCard.getAudit());
            Log.e("Endorsements ", licenseCard.getEndorsements());
            Log.e("Fee ", licenseCard.getFee());
            Log.e("CSC ", licenseCard.getCSC());
            Log.e("SigNum ", licenseCard.getSigNum());
            Log.e("Text1 ", licenseCard.getText1());
            Log.e("Text2 ", licenseCard.getText2());
            Log.e("Text3 ", licenseCard.getText3());
            Log.e("Type ", licenseCard.getType());
            Log.e("Doc Type ", licenseCard.getDocType());
            Log.e("Father Name ", licenseCard.getFatherName());
            Log.e("Mother Name ", licenseCard.getMotherName());
            Log.e("NameFirst_NonMRZ ", licenseCard.getNameFirst_NonMRZ());
            Log.e("NameLast_NonMRZ ", licenseCard.getNameLast_NonMRZ());
            Log.e("Document Detected Name ", licenseCard.getDocumentDetectedName());
            Log.e("Document Detected Name ", licenseCard.getDocumentDetectedNameShort());
            Log.e("Nationality ", licenseCard.getNationality());
            Log.e("Original ", licenseCard.getOriginal());
            Log.e("PlaceOfBirth ", licenseCard.getPlaceOfBirth());
            Log.e("PlaceOfIssue ", licenseCard.getPlaceOfIssue());
            Log.e("Social Security ", licenseCard.getSocialSecurity());
            Log.e("TID ", licenseCard.getTransactionId());


            String builder = "Authentication Result - " + licenseCard.getAuthenticationResult() +
                    ", First Name - " + licenseCard.getNameFirst() +
                    ", Middle Name - " + licenseCard.getNameMiddle() +
                    ", Last Name - " + licenseCard.getNameLast() +
                    ", Name Suffix - " + licenseCard.getNameSuffix() +
                    ", ID - " + licenseCard.getLicenceID() +
                    ", License - " + licenseCard.getLicense() +
                    ", DOB Long - " + licenseCard.getDateOfBirth4() +
                    ", DOB Short - " + licenseCard.getDateOfBirth() +
                    ", Date Of Birth Local - " + licenseCard.getDateOfBirthLocal() +
                    ", Issue Date Long - " + licenseCard.getIssueDate4() +
                    ", Issue Date Short - " + licenseCard.getIssueDate() +
                    ", Issue Date Local - " + licenseCard.getIssueDateLocal() +
                    ", Expiration Date Long - " + licenseCard.getExpirationDate4() +
                    ", Expiration Date Short - " + licenseCard.getExpirationDate() +
                    ", Eye Color - " + licenseCard.getEyeColor() +
                    ", Hair Color - " + licenseCard.getHair() +
                    ", Height - " + licenseCard.getHeight() +
                    ", Weight - " + licenseCard.getWeight() +
                    ", Address - " + licenseCard.getAddress() +
                    ", Address2 - " + licenseCard.getAddress2() +
                    ", Address3 - " + licenseCard.getAddress3() +
                    ", Address4 - " + licenseCard.getAddress4() +
                    ", Address5 - " + licenseCard.getAddress5() +
                    ", Address6 - " + licenseCard.getAddress6() +
                    ", City - " + licenseCard.getCity() +
                    ", Zip - " + licenseCard.getZip() +
                    ", State - " + licenseCard.getState() +
                    ", Country - " + licenseCard.getCounty() +
                    ", Country Short - " + licenseCard.getCountryShort() +
                    ", Country Long - " + licenseCard.getIdCountry() +
                    ", Class - " + licenseCard.getClass() +
                    ", Restriction - " + licenseCard.getRestriction() +
                    ", Sex - " + licenseCard.getSex() +
                    ", Audit - " + licenseCard.getAudit() +
                    ", Endorsements - " + licenseCard.getEndorsements() +
                    ", Fee - " + licenseCard.getFee() +
                    ", CSC - " + licenseCard.getCSC() +
                    ", SigNum - " + licenseCard.getSigNum() +
                    ", Text1 - " + licenseCard.getText1() +
                    ", Text2 - " + licenseCard.getText2() +
                    ", Text3 - " + licenseCard.getText3() +
                    ", Type - " + licenseCard.getType() +
                    ", Doc Type - " + licenseCard.getDocType() +
                    ", Father Name - " + licenseCard.getFatherName() +
                    ", Mother Name - " + licenseCard.getMotherName() +
                    ", NameFirst_NonMRZ - " + licenseCard.getNameFirst_NonMRZ() +
                    ", NameLast_NonMRZ - " + licenseCard.getNameLast_NonMRZ() +
                    ", NameLast1 - " + licenseCard.getNameLast1() +
                    ", NameLast2 - " + licenseCard.getNameLast2() +
                    ", NameMiddle_NonMRZ - " + licenseCard.getNameMiddle_NonMRZ() +
                    ", Document Detected Name - " + licenseCard.getDocumentDetectedName() +
                    ", Docuemtn Detected Name Short - " + licenseCard.getDocumentDetectedNameShort() +
                    ", Nationality - " + licenseCard.getNationality() +
                    ", Original - " + licenseCard.getOriginal() +
                    ", PlaceOfBirth - " + licenseCard.getPlaceOfBirth() +
                    ", PlaceOfIssue - " + licenseCard.getPlaceOfIssue() +
                    ", Social Security - " + licenseCard.getSocialSecurity() +
                    ", TID - " + licenseCard.getTransactionId();

            saveDLInfo(builder);

            if (isNext){

                if (licenseCard.getAuthenticationResult().toLowerCase().equals("passed")){

                    Log.e("Verification - ", "Passed");
                    AccountManagerActivity_.intent(mContext).start();

                } else {

                    showAlert("", "Could not verify your Driver's License, Please try again.");
                }

            }

            if (isSkip){

                AccountManagerActivity_.intent(mContext).start();

            }
        }

    }

    private void saveDLInfo(String metaData){

        Event event = new Event();
        event.setEvent("DL authentication");
        Date now = new Date();
        event.setTime(now.toString());
        event.setMetaData(metaData);

        List<Event> events = new ArrayList<>();
        events.add(event);

        RequestDLSave requestDLSave = new RequestDLSave();
        requestDLSave.setEvents(events);
        requestDLSave.setToken("");

        AuthoritiAPI.APIService().saveDLInfo(requestDLSave).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void checkProcess(){

        if (!capturedFront){

            showAlert("", "Please provide a front image.");

        } else {

            if (capturedsPdf417String){

                processCardValidation();

            } else {

                if(!capturedBack){

                    showAlert("", "Please provide a back image.");

                } else {

                    processCardValidation();

                }

            }


        }

    }

    private void processCardValidation(){

        ProcessImageRequestOptions options = ProcessImageRequestOptions.getInstance();
        options.autoDetectState = true;
        options.stateID = -1;
        options.reformatImage = true;
        options.reformatImageColor = 0;
        options.DPI = 150;
        options.cropImage = false;
        options.faceDetec = true;
        options.signDetec = true;
        options.iRegion = Region.REGION_UNITED_STATES;
        options.acuantCardType = CardType.DRIVERS_LICENSE;


        BitmapDrawable drawable_front = (BitmapDrawable) ivFront.getDrawable();
        Bitmap bitmap_front = drawable_front.getBitmap();

        BitmapDrawable drawable_back = (BitmapDrawable) ivBackward.getDrawable();
        Bitmap bitmap_back = null;
        if (drawable_back != null && drawable_back.getBitmap() != null){
            bitmap_back = drawable_back.getBitmap();
        }

        displayProgressDialog("Processing...");

        if(isConnect){
            acuantAndroidMobileSdkControllerInstance.callProcessImageConnectServices(bitmap_front, bitmap_back, sPdf417String, this, options);
        }else {
            acuantAndroidMobileSdkControllerInstance.callProcessImageServices(bitmap_front, bitmap_back, sPdf417String, this, options);

        }

        resetPdf417String();

    }

    private void resetPdf417String() {

        sPdf417String = "";
        capturedsPdf417String = false;
    }


    @Click(R.id.cameraFront)
    void captureFront(){

        System.gc();
        System.runFinalization();

        isBack = false;
        showCameraInterface();

    }

    @Click(R.id.cameraBack)
    void captureBack(){
        System.gc();
        System.runFinalization();

        isBack = true;
        showCameraInterface();

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.tvSkip)
    void skipButtonClicked(){

//        isSkip = true;
//        isNext = false;
//
//        checkProcess();

        AccountManagerActivity_.intent(mContext).start();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){

        isSkip = false;
        isNext = true;

        checkProcess();

    }

    // WebServiceListener
    @Override
    public void processImageServiceCompleted(Card card) {

        dismissProgressDialog();

        showCardDetails(card);

    }


    @Override
    public void activateLicenseKeyCompleted(LicenseActivationDetails licenseActivationDetails) {

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

        if (card_bitmap == null){

            showAlert("", "Unable to detect ID, Please retry.");

            ivFront.setImageBitmap(null);
            capturedFront = false;

        } else {

            ivFront.setImageBitmap(card_bitmap);
            capturedFront = true;

        }


    }

    @Override
    public void onCardCroppingFinish(Bitmap bitmapCropped, boolean scanBackSide, int detectedCardType) {

        dismissProgressDialog();
        Log.e("CARD Cropping ", "Finished");

        if (bitmapCropped == null){

            showAlert("", "Unable to detect ID, Please retry.");

            ivFront.setImageBitmap(null);
            capturedFront = false;

        } else {

            ivFront.setImageBitmap(bitmapCropped);
            capturedFront = true;

        }


    }

    @Override
    public void onPDF417Finish(String result) {
        sPdf417String = result;
        Log.e("sPdf417String", sPdf417String);

        capturedsPdf417String = true;

        checkProcess();
    }

    @Override
    public void onOriginalCapture(Bitmap bitmapOriginal) {

        Log.e("Original Bitmap ", "Captured");

    }

    @Override
    public void onCancelCapture(Bitmap croppedImageOnCancel, Bitmap originalImageonCancel) {

        Log.e("Capture ", "Cancelled");

        capturedsPdf417String = false;

        if (croppedImageOnCancel != null){

            ivBackward.setImageBitmap(croppedImageOnCancel);
            capturedBack = true;


        } else if (originalImageonCancel != null){

            ivBackward.setImageBitmap(originalImageonCancel);
            capturedBack = true;


        } else {

            ivBackward.setImageBitmap(null);
            capturedBack = false;
        }


    }

    @Override
    public void onBarcodeTimeOut(Bitmap croppedImageOnTimeout, Bitmap originalImageOnTimeout) {

        Log.e("Barcode Scan ", "Time Out");

        capturedsPdf417String = false;

        acuantAndroidMobileSdkControllerInstance.finishScanningBarcodeCamera();

        if (croppedImageOnTimeout != null){

            ivBackward.setImageBitmap(croppedImageOnTimeout);
            capturedBack = true;
            return;

        } else if (originalImageOnTimeout != null){

            ivBackward.setImageBitmap(originalImageOnTimeout);
            capturedBack = true;
            return;

        } else {

            ivBackward.setImageBitmap(null);
            capturedBack = false;
        }

        if (acuantAndroidMobileSdkControllerInstance.getBarcodeCameraContext() != null){

            showAlert(acuantAndroidMobileSdkControllerInstance.getBarcodeCameraContext(), "", "Unable to scan barcode");

        }
    }

    @Override
    public void didFailWithError(int code, String message) {

        dismissProgressDialog();
        Log.e("Did Failed with Error -", message + " - " + code);

        if (isNext){

            showAlert("", message + " - " + code);

        }

        if (isSkip){

            AccountManagerActivity_.intent(mContext).start();

        }

    }

    @Override
    public void processImageConnectServiceCompleted(String jsonString) {

    }

    @Override
    public void processImageConnectServiceFailed(int responseCode, String errorMessage) {

    }

    @Override
    public void deleteImageConnectServiceCompleted(String instanceID) {

    }

    @Override
    public void deleteImageConnectServiceFailed(int errorCode, String message) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case Permission.PERMISSION_CAMERA:{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    showCameraInterface();

                } else {

                    showAlert("", "Denied permission, Please give camera permission go proceed.");

                }
                return;
            }

            case Permission.PERMISSION_LOCATION:{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    acuantAndroidMobileSdkControllerInstance.enableLocationAuthentication(this);

                } else {

                    showAlert("", "Denied permission, Please give location permission go proceed.");

                }
                return;
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcuantAndroidMobileSDKController.cleanup();
    }
}
