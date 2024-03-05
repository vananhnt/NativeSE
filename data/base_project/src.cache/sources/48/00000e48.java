package android.speech.srec;

import android.view.WindowManager;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: UlawEncoderInputStream.class */
public final class UlawEncoderInputStream extends InputStream {
    private static final String TAG = "UlawEncoderInputStream";
    private static final int MAX_ULAW = 8192;
    private static final int SCALE_BITS = 16;
    private InputStream mIn;
    private int mMax;
    private final byte[] mBuf = new byte[1024];
    private int mBufCount = 0;
    private final byte[] mOneByte = new byte[1];

    public static void encode(byte[] pcmBuf, int pcmOffset, byte[] ulawBuf, int ulawOffset, int length, int max) {
        int i;
        if (max <= 0) {
            max = 8192;
        }
        int coef = 536870912 / max;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = pcmOffset;
            int pcmOffset2 = pcmOffset + 1;
            pcmOffset = pcmOffset2 + 1;
            int pcm = (((255 & pcmBuf[i3]) + (pcmBuf[pcmOffset2] << 8)) * coef) >> 16;
            if (pcm >= 0) {
                i = pcm <= 0 ? 255 : pcm <= 30 ? 240 + ((30 - pcm) >> 1) : pcm <= 94 ? 224 + ((94 - pcm) >> 2) : pcm <= 222 ? 208 + ((222 - pcm) >> 3) : pcm <= 478 ? 192 + ((478 - pcm) >> 4) : pcm <= 990 ? 176 + ((990 - pcm) >> 5) : pcm <= 2014 ? 160 + ((WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL - pcm) >> 6) : pcm <= 4062 ? 144 + ((4062 - pcm) >> 7) : pcm <= 8158 ? 128 + ((8158 - pcm) >> 8) : 128;
            } else {
                i = -1 <= pcm ? 127 : -31 <= pcm ? 112 + ((pcm - (-31)) >> 1) : -95 <= pcm ? 96 + ((pcm - (-95)) >> 2) : -223 <= pcm ? 80 + ((pcm - (-223)) >> 3) : -479 <= pcm ? 64 + ((pcm - (-479)) >> 4) : -991 <= pcm ? 48 + ((pcm - (-991)) >> 5) : -2015 <= pcm ? 32 + ((pcm - (-2015)) >> 6) : -4063 <= pcm ? 16 + ((pcm - (-4063)) >> 7) : -8159 <= pcm ? 0 + ((pcm - (-8159)) >> 8) : 0;
            }
            int ulaw = i;
            int i4 = ulawOffset;
            ulawOffset++;
            ulawBuf[i4] = (byte) ulaw;
        }
    }

    public static int maxAbsPcm(byte[] pcmBuf, int offset, int length) {
        int max = 0;
        for (int i = 0; i < length; i++) {
            int i2 = offset;
            int offset2 = offset + 1;
            offset = offset2 + 1;
            int pcm = (255 & pcmBuf[i2]) + (pcmBuf[offset2] << 8);
            if (pcm < 0) {
                pcm = -pcm;
            }
            if (pcm > max) {
                max = pcm;
            }
        }
        return max;
    }

    public UlawEncoderInputStream(InputStream in, int max) {
        this.mMax = 0;
        this.mIn = in;
        this.mMax = max;
    }

    @Override // java.io.InputStream
    public int read(byte[] buf, int offset, int length) throws IOException {
        if (this.mIn == null) {
            throw new IllegalStateException("not open");
        }
        while (this.mBufCount < 2) {
            int n = this.mIn.read(this.mBuf, this.mBufCount, Math.min(length * 2, this.mBuf.length - this.mBufCount));
            if (n == -1) {
                return -1;
            }
            this.mBufCount += n;
        }
        int n2 = Math.min(this.mBufCount / 2, length);
        encode(this.mBuf, 0, buf, offset, n2, this.mMax);
        this.mBufCount -= n2 * 2;
        for (int i = 0; i < this.mBufCount; i++) {
            this.mBuf[i] = this.mBuf[i + (n2 * 2)];
        }
        return n2;
    }

    @Override // java.io.InputStream
    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int n = read(this.mOneByte, 0, 1);
        if (n == -1) {
            return -1;
        }
        return 255 & this.mOneByte[0];
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        if (this.mIn != null) {
            InputStream in = this.mIn;
            this.mIn = null;
            in.close();
        }
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return (this.mIn.available() + this.mBufCount) / 2;
    }
}