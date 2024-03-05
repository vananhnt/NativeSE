package javax.crypto.spec;

import java.io.Serializable;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecretKeySpec.class */
public class SecretKeySpec implements SecretKey, KeySpec, Serializable {
    private static final long serialVersionUID = 6577238317307289933L;
    private final byte[] key;
    private final String algorithm;

    public SecretKeySpec(byte[] key, String algorithm) {
        if (key == null) {
            throw new IllegalArgumentException("key == null");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("key.length == 0");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm == null");
        }
        this.algorithm = algorithm;
        this.key = new byte[key.length];
        System.arraycopy(key, 0, this.key, 0, key.length);
    }

    public SecretKeySpec(byte[] key, int offset, int len, String algorithm) {
        if (key == null) {
            throw new IllegalArgumentException("key == null");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("key.length == 0");
        }
        if (len < 0 || offset < 0) {
            throw new ArrayIndexOutOfBoundsException("len < 0 || offset < 0");
        }
        if (key.length - offset < len) {
            throw new IllegalArgumentException("key too short");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm == null");
        }
        this.algorithm = algorithm;
        this.key = new byte[len];
        System.arraycopy(key, offset, this.key, 0, len);
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "RAW";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        byte[] result = new byte[this.key.length];
        System.arraycopy(this.key, 0, result, 0, this.key.length);
        return result;
    }

    public int hashCode() {
        int result = this.algorithm.length();
        byte[] arr$ = this.key;
        for (byte element : arr$) {
            result += element;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SecretKeySpec)) {
            return false;
        }
        SecretKeySpec ks = (SecretKeySpec) obj;
        return this.algorithm.equalsIgnoreCase(ks.algorithm) && Arrays.equals(this.key, ks.key);
    }
}