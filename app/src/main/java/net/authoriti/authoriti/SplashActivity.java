package net.authoriti.authoriti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.authoriti.authoriti.ui.auth.InviteCodeActivity_;
import net.authoriti.authoriti.ui.auth.LoginActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Log;

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

        Log.e("Test", "Log");

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
