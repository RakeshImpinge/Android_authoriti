package net.authoriti.authoriti.utils.crypto;

import net.authoriti.authoriti.utils.Constants;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.util.Base64;
import android.util.Log;

/**
 * Created by mac on 12/20/17.
 */

public class Crypto {
    public static final String TAG = "PAYLOAD_GENERATOR";

    public class PayloadGenerator {
        private BigInteger BASE = new BigInteger("62");

        public String ALPHANUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private char[] DECANUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        private String BASE22 = "0123456789abcdefghijkl";
        private String BASE14 = "0123456789abcd";

        private String[] SCHEMA1 = {BASE22, BASE22, BASE22, BASE22, BASE22, new String(DECANUM),
                new String(DECANUM), new String(DECANUM), new String(DECANUM)};
        private String[] SCHEMA2 = {ALPHANUM, ALPHANUM, BASE22, BASE22, BASE22, BASE22, BASE22};
        private String[] SCHEMA3 = {ALPHANUM, ALPHANUM, BASE22, BASE22, BASE22, BASE22, BASE22};
        private String[] SCHEMA4 = {ALPHANUM, ALPHANUM, BASE22, BASE22, BASE22, BASE22, BASE22};
        private String[] SCHEMA5 = {new String(DECANUM), new String(DECANUM), new String(DECANUM)
                , new String(DECANUM), new String(DECANUM), BASE14, BASE14, BASE14, BASE14, BASE14};
        private String[] SCHEMA6 = {BASE22, BASE22, BASE22, BASE22, BASE22, ALPHANUM, ALPHANUM};

        private String[][] SCHEMA_RANGES = {SCHEMA1, SCHEMA2, SCHEMA3, SCHEMA4, SCHEMA5, SCHEMA6};

        private String accountId;
        private String schemaVersion;

        private String payload = "";
        private String extraInput = "";
        private String privateKey = "";


        PayloadGenerator(String accountId, String schemaVersion, String privateKey) {
            this.accountId = accountId;
            this.schemaVersion = schemaVersion;
            this.privateKey = privateKey;
        }

        public void add(String picker, String value) {
            switch (picker) {
                case Constants.PICKER_INDUSTRY:
                case Constants.PICKER_LOCATION_COUNTRY:
                case Constants.PICKER_LOCATION_STATE:
                case Constants.PICKER_REQUEST:
                    payload = payload + value;
                    break;
                case Constants.PICKER_GEO:
                    String geo = CryptoUtil.intToBase62(new BigInteger(value), 1);
                    payload = payload + geo;
                    break;
                default:
                    System.out.println("TODO: handle picker " + picker + ". received value: " +
                            value);

            }
        }


        public void addTime(long expiresAt) throws Exception {
            Log.i(TAG, "Expires At: " + expiresAt);

            long difference = expiresAt - 1530403200000l;
            long minutes = TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);

            // 1537974156094
            // 1530403200000l

            Log.i(TAG, "Minutes (new): " + minutes);

            String encodedTime = "";
            if (schemaVersion.equalsIgnoreCase("5")) {
                encodedTime = intToBase14(new BigInteger(minutes + ""), 5);
                if (encodedTime.length() > 5) {
                    encodedTime = "ddddd";
                }
            } else {
                encodedTime = intToBase22(new BigInteger(minutes + ""), 5);
                if (encodedTime.length() > 5) {
                    encodedTime = "lllll";
                }
            }
            Log.i(TAG, "Time: " + encodedTime);
            payload = encodedTime + payload;
        }

        public void addInput(String inputType, String value) {
            if (schemaVersion.equalsIgnoreCase("5") && inputType.equalsIgnoreCase("amount")) {
                while (value.length() < 5) {
                    value = "0" + value;
                }
                extraInput = payload.substring(5) + extraInput;
                payload = value + payload.substring(0, 5);
            } else {
                String trimmed = CryptoUtil.cleanup(value, value.length());
                if (inputType.equalsIgnoreCase("secret")) {
                    trimmed = CryptoUtil.hash(trimmed);
                } else if (inputType.equalsIgnoreCase("amount")) {
                    String amount = value;
                    int indexOfPeriod = amount.indexOf('.');
                    if (indexOfPeriod != -1) {
                        int distance = amount.length() - indexOfPeriod;
                        if (distance == 1) {
                            amount = amount + "00";
                        } else if (distance == 2) {
                            amount = amount + "0";
                        } else {
                            amount = amount.substring(0, indexOfPeriod + 3);
                        }
                    } else amount = amount + "x00";
                    trimmed = CryptoUtil.cleanup(amount.replace('.', 'x'), amount.length());
                }

                extraInput = extraInput + trimmed;
            }

        }

        public void addDataType(int requestorLength, String[] values) {
            StringBuilder bitmask = new StringBuilder();

            for (int i = 0; i < requestorLength; i++) {
                boolean found = false;
                for (String v : values) {
                    if (Integer.parseInt(v) == i) {
                        found = true;
                        break;
                    }
                }
                if (found) bitmask.append("1");
                else bitmask.append("0");
            }
            int decimal = Integer.parseInt(bitmask.toString(), 2);
            payload = payload + CryptoUtil.intToBase62(new BigInteger(decimal + ""), 2);
        }

