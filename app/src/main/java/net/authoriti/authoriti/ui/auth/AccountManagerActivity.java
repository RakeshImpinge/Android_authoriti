package net.authoriti.authoriti.ui.auth;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.authoriti.authoriti.MainActivity_;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.api.model.request.RequestSignUp;
import net.authoriti.authoriti.api.model.response.ResponseSignUp;
import net.authoriti.authoriti.core.SecurityActivity;
import net.authoriti.authoriti.ui.alert.AccountAddDialog;
import net.authoriti.authoriti.ui.help.HelpActivity_;
import net.authoriti.authoriti.ui.items.AccountAddItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.crypto.CryptoKeyPair;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
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
public class AccountManagerActivity extends SecurityActivity implements SecurityActivity
        .TouchIDEnableAlertListener, AccountAddDialog.AccountAddDialogListener, AccountAddItem
        .AccountAddItemListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.cvFinish)
    CardView cvFinish;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    @ViewById(R.id.tvEmpty)
    TextView tvEmpty;

    FastItemAdapter<AccountAddItem> adapter;


    private AccountAddDialog accountAddDialog;

    private CryptoKeyPair keyPair;

    private boolean saveSuccess = false;

    @AfterViews
    void callAfterViewInjection() {

        keyPair = dataManager.getCryptoKeyPair(dataManager.password, "");

        dataManager.accountIDs = new ArrayList<>();

        updateFinishButton();

        accountAddDialog = new AccountAddDialog(this);
        accountAddDialog.setListener(this);

        adapter = new FastItemAdapter<AccountAddItem>();
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

    }

    private void updateFinishButton() {
        if (dataManager.accountIDs != null && dataManager.accountIDs.size() > 0) {
            cvFinish.setEnabled(true);
            cvFinish.setAlpha(1.0f);
        } else {
            cvFinish.setEnabled(false);
            cvFinish.setAlpha(0.1f);
        }
    }

    private void saveAccount(String name, String id, boolean setDefault) {
        if (setDefault) {
            dataManager.defaultAccountSelected = true;
            dataManager.defaultAccountIndex = dataManager.accountIDs.size();
        }

        if (dataManager.accountIDs == null) {
            dataManager.accountIDs = new ArrayList<>();
        }

        AccountID accountID = new AccountID(name, id);
        dataManager.accountIDs.add(accountID);

        showAccount();

        updateFinishButton();
    }

    private void showAccount() {

        tvEmpty.setVisibility(View.GONE);

        if (adapter != null) {
            adapter.clear();
        } else {
            adapter = new FastItemAdapter<>();
        }

        for (int i = 0; i < dataManager.accountIDs.size(); i++) {
            adapter.add(new AccountAddItem(dataManager.accountIDs.get(i), dataManager
                    .defaultAccountSelected && i == dataManager.defaultAccountIndex, this));
        }
    }

    private void signUp() {
        Log.e("Private Key", keyPair.getPrivateKey());
        Log.e("Public Key", keyPair.getPublicKey());
        Log.e("Salt", keyPair.getSalt());
        RequestSignUp requestSignUp = new RequestSignUp(dataManager.password, keyPair
                .getPublicKey(), keyPair.getSalt(), dataManager.inviteCode, dataManager.accountIDs);
        displayProgressDialog("Sign Up...");
        AuthoritiAPI.APIService().signUp(requestSignUp).enqueue(new Callback<ResponseSignUp>() {
            @Override
            public void onResponse(Call<ResponseSignUp> call, Response<ResponseSignUp> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    saveSuccess = true;
                    mFingerPrintAuthHelper.startAuth();
                    hideKeyboard();
                    fetchSignUpInfo(response.body());
                    checkFingerPrintAuth();
                } else {
                    showAlert("", "Sign Up Failed. Try Again Later.");
                }
            }

            @Override
            public void onFailure(Call<ResponseSignUp> call, Throwable t) {

                dismissProgressDialog();
                showAlert("", "Sign Up Failed. Try Again Later.");
            }
        });

    }

    private void fetchSignUpInfo(ResponseSignUp responseSignUp) {
        User user = responseSignUp.getUser();
        user.setUserId(responseSignUp.getUserId());
        user.setToken(responseSignUp.getToken());
        user.setPassword(dataManager.password);
        user.setInviteCode(dataManager.inviteCode);
        try {

            AesCbcWithIntegrity.SecretKeys keys;

            String salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
            keys = AesCbcWithIntegrity.generateKeyFromPassword(dataManager.password, salt);

            String keyStr = AesCbcWithIntegrity.keyString(keys);

            user.setEncryptKey(keyStr);

            try {

                user.setEncryptPrivateKey(AesCbcWithIntegrity.encrypt(keyPair.getPrivateKey(),
                        keys).toString());
                user.setEncryptSalt(AesCbcWithIntegrity.encrypt(keyPair.getSalt(), keys).toString
                        ());
                user.setEncryptPassword(AesCbcWithIntegrity.encrypt(dataManager.password, keys)
                        .toString());

                Log.e("getDefaultAccountID", dataManager.getDefaultAccountID().getTitle());
                Log.e("getDefaultAccountID", dataManager.getDefaultAccountID().getValue());

                for (int i = 0; i < user.getAccountIDs().size(); i++) {
                    AccountID accountID = user.getAccountIDs().get(i);
                    accountID.setIdentifier(CryptoUtil.hash(accountID.getIdentifier()));
                    user.getAccountIDs().set(i,accountID);
                }

                if (dataManager.defaultAccountSelected) {
                    dataManager.setDefaultAccountID(new Value(user.getAccountIDs().get
                            (dataManager.defaultAccountIndex)
                            .getIdentifier(), user.getAccountIDs().get
                            (dataManager.defaultAccountIndex).getType()
                    ));
                }
                dataManager.setUser(user);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.btnAdd)
    void addButtonClicked() {

        showAccountAddDialog();

    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        HelpActivity_.intent(mContext).start();
    }


    @Click(R.id.cvFinish)
    void finishButtonClicked() {

        signUp();

    }

    private void updateLoginState() {

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    private void checkFingerPrintAuth() {

        if (isBelowMarshmallow || fingerPrintHardwareNotDetected) {

            updateLoginState();
            goHome();

        } else {

            setListener(this);
            showTouchIDEnableAlert();

        }

    }

    private void goHome() {
        dataManager.setScheme(null);
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void enableFingerPrintAndGoHome() {

        removeListener();

        if (dataManager != null && dataManager.getUser() != null) {

            User user = dataManager.getUser();
            user.setFingerPrintAuthEnabled(true);
            dataManager.setUser(user);

            updateLoginState();
            goHome();
        }

    }

    @Override
    public void allowButtonClicked() {
        hideTouchIDEnabledAlert();
        if (fingerPrintNotRegistered) {
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

    private void showAccountAddDialog() {

        if (accountAddDialog == null) {

            accountAddDialog = new AccountAddDialog(this);
            accountAddDialog.setListener(this);

        } else {

            accountAddDialog.init();
        }

        if (!isFinishing() && !accountAddDialog.isShowing()) {

            accountAddDialog.show();
        }

    }

    private void hideAccountAddDialog() {

        if (accountAddDialog != null) {

            accountAddDialog.dismiss();
            accountAddDialog = null;

        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void accountAddDialogOKButtonClicked(String name, String id, boolean setDefault) {

        saveAccount(name, id, setDefault);
        hideAccountAddDialog();

    }

    @Override
    public void accountAddDialogCancelButtonClicked() {

        hideAccountAddDialog();

    }

    @Override
    public void itemDelete(int position) {
        dataManager.accountIDs.remove(position);
        if (dataManager.defaultAccountSelected &&
                (dataManager.defaultAccountIndex) == position) {
            dataManager.defaultAccountSelected = false;
            dataManager.defaultAccountIndex = -1;
        }
        showAccount();
        updateFinishButton();
    }
}
