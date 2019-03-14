package net.authoriti.authoriti.utils.crypto;

/**
 * Created by mac on 12/20/17.
 */

public class CryptoKeyPair {
    private String privateKey = null;
    private String publicKey = null;
    private String salt = null;

    CryptoKeyPair(String privateKey, String publicKey, String salt) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.salt = salt;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getSalt() {
        return salt;
    }
}