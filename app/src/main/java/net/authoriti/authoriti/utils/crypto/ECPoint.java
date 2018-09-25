package net.authoriti.authoriti.utils.crypto;

import java.math.BigInteger;

public class ECPoint {
    private BigInteger x;
    private BigInteger y;
    private BigInteger z;

    private ECCurve curve;

    ECPoint(ECCurve curve, BigInteger x, BigInteger y, BigInteger z) {
        this.curve = curve;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ECPoint add(ECPoint Q2){
        return this.curve.add(this, Q2);
    }

    public ECPoint mul(BigInteger m) {
        return this.curve.mul(m, this);
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
                this.z.pow(3).mod(this.curve.getP())
        );
    }

    public ECPoint(ECCurve curve, BigInteger x, BigInteger y) {
        this(curve, x, y, new BigInteger("1"));
    }

    @Override
    public String toString() {
        if (this.x.equals(this.curve.getP())) {
            return "identity_point";
        }

        return "(" + this.getX() + ", " + this.getY() + ")";
    }
}
