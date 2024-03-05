package libcore.io;

import java.nio.charset.StandardCharsets;
import libcore.util.EmptyArray;

/* loaded from: Base64.class */
public final class Base64 {
    private static final byte[] map = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};

    private Base64() {
    }

    public static byte[] decode(byte[] in) {
        return decode(in, in.length);
    }

    public static byte[] decode(byte[] in, int len) {
        int bits;
        int length = (len / 4) * 3;
        if (length == 0) {
            return EmptyArray.BYTE;
        }
        byte[] out = new byte[length];
        int pad = 0;
        while (true) {
            byte chr = in[len - 1];
            if (chr != 10 && chr != 13 && chr != 32 && chr != 9) {
                if (chr != 61) {
                    break;
                }
                pad++;
            }
            len--;
        }
        int outIndex = 0;
        int inIndex = 0;
        int quantum = 0;
        for (int i = 0; i < len; i++) {
            byte chr2 = in[i];
            if (chr2 != 10 && chr2 != 13 && chr2 != 32 && chr2 != 9) {
                if (chr2 >= 65 && chr2 <= 90) {
                    bits = chr2 - 65;
                } else if (chr2 >= 97 && chr2 <= 122) {
                    bits = chr2 - 71;
                } else if (chr2 >= 48 && chr2 <= 57) {
                    bits = chr2 + 4;
                } else if (chr2 == 43) {
                    bits = 62;
                } else if (chr2 == 47) {
                    bits = 63;
                } else {
                    return null;
                }
                quantum = (quantum << 6) | ((byte) bits);
                if (inIndex % 4 == 3) {
                    int i2 = outIndex;
                    int outIndex2 = outIndex + 1;
                    out[i2] = (byte) (quantum >> 16);
                    int outIndex3 = outIndex2 + 1;
                    out[outIndex2] = (byte) (quantum >> 8);
                    outIndex = outIndex3 + 1;
                    out[outIndex3] = (byte) quantum;
                }
                inIndex++;
            }
        }
        if (pad > 0) {
            int quantum2 = quantum << (6 * pad);
            int i3 = outIndex;
            outIndex++;
            out[i3] = (byte) (quantum2 >> 16);
            if (pad == 1) {
                outIndex++;
                out[outIndex] = (byte) (quantum2 >> 8);
            }
        }
        byte[] result = new byte[outIndex];
        System.arraycopy(out, 0, result, 0, outIndex);
        return result;
    }

    public static String encode(byte[] in) {
        int length = ((in.length + 2) * 4) / 3;
        byte[] out = new byte[length];
        int index = 0;
        int end = in.length - (in.length % 3);
        for (int i = 0; i < end; i += 3) {
            int i2 = index;
            int index2 = index + 1;
            out[i2] = map[(in[i] & 255) >> 2];
            int index3 = index2 + 1;
            out[index2] = map[((in[i] & 3) << 4) | ((in[i + 1] & 255) >> 4)];
            int index4 = index3 + 1;
            out[index3] = map[((in[i + 1] & 15) << 2) | ((in[i + 2] & 255) >> 6)];
            index = index4 + 1;
            out[index4] = map[in[i + 2] & 63];
        }
        switch (in.length % 3) {
            case 1:
                int i3 = index;
                int index5 = index + 1;
                out[i3] = map[(in[end] & 255) >> 2];
                int index6 = index5 + 1;
                out[index5] = map[(in[end] & 3) << 4];
                int index7 = index6 + 1;
                out[index6] = 61;
                index = index7 + 1;
                out[index7] = 61;
                break;
            case 2:
                int i4 = index;
                int index8 = index + 1;
                out[i4] = map[(in[end] & 255) >> 2];
                int index9 = index8 + 1;
                out[index8] = map[((in[end] & 3) << 4) | ((in[end + 1] & 255) >> 4)];
                int index10 = index9 + 1;
                out[index9] = map[(in[end + 1] & 15) << 2];
                index = index10 + 1;
                out[index10] = 61;
                break;
        }
        return new String(out, 0, index, StandardCharsets.US_ASCII);
    }
}