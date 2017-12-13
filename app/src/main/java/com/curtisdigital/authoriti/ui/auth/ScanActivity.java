package com.curtisdigital.authoriti.ui.auth;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_scan)
public class ScanActivity extends BaseActivity {

    @AfterViews
    void callAfterViewInjection(){

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.tvSkip)
    void skipButtonClicked(){
        AccountManagerActivity_.intent(mContext).start();
    }
}
