package java.security;

import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: MessageDigest.class */
public abstract class MessageDigest extends MessageDigestSpi {
    protected MessageDigest(String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public static MessageDigest getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static MessageDigest getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static MessageDigest getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public void reset() {
        throw new RuntimeException("Stub!");
    }

    public void update(byte arg0) {
        throw new RuntimeException("Stub!");
    }

    public void update(byte[] input, int offset, int len) {
        throw new RuntimeException("Stub!");
    }

    public void update(byte[] input) {
        throw new RuntimeException("Stub!");
    }

    public byte[] digest() {
        throw new RuntimeException("Stub!");
    }

    public int digest(byte[] buf, int offset, int len) throws DigestException {
        throw new RuntimeException("Stub!");
    }

    public byte[] digest(byte[] input) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public static boolean isEqual(byte[] digesta, byte[] digestb) {
        throw new RuntimeException("Stub!");
    }

    public final String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final int getDigestLength() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.MessageDigestSpi
    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    public final void update(ByteBuffer input) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: MessageDigest$MessageDigestImpl.class */
    private static class MessageDigestImpl extends MessageDigest {
        private MessageDigestSpi spiImpl;

        private MessageDigestImpl(MessageDigestSpi messageDigestSpi, Provider provider, String algorithm) {
            super(algorithm);
            MessageDigest.access$102(this, provider);
            this.spiImpl = messageDigestSpi;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.MessageDigestSpi
        public void engineReset() {
            this.spiImpl.engineReset();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.MessageDigestSpi
        public byte[] engineDigest() {
            return this.spiImpl.engineDigest();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.MessageDigestSpi
        public int engineGetDigestLength() {
            return this.spiImpl.engineGetDigestLength();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.MessageDigestSpi
        public void engineUpdate(byte arg0) {
            this.spiImpl.engineUpdate(arg0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.security.MessageDigestSpi
        public void engineUpdate(byte[] arg0, int arg1, int arg2) {
            this.spiImpl.engineUpdate(arg0, arg1, arg2);
        }

        @Override // java.security.MessageDigest, java.security.MessageDigestSpi
        public Object clone() throws CloneNotSupportedException {
            if (this.spiImpl instanceof Cloneable) {
                MessageDigestSpi spi = (MessageDigestSpi) this.spiImpl.clone();
                return new MessageDigestImpl(spi, getProvider(), getAlgorithm());
            }
            throw new CloneNotSupportedException();
        }
    }
}