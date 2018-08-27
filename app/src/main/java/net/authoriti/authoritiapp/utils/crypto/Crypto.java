package net.authoriti.authoritiapp.utils.crypto;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mac on 12/20/17.
 */

public class Crypto {

    public CryptoKeyPair generateKeyPair(String password, String salt) {
        BigInteger[] keys = CryptoUtil.keys(29);
        BigInteger n = keys[0];
        BigInteger e = keys[1];
        BigInteger d = keys[2];

        String nInBase62 = CryptoUtil.intToBase62(n,0);

        String publicKey = nInBase62 + "-" + CryptoUtil.intToBase62(e, 0);
        String privateKey = nInBase62 + "-" + CryptoUtil.intToBase62(d, 0);

        return new CryptoKeyPair(privateKey, publicKey, "");
    }

    public String addAccountNumberToPayload(String payload, String accountId) {
        String p = CryptoUtil.cleanup(payload, 10);
        String a = CryptoUtil.cleanup(accountId, 4);

        BigInteger pInt = CryptoUtil.base62ToInt(p);
        BigInteger aInt = CryptoUtil.base62ToInt(a);

        BigInteger x = pInt.add(aInt);
        return CryptoUtil.intToBase62(x, 10);
    }

    public String addIdentifierToAccountId(String identifier, String accountId) {
        String acc = CryptoUtil.cleanup(accountId, 4);
        String _identifier = CryptoUtil.cleanup(identifier.toLowerCase(), identifier.length());

        String id = CryptoUtil.cleanup(CryptoUtil.MD5(_identifier), 4);

        BigInteger a = CryptoUtil.base62ToInt(acc);
        BigInteger b = CryptoUtil.base62ToInt(id);

        BigInteger c = a.add(b);

        return CryptoUtil.intToBase62(c, 4);
    }

    public String getTimeString(int year, int month, int day, int hour, int minute) throws  Exception {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar d = Calendar.getInstance();
        d.set(2017, Calendar.NOVEMBER, 1, 0 , 0);
        d.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date epoch = d.getTime();
        Date expire = c.getTime();

        long diff = expire.getTime() - epoch.getTime();
        if (diff <= 0) {
            throw new Exception("Invalid expiry date received.");
        }

        long minutes = diff / 60000;
        return CryptoUtil.intToBase62(BigInteger.valueOf(minutes), 4);
    }

    public String encodeGeo(String geo, String payload) {
        BigInteger p = new BigInteger(geo);
        return payload + CryptoUtil.intToBase62(p, 1);
    }

    public String encodeDataTypes(String selectedTypes, String payload) {
        int dt = Integer.parseInt(selectedTypes, 2);
        return payload + CryptoUtil.intToBase62(new BigInteger(dt + ""), 2);
    }

    public String sign(String payload, String privateKey) {
        String signature = CryptoUtil.sign(payload, privateKey);
        return signature;
    }
}
