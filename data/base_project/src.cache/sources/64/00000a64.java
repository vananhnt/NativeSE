package android.nfc;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.nfc.IAppCallback;
import android.nfc.NfcAdapter;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* loaded from: NfcActivityManager.class */
public final class NfcActivityManager extends IAppCallback.Stub implements Application.ActivityLifecycleCallbacks {
    static final String TAG = "NFC";
    static final Boolean DBG = false;
    final NfcAdapter mAdapter;
    final NfcEvent mDefaultEvent;
    final List<NfcActivityState> mActivities = new LinkedList();
    final List<NfcApplicationState> mApps = new ArrayList(1);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: NfcActivityManager$NfcApplicationState.class */
    public class NfcApplicationState {
        int refCount = 0;
        final Application app;

        public NfcApplicationState(Application app) {
            this.app = app;
        }

        public void register() {
            this.refCount++;
            if (this.refCount == 1) {
                this.app.registerActivityLifecycleCallbacks(NfcActivityManager.this);
            }
        }

        public void unregister() {
            this.refCount--;
            if (this.refCount == 0) {
                this.app.unregisterActivityLifecycleCallbacks(NfcActivityManager.this);
            } else if (this.refCount < 0) {
                Log.e(NfcActivityManager.TAG, "-ve refcount for " + this.app);
            }
        }
    }

    NfcApplicationState findAppState(Application app) {
        for (NfcApplicationState appState : this.mApps) {
            if (appState.app == app) {
                return appState;
            }
        }
        return null;
    }

    void registerApplication(Application app) {
        NfcApplicationState appState = findAppState(app);
        if (appState == null) {
            appState = new NfcApplicationState(app);
            this.mApps.add(appState);
        }
        appState.register();
    }

    void unregisterApplication(Application app) {
        NfcApplicationState appState = findAppState(app);
        if (appState == null) {
            Log.e(TAG, "app was not registered " + app);
        } else {
            appState.unregister();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: NfcActivityManager$NfcActivityState.class */
    public class NfcActivityState {
        boolean resumed;
        Activity activity;
        NdefMessage ndefMessage = null;
        NfcAdapter.CreateNdefMessageCallback ndefMessageCallback = null;
        NfcAdapter.OnNdefPushCompleteCallback onNdefPushCompleteCallback = null;
        NfcAdapter.CreateBeamUrisCallback uriCallback = null;
        Uri[] uris = null;
        int flags = 0;
        int readerModeFlags = 0;
        NfcAdapter.ReaderCallback readerCallback = null;
        Bundle readerModeExtras = null;
        Binder token;

        public NfcActivityState(Activity activity) {
            this.resumed = false;
            if (activity.getWindow().isDestroyed()) {
                throw new IllegalStateException("activity is already destroyed");
            }
            this.resumed = activity.isResumed();
            this.activity = activity;
            this.token = new Binder();
            NfcActivityManager.this.registerApplication(activity.getApplication());
        }

        public void destroy() {
            NfcActivityManager.this.unregisterApplication(this.activity.getApplication());
            this.resumed = false;
            this.activity = null;
            this.ndefMessage = null;
            this.ndefMessageCallback = null;
            this.onNdefPushCompleteCallback = null;
            this.uriCallback = null;
            this.uris = null;
            this.readerModeFlags = 0;
            this.token = null;
        }

        public String toString() {
            StringBuilder s = new StringBuilder("[").append(Separators.SP);
            s.append(this.ndefMessage).append(Separators.SP).append(this.ndefMessageCallback).append(Separators.SP);
            s.append(this.uriCallback).append(Separators.SP);
            if (this.uris != null) {
                Uri[] arr$ = this.uris;
                for (Uri uri : arr$) {
                    s.append(this.onNdefPushCompleteCallback).append(Separators.SP).append(uri).append("]");
                }
            }
            return s.toString();
        }
    }

    synchronized NfcActivityState findActivityState(Activity activity) {
        for (NfcActivityState state : this.mActivities) {
            if (state.activity == activity) {
                return state;
            }
        }
        return null;
    }

    synchronized NfcActivityState getActivityState(Activity activity) {
        NfcActivityState state = findActivityState(activity);
        if (state == null) {
            state = new NfcActivityState(activity);
            this.mActivities.add(state);
        }
        return state;
    }

    synchronized NfcActivityState findResumedActivityState() {
        for (NfcActivityState state : this.mActivities) {
            if (state.resumed) {
                return state;
            }
        }
        return null;
    }

    synchronized void destroyActivityState(Activity activity) {
        NfcActivityState activityState = findActivityState(activity);
        if (activityState != null) {
            activityState.destroy();
            this.mActivities.remove(activityState);
        }
    }

    public NfcActivityManager(NfcAdapter adapter) {
        this.mAdapter = adapter;
        this.mDefaultEvent = new NfcEvent(this.mAdapter);
    }

    public void enableReaderMode(Activity activity, NfcAdapter.ReaderCallback callback, int flags, Bundle extras) {
        Binder token;
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.readerCallback = callback;
            state.readerModeFlags = flags;
            state.readerModeExtras = extras;
            token = state.token;
            isResumed = state.resumed;
        }
        if (isResumed) {
            setReaderMode(token, flags, extras);
        }
    }

    public void disableReaderMode(Activity activity) {
        Binder token;
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.readerCallback = null;
            state.readerModeFlags = 0;
            state.readerModeExtras = null;
            token = state.token;
            isResumed = state.resumed;
        }
        if (isResumed) {
            setReaderMode(token, 0, null);
        }
    }

