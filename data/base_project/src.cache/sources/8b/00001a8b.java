package com.android.internal.telephony;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.telephony.Rlog;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.io.UnsupportedEncodingException;

/* loaded from: IccUtils.class */
public class IccUtils {
    static final String LOG_TAG = "IccUtils";

    public static String bcdToString(byte[] data, int offset, int length) {
        int v;
        StringBuilder ret = new StringBuilder(length * 2);
        for (int i = offset; i < offset + length && (v = data[i] & 15) <= 9; i++) {
            ret.append((char) (48 + v));
            int v2 = (data[i] >> 4) & 15;
            if (v2 != 15) {
                if (v2 > 9) {
                    break;
                }
                ret.append((char) (48 + v2));
            }
        }
        return ret.toString();
    }

    public static String cdmaBcdToString(byte[] data, int offset, int length) {
        StringBuilder ret = new StringBuilder(length);
        int count = 0;
        int i = offset;
        while (count < length) {
            int v = data[i] & 15;
            if (v > 9) {
                v = 0;
            }
            ret.append((char) (48 + v));
            int count2 = count + 1;
            if (count2 == length) {
                break;
            }
            int v2 = (data[i] >> 4) & 15;
            if (v2 > 9) {
                v2 = 0;
            }
            ret.append((char) (48 + v2));
            count = count2 + 1;
            i++;
        }
        return ret.toString();
    }

    public static int gsmBcdByteToInt(byte b) {
        int ret = 0;
        if ((b & 240) <= 144) {
            ret = (b >> 4) & 15;
        }
        if ((b & 15) <= 9) {
            ret += (b & 15) * 10;
        }
        return ret;
    }

    public static int cdmaBcdByteToInt(byte b) {
        int ret = 0;
        if ((b & 240) <= 144) {
            ret = ((b >> 4) & 15) * 10;
        }
        if ((b & 15) <= 9) {
            ret += b & 15;
        }
        return ret;
    }

    public static String adnStringFieldToString(byte[] data, int offset, int length) {
        if (length == 0) {
            return "";
        }
        if (length >= 1 && data[offset] == Byte.MIN_VALUE) {
            int ucslen = (length - 1) / 2;
            String ret = null;
            try {
                ret = new String(data, offset + 1, ucslen * 2, "utf-16be");
            } catch (UnsupportedEncodingException ex) {
                Rlog.e(LOG_TAG, "implausible UnsupportedEncodingException", ex);
            }
            if (ret != null) {
                int ucslen2 = ret.length();
                while (ucslen2 > 0 && ret.charAt(ucslen2 - 1) == 65535) {
                    ucslen2--;
                }
                return ret.substring(0, ucslen2);
            }
        }
        boolean isucs2 = false;
        char base = 0;
        int len = 0;
        if (length >= 3 && data[offset] == -127) {
            len = data[offset + 1] & 255;
            if (len > length - 3) {
                len = length - 3;
            }
            base = (char) ((data[offset + 2] & 255) << 7);
            offset += 3;
            isucs2 = true;
        } else if (length >= 4 && data[offset] == -126) {
            len = data[offset + 1] & 255;
            if (len > length - 4) {
                len = length - 4;
            }
            base = (char) (((data[offset + 2] & 255) << 8) | (data[offset + 3] & 255));
            offset += 4;
            isucs2 = true;
        }
        if (isucs2) {
            StringBuilder ret2 = new StringBuilder();
            while (len > 0) {
                if (data[offset] < 0) {
                    ret2.append((char) (base + (data[offset] & Byte.MAX_VALUE)));
                    offset++;
                    len--;
                }
                int count = 0;
                while (count < len && data[offset + count] >= 0) {
                    count++;
                }
                ret2.append(GsmAlphabet.gsm8BitUnpackedToString(data, offset, count));
                offset += count;
                len -= count;
            }
            return ret2.toString();
        }
        Resources resource = Resources.getSystem();
        String defaultCharset = "";
        try {
            defaultCharset = resource.getString(R.string.gsm_alphabet_default_charset);
        } catch (Resources.NotFoundException e) {
        }
        return GsmAlphabet.gsm8BitUnpackedToString(data, offset, length, defaultCharset.trim());
    }

