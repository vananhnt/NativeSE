package java.security.spec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: EncodedKeySpec.class */
public abstract class EncodedKeySpec implements KeySpec {
    public abstract String getFormat();

    public EncodedKeySpec(byte[] encodedKey) {
        throw new RuntimeException("Stub!");
    }

    public byte[] getEncoded() {
        throw new RuntimeException("Stub!");
    }
}