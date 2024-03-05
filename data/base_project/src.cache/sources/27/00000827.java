package android.media.audiofx;

import android.media.audiofx.AudioEffect;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/* loaded from: Equalizer.class */
public class Equalizer extends AudioEffect {
    private static final String TAG = "Equalizer";
    public static final int PARAM_NUM_BANDS = 0;
    public static final int PARAM_LEVEL_RANGE = 1;
    public static final int PARAM_BAND_LEVEL = 2;
    public static final int PARAM_CENTER_FREQ = 3;
    public static final int PARAM_BAND_FREQ_RANGE = 4;
    public static final int PARAM_GET_BAND = 5;
    public static final int PARAM_CURRENT_PRESET = 6;
    public static final int PARAM_GET_NUM_OF_PRESETS = 7;
    public static final int PARAM_GET_PRESET_NAME = 8;
    private static final int PARAM_PROPERTIES = 9;
    public static final int PARAM_STRING_SIZE_MAX = 32;
    private short mNumBands;
    private int mNumPresets;
    private String[] mPresetNames;
    private OnParameterChangeListener mParamListener;
    private BaseParameterListener mBaseParamListener;
    private final Object mParamListenerLock;

    /* loaded from: Equalizer$OnParameterChangeListener.class */
    public interface OnParameterChangeListener {
        void onParameterChange(Equalizer equalizer, int i, int i2, int i3, int i4);
    }

