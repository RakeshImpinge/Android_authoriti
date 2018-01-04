package com.curtisdigital.authoriti.ui.auth;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
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
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.api.model.User;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.core.SecurityActivity;
import com.curtisdigital.authoriti.ui.items.SpinnerItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;
import com.google.gson.JsonObject;
import com.multidots.fingerprintauth.AuthErrorCodes;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/14/17.
 */

@EActivity(R.layout.activity_account_confirm)
public class AccountConfirmActivity extends SecurityActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupWindow.OnDismissListener, SecurityActivity.TouchIDEnableAlertListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;


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

    List<AccountID> unconfirmedAccountIDs;
    SpinnerItem adapter;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition;
    private int popupHeight, popupWidth;

    private boolean saveSuccess = false;

    @AfterViews
    void callAfterViewInjection(){

        unconfirmedAccountIDs = dataManager.getUser().getUnconfirmedAccountIDs();

        if (unconfirmedAccountIDs == null)
            return;

        selectedPosition = 0;
        etAccount.setText(unconfirmedAccountIDs.get(selectedPosition).getType());

        setSpinner();

    }

    private void setSpinner(){

        popupHeight = (int) ViewUtils.convertDpToPixel(50 * unconfirmedAccountIDs.size(), this);
        popupWidth = ViewUtils.getScreenWidth(this) - (int) ViewUtils.convertDpToPixel(64, this);


        adapter = new SpinnerItem(this, unconfirmedAccountIDs);

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

    private void saveAccountName(){

        String token = "Bearer " + dataManager.getUser().getToken();
        displayProgressDialog("");

        AuthoritiAPI.APIService().confirmAccountValue(token, etValue.getText().toString()).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status") != null){
                        if (jsonObject.get("status").getAsString().equals("Success")){

                            Snackbar.make(findViewById(R.id.id_account_confirm_activity), "Add Account Successfully", 1000).show();

                            updateAccount();

                        } else {
                            showAlert("","Failed to confirm your account number. Try again later.");
                        }
                    } else {
                        showAlert("","Failed to confirm your account number. Try again later.");
                    }
                } else {
                    showAlert("","Failed to confirm your account number. Try again later.");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                dismissProgressDialog();
                showAlert("","Failed to confirm your account number. Try again later.");

            }
        });
    }

    private void updateAccount(){

        AccountID accountID = new AccountID(unconfirmedAccountIDs.get(selectedPosition).getType(), etValue.getText().toString());
        User user = dataManager.getUser();
        user.getAccountIDs().add(accountID);

        for (int i = 0 ; i < user.getUnconfirmedAccountIDs().size() ; i ++){

            AccountID accountID1 = user.getUnconfirmedAccountIDs().get(i);
            if (accountID1.getType().equals(unconfirmedAccountIDs.get(selectedPosition).getType())){
                user.getUnconfirmedAccountIDs().remove(accountID1);
            }

        }

        dataManager.setUser(user);

        if (checkBox.isChecked()){

            checkBox.setChecked(false);

            dataManager.defaultAccountSelected = true;
            dataManager.defaultAccountIndex = dataManager.getUser().getAccountIDs().size() - 1;
        }

        unconfirmedAccountIDs.get(selectedPosition).setConfirmed(true);
        adapter.setAccountIDs(unconfirmedAccountIDs);
        adapter.notifyDataSetChanged();

        etValue.setText("");
        tiValue.setError(null);

    }

    private void updateLoginState(){

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void enableFingerPrintAndGoHome(){

        removeListener();

        if (dataManager != null && dataManager.getUser() != null){

            User user = dataManager.getUser();
            user.setFingerPrintAuthEnabled(true);
            dataManager.setUser(user);

            updateLoginState();
            goHome();
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }


    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){

            if (unconfirmedAccountIDs!= null && unconfirmedAccountIDs.size() > 0){
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

    @Click(R.id.cvConfirm)
    void confirmButtonClicked(){

        if (TextUtils.isEmpty(etValue.getText())){

            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        } else {

            if (unconfirmedAccountIDs.get(selectedPosition).isConfirmed()){

                showAlert("", "This account is already confirmed.");

            } else {

                saveAccountName();

            }


        }
    }

    @Click(R.id.cvFinish)
    void finishButtonClicked(){

        saveSuccess = true;
        mFingerPrintAuthHelper.startAuth();

        hideKeyboard();
        checkFingerPrintAuth();

    }

    @AfterTextChange(R.id.etValue)
    void valueChanged(){

        if (TextUtils.isEmpty(etValue.getText())){

            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        } else {

            tiValue.setError(null);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (pw != null)
            pw.dismiss();
        selectedPosition = position;
        etAccount.setText(unconfirmedAccountIDs.get(selectedPosition).getType());
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

    private void checkFingerPrintAuth(){


        if (isBelowMarshmallow || fingerPrintHardwareNotDetected){

            updateLoginState();
            goHome();

        } else {

            setListener(this);
            showTouchIDEnableAlert();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void allowButtonClicked() {

        hideTouchIDEnabledAlert();

        if (fingerPrintNotRegistered){

            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);


        } else {

            enableFingerPrintAndGoHome();

        }

    }

    @Override
    public void dontAllowButtonClicked() {

        hideTouchIDEnabledAlert();

        updateLoginState();
        goHome();

    }
}
