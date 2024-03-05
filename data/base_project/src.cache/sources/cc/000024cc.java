package java.security.interfaces;

import java.math.BigInteger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RSAPrivateCrtKey.class */
public interface RSAPrivateCrtKey extends RSAPrivateKey {
    public static final long serialVersionUID = -5682214253527700368L;

    BigInteger getCrtCoefficient();

    BigInteger getPrimeP();

    BigInteger getPrimeQ();

    BigInteger getPrimeExponentP();

    BigInteger getPrimeExponentQ();

    BigInteger getPublicExponent();
}