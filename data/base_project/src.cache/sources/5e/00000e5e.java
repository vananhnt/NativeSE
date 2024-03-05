package android.speech.tts;

/* loaded from: SynthesisCallback.class */
public interface SynthesisCallback {
    int getMaxBufferSize();

    int start(int i, int i2, int i3);

    int audioAvailable(byte[] bArr, int i, int i2);

    int done();

    void error();
}