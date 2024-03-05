package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DSAPrivateKey.class */
public interface DSAPrivateKey extends DSAKey, PrivateKey {
    public static final long serialVersionUID = 7776497482533790279L;

    BigInteger getX();
}