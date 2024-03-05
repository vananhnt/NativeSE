package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RSAPrivateKey.class */
public interface RSAPrivateKey extends PrivateKey, RSAKey {
    public static final long serialVersionUID = 5187144804936595022L;

    BigInteger getPrivateExponent();
}