    public void setReaderMode(Binder token, int flags, Bundle extras) {
        if (DBG.booleanValue()) {
            Log.d(TAG, "Setting reader mode");
        }
        try {
            NfcAdapter.sService.setReaderMode(token, this, flags, extras);
        } catch (RemoteException e) {
            this.mAdapter.attemptDeadServiceRecovery(e);
        }
    }

    public void setNdefPushContentUri(Activity activity, Uri[] uris) {
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.uris = uris;
            isResumed = state.resumed;
        }
        if (isResumed) {
            requestNfcServiceCallback();
        }
    }

    public void setNdefPushContentUriCallback(Activity activity, NfcAdapter.CreateBeamUrisCallback callback) {
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.uriCallback = callback;
            isResumed = state.resumed;
        }
        if (isResumed) {
            requestNfcServiceCallback();
        }
    }

    public void setNdefPushMessage(Activity activity, NdefMessage message, int flags) {
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.ndefMessage = message;
            state.flags = flags;
            isResumed = state.resumed;
        }
        if (isResumed) {
            requestNfcServiceCallback();
        }
    }

    public void setNdefPushMessageCallback(Activity activity, NfcAdapter.CreateNdefMessageCallback callback, int flags) {
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.ndefMessageCallback = callback;
            state.flags = flags;
            isResumed = state.resumed;
        }
        if (isResumed) {
            requestNfcServiceCallback();
        }
    }

    public void setOnNdefPushCompleteCallback(Activity activity, NfcAdapter.OnNdefPushCompleteCallback callback) {
        boolean isResumed;
        synchronized (this) {
            NfcActivityState state = getActivityState(activity);
            state.onNdefPushCompleteCallback = callback;
            isResumed = state.resumed;
        }
        if (isResumed) {
            requestNfcServiceCallback();
        }
    }

    void requestNfcServiceCallback() {
        try {
            NfcAdapter.sService.setAppCallback(this);
        } catch (RemoteException e) {
            this.mAdapter.attemptDeadServiceRecovery(e);
        }
    }

    @Override // android.nfc.IAppCallback
    public BeamShareData createBeamShareData() {
        synchronized (this) {
            NfcActivityState state = findResumedActivityState();
            if (state == null) {
                return null;
            }
            NfcAdapter.CreateNdefMessageCallback ndefCallback = state.ndefMessageCallback;
            NfcAdapter.CreateBeamUrisCallback urisCallback = state.uriCallback;
            NdefMessage message = state.ndefMessage;
            Uri[] uris = state.uris;
            int flags = state.flags;
            if (ndefCallback != null) {
                message = ndefCallback.createNdefMessage(this.mDefaultEvent);
            }
            if (urisCallback != null) {
                uris = urisCallback.createBeamUris(this.mDefaultEvent);
                if (uris != null) {
                    for (Uri uri : uris) {
                        if (uri == null) {
                            Log.e(TAG, "Uri not allowed to be null.");
                            return null;
                        }
                        String scheme = uri.getScheme();
                        if (scheme == null || (!scheme.equalsIgnoreCase(ContentResolver.SCHEME_FILE) && !scheme.equalsIgnoreCase("content"))) {
                            Log.e(TAG, "Uri needs to have either scheme file or scheme content");
                            return null;
                        }
                    }
                }
            }
            return new BeamShareData(message, uris, flags);
        }
    }

    @Override // android.nfc.IAppCallback
    public void onNdefPushComplete() {
        synchronized (this) {
            NfcActivityState state = findResumedActivityState();
            if (state == null) {
                return;
            }
            NfcAdapter.OnNdefPushCompleteCallback callback = state.onNdefPushCompleteCallback;
            if (callback != null) {
                callback.onNdefPushComplete(this.mDefaultEvent);
            }
        }
    }

    @Override // android.nfc.IAppCallback
    public void onTagDiscovered(Tag tag) throws RemoteException {
        synchronized (this) {
            NfcActivityState state = findResumedActivityState();
            if (state == null) {
                return;
            }
            NfcAdapter.ReaderCallback callback = state.readerCallback;
            if (callback != null) {
                callback.onTagDiscovered(tag);
            }
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        synchronized (this) {
            NfcActivityState state = findActivityState(activity);
            if (DBG.booleanValue()) {
                Log.d(TAG, "onResume() for " + activity + Separators.SP + state);
            }
            if (state == null) {
                return;
            }
            state.resumed = true;
            Binder token = state.token;
            int readerModeFlags = state.readerModeFlags;
            Bundle readerModeExtras = state.readerModeExtras;
            if (readerModeFlags != 0) {
                setReaderMode(token, readerModeFlags, readerModeExtras);
            }
            requestNfcServiceCallback();
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        synchronized (this) {
            NfcActivityState state = findActivityState(activity);
            if (DBG.booleanValue()) {
                Log.d(TAG, "onPause() for " + activity + Separators.SP + state);
            }
            if (state == null) {
                return;
            }
            state.resumed = false;
            Binder token = state.token;
            boolean readerModeFlagsSet = state.readerModeFlags != 0;
            if (readerModeFlagsSet) {
                setReaderMode(token, 0, null);
            }
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        synchronized (this) {
            NfcActivityState state = findActivityState(activity);
            if (DBG.booleanValue()) {
                Log.d(TAG, "onDestroy() for " + activity + Separators.SP + state);
            }
            if (state != null) {
                destroyActivityState(activity);
            }
        }
    }
}