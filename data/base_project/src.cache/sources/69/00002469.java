package java.security;

import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: MessageDigestSpi.class */
public abstract class MessageDigestSpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineUpdate(byte b);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineUpdate(byte[] bArr, int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract byte[] engineDigest();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineReset();

    public MessageDigestSpi() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int engineGetDigestLength() {
        throw new RuntimeException("Stub!");
    }

    protected void engineUpdate(ByteBuffer input) {
        throw new RuntimeException("Stub!");
    }

    protected int engineDigest(byte[] buf, int offset, int len) throws DigestException {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}