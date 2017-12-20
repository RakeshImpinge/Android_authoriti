package com.curtisdigital.authoriti.ui.menu;

import android.content.Intent;
import android.content.IntentFilter;
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
import com.curtisdigital.authoriti.api.model.request.RequestUserUpdate;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUp;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 11/30/17.
 */

@EFragment(R.layout.fragment_account)
public class AccountFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupWindow.OnDismissListener {


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

    @ViewById(R.id.checkbox)
    CheckBox checkBox;

    SpinnerItem adapter;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition;
    private int popupHeight, popupWidth;

    @AfterViews
    void callAfterViewInjection(){

        setSpinner();
        setAccount();
    }

    private void setSpinner(){

        popupWidth = ViewUtils.getScreenWidth(mContext) - (int) ViewUtils.convertDpToPixel(64, mContext);

        adapter = new SpinnerItem(mContext, dataManager.getUser().getAccountIDs());

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
        selectedPosition = 0;
    }

    private void setPopupHeight(){

        if (dataManager.getUser().getAccountIDs() == null || dataManager.getUser().getAccountIDs().size() == 0){

            popupHeight = 0;

        } else {

            if (dataManager.getUser().getAccountIDs().size() > 2){

                popupHeight = (int) ViewUtils.convertDpToPixel(150, mContext);

            } else {

                popupHeight = (int) ViewUtils.convertDpToPixel(50 * dataManager.getUser().getAccountIDs().size(), mContext);

            }
        }
    }

    private void setAccount(){
        if (dataManager.getUser().getAccountIDs() != null && dataManager.getUser().getAccountIDs().size() > 0){
            etAccount.setText(dataManager.getUser().getAccountIDs().get(selectedPosition).getType());
        }
    }

    private void saveAccount(){

        RequestUserUpdate request = new RequestUserUpdate();
        AccountID accountID = new AccountID(etName.getText().toString(), etValue.getText().toString());
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);
        request.setAccountIDs(accountIDs);

        String token = "Bearer " + dataManager.getUser().getToken();

        displayProgressDialog("");

        AuthoritiAPI.APIService().updateUser(token, request).enqueue(new Callback<ResponseSignUp>() {
            @Override
            public void onResponse(Call<ResponseSignUp> call, Response<ResponseSignUp> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

                    updateAccount();

                } else {

                    showAlert("", "Account Save Failed.");

                }


            }

            @Override
            public void onFailure(Call<ResponseSignUp> call, Throwable t) {

                dismissProgressDialog();
                showAlert("", "Account Save Failed.");

            }
        });

    }

    private void updateAccount(){

        User user = dataManager.getUser();
        List<AccountID> accountIDs = user.getAccountIDs();
        accountIDs.add(new AccountID(etName.getText().toString(), etValue.getText().toString()));
        dataManager.setUser(user);

        adapter.setAccountIDs(dataManager.getUser().getAccountIDs());
        adapter.notifyDataSetChanged();

        Picker accountPicker = dataManager.getAccountPicker();
        List<Value> values = accountPicker.getValues();
        Value value = new Value(etValue.getText().toString(), etName.getText().toString());
        values.add(value);

        if (checkBox.isChecked()){

            checkBox.setChecked(false);
            accountPicker.setEnableDefault(true);
            accountPicker.setDefaultIndex(values.size() - 1);

        }

        dataManager.setAccountPicker(accountPicker);

        etValue.setText("");
        tiValue.setError(null);
        etName.setText("");
        tiName.setError(null);

        etName.requestFocus();

    }

    @Click(R.id.cvFinish)
    void generateButtonClicked(){
        Intent intent = new Intent(BROADCAST_CHANGE_MENU);
        intent.putExtra(MENU_ID, MENU_CODE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){

            setPopupHeight();

            if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null && dataManager.getUser().getAccountIDs().size() > 0){
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

    @AfterTextChange(R.id.etName)
    void nameChanged(){
        if (TextUtils.isEmpty(etName.getText())){
            tiName.setError(utils.getSpannableStringForEditTextError("This field is required", mContext));

        } else {
            tiName.setError(null);
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

    @Click(R.id.cvSave)
    void saveButtonClicked(){
        if (TextUtils.isEmpty(etName.getText())){
            tiName.setError(utils.getSpannableStringForEditTextError("This field is required", mContext));

        }
        if (TextUtils.isEmpty(etValue.getText())){
            tiValue.setError(utils.getSpannableStringForEditTextError("This field is required", mContext));

        }

        if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etValue.getText())){
            saveAccount();
        }
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