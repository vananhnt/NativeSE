package android.speech.tts;

import android.speech.tts.TextToSpeechService;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PlaybackSynthesisCallback.class */
public class PlaybackSynthesisCallback extends AbstractSynthesisCallback {
    private static final String TAG = "PlaybackSynthesisRequest";
    private static final boolean DBG = false;
    private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
    private final int mStreamType;
    private final float mVolume;
    private final float mPan;
    private final AudioPlaybackHandler mAudioTrackHandler;
    private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
    private final Object mCallerIdentity;
    private final EventLogger mLogger;
    private final Object mStateLock = new Object();
    private SynthesisPlaybackQueueItem mItem = null;
    private boolean mStopped = false;
    private volatile boolean mDone = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlaybackSynthesisCallback(int streamType, float volume, float pan, AudioPlaybackHandler audioTrackHandler, TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, EventLogger logger) {
        this.mStreamType = streamType;
        this.mVolume = volume;
        this.mPan = pan;
        this.mAudioTrackHandler = audioTrackHandler;
        this.mDispatcher = dispatcher;
        this.mCallerIdentity = callerIdentity;
        this.mLogger = logger;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.AbstractSynthesisCallback
    public void stop() {
        stopImpl(false);
    }

    void stopImpl(boolean wasError) {
        this.mLogger.onStopped();
        synchronized (this.mStateLock) {
            if (this.mStopped) {
                Log.w(TAG, "stop() called twice");
                return;
            }
            SynthesisPlaybackQueueItem item = this.mItem;
            this.mStopped = true;
            if (item != null) {
                item.stop(wasError);
                return;
            }
            this.mLogger.onWriteData();
            if (wasError) {
                this.mDispatcher.dispatchOnError();
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int getMaxBufferSize() {
        return 8192;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.AbstractSynthesisCallback
    public boolean isDone() {
        return this.mDone;
    }

    @Override // android.speech.tts.SynthesisCallback
    public int start(int sampleRateInHz, int audioFormat, int channelCount) {
        int channelConfig = BlockingAudioTrack.getChannelConfig(channelCount);
        if (channelConfig == 0) {
            Log.e(TAG, "Unsupported number of channels :" + channelCount);
            return -1;
        }
        synchronized (this.mStateLock) {
            if (this.mStopped) {
                return -1;
            }
            SynthesisPlaybackQueueItem item = new SynthesisPlaybackQueueItem(this.mStreamType, sampleRateInHz, audioFormat, channelCount, this.mVolume, this.mPan, this.mDispatcher, this.mCallerIdentity, this.mLogger);
            this.mAudioTrackHandler.enqueue(item);
            this.mItem = item;
            return 0;
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int audioAvailable(byte[] buffer, int offset, int length) {
        if (length > getMaxBufferSize() || length <= 0) {
            throw new IllegalArgumentException("buffer is too large or of zero length (" + length + " bytes)");
        }
        synchronized (this.mStateLock) {
            if (this.mItem == null || this.mStopped) {
                return -1;
            }
            SynthesisPlaybackQueueItem item = this.mItem;
            byte[] bufferCopy = new byte[length];
            System.arraycopy(buffer, offset, bufferCopy, 0, length);
            try {
                item.put(bufferCopy);
                this.mLogger.onEngineDataReceived();
                return 0;
            } catch (InterruptedException e) {
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int done() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                Log.w(TAG, "Duplicate call to done()");
                return -1;
            }
            this.mDone = true;
            if (this.mItem == null) {
                return -1;
            }
            SynthesisPlaybackQueueItem item = this.mItem;
            item.done();
            this.mLogger.onEngineComplete();
            return 0;
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error() {
        this.mLogger.onError();
        stopImpl(true);
    }
}