package android.drm;

import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.UnknownServiceException;
import java.util.Arrays;
import libcore.io.Streams;

/* loaded from: DrmOutputStream.class */
public class DrmOutputStream extends OutputStream {
    private static final String TAG = "DrmOutputStream";
    private final DrmManagerClient mClient;
    private final RandomAccessFile mFile;
    private int mSessionId;

    public DrmOutputStream(DrmManagerClient client, RandomAccessFile file, String mimeType) throws IOException {
        this.mSessionId = -1;
        this.mClient = client;
        this.mFile = file;
        this.mSessionId = this.mClient.openConvertSession(mimeType);
        if (this.mSessionId == -1) {
            throw new UnknownServiceException("Failed to open DRM session for " + mimeType);
        }
    }

    public void finish() throws IOException {
        DrmConvertedStatus status = this.mClient.closeConvertSession(this.mSessionId);
        if (status.statusCode == 1) {
            this.mFile.seek(status.offset);
            this.mFile.write(status.convertedData);
            this.mSessionId = -1;
            return;
        }
        throw new IOException("Unexpected DRM status: " + status.statusCode);
    }

    @Override // java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        if (this.mSessionId == -1) {
            Log.w(TAG, "Closing stream without finishing");
        }
        this.mFile.close();
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int count) throws IOException {
        byte[] exactBuffer;
        Arrays.checkOffsetAndCount(buffer.length, offset, count);
        if (count == buffer.length) {
            exactBuffer = buffer;
        } else {
            exactBuffer = new byte[count];
            System.arraycopy(buffer, offset, exactBuffer, 0, count);
        }
        DrmConvertedStatus status = this.mClient.convertData(this.mSessionId, exactBuffer);
        if (status.statusCode == 1) {
            this.mFile.write(status.convertedData);
            return;
        }
        throw new IOException("Unexpected DRM status: " + status.statusCode);
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        Streams.writeSingleByte(this, oneByte);
    }
}