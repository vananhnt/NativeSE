package android.media;

import android.app.ActivityThread;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* loaded from: MediaRecorder.class */
public class MediaRecorder {
    private static final String TAG = "MediaRecorder";
    private int mNativeContext;
    private Surface mSurface;
    private String mPath;
    private FileDescriptor mFd;
    private EventHandler mEventHandler;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    public static final int MEDIA_RECORDER_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_SERVER_DIED = 100;
    public static final int MEDIA_RECORDER_INFO_UNKNOWN = 1;
    public static final int MEDIA_RECORDER_INFO_MAX_DURATION_REACHED = 800;
    public static final int MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED = 801;
    public static final int MEDIA_RECORDER_TRACK_INFO_LIST_START = 1000;
    public static final int MEDIA_RECORDER_TRACK_INFO_COMPLETION_STATUS = 1000;
    public static final int MEDIA_RECORDER_TRACK_INFO_PROGRESS_IN_TIME = 1001;
    public static final int MEDIA_RECORDER_TRACK_INFO_TYPE = 1002;
    public static final int MEDIA_RECORDER_TRACK_INFO_DURATION_MS = 1003;
    public static final int MEDIA_RECORDER_TRACK_INFO_MAX_CHUNK_DUR_MS = 1004;
    public static final int MEDIA_RECORDER_TRACK_INFO_ENCODED_FRAMES = 1005;
    public static final int MEDIA_RECORDER_TRACK_INTER_CHUNK_TIME_MS = 1006;
    public static final int MEDIA_RECORDER_TRACK_INFO_INITIAL_DELAY_MS = 1007;
    public static final int MEDIA_RECORDER_TRACK_INFO_START_OFFSET_MS = 1008;
    public static final int MEDIA_RECORDER_TRACK_INFO_DATA_KBYTES = 1009;
    public static final int MEDIA_RECORDER_TRACK_INFO_LIST_END = 2000;

    /* loaded from: MediaRecorder$OnErrorListener.class */
    public interface OnErrorListener {
        void onError(MediaRecorder mediaRecorder, int i, int i2);
    }

    /* loaded from: MediaRecorder$OnInfoListener.class */
    public interface OnInfoListener {
        void onInfo(MediaRecorder mediaRecorder, int i, int i2);
    }

    public native void setCamera(Camera camera);

    public native void setAudioSource(int i) throws IllegalStateException;

    public native void setVideoSource(int i) throws IllegalStateException;

    public native void setOutputFormat(int i) throws IllegalStateException;

    public native void setVideoSize(int i, int i2) throws IllegalStateException;

    public native void setVideoFrameRate(int i) throws IllegalStateException;

    public native void setMaxDuration(int i) throws IllegalArgumentException;

    public native void setMaxFileSize(long j) throws IllegalArgumentException;

    public native void setAudioEncoder(int i) throws IllegalStateException;

    public native void setVideoEncoder(int i) throws IllegalStateException;

    private native void _setOutputFile(FileDescriptor fileDescriptor, long j, long j2) throws IllegalStateException, IOException;

    private native void _prepare() throws IllegalStateException, IOException;

    public native void start() throws IllegalStateException;

    public native void stop() throws IllegalStateException;

    private native void native_reset();

    public native int getMaxAmplitude() throws IllegalStateException;

    public native void release();

    private static final native void native_init();

    private final native void native_setup(Object obj, String str) throws IllegalStateException;

    private final native void native_finalize();

