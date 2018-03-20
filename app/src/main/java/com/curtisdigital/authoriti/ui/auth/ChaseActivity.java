package com.curtisdigital.authoriti.ui.auth;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.curtisdigital.authoriti.MainActivity_;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.api.model.User;
import com.curtisdigital.authoriti.api.model.request.RequestSignUp;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUpChase;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.core.SecurityActivity;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;
import com.curtisdigital.authoriti.utils.crypto.CryptoKeyPair;
import com.multidots.fingerprintauth.AuthErrorCodes;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_chase)
public class ChaseActivity extends SecurityActivity implements SecurityActivity.TouchIDEnableAlertListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;


    @Extra
    String customer;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tiIdentifier)
    TextInputLayout tiIdentifier;

    @ViewById(R.id.etIdentifier)
    EditText etIdentifier;

    @ViewById(R.id.tiPassword)
    TextInputLayout tiPassword;

    @ViewById(R.id.etPassword)
    EditText etPassword;

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    private CryptoKeyPair keyPair;

    private boolean saveSuccess = false;

    @AfterViews
    void callAfterViewInjection(){

        keyPair = dataManager.getCryptoKeyPair(dataManager.password, "");

        tvTitle.setText(customer + " is a partner of Authority. Please enter your " + customer + " password so we can authorize you.");

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen){
                    scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(100, mContext));

                } else {
                    scrollView.scrollTo(0, 0);
                }
            }
        });
    }

    private void signUp(){

        AccountID accountID = new AccountID("", etIdentifier.getText().toString());
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);

        RequestSignUp requestSignUp = new RequestSignUp(etPassword.getText().toString(), keyPair.getPublicKey(), keyPair.getSalt(), dataManager.inviteCode, accountIDs);

        displayProgressDialog("Sign Up...");

        AuthoritiAPI.APIService().signUpChase(requestSignUp).enqueue(new Callback<ResponseSignUpChase>() {
            @Override
            public void onResponse(Call<ResponseSignUpChase> call, Response<ResponseSignUpChase> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

                    fetchSignUpInfo(response.body());


                } else {

                    showAlert("","Sign Up Failed. Try Again Later.");

                }

            }

            @Override
            public void onFailure(Call<ResponseSignUpChase> call, Throwable t) {

                dismissProgressDialog();
                showAlert("","Sign Up Failed. Try Again Later.");
            }
        });

    }

    private void fetchSignUpInfo(ResponseSignUpChase responseSignUpChase){

        User user = new User();
        user.setUserId(responseSignUpChase.getId());
        user.setToken(responseSignUpChase.getToken());
        user.setPassword(etPassword.getText().toString());
        user.setInviteCode(dataManager.inviteCode);


        AccountID accountID = new AccountID(responseSignUpChase.getAccountName(), etIdentifier.getText().toString());
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);

        user.setAccountIDs(accountIDs);

        if (responseSignUpChase.getAccounts() != null && responseSignUpChase.getAccounts().size() > 0){

            List<AccountID> accountIDs1 = new ArrayList<>();

            for (int i = 0 ; i < responseSignUpChase.getAccounts().size() ; i ++){

                AccountID accountID1 = new AccountID();
                accountID1.setConfirmed(false);
                accountID1.setType(responseSignUpChase.getAccounts().get(i));
                accountID1.setIdentifier("");

                accountIDs1.add(accountID1);

            }

            user.setUnconfirmedAccountIDs(accountIDs1);
        }

        try {

            AesCbcWithIntegrity.SecretKeys keys;

            String salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
            keys = AesCbcWithIntegrity.generateKeyFromPassword(etPassword.getText().toString(), salt);

            String keyStr = AesCbcWithIntegrity.keyString(keys);

            user.setEncryptKey(keyStr);

            try {

                user.setEncryptPrivateKey(AesCbcWithIntegrity.encrypt(keyPair.getPrivateKey(), keys).toString());
                user.setEncryptSalt(AesCbcWithIntegrity.encrypt(keyPair.getSalt(), keys).toString());
                user.setEncryptPassword(AesCbcWithIntegrity.encrypt(etPassword.getText().toString(), keys).toString());


                dataManager.setUser(user);

                if (responseSignUpChase.getAccounts() != null && responseSignUpChase.getAccounts().size() > 0){

                    AccountConfirmActivity_.intent(this).start();

                } else {

                    saveSuccess = true;
                    mFingerPrintAuthHelper.startAuth();

                    hideKeyboard();
                    checkFingerPrintAuth();

                }



            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    private void checkFingerPrintAuth(){

        if (isBelowMarshmallow || fingerPrintHardwareNotDetected){

            updateLoginState();
            goHome();

        } else {

            setListener(this);
            showTouchIDEnableAlert();

        }


    }

    private void goHome(){

        dataManager.setScheme(null);

        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void enableFingerPrintAndGoHome(){

        removeListener();

        if (dataManager != null && dataManager.getUser() != null){

            User user = dataManager.getUser();
            user.setFingerPrintAuthEnabled(true);
            dataManager.setUser(user);

            updateLoginState();
            goHome();
        }

    }

    private void updateLoginState(){

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    @AfterTextChange(R.id.etIdentifier)
    void identifierChanged(){
        if (!TextUtils.isEmpty(etIdentifier.getText())){
            tiIdentifier.setError(null);
        } else {
            tiIdentifier.setError(utils.getSpannableStringForEditTextError("This field is required", this));
        }
    }

    @AfterTextChange(R.id.etPassword)
    void passwordChanged(){
        if (!TextUtils.isEmpty(etPassword.getText())){
            tiPassword.setError(null);
        } else {
            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){

        if (TextUtils.isEmpty(etIdentifier.getText())){

            tiIdentifier.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (TextUtils.isEmpty(etPassword.getText())){

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (!TextUtils.isEmpty(etIdentifier.getText()) && !TextUtils.isEmpty(etPassword.getText())){

            hideKeyboard();
            signUp();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void allowButtonClicked() {

        hideTouchIDEnabledAlert();

        if (fingerPrintNotRegistered){

            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);


        } else {

            enableFingerPrintAndGoHome();

        }


    }

    @Override
    public void dontAllowButtonClicked() {

        hideTouchIDEnabledAlert();

        updateLoginState();
        goHome();

    }
}
