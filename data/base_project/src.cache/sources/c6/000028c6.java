package libcore.icu;

import java.text.CollationKey;

/* loaded from: CollationKeyICU.class */
public final class CollationKeyICU extends CollationKey {
    private final byte[] bytes;
    private int hashCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CollationKeyICU(String source, byte[] bytes) {
        super(source);
        this.bytes = bytes;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.text.CollationKey, java.lang.Comparable
    public int compareTo(CollationKey other) {
        byte[] rhsBytes;
        if (other instanceof CollationKeyICU) {
            rhsBytes = ((CollationKeyICU) other).bytes;
        } else {
            rhsBytes = other.toByteArray();
        }
        if (this.bytes == null || this.bytes.length == 0) {
            if (rhsBytes == null || rhsBytes.length == 0) {
                return 0;
            }
            return -1;
        } else if (rhsBytes == null || rhsBytes.length == 0) {
            return 1;
        } else {
            int count = Math.min(this.bytes.length, rhsBytes.length);
            for (int i = 0; i < count; i++) {
                int s = this.bytes[i] & 255;
                int t = rhsBytes[i] & 255;
                if (s < t) {
                    return -1;
                }
                if (s > t) {
                    return 1;
                }
            }
            if (this.bytes.length < rhsBytes.length) {
                return -1;
            }
            if (this.bytes.length > rhsBytes.length) {
                return 1;
            }
            return 0;
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof CollationKey) && compareTo((CollationKey) object) == 0;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            if (this.bytes != null && this.bytes.length != 0) {
                int len = this.bytes.length;
                int inc = ((len - 32) / 32) + 1;
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= len) {
                        break;
                    }
                    this.hashCode = (this.hashCode * 37) + this.bytes[i2];
                    i = i2 + inc;
                }
            }
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    @Override // java.text.CollationKey
    public byte[] toByteArray() {
        if (this.bytes == null || this.bytes.length == 0) {
            return null;
        }
        return (byte[]) this.bytes.clone();
    }
}