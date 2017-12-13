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
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_invite_code)
public class InviteCodeActivity extends BaseActivity {

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.tiCode)
    TextInputLayout tiCode;

    @ViewById(R.id.etCode)
    EditText etCode;

    @AfterViews
    void callAfterViewInjection(){

    }

    @AfterTextChange(R.id.etCode)
    void codeChanged(){
        if (!TextUtils.isEmpty(etCode.getText())){
            tiCode.setError(null);
        } else {
            tiCode.setError(utils.getSpannableStringForEditTextError("This field is required", this));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){
        if (TextUtils.isEmpty(etCode.getText())){
            tiCode.setError(utils.getSpannableStringForEditTextError("This field is required", this));
            return;
        }

        if (etCode.getText().toString().equals("Chase2018")){
            ChaseActivity_.intent(mContext).start();
        } else if (etCode.getText().toString().equals("Startup2018")){
            StartupActivity_.intent(mContext).start();
        }
    }
}
