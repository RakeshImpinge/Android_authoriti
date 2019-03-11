package net.authoriti.authoriti.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.authoriti.authoriti.MainActivity;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.api.model.request.RequestSignUpChase;
import net.authoriti.authoriti.api.model.request.RequestUserUpdate;
import net.authoriti.authoriti.api.model.response.ResponseSignUp;
import net.authoriti.authoriti.api.model.response.ResponseSignUpChase;
import net.authoriti.authoriti.core.AccountManagerUpdateInterfce;
import net.authoriti.authoriti.core.BaseFragment;
import net.authoriti.authoriti.ui.alert.AccountAddDialog;
import net.authoriti.authoriti.ui.alert.AccountConfirmDialog;
import net.authoriti.authoriti.ui.alert.AccountDownloadDialog;
import net.authoriti.authoriti.ui.auth.InviteCodeActivity_;
import net.authoriti.authoriti.ui.items.AccountConfirmItem;
import net.authoriti.authoriti.ui.items.AccountItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;

import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.crypto.CryptoKeyPair;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/17/17.
 */

@EFragment(R.layout.fragment_account)
public class AccountChaseFragment extends BaseFragment implements AccountConfirmDialog
        .AccountConfirmDialogListener, AccountAddDialog.AccountAddDialogListener, AccountManagerUpdateInterfce, AccountDownloadDialog.AccountDownloadDialogListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.id_account_confirm_fragment)
    View view;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    AccountAdaper adapter;
    AccountConfirmDialog accountConfirmDialog;
    AccountDownloadDialog accountDownloadDialog;

    AccountID selectedAccountId;

    private AccountAddDialog accountAddDialog;
    BroadcastReceiver broadcastAddReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAccountAddDialog();
        }
    };

    BroadcastReceiver broadcastCloudReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAccountDownloadDialog();
        }
    };

    BroadcastReceiver broadcastSyncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAccounts();
        }
    };


    List<AccountID> accountList = new ArrayList<>();


    @AfterViews
    void callAfterViewInjection() {
        adapter = new AccountAdaper(accountList, this);
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

        accountAddDialog = new AccountAddDialog(getActivity());
        accountAddDialog.setListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastAddReceiver, new
                IntentFilter(BROADCAST_ADD_BUTTON_CLICKED));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastCloudReceiver, new
                IntentFilter(BROADCAST_CLOUD_BUTTON_CLICKED));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastSyncReceiver, new
                IntentFilter(BROADCAST_SYNC_DONE));
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).updateMenuToolbar(Constants.MENU_ACCOUNT);
        showAccounts();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastAddReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastCloudReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastSyncReceiver);
    }

    public void showAccounts() {
        System.out.println("Show Accounts(Chase)");
        accountList.clear();
        int selfAccountCount = 0;

        User user = dataManager.getUser();
        if (user.getAccountIDs() != null && user.getAccountIDs().size() > 0) {
            for (int i = 0; i < user.getAccountIDs().size(); i++) {
                AccountID accountID = user.getAccountIDs().get(i);
                final String id = accountID.getIdentifier();
                user.getAccountIDs().set(i, accountID);

                if (dataManager.getDefaultAccountID().getTitle().equals(user.getAccountIDs().get
                        (i).getType()) &&
                        dataManager.getDefaultAccountID().getValue().equals(user.getAccountIDs()
                                .get(i)
                                .getIdentifier())) {
                    adapter.mDefaultPostion = i;
                }
                accountList.add(user.getAccountIDs().get(i));
                if (user.getAccountIDs().get(i).getCustomer().equals("")) {
                    selfAccountCount = selfAccountCount + 1;
                }
            }
        }


//         Its only to display add + button if no self Id is added
        if (selfAccountCount == 0) {
            accountList.add(accountList.size(), new AccountID());
        }

        Collections.sort(accountList, new Comparator<AccountID>() {
            @Override
            public int compare(AccountID accountID, AccountID t1) {
                if (accountID.getCustomer().equalsIgnoreCase("")) {
                    if (t1.getCustomer().equalsIgnoreCase("")) {
                        return accountID.getCustomer().compareTo(t1.getCustomer());
                    }
                    return 5000;
                }
                return accountID.getCustomer().compareTo(t1.getCustomer());
            }
        });
//        if (selfRegIndex != -1) {
//            AccountID selfReg = accountList.remove(selfRegIndex);
//        }

        for (int i = 0; i < accountList.size(); i++) {
            if (dataManager.getDefaultAccountID().getTitle().equals(accountList.get(i).getType()) &&
                    dataManager.getDefaultAccountID().getValue().equals(accountList.get(i).getIdentifier())) {
                adapter.mDefaultPostion = i;
            }
        }
        adapter.notifyDataSetChanged();
//
//        if (user.getUnconfirmedAccountIDs() != null && user.getUnconfirmedAccountIDs().size() > 0) {
//            for (int i = 0; i < user.getUnconfirmedAccountIDs().size(); i++) {
//                adapter.add(new AccountItem(user.getUnconfirmedAccountIDs().get(i), false, i));
//            }
//        }
    }

    private void saveAccountName(final String id, final boolean setDefault) {
        String token = "Bearer " + dataManager.getUser().getToken();
        displayProgressDialog("");
        AuthoritiAPI.APIService().confirmAccountValue(token, id).enqueue(new Callback<JsonObject>
                () {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status") != null) {
                        if (jsonObject.get("status").getAsString().equals("Success")) {
                            Snackbar.make(view, "Add Account Successfully", 1000).show();
                            updateAccount(id, setDefault);
                        } else {
                            showAlert("", "Failed to confirm your account number. Try again later" +
                                    ".");
                        }
                    } else {
                        showAlert("", "Failed to confirm your account number. Try again later.");
                    }
                } else {
                    showAlert("", "Failed to confirm your account number. Try again later.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                dismissProgressDialog();
                showAlert("", "Failed to confirm your account number. Try again later.");

            }
        });
    }

    private void updateAccount(String id, boolean setDefault) {
        AccountID accountID = new AccountID(selectedAccountId.getType(), id, true);
        User user = dataManager.getUser();
        user.getAccountIDs().add(accountID);

        for (int i = 0; i < user.getUnconfirmedAccountIDs().size(); i++) {

            AccountID accountID1 = user.getUnconfirmedAccountIDs().get(i);
            if (accountID1.getType().equals(selectedAccountId.getType())) {
                user.getUnconfirmedAccountIDs().remove(accountID1);
            }

        }

        dataManager.setUser(user);
//        Picker accountPicker = dataManager.getAccountPicker();
//        List<Value> values = accountPicker.getValues();
//        Value value = new Value(id, selectedAccountId.getType());
//        values.ic_add(value);
//        if (setDefault) {
//
//            accountPicker.setEnableDefault(true);
//            accountPicker.setDefaultIndex(values.size() - 1);
//
//        }
//
//        dataManager.setAccountPicker(accountPicker);

        if (setDefault) {
            dataManager.setDefaultAccountID(new Value(id, selectedAccountId.getType()));
        }

        showAccounts();

    }

    @Click(R.id.cvFinish)
    void generateButtonClicked() {
        Intent intent = new Intent(BROADCAST_CHANGE_MENU);
        intent.putExtra(MENU_ID, MENU_CODE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void showAccountConfirmDialog() {

        if (accountConfirmDialog == null) {

            accountConfirmDialog = new AccountConfirmDialog(mActivity);
            accountConfirmDialog.setListener(this);

        } else {

            accountConfirmDialog.init();

        }

        if (!mActivity.isFinishing() && !accountConfirmDialog.isShowing()) {

            accountConfirmDialog.show();
        }
    }

    private void hideAccountConfirmDialog() {

        if (accountConfirmDialog != null) {

            accountConfirmDialog.dismiss();
            accountConfirmDialog = null;
        }
    }

    private void showAccountDownloadDialog() {
        if (accountDownloadDialog == null) {
            accountDownloadDialog = new AccountDownloadDialog(mActivity);
            accountDownloadDialog.setListener(this);
        } else {
            accountDownloadDialog.init();
        }
        if (!mActivity.isFinishing() && !accountDownloadDialog.isShowing()) {
            accountDownloadDialog.show();
        }
    }

    private void hideAccountDownloadDialog() {
        if (accountDownloadDialog != null) {
            accountDownloadDialog.dismiss();
            accountDownloadDialog = null;
        }
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


    private void showAccountAddDialog() {
        if (accountAddDialog == null) {
            accountAddDialog = new AccountAddDialog(getActivity());
            accountAddDialog.setListener(this);
        } else {
            accountAddDialog.init();
        }
        if (!getActivity().isFinishing() && !accountAddDialog.isShowing()) {
            accountAddDialog.show();
        }
    }

    @Override
    public void accountAddDialogOKButtonClicked(String name, String id, boolean setDefault) {
        hideAccountAddDialog();
        saveAccount(name, id, setDefault);
    }

    private void saveAccount(final String name, final String id, final boolean setDefault) {
        RequestUserUpdate request = new RequestUserUpdate();
        AccountID accountID = new AccountID(name, id, true);
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);
        request.setAccountIDs(accountIDs);

        String token = "Bearer " + dataManager.getUser().getToken();
        displayProgressDialog("");
        AuthoritiAPI.APIService().updateUser(token, request).enqueue(new Callback<ResponseSignUp>
                () {
            @Override
            public void onResponse(Call<ResponseSignUp> call, Response<ResponseSignUp> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    addAccount(name, id, setDefault);
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

    private void addAccount(String name, String id, boolean setDefault) {
        User user = dataManager.getUser();
        dataManager.accountIDs = user.getAccountIDs();
        if (dataManager.accountIDs == null) {
            dataManager.accountIDs = new ArrayList<>();
        }
        if (setDefault) {
            dataManager.defaultAccountSelected = true;
            dataManager.defaultAccountIndex = dataManager.accountIDs.size();
            dataManager.setDefaultAccountID(new Value(CryptoUtil.hash(id), name));
            utils.updateDefaultvalues(getActivity(), PICKER_ACCOUNT, new Value(CryptoUtil.hash
                    (id), name), true);
        }
        Log.e("AddAccount", id);
        AccountID accountID = new AccountID(name, id, true);
        accountID.setIdentifier(accountID.getIdentifier());
        dataManager.accountIDs.add(accountID);
        user.setAccountIDs(dataManager.accountIDs);
        dataManager.setUser(user);

        showAccounts();


//        adapter.clear();
//        for (int i = 0; i < user.getAccountIDs().size(); i++) {
//            boolean isDefault = false;
//            if (dataManager.getDefaultAccountID().getTitle().equals(user.getAccountIDs().get
//                    (i).getType()) &&
//                    dataManager.getDefaultAccountID().getValue().equals(user.getAccountIDs()
//                            .get(i)
//                            .getIdentifier())) {
//                isDefault = true;
//            }
//            adapter.add(new AccountItem(user.getAccountIDs().get(i), isDefault, i));
//        }
    }

    private void deleteAccount(int position) {
        User user = dataManager.getUser();
        List<AccountID> accountIDs = user.getAccountIDs();
        if (dataManager.getDefaultAccountID().getTitle().equals(accountIDs.get(position).getType
                ()) && dataManager.getDefaultAccountID().getValue().equals(accountIDs.get(position)
                .getIdentifier())) {

            // Updating saved default values with the first index if the saved value contain
            // deleted record.
            utils.deleteDefaultvalues(getActivity(), PICKER_ACCOUNT,
                    new Value(accountIDs.get(position)
                            .getIdentifier(), accountIDs.get(position).getType()),
                    new Value(accountIDs.get(0).getIdentifier(), accountIDs.get(0).getType()));

            // Saving default saved account to blank
            dataManager.setDefaultAccountID(new Value("", ""));
        }
        accountIDs.remove(position);
        dataManager.setUser(user);
        showAccounts();
    }


    @Override
    public void accountAddDialogCancelButtonClicked() {
        hideAccountAddDialog();
    }

    private void hideAccountAddDialog() {
        if (accountAddDialog != null) {
            accountAddDialog.dismiss();
            accountAddDialog = null;
        }
    }

    @Override
    public void deleted(String accountId) {
        List<AccountID> accountIDS = dataManager.getUser().getAccountIDs();
        int nAccounts = accountIDS.size();
        for (int i = 0; i < nAccounts; i++) {
            if (accountIDS.get(i).getIdentifier().equalsIgnoreCase(accountId)) {
                deleteAccount(i);
                break;
            }
        }
    }

    @Override
    public void addSelfSigned() {
        showAccountAddDialog();
    }

    @Override
    public void syncId(String ID) {
        ((MainActivity) getActivity()).syncButtonClicked(ID);
    }

    @Override
    public void accountDownloadDialogOKButtonClicked(String inviteCode, String userName, String password) {
        hideAccountDownloadDialog();
        signUp(inviteCode, userName, password);
    }

    private void signUp(String inviteCode, final String userName, String password) {

        AccountID accountID = new AccountID("", userName, false);
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);

        AesCbcWithIntegrity.SecretKeys keys;
        String keyStr = dataManager.getUser().getEncryptKey();
        String privateKey = "";
        try {
            keys = AesCbcWithIntegrity.keys(keyStr);
            AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity.CipherTextIvMac
                    (dataManager.getUser().getEncryptPrivateKey());
            privateKey = AesCbcWithIntegrity.decryptString(civ, keys);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String dummyPublicKey = privateKey;
        RequestSignUpChase requestSignUp = new RequestSignUpChase(password,
                dummyPublicKey,
                "",
                inviteCode, accountIDs);

        displayProgressDialog("Please wait...");

        AuthoritiAPI.APIService().signUpChase(requestSignUp).enqueue(new Callback<ResponseSignUpChase>() {
            @Override
            public void onResponse(Call<ResponseSignUpChase> call, Response<ResponseSignUpChase>
                    response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    userInfo(response.body());
                } else {
                    showAlert("", "Failed. Try Again Later.");
                }
            }

            @Override
            public void onFailure(Call<ResponseSignUpChase> call, Throwable t) {
                dismissProgressDialog();
                showAlert("", "Failed. Try Again Later.");
            }
        });

    }

    private void userInfo(ResponseSignUpChase body) {
        User user = dataManager.getUser();
        user.setToken(body.getToken());
        List<AccountID> savedAccountIDs = user.getAccountIDs();
        List<AccountID> newAccountIDs = body.getAccounts();
        List<AccountID> newIds = new ArrayList<>();
        List<String> downloadIdList = user.getDownloadedWalletIDList();
        if (!downloadIdList.contains(body.getId())) {
            downloadIdList.add(body.getId());
        }

        for (int i = 0; i < newAccountIDs.size(); i++) {
            System.out.println("Checking: " + newAccountIDs.get(i));
            boolean isContained = false;
            newAccountIDs.get(i).setCustomer(body.getCustomerName());
            newAccountIDs.get(i).setCustomer_ID(body.getId());
            for (int k = 0; k < savedAccountIDs.size(); k++) {
                if (savedAccountIDs.get(k).getIdentifier().equals(newAccountIDs.get(i)
                        .getIdentifier())
                        && savedAccountIDs.get(k).getType().equals(newAccountIDs.get(i)
                        .getType())) {
                    isContained = true;
                    break;
                } else {
                }
            }
            if (!isContained) {
                newIds.add(newAccountIDs.get(i));
            }
        }
        savedAccountIDs.addAll(newIds);
        user.setAccountIDs(savedAccountIDs);
        dataManager.setUser(user);
        showAccounts();
    }


    @Override
    public void accountDownloadDialogCancelButtonClicked() {
        hideAccountDownloadDialog();
    }
}
