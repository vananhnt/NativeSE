package java.security.spec;

import java.math.BigInteger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RSAMultiPrimePrivateCrtKeySpec.class */
public class RSAMultiPrimePrivateCrtKeySpec extends RSAPrivateKeySpec {
    public RSAMultiPrimePrivateCrtKeySpec(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent, BigInteger primeP, BigInteger primeQ, BigInteger primeExponentP, BigInteger primeExponentQ, BigInteger crtCoefficient, RSAOtherPrimeInfo[] otherPrimeInfo) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public BigInteger getCrtCoefficient() {
        throw new RuntimeException("Stub!");
    }

    public RSAOtherPrimeInfo[] getOtherPrimeInfo() {
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