package com.curtisdigital.authoriti.ui.auth;

import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.response.ResponseInviteCode;
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
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_invite_code)
public class InviteCodeActivity extends BaseActivity{

    @Extra
    boolean showBack = true;

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tiCode)
    TextInputLayout tiCode;

    @ViewById(R.id.etCode)
    EditText etCode;

    @ViewById(R.id.ivBack)
    ImageView ivBack;

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    @AfterViews
    void callAfterViewInjection(){

        if (showBack){
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.INVISIBLE);
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen){
                    scrollView.scrollTo(0, (int)ViewUtils.convertDpToPixel(50, mContext));

                } else {
                    scrollView.scrollTo(0, 0);
                }
            }
        });

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

        } else {

            hideKeyboard();
            checkInviteCode();
//            StartupActivity_.intent(mContext).start();

        }
    }

    private void checkInviteCode(){

        displayProgressDialog("Validating Invite Code...");

        AuthoritiAPI.APIService().checkInviteCodeValidate(etCode.getText().toString()).enqueue(new Callback<ResponseInviteCode>() {
            @Override
            public void onResponse(Call<ResponseInviteCode> call, Response<ResponseInviteCode> response) {

                if (response.code() == 200 && response.body() != null){

                    fetchInviteCodeResult(response.body());

                } else {

                    showAlert("", "Invalid Code.");

                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseInviteCode> call, Throwable t) {

                dismissProgressDialog();
                showAlert("", t.getLocalizedMessage());

            }
        });
    }

    private void fetchInviteCodeResult(ResponseInviteCode responseInviteCode){

        if (!responseInviteCode.isValid()){

            showAlert("", "Invalid Code.");

        } else {

            dataManager.inviteCode = etCode.getText().toString();
            dataManager.showSkip = responseInviteCode.isSkipDLV();

            if (responseInviteCode.getCustomer() != null){

                ChaseActivity_.intent(mContext).customer(responseInviteCode.getCustomer()).start();

            } else {

                StartupActivity_.intent(mContext).start();

            }
        }
    }
}
