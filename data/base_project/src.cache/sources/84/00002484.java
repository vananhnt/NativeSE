package java.security;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Signature.class */
public abstract class Signature extends SignatureSpi {
    protected static final int UNINITIALIZED = 0;
    protected static final int SIGN = 2;
    protected static final int VERIFY = 3;
    protected int state;

    /* JADX INFO: Access modifiers changed from: protected */
    public Signature(String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public static Signature getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static Signature getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static Signature getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public final void initVerify(PublicKey publicKey) throws InvalidKeyException {
        throw new RuntimeException("Stub!");
    }

    public final void initVerify(java.security.cert.Certificate certificate) throws InvalidKeyException {
        throw new RuntimeException("Stub!");
    }

    public final void initSign(PrivateKey privateKey) throws InvalidKeyException {
        throw new RuntimeException("Stub!");
    }

    public final void initSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        throw new RuntimeException("Stub!");
    }

    public final byte[] sign() throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final int sign(byte[] outbuf, int offset, int len) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final boolean verify(byte[] signature) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final boolean verify(byte[] signature, int offset, int length) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final void update(byte b) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final void update(byte[] data) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final void update(byte[] data, int off, int len) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public final void update(ByteBuffer data) throws SignatureException {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void setParameter(String param, Object value) throws InvalidParameterException {
        throw new RuntimeException("Stub!");
    }

    public final void setParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("Stub!");
    }

    public final AlgorithmParameters getParameters() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final Object getParameter(String param) throws InvalidParameterException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.SignatureSpi
    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Signature$SignatureImpl.class */
    private static class SignatureImpl extends Signature {
        private SignatureSpi spiImpl;

        public SignatureImpl(SignatureSpi signatureSpi, Provider provider, String algorithm) {
            super(algorithm);
            Signature.access$002(this, provider);
            this.spiImpl = signatureSpi;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public byte[] engineSign() throws SignatureException {
            return this.spiImpl.engineSign();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public void engineUpdate(byte arg0) throws SignatureException {
            this.spiImpl.engineUpdate(arg0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public boolean engineVerify(byte[] arg0) throws SignatureException {
            return this.spiImpl.engineVerify(arg0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public void engineUpdate(byte[] arg0, int arg1, int arg2) throws SignatureException {
            this.spiImpl.engineUpdate(arg0, arg1, arg2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public void engineInitSign(PrivateKey arg0) throws InvalidKeyException {
            this.spiImpl.engineInitSign(arg0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public void engineInitVerify(PublicKey arg0) throws InvalidKeyException {
            this.spiImpl.engineInitVerify(arg0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public Object engineGetParameter(String arg0) throws InvalidParameterException {
            return this.spiImpl.engineGetParameter(arg0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.SignatureSpi
        public void engineSetParameter(String arg0, Object arg1) throws InvalidParameterException {
            this.spiImpl.engineSetParameter(arg0, arg1);
        }

        @Override // java.security.Signature, java.security.SignatureSpi
        public Object clone() throws CloneNotSupportedException {
            if (this.spiImpl instanceof Cloneable) {
                SignatureSpi spi = (SignatureSpi) this.spiImpl.clone();
                return new SignatureImpl(spi, getProvider(), getAlgorithm());
            }
            throw new CloneNotSupportedException();
        }
    }
}