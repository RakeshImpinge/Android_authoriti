package com.curtisdigital.authoriti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.auth.LoginActivity_;
import com.curtisdigital.authoriti.ui.menu.AccountChaseFragment_;
import com.curtisdigital.authoriti.ui.menu.AccountFragment_;
import com.curtisdigital.authoriti.ui.menu.CodeGenerateFragment_;
import com.curtisdigital.authoriti.ui.menu.WipeFragment_;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity{

    private static String TAG = "Authoriti/" + MainActivity.class.getName();

    private AccountHeader header = null;
    private Drawer drawer = null;

    private Fragment codeGenerateFragment;
    private Fragment accountFragment;
    private Fragment wipeFragment;

    BroadcastReceiver broadcastReceiver;

    @Bean
    AuthoritiData dataManager;


    @ViewById(R.id.toolbar)
    Toolbar toolbar;

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

            if (codeGenerateFragment == null){

                codeGenerateFragment = CodeGenerateFragment_.builder().build();

            }

            fragment = codeGenerateFragment;
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

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()){
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

}
