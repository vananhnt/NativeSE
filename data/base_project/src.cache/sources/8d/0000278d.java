package javax.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.harmony.security.fortress.Engine;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyAgreement.class */
public class KeyAgreement {
    private static final Engine ENGINE = new Engine("KeyAgreement");
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Provider provider;
    private final KeyAgreementSpi spiImpl;
    private final String algorithm;

    protected KeyAgreement(KeyAgreementSpi keyAgreeSpi, Provider provider, String algorithm) {
        this.provider = provider;
        this.algorithm = algorithm;
        this.spiImpl = keyAgreeSpi;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public static final KeyAgreement getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        Engine.SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        return new KeyAgreement((KeyAgreementSpi) sap.spi, sap.provider, algorithm);
    }

    public static final KeyAgreement getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("Provider is null or empty");
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider == null) {
            throw new NoSuchProviderException(provider);
        }
        return getInstance(algorithm, impProvider);
    }

    public static final KeyAgreement getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        }
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        Object spi = ENGINE.getInstance(algorithm, provider, null);
        return new KeyAgreement((KeyAgreementSpi) spi, provider, algorithm);
    }

    public final void init(Key key) throws InvalidKeyException {
        this.spiImpl.engineInit(key, RANDOM);
    }

    public final void init(Key key, SecureRandom random) throws InvalidKeyException {
        this.spiImpl.engineInit(key, random);
    }

    public final void init(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.spiImpl.engineInit(key, params, RANDOM);
    }

    public final void init(Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.spiImpl.engineInit(key, params, random);
    }

    public final Key doPhase(Key key, boolean lastPhase) throws InvalidKeyException, IllegalStateException {
        return this.spiImpl.engineDoPhase(key, lastPhase);
    }

    public final byte[] generateSecret() throws IllegalStateException {
        return this.spiImpl.engineGenerateSecret();
    }

    public final int generateSecret(byte[] sharedSecret, int offset) throws IllegalStateException, ShortBufferException {
        return this.spiImpl.engineGenerateSecret(sharedSecret, offset);
    }

    public final SecretKey generateSecret(String algorithm) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        return this.spiImpl.engineGenerateSecret(algorithm);
    }
}