package net.authoriti.authoritiapp.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.User;
import net.authoriti.authoritiapp.core.BaseActivity;
import net.authoriti.authoritiapp.ui.help.HelpActivity_;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.Constants;
import net.authoriti.authoritiapp.utils.ViewUtils;
import com.tozny.crypto.android.AesCbcWithIntegrity;

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

/**
 * Created by mac on 1/24/18.
 */

@EActivity(R.layout.activity_reset_password)
public class ResetPasswordActivity extends BaseActivity {

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

    @AfterViews
    void callAfterViewInjection() {

//        setupUI(findViewById(R.id.id_reset_password_activity));

        tiCurrentPassword.setError(null);
        tiNewPassword.setError(null);
        tiConfirmPassword.setError(null);

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

    }

    @Click(R.id.cvReset)
    void resetButtonClicked() {

        hideKeyboard();

        if (TextUtils.isEmpty(etCurrentPassword.getText())) {
            tiCurrentPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
        }

        if (TextUtils.isEmpty(etNewPassword.getText())) {
            tiNewPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
        }

        if (TextUtils.isEmpty(etConfirmPassword.getText())) {
            tiConfirmPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
        }

        if (!TextUtils.isEmpty(etCurrentPassword.getText()) && !TextUtils.isEmpty(etNewPassword
                .getText()) && !TextUtils.isEmpty(etConfirmPassword.getText())) {

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

                        if (password.equals(etCurrentPassword.getText().toString())) {

                            if (etNewPassword.getText().toString().equals(etConfirmPassword
                                    .getText().toString())) {

                                user.setEncryptPassword(AesCbcWithIntegrity.encrypt(etNewPassword
                                        .getText().toString(), keys).toString());
                                dataManager.setUser(user);

                                Toast.makeText(this, "Reset Password Successfully", Toast
                                        .LENGTH_SHORT).show();

                                finish();

                            } else {

                                tiNewPassword.setError(utils.getSpannableStringForEditTextError
                                        ("New password doesn't match", this));
                                tiConfirmPassword.setError(utils
                                        .getSpannableStringForEditTextError("New password doesn't" +
                                                " match", this));

                            }


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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                (Constants.HELP_BASE) + TOPIC_RESET_PASSWORD));
        startActivity(browserIntent);
    }

}
