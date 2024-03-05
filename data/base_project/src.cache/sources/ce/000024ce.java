package java.security.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RSAPublicKey.class */
public interface RSAPublicKey extends PublicKey, RSAKey {
    public static final long serialVersionUID = -8727434096241101194L;

    BigInteger getPublicExponent();
}