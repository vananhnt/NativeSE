package android.media.audiofx;

import android.util.Log;

/* loaded from: NoiseSuppressor.class */
public class NoiseSuppressor extends AudioEffect {
    private static final String TAG = "NoiseSuppressor";

    public static boolean isAvailable() {
        return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
    }

    public static NoiseSuppressor create(int audioSession) {
        NoiseSuppressor ns = null;
        try {
            try {
                ns = new NoiseSuppressor(audioSession);
                return ns;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "not implemented on this device " + ns);
                return ns;
            } catch (UnsupportedOperationException e2) {
                Log.w(TAG, "not enough resources");
                return ns;
            } catch (RuntimeException e3) {
                Log.w(TAG, "not enough memory");
                return ns;
            }
        } catch (Throwable th) {
            return ns;
        }
    }

    private NoiseSuppressor(int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_NS, EFFECT_TYPE_NULL, 0, audioSession);
    }
}