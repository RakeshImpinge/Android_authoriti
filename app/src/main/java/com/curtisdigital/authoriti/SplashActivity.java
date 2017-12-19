package com.curtisdigital.authoriti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.curtisdigital.authoriti.ui.auth.LoginActivity_;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiData_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

/**
 * Created by mac on 11/25/17.
 */

public class SplashActivity extends AppCompatActivity {


    AuthoritiData dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = AuthoritiData_.getInstance_(this);

        if (dataManager != null && dataManager.loginStatus() != null && dataManager.loginStatus().isLogin()){

            Intent intent = new Intent(this, MainActivity_.class);
            startActivity(intent);

        } else {

            Intent intent = new Intent(this, LoginActivity_.class);
            startActivity(intent);
        }

        finish();
    }
}
