package android.speech.srec;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: MicrophoneInputStream.class */
public final class MicrophoneInputStream extends InputStream {
    private static final String TAG = "MicrophoneInputStream";
    private int mAudioRecord;
    private byte[] mOneByte = new byte[1];

    private static native int AudioRecordNew(int i, int i2);

    private static native int AudioRecordStart(int i);

    private static native int AudioRecordRead(int i, byte[] bArr, int i2, int i3) throws IOException;

    private static native void AudioRecordStop(int i) throws IOException;

    private static native void AudioRecordDelete(int i) throws IOException;

    static {
        System.loadLibrary("srec_jni");
    }

    public MicrophoneInputStream(int sampleRate, int fifoDepth) throws IOException {
        this.mAudioRecord = 0;
        this.mAudioRecord = AudioRecordNew(sampleRate, fifoDepth);
        if (this.mAudioRecord == 0) {
            throw new IOException("AudioRecord constructor failed - busy?");
        }
        int status = AudioRecordStart(this.mAudioRecord);
        if (status != 0) {
            close();
            throw new IOException("AudioRecord start failed: " + status);
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.mAudioRecord == 0) {
            throw new IllegalStateException("not open");
        }
        int rtn = AudioRecordRead(this.mAudioRecord, this.mOneByte, 0, 1);
        if (rtn == 1) {
            return this.mOneByte[0] & 255;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        if (this.mAudioRecord == 0) {
            throw new IllegalStateException("not open");
        }
        return AudioRecordRead(this.mAudioRecord, b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int offset, int length) throws IOException {
        if (this.mAudioRecord == 0) {
            throw new IllegalStateException("not open");
        }
        return AudioRecordRead(this.mAudioRecord, b, offset, length);
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        if (this.mAudioRecord != 0) {
            try {
                AudioRecordStop(this.mAudioRecord);
                try {
                    AudioRecordDelete(this.mAudioRecord);
                    this.mAudioRecord = 0;
                } finally {
                }
            } catch (Throwable th) {
                try {
                    AudioRecordDelete(this.mAudioRecord);
                    this.mAudioRecord = 0;
                    throw th;
                } finally {
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        if (this.mAudioRecord != 0) {
            close();
            throw new IOException("someone forgot to close MicrophoneInputStream");
        }
    }
}