    public Equalizer(int priority, int audioSession) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException, RuntimeException {
        super(EFFECT_TYPE_EQUALIZER, EFFECT_TYPE_NULL, priority, audioSession);
        this.mNumBands = (short) 0;
        this.mParamListener = null;
        this.mBaseParamListener = null;
        this.mParamListenerLock = new Object();
        if (audioSession == 0) {
            Log.w(TAG, "WARNING: attaching an Equalizer to global output mix is deprecated!");
        }
        getNumberOfBands();
        this.mNumPresets = getNumberOfPresets();
        if (this.mNumPresets != 0) {
            this.mPresetNames = new String[this.mNumPresets];
            byte[] value = new byte[32];
            int[] param = new int[2];
            param[0] = 8;
            for (int i = 0; i < this.mNumPresets; i++) {
                param[1] = i;
                checkStatus(getParameter(param, value));
                int length = 0;
                while (value[length] != 0) {
                    length++;
                }
                try {
                    this.mPresetNames[i] = new String(value, 0, length, "ISO-8859-1");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "preset name decode error");
                }
            }
        }
    }

    public short getNumberOfBands() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        if (this.mNumBands != 0) {
            return this.mNumBands;
        }
        int[] param = {0};
        short[] result = new short[1];
        checkStatus(getParameter(param, result));
        this.mNumBands = result[0];
        return this.mNumBands;
    }

    public short[] getBandLevelRange() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        short[] result = new short[2];
        checkStatus(getParameter(1, result));
        return result;
    }

    public void setBandLevel(short band, short level) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        int[] param = {2, band};
        short[] value = {level};
        checkStatus(setParameter(param, value));
    }

    public short getBandLevel(short band) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        short[] result = new short[1];
        int[] param = {2, band};
        checkStatus(getParameter(param, result));
        return result[0];
    }

    public int getCenterFreq(short band) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        int[] result = new int[1];
        int[] param = {3, band};
        checkStatus(getParameter(param, result));
        return result[0];
    }

    public int[] getBandFreqRange(short band) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        int[] result = new int[2];
        int[] param = {4, band};
        checkStatus(getParameter(param, result));
        return result;
    }

    public short getBand(int frequency) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        short[] result = new short[1];
        int[] param = {5, frequency};
        checkStatus(getParameter(param, result));
        return result[0];
    }

    public short getCurrentPreset() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        short[] result = new short[1];
        checkStatus(getParameter(6, result));
        return result[0];
    }

    public void usePreset(short preset) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        checkStatus(setParameter(6, preset));
    }

    public short getNumberOfPresets() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        short[] result = new short[1];
        checkStatus(getParameter(7, result));
        return result[0];
    }

    public String getPresetName(short preset) {
        if (preset >= 0 && preset < this.mNumPresets) {
            return this.mPresetNames[preset];
        }
        return "";
    }

    /* loaded from: Equalizer$BaseParameterListener.class */
    private class BaseParameterListener implements AudioEffect.OnParameterChangeListener {
        private BaseParameterListener() {
        }

        @Override // android.media.audiofx.AudioEffect.OnParameterChangeListener
        public void onParameterChange(AudioEffect effect, int status, byte[] param, byte[] value) {
            OnParameterChangeListener l = null;
            synchronized (Equalizer.this.mParamListenerLock) {
                if (Equalizer.this.mParamListener != null) {
                    l = Equalizer.this.mParamListener;
                }
            }
            if (l != null) {
                int p1 = -1;
                int p2 = -1;
                int v = -1;
                if (param.length >= 4) {
                    p1 = Equalizer.this.byteArrayToInt(param, 0);
                    if (param.length >= 8) {
                        p2 = Equalizer.this.byteArrayToInt(param, 4);
                    }
                }
                if (value.length == 2) {
                    v = Equalizer.this.byteArrayToShort(value, 0);
                } else if (value.length == 4) {
                    v = Equalizer.this.byteArrayToInt(value, 0);
                }
                if (p1 != -1 && v != -1) {
                    l.onParameterChange(Equalizer.this, status, p1, p2, v);
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

    /* loaded from: Equalizer$Settings.class */
    public static class Settings {
        public short curPreset;
        public short numBands;
        public short[] bandLevels;

        public Settings() {
            this.numBands = (short) 0;
            this.bandLevels = null;
        }

        public Settings(String settings) {
            this.numBands = (short) 0;
            this.bandLevels = null;
            StringTokenizer st = new StringTokenizer(settings, "=;");
            st.countTokens();
            if (st.countTokens() < 5) {
                throw new IllegalArgumentException("settings: " + settings);
            }
            String key = st.nextToken();
            if (!key.equals(Equalizer.TAG)) {
                throw new IllegalArgumentException("invalid settings for Equalizer: " + key);
            }
            try {
                String key2 = st.nextToken();
                if (!key2.equals("curPreset")) {
                    throw new IllegalArgumentException("invalid key name: " + key2);
                }
                this.curPreset = Short.parseShort(st.nextToken());
                String key3 = st.nextToken();
                if (!key3.equals("numBands")) {
                    throw new IllegalArgumentException("invalid key name: " + key3);
                }
                this.numBands = Short.parseShort(st.nextToken());
                if (st.countTokens() != this.numBands * 2) {
                    throw new IllegalArgumentException("settings: " + settings);
                }
                this.bandLevels = new short[this.numBands];
                for (int i = 0; i < this.numBands; i++) {
                    String key4 = st.nextToken();
                    if (!key4.equals("band" + (i + 1) + "Level")) {
                        throw new IllegalArgumentException("invalid key name: " + key4);
                    }
                    this.bandLevels[i] = Short.parseShort(st.nextToken());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("invalid value for key: " + key);
            }
        }

        public String toString() {
            String str = new String("Equalizer;curPreset=" + Short.toString(this.curPreset) + ";numBands=" + Short.toString(this.numBands));
            for (int i = 0; i < this.numBands; i++) {
                str = str.concat(";band" + (i + 1) + "Level=" + Short.toString(this.bandLevels[i]));
            }
            return str;
        }
    }

    public Settings getProperties() throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        byte[] param = new byte[4 + (this.mNumBands * 2)];
        checkStatus(getParameter(9, param));
        Settings settings = new Settings();
        settings.curPreset = byteArrayToShort(param, 0);
        settings.numBands = byteArrayToShort(param, 2);
        settings.bandLevels = new short[this.mNumBands];
        for (int i = 0; i < this.mNumBands; i++) {
            settings.bandLevels[i] = byteArrayToShort(param, 4 + (2 * i));
        }
        return settings;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v13, types: [byte[], byte[][]] */
    /* JADX WARN: Type inference failed for: r1v7, types: [byte[], byte[][]] */
    public void setProperties(Settings settings) throws IllegalStateException, IllegalArgumentException, UnsupportedOperationException {
        if (settings.numBands != settings.bandLevels.length || settings.numBands != this.mNumBands) {
            throw new IllegalArgumentException("settings invalid band count: " + ((int) settings.numBands));
        }
        byte[] param = concatArrays(new byte[]{shortToByteArray(settings.curPreset), shortToByteArray(this.mNumBands)});
        for (int i = 0; i < this.mNumBands; i++) {
            param = concatArrays(new byte[]{param, shortToByteArray(settings.bandLevels[i])});
        }
        checkStatus(setParameter(9, param));
    }
}