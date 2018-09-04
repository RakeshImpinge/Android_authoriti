package net.authoriti.authoritiapp.utils.crypto;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by mac on 12/20/17.
 */

public class CryptoUtil {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final BigInteger BASE = new BigInteger("62");

    public static BigInteger[] keys(int width) {
        BigInteger p = BigInteger.probablePrime(width, new Random());
        BigInteger q = BigInteger.probablePrime(width, new Random());

        BigInteger n = p.multiply(q);

        BigInteger one = new BigInteger("1");


        BigInteger e = new BigInteger("65537");

        BigInteger phi = (p.subtract(one)).multiply((q.subtract(one)));

        BigInteger d = e.modInverse(phi);

        BigInteger[] keys = {n, e, d};

        return keys;
    }

    public static String sign(String payload, String privateKey) {
        BigInteger p = base62ToInt(payload);
        String[] parts = privateKey.split("-");

        BigInteger modulus = base62ToInt(parts[0]);
        BigInteger key = base62ToInt(parts[1]);

        System.out.println("Modulus: " + modulus);
        System.out.println("Key: " + key);

        return intToBase62(p.modPow(key, modulus), 10);
    }

    public static String intToBase62(BigInteger num, int length) {
        StringBuilder str = new StringBuilder("");
        BigInteger zero = new BigInteger("0");
        while (num.compareTo(zero) != 0) {
            int index = num.mod(BASE).intValue();
            str = str.append(CHARACTERS.charAt(index));
            num = num.divide(BASE);
        }

        if (length != -1) {
            while (str.length() < length) {
                str = str.append(CHARACTERS.charAt(0));
            }
        }

        return str.reverse().toString();
    }

    public static String cleanup(String str, int length) {
        StringBuilder result = new StringBuilder("");
        final int sz = str.length();
        int k = 0;
        for (int i = 0; i < sz; i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                k++;
                result.append(c);

                if (k == length) {
                    break;
                }
            }
        }
        return result.toString();
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static BigInteger base62ToInt(String str) {
        String b62 = new StringBuilder(str).reverse().toString();

        BigInteger num = new BigInteger("0");
        BigInteger mult = new BigInteger("1");

        final int len = b62.length();
        for (int i = 0; i < len; i++) {
            char c = b62.charAt(i);
            int asciiCode = (int)c;

            int index = 0;
            if (asciiCode >= 97 && asciiCode <= 122) {
                index = asciiCode - 97 + 10;
            } else if (asciiCode >= 65 && asciiCode <= 90) {
                index = asciiCode - 65 + 10 + 26;
            } else if (asciiCode >= 48 && asciiCode <= 57) {
                index = asciiCode - 48;
            }

            BigInteger val = BigInteger.valueOf(index);
            num = num.add((val.multiply(mult))); //  val*mult;
            mult = mult.multiply(BASE);

        }

        return num;
    }

    public static byte[] generateRandomBytes(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];

        random.nextBytes(bytes);

        return bytes;
    }

    public static BigInteger intFromBytes(byte[] bytes) {
        BigInteger value = new BigInteger("0");

        for (byte b: bytes) {
            value = value.shiftLeft(8);
            value = value.or(new BigInteger("" +  (b & 0xFF)));
        }

        return value;
    }
}