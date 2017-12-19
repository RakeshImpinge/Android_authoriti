package com.curtisdigital.authoriti.ui.code;

import android.widget.ImageView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;

import net.glxn.qrgen.android.QRCode;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 12/2/17.
 */

@EActivity(R.layout.activity_code_generate)
public class CodeGenerateActivity extends BaseActivity {

    @ViewById(R.id.ivQRCode)
    ImageView ivQRCode;

    @AfterViews
    void callAfterViewInjection(){

        ivQRCode.setImageBitmap(QRCode.from("gFR4QHfVHw").bitmap());

    }

    @Click({R.id.ivClose, R.id.cvGotIt})
    void closeButtonClicked(){
        finish();
    }

}
