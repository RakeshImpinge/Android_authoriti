package net.authoriti.authoriti.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.MainActivity_;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.api.model.response.ResponseSignUp;
import net.authoriti.authoriti.core.AccountManagerUpdateInterfce;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.alert.AccountAddDialog;
import net.authoriti.authoriti.ui.items.AccountAddItem;
import net.authoriti.authoriti.ui.menu.AccountFragment;
import net.authoriti.authoriti.ui.menu.AccountFragment_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.crypto.Crypto;
import net.authoriti.authoriti.utils.crypto.CryptoKeyPair;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_account_manager)
public class AccountManagerActivity extends BaseActivity implements  AccountAddDialog.AccountAddDialogListener, AccountAddItem
        .AccountAddItemListener, AccountManagerUpdateInterfce {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    private AccountAddDialog accountAddDialog;
    private CryptoKeyPair keyPair;

    @Extra
    String licenceID = "";


    @AfterViews
    void callAfterViewInjection() {
        dataManager.accountIDs = new ArrayList<>();

        accountAddDialog = new AccountAddDialog(this);
        accountAddDialog.setListener(this);

        generateKeypair();

        AccountFragment accountFragment = AccountFragment_.builder().build();
        accountFragment.signupInProgress = true;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, accountFragment)
                .commitAllowingStateLoss();

        if (licenceID != null && !licenceID.equals("")) {
            accountFragment.licenceID = licenceID;
        }
    }


    public void fetchSignUpInfo(ResponseSignUp responseSignUp, User currentUser) {
        List<AccountID> existingAccounts = dataManager.getUser().getAccountIDs();

        User user = responseSignUp.getUser();
        if (currentUser.getUserId() == null || currentUser.getToken() == null) {
            user.setUserId(responseSignUp.getUserId());
            user.setToken(responseSignUp.getToken());
        } else {
            user.setUserId(currentUser.getUserId());
            user.setToken(currentUser.getToken());
        }
        user.setEncryptKey(currentUser.getEncryptKey());
        user.setEncryptSalt(currentUser.getEncryptSalt());
        user.setEncryptPassword(currentUser.getEncryptPassword());
        user.setEncryptPrivateKey(currentUser.getEncryptPrivateKey());

        user.setPassword(currentUser.getPassword());
        // To update existing added invite code
        user.setInvite(dataManager.getUser().getInviteCode());
        user.setInviteCodeComma(dataManager.inviteCode);

        try {
            int szUserAccounts = user.getAccountIDs().size();
            for (int i = 0; i < szUserAccounts; i++) {
                AccountID accountID = user.getAccountIDs().get(i);
                if (!accountID.getHashed()) {
                    accountID.setIdentifier(CryptoUtil.hash(accountID.getIdentifier()));
                } else {
                    accountID.setIdentifier(accountID.getIdentifier());
                }
                user.getAccountIDs().set(i, accountID);
            }

            if (dataManager.defaultAccountSelected) {
                dataManager.setDefaultAccountID(new Value(user.getAccountIDs().get
                        (dataManager.defaultAccountIndex)
                        .getIdentifier(), user.getAccountIDs().get
                        (dataManager.defaultAccountIndex).getType()
                ));
            }
            if (existingAccounts != null) {
                user.getAccountIDs().addAll(existingAccounts);
            }
            dataManager.setUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateKeypair() {
        AesCbcWithIntegrity.SecretKeys keys;

        try {
            String salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
            keyPair = new Crypto().generateKeyPair(dataManager.password, null);
            System.out.println("Generated keypair: " + keyPair.getPrivateKey());

            keys = AesCbcWithIntegrity.generateKeyFromPassword(dataManager.password, salt);
            String keyStr = AesCbcWithIntegrity.keyString(keys);

            User user = new User();
            user.setEncryptPrivateKey(AesCbcWithIntegrity.encrypt(keyPair.getPrivateKey(),
                    keys).toString());
            user.setEncryptSalt(AesCbcWithIntegrity.encrypt(keyPair.getSalt(), keys).toString
                    ());
            user.setEncryptPassword(AesCbcWithIntegrity.encrypt(dataManager.password, keys)
                    .toString());
            user.setEncryptKey(keyStr);

            user.setAccountIDs(new ArrayList<AccountID>());

            dataManager.setUser(user);
        } catch (Exception ignore) {
        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.ivCloud)
    void cloudButtonClicked() {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_CLOUD_BUTTON_CLICKED));
    }


    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HELP_BASE + "/" +
                TOPIC_ACCOUNT_2018));
        startActivity(browserIntent);
    }

    private void updateLoginState() {
        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }


    public void goHome() {
        updateLoginState();
        User user = dataManager.getUser();
        if(utils.getTouchEnabled()){
            user.setFingerPrintAuthEnabled(true);
            user.setFingerPrintAuthStatus(TOUCH_ENABLED);
        }else
        {
            user.setFingerPrintAuthEnabled(false);
            user.setFingerPrintAuthStatus(TOUCH_DISABLED);
        }
        dataManager.setUser(user);
        dataManager.setScheme(null);
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
    }

    @Override
    public void deleted(String accountId) {
        System.out.println("Deleted: " + accountId);
    }

    @Override
    public void addSelfSigned() {
        showAccountAddDialog();
    }

    @Override
    public void syncId(String ID) {

    }
}
