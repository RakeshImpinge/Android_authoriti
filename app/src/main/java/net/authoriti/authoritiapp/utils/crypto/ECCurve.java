package net.authoriti.authoritiapp.utils.crypto;

import java.math.BigInteger;

public class ECCurve {
    private BigInteger p;
    private BigInteger a;
    private BigInteger b;
    private BigInteger n;
    private ECPoint g;

    public static void main(String[] args) {
        ECCurve curve = new ECCurve(new BigInteger("34234231"), new BigInteger("2"), new BigInteger("11"), new BigInteger("7"));

        ECPoint A = new ECPoint(curve, new BigInteger("51"), new BigInteger("123"), new BigInteger("44"));
        ECPoint B = new ECPoint(curve, new BigInteger("234"), new BigInteger("4543"), new BigInteger("129"));

        ECPoint R1 = curve.mul(new BigInteger("39"), A);
        ECPoint R2 = curve.mul(new BigInteger("39"), B);

        System.out.println(R1);
        System.out.println(R2);
    }

    public ECCurve(BigInteger p, BigInteger a, BigInteger b, BigInteger n) {
        this.p = p;
        this.a = a;
        this.b = b;
        this.n = n;
        this.g = null;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getN() {
        return n;
    }

    public void setG(ECPoint point) {
        this.g = point;
    }

    public ECPoint getG() {
         return this.g;
    }

    public BigInteger field_mul(BigInteger a, BigInteger b) {
        return a.multiply(b).mod(this.p);
    }

    public BigInteger field_div(BigInteger num, BigInteger den) {
        final BigInteger inverseDen = den.mod(this.p).modInverse(this.p);
        return this.field_mul(num.mod(this.p), inverseDen);
    }

    public BigInteger field_exp(BigInteger num, BigInteger power) {
        return num.mod(this.p).modPow(power, this.p);
    }

    public ECPoint identity() {
        return new ECPoint(this, this.p, new BigInteger("0"), new BigInteger("1"));
    }

    public ECPoint twice(ECPoint Q) {
        if (Q._x().equals(this.p)) {
            return Q;
        }

        // S  = (4*Q._x*Q._y*Q._y) % self._p
        final BigInteger S = new BigInteger("4").multiply(Q._x()).multiply(Q._y().pow(2)).mod(this.p);

        // Z2 = Q._z*Q._z
        final BigInteger Z2 = Q._z().pow(2);

        // Z4 = (Z2 * Z2) % self._p
        final BigInteger Z4 = Z2.pow(2).mod(this.p);

        // M  = (3*Q._x*Q._x + self._a*Z4)
        final BigInteger M = new BigInteger("3").multiply(Q._x().pow(2)).add(this.a.multiply(Z4));

        // Y2 = Q._y * Q._y
        final BigInteger Y2 = Q._y().pow(2);

        // x = (M*M - 2*S) % self._p
        final BigInteger x = M.pow(2).subtract(new BigInteger("2").multiply(S)).mod(this.p);

        // y = (M*(S-x) - 8*Y2*Y2) % self._p
        final BigInteger y = M.multiply(S.subtract(x)).subtract(new BigInteger("8").multiply(Y2.pow(2))).mod(this.p);

        // z = (2*Q._y*Q._z) % self._p
        final BigInteger z = new BigInteger("2").multiply(Q._y()).multiply(Q._z()).mod(this.p);

        return new ECPoint(this, x, y, z);
    }

    public ECPoint add(ECPoint Q1, ECPoint Q2){
        if (Q1._x().equals(this.p)){
            return Q2;
        }

        if (Q2._x().equals(this.p)){
            return Q1;
        }

        // Q1z2 = Q1._z*Q1._z
        final BigInteger Q1z2 = Q1._z().pow(2);

        // Q2z2 = Q2._z*Q2._z
        final BigInteger Q2z2 = Q2._z().pow(2);

        // xs1 = (Q1._x*Q2z2) % self._p
        final BigInteger xs1 = Q1._x().multiply(Q2z2).mod(this.p);

        // xs2 = (Q2._x*Q1z2) % self._p
        final BigInteger xs2 = Q2._x().multiply(Q1z2).mod(this.p);

        // ys1 = (Q1._y*Q2z2*Q2._z) % self._p
        final BigInteger ys1 = Q1._y().multiply(Q2z2.multiply(Q2._z())).mod(this.p);

        // ys2 = (Q2._y*Q1z2*Q1._z) % self._p
        final BigInteger ys2 = Q2._y().multiply(Q1z2.multiply(Q1._z())).mod(this.p);

        // Equality special cases
        if (xs1.equals(xs2)) {
            if (ys1.equals(ys2)) // adding point to itself
                return this.twice(Q1);
            else // vertical pair; result is the identity
                return this.identity();
        }

        // Ordinary case
        // xd = (xs2-xs1) % self._p   # caution: if not python, negative result?
        final BigInteger xd = xs2.subtract(xs1).mod(this.p);

        // yd = (ys2-ys1) % self._p
        final BigInteger yd = ys2.subtract(ys1).mod(this.p);

        // xd2 = (xd*xd) % self._p
        final BigInteger xd2 = xd.pow(2).mod(this.p);

        // xd3 = (xd2*xd) % self._p
        final BigInteger xd3 = xd2.multiply(xd).mod(this.p);

        // x = (yd*yd - xd3 - 2*xs1*xd2) % self._p
        final BigInteger x = yd.pow(2).subtract(xd3).subtract(new BigInteger("2").multiply(xs1).multiply(xd2)).mod(this.p);

        // y = (yd*(xs1*xd2 - x) - ys1*xd3) % self._p
        final BigInteger y = yd.multiply(xs1.multiply(xd2).subtract(x)).subtract(ys1.multiply(xd3)).mod(this.p);

        // z = (xd*Q1._z*Q2._z) % self._p
        final BigInteger z = xd.multiply(Q1._z().multiply(Q2._z())).mod(this.p);

        return new ECPoint(this, x, y, z);
    }

    public ECPoint mul(BigInteger m, ECPoint Q){
        final BigInteger zero = new BigInteger("0");
        final BigInteger one = new BigInteger("1");
        final BigInteger two = new BigInteger("2");

        ECPoint R = this.identity();

        while (!zero.equals(m)){  // binary multiply loop
            if (m.and(one).equals(one)) { // bit is set
                R = this.add(R, Q);
            }
            m = m.divide(two);
            if (!m.equals(zero)){
                Q = this.twice(Q);
            }
        }

        return R;
    }
}
