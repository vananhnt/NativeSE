package org.apache.harmony.crypto.internal;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

/* loaded from: NullCipherSpi.class */
public class NullCipherSpi extends CipherSpi {
    @Override // javax.crypto.CipherSpi
    public void engineSetMode(String arg0) throws NoSuchAlgorithmException {
    }

    @Override // javax.crypto.CipherSpi
    public void engineSetPadding(String arg0) throws NoSuchPaddingException {
    }

    @Override // javax.crypto.CipherSpi
    public int engineGetBlockSize() {
        return 1;
    }

    @Override // javax.crypto.CipherSpi
    public int engineGetOutputSize(int inputLen) {
        return inputLen;
    }

    @Override // javax.crypto.CipherSpi
    public byte[] engineGetIV() {
        return new byte[8];
    }

    @Override // javax.crypto.CipherSpi
    public AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    public void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
    }

    @Override // javax.crypto.CipherSpi
    public void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
    }

    @Override // javax.crypto.CipherSpi
    public void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
    }

    @Override // javax.crypto.CipherSpi
    public byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        if (input == null) {
            return null;
        }
        byte[] result = new byte[inputLen];
        System.arraycopy(input, inputOffset, result, 0, inputLen);
        return result;
    }

    @Override // javax.crypto.CipherSpi
    public int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (input == null) {
            return 0;
        }
        System.arraycopy(input, inputOffset, output, outputOffset, inputLen);
        return inputLen;
    }

    @Override // javax.crypto.CipherSpi
    public int engineUpdate(ByteBuffer input, ByteBuffer output) throws ShortBufferException {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        if (output == null) {
            throw new NullPointerException("output == null");
        }
        int result = input.limit() - input.position();
        try {
            output.put(input);
            return result;
        } catch (BufferOverflowException e) {
            throw new ShortBufferException("output buffer too small");
        }
    }

    @Override // javax.crypto.CipherSpi
    public byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        if (input == null) {
            return null;
        }
        return engineUpdate(input, inputOffset, inputLen);
    }

    @Override // javax.crypto.CipherSpi
    public int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        int result = engineUpdate(input, inputOffset, inputLen, output, outputOffset);
        return result;
    }

    @Override // javax.crypto.CipherSpi
    public int engineDoFinal(ByteBuffer input, ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return engineUpdate(input, output);
    }

    @Override // javax.crypto.CipherSpi
    public byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        throw new UnsupportedOperationException();
    }

    @Override // javax.crypto.CipherSpi
    public Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        throw new UnsupportedOperationException();
    }

    @Override // javax.crypto.CipherSpi
    public int engineGetKeySize(Key key) throws InvalidKeyException {
        throw new UnsupportedOperationException();
    }
}