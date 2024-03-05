package javax.crypto.spec;

import java.security.InvalidKeyException;
import java.security.spec.KeySpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DESKeySpec.class */
public class DESKeySpec implements KeySpec {
    public static final int DES_KEY_LEN = 8;
    private final byte[] key;
    private static final byte[][] SEMIWEAKS = {new byte[]{-32, 1, -32, 1, -15, 1, -15, 1}, new byte[]{1, -32, 1, -32, 1, -15, 1, -15}, new byte[]{-2, 31, -2, 31, -2, 14, -2, 14}, new byte[]{31, -2, 31, -2, 14, -2, 14, -2}, new byte[]{-32, 31, -32, 31, -15, 14, -15, 14}, new byte[]{31, -32, 31, -32, 14, -15, 14, -15}, new byte[]{1, -2, 1, -2, 1, -2, 1, -2}, new byte[]{-2, 1, -2, 1, -2, 1, -2, 1}, new byte[]{1, 31, 1, 31, 1, 14, 1, 14}, new byte[]{31, 1, 31, 1, 14, 1, 14, 1}, new byte[]{-32, -2, -32, -2, -15, -2, -15, -2}, new byte[]{-2, -32, -2, -32, -2, -15, -2, -15}, new byte[]{1, 1, 1, 1, 1, 1, 1, 1}, new byte[]{-2, -2, -2, -2, -2, -2, -2, -2}, new byte[]{-32, -32, -32, -32, -15, -15, -15, -15}, new byte[]{31, 31, 31, 31, 14, 14, 14, 14}};

    public DESKeySpec(byte[] key) throws InvalidKeyException {
        this(key, 0);
    }

    public DESKeySpec(byte[] key, int offset) throws InvalidKeyException {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (key.length - offset < 8) {
            throw new InvalidKeyException("key too short");
        }
        this.key = new byte[8];
        System.arraycopy(key, offset, this.key, 0, 8);
    }

    public byte[] getKey() {
        byte[] result = new byte[8];
        System.arraycopy(this.key, 0, result, 0, 8);
        return result;
    }

    public static boolean isParityAdjusted(byte[] key, int offset) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        if (key.length - offset < 8) {
            throw new InvalidKeyException("key too short");
        }
        for (int i = offset; i < 8; i++) {
            byte b = key[i];
            int byteKey = b ^ (b >> 1);
            int byteKey2 = byteKey ^ (byteKey >> 2);
            if (((byteKey2 ^ (byteKey2 >> 4)) & 1) == 0) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x004d, code lost:
        r7 = r7 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean isWeak(byte[] r5, int r6) throws java.security.InvalidKeyException {
        /*
            r0 = r5
            if (r0 != 0) goto Le
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            r1 = r0
            java.lang.String r2 = "key == null"
            r1.<init>(r2)
            throw r0
        Le:
            r0 = r5
            int r0 = r0.length
            r1 = r6
            int r0 = r0 - r1
            r1 = 8
            if (r0 >= r1) goto L21
            java.security.InvalidKeyException r0 = new java.security.InvalidKeyException
            r1 = r0
            java.lang.String r2 = "key too short"
            r1.<init>(r2)
            throw r0
        L21:
            r0 = 0
            r7 = r0
        L23:
            r0 = r7
            byte[][] r1 = javax.crypto.spec.DESKeySpec.SEMIWEAKS
            int r1 = r1.length
            if (r0 >= r1) goto L53
            r0 = 0
            r8 = r0
        L2d:
            r0 = r8
            r1 = 8
            if (r0 >= r1) goto L4b
            byte[][] r0 = javax.crypto.spec.DESKeySpec.SEMIWEAKS
            r1 = r7
            r0 = r0[r1]
            r1 = r8
            r0 = r0[r1]
            r1 = r5
            r2 = r6
            r3 = r8
            int r2 = r2 + r3
            r1 = r1[r2]
            if (r0 == r1) goto L45
            goto L4d
        L45:
            int r8 = r8 + 1
            goto L2d
        L4b:
            r0 = 1
            return r0
        L4d:
            int r7 = r7 + 1
            goto L23
        L53:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.crypto.spec.DESKeySpec.isWeak(byte[], int):boolean");
    }
}