package android.speech.tts;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.speech.tts.ITextToSpeechCallback;
import android.speech.tts.ITextToSpeechService;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

/* loaded from: TextToSpeech.class */
public class TextToSpeech {
    private static final String TAG = "TextToSpeech";
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int QUEUE_FLUSH = 0;
    public static final int QUEUE_ADD = 1;
    static final int QUEUE_DESTROY = 2;
    public static final int LANG_COUNTRY_VAR_AVAILABLE = 2;
    public static final int LANG_COUNTRY_AVAILABLE = 1;
    public static final int LANG_AVAILABLE = 0;
    public static final int LANG_MISSING_DATA = -1;
    public static final int LANG_NOT_SUPPORTED = -2;
    public static final String ACTION_TTS_QUEUE_PROCESSING_COMPLETED = "android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED";
    private final Context mContext;
    private Connection mConnectingServiceConnection;
    private Connection mServiceConnection;
    private OnInitListener mInitListener;
    private volatile UtteranceProgressListener mUtteranceProgressListener;
    private final Object mStartLock;
    private String mRequestedEngine;
    private final boolean mUseFallback;
    private final Map<String, Uri> mEarcons;
    private final Map<String, Uri> mUtterances;
    private final Bundle mParams;
    private final TtsEngines mEnginesHelper;
    private final String mPackageName;
    private volatile String mCurrentEngine;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextToSpeech$Action.class */
    public interface Action<R> {
        R run(ITextToSpeechService iTextToSpeechService) throws RemoteException;
    }

    /* loaded from: TextToSpeech$OnInitListener.class */
    public interface OnInitListener {
        void onInit(int i);
    }

    @Deprecated
    /* loaded from: TextToSpeech$OnUtteranceCompletedListener.class */
    public interface OnUtteranceCompletedListener {
        void onUtteranceCompleted(String str);
    }

    /* loaded from: TextToSpeech$Engine.class */
    public class Engine {
        public static final int DEFAULT_RATE = 100;
        public static final int DEFAULT_PITCH = 100;
        public static final float DEFAULT_VOLUME = 1.0f;
        public static final float DEFAULT_PAN = 0.0f;
        public static final int USE_DEFAULTS = 0;
        @Deprecated
        public static final String DEFAULT_ENGINE = "com.svox.pico";
        public static final int DEFAULT_STREAM = 3;
        public static final int CHECK_VOICE_DATA_PASS = 1;
        public static final int CHECK_VOICE_DATA_FAIL = 0;
        @Deprecated
        public static final int CHECK_VOICE_DATA_BAD_DATA = -1;
        @Deprecated
        public static final int CHECK_VOICE_DATA_MISSING_DATA = -2;
        @Deprecated
        public static final int CHECK_VOICE_DATA_MISSING_VOLUME = -3;
        public static final String INTENT_ACTION_TTS_SERVICE = "android.intent.action.TTS_SERVICE";
        public static final String SERVICE_META_DATA = "android.speech.tts";
        public static final String ACTION_INSTALL_TTS_DATA = "android.speech.tts.engine.INSTALL_TTS_DATA";
        public static final String ACTION_TTS_DATA_INSTALLED = "android.speech.tts.engine.TTS_DATA_INSTALLED";
        public static final String ACTION_CHECK_TTS_DATA = "android.speech.tts.engine.CHECK_TTS_DATA";
        public static final String ACTION_GET_SAMPLE_TEXT = "android.speech.tts.engine.GET_SAMPLE_TEXT";
        public static final String EXTRA_SAMPLE_TEXT = "sampleText";
        public static final String EXTRA_AVAILABLE_VOICES = "availableVoices";
        public static final String EXTRA_UNAVAILABLE_VOICES = "unavailableVoices";
        @Deprecated
        public static final String EXTRA_VOICE_DATA_ROOT_DIRECTORY = "dataRoot";
        @Deprecated
        public static final String EXTRA_VOICE_DATA_FILES = "dataFiles";
        @Deprecated
        public static final String EXTRA_VOICE_DATA_FILES_INFO = "dataFilesInfo";
        @Deprecated
        public static final String EXTRA_CHECK_VOICE_DATA_FOR = "checkVoiceDataFor";
        @Deprecated
        public static final String EXTRA_TTS_DATA_INSTALLED = "dataInstalled";
        public static final String KEY_PARAM_RATE = "rate";
        public static final String KEY_PARAM_LANGUAGE = "language";
        public static final String KEY_PARAM_COUNTRY = "country";
        public static final String KEY_PARAM_VARIANT = "variant";
        public static final String KEY_PARAM_ENGINE = "engine";
        public static final String KEY_PARAM_PITCH = "pitch";
        public static final String KEY_PARAM_STREAM = "streamType";
        public static final String KEY_PARAM_UTTERANCE_ID = "utteranceId";
        public static final String KEY_PARAM_VOLUME = "volume";
        public static final String KEY_PARAM_PAN = "pan";
        public static final String KEY_FEATURE_NETWORK_SYNTHESIS = "networkTts";
        public static final String KEY_FEATURE_EMBEDDED_SYNTHESIS = "embeddedTts";

