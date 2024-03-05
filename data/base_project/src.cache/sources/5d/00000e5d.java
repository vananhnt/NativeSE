package android.speech.tts;

import android.os.ConditionVariable;
import android.speech.tts.TextToSpeechService;

/* loaded from: SilencePlaybackQueueItem.class */
class SilencePlaybackQueueItem extends PlaybackQueueItem {
    private final ConditionVariable mCondVar;
    private final long mSilenceDurationMs;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SilencePlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, long silenceDurationMs) {
        super(dispatcher, callerIdentity);
        this.mCondVar = new ConditionVariable();
        this.mSilenceDurationMs = silenceDurationMs;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        getDispatcher().dispatchOnStart();
        if (this.mSilenceDurationMs > 0) {
            this.mCondVar.block(this.mSilenceDurationMs);
        }
        getDispatcher().dispatchOnDone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.PlaybackQueueItem
    public void stop(boolean isError) {
        this.mCondVar.open();
    }
}