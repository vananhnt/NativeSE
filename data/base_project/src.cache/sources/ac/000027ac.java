package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RC5ParameterSpec.class */
public class RC5ParameterSpec implements AlgorithmParameterSpec {
    private final int version;
    private final int rounds;
    private final int wordSize;
    private final byte[] iv;

    public RC5ParameterSpec(int version, int rounds, int wordSize) {
        this.version = version;
        this.rounds = rounds;
        this.wordSize = wordSize;
        this.iv = null;
    }

    public RC5ParameterSpec(int version, int rounds, int wordSize, byte[] iv) {
        if (iv == null) {
            throw new IllegalArgumentException("iv == null");
        }
        if (iv.length < 2 * (wordSize / 8)) {
            throw new IllegalArgumentException("iv.length < 2 * (wordSize / 8)");
        }
        this.version = version;
        this.rounds = rounds;
        this.wordSize = wordSize;
        this.iv = new byte[2 * (wordSize / 8)];
        System.arraycopy(iv, 0, this.iv, 0, 2 * (wordSize / 8));
    }

    public RC5ParameterSpec(int version, int rounds, int wordSize, byte[] iv, int offset) {
        if (iv == null) {
            throw new IllegalArgumentException("iv == null");
        }
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("offset < 0: " + offset);
        }
        if (iv.length - offset < 2 * (wordSize / 8)) {
            throw new IllegalArgumentException("iv.length - offset < 2 * (wordSize / 8)");
        }
        this.version = version;
        this.rounds = rounds;
        this.wordSize = wordSize;
        this.iv = new byte[offset + (2 * (wordSize / 8))];
        System.arraycopy(iv, offset, this.iv, 0, 2 * (wordSize / 8));
    }

    public int getVersion() {
        return this.version;
    }

    public int getRounds() {
        return this.rounds;
    }

    public int getWordSize() {
        return this.wordSize;
    }

    public byte[] getIV() {
        if (this.iv == null) {
            return null;
        }
        byte[] result = new byte[this.iv.length];
        System.arraycopy(this.iv, 0, result, 0, this.iv.length);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RC5ParameterSpec)) {
            return false;
        }
        RC5ParameterSpec ps = (RC5ParameterSpec) obj;
        return this.version == ps.version && this.rounds == ps.rounds && this.wordSize == ps.wordSize && Arrays.equals(this.iv, ps.iv);
    }

    public int hashCode() {
        int result = this.version + this.rounds + this.wordSize;
        if (this.iv == null) {
            return result;
        }
        byte[] arr$ = this.iv;
        for (byte element : arr$) {
            result += element & 255;
        }
        return result;
    }
}