package java.security;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SignatureSpi.class */
public abstract class SignatureSpi {
    protected SecureRandom appRandom;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInitVerify(PublicKey publicKey) throws InvalidKeyException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInitSign(PrivateKey privateKey) throws InvalidKeyException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineUpdate(byte b) throws SignatureException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract byte[] engineSign() throws SignatureException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract boolean engineVerify(byte[] bArr) throws SignatureException;

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public abstract void engineSetParameter(String str, Object obj) throws InvalidParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public abstract Object engineGetParameter(String str) throws InvalidParameterException;

    public SignatureSpi() {
        throw new RuntimeException("Stub!");
    }

    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        throw new RuntimeException("Stub!");
    }

    protected void engineUpdate(ByteBuffer input) {
        throw new RuntimeException("Stub!");
    }

    protected int engineSign(byte[] outbuf, int offset, int len) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    protected boolean engineVerify(byte[] sigBytes, int offset, int length) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    protected void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    protected AlgorithmParameters engineGetParameters() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}