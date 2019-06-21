package net.authoriti.authoriti.utils.crypto;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * Created by mac on 12/20/17.
 */

public class CryptoUtil {
    private static final String TAG = "PAYLOAD_GENERATOR";
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final BigInteger BASE = new BigInteger("62");

    static String ALPHANUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

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

    public static String xor62_cipher(String msg, String seed) {
        final int n = msg.length();
        final BigInteger baseRaisedToN = new BigInteger("62").pow(n);

        BigInteger key = intFromBytes(hash(seed)).mod(baseRaisedToN);
        String x = intToBase62(key, n);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char a = x.charAt(i);
            char b = msg.charAt(i);

            char m = xor62_minus(a, b);
            result = result.append(m);
        }

        return result.toString();
    }

    public static char xor62_minus(char a, char b) {
        int x = ALPHANUM.indexOf(a);
        int y = ALPHANUM.indexOf(b);

        String result = intToBase62(new BigInteger((x-y) + "").mod(BASE), 1);
        return result.charAt(0);
    }

    public static String level1(String str) {
        int len = str.length();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char s = str.charAt(i);
            if (s >= 'a' && s <= 'z' || s >= 'A' && s <= 'Z' || s >= '0' && s<= '9') {
                b.append(s);
            }
        }
        return b.toString();
    }

    public static String cleanup(String str, int length) {
        str = str.replaceFirst("^0+(?!$)", "");
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
        return result.toString().toLowerCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static BigInteger digestMessage(String message, BigInteger n) {
        byte [] bytes = hexStringToByteArray(message);
        BigInteger num = intFromBytes(bytes);
        return num.mod(n);
    }

    public static String hash(String str) {
        StringBuilder result = new StringBuilder("");
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                result.append(c);
            }
        }
        return SHA256(result.toString());
    }

    static String SHA256(String str) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
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

        for (int i = 0; i < length; i++) {
            if (bytes[i] == 0) {
                bytes[i] = 3;
            }
        }

        return bytes;
    }

    public static BigInteger intFromBytes(String message) {
        return intFromBytes(hexStringToByteArray(message));
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