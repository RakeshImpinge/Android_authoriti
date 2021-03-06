package net.authoriti.authoriti.ui.auth;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.widget.EditText;

import net.authoriti.authoriti.MainActivity_;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.core.SecurityActivity;
import net.authoriti.authoriti.ui.help.HelpActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.ViewUtils;

import net.authoriti.authoriti.utils.crypto.CryptoUtil;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_startup)
public class StartupActivity extends SecurityActivity implements SecurityActivity
        .TouchIDEnableAlertListener {

    private boolean passwordMatched;

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tiPassword)
    TextInputLayout tiPassword;

    @ViewById(R.id.etPassword)
    EditText etPassword;

    @ViewById(R.id.tiPasswordConfirm)
    TextInputLayout tiPasswordConfirm;

    @ViewById(R.id.etPasswordConfirm)
    EditText etPasswordConfirm;

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    @AfterViews
    void callAfterViewInjection() {
        tiPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Oswald_Regular.ttf"));
        tiPasswordConfirm.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Oswald_Regular.ttf"));


        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen) {
                    scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(100, mContext));

                } else {
                    scrollView.scrollTo(0, 0);
                }
            }
        });

    }

    @AfterTextChange(R.id.etPassword)
    void passwordChanged() {
        if (!TextUtils.isEmpty(etPassword.getText())) {
            if(etPassword.getText().toString().trim().equals("")){
                passwordMatched = false;
                tiPassword.setError(utils.getSpannableStringForEditTextError("Please don???t begin or end your password with blank space.", this));
            }else{
                tiPassword.setError(null);
            }
        } else {
            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));

        }
    }

    @AfterTextChange(R.id.etPasswordConfirm)
    void passwordConfirmChanged() {

        if (TextUtils.isEmpty(etPasswordConfirm.getText())) {
            tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
            passwordMatched = false;

        } else {
            if(etPasswordConfirm.getText().toString().trim().equals("")){
                passwordMatched = false;
                tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("Please don???t begin or end your password with blank space.", this));
            }else{
                if (!CryptoUtil.level1(etPassword.getText().toString()).equals(CryptoUtil.level1(etPasswordConfirm.getText().toString()))) {
                    tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field " +
                            "does not match", this));
                    passwordMatched = false;

                } else {
                    tiPasswordConfirm.setError(null);
                    passwordMatched = true;
                }
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.getHelpUrl(TOPIC_MASTER_PASSWORD)));
        startActivity(browserIntent);
    }


    @Click(R.id.cvNext)
    void nextButtonClicked() {

        if (TextUtils.isEmpty(etPassword.getText())) {

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));

        }

        if (TextUtils.isEmpty(etPasswordConfirm.getText())) {

            tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));

        }

        if (!TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etPassword.getText())
                && passwordMatched) {
            hideKeyboard();
            checkFingerPrintAuth();
        }
    }

    @Override
    public void allowButtonClicked() {
        if (fingerPrintNotRegistered) {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        } else {
            hideTouchIDEnabledAlert();
            utils.setTouchEnabled(true);
            goNext();
        }

    }

    @Override
    public void dontAllowButtonClicked() {
        hideTouchIDEnabledAlert();
        utils.setTouchEnabled(false);
        goNext();
    }

    private void checkFingerPrintAuth() {
        if (isBelowMarshmallow || fingerPrintHardwareNotDetected) {
            goNext();
        } else {
            setListener(this);
            showTouchIDEnableAlert();
        }
    }

    private void goNext() {
        dataManager.password = CryptoUtil.level1(etPassword.getText().toString());
        if (dataManager.showSkip) {
            AccountManagerActivity_.intent(mContext).start();
        } else {
            ScanActivity_.intent(mContext).start();
        }
    }
}
