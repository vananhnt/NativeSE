package android.media;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import gov.nist.core.Separators;
import gov.nist.javax.sip.parser.TokenNames;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/* loaded from: MediaFocusControl.class */
public class MediaFocusControl implements PendingIntent.OnFinished {
    private static final String TAG = "MediaFocusControl";
    protected static final boolean DEBUG_RC = false;
    protected static final boolean DEBUG_VOL = false;
    private final PowerManager.WakeLock mMediaEventWakeLock;
    private final MediaEventHandler mEventHandler;
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final VolumeController mVolumeController;
    private final AppOpsManager mAppOps;
    private final KeyguardManager mKeyguardManager;
    private final AudioService mAudioService;
    private final NotificationListenerObserver mNotifListenerObserver;
    private static final int RCD_REG_FAILURE = 0;
    private static final int RCD_REG_SUCCESS_PERMISSION = 1;
    private static final int RCD_REG_SUCCESS_ENABLED_NOTIF = 2;
    private static final int MSG_PERSIST_MEDIABUTTONRECEIVER = 0;
    private static final int MSG_RCDISPLAY_CLEAR = 1;
    private static final int MSG_RCDISPLAY_UPDATE = 2;
    private static final int MSG_REEVALUATE_REMOTE = 3;
    private static final int MSG_RCC_NEW_PLAYBACK_INFO = 4;
    private static final int MSG_RCC_NEW_VOLUME_OBS = 5;
    private static final int MSG_PROMOTE_RCC = 6;
    private static final int MSG_RCC_NEW_PLAYBACK_STATE = 7;
    private static final int MSG_RCC_SEEK_REQUEST = 8;
    private static final int MSG_RCC_UPDATE_METADATA = 9;
    private static final int MSG_RCDISPLAY_INIT_INFO = 10;
    private static final int MSG_REEVALUATE_RCD = 11;
    private static final int SENDMSG_REPLACE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    protected static final String IN_VOICE_COMM_FOCUS_ID = "AudioFocus_For_Phone_Ring_And_Calls";
    private static final int VOICEBUTTON_ACTION_DISCARD_CURRENT_KEY_PRESS = 1;
    private static final int VOICEBUTTON_ACTION_START_VOICE_INPUT = 2;
    private static final int VOICEBUTTON_ACTION_SIMULATE_KEY_PRESS = 3;
    private boolean mVoiceButtonDown;
    private boolean mVoiceButtonHandled;
    private static final int WAKELOCK_RELEASE_ON_FINISHED = 1980;
    private static final String EXTRA_WAKELOCK_ACQUIRED = "android.media.AudioService.WAKELOCK_ACQUIRED";
    private static final int RC_INFO_NONE = 0;
    private static final int RC_INFO_ALL = 15;
    private RemotePlaybackState mMainRemote;
    private boolean mMainRemoteIsActive;
    private boolean mHasRemotePlayback;
    private static final Uri ENABLED_NOTIFICATION_LISTENERS_URI = Settings.Secure.getUriFor(Settings.Secure.ENABLED_NOTIFICATION_LISTENERS);
    private static final Object mAudioFocusLock = new Object();
    private static final Object mRingingLock = new Object();
    private static int sLastRccId = 0;
    private boolean mIsRinging = false;
    private final BroadcastReceiver mReceiver = new PackageIntentsReceiver();
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: android.media.MediaFocusControl.1
        @Override // android.telephony.PhoneStateListener
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == 1) {
                synchronized (MediaFocusControl.mRingingLock) {
                    MediaFocusControl.this.mIsRinging = true;
                }
            } else if (state == 2 || state == 0) {
                synchronized (MediaFocusControl.mRingingLock) {
                    MediaFocusControl.this.mIsRinging = false;
                }
            }
        }
    };
    private final Stack<FocusRequester> mFocusStack = new Stack<>();
    private final Object mVoiceEventLock = new Object();
    BroadcastReceiver mKeyEventDone = new BroadcastReceiver() { // from class: android.media.MediaFocusControl.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Bundle extras;
            if (intent != null && (extras = intent.getExtras()) != null && extras.containsKey(MediaFocusControl.EXTRA_WAKELOCK_ACQUIRED)) {
                MediaFocusControl.this.mMediaEventWakeLock.release();
            }
        }
    };
    private final Object mCurrentRcLock = new Object();
    private IRemoteControlClient mCurrentRcClient = null;
    private PendingIntent mCurrentRcClientIntent = null;
    private int mCurrentRcClientGen = 0;
    private final Stack<RemoteControlStackEntry> mRCStack = new Stack<>();
    private ComponentName mMediaReceiverForCalls = null;
    private ArrayList<DisplayInfoForServer> mRcDisplays = new ArrayList<>(1);

    static /* synthetic */ int access$3004() {
        int i = sLastRccId + 1;
        sLastRccId = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MediaFocusControl(Looper looper, Context cntxt, VolumeController volumeCtrl, AudioService as) {
        this.mEventHandler = new MediaEventHandler(looper);
        this.mContext = cntxt;
        this.mContentResolver = this.mContext.getContentResolver();
        this.mVolumeController = volumeCtrl;
        this.mAudioService = as;
        PowerManager pm = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mMediaEventWakeLock = pm.newWakeLock(1, "handleMediaEvent");
        this.mMainRemote = new RemotePlaybackState(-1, AudioService.getMaxStreamVolume(3), AudioService.getMaxStreamVolume(3));
        TelephonyManager tmgr = (TelephonyManager) this.mContext.getSystemService("phone");
        tmgr.listen(this.mPhoneStateListener, 32);
        IntentFilter pkgFilter = new IntentFilter();
        pkgFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        pkgFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        pkgFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        pkgFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        pkgFilter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        this.mContext.registerReceiver(this.mReceiver, pkgFilter);
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(Context.APP_OPS_SERVICE);
        this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService(Context.KEYGUARD_SERVICE);
        this.mNotifListenerObserver = new NotificationListenerObserver();
        this.mHasRemotePlayback = false;
        this.mMainRemoteIsActive = false;
        postReevaluateRemote();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dump(PrintWriter pw) {
        dumpFocusStack(pw);
        dumpRCStack(pw);
        dumpRCCStack(pw);
        dumpRCDList(pw);
    }

    /* loaded from: MediaFocusControl$NotificationListenerObserver.class */
    private class NotificationListenerObserver extends ContentObserver {
        NotificationListenerObserver() {
            super(MediaFocusControl.this.mEventHandler);
            MediaFocusControl.this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ENABLED_NOTIFICATION_LISTENERS), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (MediaFocusControl.ENABLED_NOTIFICATION_LISTENERS_URI.equals(uri) && !selfChange) {
                MediaFocusControl.this.postReevaluateRemoteControlDisplays();
            }
        }
    }

    private int checkRcdRegistrationAuthorization(ComponentName listenerComp) {
        if (0 == this.mContext.checkCallingOrSelfPermission(Manifest.permission.MEDIA_CONTENT_CONTROL)) {
            return 1;
        }
        if (listenerComp != null) {
            long ident = Binder.clearCallingIdentity();
            try {
                int currentUser = ActivityManager.getCurrentUser();
                String enabledNotifListeners = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, currentUser);
                if (enabledNotifListeners != null) {
                    String[] components = enabledNotifListeners.split(Separators.COLON);
                    for (String str : components) {
                        ComponentName component = ComponentName.unflattenFromString(str);
                        if (component != null && listenerComp.equals(component)) {
                            return 2;
                        }
                    }
                }
                Binder.restoreCallingIdentity(ident);
                return 0;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean registerRemoteController(IRemoteControlDisplay rcd, int w, int h, ComponentName listenerComp) {
        int reg = checkRcdRegistrationAuthorization(listenerComp);
        if (reg != 0) {
            registerRemoteControlDisplay_int(rcd, w, h, listenerComp);
            return true;
        }
        Slog.w(TAG, "Access denied to process: " + Binder.getCallingPid() + ", must have permission " + Manifest.permission.MEDIA_CONTENT_CONTROL + " or be an enabled NotificationListenerService for registerRemoteController");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) {
        int reg = checkRcdRegistrationAuthorization(null);
        if (reg != 0) {
            registerRemoteControlDisplay_int(rcd, w, h, null);
            return true;
        }
        Slog.w(TAG, "Access denied to process: " + Binder.getCallingPid() + ", must have permission " + Manifest.permission.MEDIA_CONTENT_CONTROL + " to register IRemoteControlDisplay");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postReevaluateRemoteControlDisplays() {
        sendMsg(this.mEventHandler, 11, 2, 0, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReevaluateRemoteControlDisplays() {
        String[] enabledComponents;
        int currentUser = ActivityManager.getCurrentUser();
        String enabledNotifListeners = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, currentUser);
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                if (enabledNotifListeners == null) {
                    enabledComponents = null;
                } else {
                    enabledComponents = enabledNotifListeners.split(Separators.COLON);
                }
                Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
                while (displayIterator.hasNext()) {
                    DisplayInfoForServer di = displayIterator.next();
                    if (di.mClientNotifListComp != null) {
                        boolean wasEnabled = di.mEnabled;
                        di.mEnabled = isComponentInStringArray(di.mClientNotifListComp, enabledComponents);
                        if (wasEnabled != di.mEnabled) {
                            try {
                                di.mRcDisplay.setEnabled(di.mEnabled);
                                enableRemoteControlDisplayForClient_syncRcStack(di.mRcDisplay, di.mEnabled);
                                if (di.mEnabled) {
                                    sendMsg(this.mEventHandler, 10, 2, di.mArtworkExpectedWidth, di.mArtworkExpectedHeight, di.mRcDisplay, 0);
                                }
                            } catch (RemoteException e) {
                                Log.e(TAG, "Error en/disabling RCD: ", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isComponentInStringArray(ComponentName comp, String[] enabledArray) {
        if (enabledArray == null || enabledArray.length == 0) {
            return false;
        }
        String compString = comp.flattenToString();
        for (String str : enabledArray) {
            if (compString.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static void sendMsg(Handler handler, int msg, int existingMsgPolicy, int arg1, int arg2, Object obj, int delay) {
        if (existingMsgPolicy == 0) {
            handler.removeMessages(msg);
        } else if (existingMsgPolicy == 1 && handler.hasMessages(msg)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(msg, arg1, arg2, obj), delay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaFocusControl$MediaEventHandler.class */
    public class MediaEventHandler extends Handler {
        MediaEventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MediaFocusControl.this.onHandlePersistMediaButtonReceiver((ComponentName) msg.obj);
                    return;
                case 1:
                    MediaFocusControl.this.onRcDisplayClear();
                    return;
                case 2:
                    MediaFocusControl.this.onRcDisplayUpdate((RemoteControlStackEntry) msg.obj, msg.arg1);
                    return;
                case 3:
                    MediaFocusControl.this.onReevaluateRemote();
                    return;
                case 4:
                    MediaFocusControl.this.onNewPlaybackInfoForRcc(msg.arg1, msg.arg2, ((Integer) msg.obj).intValue());
                    return;
                case 5:
                    MediaFocusControl.this.onRegisterVolumeObserverForRcc(msg.arg1, (IRemoteVolumeObserver) msg.obj);
                    return;
                case 6:
                    MediaFocusControl.this.onPromoteRcc(msg.arg1);
                    return;
                case 7:
                    MediaFocusControl.this.onNewPlaybackStateForRcc(msg.arg1, msg.arg2, (RccPlaybackState) msg.obj);
                    return;
                case 8:
                    MediaFocusControl.this.onSetRemoteControlClientPlaybackPosition(msg.arg1, ((Long) msg.obj).longValue());
                    return;
                case 9:
                    MediaFocusControl.this.onUpdateRemoteControlClientMetadata(msg.arg1, msg.arg2, (Rating) msg.obj);
                    return;
                case 10:
                    MediaFocusControl.this.onRcDisplayInitInfo((IRemoteControlDisplay) msg.obj, msg.arg1, msg.arg2);
                    return;
                case 11:
                    MediaFocusControl.this.onReevaluateRemoteControlDisplays();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void discardAudioFocusOwner() {
        synchronized (mAudioFocusLock) {
            if (!this.mFocusStack.empty()) {
                FocusRequester exFocusOwner = this.mFocusStack.pop();
                exFocusOwner.handleFocusLoss(-1);
                exFocusOwner.release();
                synchronized (this.mRCStack) {
                    clearRemoteControlDisplay_syncAfRcs();
                }
            }
        }
    }

    private void notifyTopOfAudioFocusStack() {
        if (!this.mFocusStack.empty() && canReassignAudioFocus()) {
            this.mFocusStack.peek().handleFocusGain(1);
        }
    }

    private void propagateFocusLossFromGain_syncAf(int focusGain) {
        Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
        while (stackIterator.hasNext()) {
            stackIterator.next().handleExternalFocusGain(focusGain);
        }
    }

    private void dumpFocusStack(PrintWriter pw) {
        pw.println("\nAudio Focus stack entries (last is top of stack):");
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
            while (stackIterator.hasNext()) {
                stackIterator.next().dump(pw);
            }
        }
    }

    private void removeFocusStackEntry(String clientToRemove, boolean signal) {
        if (!this.mFocusStack.empty() && this.mFocusStack.peek().hasSameClient(clientToRemove)) {
            this.mFocusStack.pop().release();
            if (signal) {
                notifyTopOfAudioFocusStack();
                synchronized (this.mRCStack) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
                return;
            }
            return;
        }
        Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
        while (stackIterator.hasNext()) {
            FocusRequester fr = stackIterator.next();
            if (fr.hasSameClient(clientToRemove)) {
                Log.i(TAG, "AudioFocus  removeFocusStackEntry(): removing entry for " + clientToRemove);
                stackIterator.remove();
                fr.release();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFocusStackEntryForClient(IBinder cb) {
        boolean isTopOfStackForClientToRemove = !this.mFocusStack.isEmpty() && this.mFocusStack.peek().hasSameBinder(cb);
        Iterator<FocusRequester> stackIterator = this.mFocusStack.iterator();
        while (stackIterator.hasNext()) {
            FocusRequester fr = stackIterator.next();
            if (fr.hasSameBinder(cb)) {
                Log.i(TAG, "AudioFocus  removeFocusStackEntry(): removing entry for " + cb);
                stackIterator.remove();
            }
        }
        if (isTopOfStackForClientToRemove) {
            notifyTopOfAudioFocusStack();
            synchronized (this.mRCStack) {
                checkUpdateRemoteControlDisplay_syncAfRcs(15);
            }
        }
    }

    private boolean canReassignAudioFocus() {
        if (!this.mFocusStack.isEmpty() && this.mFocusStack.peek().hasSameClient(IN_VOICE_COMM_FOCUS_ID)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: MediaFocusControl$AudioFocusDeathHandler.class */
    public class AudioFocusDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;

        AudioFocusDeathHandler(IBinder cb) {
            this.mCb = cb;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (MediaFocusControl.mAudioFocusLock) {
                Log.w(MediaFocusControl.TAG, "  AudioFocus   audio focus client died");
                MediaFocusControl.this.removeFocusStackEntryForClient(this.mCb);
            }
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getCurrentAudioFocus() {
        synchronized (mAudioFocusLock) {
            if (this.mFocusStack.empty()) {
                return 0;
            }
            return this.mFocusStack.peek().getGainRequest();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int requestAudioFocus(int mainStreamType, int focusChangeHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName) {
        Log.i(TAG, " AudioFocus  requestAudioFocus() from " + clientId);
        if (!cb.pingBinder()) {
            Log.e(TAG, " AudioFocus DOA client for requestAudioFocus(), aborting.");
            return 0;
        } else if (this.mAppOps.noteOp(32, Binder.getCallingUid(), callingPackageName) != 0) {
            return 0;
        } else {
            synchronized (mAudioFocusLock) {
                if (!canReassignAudioFocus()) {
                    return 0;
                }
                AudioFocusDeathHandler afdh = new AudioFocusDeathHandler(cb);
                try {
                    cb.linkToDeath(afdh, 0);
                    if (!this.mFocusStack.empty() && this.mFocusStack.peek().hasSameClient(clientId)) {
                        if (this.mFocusStack.peek().getGainRequest() == focusChangeHint) {
                            cb.unlinkToDeath(afdh, 0);
                            return 1;
                        }
                        FocusRequester fr = this.mFocusStack.pop();
                        fr.release();
                    }
                    removeFocusStackEntry(clientId, false);
                    if (!this.mFocusStack.empty()) {
                        propagateFocusLossFromGain_syncAf(focusChangeHint);
                    }
                    this.mFocusStack.push(new FocusRequester(mainStreamType, focusChangeHint, fd, cb, clientId, afdh, callingPackageName, Binder.getCallingUid()));
                    synchronized (this.mRCStack) {
                        checkUpdateRemoteControlDisplay_syncAfRcs(15);
                    }
                    return 1;
                } catch (RemoteException e) {
                    Log.w(TAG, "AudioFocus  requestAudioFocus() could not link to " + cb + " binder death");
                    return 0;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int abandonAudioFocus(IAudioFocusDispatcher fl, String clientId) {
        Log.i(TAG, " AudioFocus  abandonAudioFocus() from " + clientId);
        try {
            synchronized (mAudioFocusLock) {
                removeFocusStackEntry(clientId, true);
            }
            return 1;
        } catch (ConcurrentModificationException cme) {
            Log.e(TAG, "FATAL EXCEPTION AudioFocus  abandonAudioFocus() caused " + cme);
            cme.printStackTrace();
            return 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterAudioFocusClient(String clientId) {
        synchronized (mAudioFocusLock) {
            removeFocusStackEntry(clientId, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        filterMediaKeyEvent(keyEvent, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) {
        filterMediaKeyEvent(keyEvent, true);
    }

    private void filterMediaKeyEvent(KeyEvent keyEvent, boolean needWakeLock) {
        if (!isValidMediaKeyEvent(keyEvent)) {
            Log.e(TAG, "not dispatching invalid media key event " + keyEvent);
            return;
        }
        synchronized (mRingingLock) {
            synchronized (this.mRCStack) {
                if (this.mMediaReceiverForCalls != null && (this.mIsRinging || this.mAudioService.getMode() == 2)) {
                    dispatchMediaKeyEventForCalls(keyEvent, needWakeLock);
                } else if (isValidVoiceInputKeyCode(keyEvent.getKeyCode())) {
                    filterVoiceInputKeyEvent(keyEvent, needWakeLock);
                } else {
                    dispatchMediaKeyEvent(keyEvent, needWakeLock);
                }
            }
        }
    }

    private void dispatchMediaKeyEventForCalls(KeyEvent keyEvent, boolean needWakeLock) {
        Intent keyIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, (Uri) null);
        keyIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        keyIntent.setPackage(this.mMediaReceiverForCalls.getPackageName());
        if (needWakeLock) {
            this.mMediaEventWakeLock.acquire();
            keyIntent.putExtra(EXTRA_WAKELOCK_ACQUIRED, WAKELOCK_RELEASE_ON_FINISHED);
        }
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendOrderedBroadcastAsUser(keyIntent, UserHandle.ALL, null, this.mKeyEventDone, this.mEventHandler, -1, null, null);
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    private void dispatchMediaKeyEvent(KeyEvent keyEvent, boolean needWakeLock) {
        if (needWakeLock) {
            this.mMediaEventWakeLock.acquire();
        }
        Intent keyIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, (Uri) null);
        keyIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        synchronized (this.mRCStack) {
            if (!this.mRCStack.empty()) {
                try {
                    this.mRCStack.peek().mMediaIntent.send(this.mContext, needWakeLock ? WAKELOCK_RELEASE_ON_FINISHED : 0, keyIntent, this, this.mEventHandler);
                } catch (PendingIntent.CanceledException e) {
                    Log.e(TAG, "Error sending pending intent " + this.mRCStack.peek());
                    e.printStackTrace();
                }
            } else {
                if (needWakeLock) {
                    keyIntent.putExtra(EXTRA_WAKELOCK_ACQUIRED, WAKELOCK_RELEASE_ON_FINISHED);
                }
                long ident = Binder.clearCallingIdentity();
                this.mContext.sendOrderedBroadcastAsUser(keyIntent, UserHandle.ALL, null, this.mKeyEventDone, this.mEventHandler, -1, null, null);
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    private void filterVoiceInputKeyEvent(KeyEvent keyEvent, boolean needWakeLock) {
        int voiceButtonAction = 1;
        int keyAction = keyEvent.getAction();
        synchronized (this.mVoiceEventLock) {
            if (keyAction == 0) {
                if (keyEvent.getRepeatCount() == 0) {
                    this.mVoiceButtonDown = true;
                    this.mVoiceButtonHandled = false;
                } else if (this.mVoiceButtonDown && !this.mVoiceButtonHandled && (keyEvent.getFlags() & 128) != 0) {
                    this.mVoiceButtonHandled = true;
                    voiceButtonAction = 2;
                }
            } else if (keyAction == 1 && this.mVoiceButtonDown) {
                this.mVoiceButtonDown = false;
                if (!this.mVoiceButtonHandled && !keyEvent.isCanceled()) {
                    voiceButtonAction = 3;
                }
            }
        }
        switch (voiceButtonAction) {
            case 1:
            default:
                return;
            case 2:
                startVoiceBasedInteractions(needWakeLock);
                return;
            case 3:
                sendSimulatedMediaButtonEvent(keyEvent, needWakeLock);
                return;
        }
    }

    private void sendSimulatedMediaButtonEvent(KeyEvent originalKeyEvent, boolean needWakeLock) {
        KeyEvent keyEvent = KeyEvent.changeAction(originalKeyEvent, 0);
        dispatchMediaKeyEvent(keyEvent, needWakeLock);
        KeyEvent keyEvent2 = KeyEvent.changeAction(originalKeyEvent, 1);
        dispatchMediaKeyEvent(keyEvent2, needWakeLock);
    }

    /* loaded from: MediaFocusControl$PackageIntentsReceiver.class */
    private class PackageIntentsReceiver extends BroadcastReceiver {
        private PackageIntentsReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String packageName;
            String packageName2;
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED) || action.equals(Intent.ACTION_PACKAGE_DATA_CLEARED)) {
                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false) && (packageName = intent.getData().getSchemeSpecificPart()) != null) {
                    MediaFocusControl.this.cleanupMediaButtonReceiverForPackage(packageName, true);
                }
            } else if ((action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_CHANGED)) && (packageName2 = intent.getData().getSchemeSpecificPart()) != null) {
                MediaFocusControl.this.cleanupMediaButtonReceiverForPackage(packageName2, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isMediaKeyCode(int keyCode) {
        switch (keyCode) {
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 222:
                return true;
            default:
                return false;
        }
    }

    private static boolean isValidMediaKeyEvent(KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        return isMediaKeyCode(keyEvent.getKeyCode());
    }

    private static boolean isValidVoiceInputKeyCode(int keyCode) {
        if (keyCode == 79) {
            return true;
        }
        return false;
    }

    private void startVoiceBasedInteractions(boolean needWakeLock) {
        Intent voiceIntent;
        PowerManager pm = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        boolean isLocked = this.mKeyguardManager != null && this.mKeyguardManager.isKeyguardLocked();
        if (!isLocked && pm.isScreenOn()) {
            voiceIntent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            Log.i(TAG, "voice-based interactions: about to use ACTION_WEB_SEARCH");
        } else {
            voiceIntent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
            voiceIntent.putExtra(RecognizerIntent.EXTRA_SECURE, isLocked && this.mKeyguardManager.isKeyguardSecure());
            Log.i(TAG, "voice-based interactions: about to use ACTION_VOICE_SEARCH_HANDS_FREE");
        }
        if (needWakeLock) {
            this.mMediaEventWakeLock.acquire();
        }
        long identity = Binder.clearCallingIdentity();
        if (voiceIntent != null) {
            try {
                try {
                    voiceIntent.setFlags(276824064);
                    this.mContext.startActivityAsUser(voiceIntent, UserHandle.CURRENT);
                } catch (ActivityNotFoundException e) {
                    Log.w(TAG, "No activity for search: " + e);
                    Binder.restoreCallingIdentity(identity);
                    if (needWakeLock) {
                        this.mMediaEventWakeLock.release();
                        return;
                    }
                    return;
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
                if (needWakeLock) {
                    this.mMediaEventWakeLock.release();
                }
            }
        }
    }

    @Override // android.app.PendingIntent.OnFinished
    public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
        if (resultCode == WAKELOCK_RELEASE_ON_FINISHED) {
            this.mMediaEventWakeLock.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaFocusControl$RcClientDeathHandler.class */
    public class RcClientDeathHandler implements IBinder.DeathRecipient {
        private final IBinder mCb;
        private final PendingIntent mMediaIntent;

        RcClientDeathHandler(IBinder cb, PendingIntent pi) {
            this.mCb = cb;
            this.mMediaIntent = pi;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w(MediaFocusControl.TAG, "  RemoteControlClient died");
            MediaFocusControl.this.registerRemoteControlClient(this.mMediaIntent, null, null);
            MediaFocusControl.this.postReevaluateRemote();
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaFocusControl$RemotePlaybackState.class */
    public class RemotePlaybackState {
        int mRccId;
        int mVolume;
        int mVolumeMax;
        int mVolumeHandling;

        private RemotePlaybackState(int id, int vol, int volMax) {
            this.mRccId = id;
            this.mVolume = vol;
            this.mVolumeMax = volMax;
            this.mVolumeHandling = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaFocusControl$RccPlaybackState.class */
    public static class RccPlaybackState {
        public int mState;
        public long mPositionMs;
        public float mSpeed;

        public RccPlaybackState(int state, long positionMs, float speed) {
            this.mState = state;
            this.mPositionMs = positionMs;
            this.mSpeed = speed;
        }

        public void reset() {
            this.mState = 1;
            this.mPositionMs = -1L;
            this.mSpeed = 1.0f;
        }

        public String toString() {
            return stateToString() + ", " + posToString() + ", " + this.mSpeed + TokenNames.X;
        }

        private String posToString() {
            if (this.mPositionMs == -1) {
                return "PLAYBACK_POSITION_INVALID";
            }
            if (this.mPositionMs == RemoteControlClient.PLAYBACK_POSITION_ALWAYS_UNKNOWN) {
                return "PLAYBACK_POSITION_ALWAYS_UNKNOWN";
            }
            return String.valueOf(this.mPositionMs) + "ms";
        }

        private String stateToString() {
            switch (this.mState) {
                case 0:
                    return "PLAYSTATE_NONE";
                case 1:
                    return "PLAYSTATE_STOPPED";
                case 2:
                    return "PLAYSTATE_PAUSED";
                case 3:
                    return "PLAYSTATE_PLAYING";
                case 4:
                    return "PLAYSTATE_FAST_FORWARDING";
                case 5:
                    return "PLAYSTATE_REWINDING";
                case 6:
                    return "PLAYSTATE_SKIPPING_FORWARDS";
                case 7:
                    return "PLAYSTATE_SKIPPING_BACKWARDS";
                case 8:
                    return "PLAYSTATE_BUFFERING";
                case 9:
                    return "PLAYSTATE_ERROR";
                default:
                    return "[invalid playstate]";
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: MediaFocusControl$RemoteControlStackEntry.class */
    public static class RemoteControlStackEntry implements IBinder.DeathRecipient {
        public int mRccId;
        public final MediaFocusControl mController;
        public final PendingIntent mMediaIntent;
        public final ComponentName mReceiverComponent;
        public IBinder mToken;
        public String mCallingPackageName;
        public RcClientDeathHandler mRcClientDeathHandler;
        public int mPlaybackType;
        public int mPlaybackVolume;
        public int mPlaybackVolumeMax;
        public int mPlaybackVolumeHandling;
        public int mPlaybackStream;
        public IRemoteVolumeObserver mRemoteVolumeObs;
        public int mCallingUid = -1;
        public IRemoteControlClient mRcClient = null;
        public RccPlaybackState mPlaybackState = new RccPlaybackState(1, -1, 1.0f);

        public void resetPlaybackInfo() {
            this.mPlaybackType = 0;
            this.mPlaybackVolume = 15;
            this.mPlaybackVolumeMax = 15;
            this.mPlaybackVolumeHandling = 1;
            this.mPlaybackStream = 3;
            this.mPlaybackState.reset();
            this.mRemoteVolumeObs = null;
        }

        public RemoteControlStackEntry(MediaFocusControl controller, PendingIntent mediaIntent, ComponentName eventReceiver, IBinder token) {
            this.mRccId = -1;
            this.mController = controller;
            this.mMediaIntent = mediaIntent;
            this.mReceiverComponent = eventReceiver;
            this.mToken = token;
            this.mRccId = MediaFocusControl.access$3004();
            resetPlaybackInfo();
            if (this.mToken != null) {
                try {
                    this.mToken.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    this.mController.mEventHandler.post(new Runnable() { // from class: android.media.MediaFocusControl.RemoteControlStackEntry.1
                        @Override // java.lang.Runnable
                        public void run() {
                            RemoteControlStackEntry.this.mController.unregisterMediaButtonIntent(RemoteControlStackEntry.this.mMediaIntent);
                        }
                    });
                }
            }
        }

        public void unlinkToRcClientDeath() {
            if (this.mRcClientDeathHandler != null && this.mRcClientDeathHandler.mCb != null) {
                try {
                    this.mRcClientDeathHandler.mCb.unlinkToDeath(this.mRcClientDeathHandler, 0);
                    this.mRcClientDeathHandler = null;
                } catch (NoSuchElementException e) {
                    Log.e(MediaFocusControl.TAG, "Encountered " + e + " in unlinkToRcClientDeath()");
                    e.printStackTrace();
                }
            }
        }

        public void destroy() {
            unlinkToRcClientDeath();
            if (this.mToken != null) {
                this.mToken.unlinkToDeath(this, 0);
                this.mToken = null;
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            this.mController.unregisterMediaButtonIntent(this.mMediaIntent);
        }

        protected void finalize() throws Throwable {
            destroy();
            super.finalize();
        }
    }

    private void dumpRCStack(PrintWriter pw) {
        pw.println("\nRemote Control stack entries (last is top of stack):");
        synchronized (this.mRCStack) {
            Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
            while (stackIterator.hasNext()) {
                RemoteControlStackEntry rcse = stackIterator.next();
                pw.println("  pi: " + rcse.mMediaIntent + " -- pack: " + rcse.mCallingPackageName + "  -- ercvr: " + rcse.mReceiverComponent + "  -- client: " + rcse.mRcClient + "  -- uid: " + rcse.mCallingUid + "  -- type: " + rcse.mPlaybackType + "  state: " + rcse.mPlaybackState);
            }
        }
    }

    private void dumpRCCStack(PrintWriter pw) {
        pw.println("\nRemote Control Client stack entries (last is top of stack):");
        synchronized (this.mRCStack) {
            Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
            while (stackIterator.hasNext()) {
                RemoteControlStackEntry rcse = stackIterator.next();
                pw.println("  uid: " + rcse.mCallingUid + "  -- id: " + rcse.mRccId + "  -- type: " + rcse.mPlaybackType + "  -- state: " + rcse.mPlaybackState + "  -- vol handling: " + rcse.mPlaybackVolumeHandling + "  -- vol: " + rcse.mPlaybackVolume + "  -- volMax: " + rcse.mPlaybackVolumeMax + "  -- volObs: " + rcse.mRemoteVolumeObs);
            }
            synchronized (this.mCurrentRcLock) {
                pw.println("\nCurrent remote control generation ID = " + this.mCurrentRcClientGen);
            }
        }
        synchronized (this.mMainRemote) {
            pw.println("\nRemote Volume State:");
            pw.println("  has remote: " + this.mHasRemotePlayback);
            pw.println("  is remote active: " + this.mMainRemoteIsActive);
            pw.println("  rccId: " + this.mMainRemote.mRccId);
            pw.println("  volume handling: " + (this.mMainRemote.mVolumeHandling == 0 ? "PLAYBACK_VOLUME_FIXED(0)" : "PLAYBACK_VOLUME_VARIABLE(1)"));
            pw.println("  volume: " + this.mMainRemote.mVolume);
            pw.println("  volume steps: " + this.mMainRemote.mVolumeMax);
        }
    }

    private void dumpRCDList(PrintWriter pw) {
        pw.println("\nRemote Control Display list entries:");
        synchronized (this.mRCStack) {
            Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForServer di = displayIterator.next();
                pw.println("  IRCD: " + di.mRcDisplay + "  -- w:" + di.mArtworkExpectedWidth + "  -- h:" + di.mArtworkExpectedHeight + "  -- wantsPosSync:" + di.mWantsPositionSync + "  -- " + (di.mEnabled ? "enabled" : "disabled"));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanupMediaButtonReceiverForPackage(String packageName, boolean removeAll) {
        synchronized (this.mRCStack) {
            if (this.mRCStack.empty()) {
                return;
            }
            PackageManager pm = this.mContext.getPackageManager();
            RemoteControlStackEntry oldTop = this.mRCStack.peek();
            Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
            while (stackIterator.hasNext()) {
                RemoteControlStackEntry rcse = stackIterator.next();
                if (removeAll && packageName.equals(rcse.mMediaIntent.getCreatorPackage())) {
                    stackIterator.remove();
                    rcse.destroy();
                } else if (rcse.mReceiverComponent != null) {
                    try {
                        pm.getReceiverInfo(rcse.mReceiverComponent, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        stackIterator.remove();
                        rcse.destroy();
                    }
                }
            }
            if (this.mRCStack.empty()) {
                this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(0, 0, 0, null));
            } else if (oldTop != this.mRCStack.peek()) {
                RemoteControlStackEntry rcse2 = this.mRCStack.peek();
                if (rcse2.mReceiverComponent != null) {
                    this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(0, 0, 0, rcse2.mReceiverComponent));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void restoreMediaButtonReceiver() {
        ComponentName eventReceiver;
        String receiverName = Settings.System.getStringForUser(this.mContentResolver, Settings.System.MEDIA_BUTTON_RECEIVER, -2);
        if (null == receiverName || receiverName.isEmpty() || (eventReceiver = ComponentName.unflattenFromString(receiverName)) == null) {
            return;
        }
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(eventReceiver);
        PendingIntent pi = PendingIntent.getBroadcast(this.mContext, 0, mediaButtonIntent, 0);
        registerMediaButtonIntent(pi, eventReceiver, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0063, code lost:
        r7.mRCStack.removeElementAt(r13);
        r12 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void pushMediaButtonReceiver_syncAfRcs(android.app.PendingIntent r8, android.content.ComponentName r9, android.os.IBinder r10) {
        /*
            r7 = this;
            r0 = r7
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack
            boolean r0 = r0.empty()
            if (r0 != 0) goto L1f
            r0 = r7
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack
            java.lang.Object r0 = r0.peek()
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0
            android.app.PendingIntent r0 = r0.mMediaIntent
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L1f
            return
        L1f:
            r0 = r7
            android.app.AppOpsManager r0 = r0.mAppOps
            r1 = 31
            int r2 = android.os.Binder.getCallingUid()
            r3 = r8
            java.lang.String r3 = r3.getCreatorPackage()
            int r0 = r0.noteOp(r1, r2, r3)
            if (r0 == 0) goto L33
            return
        L33:
            r0 = 0
            r11 = r0
            r0 = 0
            r12 = r0
            r0 = r7
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            int r0 = r0.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            r1 = 1
            int r0 = r0 - r1
            r13 = r0
        L44:
            r0 = r13
            if (r0 < 0) goto L78
            r0 = r7
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            r1 = r13
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            r11 = r0
            r0 = r11
            android.app.PendingIntent r0 = r0.mMediaIntent     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            r1 = r8
            boolean r0 = r0.equals(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            if (r0 == 0) goto L72
            r0 = r7
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            r1 = r13
            r0.removeElementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L7b
            r0 = 1
            r12 = r0
            goto L78
        L72:
            int r13 = r13 + (-1)
            goto L44
        L78:
            goto L88
        L7b:
            r13 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing media button stack, lock error? "
            r2 = r13
            int r0 = android.util.Log.e(r0, r1, r2)
        L88:
            r0 = r12
            if (r0 != 0) goto L9a
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = new android.media.MediaFocusControl$RemoteControlStackEntry
            r1 = r0
            r2 = r7
            r3 = r8
            r4 = r9
            r5 = r10
            r1.<init>(r2, r3, r4, r5)
            r11 = r0
        L9a:
            r0 = r7
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack
            r1 = r11
            java.lang.Object r0 = r0.push(r1)
            r0 = r9
            if (r0 == 0) goto Lbb
            r0 = r7
            android.media.MediaFocusControl$MediaEventHandler r0 = r0.mEventHandler
            r1 = r7
            android.media.MediaFocusControl$MediaEventHandler r1 = r1.mEventHandler
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = r9
            android.os.Message r1 = r1.obtainMessage(r2, r3, r4, r5)
            boolean r0 = r0.sendMessage(r1)
        Lbb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.pushMediaButtonReceiver_syncAfRcs(android.app.PendingIntent, android.content.ComponentName, android.os.IBinder):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x0025, code lost:
        r0.destroy();
        r4.mRCStack.removeElementAt(r6);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void removeMediaButtonReceiver_syncAfRcs(android.app.PendingIntent r5) {
        /*
            r4 = this;
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            int r0 = r0.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            r1 = 1
            int r0 = r0 - r1
            r6 = r0
        La:
            r0 = r6
            if (r0 < 0) goto L3a
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            r1 = r6
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            r7 = r0
            r0 = r7
            android.app.PendingIntent r0 = r0.mMediaIntent     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            r1 = r5
            boolean r0 = r0.equals(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            if (r0 == 0) goto L34
            r0 = r7
            r0.destroy()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            r1 = r6
            r0.removeElementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L3d
            goto L3a
        L34:
            int r6 = r6 + (-1)
            goto La
        L3a:
            goto L48
        L3d:
            r6 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing media button stack, lock error? "
            r2 = r6
            int r0 = android.util.Log.e(r0, r1, r2)
        L48:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.removeMediaButtonReceiver_syncAfRcs(android.app.PendingIntent):void");
    }

    private boolean isCurrentRcController(PendingIntent pi) {
        if (!this.mRCStack.empty() && this.mRCStack.peek().mMediaIntent.equals(pi)) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHandlePersistMediaButtonReceiver(ComponentName receiver) {
        Settings.System.putStringForUser(this.mContentResolver, Settings.System.MEDIA_BUTTON_RECEIVER, receiver == null ? "" : receiver.flattenToString(), -2);
    }

    private void setNewRcClientOnDisplays_syncRcsCurrc(int newClientGeneration, PendingIntent newMediaIntent, boolean clearing) {
        synchronized (this.mRCStack) {
            if (this.mRcDisplays.size() > 0) {
                Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
                while (displayIterator.hasNext()) {
                    DisplayInfoForServer di = displayIterator.next();
                    try {
                        di.mRcDisplay.setCurrentClientId(newClientGeneration, newMediaIntent, clearing);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Dead display in setNewRcClientOnDisplays_syncRcsCurrc()", e);
                        di.release();
                        displayIterator.remove();
                    }
                }
            }
        }
    }

    private void setNewRcClientGenerationOnClients_syncRcsCurrc(int newClientGeneration) {
        Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
        while (stackIterator.hasNext()) {
            RemoteControlStackEntry se = stackIterator.next();
            if (se != null && se.mRcClient != null) {
                try {
                    se.mRcClient.setCurrentClientGenerationId(newClientGeneration);
                } catch (RemoteException e) {
                    Log.w(TAG, "Dead client in setNewRcClientGenerationOnClients_syncRcsCurrc()", e);
                    stackIterator.remove();
                    se.unlinkToRcClientDeath();
                }
            }
        }
    }

    private void setNewRcClient_syncRcsCurrc(int newClientGeneration, PendingIntent newMediaIntent, boolean clearing) {
        setNewRcClientOnDisplays_syncRcsCurrc(newClientGeneration, newMediaIntent, clearing);
        setNewRcClientGenerationOnClients_syncRcsCurrc(newClientGeneration);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRcDisplayClear() {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                this.mCurrentRcClientGen++;
                setNewRcClient_syncRcsCurrc(this.mCurrentRcClientGen, null, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRcDisplayUpdate(RemoteControlStackEntry rcse, int flags) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClient != null && this.mCurrentRcClient.equals(rcse.mRcClient)) {
                    this.mCurrentRcClientGen++;
                    setNewRcClient_syncRcsCurrc(this.mCurrentRcClientGen, rcse.mMediaIntent, false);
                    try {
                        this.mCurrentRcClient.onInformationRequested(this.mCurrentRcClientGen, flags);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Current valid remote client is dead: " + e);
                        this.mCurrentRcClient = null;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRcDisplayInitInfo(IRemoteControlDisplay newRcd, int w, int h) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClient != null) {
                    try {
                        newRcd.setCurrentClientId(this.mCurrentRcClientGen, this.mCurrentRcClientIntent, false);
                        try {
                            this.mCurrentRcClient.informationRequestForDisplay(newRcd, w, h);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Current valid remote client is dead: ", e);
                            this.mCurrentRcClient = null;
                        }
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Dead display in onRcDisplayInitInfo()", e2);
                    }
                }
            }
        }
    }

    private void clearRemoteControlDisplay_syncAfRcs() {
        synchronized (this.mCurrentRcLock) {
            this.mCurrentRcClient = null;
        }
        this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(1));
    }

    private void updateRemoteControlDisplay_syncAfRcs(int infoChangedFlags) {
        RemoteControlStackEntry rcse = this.mRCStack.peek();
        int infoFlagsAboutToBeUsed = infoChangedFlags;
        if (rcse.mRcClient == null) {
            clearRemoteControlDisplay_syncAfRcs();
            return;
        }
        synchronized (this.mCurrentRcLock) {
            if (!rcse.mRcClient.equals(this.mCurrentRcClient)) {
                infoFlagsAboutToBeUsed = 15;
            }
            this.mCurrentRcClient = rcse.mRcClient;
            this.mCurrentRcClientIntent = rcse.mMediaIntent;
        }
        this.mEventHandler.sendMessage(this.mEventHandler.obtainMessage(2, infoFlagsAboutToBeUsed, 0, rcse));
    }

    private void checkUpdateRemoteControlDisplay_syncAfRcs(int infoChangedFlags) {
        if (this.mRCStack.isEmpty() || this.mFocusStack.isEmpty()) {
            clearRemoteControlDisplay_syncAfRcs();
            return;
        }
        FocusRequester af = null;
        try {
            for (int index = this.mFocusStack.size() - 1; index >= 0; index--) {
                FocusRequester fr = this.mFocusStack.elementAt(index);
                if (fr.getStreamType() == 3 || fr.getGainRequest() == 1) {
                    af = fr;
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Wrong index accessing audio focus stack when updating RCD: " + e);
            af = null;
        }
        if (af == null) {
            clearRemoteControlDisplay_syncAfRcs();
        } else if (!af.hasSamePackage(this.mRCStack.peek().mCallingPackageName)) {
            clearRemoteControlDisplay_syncAfRcs();
        } else if (!af.hasSameUid(this.mRCStack.peek().mCallingUid)) {
            clearRemoteControlDisplay_syncAfRcs();
        } else {
            updateRemoteControlDisplay_syncAfRcs(infoChangedFlags);
        }
    }

    private void postPromoteRcc(int rccId) {
        sendMsg(this.mEventHandler, 6, 0, rccId, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPromoteRcc(int rccId) {
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                if (this.mRCStack.isEmpty() || this.mRCStack.peek().mRccId != rccId) {
                    int indexToPromote = -1;
                    try {
                        int index = this.mRCStack.size() - 1;
                        while (true) {
                            if (index < 0) {
                                break;
                            }
                            RemoteControlStackEntry rcse = this.mRCStack.elementAt(index);
                            if (rcse.mRccId != rccId) {
                                index--;
                            } else {
                                indexToPromote = index;
                                break;
                            }
                        }
                        if (indexToPromote >= 0) {
                            RemoteControlStackEntry rcse2 = this.mRCStack.remove(indexToPromote);
                            this.mRCStack.push(rcse2);
                            checkUpdateRemoteControlDisplay_syncAfRcs(15);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e(TAG, "Wrong index accessing RC stack, lock error? ", e);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerMediaButtonIntent(PendingIntent mediaIntent, ComponentName eventReceiver, IBinder token) {
        Log.i(TAG, "  Remote Control   registerMediaButtonIntent() for " + mediaIntent);
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                pushMediaButtonReceiver_syncAfRcs(mediaIntent, eventReceiver, token);
                checkUpdateRemoteControlDisplay_syncAfRcs(15);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterMediaButtonIntent(PendingIntent mediaIntent) {
        Log.i(TAG, "  Remote Control   unregisterMediaButtonIntent() for " + mediaIntent);
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                boolean topOfStackWillChange = isCurrentRcController(mediaIntent);
                removeMediaButtonReceiver_syncAfRcs(mediaIntent);
                if (topOfStackWillChange) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerMediaButtonEventReceiverForCalls(ComponentName c) {
        if (this.mContext.checkCallingPermission(Manifest.permission.MODIFY_PHONE_STATE) != 0) {
            Log.e(TAG, "Invalid permissions to register media button receiver for calls");
            return;
        }
        synchronized (this.mRCStack) {
            this.mMediaReceiverForCalls = c;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterMediaButtonEventReceiverForCalls() {
        if (this.mContext.checkCallingPermission(Manifest.permission.MODIFY_PHONE_STATE) != 0) {
            Log.e(TAG, "Invalid permissions to unregister media button receiver for calls");
            return;
        }
        synchronized (this.mRCStack) {
            this.mMediaReceiverForCalls = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int registerRemoteControlClient(PendingIntent mediaIntent, IRemoteControlClient rcClient, String callingPackageName) {
        int rccId = -1;
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                try {
                    int index = this.mRCStack.size() - 1;
                    while (true) {
                        if (index < 0) {
                            break;
                        }
                        RemoteControlStackEntry rcse = this.mRCStack.elementAt(index);
                        if (!rcse.mMediaIntent.equals(mediaIntent)) {
                            index--;
                        } else {
                            if (rcse.mRcClientDeathHandler != null) {
                                rcse.unlinkToRcClientDeath();
                            }
                            rcse.mRcClient = rcClient;
                            rcse.mCallingPackageName = callingPackageName;
                            rcse.mCallingUid = Binder.getCallingUid();
                            if (rcClient == null) {
                                rcse.resetPlaybackInfo();
                            } else {
                                rccId = rcse.mRccId;
                                if (this.mRcDisplays.size() > 0) {
                                    plugRemoteControlDisplaysIntoClient_syncRcStack(rcse.mRcClient);
                                }
                                IBinder b = rcse.mRcClient.asBinder();
                                RcClientDeathHandler rcdh = new RcClientDeathHandler(b, rcse.mMediaIntent);
                                try {
                                    b.linkToDeath(rcdh, 0);
                                } catch (RemoteException e) {
                                    Log.w(TAG, "registerRemoteControlClient() has a dead client " + b);
                                    rcse.mRcClient = null;
                                }
                                rcse.mRcClientDeathHandler = rcdh;
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e2) {
                    Log.e(TAG, "Wrong index accessing RC stack, lock error? ", e2);
                }
                if (isCurrentRcController(mediaIntent)) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
            }
        }
        return rccId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0047, code lost:
        r0.unlinkToRcClientDeath();
        r0.mRcClient = null;
        r0.mCallingPackageName = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0063, code lost:
        if (r10 != (r4.mRCStack.size() - 1)) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0066, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x006a, code lost:
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x006b, code lost:
        r9 = r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void unregisterRemoteControlClient(android.app.PendingIntent r5, android.media.IRemoteControlClient r6) {
        /*
            r4 = this;
            java.lang.Object r0 = android.media.MediaFocusControl.mAudioFocusLock
            r1 = r0
            r7 = r1
            monitor-enter(r0)
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.Throwable -> La4
            r1 = r0
            r8 = r1
            monitor-enter(r0)     // Catch: java.lang.Throwable -> La4
            r0 = 0
            r9 = r0
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            int r0 = r0.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
        L1c:
            r0 = r10
            if (r0 < 0) goto L76
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r1 = r10
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r11 = r0
            r0 = r11
            android.app.PendingIntent r0 = r0.mMediaIntent     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r1 = r5
            boolean r0 = r0.equals(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            if (r0 == 0) goto L70
            r0 = r6
            r1 = r11
            android.media.IRemoteControlClient r1 = r1.mRcClient     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            boolean r0 = r0.equals(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            if (r0 == 0) goto L70
            r0 = r11
            r0.unlinkToRcClientDeath()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r0 = r11
            r1 = 0
            r0.mRcClient = r1     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r0 = r11
            r1 = 0
            r0.mCallingPackageName = r1     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r0 = r10
            r1 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r1 = r1.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            int r1 = r1.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L79 java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r2 = 1
            int r1 = r1 - r2
            if (r0 != r1) goto L6a
            r0 = 1
            goto L6b
        L6a:
            r0 = 0
        L6b:
            r9 = r0
            goto L76
        L70:
            int r10 = r10 + (-1)
            goto L1c
        L76:
            goto L86
        L79:
            r10 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing RC stack, lock error? "
            r2 = r10
            int r0 = android.util.Log.e(r0, r1, r2)     // Catch: java.lang.Throwable -> L97 java.lang.Throwable -> La4
        L86:
            r0 = r9
            if (r0 == 0) goto L91
            r0 = r4
            r1 = 15
            r0.checkUpdateRemoteControlDisplay_syncAfRcs(r1)     // Catch: java.lang.Throwable -> L97 java.lang.Throwable -> La4
        L91:
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97 java.lang.Throwable -> La4
            goto L9f
        L97:
            r12 = move-exception
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L97 java.lang.Throwable -> La4
            r0 = r12
            throw r0     // Catch: java.lang.Throwable -> La4
        L9f:
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La4
            goto Lab
        La4:
            r13 = move-exception
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La4
            r0 = r13
            throw r0
        Lab:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.unregisterRemoteControlClient(android.app.PendingIntent, android.media.IRemoteControlClient):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaFocusControl$DisplayInfoForServer.class */
    public class DisplayInfoForServer implements IBinder.DeathRecipient {
        private final IRemoteControlDisplay mRcDisplay;
        private final IBinder mRcDisplayBinder;
        private int mArtworkExpectedWidth;
        private int mArtworkExpectedHeight;
        private ComponentName mClientNotifListComp;
        private boolean mWantsPositionSync = false;
        private boolean mEnabled = true;

        public DisplayInfoForServer(IRemoteControlDisplay rcd, int w, int h) {
            this.mArtworkExpectedWidth = -1;
            this.mArtworkExpectedHeight = -1;
            this.mRcDisplay = rcd;
            this.mRcDisplayBinder = rcd.asBinder();
            this.mArtworkExpectedWidth = w;
            this.mArtworkExpectedHeight = h;
        }

        public boolean init() {
            try {
                this.mRcDisplayBinder.linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                Log.w(MediaFocusControl.TAG, "registerRemoteControlDisplay() has a dead client " + this.mRcDisplayBinder);
                return false;
            }
        }

        public void release() {
            try {
                this.mRcDisplayBinder.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
                Log.e(MediaFocusControl.TAG, "Error in DisplaInfoForServer.relase()", e);
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (MediaFocusControl.this.mRCStack) {
                Log.w(MediaFocusControl.TAG, "RemoteControl: display " + this.mRcDisplay + " died");
                Iterator<DisplayInfoForServer> displayIterator = MediaFocusControl.this.mRcDisplays.iterator();
                while (displayIterator.hasNext()) {
                    DisplayInfoForServer di = displayIterator.next();
                    if (di.mRcDisplay == this.mRcDisplay) {
                        displayIterator.remove();
                        return;
                    }
                }
            }
        }
    }

    private void plugRemoteControlDisplaysIntoClient_syncRcStack(IRemoteControlClient rcc) {
        Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
        while (displayIterator.hasNext()) {
            DisplayInfoForServer di = displayIterator.next();
            try {
                rcc.plugRemoteControlDisplay(di.mRcDisplay, di.mArtworkExpectedWidth, di.mArtworkExpectedHeight);
                if (di.mWantsPositionSync) {
                    rcc.setWantsSyncForDisplay(di.mRcDisplay, true);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error connecting RCD to RCC in RCC registration", e);
            }
        }
    }

    private void enableRemoteControlDisplayForClient_syncRcStack(IRemoteControlDisplay rcd, boolean enabled) {
        Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
        while (stackIterator.hasNext()) {
            RemoteControlStackEntry rcse = stackIterator.next();
            if (rcse.mRcClient != null) {
                try {
                    rcse.mRcClient.enableRemoteControlDisplay(rcd, enabled);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error connecting RCD to client: ", e);
                }
            }
        }
    }

    private boolean rcDisplayIsPluggedIn_syncRcStack(IRemoteControlDisplay rcd) {
        Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
        while (displayIterator.hasNext()) {
            DisplayInfoForServer di = displayIterator.next();
            if (di.mRcDisplay.asBinder().equals(rcd.asBinder())) {
                return true;
            }
        }
        return false;
    }

    private void registerRemoteControlDisplay_int(IRemoteControlDisplay rcd, int w, int h, ComponentName listenerComp) {
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                if (rcd == null || rcDisplayIsPluggedIn_syncRcStack(rcd)) {
                    return;
                }
                DisplayInfoForServer di = new DisplayInfoForServer(rcd, w, h);
                di.mEnabled = true;
                di.mClientNotifListComp = listenerComp;
                if (di.init()) {
                    this.mRcDisplays.add(di);
                    Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
                    while (stackIterator.hasNext()) {
                        RemoteControlStackEntry rcse = stackIterator.next();
                        if (rcse.mRcClient != null) {
                            try {
                                rcse.mRcClient.plugRemoteControlDisplay(rcd, w, h);
                            } catch (RemoteException e) {
                                Log.e(TAG, "Error connecting RCD to client: ", e);
                            }
                        }
                    }
                    sendMsg(this.mEventHandler, 10, 2, w, h, rcd, 0);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) {
        synchronized (this.mRCStack) {
            if (rcd == null) {
                return;
            }
            boolean displayWasPluggedIn = false;
            Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext() && !displayWasPluggedIn) {
                DisplayInfoForServer di = displayIterator.next();
                if (di.mRcDisplay.asBinder().equals(rcd.asBinder())) {
                    displayWasPluggedIn = true;
                    di.release();
                    displayIterator.remove();
                }
            }
            if (displayWasPluggedIn) {
                Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
                while (stackIterator.hasNext()) {
                    RemoteControlStackEntry rcse = stackIterator.next();
                    if (rcse.mRcClient != null) {
                        try {
                            rcse.mRcClient.unplugRemoteControlDisplay(rcd);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error disconnecting remote control display to client: ", e);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) {
        synchronized (this.mRCStack) {
            Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
            boolean artworkSizeUpdate = false;
            while (displayIterator.hasNext() && !artworkSizeUpdate) {
                DisplayInfoForServer di = displayIterator.next();
                if (di.mRcDisplay.asBinder().equals(rcd.asBinder()) && (di.mArtworkExpectedWidth != w || di.mArtworkExpectedHeight != h)) {
                    di.mArtworkExpectedWidth = w;
                    di.mArtworkExpectedHeight = h;
                    artworkSizeUpdate = true;
                }
            }
            if (artworkSizeUpdate) {
                Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
                while (stackIterator.hasNext()) {
                    RemoteControlStackEntry rcse = stackIterator.next();
                    if (rcse.mRcClient != null) {
                        try {
                            rcse.mRcClient.setBitmapSizeForDisplay(rcd, w, h);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error setting bitmap size for RCD on RCC: ", e);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) {
        synchronized (this.mRCStack) {
            boolean rcdRegistered = false;
            Iterator<DisplayInfoForServer> displayIterator = this.mRcDisplays.iterator();
            while (true) {
                if (!displayIterator.hasNext()) {
                    break;
                }
                DisplayInfoForServer di = displayIterator.next();
                if (di.mRcDisplay.asBinder().equals(rcd.asBinder())) {
                    di.mWantsPositionSync = wantsSync;
                    rcdRegistered = true;
                    break;
                }
            }
            if (rcdRegistered) {
                Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
                while (stackIterator.hasNext()) {
                    RemoteControlStackEntry rcse = stackIterator.next();
                    if (rcse.mRcClient != null) {
                        try {
                            rcse.mRcClient.setWantsSyncForDisplay(rcd, wantsSync);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error setting position sync flag for RCD on RCC: ", e);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRemoteControlClientPlaybackPosition(int generationId, long timeMs) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClientGen != generationId) {
                    return;
                }
                sendMsg(this.mEventHandler, 8, 0, generationId, 0, new Long(timeMs), 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetRemoteControlClientPlaybackPosition(int generationId, long timeMs) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClient != null && this.mCurrentRcClientGen == generationId) {
                    try {
                        this.mCurrentRcClient.seekTo(generationId, timeMs);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Current valid remote client is dead: " + e);
                        this.mCurrentRcClient = null;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateRemoteControlClientMetadata(int genId, int key, Rating value) {
        sendMsg(this.mEventHandler, 9, 2, genId, key, value, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateRemoteControlClientMetadata(int genId, int key, Rating value) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClient != null && this.mCurrentRcClientGen == genId) {
                    try {
                        switch (key) {
                            case MediaMetadataEditor.RATING_KEY_BY_USER /* 268435457 */:
                                this.mCurrentRcClient.updateMetadata(genId, key, value);
                                break;
                            default:
                                Log.e(TAG, "unhandled metadata key " + key + " update for RCC " + genId);
                                break;
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "Current valid remote client is dead", e);
                        this.mCurrentRcClient = null;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPlaybackInfoForRcc(int rccId, int what, int value) {
        sendMsg(this.mEventHandler, 4, 2, rccId, what, Integer.valueOf(value), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackInfoForRcc(int rccId, int key, int value) {
        synchronized (this.mRCStack) {
            try {
                for (int index = this.mRCStack.size() - 1; index >= 0; index--) {
                    RemoteControlStackEntry rcse = this.mRCStack.elementAt(index);
                    if (rcse.mRccId == rccId) {
                        switch (key) {
                            case 1:
                                rcse.mPlaybackType = value;
                                postReevaluateRemote();
                                break;
                            case 2:
                                rcse.mPlaybackVolume = value;
                                synchronized (this.mMainRemote) {
                                    if (rccId == this.mMainRemote.mRccId) {
                                        this.mMainRemote.mVolume = value;
                                        this.mVolumeController.postHasNewRemotePlaybackInfo();
                                    }
                                }
                                break;
                            case 3:
                                rcse.mPlaybackVolumeMax = value;
                                synchronized (this.mMainRemote) {
                                    if (rccId == this.mMainRemote.mRccId) {
                                        this.mMainRemote.mVolumeMax = value;
                                        this.mVolumeController.postHasNewRemotePlaybackInfo();
                                    }
                                }
                                break;
                            case 4:
                                rcse.mPlaybackVolumeHandling = value;
                                synchronized (this.mMainRemote) {
                                    if (rccId == this.mMainRemote.mRccId) {
                                        this.mMainRemote.mVolumeHandling = value;
                                        this.mVolumeController.postHasNewRemotePlaybackInfo();
                                    }
                                }
                                break;
                            case 5:
                                rcse.mPlaybackStream = value;
                                break;
                            default:
                                Log.e(TAG, "unhandled key " + key + " for RCC " + rccId);
                                break;
                        }
                        return;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "Wrong index mRCStack on onNewPlaybackInfoForRcc, lock error? ", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPlaybackStateForRcc(int rccId, int state, long timeMs, float speed) {
        sendMsg(this.mEventHandler, 7, 2, rccId, state, new RccPlaybackState(state, timeMs, speed), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackStateForRcc(int rccId, int state, RccPlaybackState newState) {
        synchronized (this.mRCStack) {
            try {
                for (int index = this.mRCStack.size() - 1; index >= 0; index--) {
                    RemoteControlStackEntry rcse = this.mRCStack.elementAt(index);
                    if (rcse.mRccId == rccId) {
                        rcse.mPlaybackState = newState;
                        synchronized (this.mMainRemote) {
                            if (rccId == this.mMainRemote.mRccId) {
                                this.mMainRemoteIsActive = isPlaystateActive(state);
                                postReevaluateRemote();
                            }
                        }
                        if (isPlaystateActive(state)) {
                            postPromoteRcc(rccId);
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "Wrong index on mRCStack in onNewPlaybackStateForRcc, lock error? ", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerRemoteVolumeObserverForRcc(int rccId, IRemoteVolumeObserver rvo) {
        sendMsg(this.mEventHandler, 5, 2, rccId, 0, rvo, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x002e, code lost:
        r0.mRemoteVolumeObs = r6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onRegisterVolumeObserverForRcc(int r5, android.media.IRemoteVolumeObserver r6) {
        /*
            r4 = this;
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack
            r1 = r0
            r7 = r1
            monitor-enter(r0)
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            int r0 = r0.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            r1 = 1
            int r0 = r0 - r1
            r8 = r0
        L12:
            r0 = r8
            if (r0 < 0) goto L3d
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            r1 = r8
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            r9 = r0
            r0 = r9
            int r0 = r0.mRccId     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            r1 = r5
            if (r0 != r1) goto L37
            r0 = r9
            r1 = r6
            r0.mRemoteVolumeObs = r1     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L40 java.lang.Throwable -> L52
            goto L3d
        L37:
            int r8 = r8 + (-1)
            goto L12
        L3d:
            goto L4d
        L40:
            r8 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing media button stack, lock error? "
            r2 = r8
            int r0 = android.util.Log.e(r0, r1, r2)     // Catch: java.lang.Throwable -> L52
        L4d:
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L52
            goto L59
        L52:
            r10 = move-exception
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L52
            r0 = r10
            throw r0
        L59:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.onRegisterVolumeObserverForRcc(int, android.media.IRemoteVolumeObserver):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkUpdateRemoteStateIfActive(int streamType) {
        synchronized (this.mRCStack) {
            try {
                for (int index = this.mRCStack.size() - 1; index >= 0; index--) {
                    RemoteControlStackEntry rcse = this.mRCStack.elementAt(index);
                    if (rcse.mPlaybackType == 1 && isPlaystateActive(rcse.mPlaybackState.mState) && rcse.mPlaybackStream == streamType) {
                        synchronized (this.mMainRemote) {
                            this.mMainRemote.mRccId = rcse.mRccId;
                            this.mMainRemote.mVolume = rcse.mPlaybackVolume;
                            this.mMainRemote.mVolumeMax = rcse.mPlaybackVolumeMax;
                            this.mMainRemote.mVolumeHandling = rcse.mPlaybackVolumeHandling;
                            this.mMainRemoteIsActive = true;
                        }
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "Wrong index accessing RC stack, lock error? ", e);
            }
            synchronized (this.mMainRemote) {
                this.mMainRemoteIsActive = false;
            }
            return false;
        }
    }

    private static boolean isPlaystateActive(int playState) {
        switch (playState) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void adjustRemoteVolume(int streamType, int direction, int flags) {
        synchronized (this.mMainRemote) {
            if (this.mMainRemoteIsActive) {
                int rccId = this.mMainRemote.mRccId;
                boolean volFixed = this.mMainRemote.mVolumeHandling == 0;
                if (!volFixed) {
                    sendVolumeUpdateToRemote(rccId, direction);
                }
                this.mVolumeController.postRemoteVolumeChanged(streamType, flags);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0036, code lost:
        r7 = r0.mRemoteVolumeObs;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void sendVolumeUpdateToRemote(int r5, int r6) {
        /*
            r4 = this;
            r0 = r6
            if (r0 != 0) goto L5
            return
        L5:
            r0 = 0
            r7 = r0
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack
            r1 = r0
            r8 = r1
            monitor-enter(r0)
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            int r0 = r0.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            r1 = 1
            int r0 = r0 - r1
            r9 = r0
        L1a:
            r0 = r9
            if (r0 < 0) goto L45
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            r1 = r9
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            r10 = r0
            r0 = r10
            int r0 = r0.mRccId     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            r1 = r5
            if (r0 != r1) goto L3f
            r0 = r10
            android.media.IRemoteVolumeObserver r0 = r0.mRemoteVolumeObs     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L48 java.lang.Throwable -> L5b
            r7 = r0
            goto L45
        L3f:
            int r9 = r9 + (-1)
            goto L1a
        L45:
            goto L55
        L48:
            r9 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing media button stack, lock error? "
            r2 = r9
            int r0 = android.util.Log.e(r0, r1, r2)     // Catch: java.lang.Throwable -> L5b
        L55:
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L5b
            goto L63
        L5b:
            r11 = move-exception
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L5b
            r0 = r11
            throw r0
        L63:
            r0 = r7
            if (r0 == 0) goto L7f
            r0 = r7
            r1 = r6
            r2 = -1
            r0.dispatchRemoteVolumeUpdate(r1, r2)     // Catch: android.os.RemoteException -> L72
            goto L7f
        L72:
            r8 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Error dispatching relative volume update"
            r2 = r8
            int r0 = android.util.Log.e(r0, r1, r2)
        L7f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.sendVolumeUpdateToRemote(int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getRemoteStreamMaxVolume() {
        synchronized (this.mMainRemote) {
            if (this.mMainRemote.mRccId == -1) {
                return 0;
            }
            return this.mMainRemote.mVolumeMax;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getRemoteStreamVolume() {
        synchronized (this.mMainRemote) {
            if (this.mMainRemote.mRccId == -1) {
                return 0;
            }
            return this.mMainRemote.mVolume;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x005c, code lost:
        r7 = r0.mRemoteVolumeObs;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setRemoteStreamVolume(int r5) {
        /*
            r4 = this;
            r0 = -1
            r6 = r0
            r0 = r4
            android.media.MediaFocusControl$RemotePlaybackState r0 = r0.mMainRemote
            r1 = r0
            r7 = r1
            monitor-enter(r0)
            r0 = r4
            android.media.MediaFocusControl$RemotePlaybackState r0 = r0.mMainRemote     // Catch: java.lang.Throwable -> L24
            int r0 = r0.mRccId     // Catch: java.lang.Throwable -> L24
            r1 = -1
            if (r0 != r1) goto L17
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L24
            return
        L17:
            r0 = r4
            android.media.MediaFocusControl$RemotePlaybackState r0 = r0.mMainRemote     // Catch: java.lang.Throwable -> L24
            int r0 = r0.mRccId     // Catch: java.lang.Throwable -> L24
            r6 = r0
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L24
            goto L2b
        L24:
            r8 = move-exception
            r0 = r7
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L24
            r0 = r8
            throw r0
        L2b:
            r0 = 0
            r7 = r0
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack
            r1 = r0
            r8 = r1
            monitor-enter(r0)
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            int r0 = r0.size()     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            r1 = 1
            int r0 = r0 - r1
            r9 = r0
        L40:
            r0 = r9
            if (r0 < 0) goto L6b
            r0 = r4
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r0.mRCStack     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            r1 = r9
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            android.media.MediaFocusControl$RemoteControlStackEntry r0 = (android.media.MediaFocusControl.RemoteControlStackEntry) r0     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            r10 = r0
            r0 = r10
            int r0 = r0.mRccId     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            r1 = r6
            if (r0 != r1) goto L65
            r0 = r10
            android.media.IRemoteVolumeObserver r0 = r0.mRemoteVolumeObs     // Catch: java.lang.ArrayIndexOutOfBoundsException -> L6e java.lang.Throwable -> L81
            r7 = r0
            goto L6b
        L65:
            int r9 = r9 + (-1)
            goto L40
        L6b:
            goto L7b
        L6e:
            r9 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing media button stack, lock error? "
            r2 = r9
            int r0 = android.util.Log.e(r0, r1, r2)     // Catch: java.lang.Throwable -> L81
        L7b:
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L81
            goto L89
        L81:
            r11 = move-exception
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L81
            r0 = r11
            throw r0
        L89:
            r0 = r7
            if (r0 == 0) goto La5
            r0 = r7
            r1 = 0
            r2 = r5
            r0.dispatchRemoteVolumeUpdate(r1, r2)     // Catch: android.os.RemoteException -> L98
            goto La5
        L98:
            r8 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Error dispatching absolute volume update"
            r2 = r8
            int r0 = android.util.Log.e(r0, r1, r2)
        La5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.setRemoteStreamVolume(int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postReevaluateRemote() {
        sendMsg(this.mEventHandler, 3, 2, 0, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReevaluateRemote() {
        boolean hasRemotePlayback = false;
        synchronized (this.mRCStack) {
            Iterator<RemoteControlStackEntry> stackIterator = this.mRCStack.iterator();
            while (true) {
                if (!stackIterator.hasNext()) {
                    break;
                }
                RemoteControlStackEntry rcse = stackIterator.next();
                if (rcse.mPlaybackType == 1) {
                    hasRemotePlayback = true;
                    break;
                }
            }
        }
        synchronized (this.mMainRemote) {
            if (this.mHasRemotePlayback != hasRemotePlayback) {
                this.mHasRemotePlayback = hasRemotePlayback;
                this.mVolumeController.postRemoteSliderVisibility(hasRemotePlayback);
            }
        }
    }
}