package com.curtisdigital.authoriti.ui.auth;

import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
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
    void callAfterViewInjection(){

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

    @AfterTextChange(R.id.etPassword)
    void passwordChanged(){

        if (!TextUtils.isEmpty(etPassword.getText())){

            tiPassword.setError(null);

        } else {

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }
    }

    @AfterTextChange(R.id.etPasswordConfirm)
    void passwordConfirmChanged(){

        if (TextUtils.isEmpty(etPasswordConfirm.getText())){

            tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field is required", this));
            passwordMatched = false;

        } else {

            if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())){

                tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field does not match", this));
                passwordMatched = false;

            } else {

                tiPasswordConfirm.setError(null);
                passwordMatched = true;
            }
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){

        if (TextUtils.isEmpty(etPassword.getText())){

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (TextUtils.isEmpty(etPasswordConfirm.getText())){

            tiPasswordConfirm.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (!TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etPassword.getText()) && passwordMatched){

            hideKeyboard();
            dataManager.password = etPassword.getText().toString();
            ScanActivity_.intent(mContext).start();

        }

    }
}
