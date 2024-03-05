package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;

/* loaded from: AudioTrack.class */
public class AudioTrack {
    private static final float VOLUME_MIN = 0.0f;
    private static final float VOLUME_MAX = 1.0f;
    private static final int SAMPLE_RATE_HZ_MIN = 4000;
    private static final int SAMPLE_RATE_HZ_MAX = 48000;
    public static final int PLAYSTATE_STOPPED = 1;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int MODE_STATIC = 0;
    public static final int MODE_STREAM = 1;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_NO_STATIC_DATA = 2;
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int ERROR_NATIVESETUP_AUDIOSYSTEM = -16;
    private static final int ERROR_NATIVESETUP_INVALIDCHANNELMASK = -17;
    private static final int ERROR_NATIVESETUP_INVALIDFORMAT = -18;
    private static final int ERROR_NATIVESETUP_INVALIDSTREAMTYPE = -19;
    private static final int ERROR_NATIVESETUP_NATIVEINITFAILED = -20;
    private static final int NATIVE_EVENT_MARKER = 3;
    private static final int NATIVE_EVENT_NEW_POS = 4;
    private static final String TAG = "android.media.AudioTrack";
    private int mState;
    private int mPlayState;
    private final Object mPlayStateLock;
    private int mNativeBufferSizeInBytes;
    private int mNativeBufferSizeInFrames;
    private NativeEventHandlerDelegate mEventHandlerDelegate;
    private final Looper mInitializationLooper;
    private int mSampleRate;
    private int mChannelCount;
    private int mChannels;
    private int mStreamType;
    private int mDataLoadMode;
    private int mChannelConfiguration;
    private int mAudioFormat;
    private int mSessionId;
    private int mNativeTrackInJavaObj;
    private int mJniData;
    private static final int SUPPORTED_OUT_CHANNELS = 1276;

    /* loaded from: AudioTrack$OnPlaybackPositionUpdateListener.class */
    public interface OnPlaybackPositionUpdateListener {
        void onMarkerReached(AudioTrack audioTrack);

        void onPeriodicNotification(AudioTrack audioTrack);
    }

    private final native int native_setup(Object obj, int i, int i2, int i3, int i4, int i5, int i6, int[] iArr);

    private final native void native_finalize();

    private final native void native_release();

    private final native void native_start();

    private final native void native_stop();

    private final native void native_pause();

    private final native void native_flush();

    private final native int native_write_byte(byte[] bArr, int i, int i2, int i3);

    private final native int native_write_short(short[] sArr, int i, int i2, int i3);

    private final native int native_reload_static();

    private final native int native_get_native_frame_count();

    private final native void native_setVolume(float f, float f2);

    private final native int native_set_playback_rate(int i);

    private final native int native_get_playback_rate();

    private final native int native_set_marker_pos(int i);

    private final native int native_get_marker_pos();

    private final native int native_set_pos_update_period(int i);

    private final native int native_get_pos_update_period();

    private final native int native_set_position(int i);

    private final native int native_get_position();

    private final native int native_get_latency();

    private final native int native_get_timestamp(long[] jArr);

    private final native int native_set_loop(int i, int i2, int i3);

    private static final native int native_get_output_sample_rate(int i);

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private final native int native_attachAuxEffect(int i);

    private final native void native_setAuxEffectSendLevel(float f);

