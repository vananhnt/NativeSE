package java.nio.charset;

import java.io.UTFDataFormatException;
import java.nio.ByteOrder;
import libcore.io.Memory;

/* loaded from: ModifiedUtf8.class */
public class ModifiedUtf8 {
    public static String decode(byte[] in, char[] out, int offset, int utfSize) throws UTFDataFormatException {
        int count = 0;
        int s = 0;
        while (count < utfSize) {
            int i = count;
            count++;
            char c = (char) in[offset + i];
            out[s] = c;
            if (c < 128) {
                s++;
            } else {
                char c2 = out[s];
                if ((c2 & 224) == 192) {
                    if (count >= utfSize) {
                        throw new UTFDataFormatException("bad second byte at " + count);
                    }
                    count++;
                    byte b = in[offset + count];
                    if ((b & 192) != 128) {
                        throw new UTFDataFormatException("bad second byte at " + (count - 1));
                    }
                    int i2 = s;
                    s++;
                    out[i2] = (char) (((c2 & 31) << 6) | (b & 63));
                } else if ((c2 & 240) == 224) {
                    if (count + 1 >= utfSize) {
                        throw new UTFDataFormatException("bad third byte at " + (count + 1));
                    }
                    int count2 = count + 1;
                    byte b2 = in[offset + count];
                    count = count2 + 1;
                    byte b3 = in[offset + count2];
                    if ((b2 & 192) != 128 || (b3 & 192) != 128) {
                        throw new UTFDataFormatException("bad second or third byte at " + (count - 2));
                    }
                    int i3 = s;
                    s++;
                    out[i3] = (char) (((c2 & 15) << 12) | ((b2 & 63) << 6) | (b3 & 63));
                } else {
                    throw new UTFDataFormatException("bad byte at " + (count - 1));
                }
            }
        }
        return new String(out, 0, s);
    }

    public static long countBytes(String s, boolean shortLength) throws UTFDataFormatException {
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
        byte[] result = new byte[2 + utfCount];
        Memory.pokeShort(result, 0, (short) utfCount, ByteOrder.BIG_ENDIAN);
        encode(result, 2, s);
        return result;
    }

    private ModifiedUtf8() {
    }
}