        public String generate() {
            String encodedPayload = "";
            String extra = accountId;
            switch (schemaVersion) {
                case "1":
                    final char countryValue = payload.charAt(7);
                    payload = payload.substring(0, 7) + payload.substring(8);
                    encodedPayload = encodePayload(payload, 1);
                    extra = extraInput + extra + countryValue;
                    break;
                case "7":
                    encodedPayload = encodePayload(payload + "99", 1);
                    extra = extraInput + extra + "1"; // 1 = United States; Make this dynamic
                    break;
                case "2":
                    String tmpTime = payload.substring(0, 5);
                    payload = payload.replace(tmpTime, "") + tmpTime;
                    extra = extra + extraInput + payload.substring(0, 3);
                    payload = payload.substring(3);
                    encodedPayload = encodePayload(payload, 2);
                    break;
                case "3":
                case "4":
                    String tmpHash = CryptoUtil.hash(extraInput);
                    encodedPayload = encodePayload(tmpHash.substring(0, 2) + payload, 3);
                    extra = extra + extraInput;
                    break;
                case "5":
                    encodedPayload = encodePayload(payload, 5);
                    extra = extra + extraInput;
                    break;
                case "6":
                    encodedPayload = encodePayload(payload, 6);
                    break;
            }

            final String signedCode = sign(encodedPayload, privateKey);
            final String passcode = addDataToCode(extra, signedCode);
            Log.i(TAG, "PC: " + passcode);
            return passcode;
        }

        private String encodePayload(String payload, int schema) {
            String[] payloadRanges = SCHEMA_RANGES[schema - 1];
            return encodePayload(payload, payloadRanges);
        }

        private String encodePayload(String payload, String[] ranges) {
            BigInteger total = new BigInteger("0");
            BigInteger mult = new BigInteger("1");

            int len = payload.length();
            for (int i = len - 1; i >= 0; i--) {
                char d = payload.charAt(i);
                String r = ranges[i];

                int index = r.indexOf(d);
                if (index != -1) {
                    total = total.add(new BigInteger(index + "").multiply(mult));
                    mult = mult.multiply(new BigInteger(r.length() + ""));
                }
            }


            StringBuilder encoded = new StringBuilder(CryptoUtil.intToBase62(total, 6));
            return encoded.toString();
        }

        private String intToBase22(BigInteger number, int length) {
            StringBuilder str = new StringBuilder();
            final BigInteger zero = new BigInteger("0");
            if (number.compareTo(zero) == 0) {
                return "0";
            }

            final BigInteger base = new BigInteger("22");
            while (number.compareTo(zero) != 0) {
                final int index = number.mod(base).intValue();
                str.append(BASE22.charAt(index));
                number = number.divide(base);
            }

            while (str.length() < length) {
                str.append(BASE22.charAt(0));
            }

            return str.reverse().toString();
        }

        private String intToBase14(BigInteger number, int length) {
            StringBuilder str = new StringBuilder();
            final BigInteger zero = new BigInteger("0");
            if (number.compareTo(zero) == 0) {
                return "0";
            }

            final BigInteger base = new BigInteger("14");
            while (number.compareTo(zero) != 0) {
                final int index = number.mod(base).intValue();
                str.append(BASE14.charAt(index));
                number = number.divide(base);
            }

            while (str.length() < length) {
                str.append(BASE14.charAt(0));
            }

            return str.reverse().toString();
        }

        private String addDataToCode(String data, String code) {
            final String _data = CryptoUtil.cleanup(CryptoUtil.hash(data), 8);

            BigInteger a = CryptoUtil.base62ToInt(_data);
            BigInteger b = CryptoUtil.base62ToInt(code);

            BigInteger sum = a.add(b);

            return CryptoUtil.intToBase62(sum, 10);
        }
    }

    public PayloadGenerator init(String accountId, String schemaVersion, String privateKey) {
        return new PayloadGenerator(accountId, schemaVersion, privateKey);
    }

    public CryptoKeyPair generateKeyPair(String password, String salt) {
        byte[] saltBytes;
        if (salt == null) {
            saltBytes = CryptoUtil.generateRandomBytes(64);
            salt = new String(saltBytes);
        } else {
            saltBytes = salt.getBytes();
        }


        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password.toCharArray()),
                saltBytes, 4096);
        KeyParameter key = (KeyParameter) generator.generateDerivedMacParameters(256);

        byte[] seedBytes = key.getKey();

        BigInteger numPrivateKey = CryptoUtil.intFromBytes(seedBytes).mod(new BigInteger("62")
                .pow(6));

        String privateKey = CryptoUtil.intToBase62(numPrivateKey, -1);
        String publicKey = new EcDSA().getPublicKey(numPrivateKey);


        return new CryptoKeyPair(privateKey, publicKey, salt);
    }

    public String addAccountNumberToPayload(String payload, String accountId) {
        String p = CryptoUtil.cleanup(payload, 10);
        String a = CryptoUtil.cleanup(accountId, 4);

        BigInteger pInt = CryptoUtil.base62ToInt(p);
        BigInteger aInt = CryptoUtil.base62ToInt(a);

        BigInteger x = pInt.add(aInt);
        return CryptoUtil.intToBase62(x, 10);
    }

//    public String addIdentifierToAccountId(String identifier, String accountId) {
//        String acc = CryptoUtil.cleanup(accountId, 4);
//        String _identifier = CryptoUtil.cleanup(identifier.toLowerCase(), identifier.length());
//
//        String id = CryptoUtil.cleanup(CryptoUtil.MD5(_identifier), 4);
//
//        BigInteger a = CryptoUtil.base62ToInt(acc);
//        BigInteger b = CryptoUtil.base62ToInt(id);
//
//        BigInteger c = a.add(b);
//
//        return CryptoUtil.intToBase62(c, 4);
//    }

    public String getTimeString(int year, int month, int day, int hour, int minute) throws
            Exception {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar d = Calendar.getInstance();
        d.set(2017, Calendar.NOVEMBER, 1, 0, 0);
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

    private String sign(String payload, String privateKey) {
        return new EcDSA().sign(payload, privateKey);
    }
}
