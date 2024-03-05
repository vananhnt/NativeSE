package javax.crypto;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CipherSpi.class */
public abstract class CipherSpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineSetMode(String str) throws NoSuchAlgorithmException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineSetPadding(String str) throws NoSuchPaddingException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int engineGetBlockSize();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int engineGetOutputSize(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract byte[] engineGetIV();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract AlgorithmParameters engineGetParameters();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract byte[] engineUpdate(byte[] bArr, int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract byte[] engineDoFinal(byte[] bArr, int i, int i2) throws IllegalBlockSizeException, BadPaddingException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException;

    /* JADX INFO: Access modifiers changed from: protected */
    public int engineUpdate(ByteBuffer input, ByteBuffer output) throws ShortBufferException {
        byte[] bOutput;
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (output == null) {
            throw new NullPointerException("output == null");
        }
        int position = input.position();
        int limit = input.limit();
        if (limit - position <= 0) {
            return 0;
        }
        if (input.hasArray()) {
            byte[] bInput = input.array();
            int offset = input.arrayOffset();
            bOutput = engineUpdate(bInput, offset + position, limit - position);
            input.position(limit);
        } else {
            byte[] bInput2 = new byte[limit - position];
            input.get(bInput2);
            bOutput = engineUpdate(bInput2, 0, limit - position);
        }
        if (bOutput == null) {
            return 0;
        }
        if (output.remaining() < bOutput.length) {
            throw new ShortBufferException("output buffer too small");
        }
        try {
            output.put(bOutput);
            return bOutput.length;
        } catch (BufferOverflowException e) {
            throw new ShortBufferException("output buffer too small");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void engineUpdateAAD(byte[] input, int inputOffset, int inputLen) {
        throw new UnsupportedOperationException("This cipher does not support Authenticated Encryption with Additional Data");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void engineUpdateAAD(ByteBuffer input) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        int position = input.position();
        int limit = input.limit();
        if (limit - position <= 0) {
            return;
        }
        if (input.hasArray()) {
            byte[] bInput = input.array();
            int offset = input.arrayOffset();
            engineUpdateAAD(bInput, offset + position, limit - position);
            input.position(limit);
            return;
        }
        int len = limit - position;
        byte[] bInput2 = new byte[len];
        input.get(bInput2);
        engineUpdateAAD(bInput2, 0, len);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int engineDoFinal(ByteBuffer input, ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] bOutput;
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (output == null) {
            throw new NullPointerException("output == null");
        }
        int position = input.position();
        int limit = input.limit();
        if (limit - position <= 0) {
            return 0;
        }
        if (input.hasArray()) {
            byte[] bInput = input.array();
            int offset = input.arrayOffset();
            bOutput = engineDoFinal(bInput, offset + position, limit - position);
            input.position(limit);
        } else {
            byte[] bInput2 = new byte[limit - position];
            input.get(bInput2);
            bOutput = engineDoFinal(bInput2, 0, limit - position);
        }
        if (output.remaining() < bOutput.length) {
            throw new ShortBufferException("output buffer too small");
        }
        try {
            output.put(bOutput);
            return bOutput.length;
        } catch (BufferOverflowException e) {
            throw new ShortBufferException("output buffer too small");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        throw new UnsupportedOperationException();
    }

    protected int engineGetKeySize(Key key) throws InvalidKeyException {
        throw new UnsupportedOperationException();
    }
}