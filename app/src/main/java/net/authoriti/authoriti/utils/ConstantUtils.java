package net.authoriti.authoriti.utils;

import android.util.Base64;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.AuthoritiApplication;
import net.authoriti.authoriti.BuildConfig;

public class ConstantUtils {
    private static String PASSWORD = AuthoritiApplication.class.getName();
    private static String SALT = Base64.encodeToString(PASSWORD.getBytes(), Base64.DEFAULT);

    public static String encrypt(String value) {
        try {

            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (PASSWORD, SALT);
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
                    (PASSWORD, SALT);
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

    public static String getBuildFlavor() {
        return BuildConfig.FLAVOR;
    }

    public static boolean isBuildFlavorVnb() {
        return getBuildFlavor().equals(Constants.BUILD_FLAVOUR_VNB);
    }
}
