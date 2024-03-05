package android.media.audiofx;

import android.media.audiofx.AudioEffect;
import android.util.Log;
import java.util.StringTokenizer;

/* loaded from: Virtualizer.class */
public class Virtualizer extends AudioEffect {
    private static final String TAG = "Virtualizer";
    public static final int PARAM_STRENGTH_SUPPORTED = 0;
    public static final int PARAM_STRENGTH = 1;
    private boolean mStrengthSupported;
    private OnParameterChangeListener mParamListener;
    private BaseParameterListener mBaseParamListener;
    private final Object mParamListenerLock;

    /* loaded from: Virtualizer$OnParameterChangeListener.class */
    public interface OnParameterChangeListener {
        void onParameterChange(Virtualizer virtualizer, int i, int i2, short s);
    }

    public Virtualizer(int priority, int audioSession) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_VIRTUALIZER, EFFECT_TYPE_NULL, priority, audioSession);
        this.mStrengthSupported = false;
        this.mParamListener = null;
        this.mBaseParamListener = null;
        this.mParamListenerLock = new Object();
        if (audioSession == 0) {
            Log.w(TAG, "WARNING: attaching a Virtualizer to global output mix is deprecated!");
        }
        int[] value = new int[1];
        checkStatus(getParameter(0, value));
        this.mStrengthSupported = value[0] != 0;
    }

    public boolean getStrengthSupported() {
        return this.mStrengthSupported;
    }

    public void setStrength(short strength) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(1, strength));
    }

    public short getRoundedStrength() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        short[] value = new short[1];
        checkStatus(getParameter(1, value));
        return value[0];
    }

    /* loaded from: Virtualizer$BaseParameterListener.class */
    private class BaseParameterListener implements AudioEffect.OnParameterChangeListener {
        private BaseParameterListener() {
        }

        @Override // android.media.audiofx.AudioEffect.OnParameterChangeListener
        public void onParameterChange(AudioEffect effect, int status, byte[] param, byte[] value) {
            OnParameterChangeListener l = null;
            synchronized (Virtualizer.this.mParamListenerLock) {
                if (Virtualizer.this.mParamListener != null) {
                    l = Virtualizer.this.mParamListener;
                }
            }
            if (l != null) {
                int p = -1;
                short v = -1;
                if (param.length == 4) {
                    p = Virtualizer.this.byteArrayToInt(param, 0);
                }
                if (value.length == 2) {
                    v = Virtualizer.this.byteArrayToShort(value, 0);
                }
                if (p != -1 && v != -1) {
                    l.onParameterChange(Virtualizer.this, status, p, v);
                }
            }
        }
    }

    public void setParameterListener(OnParameterChangeListener listener) {
        synchronized (this.mParamListenerLock) {
            if (this.mParamListener == null) {
                this.mParamListener = listener;
                this.mBaseParamListener = new BaseParameterListener();
                super.setParameterListener(this.mBaseParamListener);
            }
        }
    }

    /* loaded from: Virtualizer$Settings.class */
    public static class Settings {
        public short strength;

        public Settings() {
        }

        public Settings(String settings) {
            StringTokenizer st = new StringTokenizer(settings, "=;");
            st.countTokens();
            if (st.countTokens() != 3) {
                throw new IllegalArgumentException("settings: " + settings);
            }
            String key = st.nextToken();
            if (!key.equals(Virtualizer.TAG)) {
                throw new IllegalArgumentException("invalid settings for Virtualizer: " + key);
            }
            try {
                String key2 = st.nextToken();
                if (!key2.equals("strength")) {
                    throw new IllegalArgumentException("invalid key name: " + key2);
                }
                this.strength = Short.parseShort(st.nextToken());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid value for key: " + key);
            }
        }

        public String toString() {
            String str = new String("Virtualizer;strength=" + Short.toString(this.strength));
            return str;
        }
    }

    public Settings getProperties() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        Settings settings = new Settings();
        short[] value = new short[1];
        checkStatus(getParameter(1, value));
        settings.strength = value[0];
        return settings;
    }

    public void setProperties(Settings settings) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(1, settings.strength));
    }
}