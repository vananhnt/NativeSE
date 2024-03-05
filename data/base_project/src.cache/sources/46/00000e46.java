package android.speech.srec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/* loaded from: Recognizer.class */
public final class Recognizer {
    private static String TAG;
    public static final String KEY_CONFIDENCE = "conf";
    public static final String KEY_LITERAL = "literal";
    public static final String KEY_MEANING = "meaning";
    private int mVocabulary;
    private int mRecognizer;
    private Grammar mActiveGrammar = null;
    private byte[] mPutAudioBuffer = null;
    public static final int EVENT_INVALID = 0;
    public static final int EVENT_NO_MATCH = 1;
    public static final int EVENT_INCOMPLETE = 2;
    public static final int EVENT_STARTED = 3;
    public static final int EVENT_STOPPED = 4;
    public static final int EVENT_START_OF_VOICING = 5;
    public static final int EVENT_END_OF_VOICING = 6;
    public static final int EVENT_SPOKE_TOO_SOON = 7;
    public static final int EVENT_RECOGNITION_RESULT = 8;
    public static final int EVENT_START_OF_UTTERANCE_TIMEOUT = 9;
    public static final int EVENT_RECOGNITION_TIMEOUT = 10;
    public static final int EVENT_NEED_MORE_AUDIO = 11;
    public static final int EVENT_MAX_SPEECH = 12;

    private static native void PMemInit();

    private static native void PMemShutdown();

    private static native void SR_SessionCreate(String str);

    private static native void SR_SessionDestroy();

    private static native void SR_RecognizerStart(int i);

    private static native void SR_RecognizerStop(int i);

    private static native int SR_RecognizerCreate();

    private static native void SR_RecognizerDestroy(int i);

    private static native void SR_RecognizerSetup(int i);

    private static native void SR_RecognizerUnsetup(int i);

    private static native boolean SR_RecognizerIsSetup(int i);

    private static native String SR_RecognizerGetParameter(int i, String str);

    private static native int SR_RecognizerGetSize_tParameter(int i, String str);

    private static native boolean SR_RecognizerGetBoolParameter(int i, String str);

    private static native void SR_RecognizerSetParameter(int i, String str, String str2);

    private static native void SR_RecognizerSetSize_tParameter(int i, String str, int i2);

    private static native void SR_RecognizerSetBoolParameter(int i, String str, boolean z);

    private static native void SR_RecognizerSetupRule(int i, int i2, String str);

    private static native boolean SR_RecognizerHasSetupRules(int i);

    private static native void SR_RecognizerActivateRule(int i, int i2, String str, int i3);

    private static native void SR_RecognizerDeactivateRule(int i, int i2, String str);

    private static native void SR_RecognizerDeactivateAllRules(int i);

    private static native boolean SR_RecognizerIsActiveRule(int i, int i2, String str);

    private static native boolean SR_RecognizerCheckGrammarConsistency(int i, int i2);

    private static native int SR_RecognizerPutAudio(int i, byte[] bArr, int i2, int i3, boolean z);

    private static native int SR_RecognizerAdvance(int i);

    private static native boolean SR_RecognizerIsSignalClipping(int i);

    private static native boolean SR_RecognizerIsSignalDCOffset(int i);

    private static native boolean SR_RecognizerIsSignalNoisy(int i);

    private static native boolean SR_RecognizerIsSignalTooQuiet(int i);

    private static native boolean SR_RecognizerIsSignalTooFewSamples(int i);

    private static native boolean SR_RecognizerIsSignalTooManySamples(int i);

    private static native void SR_AcousticStateReset(int i);

    private static native void SR_AcousticStateSet(int i, String str);

