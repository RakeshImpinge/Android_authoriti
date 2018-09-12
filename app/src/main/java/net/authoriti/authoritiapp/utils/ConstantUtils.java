package net.authoriti.authoritiapp.utils;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoritiapp.AuthoritiApplication;

public class ConstantUtils {

    public static String encrypt(String value) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (AuthoritiApplication.class.getName(), AuthoritiApplication.class.getName());
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt
                    (value, keys);
            return cipherTextIvMac.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String decode(String ciphervalue) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (AuthoritiApplication.class.getName(), AuthoritiApplication.class.getName());
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac_d = new AesCbcWithIntegrity
                    .CipherTextIvMac(ciphervalue);
            return AesCbcWithIntegrity.decryptString(cipherTextIvMac_d, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getHelpUrl(String endpoints) {
        return Constants.HELP_BASE + "/" + endpoints;
    }

    public static String getBaseUrl() {
        return decode(Constants.API_BASE_URL);
    }
}
