package net.authoriti.authoritiapp.utils.crypto;

import net.authoriti.authoritiapp.utils.Constants;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

/**
 * Created by mac on 12/20/17.
 */

public class Crypto {
    public class PayloadGenerator {
        public static final String TAG = "PAYLOAD_GENERATOR";

        private BigInteger BASE = new BigInteger("62");

        private String ALPHANUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private char[] DECANUM = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        private String BASE22 = "0123456789abcdefghijkl";
        private String BASE14 = "0123456789abcd";

        private String[] SCHEMA1 = { BASE22, BASE22, BASE22, BASE22, BASE22, new String(DECANUM), new String(DECANUM), new String(DECANUM), new String(DECANUM) };
        private String[] SCHEMA2 = { ALPHANUM, ALPHANUM, BASE22, BASE22, BASE22, BASE22, BASE22 };
        private String[] SCHEMA3 = { ALPHANUM, ALPHANUM, BASE22, BASE22, BASE22, BASE22, BASE22 };
        private String[] SCHEMA4 = { ALPHANUM, ALPHANUM, BASE22, BASE22, BASE22, BASE22, BASE22 };
        private String[] SCHEMA5 = { new String(DECANUM), new String(DECANUM), new String(DECANUM), new String(DECANUM), new String(DECANUM), BASE14, BASE14, BASE14, BASE14, BASE14 };
        private String[] SCHEMA6 =  { BASE22, BASE22, BASE22, BASE22, BASE22, ALPHANUM, ALPHANUM };

        private String[][] SCHEMA_RANGES = { SCHEMA1, SCHEMA2, SCHEMA3, SCHEMA4, SCHEMA5, SCHEMA6 };

        private String accountId;
        private String schemaVersion;

        private String payload = "";

        PayloadGenerator(String accountId, String schemaVersion) {
            this.accountId = accountId;
            this.schemaVersion = schemaVersion;
        }

        public void add(String picker, String value) {
            Log.v(TAG, "picker: " + picker + "; value: " + value);
            switch (picker) {
                case Constants.PICKER_INDUSTRY:
                case Constants.PICKER_LOCATION_COUNTRY:
                case Constants.PICKER_LOCATION_STATE:
//                case Constants.PICKER_ANY_STATE:
                    payload = payload + value;
                    break;
                default:
                    System.out.println("TODO: handle picker " + picker + ". received value: " +
                            value);

            }
        }

        public void addTime(int year, int month, int day, int hour, int minute) throws Exception {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);

            Date expirationTime = calendar.getTime();

            calendar.set(Calendar.YEAR, 2018);
            calendar.set(Calendar.MONTH, Calendar.JULY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);


            Date baseTime = calendar.getTime();

            long difference = expirationTime.getTime() - baseTime.getTime();
            long minutes = difference / 60000 - 360;

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

            payload = encodedTime + payload;
        }

        public void addInput(String inputType, String value) {
            //TODO: Placeholder code for now
            Log.v(TAG, "picker (input): " + inputType + "; value: " + value);
        }

        public void addDataType(int requestorLength, String[] values) {
            //TODO: Placeholder code for now
            Log.v(TAG, "data_type: " + requestorLength);
            for (String value : values) {
                System.out.println("values: " + value);
            }
        }

        public String generate() {
            //TODO: Placeholder code for now
            Log.v(TAG, "Generated Payload: " + payload);
            return "0000000000";
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
    }

    public PayloadGenerator init(String accountId, String schemaVersion) {
        return new PayloadGenerator(accountId, schemaVersion);
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

    public String encodeGeo(String geo, String payload) {
        BigInteger p = new BigInteger(geo);
        return payload + CryptoUtil.intToBase62(p, 1);
    }

    public String encodeDataTypes(String selectedTypes, String payload) {
        int dt = Integer.parseInt(selectedTypes, 2);
        return payload + CryptoUtil.intToBase62(new BigInteger(dt + ""), 2);
    }

    public String sign(String payload, String privateKey) {
        System.out.println("Payload: " + payload);
        System.out.println("Private-Key: " + privateKey);

        return payload;
//        String signature = CryptoUtil.sign(payload, privateKey);
//        return signature;
    }
}