    private native void setParameter(String str);

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public MediaRecorder() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this.mEventHandler = new EventHandler(this, looper2);
            } else {
                this.mEventHandler = null;
            }
        }
        String packageName = ActivityThread.currentPackageName();
        native_setup(new WeakReference(this), packageName);
    }

    public void setPreviewDisplay(Surface sv) {
        this.mSurface = sv;
    }

    /* loaded from: MediaRecorder$AudioSource.class */
    public final class AudioSource {
        public static final int DEFAULT = 0;
        public static final int MIC = 1;
        public static final int VOICE_UPLINK = 2;
        public static final int VOICE_DOWNLINK = 3;
        public static final int VOICE_CALL = 4;
        public static final int CAMCORDER = 5;
        public static final int VOICE_RECOGNITION = 6;
        public static final int VOICE_COMMUNICATION = 7;
        public static final int REMOTE_SUBMIX = 8;
        protected static final int HOTWORD = 1999;

        private AudioSource() {
        }
    }

    /* loaded from: MediaRecorder$VideoSource.class */
    public final class VideoSource {
        public static final int DEFAULT = 0;
        public static final int CAMERA = 1;
        public static final int GRALLOC_BUFFER = 2;

        private VideoSource() {
        }
    }

    /* loaded from: MediaRecorder$OutputFormat.class */
    public final class OutputFormat {
        public static final int DEFAULT = 0;
        public static final int THREE_GPP = 1;
        public static final int MPEG_4 = 2;
        public static final int RAW_AMR = 3;
        public static final int AMR_NB = 3;
        public static final int AMR_WB = 4;
        public static final int AAC_ADIF = 5;
        public static final int AAC_ADTS = 6;
        public static final int OUTPUT_FORMAT_RTP_AVP = 7;
        public static final int OUTPUT_FORMAT_MPEG2TS = 8;

        private OutputFormat() {
        }
    }

    /* loaded from: MediaRecorder$AudioEncoder.class */
    public final class AudioEncoder {
        public static final int DEFAULT = 0;
        public static final int AMR_NB = 1;
        public static final int AMR_WB = 2;
        public static final int AAC = 3;
        public static final int HE_AAC = 4;
        public static final int AAC_ELD = 5;

        private AudioEncoder() {
        }
    }

    /* loaded from: MediaRecorder$VideoEncoder.class */
    public final class VideoEncoder {
        public static final int DEFAULT = 0;
        public static final int H263 = 1;
        public static final int H264 = 2;
        public static final int MPEG_4_SP = 3;

        private VideoEncoder() {
        }
    }

    public static final int getAudioSourceMax() {
        return 8;
    }

    public void setProfile(CamcorderProfile profile) {
        setOutputFormat(profile.fileFormat);
        setVideoFrameRate(profile.videoFrameRate);
        setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        setVideoEncodingBitRate(profile.videoBitRate);
        setVideoEncoder(profile.videoCodec);
        if (profile.quality < 1000 || profile.quality > 1007) {
            setAudioEncodingBitRate(profile.audioBitRate);
            setAudioChannels(profile.audioChannels);
            setAudioSamplingRate(profile.audioSampleRate);
            setAudioEncoder(profile.audioCodec);
        }
    }

    public void setCaptureRate(double fps) {
        setParameter("time-lapse-enable=1");
        double timeBetweenFrameCapture = 1.0d / fps;
        int timeBetweenFrameCaptureMs = (int) (1000.0d * timeBetweenFrameCapture);
        setParameter("time-between-time-lapse-frame-capture=" + timeBetweenFrameCaptureMs);
    }

    public void setOrientationHint(int degrees) {
        if (degrees != 0 && degrees != 90 && degrees != 180 && degrees != 270) {
            throw new IllegalArgumentException("Unsupported angle: " + degrees);
        }
        setParameter("video-param-rotation-angle-degrees=" + degrees);
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
        } else {
            setParameter("param-geotag-latitude=" + latitudex10000);
            setParameter("param-geotag-longitude=" + longitudex10000);
        }
    }

    public void setAudioSamplingRate(int samplingRate) {
        if (samplingRate <= 0) {
            throw new IllegalArgumentException("Audio sampling rate is not positive");
        }
        setParameter("audio-param-sampling-rate=" + samplingRate);
    }

    public void setAudioChannels(int numChannels) {
        if (numChannels <= 0) {
            throw new IllegalArgumentException("Number of channels is not positive");
        }
        setParameter("audio-param-number-of-channels=" + numChannels);
    }

    public void setAudioEncodingBitRate(int bitRate) {
        if (bitRate <= 0) {
            throw new IllegalArgumentException("Audio encoding bit rate is not positive");
        }
        setParameter("audio-param-encoding-bitrate=" + bitRate);
    }

    public void setVideoEncodingBitRate(int bitRate) {
        if (bitRate <= 0) {
            throw new IllegalArgumentException("Video encoding bit rate is not positive");
        }
        setParameter("video-param-encoding-bitrate=" + bitRate);
    }

    public void setAuxiliaryOutputFile(FileDescriptor fd) {
        Log.w(TAG, "setAuxiliaryOutputFile(FileDescriptor) is no longer supported.");
    }

    public void setAuxiliaryOutputFile(String path) {
        Log.w(TAG, "setAuxiliaryOutputFile(String) is no longer supported.");
    }

    public void setOutputFile(FileDescriptor fd) throws IllegalStateException {
        this.mPath = null;
        this.mFd = fd;
    }

    public void setOutputFile(String path) throws IllegalStateException {
        this.mFd = null;
        this.mPath = path;
    }

    public void prepare() throws IllegalStateException, IOException {
        if (this.mPath != null) {
            FileOutputStream fos = new FileOutputStream(this.mPath);
            try {
                _setOutputFile(fos.getFD(), 0L, 0L);
                fos.close();
            } catch (Throwable th) {
                fos.close();
                throw th;
            }
        } else if (this.mFd != null) {
            _setOutputFile(this.mFd, 0L, 0L);
        } else {
            throw new IOException("No valid output file");
        }
        _prepare();
    }

    public void reset() {
        native_reset();
        this.mEventHandler.removeCallbacksAndMessages(null);
    }

    public void setOnErrorListener(OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public void setOnInfoListener(OnInfoListener listener) {
        this.mOnInfoListener = listener;
    }

    /* loaded from: MediaRecorder$EventHandler.class */
    private class EventHandler extends Handler {
        private MediaRecorder mMediaRecorder;
        private static final int MEDIA_RECORDER_EVENT_LIST_START = 1;
        private static final int MEDIA_RECORDER_EVENT_ERROR = 1;
        private static final int MEDIA_RECORDER_EVENT_INFO = 2;
        private static final int MEDIA_RECORDER_EVENT_LIST_END = 99;
        private static final int MEDIA_RECORDER_TRACK_EVENT_LIST_START = 100;
        private static final int MEDIA_RECORDER_TRACK_EVENT_ERROR = 100;
        private static final int MEDIA_RECORDER_TRACK_EVENT_INFO = 101;
        private static final int MEDIA_RECORDER_TRACK_EVENT_LIST_END = 1000;

        public EventHandler(MediaRecorder mr, Looper looper) {
            super(looper);
            this.mMediaRecorder = mr;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (this.mMediaRecorder.mNativeContext != 0) {
                switch (msg.what) {
                    case 1:
                    case 100:
                        if (MediaRecorder.this.mOnErrorListener != null) {
                            MediaRecorder.this.mOnErrorListener.onError(this.mMediaRecorder, msg.arg1, msg.arg2);
                            return;
                        }
                        return;
                    case 2:
                    case 101:
                        if (MediaRecorder.this.mOnInfoListener != null) {
                            MediaRecorder.this.mOnInfoListener.onInfo(this.mMediaRecorder, msg.arg1, msg.arg2);
                            return;
                        }
                        return;
                    default:
                        Log.e(MediaRecorder.TAG, "Unknown message type " + msg.what);
                        return;
                }
            }
            Log.w(MediaRecorder.TAG, "mediarecorder went away with unhandled events");
        }
    }

    private static void postEventFromNative(Object mediarecorder_ref, int what, int arg1, int arg2, Object obj) {
        MediaRecorder mr = (MediaRecorder) ((WeakReference) mediarecorder_ref).get();
        if (mr != null && mr.mEventHandler != null) {
            Message m = mr.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            mr.mEventHandler.sendMessage(m);
        }
    }

    protected void finalize() {
        native_finalize();
    }
}