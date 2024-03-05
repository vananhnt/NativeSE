package java.nio.charset;

/* loaded from: Charsets.class */
public final class Charsets {
    public static native byte[] toAsciiBytes(char[] cArr, int i, int i2);

    public static native byte[] toIsoLatin1Bytes(char[] cArr, int i, int i2);

    public static native byte[] toUtf8Bytes(char[] cArr, int i, int i2);

    public static native void asciiBytesToChars(byte[] bArr, int i, int i2, char[] cArr);

    public static native void isoLatin1BytesToChars(byte[] bArr, int i, int i2, char[] cArr);

    public static byte[] toBigEndianUtf16Bytes(char[] chars, int offset, int length) {
        byte[] result = new byte[length * 2];
        int end = offset + length;
        int resultIndex = 0;
        for (int i = offset; i < end; i++) {
            char ch = chars[i];
            int i2 = resultIndex;
            int resultIndex2 = resultIndex + 1;
            result[i2] = (byte) (ch >> '\b');
            resultIndex = resultIndex2 + 1;
            result[resultIndex2] = (byte) ch;
        }
        return result;
    }

    private Charsets() {
    }
}