package android.util;

import java.io.UnsupportedEncodingException;

/* loaded from: Base64.class */
public class Base64 {
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int CRLF = 4;
    public static final int URL_SAFE = 8;
    public static final int NO_CLOSE = 16;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Base64.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Base64$Coder.class */
    public static abstract class Coder {
        public byte[] output;
        public int op;

        public abstract boolean process(byte[] bArr, int i, int i2, boolean z);

        public abstract int maxOutputSize(int i);

        Coder() {
        }
    }

    public static byte[] decode(String str, int flags) {
        return decode(str.getBytes(), flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        return decode(input, 0, input.length, flags);
    }

    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        Decoder decoder = new Decoder(flags, new byte[(len * 3) / 4]);
        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        }
        if (decoder.op == decoder.output.length) {
            return decoder.output;
        }
        byte[] temp = new byte[decoder.op];
        System.arraycopy(decoder.output, 0, temp, 0, decoder.op);
        return temp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Base64$Decoder.class */
    public static class Decoder extends Coder {
        private static final int[] DECODE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int[] DECODE_WEBSAFE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int SKIP = -1;
        private static final int EQUALS = -2;
        private int state;
        private int value;
        private final int[] alphabet;

        public Decoder(int flags, byte[] output) {
            this.output = output;
            this.alphabet = (flags & 8) == 0 ? DECODE : DECODE_WEBSAFE;
            this.state = 0;
            this.value = 0;
        }

        @Override // android.util.Base64.Coder
        public int maxOutputSize(int len) {
            return ((len * 3) / 4) + 10;
        }

