package com.curtisdigital.authoriti.ui.auth;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.curtisdigital.authoriti.MainActivity_;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.User;
import com.curtisdigital.authoriti.api.model.request.RequestSignUp;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUp;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.items.SpinnerItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_account_manager)
public class AccountManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupWindow.OnDismissListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tiName)
    TextInputLayout tiName;

    @ViewById(R.id.etName)
    EditText etName;

    @ViewById(R.id.tiValue)
    TextInputLayout tiValue;

    @ViewById(R.id.etValue)
    EditText etValue;

    @ViewById(R.id.etAccount)
    EditText etAccount;

    @ViewById(R.id.spinner)
    View spinner;

    @ViewById(R.id.cvFinish)
    CardView cvFinish;

    @ViewById(R.id.checkbox)
    CheckBox checkBox;

    SpinnerItem adapter;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition;
    private int popupHeight, popupWidth;
    private boolean markDefault;

    @AfterViews
    void callAfterViewInjection(){

        setSpinner();
        updateFinishButton();

    }

    private void setSpinner(){

        popupWidth = ViewUtils.getScreenWidth(this) - (int) ViewUtils.convertDpToPixel(64, this);

        if (dataManager.accountIDs == null){
            dataManager.accountIDs = new ArrayList<>();
        }

        adapter = new SpinnerItem(this, dataManager.accountIDs);

        lv = new ListView(this);
        lv.setAdapter(adapter);
        lv.setDividerHeight(0);
        lv.setOnItemClickListener(this);
        lv.setOnItemSelectedListener(this);
        lv.setSelector(android.R.color.transparent);
        lv.setBackgroundResource(R.drawable.bg_spinner_pop);
        lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        selectedPosition = 0;
    }

    private void setPopupHeight(){

        if (dataManager.accountIDs == null || dataManager.accountIDs.size() == 0){

            popupHeight = 0;

        } else {

            if (dataManager.accountIDs.size() > 2){

                popupHeight = (int) ViewUtils.convertDpToPixel(150, this);

            } else {

                popupHeight = (int) ViewUtils.convertDpToPixel(50 * dataManager.accountIDs.size(), this);

            }
        }
    }

    private void updateFinishButton(){

        if (dataManager.accountIDs != null && dataManager.accountIDs.size() > 0){

            cvFinish.setEnabled(true);
            cvFinish.setAlpha(1.0f);

        } else {

            cvFinish.setEnabled(false);
            cvFinish.setAlpha(0.1f);

        }
    }

    private void saveAccount(){

        if (!checkBox.isChecked() && !markDefault){

            etAccount.setText(etName.getText().toString());

        } else if (checkBox.isChecked()){

            etAccount.setText(etName.getText().toString());
            markDefault = true;
            checkBox.setChecked(false);

        }

        if (dataManager.accountIDs == null){
            dataManager.accountIDs = new ArrayList<>();
        }

        AccountID accountID = new AccountID(etName.getText().toString(), etValue.getText().toString());
        dataManager.accountIDs.add(accountID);
        adapter.setAccountIDs(dataManager.accountIDs);
        adapter.notifyDataSetChanged();

        resetInputForm();
        updateFinishButton();
    }

    private void resetInputForm(){

        etName.setText("");
        tiName.setError(null);
        etValue.setText("");
        tiValue.setError(null);

        etName.requestFocus();
    }

    private void signUp(){

        dataManager.key = "privatekey";
        dataManager.salt = "salt";

        RequestSignUp requestSignUp = new RequestSignUp(dataManager.password, dataManager.key, dataManager.salt, dataManager.inviteCode, dataManager.accountIDs);

        displayProgressDialog("Sign Up...");

        AuthoritiAPI.APIService().signUp(requestSignUp).enqueue(new Callback<ResponseSignUp>() {
            @Override
            public void onResponse(Call<ResponseSignUp> call, Response<ResponseSignUp> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

                    fetchSignUpInfo(response.body());


                } else {

                    showAlert("","Sign Up Failed. Try Again Later.");

                }

            }

            @Override
            public void onFailure(Call<ResponseSignUp> call, Throwable t) {

                dismissProgressDialog();
                showAlert("","Sign Up Failed. Try Again Later.");
            }
        });

    }

    private void fetchSignUpInfo(ResponseSignUp responseSignUp){

        User user = responseSignUp.getUser();
        user.setUserId(responseSignUp.getUserId());
        user.setToken(responseSignUp.getToken());
        user.setPassword(dataManager.password);
        user.setInviteCode(dataManager.inviteCode);
        user.setSalt(dataManager.salt);
        user.setPrivateKey(dataManager.key);

        dataManager.setUser(user);

        goHome();

    }

    @AfterTextChange(R.id.etName)
    void nameChanged(){
        if (TextUtils.isEmpty(etName.getText())){
            tiName.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        } else {
            tiName.setError(null);
        }
    }

    @AfterTextChange(R.id.etValue)
    void valueChanged(){
        if (TextUtils.isEmpty(etValue.getText())){
            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        } else {
            tiValue.setError(null);
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvSave)
    void saveButtonClicked(){
        if (TextUtils.isEmpty(etName.getText())){
            tiName.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }
        if (TextUtils.isEmpty(etValue.getText())){
            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etValue.getText())){
            saveAccount();
        }
    }

    @Click(R.id.cvFinish)
    void finishButtonClicked(){

        signUp();
    }

    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){

            setPopupHeight();

            if (dataManager.accountIDs != null && dataManager.accountIDs.size() >= 0){
                if (pw == null || !pw.isShowing()) {
                    pw = new PopupWindow(spinner);
                    pw.setContentView(lv);
                    pw.setWidth(popupWidth);
                    pw.setHeight(popupHeight);
                    pw.setOutsideTouchable(true);
                    pw.setFocusable(true);
                    pw.setClippingEnabled(false);
                    pw.showAsDropDown(spinner, spinner.getLeft(),0);
                    pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_spinner_pop));
                    pw.setOnDismissListener(this);
                    opened = true;
                }

            }

        } else {
            if (pw != null){
                pw.dismiss();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (pw != null)
            pw.dismiss();

        selectedPosition = position;
        etAccount.setText(dataManager.accountIDs.get(selectedPosition).getType());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDismiss() {
        pw = null;
        opened = false;
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