    private static native String SR_AcousticStateGet(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarCompile(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarAddWordToSlot(int i, String str, String str2, String str3, int i2, String str4);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarResetAllSlots(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarSetupVocabulary(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarSetupRecognizer(int i, int i2);

    private static native void SR_GrammarUnsetupRecognizer(int i);

    private static native int SR_GrammarCreate();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarDestroy(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int SR_GrammarLoad(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void SR_GrammarSave(int i, String str);

    private static native void SR_GrammarAllowOnly(int i, String str);

    private static native void SR_GrammarAllowAll(int i);

    private static native int SR_VocabularyLoad();

    private static native void SR_VocabularyDestroy(int i);

    private static native String SR_VocabularyGetPronunciation(int i, String str);

    private static native byte[] SR_RecognizerResultGetWaveform(int i);

    private static native int SR_RecognizerResultGetSize(int i);

    private static native int SR_RecognizerResultGetKeyCount(int i, int i2);

    private static native String[] SR_RecognizerResultGetKeyList(int i, int i2);

    private static native String SR_RecognizerResultGetValue(int i, int i2, String str);

    static {
        System.loadLibrary("srec_jni");
        TAG = "Recognizer";
    }

    public static String getConfigDir(Locale locale) {
        if (locale == null) {
            locale = Locale.US;
        }
        String dir = "/system/usr/srec/config/" + locale.toString().replace('_', '.').toLowerCase(Locale.ROOT);
        if (new File(dir).isDirectory()) {
            return dir;
        }
        return null;
    }

    public Recognizer(String configFile) throws IOException {
        this.mVocabulary = 0;
        this.mRecognizer = 0;
        PMemInit();
        SR_SessionCreate(configFile);
        this.mRecognizer = SR_RecognizerCreate();
        SR_RecognizerSetup(this.mRecognizer);
        this.mVocabulary = SR_VocabularyLoad();
    }

    /* loaded from: Recognizer$Grammar.class */
    public class Grammar {
        private int mGrammar;

        public Grammar(String g2gFileName) throws IOException {
            this.mGrammar = 0;
            this.mGrammar = Recognizer.SR_GrammarLoad(g2gFileName);
            Recognizer.SR_GrammarSetupVocabulary(this.mGrammar, Recognizer.this.mVocabulary);
        }

        public void resetAllSlots() {
            Recognizer.SR_GrammarResetAllSlots(this.mGrammar);
        }

        public void addWordToSlot(String slot, String word, String pron, int weight, String tag) {
            Recognizer.SR_GrammarAddWordToSlot(this.mGrammar, slot, word, pron, weight, tag);
        }

        public void compile() {
            Recognizer.SR_GrammarCompile(this.mGrammar);
        }

        public void setupRecognizer() {
            Recognizer.SR_GrammarSetupRecognizer(this.mGrammar, Recognizer.this.mRecognizer);
            Recognizer.this.mActiveGrammar = this;
        }

        public void save(String g2gFileName) throws IOException {
            Recognizer.SR_GrammarSave(this.mGrammar, g2gFileName);
        }

        public void destroy() {
            if (this.mGrammar != 0) {
                Recognizer.SR_GrammarDestroy(this.mGrammar);
                this.mGrammar = 0;
            }
        }

        protected void finalize() {
            if (this.mGrammar != 0) {
                destroy();
                throw new IllegalStateException("someone forgot to destroy Grammar");
            }
        }
    }

    public void start() {
        SR_RecognizerActivateRule(this.mRecognizer, this.mActiveGrammar.mGrammar, "trash", 1);
        SR_RecognizerStart(this.mRecognizer);
    }

    public int advance() {
        return SR_RecognizerAdvance(this.mRecognizer);
    }

    public int putAudio(byte[] buf, int offset, int length, boolean isLast) {
        return SR_RecognizerPutAudio(this.mRecognizer, buf, offset, length, isLast);
    }

    public void putAudio(InputStream audio) throws IOException {
        if (this.mPutAudioBuffer == null) {
            this.mPutAudioBuffer = new byte[512];
        }
        int nbytes = audio.read(this.mPutAudioBuffer);
        if (nbytes == -1) {
            SR_RecognizerPutAudio(this.mRecognizer, this.mPutAudioBuffer, 0, 0, true);
        } else if (nbytes != SR_RecognizerPutAudio(this.mRecognizer, this.mPutAudioBuffer, 0, nbytes, false)) {
            throw new IOException("SR_RecognizerPutAudio failed nbytes=" + nbytes);
        }
    }

    public int getResultCount() {
        return SR_RecognizerResultGetSize(this.mRecognizer);
    }

    public String[] getResultKeys(int index) {
        return SR_RecognizerResultGetKeyList(this.mRecognizer, index);
    }

    public String getResult(int index, String key) {
        return SR_RecognizerResultGetValue(this.mRecognizer, index, key);
    }

    public void stop() {
        SR_RecognizerStop(this.mRecognizer);
        SR_RecognizerDeactivateRule(this.mRecognizer, this.mActiveGrammar.mGrammar, "trash");
    }

    public void resetAcousticState() {
        SR_AcousticStateReset(this.mRecognizer);
    }

    public void setAcousticState(String state) {
        SR_AcousticStateSet(this.mRecognizer, state);
    }

    public String getAcousticState() {
        return SR_AcousticStateGet(this.mRecognizer);
    }

    public void destroy() {
        try {
            if (this.mVocabulary != 0) {
                SR_VocabularyDestroy(this.mVocabulary);
            }
            this.mVocabulary = 0;
            try {
                if (this.mRecognizer != 0) {
                    SR_RecognizerUnsetup(this.mRecognizer);
                }
                try {
                    if (this.mRecognizer != 0) {
                        SR_RecognizerDestroy(this.mRecognizer);
                    }
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                    } finally {
                    }
                } catch (Throwable th) {
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th;
                    } finally {
                    }
                }
            } catch (Throwable th2) {
                try {
                    if (this.mRecognizer != 0) {
                        SR_RecognizerDestroy(this.mRecognizer);
                    }
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th2;
                    } finally {
                    }
                } catch (Throwable th3) {
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th3;
                    } finally {
                    }
                }
            }
        } catch (Throwable th4) {
            this.mVocabulary = 0;
            try {
                if (this.mRecognizer != 0) {
                    SR_RecognizerUnsetup(this.mRecognizer);
                }
                try {
                    if (this.mRecognizer != 0) {
                        SR_RecognizerDestroy(this.mRecognizer);
                    }
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th4;
                    } finally {
                    }
                } catch (Throwable th5) {
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th5;
                    } finally {
                    }
                }
            } catch (Throwable th6) {
                try {
                    if (this.mRecognizer != 0) {
                        SR_RecognizerDestroy(this.mRecognizer);
                    }
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th6;
                    } finally {
                    }
                } catch (Throwable th7) {
                    this.mRecognizer = 0;
                    try {
                        SR_SessionDestroy();
                        throw th7;
                    } finally {
                    }
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        if (this.mVocabulary != 0 || this.mRecognizer != 0) {
            destroy();
            throw new IllegalStateException("someone forgot to destroy Recognizer");
        }
    }

    public static String eventToString(int event) {
        switch (event) {
            case 0:
                return "EVENT_INVALID";
            case 1:
                return "EVENT_NO_MATCH";
            case 2:
                return "EVENT_INCOMPLETE";
            case 3:
                return "EVENT_STARTED";
            case 4:
                return "EVENT_STOPPED";
            case 5:
                return "EVENT_START_OF_VOICING";
            case 6:
                return "EVENT_END_OF_VOICING";
            case 7:
                return "EVENT_SPOKE_TOO_SOON";
            case 8:
                return "EVENT_RECOGNITION_RESULT";
            case 9:
                return "EVENT_START_OF_UTTERANCE_TIMEOUT";
            case 10:
                return "EVENT_RECOGNITION_TIMEOUT";
            case 11:
                return "EVENT_NEED_MORE_AUDIO";
            case 12:
                return "EVENT_MAX_SPEECH";
            default:
                return "EVENT_" + event;
        }
    }
}