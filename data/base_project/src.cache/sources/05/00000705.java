package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/* loaded from: AudioRecord.class */
public class AudioRecord {
    public static final int STATE_UNINITIALIZED = 0;
    public static final int STATE_INITIALIZED = 1;
    public static final int RECORDSTATE_STOPPED = 1;
    public static final int RECORDSTATE_RECORDING = 3;
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int AUDIORECORD_ERROR_SETUP_ZEROFRAMECOUNT = -16;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDCHANNELMASK = -17;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDFORMAT = -18;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDSOURCE = -19;
    private static final int AUDIORECORD_ERROR_SETUP_NATIVEINITFAILED = -20;
    private static final int NATIVE_EVENT_MARKER = 2;
    private static final int NATIVE_EVENT_NEW_POS = 3;
    private static final String TAG = "android.media.AudioRecord";
    private int mNativeRecorderInJavaObj;
    private int mNativeCallbackCookie;
    private int mSampleRate;
    private int mChannelCount;
    private int mChannelMask;
    private int mAudioFormat;
    private int mRecordSource;
    private int mState;
    private int mRecordingState;
    private Looper mInitializationLooper;
    private int mSessionId;
    private final Object mRecordingStateLock = new Object();
    private OnRecordPositionUpdateListener mPositionListener = null;
    private final Object mPositionListenerLock = new Object();
    private NativeEventHandler mEventHandler = null;
    private int mNativeBufferSizeInBytes = 0;

    /* loaded from: AudioRecord$OnRecordPositionUpdateListener.class */
    public interface OnRecordPositionUpdateListener {
        void onMarkerReached(AudioRecord audioRecord);

        void onPeriodicNotification(AudioRecord audioRecord);
    }

    private final native int native_setup(Object obj, int i, int i2, int i3, int i4, int i5, int[] iArr);

    private final native void native_finalize();

    private final native void native_release();

    private final native int native_start(int i, int i2);

    private final native void native_stop();

    private final native int native_read_in_byte_array(byte[] bArr, int i, int i2);

    private final native int native_read_in_short_array(short[] sArr, int i, int i2);

    private final native int native_read_in_direct_buffer(Object obj, int i);

    private final native int native_set_marker_pos(int i);

    private final native int native_get_marker_pos();

    private final native int native_set_pos_update_period(int i);

