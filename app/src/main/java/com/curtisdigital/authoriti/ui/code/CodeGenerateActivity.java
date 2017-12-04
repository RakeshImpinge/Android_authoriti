package com.curtisdigital.authoriti.ui.code;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/**
 * Created by mac on 12/2/17.
 */

@EActivity(R.layout.activity_code_generate)
public class CodeGenerateActivity extends BaseActivity {

    @AfterViews
    void callAfterViewInjection(){

    }

    @Click({R.id.ivClose, R.id.cvGotIt})
    void closeButtonClicked(){
        finish();
    }

}
