package javax.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyGeneratorSpi.class */
public abstract class KeyGeneratorSpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract SecretKey engineGenerateKey();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(int i, SecureRandom secureRandom);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(SecureRandom secureRandom);
}