package net.authoriti.authoritiapp.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.core.BaseActivity;
import net.authoriti.authoritiapp.ui.help.HelpActivity_;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiData_;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.ConstantUtils;
import net.authoriti.authoritiapp.utils.Constants;
import net.authoriti.authoritiapp.utils.ViewUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_startup)
public class StartupActivity extends BaseActivity {

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

            tiPassword.setError(null);

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

            if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {

                tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field " +
                        "does not match", this));
                passwordMatched = false;

            } else {

                tiPasswordConfirm.setError(null);
                passwordMatched = true;
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.getHelpUrl(TOPIC_PASSWORD)));
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
            dataManager.password = etPassword.getText().toString();
            if (dataManager.showSkip) {
                AccountManagerActivity_.intent(mContext).start();
            } else {
                ScanActivity_.intent(mContext).start();
            }
        }
    }
}
