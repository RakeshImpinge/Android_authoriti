package net.authoriti.authoriti.ui.auth;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.core.SecurityActivity;
import net.authoriti.authoriti.utils.AuthoritiData;
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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

@EActivity(R.layout.activity_change_password)
public class ChangePasswordActivity extends SecurityActivity implements PopupWindow.OnDismissListener {


    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tiCurrentPassword)
    TextInputLayout tiCurrentPassword;

    @ViewById(R.id.etCurrentPassword)
    EditText etCurrentPassword;

    @ViewById(R.id.tiNewPassword)
    TextInputLayout tiNewPassword;

    @ViewById(R.id.etNewPassword)
    EditText etNewPassword;

    @ViewById(R.id.tiConfirmPassword)
    TextInputLayout tiConfirmPassword;

    @ViewById(R.id.etConfirmPassword)
    EditText etConfirmPassword;

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    @ViewById(R.id.space)
    Space space;

    @ViewById(R.id.change_pwd_logo)
    ImageView logo;

    @ViewById(R.id.checkboxFingerPrint)
    CheckBox checkboxFingerPrint;

    private boolean fingerPrintAuthEnabled = false;

    @ViewById(R.id.linCheckBox)
    LinearLayout linCheckBox;

    @AfterViews
    void callAfterViewInjection() {
        tiCurrentPassword.setError(null);
        tiNewPassword.setError(null);
        tiConfirmPassword.setError(null);

        if (ConstantUtils.isBuildFlavorVnb()) {
            logo.setImageResource(R.mipmap.icon_valley_wire_auth);
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    space.setVisibility(View.VISIBLE);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(150, mContext));
                        }
                    });

                } else {
                    space.setVisibility(View.GONE);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, 0);
                        }
                    });
                }
            }
        });

        if (dataManager != null && dataManager.getUser() != null && dataManager.getUser()
                .isFingerPrintAuthEnabled()) {
            mFingerPrintAuthHelper.startAuth();
            fingerPrintAuthEnabled = true;
            linCheckBox.setVisibility(View.VISIBLE);
        } else {
            linCheckBox.setVisibility(View.GONE);
        }

    }


    @Click(R.id.cvReset)
    void resetButtonClicked() {
        validateInut();
    }

    private void validateInut() {
        boolean isInputValid = true;
        if (TextUtils.isEmpty(etCurrentPassword.getText()) && !checkboxFingerPrint.isChecked()) {
            tiCurrentPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
            if (isInputValid) {
                isInputValid = false;
            }
        }

        if (TextUtils.isEmpty(etNewPassword.getText())) {
            tiNewPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
            if (isInputValid) {
                isInputValid = false;
            }
        }

        if (TextUtils.isEmpty(etConfirmPassword.getText())) {
            tiConfirmPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
            if (isInputValid) {
                isInputValid = false;
            }
        }

        if (!etNewPassword.getText().toString().equals(etConfirmPassword
                .getText().toString())) {
            tiNewPassword.setError(utils.getSpannableStringForEditTextError
                    ("New password doesn't match", this));
            tiConfirmPassword.setError(utils
                    .getSpannableStringForEditTextError("New password doesn't" +
                            " match", this));
            if (isInputValid) {
                isInputValid = false;
            }
        }

        if(isInputValid){
            if (checkboxFingerPrint.isChecked()) {
                showTouchIdAlert();
            } else {
                changePassword();
            }
        }
    }


    private void changePassword() {
        hideKeyboard();
        User user = dataManager.getUser();
        if (user != null && user.getAccountIDs() != null && user.getAccountIDs().size() > 0) {
            AesCbcWithIntegrity.SecretKeys keys;
            String keyStr = dataManager.getUser().getEncryptKey();
            try {
                keys = AesCbcWithIntegrity.keys(keyStr);
                String password = null;
                try {
                    AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity
                            .CipherTextIvMac(dataManager.getUser().getEncryptPassword());
                    password = AesCbcWithIntegrity.decryptString(civ, keys);
                    if (password.equals(etCurrentPassword.getText().toString()) || checkboxFingerPrint.isChecked()) {
                        user.setEncryptPassword(AesCbcWithIntegrity.encrypt(CryptoUtil.level1(etNewPassword.getText().toString()), keys).toString());
                        dataManager.setUser(user);
                        Toast.makeText(this, "Password Changed Successfully", Toast
                                .LENGTH_SHORT).show();
                        finish();
                    } else {
                        tiCurrentPassword.setError(utils.getSpannableStringForEditTextError
                                ("Password doesn't match wth current password", this));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterTextChange(R.id.etCurrentPassword)
    void currentPasswordChanged() {
        if (!TextUtils.isEmpty(etCurrentPassword.getText())) {
            tiCurrentPassword.setError(null);
        }
    }

    @AfterTextChange(R.id.etNewPassword)
    void newPasswordChanged() {
        if (!TextUtils.isEmpty(etNewPassword.getText())) {
            tiNewPassword.setError(null);
        }
    }

    @AfterTextChange(R.id.etConfirmPassword)
    void confirmPasswordChanged() {
        if (!TextUtils.isEmpty(etConfirmPassword.getText())) {
            tiConfirmPassword.setError(null);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.getHelpUrl(TOPIC_RESET_PASSWORD)));
        startActivity(browserIntent);
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkboxFingerPrint:
                if (checked) {
                    tiCurrentPassword.setVisibility(View.GONE);
                } else {
                    tiCurrentPassword.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        super.onAuthSuccess(cryptoObject);
        if (fingerPrintAuthEnabled) {
            dismissTouchIDAlert();
            changePassword();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (fingerPrintAuthEnabled) {
            mFingerPrintAuthHelper.startAuth();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void touchIDAlertDialogCancelButtonClicked() {
        dismissTouchIDAlert();
    }
}
