package net.authoriti.authoriti.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import net.authoriti.authoriti.ui.items.AccountAddItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

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
        .AccountAddItemListener, AccountAddDialog.AccountAddDialogListener {


    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    AccountAdaper adapter;
    List<AccountID> accountList = new ArrayList<>();

    private AccountAddDialog accountAddDialog;
    BroadcastReceiver broadcastReceiver;

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

        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new
                IntentFilter(BROADCAST_ADD_BUTTON_CLICKED));

        adapter = new AccountAdaper(accountList);
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
        List<AccountID> accountIDs = user.getAccountIDs();
        accountIDs.add(new AccountID(name, CryptoUtil.hash(id)));
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
}
