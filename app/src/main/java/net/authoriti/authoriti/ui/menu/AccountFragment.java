package net.authoriti.authoriti.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import net.authoriti.authoriti.ui.alert.AccountDownloadDialog;
import net.authoriti.authoriti.ui.items.AccountAddItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;
import net.authoriti.authoriti.utils.crypto.EcDSA;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by mac on 11/30/17.
 */

@EFragment(R.layout.fragment_account)
public class AccountFragment extends BaseFragment implements AccountAddItem
        .AccountAddItemListener, AccountAddDialog.AccountAddDialogListener, AccountManagerUpdateInterfce, AccountDownloadDialog.AccountDownloadDialogListener {


    @Bean
    AuthoritiUtils utils;
    @Bean
    AuthoritiData dataManager;
    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;
    AccountAdaper adapter;
    List<AccountID> accountList = new ArrayList<>();
    private AccountAddDialog accountAddDialog;
    BroadcastReceiver broadcastReceiver, broadcastCloudReceiver, broadcastSyncReceiver;
    AccountDownloadDialog accountDownloadDialog;


    @AfterViews
    void callAfterViewInjection() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_ADD_BUTTON_CLICKED)) {
                    showAccountAddDialog();
                }
            }
        };

        broadcastCloudReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showAccountDownloadDialog();
            }
        };

        broadcastSyncReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showAccounts();
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new
                IntentFilter(BROADCAST_ADD_BUTTON_CLICKED));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastCloudReceiver, new
                IntentFilter(BROADCAST_CLOUD_BUTTON_CLICKED));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastSyncReceiver, new
                IntentFilter(BROADCAST_SYNC_BUTTON_CLICKED));


        adapter = new AccountAdaper(accountList, this);
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

        showAccounts();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateMenuToolbar(Constants.MENU_ACCOUNT);

    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastCloudReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastSyncReceiver);
    }

    private void showAccounts() {
        accountList.clear();
        if (dataManager.getUser().getAccountIDs() != null) {
            for (int i = 0; i < dataManager.getUser().getAccountIDs().size(); i++) {
                AccountID accountID = dataManager.getUser().getAccountIDs().get(i);
                if (dataManager.getDefaultAccountID().getTitle().equals(accountID.getType()) &&
                        dataManager.getDefaultAccountID().getValue().equals(accountID
                                .getIdentifier())) {
                }
                accountList.add(dataManager.getUser().getAccountIDs().get(i));
            }
        }
        Collections.sort(accountList, new Comparator<AccountID>() {
            @Override
            public int compare(AccountID accountID, AccountID t1) {
                return accountID.getCustomer().compareTo(t1.getCustomer());
            }
        });

        for (int i = 0; i < accountList.size(); i++) {
            if (dataManager.getDefaultAccountID().getTitle().equals(accountList.get(i).getType()) &&
                    dataManager.getDefaultAccountID().getValue().equals(accountList.get(i).getIdentifier())) {
                adapter.mDefaultPostion = i;
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void saveAccount(final String name, final String id, final boolean setDefault) {
        RequestUserUpdate request = new RequestUserUpdate();
        Log.e("AccountFragment", id);
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
        List<AccountID> accountIDs = user.getAccountIDs();
        accountIDs.add(new AccountID(name, id, true));
        dataManager.setUser(user);
        if (setDefault) {
            dataManager.setDefaultAccountID(new Value(CryptoUtil.hash(id), name));
            utils.updateDefaultvalues(getActivity(), PICKER_ACCOUNT, new Value(CryptoUtil.hash
                    (id), name), true);
        }
        showAccounts();
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


    private void showAccountAddDialog() {

        if (accountAddDialog == null) {

            accountAddDialog = new AccountAddDialog(mActivity);
            accountAddDialog.setListener(this);

        } else {

            accountAddDialog.init();
        }

        if (!mActivity.isFinishing() && !accountAddDialog.isShowing()) {

            accountAddDialog.show();
        }

    }

    private void hideAccountAddDialog() {

        if (accountAddDialog != null) {

            accountAddDialog.dismiss();
            accountAddDialog = null;

        }
    }

    @Click(R.id.cvFinish)
    void finishButtonClicked() {

        Intent intent = new Intent(BROADCAST_CHANGE_MENU);
        intent.putExtra(MENU_ID, MENU_CODE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

    }

    @Override
    public void itemDelete(int position) {

        if (dataManager.getUser().getAccountIDs().size() == 1) {
            showAlert("", "You must have at least one Account/ID!");
        } else {
            deleteAccount(position);
        }

    }

    @Override
    public void accountAddDialogOKButtonClicked(String name, String id, boolean setDefault) {

        hideAccountAddDialog();
        saveAccount(name, id, setDefault);
    }

    @Override
    public void accountAddDialogCancelButtonClicked() {

        hideAccountAddDialog();

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
    public void accountDownloadDialogOKButtonClicked(String inviteCode, String userName, String password) {
        hideAccountDownloadDialog();
        signUp(inviteCode, userName, password);
    }

    @Override
    public void accountDownloadDialogCancelButtonClicked() {
        hideAccountDownloadDialog();
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
        String publicKey = new EcDSA().getPublicKey(CryptoUtil.base62ToInt(privateKey));
        RequestSignUpChase requestSignUp = new RequestSignUpChase(password,
                publicKey,
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
                showAlert("", "SFailed. Try Again Later.");
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

}
