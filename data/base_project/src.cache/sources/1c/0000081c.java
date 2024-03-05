package android.media.audiofx;

import android.util.Log;

/* loaded from: AutomaticGainControl.class */
public class AutomaticGainControl extends AudioEffect {
    private static final String TAG = "AutomaticGainControl";

    public static boolean isAvailable() {
        return AudioEffect.isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AGC);
    }

    public static AutomaticGainControl create(int audioSession) {
        AutomaticGainControl agc = null;
        try {
            try {
                agc = new AutomaticGainControl(audioSession);
                return agc;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "not implemented on this device " + agc);
                return agc;
            } catch (UnsupportedOperationException e2) {
                Log.w(TAG, "not enough resources");
                return agc;
            } catch (RuntimeException e3) {
                Log.w(TAG, "not enough memory");
                return agc;
            }
        } catch (Throwable th) {
            return agc;
        }
    }

    private AutomaticGainControl(int audioSession) throws IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_AGC, EFFECT_TYPE_NULL, 0, audioSession);
    }
}