        public Engine() {
        }
    }

    public TextToSpeech(Context context, OnInitListener listener) {
        this(context, listener, null);
    }

    public TextToSpeech(Context context, OnInitListener listener, String engine) {
        this(context, listener, engine, null, true);
    }

    public TextToSpeech(Context context, OnInitListener listener, String engine, String packageName, boolean useFallback) {
        this.mStartLock = new Object();
        this.mParams = new Bundle();
        this.mCurrentEngine = null;
        this.mContext = context;
        this.mInitListener = listener;
        this.mRequestedEngine = engine;
        this.mUseFallback = useFallback;
        this.mEarcons = new HashMap();
        this.mUtterances = new HashMap();
        this.mUtteranceProgressListener = null;
        this.mEnginesHelper = new TtsEngines(this.mContext);
        if (packageName != null) {
            this.mPackageName = packageName;
        } else {
            this.mPackageName = this.mContext.getPackageName();
        }
        initTts();
    }

    private <R> R runActionNoReconnect(Action<R> action, R errorResult, String method, boolean onlyEstablishedConnection) {
        return (R) runAction(action, errorResult, method, false, onlyEstablishedConnection);
    }

    private <R> R runAction(Action<R> action, R errorResult, String method) {
        return (R) runAction(action, errorResult, method, true, true);
    }