    public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode) throws IllegalArgumentException {
        this(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode, 0);
    }

    public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode, int sessionId) throws IllegalArgumentException {
        this.mState = 0;
        this.mPlayState = 1;
        this.mPlayStateLock = new Object();
        this.mNativeBufferSizeInBytes = 0;
        this.mNativeBufferSizeInFrames = 0;
        this.mChannelCount = 1;
        this.mChannels = 4;
        this.mStreamType = 3;
        this.mDataLoadMode = 1;
        this.mChannelConfiguration = 4;
        this.mAudioFormat = 2;
        this.mSessionId = 0;
        Looper myLooper = Looper.myLooper();
        Looper looper = myLooper;
        this.mInitializationLooper = myLooper == null ? Looper.getMainLooper() : looper;
        audioParamCheck(streamType, sampleRateInHz, channelConfig, audioFormat, mode);
        audioBuffSizeCheck(bufferSizeInBytes);
        if (sessionId < 0) {
            throw new IllegalArgumentException("Invalid audio session ID: " + sessionId);
        }
        int[] session = {sessionId};
        int initResult = native_setup(new WeakReference(this), this.mStreamType, this.mSampleRate, this.mChannels, this.mAudioFormat, this.mNativeBufferSizeInBytes, this.mDataLoadMode, session);
        if (initResult != 0) {
            loge("Error code " + initResult + " when initializing AudioTrack.");
            return;
        }
        this.mSessionId = session[0];
        if (this.mDataLoadMode == 0) {
            this.mState = 2;
        } else {
            this.mState = 1;
        }
    }

    private void audioParamCheck(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int mode) {
        if (streamType != 4 && streamType != 3 && streamType != 2 && streamType != 1 && streamType != 0 && streamType != 5 && streamType != 6 && streamType != 8) {
            throw new IllegalArgumentException("Invalid stream type.");
        }
        this.mStreamType = streamType;
        if (sampleRateInHz < SAMPLE_RATE_HZ_MIN || sampleRateInHz > 48000) {
            throw new IllegalArgumentException(sampleRateInHz + "Hz is not a supported sample rate.");
        }
        this.mSampleRate = sampleRateInHz;
        this.mChannelConfiguration = channelConfig;
        switch (channelConfig) {
            case 1:
            case 2:
            case 4:
                this.mChannelCount = 1;
                this.mChannels = 4;
                break;
            case 3:
            case 12:
                this.mChannelCount = 2;
                this.mChannels = 12;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                if (!isMultichannelConfigSupported(channelConfig)) {
                    throw new IllegalArgumentException("Unsupported channel configuration.");
                }
                this.mChannels = channelConfig;
                this.mChannelCount = Integer.bitCount(channelConfig);
                break;
        }
        switch (audioFormat) {
            case 1:
                this.mAudioFormat = 2;
                break;
            case 2:
            case 3:
                this.mAudioFormat = audioFormat;
                break;
            default:
                throw new IllegalArgumentException("Unsupported sample encoding. Should be ENCODING_PCM_8BIT or ENCODING_PCM_16BIT.");
        }
        if (mode != 1 && mode != 0) {
            throw new IllegalArgumentException("Invalid mode.");
        }
        this.mDataLoadMode = mode;
    }

    private static boolean isMultichannelConfigSupported(int channelConfig) {
        if ((channelConfig & SUPPORTED_OUT_CHANNELS) != channelConfig) {
            loge("Channel configuration features unsupported channels");
            return false;
        } else if ((channelConfig & 12) != 12) {
            loge("Front channels must be present in multichannel configurations");
            return false;
        } else if ((channelConfig & 192) != 0 && (channelConfig & 192) != 192) {
            loge("Rear channels can't be used independently");
            return false;
        } else {
            return true;
        }
    }

    private void audioBuffSizeCheck(int audioBufferSize) {
        int frameSizeInBytes = this.mChannelCount * (this.mAudioFormat == 3 ? 1 : 2);
        if (audioBufferSize % frameSizeInBytes != 0 || audioBufferSize < 1) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = audioBufferSize;
        this.mNativeBufferSizeInFrames = audioBufferSize / frameSizeInBytes;
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

    public static float getMinVolume() {
        return 0.0f;
    }

    public static float getMaxVolume() {
        return 1.0f;
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getPlaybackRate() {
        return native_get_playback_rate();
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getStreamType() {
        return this.mStreamType;
    }

    public int getChannelConfiguration() {
        return this.mChannelConfiguration;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getState() {
        return this.mState;
    }

    public int getPlayState() {
        int i;
        synchronized (this.mPlayStateLock) {
            i = this.mPlayState;
        }
        return i;
    }

    @Deprecated
    protected int getNativeFrameCount() {
        return native_get_native_frame_count();
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    public int getPlaybackHeadPosition() {
        return native_get_position();
    }

    public int getLatency() {
        return native_get_latency();
    }

    public static int getNativeOutputSampleRate(int streamType) {
        return native_get_output_sample_rate(streamType);
    }

    public static int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat) {
        int channelCount;
        switch (channelConfig) {
            case 2:
            case 4:
                channelCount = 1;
                break;
            case 3:
            case 12:
                channelCount = 2;
                break;
            default:
                if ((channelConfig & SUPPORTED_OUT_CHANNELS) != channelConfig) {
                    loge("getMinBufferSize(): Invalid channel configuration.");
                    return -2;
                }
                channelCount = Integer.bitCount(channelConfig);
                break;
        }
        if (audioFormat != 2 && audioFormat != 3) {
            loge("getMinBufferSize(): Invalid audio format.");
            return -2;
        } else if (sampleRateInHz < SAMPLE_RATE_HZ_MIN || sampleRateInHz > 48000) {
            loge("getMinBufferSize(): " + sampleRateInHz + " Hz is not a supported sample rate.");
            return -2;
        } else {
            int size = native_get_min_buff_size(sampleRateInHz, channelCount, audioFormat);
            if (size <= 0) {
                loge("getMinBufferSize(): error querying hardware");
                return -1;
            }
            return size;
        }
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public boolean getTimestamp(AudioTimestamp timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException();
        }
        long[] longArray = new long[2];
        int ret = native_get_timestamp(longArray);
        if (ret != 0) {
            return false;
        }
        timestamp.framePosition = longArray[0];
        timestamp.nanoTime = longArray[1];
        return true;
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener listener) {
        setPlaybackPositionUpdateListener(listener, null);
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener listener, Handler handler) {
        if (listener != null) {
            this.mEventHandlerDelegate = new NativeEventHandlerDelegate(this, listener, handler);
        } else {
            this.mEventHandlerDelegate = null;
        }
    }

    public int setStereoVolume(float leftVolume, float rightVolume) {
        if (this.mState == 0) {
            return -3;
        }
        if (leftVolume < getMinVolume()) {
            leftVolume = getMinVolume();
        }
        if (leftVolume > getMaxVolume()) {
            leftVolume = getMaxVolume();
        }
        if (rightVolume < getMinVolume()) {
            rightVolume = getMinVolume();
        }
        if (rightVolume > getMaxVolume()) {
            rightVolume = getMaxVolume();
        }
        native_setVolume(leftVolume, rightVolume);
        return 0;
    }

    public int setVolume(float volume) {
        return setStereoVolume(volume, volume);
    }

    public int setPlaybackRate(int sampleRateInHz) {
        if (this.mState != 1) {
            return -3;
        }
        if (sampleRateInHz <= 0) {
            return -2;
        }
        return native_set_playback_rate(sampleRateInHz);
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

    public int setPlaybackHeadPosition(int positionInFrames) {
        if (this.mDataLoadMode == 1 || this.mState != 1 || getPlayState() == 3) {
            return -3;
        }
        if (0 > positionInFrames || positionInFrames > this.mNativeBufferSizeInFrames) {
            return -2;
        }
        return native_set_position(positionInFrames);
    }

    public int setLoopPoints(int startInFrames, int endInFrames, int loopCount) {
        if (this.mDataLoadMode == 1 || this.mState != 1 || getPlayState() == 3) {
            return -3;
        }
        if (loopCount != 0 && (0 > startInFrames || startInFrames >= this.mNativeBufferSizeInFrames || startInFrames >= endInFrames || endInFrames > this.mNativeBufferSizeInFrames)) {
            return -2;
        }
        return native_set_loop(startInFrames, endInFrames, loopCount);
    }

    @Deprecated
    protected void setState(int state) {
        this.mState = state;
    }

    public void play() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("play() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_start();
            this.mPlayState = 3;
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("stop() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_stop();
            this.mPlayState = 1;
        }
    }

    public void pause() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("pause() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_pause();
            this.mPlayState = 2;
        }
    }

    public void flush() {
        if (this.mState == 1) {
            native_flush();
        }
    }

    public int write(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (this.mState == 0) {
            return -3;
        }
        if (audioData == null || offsetInBytes < 0 || sizeInBytes < 0 || offsetInBytes + sizeInBytes < 0 || offsetInBytes + sizeInBytes > audioData.length) {
            return -2;
        }
        int ret = native_write_byte(audioData, offsetInBytes, sizeInBytes, this.mAudioFormat);
        if (this.mDataLoadMode == 0 && this.mState == 2 && ret > 0) {
            this.mState = 1;
        }
        return ret;
    }

    public int write(short[] audioData, int offsetInShorts, int sizeInShorts) {
        if (this.mState == 0) {
            return -3;
        }
        if (audioData == null || offsetInShorts < 0 || sizeInShorts < 0 || offsetInShorts + sizeInShorts < 0 || offsetInShorts + sizeInShorts > audioData.length) {
            return -2;
        }
        int ret = native_write_short(audioData, offsetInShorts, sizeInShorts, this.mAudioFormat);
        if (this.mDataLoadMode == 0 && this.mState == 2 && ret > 0) {
            this.mState = 1;
        }
        return ret;
    }

    public int reloadStaticData() {
        if (this.mDataLoadMode == 1 || this.mState != 1) {
            return -3;
        }
        return native_reload_static();
    }

    public int attachAuxEffect(int effectId) {
        if (this.mState == 0) {
            return -3;
        }
        return native_attachAuxEffect(effectId);
    }

    public int setAuxEffectSendLevel(float level) {
        if (this.mState == 0) {
            return -3;
        }
        if (level < getMinVolume()) {
            level = getMinVolume();
        }
        if (level > getMaxVolume()) {
            level = getMaxVolume();
        }
        native_setAuxEffectSendLevel(level);
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioTrack$NativeEventHandlerDelegate.class */
    public class NativeEventHandlerDelegate {
        private final Handler mHandler;

        NativeEventHandlerDelegate(final AudioTrack track, final OnPlaybackPositionUpdateListener listener, Handler handler) {
            Looper looper;
            if (handler == null) {
                looper = AudioTrack.this.mInitializationLooper;
            } else {
                looper = handler.getLooper();
            }
            if (looper != null) {
                this.mHandler = new Handler(looper) { // from class: android.media.AudioTrack.NativeEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message msg) {
                        if (track == null) {
                            return;
                        }
                        switch (msg.what) {
                            case 3:
                                if (listener != null) {
                                    listener.onMarkerReached(track);
                                    return;
                                }
                                return;
                            case 4:
                                if (listener != null) {
                                    listener.onPeriodicNotification(track);
                                    return;
                                }
                                return;
                            default:
                                AudioTrack.loge("Unknown native event type: " + msg.what);
                                return;
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler getHandler() {
            return this.mHandler;
        }
    }

    private static void postEventFromNative(Object audiotrack_ref, int what, int arg1, int arg2, Object obj) {
        NativeEventHandlerDelegate delegate;
        Handler handler;
        AudioTrack track = (AudioTrack) ((WeakReference) audiotrack_ref).get();
        if (track != null && (delegate = track.mEventHandlerDelegate) != null && (handler = delegate.getHandler()) != null) {
            Message m = handler.obtainMessage(what, arg1, arg2, obj);
            handler.sendMessage(m);
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