package android.media;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: AmrInputStream.class */
public final class AmrInputStream extends InputStream {
    private static final String TAG = "AmrInputStream";
    private static final int SAMPLES_PER_FRAME = 160;
    private InputStream mInputStream;
    private final byte[] mBuf = new byte[320];
    private int mBufIn = 0;
    private int mBufOut = 0;
    private byte[] mOneByte = new byte[1];
    private int mGae = GsmAmrEncoderNew();

    private static native int GsmAmrEncoderNew();

    private static native void GsmAmrEncoderInitialize(int i);

    private static native int GsmAmrEncoderEncode(int i, byte[] bArr, int i2, byte[] bArr2, int i3) throws IOException;

    private static native void GsmAmrEncoderCleanup(int i);

    private static native void GsmAmrEncoderDelete(int i);

    static {
        System.loadLibrary("media_jni");
    }

    public AmrInputStream(InputStream inputStream) {
        this.mInputStream = inputStream;
        GsmAmrEncoderInitialize(this.mGae);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int rtn = read(this.mOneByte, 0, 1);
        if (rtn == 1) {
            return 255 & this.mOneByte[0];
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int offset, int length) throws IOException {
        if (this.mGae == 0) {
            throw new IllegalStateException("not open");
        }
        if (this.mBufOut >= this.mBufIn) {
            this.mBufOut = 0;
            this.mBufIn = 0;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < 320) {
                    int n = this.mInputStream.read(this.mBuf, i2, 320 - i2);
                    if (n == -1) {
                        return -1;
                    }
                    i = i2 + n;
                } else {
                    this.mBufIn = GsmAmrEncoderEncode(this.mGae, this.mBuf, 0, this.mBuf, 0);
                    break;
                }
            }
        }
        if (length > this.mBufIn - this.mBufOut) {
            length = this.mBufIn - this.mBufOut;
        }
        System.arraycopy(this.mBuf, this.mBufOut, b, offset, length);
        this.mBufOut += length;
        return length;
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        try {
            if (this.mInputStream != null) {
                this.mInputStream.close();
            }
            this.mInputStream = null;
            try {
                if (this.mGae != 0) {
                    GsmAmrEncoderCleanup(this.mGae);
                }
                try {
                    if (this.mGae != 0) {
                        GsmAmrEncoderDelete(this.mGae);
                    }
                    this.mGae = 0;
                } finally {
                }
            } catch (Throwable th) {
                try {
                    if (this.mGae != 0) {
                        GsmAmrEncoderDelete(this.mGae);
                    }
                    this.mGae = 0;
                    throw th;
                } finally {
                }
            }
        } catch (Throwable th2) {
            this.mInputStream = null;
            try {
                if (this.mGae != 0) {
                    GsmAmrEncoderCleanup(this.mGae);
                }
                try {
                    if (this.mGae != 0) {
                        GsmAmrEncoderDelete(this.mGae);
                    }
                    this.mGae = 0;
                    throw th2;
                } finally {
                    this.mGae = 0;
                }
            } catch (Throwable th3) {
                try {
                    if (this.mGae != 0) {
                        GsmAmrEncoderDelete(this.mGae);
                    }
                    this.mGae = 0;
                    throw th3;
                } finally {
                    this.mGae = 0;
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        if (this.mGae != 0) {
            close();
            throw new IllegalStateException("someone forgot to close AmrInputStream");
        }
    }
}