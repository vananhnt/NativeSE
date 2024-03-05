package android.speech.tts;

import android.os.SystemClock;
import android.text.TextUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: EventLogger.class */
public class EventLogger {
    private final SynthesisRequest mRequest;
    private final String mServiceApp;
    private final int mCallerUid;
    private final int mCallerPid;
    private long mPlaybackStartTime = -1;
    private volatile long mRequestProcessingStartTime = -1;
    private volatile long mEngineStartTime = -1;
    private volatile long mEngineCompleteTime = -1;
    private volatile boolean mError = false;
    private volatile boolean mStopped = false;
    private boolean mLogWritten = false;
    private final long mReceivedTime = SystemClock.elapsedRealtime();

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventLogger(SynthesisRequest request, int callerUid, int callerPid, String serviceApp) {
        this.mRequest = request;
        this.mCallerUid = callerUid;
        this.mCallerPid = callerPid;
        this.mServiceApp = serviceApp;
    }

    public void onRequestProcessingStart() {
        this.mRequestProcessingStartTime = SystemClock.elapsedRealtime();
    }

    public void onEngineDataReceived() {
        if (this.mEngineStartTime == -1) {
            this.mEngineStartTime = SystemClock.elapsedRealtime();
        }
    }

    public void onEngineComplete() {
        this.mEngineCompleteTime = SystemClock.elapsedRealtime();
    }

    public void onAudioDataWritten() {
        if (this.mPlaybackStartTime == -1) {
            this.mPlaybackStartTime = SystemClock.elapsedRealtime();
        }
    }

    public void onStopped() {
        this.mStopped = false;
    }

    public void onError() {
        this.mError = true;
    }

    public void onWriteData() {
        if (this.mLogWritten) {
            return;
        }
        this.mLogWritten = true;
        SystemClock.elapsedRealtime();
        if (this.mError || this.mPlaybackStartTime == -1 || this.mEngineCompleteTime == -1) {
            EventLogTags.writeTtsSpeakFailure(this.mServiceApp, this.mCallerUid, this.mCallerPid, getUtteranceLength(), getLocaleString(), this.mRequest.getSpeechRate(), this.mRequest.getPitch());
        } else if (this.mStopped) {
        } else {
            long audioLatency = this.mPlaybackStartTime - this.mReceivedTime;
            long engineLatency = this.mEngineStartTime - this.mRequestProcessingStartTime;
            long engineTotal = this.mEngineCompleteTime - this.mRequestProcessingStartTime;
            EventLogTags.writeTtsSpeakSuccess(this.mServiceApp, this.mCallerUid, this.mCallerPid, getUtteranceLength(), getLocaleString(), this.mRequest.getSpeechRate(), this.mRequest.getPitch(), engineLatency, engineTotal, audioLatency);
        }
    }

    private int getUtteranceLength() {
        String utterance = this.mRequest.getText();
        if (utterance == null) {
            return 0;
        }
        return utterance.length();
    }

    private String getLocaleString() {
        StringBuilder sb = new StringBuilder(this.mRequest.getLanguage());
        if (!TextUtils.isEmpty(this.mRequest.getCountry())) {
            sb.append('-');
            sb.append(this.mRequest.getCountry());
            if (!TextUtils.isEmpty(this.mRequest.getVariant())) {
                sb.append('-');
                sb.append(this.mRequest.getVariant());
            }
        }
        return sb.toString();
    }
}