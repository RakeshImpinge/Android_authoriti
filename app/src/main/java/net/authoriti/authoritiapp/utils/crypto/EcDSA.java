package net.authoriti.authoritiapp.utils.crypto;

import java.math.BigInteger;
import android.util.Log;


public class EcDSA {
    private ECCurve curve;
    private BigInteger base;


    public EcDSA() {
        final ECCurve curve6_62 = new ECCurve(
                new BigInteger("56800235581"), // 62^6 - 2 - 1
                new BigInteger("0"),
                new BigInteger("7"),
                new BigInteger("56799904201")
        );

        curve6_62.setG(new ECPoint(
                curve6_62,
                new BigInteger("44196914945"),
                new BigInteger("6594789113"),
                new BigInteger("1")
        ));

        this.curve = curve6_62;
        this.base = new BigInteger("62");
    }

    public String getPublicKey(BigInteger privateKey) {
        ECPoint publicKey = this.curve.getG().mul(privateKey);

        BigInteger x = publicKey.getX();
        BigInteger y = publicKey.getY();

        return CryptoUtil.intToBase62(x, -1) + CryptoUtil.intToBase62(y, -1);
    }

    public String sign(String payload, String privateKey) {
        BigInteger n = this.curve.getN();

        BigInteger key = CryptoUtil.base62ToInt(privateKey);
        BigInteger d = key.mod(n);

        final String hashed = CryptoUtil.hash(payload);
        BigInteger z = CryptoUtil.digestMessage(hashed, n);

        String publicKey = getPublicKey(key);

        BigInteger k = CryptoUtil.intFromBytes(CryptoUtil.hash(payload + publicKey));
        k = k.mod(n);

        ECPoint C = this.curve.getG().mul(k);
        BigInteger r = C.getX().mod(n);

        BigInteger modInv = k.modInverse(n).mod(n);

        BigInteger s = z.add(r.multiply(d)).mod(n).multiply(modInv).mod(n);

        String s1 = CryptoUtil.intToBase62(s, 6);

        char s0Index0 = payload.charAt(0);
        char s0Index1 = payload.charAt(1);

        char s1Index1 = s1.charAt(4);
        char s1Index5 = s1.charAt(5);

        String overlap = CryptoUtil.xor62_minus(s0Index0, s1Index1) + "" + CryptoUtil.xor62_minus(s0Index1, s1Index5);
        String xor62Seed = publicKey + s1.substring(0, 4) + overlap;
        String encrypted = CryptoUtil.xor62_cipher(payload.substring(2), xor62Seed);

        final String signature = s1.substring(0, 4) + overlap + encrypted;
        return signature;
    }

}
