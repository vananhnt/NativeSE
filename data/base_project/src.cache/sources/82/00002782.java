package javax.crypto;

import gov.nist.core.Separators;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.Set;
import org.apache.harmony.crypto.internal.NullCipherSpi;
import org.apache.harmony.security.fortress.Engine;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Cipher.class */
public class Cipher {
    public static final int DECRYPT_MODE = 2;
    public static final int ENCRYPT_MODE = 1;
    public static final int PRIVATE_KEY = 2;
    public static final int PUBLIC_KEY = 1;
    public static final int SECRET_KEY = 3;
    public static final int UNWRAP_MODE = 4;
    public static final int WRAP_MODE = 3;
    private int mode;
    private static final String SERVICE = "Cipher";
    private static final Engine ENGINE = new Engine(SERVICE);
    private Provider provider;
    private CipherSpi spiImpl;
    private String transformation;
    private static SecureRandom secureRandom;

    /* JADX INFO: Access modifiers changed from: protected */
    public Cipher(CipherSpi cipherSpi, Provider provider, String transformation) {
        if (cipherSpi == null) {
            throw new NullPointerException("cipherSpi == null");
        }
        if (!(cipherSpi instanceof NullCipherSpi) && provider == null) {
            throw new NullPointerException("provider == null");
        }
        this.provider = provider;
        this.transformation = transformation;
        this.spiImpl = cipherSpi;
    }

    public static final Cipher getInstance(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
        return getCipher(transformation, null);
    }