    private <R> R runAction(Action<R> action, R errorResult, String method, boolean reconnect, boolean onlyEstablishedConnection) {
        synchronized (this.mStartLock) {
            if (this.mServiceConnection == null) {
                Log.w(TAG, method + " failed: not bound to TTS engine");
                return errorResult;
            }
            return (R) this.mServiceConnection.runAction(action, errorResult, method, reconnect, onlyEstablishedConnection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int initTts() {
        if (this.mRequestedEngine != null) {
            if (this.mEnginesHelper.isEngineInstalled(this.mRequestedEngine)) {
                if (connectToEngine(this.mRequestedEngine)) {
                    this.mCurrentEngine = this.mRequestedEngine;
                    return 0;
                } else if (!this.mUseFallback) {
                    this.mCurrentEngine = null;
                    dispatchOnInit(-1);
                    return -1;
                }
            } else if (!this.mUseFallback) {
                Log.i(TAG, "Requested engine not installed: " + this.mRequestedEngine);
                this.mCurrentEngine = null;
                dispatchOnInit(-1);
                return -1;
            }
        }
        String defaultEngine = getDefaultEngine();
        if (defaultEngine != null && !defaultEngine.equals(this.mRequestedEngine) && connectToEngine(defaultEngine)) {
            this.mCurrentEngine = defaultEngine;
            return 0;
        }
        String highestRanked = this.mEnginesHelper.getHighestRankedEngineName();
        if (highestRanked != null && !highestRanked.equals(this.mRequestedEngine) && !highestRanked.equals(defaultEngine) && connectToEngine(highestRanked)) {
            this.mCurrentEngine = highestRanked;
            return 0;
        }
        this.mCurrentEngine = null;
        dispatchOnInit(-1);
        return -1;
    }

    private boolean connectToEngine(String engine) {
        Connection connection = new Connection();
        Intent intent = new Intent(Engine.INTENT_ACTION_TTS_SERVICE);
        intent.setPackage(engine);
        boolean bound = this.mContext.bindService(intent, connection, 1);
        if (!bound) {
            Log.e(TAG, "Failed to bind to " + engine);
            return false;
        }
        Log.i(TAG, "Sucessfully bound to " + engine);
        this.mConnectingServiceConnection = connection;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnInit(int result) {
        synchronized (this.mStartLock) {
            if (this.mInitListener != null) {
                this.mInitListener.onInit(result);
                this.mInitListener = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IBinder getCallerIdentity() {
        return this.mServiceConnection.getCallerIdentity();
    }

    public void shutdown() {
        synchronized (this.mStartLock) {
            if (this.mConnectingServiceConnection != null) {
                this.mContext.unbindService(this.mConnectingServiceConnection);
                this.mConnectingServiceConnection = null;
                return;
            }
            runActionNoReconnect(new Action<Void>() { // from class: android.speech.tts.TextToSpeech.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.speech.tts.TextToSpeech.Action
                public Void run(ITextToSpeechService service) throws RemoteException {
                    service.setCallback(TextToSpeech.this.getCallerIdentity(), null);
                    service.stop(TextToSpeech.this.getCallerIdentity());
                    TextToSpeech.this.mServiceConnection.disconnect();
                    TextToSpeech.this.mServiceConnection = null;
                    TextToSpeech.this.mCurrentEngine = null;
                    return null;
                }
            }, null, "shutdown", false);
        }
    }

    public int addSpeech(String text, String packagename, int resourceId) {
        synchronized (this.mStartLock) {
            this.mUtterances.put(text, makeResourceUri(packagename, resourceId));
        }
        return 0;
    }

    public int addSpeech(String text, String filename) {
        synchronized (this.mStartLock) {
            this.mUtterances.put(text, Uri.parse(filename));
        }
        return 0;
    }

    public int addEarcon(String earcon, String packagename, int resourceId) {
        synchronized (this.mStartLock) {
            this.mEarcons.put(earcon, makeResourceUri(packagename, resourceId));
        }
        return 0;
    }

    public int addEarcon(String earcon, String filename) {
        synchronized (this.mStartLock) {
            this.mEarcons.put(earcon, Uri.parse(filename));
        }
        return 0;
    }

    private Uri makeResourceUri(String packageName, int resourceId) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).encodedAuthority(packageName).appendEncodedPath(String.valueOf(resourceId)).build();
    }

    public int speak(final String text, final int queueMode, final HashMap<String, String> params) {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                Uri utteranceUri = (Uri) TextToSpeech.this.mUtterances.get(text);
                if (utteranceUri != null) {
                    return Integer.valueOf(service.playAudio(TextToSpeech.this.getCallerIdentity(), utteranceUri, queueMode, TextToSpeech.this.getParams(params)));
                }
                return Integer.valueOf(service.speak(TextToSpeech.this.getCallerIdentity(), text, queueMode, TextToSpeech.this.getParams(params)));
            }
        }, -1, "speak")).intValue();
    }

    public int playEarcon(final String earcon, final int queueMode, final HashMap<String, String> params) {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                Uri earconUri = (Uri) TextToSpeech.this.mEarcons.get(earcon);
                if (earconUri == null) {
                    return -1;
                }
                return Integer.valueOf(service.playAudio(TextToSpeech.this.getCallerIdentity(), earconUri, queueMode, TextToSpeech.this.getParams(params)));
            }
        }, -1, "playEarcon")).intValue();
    }

    public int playSilence(final long durationInMs, final int queueMode, final HashMap<String, String> params) {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.4
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                return Integer.valueOf(service.playSilence(TextToSpeech.this.getCallerIdentity(), durationInMs, queueMode, TextToSpeech.this.getParams(params)));
            }
        }, -1, "playSilence")).intValue();
    }

    public Set<String> getFeatures(final Locale locale) {
        return (Set) runAction(new Action<Set<String>>() { // from class: android.speech.tts.TextToSpeech.5
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Set<String> run(ITextToSpeechService service) throws RemoteException {
                String[] features = service.getFeaturesForLanguage(locale.getISO3Language(), locale.getISO3Country(), locale.getVariant());
                if (features != null) {
                    Set<String> featureSet = new HashSet<>();
                    Collections.addAll(featureSet, features);
                    return featureSet;
                }
                return null;
            }
        }, null, "getFeatures");
    }

    public boolean isSpeaking() {
        return ((Boolean) runAction(new Action<Boolean>() { // from class: android.speech.tts.TextToSpeech.6
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Boolean run(ITextToSpeechService service) throws RemoteException {
                return Boolean.valueOf(service.isSpeaking());
            }
        }, false, "isSpeaking")).booleanValue();
    }

    public int stop() {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.7
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                return Integer.valueOf(service.stop(TextToSpeech.this.getCallerIdentity()));
            }
        }, -1, "stop")).intValue();
    }

    public int setSpeechRate(float speechRate) {
        int intRate;
        if (speechRate > 0.0f && (intRate = (int) (speechRate * 100.0f)) > 0) {
            synchronized (this.mStartLock) {
                this.mParams.putInt(Engine.KEY_PARAM_RATE, intRate);
            }
            return 0;
        }
        return -1;
    }

    public int setPitch(float pitch) {
        int intPitch;
        if (pitch > 0.0f && (intPitch = (int) (pitch * 100.0f)) > 0) {
            synchronized (this.mStartLock) {
                this.mParams.putInt(Engine.KEY_PARAM_PITCH, intPitch);
            }
            return 0;
        }
        return -1;
    }

    public String getCurrentEngine() {
        return this.mCurrentEngine;
    }

    public Locale getDefaultLanguage() {
        return (Locale) runAction(new Action<Locale>() { // from class: android.speech.tts.TextToSpeech.8
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Locale run(ITextToSpeechService service) throws RemoteException {
                String[] defaultLanguage = service.getClientDefaultLanguage();
                return new Locale(defaultLanguage[0], defaultLanguage[1], defaultLanguage[2]);
            }
        }, null, "getDefaultLanguage");
    }

    public int setLanguage(final Locale loc) {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.9
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                if (loc == null) {
                    return -2;
                }
                try {
                    String language = loc.getISO3Language();
                    try {
                        String country = loc.getISO3Country();
                        String variant = loc.getVariant();
                        int result = service.loadLanguage(TextToSpeech.this.getCallerIdentity(), language, country, variant);
                        if (result >= 0) {
                            if (result < 2) {
                                variant = "";
                                if (result < 1) {
                                    country = "";
                                }
                            }
                            TextToSpeech.this.mParams.putString("language", language);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_COUNTRY, country);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_VARIANT, variant);
                        }
                        return Integer.valueOf(result);
                    } catch (MissingResourceException e) {
                        Log.w(TextToSpeech.TAG, "Couldn't retrieve ISO 3166 country code for locale: " + loc, e);
                        return -2;
                    }
                } catch (MissingResourceException e2) {
                    Log.w(TextToSpeech.TAG, "Couldn't retrieve ISO 639-2/T language code for locale: " + loc, e2);
                    return -2;
                }
            }
        }, -2, "setLanguage")).intValue();
    }

    public Locale getLanguage() {
        return (Locale) runAction(new Action<Locale>() { // from class: android.speech.tts.TextToSpeech.10
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Locale run(ITextToSpeechService service) {
                String lang = TextToSpeech.this.mParams.getString("language", "");
                String country = TextToSpeech.this.mParams.getString(Engine.KEY_PARAM_COUNTRY, "");
                String variant = TextToSpeech.this.mParams.getString(Engine.KEY_PARAM_VARIANT, "");
                return new Locale(lang, country, variant);
            }
        }, null, "getLanguage");
    }

    public int isLanguageAvailable(final Locale loc) {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.11
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                try {
                    String language = loc.getISO3Language();
                    try {
                        String country = loc.getISO3Country();
                        return Integer.valueOf(service.isLanguageAvailable(language, country, loc.getVariant()));
                    } catch (MissingResourceException e) {
                        Log.w(TextToSpeech.TAG, "Couldn't retrieve ISO 3166 country code for locale: " + loc, e);
                        return -2;
                    }
                } catch (MissingResourceException e2) {
                    Log.w(TextToSpeech.TAG, "Couldn't retrieve ISO 639-2/T language code for locale: " + loc, e2);
                    return -2;
                }
            }
        }, -2, "isLanguageAvailable")).intValue();
    }

    public int synthesizeToFile(final String text, final HashMap<String, String> params, final String filename) {
        return ((Integer) runAction(new Action<Integer>() { // from class: android.speech.tts.TextToSpeech.12
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.speech.tts.TextToSpeech.Action
            public Integer run(ITextToSpeechService service) throws RemoteException {
                try {
                    File file = new File(filename);
                    if (file.exists() && !file.canWrite()) {
                        Log.e(TextToSpeech.TAG, "Can't write to " + filename);
                        return -1;
                    }
                    ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, 738197504);
                    int returnValue = service.synthesizeToFileDescriptor(TextToSpeech.this.getCallerIdentity(), text, fileDescriptor, TextToSpeech.this.getParams(params));
                    fileDescriptor.close();
                    return Integer.valueOf(returnValue);
                } catch (FileNotFoundException e) {
                    Log.e(TextToSpeech.TAG, "Opening file " + filename + " failed", e);
                    return -1;
                } catch (IOException e2) {
                    Log.e(TextToSpeech.TAG, "Closing file " + filename + " failed", e2);
                    return -1;
                }
            }
        }, -1, "synthesizeToFile")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bundle getParams(HashMap<String, String> params) {
        if (params != null && !params.isEmpty()) {
            Bundle bundle = new Bundle(this.mParams);
            copyIntParam(bundle, params, Engine.KEY_PARAM_STREAM);
            copyStringParam(bundle, params, Engine.KEY_PARAM_UTTERANCE_ID);
            copyFloatParam(bundle, params, "volume");
            copyFloatParam(bundle, params, Engine.KEY_PARAM_PAN);
            copyStringParam(bundle, params, "networkTts");
            copyStringParam(bundle, params, "embeddedTts");
            if (!TextUtils.isEmpty(this.mCurrentEngine)) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    if (key != null && key.startsWith(this.mCurrentEngine)) {
                        bundle.putString(key, entry.getValue());
                    }
                }
            }
            return bundle;
        }
        return this.mParams;
    }

    private void copyStringParam(Bundle bundle, HashMap<String, String> params, String key) {
        String value = params.get(key);
        if (value != null) {
            bundle.putString(key, value);
        }
    }

    private void copyIntParam(Bundle bundle, HashMap<String, String> params, String key) {
        String valueString = params.get(key);
        if (!TextUtils.isEmpty(valueString)) {
            try {
                int value = Integer.parseInt(valueString);
                bundle.putInt(key, value);
            } catch (NumberFormatException e) {
            }
        }
    }

    private void copyFloatParam(Bundle bundle, HashMap<String, String> params, String key) {
        String valueString = params.get(key);
        if (!TextUtils.isEmpty(valueString)) {
            try {
                float value = Float.parseFloat(valueString);
                bundle.putFloat(key, value);
            } catch (NumberFormatException e) {
            }
        }
    }

    @Deprecated
    public int setOnUtteranceCompletedListener(OnUtteranceCompletedListener listener) {
        this.mUtteranceProgressListener = UtteranceProgressListener.from(listener);
        return 0;
    }

    public int setOnUtteranceProgressListener(UtteranceProgressListener listener) {
        this.mUtteranceProgressListener = listener;
        return 0;
    }

    @Deprecated
    public int setEngineByPackageName(String enginePackageName) {
        this.mRequestedEngine = enginePackageName;
        return initTts();
    }

    public String getDefaultEngine() {
        return this.mEnginesHelper.getDefaultEngine();
    }

    public boolean areDefaultsEnforced() {
        return false;
    }

    public List<EngineInfo> getEngines() {
        return this.mEnginesHelper.getEngines();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextToSpeech$Connection.class */
    public class Connection implements ServiceConnection {
        private ITextToSpeechService mService;
        private SetupConnectionAsyncTask mOnSetupConnectionAsyncTask;
        private boolean mEstablished;
        private final ITextToSpeechCallback.Stub mCallback;

        private Connection() {
            this.mCallback = new ITextToSpeechCallback.Stub() { // from class: android.speech.tts.TextToSpeech.Connection.1
                @Override // android.speech.tts.ITextToSpeechCallback
                public void onDone(String utteranceId) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onDone(utteranceId);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onError(String utteranceId) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onError(utteranceId);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onStart(String utteranceId) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onStart(utteranceId);
                    }
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: TextToSpeech$Connection$SetupConnectionAsyncTask.class */
        public class SetupConnectionAsyncTask extends AsyncTask<Void, Void, Integer> {
            private final ComponentName mName;

            public SetupConnectionAsyncTask(ComponentName name) {
                this.mName = name;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Integer doInBackground(Void... params) {
                synchronized (TextToSpeech.this.mStartLock) {
                    if (!isCancelled()) {
                        try {
                            Connection.this.mService.setCallback(Connection.this.getCallerIdentity(), Connection.this.mCallback);
                            String[] defaultLanguage = Connection.this.mService.getClientDefaultLanguage();
                            TextToSpeech.this.mParams.putString("language", defaultLanguage[0]);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_COUNTRY, defaultLanguage[1]);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_VARIANT, defaultLanguage[2]);
                            Log.i(TextToSpeech.TAG, "Set up connection to " + this.mName);
                            return 0;
                        } catch (RemoteException e) {
                            Log.e(TextToSpeech.TAG, "Error connecting to service, setCallback() failed");
                            return -1;
                        }
                    }
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Integer result) {
                synchronized (TextToSpeech.this.mStartLock) {
                    if (Connection.this.mOnSetupConnectionAsyncTask == this) {
                        Connection.this.mOnSetupConnectionAsyncTask = null;
                    }
                    Connection.this.mEstablished = true;
                    TextToSpeech.this.dispatchOnInit(result.intValue());
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (TextToSpeech.this.mStartLock) {
                TextToSpeech.this.mConnectingServiceConnection = null;
                Log.i(TextToSpeech.TAG, "Connected to " + name);
                if (this.mOnSetupConnectionAsyncTask != null) {
                    this.mOnSetupConnectionAsyncTask.cancel(false);
                }
                this.mService = ITextToSpeechService.Stub.asInterface(service);
                TextToSpeech.this.mServiceConnection = this;
                this.mEstablished = false;
                this.mOnSetupConnectionAsyncTask = new SetupConnectionAsyncTask(name);
                this.mOnSetupConnectionAsyncTask.execute(new Void[0]);
            }
        }

        public IBinder getCallerIdentity() {
            return this.mCallback;
        }

        private boolean clearServiceConnection() {
            boolean z;
            synchronized (TextToSpeech.this.mStartLock) {
                boolean result = false;
                if (this.mOnSetupConnectionAsyncTask != null) {
                    result = this.mOnSetupConnectionAsyncTask.cancel(false);
                    this.mOnSetupConnectionAsyncTask = null;
                }
                this.mService = null;
                if (TextToSpeech.this.mServiceConnection == this) {
                    TextToSpeech.this.mServiceConnection = null;
                }
                z = result;
            }
            return z;
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TextToSpeech.TAG, "Asked to disconnect from " + name);
            if (clearServiceConnection()) {
                TextToSpeech.this.dispatchOnInit(-1);
            }
        }

        public void disconnect() {
            TextToSpeech.this.mContext.unbindService(this);
            clearServiceConnection();
        }

        public boolean isEstablished() {
            return this.mService != null && this.mEstablished;
        }

        public <R> R runAction(Action<R> action, R errorResult, String method, boolean reconnect, boolean onlyEstablishedConnection) {
            synchronized (TextToSpeech.this.mStartLock) {
                try {
                    if (this.mService == null) {
                        Log.w(TextToSpeech.TAG, method + " failed: not connected to TTS engine");
                        return errorResult;
                    } else if (onlyEstablishedConnection && !isEstablished()) {
                        Log.w(TextToSpeech.TAG, method + " failed: TTS engine connection not fully set up");
                        return errorResult;
                    } else {
                        return action.run(this.mService);
                    }
                } catch (RemoteException ex) {
                    Log.e(TextToSpeech.TAG, method + " failed", ex);
                    if (reconnect) {
                        disconnect();
                        TextToSpeech.this.initTts();
                    }
                    return errorResult;
                }
            }
        }
    }

    /* loaded from: TextToSpeech$EngineInfo.class */
    public static class EngineInfo {
        public String name;
        public String label;
        public int icon;
        public boolean system;
        public int priority;

        public String toString() {
            return "EngineInfo{name=" + this.name + "}";
        }
    }

    public static int getMaxSpeechInputLength() {
        return 4000;
    }
}