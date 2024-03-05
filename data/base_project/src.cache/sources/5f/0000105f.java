package android.support.v4.speech.tts;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;
import java.util.Set;

/* loaded from: TextToSpeechICSMR1.class */
class TextToSpeechICSMR1 {
    public static final String KEY_FEATURE_EMBEDDED_SYNTHESIS = "embeddedTts";
    public static final String KEY_FEATURE_NETWORK_SYNTHESIS = "networkTts";

    /* loaded from: TextToSpeechICSMR1$UtteranceProgressListenerICSMR1.class */
    interface UtteranceProgressListenerICSMR1 {
        void onDone(String str);

        void onError(String str);

        void onStart(String str);
    }

    TextToSpeechICSMR1() {
    }

    static Set<String> getFeatures(TextToSpeech textToSpeech, Locale locale) {
        if (Build.VERSION.SDK_INT >= 15) {
            return textToSpeech.getFeatures(locale);
        }
        return null;
    }

    static void setUtteranceProgressListener(TextToSpeech textToSpeech, UtteranceProgressListenerICSMR1 utteranceProgressListenerICSMR1) {
        if (Build.VERSION.SDK_INT >= 15) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener(utteranceProgressListenerICSMR1) { // from class: android.support.v4.speech.tts.TextToSpeechICSMR1.1
                final UtteranceProgressListenerICSMR1 val$listener;

                {
                    this.val$listener = utteranceProgressListenerICSMR1;
                }

                @Override // android.speech.tts.UtteranceProgressListener
                public void onDone(String str) {
                    this.val$listener.onDone(str);
                }

                @Override // android.speech.tts.UtteranceProgressListener
                public void onError(String str) {
                    this.val$listener.onError(str);
                }

                @Override // android.speech.tts.UtteranceProgressListener
                public void onStart(String str) {
                    this.val$listener.onStart(str);
                }
            });
        } else {
            textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener(utteranceProgressListenerICSMR1) { // from class: android.support.v4.speech.tts.TextToSpeechICSMR1.2
                final UtteranceProgressListenerICSMR1 val$listener;

                {
                    this.val$listener = utteranceProgressListenerICSMR1;
                }

                @Override // android.speech.tts.TextToSpeech.OnUtteranceCompletedListener
                public void onUtteranceCompleted(String str) {
                    this.val$listener.onStart(str);
                    this.val$listener.onDone(str);
                }
            });
        }
    }
}