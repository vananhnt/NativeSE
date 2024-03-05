package android.speech.tts;

import android.speech.tts.TextToSpeechService;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SynthesisPlaybackQueueItem.class */
public final class SynthesisPlaybackQueueItem extends PlaybackQueueItem {
    private static final String TAG = "TTS.SynthQueueItem";
    private static final boolean DBG = false;
    private static final long MAX_UNCONSUMED_AUDIO_MS = 500;
    private final Lock mListLock;
    private final Condition mReadReady;
    private final Condition mNotFull;
    private final LinkedList<ListEntry> mDataBufferList;
    private int mUnconsumedBytes;
    private volatile boolean mStopped;
    private volatile boolean mDone;
    private volatile boolean mIsError;
    private final BlockingAudioTrack mAudioTrack;
    private final EventLogger mLogger;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SynthesisPlaybackQueueItem(int streamType, int sampleRate, int audioFormat, int channelCount, float volume, float pan, TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, EventLogger logger) {
        super(dispatcher, callerIdentity);
        this.mListLock = new ReentrantLock();
        this.mReadReady = this.mListLock.newCondition();
        this.mNotFull = this.mListLock.newCondition();
        this.mDataBufferList = new LinkedList<>();
        this.mUnconsumedBytes = 0;
        this.mStopped = false;
        this.mDone = false;
        this.mIsError = false;
        this.mAudioTrack = new BlockingAudioTrack(streamType, sampleRate, audioFormat, channelCount, volume, pan);
        this.mLogger = logger;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        TextToSpeechService.UtteranceProgressDispatcher dispatcher = getDispatcher();
        dispatcher.dispatchOnStart();
        if (!this.mAudioTrack.init()) {
            dispatcher.dispatchOnError();
            return;
        }
        while (true) {
            try {
                byte[] buffer = take();
                if (buffer == null) {
                    break;
                }
                this.mAudioTrack.write(buffer);
                this.mLogger.onAudioDataWritten();
            } catch (InterruptedException e) {
            }
        }
        this.mAudioTrack.waitAndRelease();
        if (this.mIsError) {
            dispatcher.dispatchOnError();
        } else {
            dispatcher.dispatchOnDone();
        }
        this.mLogger.onWriteData();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.PlaybackQueueItem
    public void stop(boolean isError) {
        try {
            this.mListLock.lock();
            this.mStopped = true;
            this.mIsError = isError;
            this.mReadReady.signal();
            this.mNotFull.signal();
            this.mListLock.unlock();
            this.mAudioTrack.stop();
        } catch (Throwable th) {
            this.mListLock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void done() {
        try {
            this.mListLock.lock();
            this.mDone = true;
            this.mReadReady.signal();
            this.mNotFull.signal();
            this.mListLock.unlock();
        } catch (Throwable th) {
            this.mListLock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(byte[] buffer) throws InterruptedException {
        try {
            this.mListLock.lock();
            while (this.mAudioTrack.getAudioLengthMs(this.mUnconsumedBytes) > MAX_UNCONSUMED_AUDIO_MS && !this.mStopped) {
                this.mNotFull.await();
            }
            if (this.mStopped) {
                return;
            }
            this.mDataBufferList.add(new ListEntry(buffer));
            this.mUnconsumedBytes += buffer.length;
            this.mReadReady.signal();
            this.mListLock.unlock();
        } finally {
            this.mListLock.unlock();
        }
    }

    private byte[] take() throws InterruptedException {
        try {
            this.mListLock.lock();
            while (this.mDataBufferList.size() == 0 && !this.mStopped && !this.mDone) {
                this.mReadReady.await();
            }
            if (this.mStopped) {
                return null;
            }
            ListEntry entry = this.mDataBufferList.poll();
            if (entry != null) {
                this.mUnconsumedBytes -= entry.mBytes.length;
                this.mNotFull.signal();
                byte[] bArr = entry.mBytes;
                this.mListLock.unlock();
                return bArr;
            }
            this.mListLock.unlock();
            return null;
        } finally {
            this.mListLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SynthesisPlaybackQueueItem$ListEntry.class */
    public static final class ListEntry {
        final byte[] mBytes;

        ListEntry(byte[] bytes) {
            this.mBytes = bytes;
        }
    }
}