package net.authoriti.authoriti.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.authoriti.authoriti.MainActivity;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.api.model.request.RequestUserUpdate;
import net.authoriti.authoriti.api.model.response.ResponseSignUp;
import net.authoriti.authoriti.core.BaseFragment;
import net.authoriti.authoriti.ui.alert.AccountAddDialog;
import net.authoriti.authoriti.ui.alert.AccountConfirmDialog;
import net.authoriti.authoriti.ui.auth.InviteCodeActivity_;
import net.authoriti.authoriti.ui.items.AccountConfirmItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;

import com.google.gson.JsonObject;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

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
 * Created by mac on 12/17/17.
 */

@EFragment(R.layout.fragment_account_chase)
public class AccountChaseFragment extends BaseFragment implements AccountConfirmDialog
        .AccountConfirmDialogListener, AccountAddDialog.AccountAddDialogListener {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.id_account_confirm_fragment)
    View view;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    FastItemAdapter<AccountConfirmItem> adapter;
    AccountConfirmDialog accountConfirmDialog;

    AccountID selectedAccountId;
    int selectedPosition;

    private AccountAddDialog accountAddDialog;


    BroadcastReceiver broadcastSyncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InviteCodeActivity_.intent(getActivity()).showBack(true).isSyncRequired(true)
                    .start();
        }
    };
    BroadcastReceiver broadcastAddReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAccountAddDialog();
        }
    };

    @AfterViews
    void callAfterViewInjection() {
        adapter = new FastItemAdapter<AccountConfirmItem>();
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

        accountAddDialog = new AccountAddDialog(getActivity());
        accountAddDialog.setListener(this);

        adapter.withOnClickListener(new FastAdapter.OnClickListener<AccountConfirmItem>() {
            @Override
            public boolean onClick(View v, IAdapter<AccountConfirmItem> adapter,
                                   AccountConfirmItem item, int position) {
                selectedAccountId = item.getAccountID();
                selectedPosition = position;
//                if (selectedAccountId.isConfirmed()) {
//                    showAlert("", "This account has already confirmed.");
//                } else {
//                    showAccountConfirmDialog();
//                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastSyncReceiver, new
                IntentFilter(BROADCAST_SYNC_BUTTON_CLICKED));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastAddReceiver, new
                IntentFilter(BROADCAST_ADD_BUTTON_CLICKED));
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
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastSyncReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastAddReceiver);
    }

    private void showAccounts() {
        if (adapter != null) {
            adapter.clear();
        } else {
            adapter = new FastItemAdapter<>();
        }
        User user = dataManager.getUser();
        if (user.getAccountIDs() != null && user.getAccountIDs().size() > 0) {
            for (int i = 0; i < user.getAccountIDs().size(); i++) {
                boolean isDefault = false;
                if (dataManager.getDefaultAccountID().getTitle().equals(user.getAccountIDs().get
                        (i).getType()) &&
                        dataManager.getDefaultAccountID().getValue().equals(user.getAccountIDs()
                                .get(i)
                                .getIdentifier())) {
                    isDefault = true;
                }
                adapter.add(new AccountConfirmItem(user.getAccountIDs().get(i), isDefault));
            }
        }

        if (user.getUnconfirmedAccountIDs() != null && user.getUnconfirmedAccountIDs().size() > 0) {
            for (int i = 0; i < user.getUnconfirmedAccountIDs().size(); i++) {
                adapter.add(new AccountConfirmItem(user.getUnconfirmedAccountIDs().get(i), false));
            }
        }
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

        AccountID accountID = new AccountID(selectedAccountId.getType(), id);
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
        AccountID accountID = new AccountID(name, id);
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
        AccountID accountID = new AccountID(name, id);
        accountID.setIdentifier(CryptoUtil.hash(accountID.getIdentifier()));
        dataManager.accountIDs.add(accountID);
        user.setAccountIDs(dataManager.accountIDs);
        dataManager.setUser(user);

        adapter.clear();
        for (int i = 0; i < user.getAccountIDs().size(); i++) {
            boolean isDefault = false;
            if (dataManager.getDefaultAccountID().getTitle().equals(user.getAccountIDs().get
                    (i).getType()) &&
                    dataManager.getDefaultAccountID().getValue().equals(user.getAccountIDs()
                            .get(i)
                            .getIdentifier())) {
                isDefault = true;
            }
            adapter.add(new AccountConfirmItem(user.getAccountIDs().get(i), isDefault));
        }
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

}
