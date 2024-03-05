package android.speech;

import android.os.Bundle;

/* loaded from: RecognitionListener.class */
public interface RecognitionListener {
    void onReadyForSpeech(Bundle bundle);

    void onBeginningOfSpeech();

    void onRmsChanged(float f);

    void onBufferReceived(byte[] bArr);

    void onEndOfSpeech();

    void onError(int i);

    void onResults(Bundle bundle);

    void onPartialResults(Bundle bundle);

    void onEvent(int i, Bundle bundle);
}