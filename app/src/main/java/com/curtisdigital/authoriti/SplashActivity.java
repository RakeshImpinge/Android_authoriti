package com.curtisdigital.authoriti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.curtisdigital.authoriti.ui.auth.LoginActivity_;

/**
 * Created by mac on 11/25/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
        finish();
    }
}