    public static final Cipher getInstance(String transformation, String provider) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        }
        Provider p = Security.getProvider(provider);
        if (p == null) {
            throw new NoSuchProviderException("Provider not available: " + provider);
        }
        return getInstance(transformation, p);
    }

    public static final Cipher getInstance(String transformation, Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        }
        Cipher c = getCipher(transformation, provider);
        return c;
    }

    private static NoSuchAlgorithmException invalidTransformation(String transformation) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("Invalid transformation: " + transformation);
    }

    private static synchronized Cipher getCipher(String transformation, Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (transformation == null || transformation.isEmpty()) {
            throw invalidTransformation(transformation);
        }
        String[] transf = checkTransformation(transformation);
        boolean needSetPadding = false;
        boolean needSetMode = false;
        Object engineSpi = null;
        Provider engineProvider = provider;
        if (transf[1] == null && transf[2] == null) {
            if (provider == null) {
                Engine.SpiAndProvider sap = ENGINE.getInstance(transf[0], null);
                engineSpi = sap.spi;
                engineProvider = sap.provider;
            } else {
                engineSpi = ENGINE.getInstance(transf[0], provider, null);
            }
        } else {
            String[] searchOrder = {transf[0] + Separators.SLASH + transf[1] + Separators.SLASH + transf[2], transf[0] + Separators.SLASH + transf[1], transf[0] + "//" + transf[2], transf[0]};
            int i = 0;
            while (i < searchOrder.length) {
                try {
                    if (provider == null) {
                        Engine.SpiAndProvider sap2 = ENGINE.getInstance(searchOrder[i], null);
                        engineSpi = sap2.spi;
                        engineProvider = sap2.provider;
                    } else {
                        engineSpi = ENGINE.getInstance(searchOrder[i], provider, null);
                    }
                    break;
                } catch (NoSuchAlgorithmException e) {
                    if (i != searchOrder.length - 1) {
                        i++;
                    } else {
                        throw new NoSuchAlgorithmException(transformation, e);
                    }
                }
            }
            switch (i) {
                case 1:
                    needSetPadding = true;
                    break;
                case 2:
                    needSetMode = true;
                    break;
                case 3:
                    needSetPadding = true;
                    needSetMode = true;
                    break;
            }
        }
        if (engineSpi == null || engineProvider == null) {
            throw new NoSuchAlgorithmException(transformation);
        }
        if (!(engineSpi instanceof CipherSpi)) {
            throw new NoSuchAlgorithmException(engineSpi.getClass().getName());
        }
        CipherSpi cspi = (CipherSpi) engineSpi;
        Cipher c = new Cipher(cspi, engineProvider, transformation);
        if (needSetMode) {
            c.spiImpl.engineSetMode(transf[1]);
        }
        if (needSetPadding) {
            c.spiImpl.engineSetPadding(transf[2]);
        }
        return c;
    }

    private static String[] checkTransformation(String transformation) throws NoSuchAlgorithmException {
        if (transformation.startsWith(Separators.SLASH)) {
            transformation = transformation.substring(1);
        }
        String[] pieces = transformation.split(Separators.SLASH);
        if (pieces.length > 3) {
            throw invalidTransformation(transformation);
        }
        String[] result = new String[3];
        for (int i = 0; i < pieces.length; i++) {
            String piece = pieces[i].trim();
            if (!piece.isEmpty()) {
                result[i] = piece;
            }
        }
        if (result[0] == null) {
            throw invalidTransformation(transformation);
        }
        if ((result[1] != null || result[2] != null) && (result[1] == null || result[2] == null)) {
            throw invalidTransformation(transformation);
        }
        return result;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final String getAlgorithm() {
        return this.transformation;
    }

    public final int getBlockSize() {
        return this.spiImpl.engineGetBlockSize();
    }

    public final int getOutputSize(int inputLen) {
        if (this.mode == 0) {
            throw new IllegalStateException("Cipher has not yet been initialized");
        }
        return this.spiImpl.engineGetOutputSize(inputLen);
    }

    public final byte[] getIV() {
        return this.spiImpl.engineGetIV();
    }

    public final AlgorithmParameters getParameters() {
        return this.spiImpl.engineGetParameters();
    }

    public final ExemptionMechanism getExemptionMechanism() {
        return null;
    }

    public final void init(int opmode, Key key) throws InvalidKeyException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, key, secureRandom);
    }

    public final void init(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        checkMode(opmode);
        this.spiImpl.engineInit(opmode, key, random);
        this.mode = opmode;
    }

    private void checkMode(int mode) {
        if (mode != 1 && mode != 2 && mode != 4 && mode != 3) {
            throw new InvalidParameterException("Invalid mode: " + mode);
        }
    }

    public final void init(int opmode, Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, key, params, secureRandom);
    }

    public final void init(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        checkMode(opmode);
        this.spiImpl.engineInit(opmode, key, params, random);
        this.mode = opmode;
    }

    public final void init(int opmode, Key key, AlgorithmParameters params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, key, params, secureRandom);
    }

    public final void init(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        checkMode(opmode);
        this.spiImpl.engineInit(opmode, key, params, random);
        this.mode = opmode;
    }

    public final void init(int opmode, Certificate certificate) throws InvalidKeyException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, certificate, secureRandom);
    }

    public final void init(int opmode, Certificate certificate, SecureRandom random) throws InvalidKeyException {
        boolean[] keyUsage;
        checkMode(opmode);
        if (certificate instanceof X509Certificate) {
            Set<String> ce = ((X509Certificate) certificate).getCriticalExtensionOIDs();
            boolean critical = false;
            if (ce != null && !ce.isEmpty()) {
                Iterator i$ = ce.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    String oid = i$.next();
                    if (oid.equals("2.5.29.15")) {
                        critical = true;
                        break;
                    }
                }
                if (critical && (keyUsage = ((X509Certificate) certificate).getKeyUsage()) != null) {
                    if (opmode == 1 && !keyUsage[3]) {
                        throw new InvalidKeyException("The public key in the certificate cannot be used for ENCRYPT_MODE");
                    }
                    if (opmode == 3 && !keyUsage[2]) {
                        throw new InvalidKeyException("The public key in the certificate cannot be used for WRAP_MODE");
                    }
                }
            }
        }
        this.spiImpl.engineInit(opmode, certificate.getPublicKey(), random);
        this.mode = opmode;
    }

    public final byte[] update(byte[] input) {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        if (input.length == 0) {
            return null;
        }
        return this.spiImpl.engineUpdate(input, 0, input.length);
    }

    public final byte[] update(byte[] input, int inputOffset, int inputLen) {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        checkInputOffsetAndCount(input.length, inputOffset, inputLen);
        if (input.length == 0) {
            return null;
        }
        return this.spiImpl.engineUpdate(input, inputOffset, inputLen);
    }

    private static void checkInputOffsetAndCount(int inputArrayLength, int inputOffset, int inputLen) {
        if ((inputOffset | inputLen) < 0 || inputOffset > inputArrayLength || inputArrayLength - inputOffset < inputLen) {
            throw new IllegalArgumentException("input.length=" + inputArrayLength + "; inputOffset=" + inputOffset + "; inputLen=" + inputLen);
        }
    }

    public final int update(byte[] input, int inputOffset, int inputLen, byte[] output) throws ShortBufferException {
        return update(input, inputOffset, inputLen, output, 0);
    }

    public final int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        if (output == null) {
            throw new IllegalArgumentException("output == null");
        }
        if (outputOffset < 0) {
            throw new IllegalArgumentException("outputOffset < 0. outputOffset=" + outputOffset);
        }
        checkInputOffsetAndCount(input.length, inputOffset, inputLen);
        if (input.length == 0) {
            return 0;
        }
        return this.spiImpl.engineUpdate(input, inputOffset, inputLen, output, outputOffset);
    }

    public final int update(ByteBuffer input, ByteBuffer output) throws ShortBufferException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input == output) {
            throw new IllegalArgumentException("input == output");
        }
        return this.spiImpl.engineUpdate(input, output);
    }

    public final void updateAAD(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input.length == 0) {
            return;
        }
        this.spiImpl.engineUpdateAAD(input, 0, input.length);
    }

    public final void updateAAD(byte[] input, int inputOffset, int inputLen) {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        checkInputOffsetAndCount(input.length, inputOffset, inputLen);
        if (input.length == 0) {
            return;
        }
        this.spiImpl.engineUpdateAAD(input, inputOffset, inputLen);
    }

    public final void updateAAD(ByteBuffer input) {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException("Cipher is not initialized");
        }
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        this.spiImpl.engineUpdateAAD(input);
    }

    public final byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        return this.spiImpl.engineDoFinal(null, 0, 0);
    }

    public final int doFinal(byte[] output, int outputOffset) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (outputOffset < 0) {
            throw new IllegalArgumentException("outputOffset < 0. outputOffset=" + outputOffset);
        }
        return this.spiImpl.engineDoFinal(null, 0, 0, output, outputOffset);
    }

    public final byte[] doFinal(byte[] input) throws IllegalBlockSizeException, BadPaddingException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        return this.spiImpl.engineDoFinal(input, 0, input.length);
    }

    public final byte[] doFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        checkInputOffsetAndCount(input.length, inputOffset, inputLen);
        return this.spiImpl.engineDoFinal(input, inputOffset, inputLen);
    }

    public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return doFinal(input, inputOffset, inputLen, output, 0);
    }

    public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        checkInputOffsetAndCount(input.length, inputOffset, inputLen);
        return this.spiImpl.engineDoFinal(input, inputOffset, inputLen, output, outputOffset);
    }

    public final int doFinal(ByteBuffer input, ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (this.mode != 1 && this.mode != 2) {
            throw new IllegalStateException();
        }
        if (input == output) {
            throw new IllegalArgumentException("input == output");
        }
        return this.spiImpl.engineDoFinal(input, output);
    }

    public final byte[] wrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        if (this.mode != 3) {
            throw new IllegalStateException();
        }
        return this.spiImpl.engineWrap(key);
    }

    public final Key unwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        if (this.mode != 4) {
            throw new IllegalStateException();
        }
        return this.spiImpl.engineUnwrap(wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
    }

    public static final int getMaxAllowedKeyLength(String transformation) throws NoSuchAlgorithmException {
        if (transformation == null) {
            throw new NullPointerException("transformation == null");
        }
        checkTransformation(transformation);
        return Integer.MAX_VALUE;
    }

    public static final AlgorithmParameterSpec getMaxAllowedParameterSpec(String transformation) throws NoSuchAlgorithmException {
        if (transformation == null) {
            throw new NullPointerException("transformation == null");
        }
        checkTransformation(transformation);
        return null;
    }
}