package java.util.zip;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Checksum.class */
public interface Checksum {
    long getValue();

    void reset();

    void update(byte[] bArr, int i, int i2);

    void update(int i);
}