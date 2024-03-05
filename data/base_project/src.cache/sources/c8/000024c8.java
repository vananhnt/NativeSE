package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ECPrivateKey.class */
public interface ECPrivateKey extends PrivateKey, ECKey {
    public static final long serialVersionUID = -7896394956925609184L;

    BigInteger getS();
}