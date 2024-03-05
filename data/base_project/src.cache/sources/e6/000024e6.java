package java.security.spec;

import java.math.BigInteger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RSAPrivateCrtKeySpec.class */
public class RSAPrivateCrtKeySpec extends RSAPrivateKeySpec {
    public RSAPrivateCrtKeySpec(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent, BigInteger primeP, BigInteger primeQ, BigInteger primeExponentP, BigInteger primeExponentQ, BigInteger crtCoefficient) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public BigInteger getCrtCoefficient() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getPrimeExponentP() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getPrimeExponentQ() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getPrimeP() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getPrimeQ() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getPublicExponent() {
        throw new RuntimeException("Stub!");
    }
}