package java.security;

import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AlgorithmParameterGenerator.class */
public class AlgorithmParameterGenerator {
    protected AlgorithmParameterGenerator(AlgorithmParameterGeneratorSpi paramGenSpi, Provider provider, String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public final String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public static AlgorithmParameterGenerator getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static AlgorithmParameterGenerator getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static AlgorithmParameterGenerator getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final void init(int size) {
        throw new RuntimeException("Stub!");
    }

    public final void init(int size, SecureRandom random) {
        throw new RuntimeException("Stub!");
    }

    public final void init(AlgorithmParameterSpec genParamSpec) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public final void init(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public final AlgorithmParameters generateParameters() {
        throw new RuntimeException("Stub!");
    }
}