package com.android.internal.util;

import gov.nist.core.Separators;

/* loaded from: HexDump.class */
public class HexDump {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String dumpHexString(byte[] array) {
        return dumpHexString(array, 0, array.length);
    }

    public static String dumpHexString(byte[] array, int offset, int length) {
        StringBuilder result = new StringBuilder();
        byte[] line = new byte[16];
        int lineIndex = 0;
        result.append("\n0x");
        result.append(toHexString(offset));
        for (int i = offset; i < offset + length; i++) {
            if (lineIndex == 16) {
                result.append(Separators.SP);
                for (int j = 0; j < 16; j++) {
                    if (line[j] > 32 && line[j] < 126) {
                        result.append(new String(line, j, 1));
                    } else {
                        result.append(Separators.DOT);
                    }
                }
                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }
            byte b = array[i];
            result.append(Separators.SP);
            result.append(HEX_DIGITS[(b >>> 4) & 15]);
            result.append(HEX_DIGITS[b & 15]);
            int i2 = lineIndex;
            lineIndex++;
            line[i2] = b;
        }
        if (lineIndex != 16) {
            int count = (16 - lineIndex) * 3;
            int count2 = count + 1;
            for (int i3 = 0; i3 < count2; i3++) {
                result.append(Separators.SP);
            }
            for (int i4 = 0; i4 < lineIndex; i4++) {
                if (line[i4] > 32 && line[i4] < 126) {
                    result.append(new String(line, i4, 1));
                } else {
                    result.append(Separators.DOT);
                }
            }
        }
        return result.toString();
    }

    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length);
    }

    public static String toHexString(byte[] array, int offset, int length) {
        char[] buf = new char[length * 2];
        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            int i2 = bufIndex;
            int bufIndex2 = bufIndex + 1;
            buf[i2] = HEX_DIGITS[(b >>> 4) & 15];
            bufIndex = bufIndex2 + 1;
            buf[bufIndex2] = HEX_DIGITS[b & 15];
        }
        return new String(buf);
    }

    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b) {
        byte[] array = {b};
        return array;
    }

    public static byte[] toByteArray(int i) {
        byte[] array = {(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
        return array;
    }

    private static int toByte(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                    throw new RuntimeException("Invalid hex char '" + c + Separators.QUOTE);
                }
                return (c - 'a') + 10;
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString.charAt(i + 1)));
        }
        return buffer;
    }
}