package javax.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.harmony.security.fortress.Engine;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Mac.class */
public class Mac implements Cloneable {
    private static final Engine ENGINE = new Engine("Mac");
    private final Provider provider;
    private final MacSpi spiImpl;
    private final String algorithm;
    private boolean isInitMac = false;

    protected Mac(MacSpi macSpi, Provider provider, String algorithm) {
        this.provider = provider;
        this.algorithm = algorithm;
        this.spiImpl = macSpi;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public static final Mac getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        Engine.SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        return new Mac((MacSpi) sap.spi, sap.provider, algorithm);
    }

    public static final Mac getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("Provider is null or empty");
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider == null) {
            throw new NoSuchProviderException(provider);
        }
        return getInstance(algorithm, impProvider);
    }

    public static final Mac getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        }
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        Object spi = ENGINE.getInstance(algorithm, provider, null);
        return new Mac((MacSpi) spi, provider, algorithm);
    }

    public final int getMacLength() {
        return this.spiImpl.engineGetMacLength();
    }

    public final void init(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        this.spiImpl.engineInit(key, params);
        this.isInitMac = true;
    }

    public final void init(Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        try {
            this.spiImpl.engineInit(key, null);
            this.isInitMac = true;
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public final void update(byte input) throws IllegalStateException {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        this.spiImpl.engineUpdate(input);
    }

    public final void update(byte[] input, int offset, int len) throws IllegalStateException {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        if (input == null) {
            return;
        }
        if (offset < 0 || len < 0 || offset + len > input.length) {
            throw new IllegalArgumentException("Incorrect arguments. input.length=" + input.length + " offset=" + offset + ", len=" + len);
        }
        this.spiImpl.engineUpdate(input, offset, len);
    }

    public final void update(byte[] input) throws IllegalStateException {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        if (input != null) {
            this.spiImpl.engineUpdate(input, 0, input.length);
        }
    }

    public final void update(ByteBuffer input) {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        if (input != null) {
            this.spiImpl.engineUpdate(input);
            return;
        }
        throw new IllegalArgumentException("input == null");
    }

    public final byte[] doFinal() throws IllegalStateException {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        return this.spiImpl.engineDoFinal();
    }

    public final void doFinal(byte[] output, int outOffset) throws ShortBufferException, IllegalStateException {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        if (output == null) {
            throw new ShortBufferException("output == null");
        }
        if (outOffset < 0 || outOffset >= output.length) {
            throw new ShortBufferException("Incorrect outOffset: " + outOffset);
        }
        int t = this.spiImpl.engineGetMacLength();
        if (t > output.length - outOffset) {
            throw new ShortBufferException("Output buffer is short. Needed " + t + " bytes.");
        }
        byte[] result = this.spiImpl.engineDoFinal();
        System.arraycopy(result, 0, output, outOffset, result.length);
    }

    public final byte[] doFinal(byte[] input) throws IllegalStateException {
        if (!this.isInitMac) {
            throw new IllegalStateException();
        }
        if (input != null) {
            this.spiImpl.engineUpdate(input, 0, input.length);
        }
        return this.spiImpl.engineDoFinal();
    }

    public final void reset() {
        this.spiImpl.engineReset();
    }

    public final Object clone() throws CloneNotSupportedException {
        MacSpi newSpiImpl = (MacSpi) this.spiImpl.clone();
        Mac mac = new Mac(newSpiImpl, this.provider, this.algorithm);
        mac.isInitMac = this.isInitMac;
        return mac;
    }
}