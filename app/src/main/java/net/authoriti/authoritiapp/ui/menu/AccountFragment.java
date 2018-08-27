package net.authoriti.authoritiapp.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.AuthoritiAPI;
import net.authoriti.authoritiapp.api.model.AccountID;
import net.authoriti.authoritiapp.api.model.Picker;
import net.authoriti.authoritiapp.api.model.User;
import net.authoriti.authoritiapp.api.model.Value;
import net.authoriti.authoritiapp.api.model.request.RequestUserUpdate;
import net.authoriti.authoritiapp.api.model.response.ResponseSignUp;
import net.authoriti.authoritiapp.core.BaseFragment;
import net.authoriti.authoritiapp.ui.alert.AccountAddDialog;
import net.authoriti.authoritiapp.ui.items.AccountAddItem;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

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
 * Created by mac on 11/30/17.
 */

@EFragment(R.layout.fragment_account)
public class AccountFragment extends BaseFragment implements AccountAddItem.AccountAddItemListener, AccountAddDialog.AccountAddDialogListener {


    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    FastItemAdapter<AccountAddItem> adapter;
    private AccountAddDialog accountAddDialog;
    BroadcastReceiver broadcastReceiver;

    @AfterViews
    void callAfterViewInjection(){

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_ADD_BUTTON_CLICKED)){
                    showAccountAddDialog();
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ADD_BUTTON_CLICKED));

        adapter = new FastItemAdapter<AccountAddItem>();
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

        showAccounts();
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
    }

    private void showAccounts(){

        if (dataManager.getUser().getAccountIDs() != null){

            if (adapter != null){
                adapter.clear();
            } else {
                adapter = new FastItemAdapter<>();
            }

            for (int i = 0 ; i < dataManager.getUser().getAccountIDs().size() ; i ++){

                AccountID accountID = dataManager.getUser().getAccountIDs().get(i);
                adapter.add(new AccountAddItem(accountID, dataManager.getAccountPicker().isEnableDefault() && i == dataManager.getAccountPicker().getDefaultIndex(), this));


            }
        }

    }

    private void saveAccount(final String name, final String id, final boolean setDefault){

        RequestUserUpdate request = new RequestUserUpdate();
        AccountID accountID = new AccountID(name, id);
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);
        request.setAccountIDs(accountIDs);

        String token = "Bearer " + dataManager.getUser().getToken();

        displayProgressDialog("");

        AuthoritiAPI.APIService().updateUser(token, request).enqueue(new Callback<ResponseSignUp>() {
            @Override
            public void onResponse(Call<ResponseSignUp> call, Response<ResponseSignUp> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

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

    private void addAccount(String name, String id, boolean setDefault){

        User user = dataManager.getUser();
        List<AccountID> accountIDs = user.getAccountIDs();
        accountIDs.add(new AccountID(name, id));
        dataManager.setUser(user);


        Picker accountPicker = dataManager.getAccountPicker();
        List<Value> values = accountPicker.getValues();
        Value value = new Value(id, name);
        values.add(value);

        if (setDefault){

            accountPicker.setEnableDefault(true);
            accountPicker.setDefaultIndex(values.size() - 1);

        }

        dataManager.setAccountPicker(accountPicker);

        showAccounts();
    }

    private void deleteAccount(int position){

        User user = dataManager.getUser();
        List<AccountID> accountIDs = user.getAccountIDs();
        accountIDs.remove(position);
        dataManager.setUser(user);

        Picker accountPicker = dataManager.getAccountPicker();
        List<Value> values = accountPicker.getValues();
        values.remove(position);

        if (accountPicker.isEnableDefault()){

            if (accountPicker.getDefaultIndex() == position){

                accountPicker.setEnableDefault(false);

            } else {

                if (accountPicker.getDefaultIndex() != 0 && accountPicker.getDefaultIndex() == values.size()){

                    accountPicker.setDefaultIndex(accountPicker.getDefaultIndex() - 1);
                }

            }


        }

        dataManager.setAccountPicker(accountPicker);

        showAccounts();
    }


    private void showAccountAddDialog(){

        if (accountAddDialog == null){

            accountAddDialog = new AccountAddDialog(mActivity);
            accountAddDialog.setListener(this);

        } else {

            accountAddDialog.init();
        }

        if (!mActivity.isFinishing() && !accountAddDialog.isShowing()){

            accountAddDialog.show();
        }

    }

    private void hideAccountAddDialog(){

        if (accountAddDialog != null){

            accountAddDialog.dismiss();
            accountAddDialog = null;

        }
    }

    @Click(R.id.cvFinish)
    void finishButtonClicked(){

        Intent intent = new Intent(BROADCAST_CHANGE_MENU);
        intent.putExtra(MENU_ID, MENU_CODE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

    }

    @Override
    public void itemDelete(int position) {

        if (dataManager.getUser().getAccountIDs().size() == 1){
            showAlert("", "You have to at least 1 account ID.");

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
