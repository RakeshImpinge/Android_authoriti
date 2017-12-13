package com.curtisdigital.authoriti.ui.auth;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.items.SpinnerItem;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.ViewUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_account_manager)
public class AccountManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupWindow.OnDismissListener {

    @Bean
    AuthoritiUtils utils;

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

    List<String> list;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition;
    private int popupHeight, popupWidth;

    @AfterViews
    void callAfterViewInjection(){
        setSpinner();
    }

    private void setSpinner(){

        popupHeight = (int) ViewUtils.convertDpToPixel(150, this);
        popupWidth = ViewUtils.getScreenWidth(this) - (int) ViewUtils.convertDpToPixel(64, this);

        list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");

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
        selectedPosition = 0;
    }

    private void setAccount(){
        if (list != null){
            etAccount.setText(list.get(selectedPosition));
        }
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
    }

    @Click(R.id.spinner)
    void spinnerClicked(){
        if (!opened){
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
