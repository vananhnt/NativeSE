package java.security;

import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AlgorithmParameterGeneratorSpi.class */
public abstract class AlgorithmParameterGeneratorSpi {
    protected abstract void engineInit(int i, SecureRandom secureRandom);

    protected abstract void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException;

    protected abstract AlgorithmParameters engineGenerateParameters();

    public AlgorithmParameterGeneratorSpi() {
        throw new RuntimeException("Stub!");
    }
}