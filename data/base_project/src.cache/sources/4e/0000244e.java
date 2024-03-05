package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyFactorySpi.class */
public abstract class KeyFactorySpi {
    protected abstract PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException;

    protected abstract PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException;

    protected abstract <T extends KeySpec> T engineGetKeySpec(Key key, Class<T> cls) throws InvalidKeySpecException;

    protected abstract Key engineTranslateKey(Key key) throws InvalidKeyException;

    public KeyFactorySpi() {
        throw new RuntimeException("Stub!");
    }
}