package com.android.dex;

import java.io.UTFDataFormatException;

/* loaded from: Mutf8.class */
public final class Mutf8 {
    private Mutf8() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x00b6, code lost:
        throw new java.io.UTFDataFormatException("bad second or third byte");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String decode(com.android.dex.util.ByteInput r6, char[] r7) throws java.io.UTFDataFormatException {
        /*
            Method dump skipped, instructions count: 228
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dex.Mutf8.decode(com.android.dex.util.ByteInput, char[]):java.lang.String");
    }

    private static long countBytes(String s, boolean shortLength) throws UTFDataFormatException {
        long result = 0;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch != 0 && ch <= 127) {
                result++;
            } else if (ch <= 2047) {
                result += 2;
            } else {
                result += 3;
            }
            if (shortLength && result > 65535) {
                throw new UTFDataFormatException("String more than 65535 UTF bytes long");
            }
        }
        return result;
    }

    public static void encode(byte[] dst, int offset, String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch != 0 && ch <= 127) {
                int i2 = offset;
                offset++;
                dst[i2] = (byte) ch;
            } else if (ch <= 2047) {
                int i3 = offset;
                int offset2 = offset + 1;
                dst[i3] = (byte) (192 | (31 & (ch >> 6)));
                offset = offset2 + 1;
                dst[offset2] = (byte) (128 | ('?' & ch));
            } else {
                int i4 = offset;
                int offset3 = offset + 1;
                dst[i4] = (byte) (224 | (15 & (ch >> '\f')));
                int offset4 = offset3 + 1;
                dst[offset3] = (byte) (128 | (63 & (ch >> 6)));
                offset = offset4 + 1;
                dst[offset4] = (byte) (128 | ('?' & ch));
            }
        }
    }

    public static byte[] encode(String s) throws UTFDataFormatException {
        int utfCount = (int) countBytes(s, true);
        byte[] result = new byte[utfCount];
        encode(result, 0, s);
        return result;
    }
}