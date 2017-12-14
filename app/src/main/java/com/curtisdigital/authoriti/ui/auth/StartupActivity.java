package com.curtisdigital.authoriti.ui.auth;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;

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

    @AfterViews
    void callAfterViewInjection(){

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

            dataManager.password = etPassword.getText().toString();
            ScanActivity_.intent(mContext).start();

        }

    }
}
