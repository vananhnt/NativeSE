package android.speech.tts;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.speech.tts.ITextToSpeechService;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/* loaded from: TextToSpeechService.class */
public abstract class TextToSpeechService extends Service {
    private static final boolean DBG = false;
    private static final String TAG = "TextToSpeechService";
    private static final String SYNTH_THREAD_NAME = "SynthThread";
    private SynthHandler mSynthHandler;
    private AudioPlaybackHandler mAudioPlaybackHandler;
    private TtsEngines mEngineHelper;
    private CallbackMap mCallbacks;
    private String mPackageName;
    private final ITextToSpeechService.Stub mBinder = new ITextToSpeechService.Stub() { // from class: android.speech.tts.TextToSpeechService.1
        @Override // android.speech.tts.ITextToSpeechService
        public int speak(IBinder caller, String text, int queueMode, Bundle params) {
            if (!checkNonNull(caller, text, params)) {
                return -1;
            }
            SpeechItem item = new SynthesisSpeechItem(caller, Binder.getCallingUid(), Binder.getCallingPid(), params, text);
            return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(queueMode, item);
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int synthesizeToFileDescriptor(IBinder caller, String text, ParcelFileDescriptor fileDescriptor, Bundle params) {
            if (!checkNonNull(caller, text, fileDescriptor, params)) {
                return -1;
            }
            ParcelFileDescriptor sameFileDescriptor = ParcelFileDescriptor.adoptFd(fileDescriptor.detachFd());
            SpeechItem item = new SynthesisToFileOutputStreamSpeechItem(caller, Binder.getCallingUid(), Binder.getCallingPid(), params, text, new ParcelFileDescriptor.AutoCloseOutputStream(sameFileDescriptor));
            return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(1, item);
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int playAudio(IBinder caller, Uri audioUri, int queueMode, Bundle params) {
            if (!checkNonNull(caller, audioUri, params)) {
                return -1;
            }
            SpeechItem item = new AudioSpeechItem(caller, Binder.getCallingUid(), Binder.getCallingPid(), params, audioUri);
            return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(queueMode, item);
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int playSilence(IBinder caller, long duration, int queueMode, Bundle params) {
            if (!checkNonNull(caller, params)) {
                return -1;
            }
            SpeechItem item = new SilenceSpeechItem(caller, Binder.getCallingUid(), Binder.getCallingPid(), params, duration);
            return TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(queueMode, item);
        }

        @Override // android.speech.tts.ITextToSpeechService
        public boolean isSpeaking() {
            return TextToSpeechService.this.mSynthHandler.isSpeaking() || TextToSpeechService.this.mAudioPlaybackHandler.isSpeaking();
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int stop(IBinder caller) {
            if (checkNonNull(caller)) {
                return TextToSpeechService.this.mSynthHandler.stopForApp(caller);
            }
            return -1;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getLanguage() {
            return TextToSpeechService.this.onGetLanguage();
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getClientDefaultLanguage() {
            return TextToSpeechService.this.getSettingsLocale();
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int isLanguageAvailable(String lang, String country, String variant) {
            if (!checkNonNull(lang)) {
                return -1;
            }
            return TextToSpeechService.this.onIsLanguageAvailable(lang, country, variant);
        }

        @Override // android.speech.tts.ITextToSpeechService
        public String[] getFeaturesForLanguage(String lang, String country, String variant) {
            String[] featuresArray;
            Set<String> features = TextToSpeechService.this.onGetFeaturesForLanguage(lang, country, variant);
            if (features != null) {
                featuresArray = new String[features.size()];
                features.toArray(featuresArray);
            } else {
                featuresArray = new String[0];
            }
            return featuresArray;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public int loadLanguage(IBinder caller, String lang, String country, String variant) {
            if (!checkNonNull(lang)) {
                return -1;
            }
            int retVal = TextToSpeechService.this.onIsLanguageAvailable(lang, country, variant);
            if (retVal == 0 || retVal == 1 || retVal == 2) {
                SpeechItem item = new LoadLanguageItem(caller, Binder.getCallingUid(), Binder.getCallingPid(), null, lang, country, variant);
                if (TextToSpeechService.this.mSynthHandler.enqueueSpeechItem(1, item) != 0) {
                    return -1;
                }
            }
            return retVal;
        }

        @Override // android.speech.tts.ITextToSpeechService
        public void setCallback(IBinder caller, ITextToSpeechCallback cb) {
            if (checkNonNull(caller)) {
                TextToSpeechService.this.mCallbacks.setCallback(caller, cb);
            }
        }

        private String intern(String in) {
            return in.intern();
        }

        private boolean checkNonNull(Object... args) {
            for (Object o : args) {
                if (o == null) {
                    return false;
                }
            }
            return true;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TextToSpeechService$UtteranceProgressDispatcher.class */
    public interface UtteranceProgressDispatcher {
        void dispatchOnDone();

        void dispatchOnStart();

        void dispatchOnError();
    }

    protected abstract int onIsLanguageAvailable(String str, String str2, String str3);

    protected abstract String[] onGetLanguage();

    protected abstract int onLoadLanguage(String str, String str2, String str3);

    protected abstract void onStop();

    protected abstract void onSynthesizeText(SynthesisRequest synthesisRequest, SynthesisCallback synthesisCallback);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        SynthThread synthThread = new SynthThread();
        synthThread.start();
        this.mSynthHandler = new SynthHandler(synthThread.getLooper());
        this.mAudioPlaybackHandler = new AudioPlaybackHandler();
        this.mAudioPlaybackHandler.start();
        this.mEngineHelper = new TtsEngines(this);
        this.mCallbacks = new CallbackMap();
        this.mPackageName = getApplicationInfo().packageName;
        String[] defaultLocale = getSettingsLocale();
        onLoadLanguage(defaultLocale[0], defaultLocale[1], defaultLocale[2]);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mSynthHandler.quit();
        this.mAudioPlaybackHandler.quit();
        this.mCallbacks.kill();
        super.onDestroy();
    }

    protected Set<String> onGetFeaturesForLanguage(String lang, String country, String variant) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDefaultSpeechRate() {
        return getSecureSettingInt(Settings.Secure.TTS_DEFAULT_RATE, 100);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] getSettingsLocale() {
        String locale = this.mEngineHelper.getLocalePrefForEngine(this.mPackageName);
        return TtsEngines.parseLocalePref(locale);
    }

    private int getSecureSettingInt(String name, int defaultValue) {
        return Settings.Secure.getInt(getContentResolver(), name, defaultValue);
    }

    /* loaded from: TextToSpeechService$SynthThread.class */
    private class SynthThread extends HandlerThread implements MessageQueue.IdleHandler {
        private boolean mFirstIdle;

        public SynthThread() {
            super(TextToSpeechService.SYNTH_THREAD_NAME, 0);
            this.mFirstIdle = true;
        }

        @Override // android.os.HandlerThread
        protected void onLooperPrepared() {
            getLooper().getQueue().addIdleHandler(this);
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            if (this.mFirstIdle) {
                this.mFirstIdle = false;
                return true;
            }
            broadcastTtsQueueProcessingCompleted();
            return true;
        }

        private void broadcastTtsQueueProcessingCompleted() {
            Intent i = new Intent(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
            TextToSpeechService.this.sendBroadcast(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextToSpeechService$SynthHandler.class */
    public class SynthHandler extends Handler {
        private SpeechItem mCurrentSpeechItem;

        public SynthHandler(Looper looper) {
            super(looper);
            this.mCurrentSpeechItem = null;
        }

        private synchronized SpeechItem getCurrentSpeechItem() {
            return this.mCurrentSpeechItem;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized SpeechItem setCurrentSpeechItem(SpeechItem speechItem) {
            SpeechItem old = this.mCurrentSpeechItem;
            this.mCurrentSpeechItem = speechItem;
            return old;
        }

        private synchronized SpeechItem maybeRemoveCurrentSpeechItem(Object callerIdentity) {
            if (this.mCurrentSpeechItem != null && this.mCurrentSpeechItem.getCallerIdentity() == callerIdentity) {
                SpeechItem current = this.mCurrentSpeechItem;
                this.mCurrentSpeechItem = null;
                return current;
            }
            return null;
        }

        public boolean isSpeaking() {
            return getCurrentSpeechItem() != null;
        }

        public void quit() {
            getLooper().quit();
            SpeechItem current = setCurrentSpeechItem(null);
            if (current != null) {
                current.stop();
            }
        }

        public int enqueueSpeechItem(int queueMode, final SpeechItem speechItem) {
            UtteranceProgressDispatcher utterenceProgress = null;
            if (speechItem instanceof UtteranceProgressDispatcher) {
                utterenceProgress = (UtteranceProgressDispatcher) speechItem;
            }
            if (!speechItem.isValid()) {
                if (utterenceProgress != null) {
                    utterenceProgress.dispatchOnError();
                    return -1;
                }
                return -1;
            }
            if (queueMode == 0) {
                stopForApp(speechItem.getCallerIdentity());
            } else if (queueMode == 2) {
                stopAll();
            }
            Runnable runnable = new Runnable() { // from class: android.speech.tts.TextToSpeechService.SynthHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    SynthHandler.this.setCurrentSpeechItem(speechItem);
                    speechItem.play();
                    SynthHandler.this.setCurrentSpeechItem(null);
                }
            };
            Message msg = Message.obtain(this, runnable);
            msg.obj = speechItem.getCallerIdentity();
            if (sendMessage(msg)) {
                return 0;
            }
            Log.w(TextToSpeechService.TAG, "SynthThread has quit");
            if (utterenceProgress != null) {
                utterenceProgress.dispatchOnError();
                return -1;
            }
            return -1;
        }

        public int stopForApp(Object callerIdentity) {
            if (callerIdentity == null) {
                return -1;
            }
            removeCallbacksAndMessages(callerIdentity);
            SpeechItem current = maybeRemoveCurrentSpeechItem(callerIdentity);
            if (current != null) {
                current.stop();
            }
            TextToSpeechService.this.mAudioPlaybackHandler.stopForApp(callerIdentity);
            return 0;
        }

        public int stopAll() {
            SpeechItem current = setCurrentSpeechItem(null);
            if (current != null) {
                current.stop();
            }
            removeCallbacksAndMessages(null);
            TextToSpeechService.this.mAudioPlaybackHandler.stop();
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextToSpeechService$SpeechItem.class */
    public abstract class SpeechItem {
        private final Object mCallerIdentity;
        protected final Bundle mParams;
        private final int mCallerUid;
        private final int mCallerPid;
        private boolean mStarted = false;
        private boolean mStopped = false;

        public abstract boolean isValid();

        protected abstract int playImpl();

        protected abstract void stopImpl();

        public SpeechItem(Object caller, int callerUid, int callerPid, Bundle params) {
            this.mCallerIdentity = caller;
            this.mParams = params;
            this.mCallerUid = callerUid;
            this.mCallerPid = callerPid;
        }

        public Object getCallerIdentity() {
            return this.mCallerIdentity;
        }

        public int getCallerUid() {
            return this.mCallerUid;
        }

        public int getCallerPid() {
            return this.mCallerPid;
        }

        public int play() {
            synchronized (this) {
                if (this.mStarted) {
                    throw new IllegalStateException("play() called twice");
                }
                this.mStarted = true;
            }
            return playImpl();
        }

        public void stop() {
            synchronized (this) {
                if (this.mStopped) {
                    throw new IllegalStateException("stop() called twice");
                }
                this.mStopped = true;
            }
            stopImpl();
        }

        protected synchronized boolean isStopped() {
            return this.mStopped;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextToSpeechService$UtteranceSpeechItem.class */
    public abstract class UtteranceSpeechItem extends SpeechItem implements UtteranceProgressDispatcher {
        public UtteranceSpeechItem(Object caller, int callerUid, int callerPid, Bundle params) {
            super(caller, callerUid, callerPid, params);
        }

        @Override // android.speech.tts.TextToSpeechService.UtteranceProgressDispatcher
        public void dispatchOnDone() {
            String utteranceId = getUtteranceId();
            if (utteranceId != null) {
                TextToSpeechService.this.mCallbacks.dispatchOnDone(getCallerIdentity(), utteranceId);
            }
        }

        @Override // android.speech.tts.TextToSpeechService.UtteranceProgressDispatcher
        public void dispatchOnStart() {
            String utteranceId = getUtteranceId();
            if (utteranceId != null) {
                TextToSpeechService.this.mCallbacks.dispatchOnStart(getCallerIdentity(), utteranceId);
            }
        }

        @Override // android.speech.tts.TextToSpeechService.UtteranceProgressDispatcher
        public void dispatchOnError() {
            String utteranceId = getUtteranceId();
            if (utteranceId != null) {
                TextToSpeechService.this.mCallbacks.dispatchOnError(getCallerIdentity(), utteranceId);
            }
        }

        public int getStreamType() {
            return getIntParam(TextToSpeech.Engine.KEY_PARAM_STREAM, 3);
        }

        public float getVolume() {
            return getFloatParam("volume", 1.0f);
        }

        public float getPan() {
            return getFloatParam(TextToSpeech.Engine.KEY_PARAM_PAN, 0.0f);
        }

        public String getUtteranceId() {
            return getStringParam(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
        }

        protected String getStringParam(String key, String defaultValue) {
            return this.mParams == null ? defaultValue : this.mParams.getString(key, defaultValue);
        }

        protected int getIntParam(String key, int defaultValue) {
            return this.mParams == null ? defaultValue : this.mParams.getInt(key, defaultValue);
        }

        protected float getFloatParam(String key, float defaultValue) {
            return this.mParams == null ? defaultValue : this.mParams.getFloat(key, defaultValue);
        }
    }

    /* loaded from: TextToSpeechService$SynthesisSpeechItem.class */
    class SynthesisSpeechItem extends UtteranceSpeechItem {
        private final String mText;
        private final SynthesisRequest mSynthesisRequest;
        private final String[] mDefaultLocale;
        private AbstractSynthesisCallback mSynthesisCallback;
        private final EventLogger mEventLogger;
        private final int mCallerUid;

        public SynthesisSpeechItem(Object callerIdentity, int callerUid, int callerPid, Bundle params, String text) {
            super(callerIdentity, callerUid, callerPid, params);
            this.mText = text;
            this.mCallerUid = callerUid;
            this.mSynthesisRequest = new SynthesisRequest(this.mText, this.mParams);
            this.mDefaultLocale = TextToSpeechService.this.getSettingsLocale();
            setRequestParams(this.mSynthesisRequest);
            this.mEventLogger = new EventLogger(this.mSynthesisRequest, callerUid, callerPid, TextToSpeechService.this.mPackageName);
        }

        public String getText() {
            return this.mText;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            if (this.mText == null) {
                Log.e(TextToSpeechService.TAG, "null synthesis text");
                return false;
            } else if (this.mText.length() >= TextToSpeech.getMaxSpeechInputLength()) {
                Log.w(TextToSpeechService.TAG, "Text too long: " + this.mText.length() + " chars");
                return false;
            } else {
                return true;
            }
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            this.mEventLogger.onRequestProcessingStart();
            synchronized (this) {
                if (isStopped()) {
                    return -1;
                }
                this.mSynthesisCallback = createSynthesisCallback();
                AbstractSynthesisCallback synthesisCallback = this.mSynthesisCallback;
                TextToSpeechService.this.onSynthesizeText(this.mSynthesisRequest, synthesisCallback);
                return synthesisCallback.isDone() ? 0 : -1;
            }
        }

        protected AbstractSynthesisCallback createSynthesisCallback() {
            return new PlaybackSynthesisCallback(getStreamType(), getVolume(), getPan(), TextToSpeechService.this.mAudioPlaybackHandler, this, getCallerIdentity(), this.mEventLogger);
        }

        private void setRequestParams(SynthesisRequest request) {
            request.setLanguage(getLanguage(), getCountry(), getVariant());
            request.setSpeechRate(getSpeechRate());
            request.setCallerUid(this.mCallerUid);
            request.setPitch(getPitch());
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
            AbstractSynthesisCallback synthesisCallback;
            synchronized (this) {
                synthesisCallback = this.mSynthesisCallback;
            }
            if (synthesisCallback != null) {
                synthesisCallback.stop();
                TextToSpeechService.this.onStop();
            }
        }

        public String getLanguage() {
            return getStringParam("language", this.mDefaultLocale[0]);
        }

        private boolean hasLanguage() {
            return !TextUtils.isEmpty(getStringParam("language", null));
        }

        private String getCountry() {
            return !hasLanguage() ? this.mDefaultLocale[1] : getStringParam(TextToSpeech.Engine.KEY_PARAM_COUNTRY, "");
        }

        private String getVariant() {
            return !hasLanguage() ? this.mDefaultLocale[2] : getStringParam(TextToSpeech.Engine.KEY_PARAM_VARIANT, "");
        }

        private int getSpeechRate() {
            return getIntParam(TextToSpeech.Engine.KEY_PARAM_RATE, TextToSpeechService.this.getDefaultSpeechRate());
        }

        private int getPitch() {
            return getIntParam(TextToSpeech.Engine.KEY_PARAM_PITCH, 100);
        }
    }

    /* loaded from: TextToSpeechService$SynthesisToFileOutputStreamSpeechItem.class */
    private class SynthesisToFileOutputStreamSpeechItem extends SynthesisSpeechItem {
        private final FileOutputStream mFileOutputStream;

        public SynthesisToFileOutputStreamSpeechItem(Object callerIdentity, int callerUid, int callerPid, Bundle params, String text, FileOutputStream fileOutputStream) {
            super(callerIdentity, callerUid, callerPid, params, text);
            this.mFileOutputStream = fileOutputStream;
        }

        @Override // android.speech.tts.TextToSpeechService.SynthesisSpeechItem
        protected AbstractSynthesisCallback createSynthesisCallback() {
            return new FileSynthesisCallback(this.mFileOutputStream.getChannel());
        }

        @Override // android.speech.tts.TextToSpeechService.SynthesisSpeechItem, android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            dispatchOnStart();
            int status = super.playImpl();
            if (status == 0) {
                dispatchOnDone();
            } else {
                dispatchOnError();
            }
            try {
                this.mFileOutputStream.close();
            } catch (IOException e) {
                Log.w(TextToSpeechService.TAG, "Failed to close output file", e);
            }
            return status;
        }
    }

    /* loaded from: TextToSpeechService$AudioSpeechItem.class */
    private class AudioSpeechItem extends UtteranceSpeechItem {
        private final AudioPlaybackQueueItem mItem;

        public AudioSpeechItem(Object callerIdentity, int callerUid, int callerPid, Bundle params, Uri uri) {
            super(callerIdentity, callerUid, callerPid, params);
            this.mItem = new AudioPlaybackQueueItem(this, getCallerIdentity(), TextToSpeechService.this, uri, getStreamType());
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            return true;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            TextToSpeechService.this.mAudioPlaybackHandler.enqueue(this.mItem);
            return 0;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
        }
    }

    /* loaded from: TextToSpeechService$SilenceSpeechItem.class */
    private class SilenceSpeechItem extends UtteranceSpeechItem {
        private final long mDuration;

        public SilenceSpeechItem(Object callerIdentity, int callerUid, int callerPid, Bundle params, long duration) {
            super(callerIdentity, callerUid, callerPid, params);
            this.mDuration = duration;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            return true;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            TextToSpeechService.this.mAudioPlaybackHandler.enqueue(new SilencePlaybackQueueItem(this, getCallerIdentity(), this.mDuration));
            return 0;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
        }
    }

    /* loaded from: TextToSpeechService$LoadLanguageItem.class */
    private class LoadLanguageItem extends SpeechItem {
        private final String mLanguage;
        private final String mCountry;
        private final String mVariant;

        public LoadLanguageItem(Object callerIdentity, int callerUid, int callerPid, Bundle params, String language, String country, String variant) {
            super(callerIdentity, callerUid, callerPid, params);
            this.mLanguage = language;
            this.mCountry = country;
            this.mVariant = variant;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        public boolean isValid() {
            return true;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected int playImpl() {
            int result = TextToSpeechService.this.onLoadLanguage(this.mLanguage, this.mCountry, this.mVariant);
            if (result == 0 || result == 1 || result == 2) {
                return 0;
            }
            return -1;
        }

        @Override // android.speech.tts.TextToSpeechService.SpeechItem
        protected void stopImpl() {
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE.equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextToSpeechService$CallbackMap.class */
    public class CallbackMap extends RemoteCallbackList<ITextToSpeechCallback> {
        private final HashMap<IBinder, ITextToSpeechCallback> mCallerToCallback;

        private CallbackMap() {
            this.mCallerToCallback = new HashMap<>();
        }

        public void setCallback(IBinder caller, ITextToSpeechCallback cb) {
            ITextToSpeechCallback old;
            synchronized (this.mCallerToCallback) {
                if (cb != null) {
                    register(cb, caller);
                    old = this.mCallerToCallback.put(caller, cb);
                } else {
                    old = this.mCallerToCallback.remove(caller);
                }
                if (old != null && old != cb) {
                    unregister(old);
                }
            }
        }

        public void dispatchOnDone(Object callerIdentity, String utteranceId) {
            ITextToSpeechCallback cb = getCallbackFor(callerIdentity);
            if (cb == null) {
                return;
            }
            try {
                cb.onDone(utteranceId);
            } catch (RemoteException e) {
                Log.e(TextToSpeechService.TAG, "Callback onDone failed: " + e);
            }
        }

        public void dispatchOnStart(Object callerIdentity, String utteranceId) {
            ITextToSpeechCallback cb = getCallbackFor(callerIdentity);
            if (cb == null) {
                return;
            }
            try {
                cb.onStart(utteranceId);
            } catch (RemoteException e) {
                Log.e(TextToSpeechService.TAG, "Callback onStart failed: " + e);
            }
        }

        public void dispatchOnError(Object callerIdentity, String utteranceId) {
            ITextToSpeechCallback cb = getCallbackFor(callerIdentity);
            if (cb == null) {
                return;
            }
            try {
                cb.onError(utteranceId);
            } catch (RemoteException e) {
                Log.e(TextToSpeechService.TAG, "Callback onError failed: " + e);
            }
        }

        @Override // android.os.RemoteCallbackList
        public void onCallbackDied(ITextToSpeechCallback callback, Object cookie) {
            IBinder caller = (IBinder) cookie;
            synchronized (this.mCallerToCallback) {
                this.mCallerToCallback.remove(caller);
            }
            TextToSpeechService.this.mSynthHandler.stopForApp(caller);
        }

        @Override // android.os.RemoteCallbackList
        public void kill() {
            synchronized (this.mCallerToCallback) {
                this.mCallerToCallback.clear();
                super.kill();
            }
        }

        private ITextToSpeechCallback getCallbackFor(Object caller) {
            ITextToSpeechCallback cb;
            IBinder asBinder = (IBinder) caller;
            synchronized (this.mCallerToCallback) {
                cb = this.mCallerToCallback.get(asBinder);
            }
            return cb;
        }
    }
}