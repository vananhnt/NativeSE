package java.security;

import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyPairGenerator.class */
public abstract class KeyPairGenerator extends KeyPairGeneratorSpi {
    protected KeyPairGenerator(String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public static KeyPairGenerator getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static KeyPairGenerator getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static KeyPairGenerator getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public void initialize(int keysize) {
        throw new RuntimeException("Stub!");
    }

    public void initialize(AlgorithmParameterSpec param) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public final KeyPair genKeyPair() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(int keysize, SecureRandom random) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec param, SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: KeyPairGenerator$KeyPairGeneratorImpl.class */
    private static class KeyPairGeneratorImpl extends KeyPairGenerator {
        private KeyPairGeneratorSpi spiImpl;

        private KeyPairGeneratorImpl(KeyPairGeneratorSpi keyPairGeneratorSpi, Provider provider, String algorithm) {
            super(algorithm);
            KeyPairGenerator.access$102(this, provider);
            this.spiImpl = keyPairGeneratorSpi;
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public void initialize(int keysize, SecureRandom random) {
            this.spiImpl.initialize(keysize, random);
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public KeyPair generateKeyPair() {
            return this.spiImpl.generateKeyPair();
        }

        @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
        public void initialize(AlgorithmParameterSpec param, SecureRandom random) throws InvalidAlgorithmParameterException {
            this.spiImpl.initialize(param, random);
        }
    }
}