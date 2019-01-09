package net.authoriti.authoriti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.ui.auth.InviteCodeActivity_;
import net.authoriti.authoriti.ui.auth.LoginActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Log;

import org.androidannotations.annotations.EActivity;

import tgio.rncryptor.RNCryptorNative;


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
        AuthLogIn logIn = dataManager.loginStatus();
        if (logIn != null) {
            logIn.setLogin(false);
            dataManager.setAuthLogin(logIn);
        }

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

        String enc = encrypt("ank", "aa");
        String dec = decode(enc, "aa");

        System.out.println("-----" + enc);
        System.out.println("-----" + dec);

    }

//    public String encrypt(String value, String password) {
//        RNCryptorNative rncryptor = new RNCryptorNative();
//        return new String(rncryptor.encrypt(value, password));
//    }
//
//    private String decode(String ciphervalue, String password) {
//        RNCryptorNative rncryptor = new RNCryptorNative();
//        return rncryptor.decrypt(ciphervalue, password);
//    }


    public String encrypt(String value, String password) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (password, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt
                    (value, keys);
            return cipherTextIvMac.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String decode(String ciphervalue, String password) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (password, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac_d = new AesCbcWithIntegrity
                    .CipherTextIvMac(ciphervalue);
            return AesCbcWithIntegrity.decryptString(cipherTextIvMac_d, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
