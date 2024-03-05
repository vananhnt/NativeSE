package android.speech.tts;

import android.media.AudioTrack;
import android.util.Log;

/* loaded from: BlockingAudioTrack.class */
class BlockingAudioTrack {
    private static final String TAG = "TTS.BlockingAudioTrack";
    private static final boolean DBG = false;
    private static final long MIN_SLEEP_TIME_MS = 20;
    private static final long MAX_SLEEP_TIME_MS = 2500;
    private static final long MAX_PROGRESS_WAIT_MS = 2500;
    private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
    private final int mStreamType;
    private final int mSampleRateInHz;
    private final int mAudioFormat;
    private final int mChannelCount;
    private final float mVolume;
    private final float mPan;
    private final int mBytesPerFrame;
    private int mBytesWritten;
    private Object mAudioTrackLock = new Object();
    private boolean mIsShortUtterance = false;
    private int mAudioBufferSize = 0;
    private AudioTrack mAudioTrack = null;
    private volatile boolean mStopped = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockingAudioTrack(int streamType, int sampleRate, int audioFormat, int channelCount, float volume, float pan) {
        this.mBytesWritten = 0;
        this.mStreamType = streamType;
        this.mSampleRateInHz = sampleRate;
        this.mAudioFormat = audioFormat;
        this.mChannelCount = channelCount;
        this.mVolume = volume;
        this.mPan = pan;
        this.mBytesPerFrame = getBytesPerFrame(this.mAudioFormat) * this.mChannelCount;
        this.mBytesWritten = 0;
    }

    public boolean init() {
        AudioTrack track = createStreamingAudioTrack();
        synchronized (this.mAudioTrackLock) {
            this.mAudioTrack = track;
        }
        if (track == null) {
            return false;
        }
        return true;
    }

    public void stop() {
        synchronized (this.mAudioTrackLock) {
            if (this.mAudioTrack != null) {
                this.mAudioTrack.stop();
            }
            this.mStopped = true;
        }
    }

    public int write(byte[] data) {
        AudioTrack track;
        synchronized (this.mAudioTrackLock) {
            track = this.mAudioTrack;
        }
        if (track == null || this.mStopped) {
            return -1;
        }
        int bytesWritten = writeToAudioTrack(track, data);
        this.mBytesWritten += bytesWritten;
        return bytesWritten;
    }

    public void waitAndRelease() {
        AudioTrack track;
        synchronized (this.mAudioTrackLock) {
            track = this.mAudioTrack;
        }
        if (track == null) {
            return;
        }
        if (this.mBytesWritten < this.mAudioBufferSize && !this.mStopped) {
            this.mIsShortUtterance = true;
            track.stop();
        }
        if (!this.mStopped) {
            blockUntilDone(this.mAudioTrack);
        }
        synchronized (this.mAudioTrackLock) {
            this.mAudioTrack = null;
        }
        track.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getChannelConfig(int channelCount) {
        if (channelCount == 1) {
            return 4;
        }
        if (channelCount == 2) {
            return 12;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getAudioLengthMs(int numBytes) {
        int unconsumedFrames = numBytes / this.mBytesPerFrame;
        long estimatedTimeMs = (unconsumedFrames * 1000) / this.mSampleRateInHz;
        return estimatedTimeMs;
    }

    private static int writeToAudioTrack(AudioTrack audioTrack, byte[] bytes) {
        int count;
        int written;
        if (audioTrack.getPlayState() != 3) {
            audioTrack.play();
        }
        int i = 0;
        while (true) {
            count = i;
            if (count >= bytes.length || (written = audioTrack.write(bytes, count, bytes.length)) <= 0) {
                break;
            }
            i = count + written;
        }
        return count;
    }

    private AudioTrack createStreamingAudioTrack() {
        int channelConfig = getChannelConfig(this.mChannelCount);
        int minBufferSizeInBytes = AudioTrack.getMinBufferSize(this.mSampleRateInHz, channelConfig, this.mAudioFormat);
        int bufferSizeInBytes = Math.max(8192, minBufferSizeInBytes);
        AudioTrack audioTrack = new AudioTrack(this.mStreamType, this.mSampleRateInHz, channelConfig, this.mAudioFormat, bufferSizeInBytes, 1);
        if (audioTrack.getState() != 1) {
            Log.w(TAG, "Unable to create audio track.");
            audioTrack.release();
            return null;
        }
        this.mAudioBufferSize = bufferSizeInBytes;
        setupVolume(audioTrack, this.mVolume, this.mPan);
        return audioTrack;
    }

    private static int getBytesPerFrame(int audioFormat) {
        if (audioFormat == 3) {
            return 1;
        }
        if (audioFormat == 2) {
            return 2;
        }
        return -1;
    }

    private void blockUntilDone(AudioTrack audioTrack) {
        if (this.mBytesWritten <= 0) {
            return;
        }
        if (this.mIsShortUtterance) {
            blockUntilEstimatedCompletion();
        } else {
            blockUntilCompletion(audioTrack);
        }
    }

    private void blockUntilEstimatedCompletion() {
        int lengthInFrames = this.mBytesWritten / this.mBytesPerFrame;
        long estimatedTimeMs = (lengthInFrames * 1000) / this.mSampleRateInHz;
        try {
            Thread.sleep(estimatedTimeMs);
        } catch (InterruptedException e) {
        }
    }

    private void blockUntilCompletion(AudioTrack audioTrack) {
        int lengthInFrames = this.mBytesWritten / this.mBytesPerFrame;
        int previousPosition = -1;
        long blockedTimeMs = 0;
        while (true) {
            int currentPosition = audioTrack.getPlaybackHeadPosition();
            if (currentPosition < lengthInFrames && audioTrack.getPlayState() == 3 && !this.mStopped) {
                long estimatedTimeMs = ((lengthInFrames - currentPosition) * 1000) / audioTrack.getSampleRate();
                long sleepTimeMs = clip(estimatedTimeMs, (long) MIN_SLEEP_TIME_MS, 2500L);
                if (currentPosition == previousPosition) {
                    blockedTimeMs += sleepTimeMs;
                    if (blockedTimeMs > 2500) {
                        Log.w(TAG, "Waited unsuccessfully for 2500ms for AudioTrack to make progress, Aborting");
                        return;
                    }
                } else {
                    blockedTimeMs = 0;
                }
                previousPosition = currentPosition;
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private static void setupVolume(AudioTrack audioTrack, float volume, float pan) {
        float vol = clip(volume, 0.0f, 1.0f);
        float panning = clip(pan, -1.0f, 1.0f);
        float volLeft = vol;
        float volRight = vol;
        if (panning > 0.0f) {
            volLeft *= 1.0f - panning;
        } else if (panning < 0.0f) {
            volRight *= 1.0f + panning;
        }
        if (audioTrack.setStereoVolume(volLeft, volRight) != 0) {
            Log.e(TAG, "Failed to set volume");
        }
    }

    private static final long clip(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static float clip(float value, float min, float max) {
        return value > max ? max : value < min ? min : value;
    }
}