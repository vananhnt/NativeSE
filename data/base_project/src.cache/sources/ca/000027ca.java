package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import org.apache.harmony.security.fortress.Engine;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyManagerFactory.class */
public class KeyManagerFactory {
    private static final String SERVICE = "KeyManagerFactory";
    private static final Engine ENGINE = new Engine(SERVICE);
    private static final String PROPERTY_NAME = "ssl.KeyManagerFactory.algorithm";
    private static final String DEFAULT_PROPERTY = "PKIX";
    private final Provider provider;
    private final KeyManagerFactorySpi spiImpl;
    private final String algorithm;

    public static final String getDefaultAlgorithm() {
        String algorithm = Security.getProperty(PROPERTY_NAME);
        return algorithm != null ? algorithm : DEFAULT_PROPERTY;
    }

    public static final KeyManagerFactory getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        Engine.SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        return new KeyManagerFactory((KeyManagerFactorySpi) sap.spi, sap.provider, algorithm);
    }

    public static final KeyManagerFactory getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.length() == 0) {
            throw new IllegalArgumentException("Provider is null or empty");
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider == null) {
            throw new NoSuchProviderException(provider);
        }
        return getInstance(algorithm, impProvider);
    }

    public static final KeyManagerFactory getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("Provider is null");
        }
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        Object spi = ENGINE.getInstance(algorithm, provider, null);
        return new KeyManagerFactory((KeyManagerFactorySpi) spi, provider, algorithm);
    }

    protected KeyManagerFactory(KeyManagerFactorySpi factorySpi, Provider provider, String algorithm) {
        this.provider = provider;
        this.algorithm = algorithm;
        this.spiImpl = factorySpi;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final void init(KeyStore ks, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.spiImpl.engineInit(ks, password);
    }

    public final void init(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
        this.spiImpl.engineInit(spec);
    }

    public final KeyManager[] getKeyManagers() {
        return this.spiImpl.engineGetKeyManagers();
    }
}