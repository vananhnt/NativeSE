package org.apache.commons.codec.binary;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/* loaded from: Hex.class */
public class Hex implements BinaryEncoder, BinaryDecoder {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] decodeHex(char[] data) throws DecoderException {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new DecoderException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        int i = 0;
        int j = 0;
        while (j < len) {
            int f = toDigit(data[j], j) << 4;
            int j2 = j + 1;
            int f2 = f | toDigit(data[j2], j2);
            j = j2 + 1;
            out[i] = (byte) (f2 & 255);
            i++;
        }
        return out;
    }

    protected static int toDigit(char ch, int index) throws DecoderException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal charcter " + ch + " at index " + index);
        }
        return digit;
    }

    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int i2 = j;
            int j2 = j + 1;
            out[i2] = DIGITS[(240 & data[i]) >>> 4];
            j = j2 + 1;
            out[j2] = DIGITS[15 & data[i]];
        }
        return out;
    }

    @Override // org.apache.commons.codec.BinaryDecoder
    public byte[] decode(byte[] array) throws DecoderException {
        return decodeHex(new String(array).toCharArray());
    }

    @Override // org.apache.commons.codec.Decoder
    public Object decode(Object object) throws DecoderException {
        try {
            char[] charArray = object instanceof String ? ((String) object).toCharArray() : (char[]) object;
            return decodeHex(charArray);
        } catch (ClassCastException e) {
            throw new DecoderException(e.getMessage());
        }
    }

    @Override // org.apache.commons.codec.BinaryEncoder
    public byte[] encode(byte[] array) {
        return new String(encodeHex(array)).getBytes();
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object object) throws EncoderException {
        try {
            byte[] byteArray = object instanceof String ? ((String) object).getBytes() : (byte[]) object;
            return encodeHex(byteArray);
        } catch (ClassCastException e) {
            throw new EncoderException(e.getMessage());
        }
    }
}