package com.curtisdigital.authoriti.ui.menu;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.User;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.core.BaseFragment;
import com.curtisdigital.authoriti.ui.items.SpinnerItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/17/17.
 */

@EFragment(R.layout.fragment_account_chase)
public class AccountChaseFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupWindow.OnDismissListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.id_account_confirm_fragment)
    View view;

    @ViewById(R.id.tiValue)
    TextInputLayout tiValue;

    @ViewById(R.id.etValue)
    EditText etValue;

    @ViewById(R.id.etAccount)
    EditText etAccount;

    @ViewById(R.id.spinner)
    View spinner;

    @ViewById(R.id.checkbox)
    CheckBox checkBox;

    List<AccountID> accountIDs;
    SpinnerItem adapter;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition;
    private int popupHeight, popupWidth;

    @AfterViews
    void callAfterViewInjection(){

        tiValue.setError(null);

        setAccountIDs();
        setSpinner();
        setAccount();

    }

    private void setAccountIDs(){

        accountIDs = dataManager.getUser().getAccountIDs();

        if (dataManager.getUser().getUnconfirmedAccountIDs() != null && dataManager.getUser().getUnconfirmedAccountIDs().size() > 0){

            for (AccountID accountID : dataManager.getUser().getUnconfirmedAccountIDs()){

                accountIDs.add(accountID);

            }
        }
    }

    private void setSpinner(){

        popupHeight = (int) ViewUtils.convertDpToPixel(50 * accountIDs.size(), mContext);
        popupWidth = ViewUtils.getScreenWidth(mContext) - (int) ViewUtils.convertDpToPixel(64, mContext);


        adapter = new SpinnerItem(mContext, accountIDs);

        lv = new ListView(mContext);
        lv.setAdapter(adapter);
        lv.setDividerHeight(0);
        lv.setOnItemClickListener(this);
        lv.setOnItemSelectedListener(this);
        lv.setSelector(android.R.color.transparent);
        lv.setBackgroundResource(R.drawable.bg_spinner_pop);
        lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        lv.setOverScrollMode(View.OVER_SCROLL_NEVER);

    }

    private void setAccount(){
        if (accountIDs != null && accountIDs.size() > 0){
            etAccount.setText(accountIDs.get(selectedPosition).getType());
        }
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

                            Snackbar.make(view, "Add Account Successfully", 1000).show();

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

        AccountID accountID = new AccountID(accountIDs.get(selectedPosition).getType(), etValue.getText().toString());
        User user = dataManager.getUser();
        user.getAccountIDs().add(accountID);

        for (int i = 0 ; i < user.getUnconfirmedAccountIDs().size() ; i ++){

            AccountID accountID1 = user.getUnconfirmedAccountIDs().get(i);
            if (accountID1.getType().equals(accountIDs.get(selectedPosition).getType())){
                user.getUnconfirmedAccountIDs().remove(accountID1);
            }

        }

        dataManager.setUser(user);

        updateAccountPicker(accountID);
        accountIDs.get(selectedPosition).setConfirmed(true);
        adapter.setAccountIDs(accountIDs);
        adapter.notifyDataSetChanged();

        etValue.setText("");
        tiValue.setError(null);

    }

    private void updateAccountPicker(AccountID accountID){

        Picker picker = dataManager.getAccountPicker();
        Value value = new Value(accountID.getIdentifier(), accountID.getType());
        picker.getValues().add(value);

        dataManager.setAccountPicker(picker);

        if (checkBox.isChecked()){

            checkBox.setChecked(false);

            picker.setEnableDefault(true);
            picker.setDefaultIndex(selectedPosition);
        }

        dataManager.setAccountPicker(picker);


    }

    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){

            if (accountIDs!= null && accountIDs.size() > 0){
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

            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", mContext));

        } else {

            if (accountIDs.get(selectedPosition).isConfirmed()){

                showAlert("", "This account is already confirmed.");

            } else {

                saveAccountName();

            }


        }
    }

    @AfterTextChange(R.id.etValue)
    void valueChanged(){

        if (TextUtils.isEmpty(etValue.getText())){

            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", mContext));

        } else {

            tiValue.setError(null);

        }
    }

    @Click(R.id.cvFinish)
    void generateButtonClicked(){
        Intent intent = new Intent(BROADCAST_CHANGE_MENU);
        intent.putExtra(MENU_ID, MENU_CODE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (pw != null)
            pw.dismiss();
        selectedPosition = position;
        setAccount();
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
}
