package com.curtisdigital.authoriti.ui.auth;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
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
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.items.SpinnerItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import se.simbio.encryption.Encryption;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements PopupWindow.OnDismissListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tiAccount)
    TextInputLayout tiAccount;

    @ViewById(R.id.etAccount)
    EditText etAccount;

    @ViewById(R.id.tiPassword)
    TextInputLayout tiPassword;

    @ViewById(R.id.etPassword)
    EditText etPassword;

    @ViewById(R.id.spinner)
    View spinner;

    @ViewById(R.id.checkbox)
    CheckBox checkBox;

    List<AccountID> list;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition = 0;
    private int popupHeight, popupWidth;

    Picker picker;

    @AfterViews
    void callAfterViewInjection(){

        checkDefault();

        setSpinner();
        setAccount();

        tiAccount.setError(null);
        tiPassword.setError(null);

    }

    private void checkDefault(){

        if (dataManager.getUser() != null && dataManager.getAccountPicker() != null){

            picker = dataManager.getAccountPicker();

            if (picker.isEnableDefault()){

                selectedPosition = picker.getDefaultIndex();

            }
        }

    }

    private void setSpinner(){

        setPopupHeight();
        popupWidth = ViewUtils.getScreenWidth(this) - (int) ViewUtils.convertDpToPixel(64, this);

        list = new ArrayList<>();
        if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null && dataManager.getUser().getAccountIDs().size() > 0){
            list = dataManager.getUser().getAccountIDs();
        }

        SpinnerItem item = new SpinnerItem(this, list);

        lv = new ListView(this);
        lv.setAdapter(item);
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

        if (list != null && list.size() > 0){

            etAccount.setText(list.get(selectedPosition).getType());

            if (picker != null && picker.isEnableDefault() && picker.getDefaultIndex() == selectedPosition){

                checkBox.setChecked(true);

            } else {

                checkBox.setChecked(false);

            }
        }
    }

    private void setPopupHeight(){

        if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null){

            if (dataManager.getUser().getAccountIDs().size() > 2){

                popupHeight = (int) ViewUtils.convertDpToPixel(150, this);

            } else {

                popupHeight = (int) ViewUtils.convertDpToPixel(50 * dataManager.getUser().getAccountIDs().size(), this);

            }

        } else {

            popupHeight = 0;

        }
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Click(R.id.cvSign)
    void signButtonClicked(){

        if (TextUtils.isEmpty(etAccount.getText())){

            tiAccount.setError(utils.getSpannableStringForEditTextError("Choose your account", this));

        }

        if (TextUtils.isEmpty(etPassword.getText())){

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (!TextUtils.isEmpty(etAccount.getText()) && !TextUtils.isEmpty(etPassword.getText())){

            if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null && dataManager.getUser().getAccountIDs().size() > 0){

                Encryption encryption = Encryption.getDefault(dataManager.key, dataManager.salt, dataManager.iv);
                boolean matched = false;

                for (AccountID accountID : dataManager.getUser().getAccountIDs()){

                    if (accountID.getType().equals(etAccount.getText().toString()) && dataManager.getUser().getEncryptPassword().equals(encryption.encryptOrNull(etPassword.getText().toString()))){

                        matched = true;
                        break;

                    }
                }

                if (matched){

                    updateLoginState();
                    goHome();

                } else {

                    showAlert("", "Invalid username or password!");

                }

            }
        }
    }

    private void updateLoginState(){

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    @AfterTextChange(R.id.etPassword)
    void passwordChanged(){
        if (!TextUtils.isEmpty(etPassword.getText())){
            tiPassword.setError(null);
        }
    }

    @CheckedChange(R.id.checkbox)
    void setDefault(boolean checked){

        if (dataManager.getUser() != null && picker != null){


            if (checked){

                picker.setEnableDefault(true);
                picker.setDefaultIndex(selectedPosition);

                dataManager.setAccountPicker(picker);


            } else {

                if (selectedPosition == picker.getDefaultIndex()){

                    picker.setEnableDefault(false);

                    dataManager.setAccountPicker(picker);

                }

            }

        }

    }

    @Click(R.id.cvSet)
    void setButtonClicked(){
        InviteCodeActivity_.intent(mContext).start();
    }

    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){

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

    @Override
    public void onDismiss() {
        pw = null;
        opened = false;
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
}
