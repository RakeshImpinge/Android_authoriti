package net.authoriti.authoritiapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoritiapp.ui.auth.InviteCodeActivity_;
import net.authoriti.authoritiapp.ui.auth.LoginActivity_;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiData_;
import net.authoriti.authoritiapp.utils.ConstantUtils;
import net.authoriti.authoritiapp.utils.Constants;

import org.androidannotations.annotations.EActivity;

import java.security.GeneralSecurityException;


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

        if (dataManager.loginStatus() != null && dataManager.loginStatus().isLogin()) {
            MainActivity_.intent(this).start();
        } else {
            if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null &&
                    dataManager.getUser().getAccountIDs().size() > 0) {
                LoginActivity_.intent(this).start();
            } else {
                InviteCodeActivity_.intent(this).showBack(false).start();
            }
        }

        finish();
    }
}