    private final native int native_get_pos_update_period();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    public AudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) throws IllegalArgumentException {
        this.mState = 0;
        this.mRecordingState = 1;
        this.mInitializationLooper = null;
        this.mSessionId = 0;
        this.mRecordingState = 1;
        Looper myLooper = Looper.myLooper();
        this.mInitializationLooper = myLooper;
        if (myLooper == null) {
            this.mInitializationLooper = Looper.getMainLooper();
        }
        audioParamCheck(audioSource, sampleRateInHz, channelConfig, audioFormat);
        audioBuffSizeCheck(bufferSizeInBytes);
        int[] session = {0};
        int initResult = native_setup(new WeakReference(this), this.mRecordSource, this.mSampleRate, this.mChannelMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, session);
        if (initResult != 0) {
            loge("Error code " + initResult + " when initializing native AudioRecord object.");
            return;
        }
        this.mSessionId = session[0];
        this.mState = 1;
    }

    private void audioParamCheck(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        if (audioSource < 0 || (audioSource > MediaRecorder.getAudioSourceMax() && audioSource != 1999)) {
            throw new IllegalArgumentException("Invalid audio source.");
        }
        this.mRecordSource = audioSource;
        if (sampleRateInHz < 4000 || sampleRateInHz > 48000) {
            throw new IllegalArgumentException(sampleRateInHz + "Hz is not a supported sample rate.");
        }
        this.mSampleRate = sampleRateInHz;
        switch (channelConfig) {
            case 1:
            case 2:
            case 16:
                this.mChannelCount = 1;
                this.mChannelMask = 16;
                break;
            case 3:
            case 12:
                this.mChannelCount = 2;
                this.mChannelMask = 12;
                break;
            case 48:
                this.mChannelCount = 2;
                this.mChannelMask = channelConfig;
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel configuration.");
        }
        switch (audioFormat) {
            case 1:
                this.mAudioFormat = 2;
                return;
            case 2:
            case 3:
                this.mAudioFormat = audioFormat;
                return;
            default:
                throw new IllegalArgumentException("Unsupported sample encoding. Should be ENCODING_PCM_8BIT or ENCODING_PCM_16BIT.");
        }
    }

    private void audioBuffSizeCheck(int audioBufferSize) {
        int frameSizeInBytes = this.mChannelCount * (this.mAudioFormat == 3 ? 1 : 2);
        if (audioBufferSize % frameSizeInBytes != 0 || audioBufferSize < 1) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = audioBufferSize;
    }

    public void release() {
        try {
            stop();
        } catch (IllegalStateException e) {
        }
        native_release();
        this.mState = 0;
    }

    protected void finalize() {
        native_finalize();
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getAudioSource() {
        return this.mRecordSource;
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getChannelConfiguration() {
        return this.mChannelMask;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getState() {
        return this.mState;
    }

    public int getRecordingState() {
        int i;
        synchronized (this.mRecordingStateLock) {
            i = this.mRecordingState;
        }
        return i;
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    public static int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat) {
        int channelCount;
        switch (channelConfig) {
            case 0:
            default:
                loge("getMinBufferSize(): Invalid channel configuration.");
                return -2;
            case 1:
            case 2:
            case 16:
                channelCount = 1;
                break;
            case 3:
            case 12:
            case 48:
                channelCount = 2;
                break;
        }
        if (audioFormat != 2) {
            loge("getMinBufferSize(): Invalid audio format.");
            return -2;
        }
        int size = native_get_min_buff_size(sampleRateInHz, channelCount, audioFormat);
        if (size == 0) {
            return -2;
        }
        if (size == -1) {
            return -1;
        }
        return size;
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public void startRecording() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(0, 0) == 0) {
                this.mRecordingState = 3;
            }
        }
    }

    public void startRecording(MediaSyncEvent syncEvent) throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(syncEvent.getType(), syncEvent.getAudioSessionId()) == 0) {
                this.mRecordingState = 3;
            }
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("stop() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            native_stop();
            this.mRecordingState = 1;
        }
    }

    public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (this.mState != 1) {
            return -3;
        }
        if (audioData == null || offsetInBytes < 0 || sizeInBytes < 0 || offsetInBytes + sizeInBytes < 0 || offsetInBytes + sizeInBytes > audioData.length) {
            return -2;
        }
        return native_read_in_byte_array(audioData, offsetInBytes, sizeInBytes);
    }

    public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {
        if (this.mState != 1) {
            return -3;
        }
        if (audioData == null || offsetInShorts < 0 || sizeInShorts < 0 || offsetInShorts + sizeInShorts < 0 || offsetInShorts + sizeInShorts > audioData.length) {
            return -2;
        }
        return native_read_in_short_array(audioData, offsetInShorts, sizeInShorts);
    }

    public int read(ByteBuffer audioBuffer, int sizeInBytes) {
        if (this.mState != 1) {
            return -3;
        }
        if (audioBuffer == null || sizeInBytes < 0) {
            return -2;
        }
        return native_read_in_direct_buffer(audioBuffer, sizeInBytes);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener listener) {
        setRecordPositionUpdateListener(listener, null);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener listener, Handler handler) {
        synchronized (this.mPositionListenerLock) {
            this.mPositionListener = listener;
            if (listener != null) {
                if (handler != null) {
                    this.mEventHandler = new NativeEventHandler(this, handler.getLooper());
                } else {
                    this.mEventHandler = new NativeEventHandler(this, this.mInitializationLooper);
                }
            } else {
                this.mEventHandler = null;
            }
        }
    }

    public int setNotificationMarkerPosition(int markerInFrames) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_marker_pos(markerInFrames);
    }

    public int setPositionNotificationPeriod(int periodInFrames) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_pos_update_period(periodInFrames);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioRecord$NativeEventHandler.class */
    public class NativeEventHandler extends Handler {
        private final AudioRecord mAudioRecord;

        NativeEventHandler(AudioRecord recorder, Looper looper) {
            super(looper);
            this.mAudioRecord = recorder;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            OnRecordPositionUpdateListener listener;
            synchronized (AudioRecord.this.mPositionListenerLock) {
                listener = this.mAudioRecord.mPositionListener;
            }
            switch (msg.what) {
                case 2:
                    if (listener != null) {
                        listener.onMarkerReached(this.mAudioRecord);
                        return;
                    }
                    return;
                case 3:
                    if (listener != null) {
                        listener.onPeriodicNotification(this.mAudioRecord);
                        return;
                    }
                    return;
                default:
                    AudioRecord.loge("Unknown native event type: " + msg.what);
                    return;
            }
        }
    }

    private static void postEventFromNative(Object audiorecord_ref, int what, int arg1, int arg2, Object obj) {
        AudioRecord recorder = (AudioRecord) ((WeakReference) audiorecord_ref).get();
        if (recorder != null && recorder.mEventHandler != null) {
            Message m = recorder.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            recorder.mEventHandler.sendMessage(m);
        }
    }

    private static void logd(String msg) {
        Log.d(TAG, msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String msg) {
        Log.e(TAG, msg);
    }
}