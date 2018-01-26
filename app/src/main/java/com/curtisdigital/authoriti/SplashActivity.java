package com.curtisdigital.authoriti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.curtisdigital.authoriti.ui.auth.InviteCodeActivity_;
import com.curtisdigital.authoriti.ui.auth.LoginActivity_;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiData_;

import org.androidannotations.annotations.EActivity;

/**
 * Created by mac on 11/25/17.
 */

@EActivity
public class SplashActivity extends AppCompatActivity {

    AuthoritiData dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = AuthoritiData_.getInstance_(this);

        if (dataManager.loginStatus() != null && dataManager.loginStatus().isLogin()){

            MainActivity_.intent(this).start();

        } else {

            if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null && dataManager.getUser().getAccountIDs().size() > 0){

                LoginActivity_.intent(this).start();

            } else {

                InviteCodeActivity_.intent(this).showBack(false).start();
            }

        }

        finish();
    }
}
