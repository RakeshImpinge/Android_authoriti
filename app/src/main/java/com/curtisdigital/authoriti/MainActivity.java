package com.curtisdigital.authoriti;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.menu.AccountFragment_;
import com.curtisdigital.authoriti.ui.menu.CodeGenerateFragment_;
import com.curtisdigital.authoriti.ui.menu.WipeFragment_;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    public static final long MENU_CODE              = 1001;
    public static final long MENU_ACCOUNT           = 1002;
    public static final long MENU_WIPE              = 1003;
    public static final long MENU_LOGOUT            = 1004;

    private AccountHeader header = null;
    private Drawer drawer = null;

    private Fragment codeGenerateFragment;
    private Fragment accountFragment;
    private Fragment wipeFragment;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.ivAdd)
    ImageButton ivAdd;

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

                        showAddIcon(drawerItem.getIdentifier());

                        if (drawerItem.getIdentifier() == MENU_LOGOUT){
                            logOut();
                        } else {
                            menuSelected(drawerItem.getIdentifier());
                        }

                        return false;
                    }
                }).build();

        menuSelected(MENU_CODE);
    }

    private void menuSelected(long menu_id){
        Fragment fragment = null;
        if (menu_id == MENU_CODE){
            if (codeGenerateFragment == null){
                codeGenerateFragment = CodeGenerateFragment_.builder().build();
            }
            fragment = codeGenerateFragment;
        } else if (menu_id == MENU_ACCOUNT){
            if (accountFragment == null){
                accountFragment = AccountFragment_.builder().build();
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

    private void showAddIcon(long menu_id){
        if (menu_id == MENU_ACCOUNT){
            ivAdd.setVisibility(View.VISIBLE);
        } else {
            ivAdd.setVisibility(View.INVISIBLE);
        }
    }

    private void logOut(){

    }

    @Click(R.id.ivAdd)
    void addButtonClicked(){

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
