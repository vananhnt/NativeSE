package javax.crypto.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DHPrivateKey.class */
public interface DHPrivateKey extends DHKey, PrivateKey {
    public static final long serialVersionUID = 2211791113380396553L;

    BigInteger getX();
}