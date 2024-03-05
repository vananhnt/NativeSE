package android.media;

import android.media.MediaCodec;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/* loaded from: MediaMuxer.class */
public final class MediaMuxer {
    private int mNativeContext;
    private static final int MUXER_STATE_UNINITIALIZED = -1;
    private static final int MUXER_STATE_INITIALIZED = 0;
    private static final int MUXER_STATE_STARTED = 1;
    private static final int MUXER_STATE_STOPPED = 2;
    private int mState;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private int mLastTrackIndex = -1;
    private int mNativeObject;

    private static native int nativeSetup(FileDescriptor fileDescriptor, int i);

    private static native void nativeRelease(int i);

    private static native void nativeStart(int i);

    private static native void nativeStop(int i);

    private static native int nativeAddTrack(int i, String[] strArr, Object[] objArr);

    private static native void nativeSetOrientationHint(int i, int i2);

    private static native void nativeSetLocation(int i, int i2, int i3);

    private static native void nativeWriteSampleData(int i, int i2, ByteBuffer byteBuffer, int i3, int i4, long j, int i5);

    static {
        System.loadLibrary("media_jni");
    }

    /* loaded from: MediaMuxer$OutputFormat.class */
    public static final class OutputFormat {
        public static final int MUXER_OUTPUT_MPEG_4 = 0;

        private OutputFormat() {
        }
    }

    public MediaMuxer(String path, int format) throws IOException {
        this.mState = -1;
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        if (format != 0) {
            throw new IllegalArgumentException("format is invalid");
        }
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            fos = new FileOutputStream(file);
            FileDescriptor fd = fos.getFD();
            this.mNativeObject = nativeSetup(fd, format);
            this.mState = 0;
            this.mCloseGuard.open("release");
            if (fos != null) {
                fos.close();
            }
        } catch (Throwable th) {
            if (fos != null) {
                fos.close();
            }
            throw th;
        }
    }

    public void setOrientationHint(int degrees) {
        if (degrees != 0 && degrees != 90 && degrees != 180 && degrees != 270) {
            throw new IllegalArgumentException("Unsupported angle: " + degrees);
        }
        if (this.mState == 0) {
            nativeSetOrientationHint(this.mNativeObject, degrees);
            return;
        }
        throw new IllegalStateException("Can't set rotation degrees due to wrong state.");
    }

    public void setLocation(float latitude, float longitude) {
        int latitudex10000 = (int) ((latitude * 10000.0f) + 0.5d);
        int longitudex10000 = (int) ((longitude * 10000.0f) + 0.5d);
        if (latitudex10000 > 900000 || latitudex10000 < -900000) {
            String msg = "Latitude: " + latitude + " out of range.";
            throw new IllegalArgumentException(msg);
        } else if (longitudex10000 > 1800000 || longitudex10000 < -1800000) {
            String msg2 = "Longitude: " + longitude + " out of range";
            throw new IllegalArgumentException(msg2);
        } else if (this.mState == 0 && this.mNativeObject != 0) {
            nativeSetLocation(this.mNativeObject, latitudex10000, longitudex10000);
        } else {
            throw new IllegalStateException("Can't set location due to wrong state.");
        }
    }

    public void start() {
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        if (this.mState == 0) {
            nativeStart(this.mNativeObject);
            this.mState = 1;
            return;
        }
        throw new IllegalStateException("Can't start due to wrong state.");
    }

    public void stop() {
        if (this.mState == 1) {
            nativeStop(this.mNativeObject);
            this.mState = 2;
            return;
        }
        throw new IllegalStateException("Can't stop due to wrong state.");
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            if (this.mNativeObject != 0) {
                nativeRelease(this.mNativeObject);
                this.mNativeObject = 0;
            }
        } finally {
            super.finalize();
        }
    }

    public int addTrack(MediaFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("format must not be null.");
        }
        if (this.mState != 0) {
            throw new IllegalStateException("Muxer is not initialized.");
        }
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        Map<String, Object> formatMap = format.getMap();
        int mapSize = formatMap.size();
        if (mapSize > 0) {
            String[] keys = new String[mapSize];
            Object[] values = new Object[mapSize];
            int i = 0;
            for (Map.Entry<String, Object> entry : formatMap.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
            int trackIndex = nativeAddTrack(this.mNativeObject, keys, values);
            if (this.mLastTrackIndex >= trackIndex) {
                throw new IllegalArgumentException("Invalid format.");
            }
            this.mLastTrackIndex = trackIndex;
            return trackIndex;
        }
        throw new IllegalArgumentException("format must not be empty.");
    }

    public void writeSampleData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
        if (trackIndex < 0 || trackIndex > this.mLastTrackIndex) {
            throw new IllegalArgumentException("trackIndex is invalid");
        }
        if (byteBuf == null) {
            throw new IllegalArgumentException("byteBuffer must not be null");
        }
        if (bufferInfo == null) {
            throw new IllegalArgumentException("bufferInfo must not be null");
        }
        if (bufferInfo.size < 0 || bufferInfo.offset < 0 || bufferInfo.offset + bufferInfo.size > byteBuf.capacity() || bufferInfo.presentationTimeUs < 0) {
            throw new IllegalArgumentException("bufferInfo must specify a valid buffer offset, size and presentation time");
        }
        if (this.mNativeObject == 0) {
            throw new IllegalStateException("Muxer has been released!");
        }
        if (this.mState != 1) {
            throw new IllegalStateException("Can't write, muxer is not started");
        }
        nativeWriteSampleData(this.mNativeObject, trackIndex, byteBuf, bufferInfo.offset, bufferInfo.size, bufferInfo.presentationTimeUs, bufferInfo.flags);
    }

    public void release() {
        if (this.mState == 1) {
            stop();
        }
        if (this.mNativeObject != 0) {
            nativeRelease(this.mNativeObject);
            this.mNativeObject = 0;
            this.mCloseGuard.close();
        }
        this.mState = -1;
    }
}