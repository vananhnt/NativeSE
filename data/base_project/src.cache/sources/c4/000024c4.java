package java.security.interfaces;

import java.math.BigInteger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DSAParams.class */
public interface DSAParams {
    BigInteger getG();

    BigInteger getP();

    BigInteger getQ();
}