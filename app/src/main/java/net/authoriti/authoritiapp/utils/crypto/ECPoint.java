package net.authoriti.authoritiapp.utils.crypto;

import java.math.BigInteger;

public class ECPoint {
    private ECCurve curve;
    private BigInteger x;
    private BigInteger y;
    private BigInteger z;

    public ECPoint(ECCurve curve, BigInteger x, BigInteger y, BigInteger z) {
        this.curve = curve;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BigInteger _x() {
        return x;
    }

    public BigInteger _y() {
        return y;
    }

    public BigInteger _z() {
        return z;
    }

    public BigInteger getX() {
        return this.curve.field_div(
                this.x,
                this.z.pow(2).mod(this.curve.getP())
        );
    }

    public BigInteger getY() {
        return this.curve.field_div(
                this.y,
                this.z.pow(2).multiply(this.z).mod(this.curve.getP())
        );
    }

    public ECPoint(ECCurve curve, BigInteger x, BigInteger y) {
        this(curve, x, y, new BigInteger("1"));
    }

    public ECPoint mul(BigInteger m) {
        return this.curve.mul(m, this);
    }

    @Override
    public String toString() {
        if (this.x.equals(this.curve.getP())) {
            return "identity_point";
        }

        return "(" + this.getX() + ", " + this.getY() + ")";
    }
}
