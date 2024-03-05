package java.security;

import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyPairGeneratorSpi.class */
public abstract class KeyPairGeneratorSpi {
    public abstract KeyPair generateKeyPair();

    public abstract void initialize(int i, SecureRandom secureRandom);

    public KeyPairGeneratorSpi() {
        throw new RuntimeException("Stub!");
    }

    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }
}