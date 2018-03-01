package com.curtisdigital.authoriti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.api.model.Order;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Scheme;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.auth.LoginActivity_;
import com.curtisdigital.authoriti.ui.help.HelpActivity_;
import com.curtisdigital.authoriti.ui.menu.AccountChaseFragment_;
import com.curtisdigital.authoriti.ui.menu.AccountFragment_;
import com.curtisdigital.authoriti.ui.menu.PurposeFragment_;
import com.curtisdigital.authoriti.ui.menu.WipeFragment_;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.sjl.foreground.Foreground;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity{

    private static String TAG = "Authoriti/" + MainActivity.class.getName();

    private AccountHeader header = null;
    private Drawer drawer = null;

    private Fragment purposeFragment;
    private Fragment accountFragment;
    private Fragment wipeFragment;

    BroadcastReceiver broadcastReceiver;

    @Bean
    AuthoritiData dataManager;


    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.btnAdd)
    Button btnAdd;

    @ViewById(R.id.ivHelp)
    ImageButton ivHelp;

    Foreground.Listener listener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {

            if (dataManager.getInactiveTime() != null && !dataManager.getInactiveTime().equals("")){


                long currentTime = System.currentTimeMillis() / 1000;
                Log.e("Active TimeStamp", String.valueOf(currentTime));

                long inactiveTime = Long.parseLong(dataManager.getInactiveTime());
                Log.e("Inactive TimeStamp", String.valueOf(inactiveTime));

                if (currentTime - inactiveTime > 300){

                    logOut();

                } else {

                    dataManager.setInactiveTime("");
                }

            } else {

                dataManager.setInactiveTime("");
            }

        }

        @Override
        public void onBecameBackground() {

            long time = System.currentTimeMillis() / 1000;
            dataManager.setInactiveTime(String.valueOf(time));
            Log.e("Inactive TimeStamp", String.valueOf(time));

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Foreground.get(getApplication()).addListener(listener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @AfterViews
    void callAfterViewInjection(){

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(android.R.color.white)
                .build();

        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_oswaldo_regular));

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_code_generate).withIdentifier(MENU_CODE).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_account).withIdentifier(MENU_ACCOUNT).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_wipe).withIdentifier(MENU_WIPE).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_logOut).withIdentifier(MENU_LOGOUT).withSelectable(true).withTypeface(typeface)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem.getIdentifier() == MENU_LOGOUT){
                            logOut();
                        } else {
                            menuSelected(drawerItem.getIdentifier());
                        }

                        return false;
                    }
                }).build();


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_CHANGE_MENU)){
                    long menuId = intent.getLongExtra(MENU_ID, 0);
                    drawer.setSelection(menuId);
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_CHANGE_MENU));

        menuSelected(MENU_CODE);
    }

    private void menuSelected(long menu_id){
        Fragment fragment = null;
        if (menu_id == MENU_CODE){

            if (purposeFragment == null){

                purposeFragment = PurposeFragment_.builder().build();

            }

            fragment = purposeFragment;
        }
        else if (menu_id == MENU_ACCOUNT){

            if (dataManager.getUser().getInviteCode().equals("Startup2018")){

                accountFragment = AccountFragment_.builder().build();

            } else {

                accountFragment = AccountChaseFragment_.builder().build();

            }

            fragment = accountFragment;

        } else if (menu_id == MENU_WIPE){

            if (wipeFragment == null){

                wipeFragment = WipeFragment_.builder().build();

            }

            fragment = wipeFragment;

        }

        if (menu_id == MENU_ACCOUNT){

            if (dataManager.getUser().getInviteCode().equals("Startup2018")){
                btnAdd.setVisibility(View.VISIBLE);
            } else {
                btnAdd.setVisibility(View.INVISIBLE);
            }

            ivHelp.setVisibility(View.INVISIBLE);

        } else {

            btnAdd.setVisibility(View.INVISIBLE);
            ivHelp.setVisibility(View.VISIBLE);

        }
        changeFragment(fragment);
    }

    private void changeFragment(Fragment fragment){
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commitAllowingStateLoss();
        }
    }

    private void logOut(){

        AuthLogIn logIn = dataManager.loginStatus();
        logIn.setLogin(false);
        dataManager.setAuthLogin(logIn);

        Intent intent = new Intent(this, LoginActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Click(R.id.ivMenu)
    void menuButtonClicked(){
        if (drawer.isDrawerOpen()){
            drawer.closeDrawer();
        } else {
            drawer.openDrawer();
        }
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked(){
        HelpActivity_.intent(mContext).start();
    }

    @Click(R.id.btnAdd)
    void addButtonClicked(){
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_ADD_BUTTON_CLICKED));
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()){
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataManager.getScheme() != null){

//            loadScheme();
        }

    }

    private void loadScheme(){

        AuthoritiAPI.APIService().getScheme().enqueue(new Callback<Scheme>() {

            @Override
            public void onResponse(Call<Scheme> call, Response<Scheme> response) {

                if (response.code() == 200 && response.body() != null){

                    dataManager.setScheme(response.body());
                    updatePickers();

                    Log.e("Scheme", "Refreshed");

                }
            }

            @Override
            public void onFailure(Call<Scheme> call, Throwable t) {


            }
        });
    }

    private void updatePickers(){

        if (dataManager.getScheme() != null && dataManager.getScheme().getPickers() != null) {

            Order order = new Order();
            List<String> pickers = new ArrayList<>();

            for (Picker picker : dataManager.getScheme().getPickers()) {

                pickers.add(picker.getPicker());

                switch (picker.getPicker()) {

                    case PICKER_ACCOUNT:

                        if (dataManager.getAccountPicker() != null){

                            Picker temp = new Picker();
                            temp.setPicker(dataManager.getAccountPicker().getPicker());
                            temp.setBytes(dataManager.getAccountPicker().getBytes());
                            temp.setValues(dataManager.getAccountPicker().getValues());
                            temp.setTitle(picker.getTitle());
                            temp.setLabel(picker.getLabel());
                            temp.setEnableDefault(dataManager.getAccountPicker().isEnableDefault());
                            temp.setDefaultIndex(dataManager.getAccountPicker().getDefaultIndex());

                            dataManager.setAccountPicker(temp);
                        }

                        break;

                    case PICKER_INDUSTRY:

                        if (dataManager.getIndustryPicker() != null){

                            Picker temp = new Picker();
                            temp.setPicker(picker.getPicker());
                            temp.setBytes(picker.getBytes());
                            temp.setValues(picker.getValues());
                            temp.setTitle(picker.getTitle());
                            temp.setLabel(picker.getLabel());
                            temp.setEnableDefault(dataManager.getIndustryPicker().isEnableDefault());
                            temp.setDefaultIndex(dataManager.getIndustryPicker().getDefaultIndex());

                            dataManager.setIndustryPicker(temp);


                        } else {

                            dataManager.setIndustryPicker(picker);
                        }

                        break;

                    case PICKER_LOCATION_STATE:

                        if (dataManager.getLocationPicker() != null){

                            Picker temp = new Picker();
                            temp.setPicker(picker.getPicker());
                            temp.setBytes(picker.getBytes());
                            temp.setValues(picker.getValues());
                            temp.setTitle(picker.getTitle());
                            temp.setLabel(picker.getLabel());
                            temp.setEnableDefault(dataManager.getLocationPicker().isEnableDefault());
                            temp.setDefaultIndex(dataManager.getLocationPicker().getDefaultIndex());

                            dataManager.setLocationPicker(temp);


                        } else {

                            dataManager.setLocationPicker(picker);
                        }

                        break;

                    case PICKER_LOCATION_COUNTRY:

                        dataManager.setCountryPicker(picker);

                        break;

                    case PICKER_TIME:

                        if (dataManager.getTimePicker() != null){

                            Picker temp = new Picker();
                            temp.setPicker(picker.getPicker());
                            temp.setBytes(picker.getBytes());
                            temp.setValues(dataManager.getTimePicker().getValues());
                            temp.setTitle(picker.getTitle());
                            temp.setLabel(picker.getLabel());
                            temp.setEnableDefault(dataManager.getTimePicker().isEnableDefault());
                            temp.setDefaultIndex(dataManager.getTimePicker().getDefaultIndex());

                            dataManager.setTimePicker(temp);

                        }

                        break;

                }
            }

            order.setPickers(pickers);
            dataManager.setPickerOrder(order);

        }
    }

}
