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
import net.authoriti.authoriti.core.BaseFragment;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/17/17.
 */

@EFragment(R.layout.fragment_account_chase)
public class AccountChaseFragment extends BaseFragment implements AccountConfirmDialog
        .AccountConfirmDialogListener {

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

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            InviteCodeActivity_.intent(getActivity()).showBack(true).isSyncRequired(true)
                    .start();
        }
    };

    @AfterViews
    void callAfterViewInjection() {
        adapter = new FastItemAdapter<AccountConfirmItem>();
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

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
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new
                IntentFilter(BROADCAST_SYNC_BUTTON_CLICKED));
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
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
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
//        values.add(value);
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
}
