package android.speech.tts;

import android.speech.tts.TextToSpeech;

/* loaded from: UtteranceProgressListener.class */
public abstract class UtteranceProgressListener {
    public abstract void onStart(String str);

    public abstract void onDone(String str);

    public abstract void onError(String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UtteranceProgressListener from(final TextToSpeech.OnUtteranceCompletedListener listener) {
        return new UtteranceProgressListener() { // from class: android.speech.tts.UtteranceProgressListener.1
            @Override // android.speech.tts.UtteranceProgressListener
            public synchronized void onDone(String utteranceId) {
                TextToSpeech.OnUtteranceCompletedListener.this.onUtteranceCompleted(utteranceId);
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onError(String utteranceId) {
                TextToSpeech.OnUtteranceCompletedListener.this.onUtteranceCompleted(utteranceId);
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onStart(String utteranceId) {
            }
        };
    }
}