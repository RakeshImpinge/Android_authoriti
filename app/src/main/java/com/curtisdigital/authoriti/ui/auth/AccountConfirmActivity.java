package com.curtisdigital.authoriti.ui.auth;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.curtisdigital.authoriti.MainActivity_;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.api.model.User;
import com.curtisdigital.authoriti.core.SecurityActivity;
import com.curtisdigital.authoriti.ui.alert.AccountConfirmDialog;
import com.curtisdigital.authoriti.ui.items.AccountConfirmItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/14/17.
 */

@EActivity(R.layout.activity_account_confirm)
public class AccountConfirmActivity extends SecurityActivity implements SecurityActivity.TouchIDEnableAlertListener, AccountConfirmDialog.AccountConfirmDialogListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.cvFinish)
    CardView cvFinish;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    FastItemAdapter<AccountConfirmItem> adapter;
    AccountConfirmDialog accountConfirmDialog;

    AccountID selectedAccountId;
    int selectedPosition;

    private boolean saveSuccess = false;

    @AfterViews
    void callAfterViewInjection(){

        adapter = new FastItemAdapter<AccountConfirmItem>();
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

        adapter.withOnClickListener(new FastAdapter.OnClickListener<AccountConfirmItem>() {
            @Override
            public boolean onClick(View v, IAdapter<AccountConfirmItem> adapter, AccountConfirmItem item, int position) {

                selectedAccountId = item.getAccountID();
                selectedPosition = position;
                if (selectedAccountId.isConfirmed()){
                    showAlert("", "This account has already confirmed.");

                } else {

                    showAccountConfirmDialog();
                }

                return false;
            }
        });
        showAccounts();
    }

    private void showAccounts(){

        if (adapter != null){
            adapter.clear();
        } else {
            adapter = new FastItemAdapter<>();
        }

        User user = dataManager.getUser();
        if (user.getAccountIDs() != null && user.getAccountIDs().size() > 0){
            for (int i = 0 ; i < user.getAccountIDs().size() ; i ++){
                adapter.add(new AccountConfirmItem(user.getAccountIDs().get(i),dataManager.defaultAccountSelected && i == dataManager.defaultAccountIndex));
            }
        }

        if (user.getUnconfirmedAccountIDs() != null && user.getUnconfirmedAccountIDs().size() > 0){
            for (int i = 0 ; i < user.getUnconfirmedAccountIDs().size() ; i ++){
                adapter.add(new AccountConfirmItem(user.getUnconfirmedAccountIDs().get(i),false));
            }
        }
    }

    private void showAccountConfirmDialog(){

        if (accountConfirmDialog == null){

            accountConfirmDialog = new AccountConfirmDialog(this);
            accountConfirmDialog.setListener(this);

        } else {

            accountConfirmDialog.init();

        }

        if (!isFinishing() && !accountConfirmDialog.isShowing()){

            accountConfirmDialog.show();
        }
    }

    private void hideAccountConfirmDialog(){

        if (accountConfirmDialog != null){

            accountConfirmDialog.dismiss();
            accountConfirmDialog = null;
        }
    }

    private void saveAccountName(final String id, final boolean setDefault){

        String token = "Bearer " + dataManager.getUser().getToken();
        displayProgressDialog("");

        AuthoritiAPI.APIService().confirmAccountValue(token, id).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status") != null){
                        if (jsonObject.get("status").getAsString().equals("Success")){

                            Snackbar.make(findViewById(R.id.id_account_confirm_activity), "Add Account Successfully", 1000).show();

                            updateAccount(id, setDefault);

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

    private void updateAccount(String id, boolean setDefault){

        AccountID accountID = new AccountID(selectedAccountId.getType(), id);
        User user = dataManager.getUser();
        user.getAccountIDs().add(accountID);

        for (int i = 0 ; i < user.getUnconfirmedAccountIDs().size() ; i ++){

            AccountID accountID1 = user.getUnconfirmedAccountIDs().get(i);
            if (accountID1.getType().equals(selectedAccountId.getType())){
                user.getUnconfirmedAccountIDs().remove(accountID1);
            }

        }


        dataManager.setUser(user);

        if (setDefault){
            dataManager.defaultAccountSelected = true;
            dataManager.defaultAccountIndex = dataManager.getUser().getAccountIDs().size() - 1;
        }

        showAccounts();

    }

    private void updateLoginState(){

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    private void goHome(){

        dataManager.setScheme(null);

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


    @Click(R.id.cvFinish)
    void finishButtonClicked(){

        saveSuccess = true;
        mFingerPrintAuthHelper.startAuth();

        hideKeyboard();
        checkFingerPrintAuth();

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

    @Override
    public void accountConfirmDialogOKButtonClicked(String id, boolean setDefault) {

        hideAccountConfirmDialog();
        saveAccountName(id, setDefault);
    }

    @Override
    public void accountConfirmDialogCancelButtonClicked() {

        hideAccountConfirmDialog();
    }
}
