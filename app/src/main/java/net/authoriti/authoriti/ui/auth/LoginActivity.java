package net.authoriti.authoriti.ui.auth;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import net.authoriti.authoriti.MainActivity_;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.core.SecurityActivity;
import net.authoriti.authoriti.ui.items.SpinnerItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.ViewUtils;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.tozny.crypto.android.AesCbcWithIntegrity;


import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;


import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_login)
public class LoginActivity extends SecurityActivity implements PopupWindow.OnDismissListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

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

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    @ViewById(R.id.space)
    Space space;

    List<AccountID> list;
    private PopupWindow pw;
    private ListView lv;
    private boolean opened;
    private int selectedPosition = 0;
    private int defaultPosition = -1;
    private int popupHeight, popupWidth;

    private boolean fingerPrintAuthEnabled = false;


    @AfterViews
    void callAfterViewInjection() {

        checkDefault();

        setSpinner();
        setAccount();

        tiAccount.setError(null);
        tiPassword.setError(null);

        tiPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Oswald_Regular.ttf"));


        if (dataManager != null && dataManager.getUser() != null && dataManager.getUser()
                .isFingerPrintAuthEnabled()) {

            mFingerPrintAuthHelper.startAuth();
            fingerPrintAuthEnabled = true;
            checkFingerPrintAuth();
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen) {

                    space.setVisibility(View.VISIBLE);

                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(100, mContext));
                        }
                    });

                } else {

                    space.setVisibility(View.GONE);

                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, 0);
                        }
                    });
                }
            }
        });

    }

    private void checkDefault() {
        if (dataManager.getUser() != null && dataManager.getDefaultAccountID() != null) {
            for (int i = 0; i < dataManager.getUser().getAccountIDs().size(); i++) {
                if (isDefaultUser(dataManager.getUser().getAccountIDs().get(i), dataManager
                        .getDefaultAccountID())) {
                    defaultPosition = i;
                }
            }
        }
    }

    private boolean isDefaultUser(AccountID accountID, Value defaultAccountID) {
        return accountID.getIdentifier().equals(defaultAccountID.getValue())
                && accountID.getType().equals(defaultAccountID.getTitle());
    }

    private void setSpinner() {
        setPopupHeight();
        popupWidth = ViewUtils.getScreenWidth(this) - (int) ViewUtils.convertDpToPixel(64, this);

        list = new ArrayList<>();
        if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null &&
                dataManager.getUser().getAccountIDs().size() > 0) {
            list = dataManager.getUser().getAccountIDs();
        }

//        SpinnerItem item = new SpinnerItem(this, list);
//
//        lv = new ListView(this);
//        lv.setAdapter(item);
//        lv.setDividerHeight(0);
//        lv.setOnItemClickListener(this);
//        lv.setOnItemSelectedListener(this);
//        lv.setSelector(android.R.color.transparent);
//        lv.setBackgroundResource(R.drawable.bg_spinner_pop);
//        lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void setAccount() {
        if (list != null && list.size() > 0 && selectedPosition < list.size()) {
            etAccount.setText(list.get(selectedPosition).getType());
//            if (defaultPosition == selectedPosition) {
//                checkBox.setChecked(true);
//            } else {
//                checkBox.setChecked(false);
//            }
        }
    }

    private void setPopupHeight() {

        if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null) {

            if (dataManager.getUser().getAccountIDs().size() > 2) {

                popupHeight = (int) ViewUtils.convertDpToPixel(150, this);

            } else {

                popupHeight = (int) ViewUtils.convertDpToPixel(50 * dataManager.getUser()
                        .getAccountIDs().size(), this);

            }

        } else {

            popupHeight = 0;

        }
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Click(R.id.cvSign)
    void signButtonClicked() {

        if (TextUtils.isEmpty(etAccount.getText())) {

            tiAccount.setError(utils.getSpannableStringForEditTextError("Choose your account",
                    this));

        }

        if (TextUtils.isEmpty(etPassword.getText())) {

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));

        }

        if (!TextUtils.isEmpty(etAccount.getText()) && !TextUtils.isEmpty(etPassword.getText())) {

            hideKeyboard();

            if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null &&
                    dataManager.getUser().getAccountIDs().size() > 0) {

                AesCbcWithIntegrity.SecretKeys keys;
                String keyStr = dataManager.getUser().getEncryptKey();

                try {

                    keys = AesCbcWithIntegrity.keys(keyStr);

                    String password = null;

                    try {

                        AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity
                                .CipherTextIvMac(dataManager.getUser().getEncryptPassword());
                        password = AesCbcWithIntegrity.decryptString(civ, keys);

                        if (password.equals(etPassword.getText().toString())) {

                            // save account as default
                            Value value = new Value(list.get(selectedPosition).getIdentifier(),
                                    list.get(selectedPosition).getType());
                            utils.updateDefaultvalues(getApplicationContext(), Constants
                                    .PICKER_ACCOUNT, value, true);
                            dataManager.setDefaultAccountID(value);

                            hideKeyboard();
                            updateLoginState();
                            goHome();

                        } else {

                            showAlert("Oops!", "Invalid username or password!");
                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }


                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void checkFingerPrintAuth() {

        if (!fingerPrintNotRegistered) {
            showTouchIdAlert();
        }

    }


    private void updateLoginState() {

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    @AfterTextChange(R.id.etPassword)
    void passwordChanged() {
        if (!TextUtils.isEmpty(etPassword.getText())) {
            tiPassword.setError(null);
        }
    }

    @CheckedChange(R.id.checkbox)
    void setDefault(boolean checked) {

    }

    @Click(R.id.cvSet)
    void setButtonClicked() {
        InviteCodeActivity_.intent(mContext).start();
    }

    @Click(R.id.spinner)
    void spinnerClicked() {
        if (!opened) {

            if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null &&
                    dataManager.getUser().getAccountIDs().size() > 0) {
                if (pw == null || !pw.isShowing()) {
                    pw = new PopupWindow(spinner);
                    pw.setContentView(lv);
                    pw.setWidth(popupWidth);
                    pw.setHeight(popupHeight);
                    pw.setOutsideTouchable(true);
                    pw.setFocusable(true);
                    pw.setClippingEnabled(false);
                    pw.showAsDropDown(spinner, spinner.getLeft(), 0);
                    pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_spinner_pop));
                    pw.setOnDismissListener(this);
                    opened = true;
                }

            }

        } else {
            if (pw != null) {
                pw.dismiss();
            }
        }
    }

    @Click(R.id.btnReset)
    void resetButtonClicked() {
        ResetPasswordActivity_.intent(mContext).start();
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

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        super.onAuthSuccess(cryptoObject);

        if (fingerPrintAuthEnabled) {
            dismissTouchIDAlert();
            updateLoginState();
            goHome();
        }
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        super.onAuthFailed(errorCode, errorMessage);

        if (fingerPrintAuthEnabled) {

            switch (errorCode) {
                case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                    updateTouchIDAlert("Cannot recognize your finger print. Please try again.");
                    break;
                case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                    updateTouchIDAlert("Cannot initialize finger print authentication.");
                    break;
                case AuthErrorCodes.RECOVERABLE_ERROR:
                    updateTouchIDAlert(errorMessage);
                    break;
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (fingerPrintAuthEnabled) {

            mFingerPrintAuthHelper.startAuth();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void touchIDAlertDialogCancelButtonClicked() {
        dismissTouchIDAlert();
    }
}
