package android.speech.tts;

import android.speech.tts.TextToSpeechService;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PlaybackQueueItem.class */
public abstract class PlaybackQueueItem implements Runnable {
    private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
    private final Object mCallerIdentity;

    @Override // java.lang.Runnable
    public abstract void run();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void stop(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity) {
        this.mDispatcher = dispatcher;
        this.mCallerIdentity = callerIdentity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object getCallerIdentity() {
        return this.mCallerIdentity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TextToSpeechService.UtteranceProgressDispatcher getDispatcher() {
        return this.mDispatcher;
    }
}