package java.security.interfaces;

import java.math.BigInteger;
import java.security.spec.RSAOtherPrimeInfo;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RSAMultiPrimePrivateCrtKey.class */
public interface RSAMultiPrimePrivateCrtKey extends RSAPrivateKey {
    public static final long serialVersionUID = 618058533534628008L;

    BigInteger getCrtCoefficient();

    RSAOtherPrimeInfo[] getOtherPrimeInfo();

    BigInteger getPrimeP();

    BigInteger getPrimeQ();

    BigInteger getPrimeExponentP();

    BigInteger getPrimeExponentQ();

    BigInteger getPublicExponent();
}