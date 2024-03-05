package javax.crypto;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecretKeyFactorySpi.class */
public abstract class SecretKeyFactorySpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract KeySpec engineGetKeySpec(SecretKey secretKey, Class cls) throws InvalidKeySpecException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SecretKey engineTranslateKey(SecretKey secretKey) throws InvalidKeyException;
}