        /* JADX WARN: Removed duplicated region for block: B:69:0x020d  */
        /* JADX WARN: Removed duplicated region for block: B:71:0x0221  */
        /* JADX WARN: Removed duplicated region for block: B:89:0x0208 A[SYNTHETIC] */
        @Override // android.util.Base64.Coder
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean process(byte[] r7, int r8, int r9, boolean r10) {
            /*
                Method dump skipped, instructions count: 663
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.util.Base64.Decoder.process(byte[], int, int, boolean):boolean");
        }
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static String encodeToString(byte[] input, int offset, int len, int flags) {
        try {
            return new String(encode(input, offset, len, flags), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, null);
        int output_len = (len / 3) * 4;
        if (encoder.do_padding) {
            if (len % 3 > 0) {
                output_len += 4;
            }
        } else {
            switch (len % 3) {
                case 1:
                    output_len += 2;
                    break;
                case 2:
                    output_len += 3;
                    break;
            }
        }
        if (encoder.do_newline && len > 0) {
            output_len += (((len - 1) / 57) + 1) * (encoder.do_cr ? 2 : 1);
        }
        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);
        if ($assertionsDisabled || encoder.op == output_len) {
            return encoder.output;
        }
        throw new AssertionError();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Base64$Encoder.class */
    public static class Encoder extends Coder {
        public static final int LINE_GROUPS = 19;
        private static final byte[] ENCODE;
        private static final byte[] ENCODE_WEBSAFE;
        private final byte[] tail;
        int tailLen;
        private int count;
        public final boolean do_padding;
        public final boolean do_newline;
        public final boolean do_cr;
        private final byte[] alphabet;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !Base64.class.desiredAssertionStatus();
            ENCODE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
            ENCODE_WEBSAFE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
        }

        public Encoder(int flags, byte[] output) {
            this.output = output;
            this.do_padding = (flags & 1) == 0;
            this.do_newline = (flags & 2) == 0;
            this.do_cr = (flags & 4) != 0;
            this.alphabet = (flags & 8) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[2];
            this.tailLen = 0;
            this.count = this.do_newline ? 19 : -1;
        }

        @Override // android.util.Base64.Coder
        public int maxOutputSize(int len) {
            return ((len * 8) / 5) + 10;
        }

        @Override // android.util.Base64.Coder
        public boolean process(byte[] input, int offset, int len, boolean finish) {
            byte b;
            byte b2;
            byte b3;
            byte[] alphabet = this.alphabet;
            byte[] output = this.output;
            int op = 0;
            int count = this.count;
            int p = offset;
            int len2 = len + offset;
            int v = -1;
            switch (this.tailLen) {
                case 1:
                    if (p + 2 <= len2) {
                        int p2 = p + 1;
                        p = p2 + 1;
                        v = ((this.tail[0] & 255) << 16) | ((input[p] & 255) << 8) | (input[p2] & 255);
                        this.tailLen = 0;
                        break;
                    }
                    break;
                case 2:
                    if (p + 1 <= len2) {
                        p++;
                        v = ((this.tail[0] & 255) << 16) | ((this.tail[1] & 255) << 8) | (input[p] & 255);
                        this.tailLen = 0;
                        break;
                    }
                    break;
            }
            if (v != -1) {
                int op2 = 0 + 1;
                output[0] = alphabet[(v >> 18) & 63];
                int op3 = op2 + 1;
                output[op2] = alphabet[(v >> 12) & 63];
                int op4 = op3 + 1;
                output[op3] = alphabet[(v >> 6) & 63];
                op = op4 + 1;
                output[op4] = alphabet[v & 63];
                count--;
                if (count == 0) {
                    if (this.do_cr) {
                        op++;
                        output[op] = 13;
                    }
                    int i = op;
                    op++;
                    output[i] = 10;
                    count = 19;
                }
            }
            while (p + 3 <= len2) {
                int v2 = ((input[p] & 255) << 16) | ((input[p + 1] & 255) << 8) | (input[p + 2] & 255);
                output[op] = alphabet[(v2 >> 18) & 63];
                output[op + 1] = alphabet[(v2 >> 12) & 63];
                output[op + 2] = alphabet[(v2 >> 6) & 63];
                output[op + 3] = alphabet[v2 & 63];
                p += 3;
                op += 4;
                count--;
                if (count == 0) {
                    if (this.do_cr) {
                        op++;
                        output[op] = 13;
                    }
                    int i2 = op;
                    op++;
                    output[i2] = 10;
                    count = 19;
                }
            }
            if (finish) {
                if (p - this.tailLen == len2 - 1) {
                    int t = 0;
                    if (this.tailLen > 0) {
                        t = 0 + 1;
                        b3 = this.tail[0];
                    } else {
                        int i3 = p;
                        p++;
                        b3 = input[i3];
                    }
                    int v3 = (b3 & 255) << 4;
                    this.tailLen -= t;
                    int i4 = op;
                    int op5 = op + 1;
                    output[i4] = alphabet[(v3 >> 6) & 63];
                    op = op5 + 1;
                    output[op5] = alphabet[v3 & 63];
                    if (this.do_padding) {
                        int op6 = op + 1;
                        output[op] = 61;
                        op = op6 + 1;
                        output[op6] = 61;
                    }
                    if (this.do_newline) {
                        if (this.do_cr) {
                            int i5 = op;
                            op++;
                            output[i5] = 13;
                        }
                        int i6 = op;
                        op++;
                        output[i6] = 10;
                    }
                } else if (p - this.tailLen == len2 - 2) {
                    int t2 = 0;
                    if (this.tailLen > 1) {
                        t2 = 0 + 1;
                        b = this.tail[0];
                    } else {
                        int i7 = p;
                        p++;
                        b = input[i7];
                    }
                    int i8 = (b & 255) << 10;
                    if (this.tailLen > 0) {
                        int i9 = t2;
                        t2++;
                        b2 = this.tail[i9];
                    } else {
                        int i10 = p;
                        p++;
                        b2 = input[i10];
                    }
                    int v4 = i8 | ((b2 & 255) << 2);
                    this.tailLen -= t2;
                    int i11 = op;
                    int op7 = op + 1;
                    output[i11] = alphabet[(v4 >> 12) & 63];
                    int op8 = op7 + 1;
                    output[op7] = alphabet[(v4 >> 6) & 63];
                    op = op8 + 1;
                    output[op8] = alphabet[v4 & 63];
                    if (this.do_padding) {
                        op++;
                        output[op] = 61;
                    }
                    if (this.do_newline) {
                        if (this.do_cr) {
                            int i12 = op;
                            op++;
                            output[i12] = 13;
                        }
                        int i13 = op;
                        op++;
                        output[i13] = 10;
                    }
                } else if (this.do_newline && op > 0 && count != 19) {
                    if (this.do_cr) {
                        int i14 = op;
                        op++;
                        output[i14] = 13;
                    }
                    int i15 = op;
                    op++;
                    output[i15] = 10;
                }
                if (!$assertionsDisabled && this.tailLen != 0) {
                    throw new AssertionError();
                }
                if (!$assertionsDisabled && p != len2) {
                    throw new AssertionError();
                }
            } else if (p == len2 - 1) {
                byte[] bArr = this.tail;
                int i16 = this.tailLen;
                this.tailLen = i16 + 1;
                bArr[i16] = input[p];
            } else if (p == len2 - 2) {
                byte[] bArr2 = this.tail;
                int i17 = this.tailLen;
                this.tailLen = i17 + 1;
                bArr2[i17] = input[p];
                byte[] bArr3 = this.tail;
                int i18 = this.tailLen;
                this.tailLen = i18 + 1;
                bArr3[i18] = input[p + 1];
            }
            this.op = op;
            this.count = count;
            return true;
        }
    }

    private Base64() {
    }
}