package com.curtisdigital.authoriti.ui.auth;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;
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

@EActivity(R.layout.activity_chase)
public class ChaseActivity extends BaseActivity {

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.tiIdentifier)
    TextInputLayout tiIdentifier;

    @ViewById(R.id.etIdentifier)
    EditText etIdentifier;

    @ViewById(R.id.tiPassword)
    TextInputLayout tiPassword;

    @ViewById(R.id.etPassword)
    EditText etPassword;

    @AfterViews
    void callAfterViewInjection(){

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

    }
}
