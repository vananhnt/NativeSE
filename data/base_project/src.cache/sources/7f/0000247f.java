package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecureRandomSpi.class */
public abstract class SecureRandomSpi implements Serializable {
    protected abstract void engineSetSeed(byte[] bArr);

    protected abstract void engineNextBytes(byte[] bArr);

    protected abstract byte[] engineGenerateSeed(int i);

    public SecureRandomSpi() {
        throw new RuntimeException("Stub!");
    }
}