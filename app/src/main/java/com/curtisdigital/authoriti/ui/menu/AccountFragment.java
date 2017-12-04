package com.curtisdigital.authoriti.ui.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.core.BaseFragment;
import com.curtisdigital.authoriti.ui.items.AccountItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 11/30/17.
 */

@EFragment(R.layout.fragment_account)
public class AccountFragment extends BaseFragment{

    private FastItemAdapter<AccountItem> adapter;

    BroadcastReceiver broadcastReceiver;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.rvAccount)
    RecyclerView rvAccount;

    @AfterViews
    void callAfterViewInjection(){

        adapter = new FastItemAdapter<AccountItem>();
        rvAccount.setLayoutManager(new LinearLayoutManager(mContext));
        rvAccount.setAdapter(adapter);

        showAccount();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_ACCOUNT_ADDED)){
                    showAccount();
                }
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACCOUNT_ADDED));

    }

    private void showAccount(){

        if (adapter == null){
            adapter = new FastItemAdapter<AccountItem>();
        } else {
            adapter.clear();
        }

        if (dataManager.getAccountPicker() != null && dataManager.getAccountPicker().getValues() != null){
            for (Value value : dataManager.getAccountPicker().getValues()){
                adapter.add(new AccountItem(value));
            }
        }
    }

    @Click(R.id.cvGenerate)
    void generateButtonClicked(){
        Intent intent = new Intent(BROADCAST_CHANGE_MENU);
        intent.putExtra(MENU_ID, MENU_CODE);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
