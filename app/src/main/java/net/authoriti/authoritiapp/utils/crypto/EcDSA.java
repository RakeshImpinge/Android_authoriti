package net.authoriti.authoritiapp.utils.crypto;

import java.math.BigInteger;

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

}