    static int hexCharToInt(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                    throw new RuntimeException("invalid hex char '" + c + Separators.QUOTE);
                }
                return (c - 'a') + 10;
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null) {
            return null;
        }
        int sz = s.length();
        byte[] ret = new byte[sz / 2];
        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4) | hexCharToInt(s.charAt(i + 1)));
        }
        return ret;
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int b = 15 & (bytes[i] >> 4);
            ret.append("0123456789abcdef".charAt(b));
            int b2 = 15 & bytes[i];
            ret.append("0123456789abcdef".charAt(b2));
        }
        return ret.toString();
    }

    public static String networkNameToString(byte[] data, int offset, int length) {
        String ret;
        if ((data[offset] & 128) != 128 || length < 1) {
            return "";
        }
        switch ((data[offset] >>> 4) & 7) {
            case 0:
                int unusedBits = data[offset] & 7;
                int countSeptets = (((length - 1) * 8) - unusedBits) / 7;
                ret = GsmAlphabet.gsm7BitPackedToString(data, offset + 1, countSeptets);
                break;
            case 1:
                try {
                    ret = new String(data, offset + 1, length - 1, "utf-16");
                    break;
                } catch (UnsupportedEncodingException ex) {
                    ret = "";
                    Rlog.e(LOG_TAG, "implausible UnsupportedEncodingException", ex);
                    break;
                }
            default:
                ret = "";
                break;
        }
        if ((data[offset] & 64) != 0) {
        }
        return ret;
    }

    public static Bitmap parseToBnW(byte[] data, int length) {
        int valueIndex = 0 + 1;
        int width = data[0] & 255;
        int valueIndex2 = valueIndex + 1;
        int height = data[valueIndex] & 255;
        int numOfPixels = width * height;
        int[] pixels = new int[numOfPixels];
        int pixelIndex = 0;
        byte bitIndex = 7;
        byte currentByte = 0;
        while (pixelIndex < numOfPixels) {
            if (pixelIndex % 8 == 0) {
                int i = valueIndex2;
                valueIndex2++;
                currentByte = data[i];
                bitIndex = 7;
            }
            int i2 = pixelIndex;
            pixelIndex++;
            pixels[i2] = bitToRGB((currentByte >> bitIndex) & 1);
            bitIndex--;
        }
        if (pixelIndex != numOfPixels) {
            Rlog.e(LOG_TAG, "parse end and size error");
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    private static int bitToRGB(int bit) {
        if (bit == 1) {
            return -1;
        }
        return -16777216;
    }

    public static Bitmap parseToRGB(byte[] data, int length, boolean transparency) {
        int[] resultArray;
        int valueIndex = 0 + 1;
        int width = data[0] & 255;
        int valueIndex2 = valueIndex + 1;
        int height = data[valueIndex] & 255;
        int valueIndex3 = valueIndex2 + 1;
        int bits = data[valueIndex2] & 255;
        int valueIndex4 = valueIndex3 + 1;
        int colorNumber = data[valueIndex3] & 255;
        int valueIndex5 = valueIndex4 + 1;
        int valueIndex6 = valueIndex5 + 1;
        int clutOffset = ((data[valueIndex4] & 255) << 8) | (data[valueIndex5] & 255);
        int[] colorIndexArray = getCLUT(data, clutOffset, colorNumber);
        if (true == transparency) {
            colorIndexArray[colorNumber - 1] = 0;
        }
        if (0 == 8 % bits) {
            resultArray = mapTo2OrderBitColor(data, valueIndex6, width * height, colorIndexArray, bits);
        } else {
            resultArray = mapToNon2OrderBitColor(data, valueIndex6, width * height, colorIndexArray, bits);
        }
        return Bitmap.createBitmap(resultArray, width, height, Bitmap.Config.RGB_565);
    }

    private static int[] mapTo2OrderBitColor(byte[] data, int valueIndex, int length, int[] colorArray, int bits) {
        if (0 != 8 % bits) {
            Rlog.e(LOG_TAG, "not event number of color");
            return mapToNon2OrderBitColor(data, valueIndex, length, colorArray, bits);
        }
        int mask = 1;
        switch (bits) {
            case 1:
                mask = 1;
                break;
            case 2:
                mask = 3;
                break;
            case 4:
                mask = 15;
                break;
            case 8:
                mask = 255;
                break;
        }
        int[] resultArray = new int[length];
        int resultIndex = 0;
        int run = 8 / bits;
        while (resultIndex < length) {
            int i = valueIndex;
            valueIndex++;
            byte tempByte = data[i];
            for (int runIndex = 0; runIndex < run; runIndex++) {
                int offset = (run - runIndex) - 1;
                int i2 = resultIndex;
                resultIndex++;
                resultArray[i2] = colorArray[(tempByte >> (offset * bits)) & mask];
            }
        }
        return resultArray;
    }

    private static int[] mapToNon2OrderBitColor(byte[] data, int valueIndex, int length, int[] colorArray, int bits) {
        if (0 == 8 % bits) {
            Rlog.e(LOG_TAG, "not odd number of color");
            return mapTo2OrderBitColor(data, valueIndex, length, colorArray, bits);
        }
        int[] resultArray = new int[length];
        return resultArray;
    }

    private static int[] getCLUT(byte[] rawData, int offset, int number) {
        if (null == rawData) {
            return null;
        }
        int[] result = new int[number];
        int endIndex = offset + (number * 3);
        int valueIndex = offset;
        int colorIndex = 0;
        do {
            int i = colorIndex;
            colorIndex++;
            int i2 = valueIndex;
            int valueIndex2 = valueIndex + 1;
            int valueIndex3 = valueIndex2 + 1;
            valueIndex = valueIndex3 + 1;
            result[i] = (-16777216) | ((rawData[i2] & 255) << 16) | ((rawData[valueIndex2] & 255) << 8) | (rawData[valueIndex3] & 255);
        } while (valueIndex < endIndex);
        return result;
    }
}