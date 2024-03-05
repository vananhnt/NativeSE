package android.media;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.IRemoteControlDisplay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.ref.WeakReference;

/* loaded from: RemoteController.class */
public final class RemoteController {
    private static final int MAX_BITMAP_DIMENSION = 512;
    private static final int TRANSPORT_UNKNOWN = 0;
    private static final String TAG = "RemoteController";
    private static final boolean DEBUG = false;
    private static final Object mGenLock = new Object();
    private static final Object mInfoLock = new Object();
    private final RcDisplay mRcd;
    private final Context mContext;
    private final AudioManager mAudioManager;
    private final int mMaxBitmapDimension;
    private MetadataEditor mMetadataEditor;
    private int mClientGenerationIdCurrent;
    private boolean mIsRegistered;
    private PendingIntent mClientPendingIntentCurrent;
    private OnClientUpdateListener mOnClientUpdateListener;
    private PlaybackInfo mLastPlaybackInfo;
    private int mArtworkWidth;
    private int mArtworkHeight;
    private boolean mEnabled;
    public static final int POSITION_SYNCHRONIZATION_NONE = 0;
    public static final int POSITION_SYNCHRONIZATION_CHECK = 1;
    private final EventHandler mEventHandler;
    private static final int MSG_NEW_PENDING_INTENT = 0;
    private static final int MSG_NEW_PLAYBACK_INFO = 1;
    private static final int MSG_NEW_TRANSPORT_INFO = 2;
    private static final int MSG_NEW_METADATA = 3;
    private static final int MSG_CLIENT_CHANGE = 4;
    private static final int MSG_DISPLAY_ENABLE = 5;
    private static final int SENDMSG_REPLACE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;

    /* loaded from: RemoteController$OnClientUpdateListener.class */
    public interface OnClientUpdateListener {
        void onClientChange(boolean z);

        void onClientPlaybackStateUpdate(int i);

        void onClientPlaybackStateUpdate(int i, long j, long j2, float f);

        void onClientTransportControlUpdate(int i);

        void onClientMetadataUpdate(MetadataEditor metadataEditor);
    }

    public RemoteController(Context context, OnClientUpdateListener updateListener) throws IllegalArgumentException {
        this(context, updateListener, null);
    }

    public RemoteController(Context context, OnClientUpdateListener updateListener, Looper looper) throws IllegalArgumentException {
        this.mClientGenerationIdCurrent = 0;
        this.mIsRegistered = false;
        this.mArtworkWidth = -1;
        this.mArtworkHeight = -1;
        this.mEnabled = true;
        if (context == null) {
            throw new IllegalArgumentException("Invalid null Context");
        }
        if (updateListener == null) {
            throw new IllegalArgumentException("Invalid null OnClientUpdateListener");
        }
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper l = Looper.myLooper();
            if (l != null) {
                this.mEventHandler = new EventHandler(this, l);
            } else {
                throw new IllegalArgumentException("Calling thread not associated with a looper");
            }
        }
        this.mOnClientUpdateListener = updateListener;
        this.mContext = context;
        this.mRcd = new RcDisplay(this);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (ActivityManager.isLowRamDeviceStatic()) {
            this.mMaxBitmapDimension = 512;
            return;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.mMaxBitmapDimension = Math.max(dm.widthPixels, dm.heightPixels);
    }

    public String getRemoteControlClientPackageName() {
        if (this.mClientPendingIntentCurrent != null) {
            return this.mClientPendingIntentCurrent.getCreatorPackage();
        }
        return null;
    }

    public long getEstimatedMediaPosition() {
        if (this.mLastPlaybackInfo != null) {
            if (!RemoteControlClient.playbackPositionShouldMove(this.mLastPlaybackInfo.mState)) {
                return this.mLastPlaybackInfo.mCurrentPosMs;
            }
            long thenPos = this.mLastPlaybackInfo.mCurrentPosMs;
            if (thenPos < 0) {
                return -1L;
            }
            long now = SystemClock.elapsedRealtime();
            long then = this.mLastPlaybackInfo.mStateChangeTimeMs;
            long sinceThen = now - then;
            long scaledSinceThen = ((float) sinceThen) * this.mLastPlaybackInfo.mSpeed;
            return thenPos + scaledSinceThen;
        }
        return -1L;
    }

    public boolean sendMediaKeyEvent(KeyEvent keyEvent) throws IllegalArgumentException {
        if (!MediaFocusControl.isMediaKeyCode(keyEvent.getKeyCode())) {
            throw new IllegalArgumentException("not a media key event");
        }
        synchronized (mInfoLock) {
            if (!this.mIsRegistered) {
                Log.e(TAG, "Cannot use sendMediaKeyEvent() from an unregistered RemoteController");
                return false;
            } else if (!this.mEnabled) {
                Log.e(TAG, "Cannot use sendMediaKeyEvent() from a disabled RemoteController");
                return false;
            } else {
                PendingIntent pi = this.mClientPendingIntentCurrent;
                if (pi != null) {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                    try {
                        pi.send(this.mContext, 0, intent);
                        return true;
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(TAG, "Error sending intent for media button down: ", e);
                        return false;
                    }
                }
                Log.i(TAG, "No-op when sending key click, no receiver right now");
                return false;
            }
        }
    }

    public boolean seekTo(long timeMs) throws IllegalArgumentException {
        int genId;
        if (!this.mEnabled) {
            Log.e(TAG, "Cannot use seekTo() from a disabled RemoteController");
            return false;
        } else if (timeMs < 0) {
            throw new IllegalArgumentException("illegal negative time value");
        } else {
            synchronized (mGenLock) {
                genId = this.mClientGenerationIdCurrent;
            }
            this.mAudioManager.setRemoteControlClientPlaybackPosition(genId, timeMs);
            return true;
        }
    }

    public boolean setArtworkConfiguration(boolean wantBitmap, int width, int height) throws IllegalArgumentException {
        synchronized (mInfoLock) {
            if (wantBitmap) {
                if (width > 0 && height > 0) {
                    if (width > this.mMaxBitmapDimension) {
                        width = this.mMaxBitmapDimension;
                    }
                    if (height > this.mMaxBitmapDimension) {
                        height = this.mMaxBitmapDimension;
                    }
                    this.mArtworkWidth = width;
                    this.mArtworkHeight = height;
                } else {
                    throw new IllegalArgumentException("Invalid dimensions");
                }
            } else {
                this.mArtworkWidth = -1;
                this.mArtworkHeight = -1;
            }
            if (this.mIsRegistered) {
                this.mAudioManager.remoteControlDisplayUsesBitmapSize(this.mRcd, this.mArtworkWidth, this.mArtworkHeight);
            }
        }
        return true;
    }

    public boolean setArtworkConfiguration(int width, int height) throws IllegalArgumentException {
        return setArtworkConfiguration(true, width, height);
    }

    public boolean clearArtworkConfiguration() {
        return setArtworkConfiguration(false, -1, -1);
    }

    public boolean setSynchronizationMode(int sync) throws IllegalArgumentException {
        if (sync != 0 || sync != 1) {
            throw new IllegalArgumentException("Unknown synchronization mode " + sync);
        }
        if (!this.mIsRegistered) {
            Log.e(TAG, "Cannot set synchronization mode on an unregistered RemoteController");
            return false;
        }
        this.mAudioManager.remoteControlDisplayWantsPlaybackPositionSync(this.mRcd, 1 == sync);
        return true;
    }

    public MetadataEditor editMetadata() {
        MetadataEditor editor = new MetadataEditor();
        editor.mEditorMetadata = new Bundle();
        editor.mEditorArtwork = null;
        editor.mMetadataChanged = true;
        editor.mArtworkChanged = true;
        editor.mEditableKeys = 0L;
        return editor;
    }

    /* loaded from: RemoteController$MetadataEditor.class */
    public class MetadataEditor extends MediaMetadataEditor {
        protected MetadataEditor() {
        }

        protected MetadataEditor(Bundle metadata, long editableKeys) {
            this.mEditorMetadata = metadata;
            this.mEditableKeys = editableKeys;
            this.mEditorArtwork = (Bitmap) metadata.getParcelable(String.valueOf(100));
            if (this.mEditorArtwork != null) {
                cleanupBitmapFromBundle(100);
            }
            this.mMetadataChanged = true;
            this.mArtworkChanged = true;
            this.mApplied = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cleanupBitmapFromBundle(int key) {
            if (METADATA_KEYS_TYPE.get(key, -1) == 2) {
                this.mEditorMetadata.remove(String.valueOf(key));
            }
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void apply() {
            int genId;
            if (this.mMetadataChanged) {
                synchronized (RemoteController.mGenLock) {
                    genId = RemoteController.this.mClientGenerationIdCurrent;
                }
                synchronized (RemoteController.mInfoLock) {
                    if (this.mEditorMetadata.containsKey(String.valueOf((int) MediaMetadataEditor.RATING_KEY_BY_USER))) {
                        Rating rating = (Rating) getObject(MediaMetadataEditor.RATING_KEY_BY_USER, null);
                        RemoteController.this.mAudioManager.updateRemoteControlClientMetadata(genId, MediaMetadataEditor.RATING_KEY_BY_USER, rating);
                    } else {
                        Log.e(RemoteController.TAG, "no metadata to apply");
                    }
                    this.mApplied = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteController$RcDisplay.class */
    public static class RcDisplay extends IRemoteControlDisplay.Stub {
        private final WeakReference<RemoteController> mController;

        RcDisplay(RemoteController rc) {
            this.mController = new WeakReference<>(rc);
        }

        @Override // android.media.IRemoteControlDisplay
        public void setCurrentClientId(int genId, PendingIntent clientMediaIntent, boolean clearing) {
            RemoteController rc = this.mController.get();
            if (rc == null) {
                return;
            }
            boolean isNew = false;
            synchronized (RemoteController.mGenLock) {
                if (rc.mClientGenerationIdCurrent != genId) {
                    rc.mClientGenerationIdCurrent = genId;
                    isNew = true;
                }
            }
            if (clientMediaIntent != null) {
                RemoteController.sendMsg(rc.mEventHandler, 0, 0, genId, 0, clientMediaIntent, 0);
            }
            if (isNew || clearing) {
                RemoteController.sendMsg(rc.mEventHandler, 4, 0, genId, clearing ? 1 : 0, null, 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setEnabled(boolean enabled) {
            RemoteController rc = this.mController.get();
            if (rc != null) {
                RemoteController.sendMsg(rc.mEventHandler, 5, 0, enabled ? 1 : 0, 0, null, 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setPlaybackState(int genId, int state, long stateChangeTimeMs, long currentPosMs, float speed) {
            RemoteController rc = this.mController.get();
            if (rc != null) {
                synchronized (RemoteController.mGenLock) {
                    if (rc.mClientGenerationIdCurrent != genId) {
                        return;
                    }
                    PlaybackInfo playbackInfo = new PlaybackInfo(state, stateChangeTimeMs, currentPosMs, speed);
                    RemoteController.sendMsg(rc.mEventHandler, 1, 0, genId, 0, playbackInfo, 0);
                }
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setTransportControlInfo(int genId, int transportControlFlags, int posCapabilities) {
            RemoteController rc = this.mController.get();
            if (rc != null) {
                synchronized (RemoteController.mGenLock) {
                    if (rc.mClientGenerationIdCurrent != genId) {
                        return;
                    }
                    RemoteController.sendMsg(rc.mEventHandler, 2, 0, genId, transportControlFlags, null, 0);
                }
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setMetadata(int genId, Bundle metadata) {
            RemoteController rc = this.mController.get();
            if (rc != null && metadata != null) {
                synchronized (RemoteController.mGenLock) {
                    if (rc.mClientGenerationIdCurrent != genId) {
                        return;
                    }
                    RemoteController.sendMsg(rc.mEventHandler, 3, 2, genId, 0, metadata, 0);
                }
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setArtwork(int genId, Bitmap artwork) {
            RemoteController rc = this.mController.get();
            if (rc != null) {
                synchronized (RemoteController.mGenLock) {
                    if (rc.mClientGenerationIdCurrent != genId) {
                        return;
                    }
                    Bundle metadata = new Bundle(1);
                    metadata.putParcelable(String.valueOf(100), artwork);
                    RemoteController.sendMsg(rc.mEventHandler, 3, 2, genId, 0, metadata, 0);
                }
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setAllMetadata(int genId, Bundle metadata, Bitmap artwork) {
            RemoteController rc = this.mController.get();
            if (rc == null) {
                return;
            }
            if (metadata != null || artwork != null) {
                synchronized (RemoteController.mGenLock) {
                    if (rc.mClientGenerationIdCurrent != genId) {
                        return;
                    }
                    if (metadata == null) {
                        metadata = new Bundle(1);
                    }
                    if (artwork != null) {
                        metadata.putParcelable(String.valueOf(100), artwork);
                    }
                    RemoteController.sendMsg(rc.mEventHandler, 3, 2, genId, 0, metadata, 0);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteController$EventHandler.class */
    public class EventHandler extends Handler {
        public EventHandler(RemoteController rc, Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    RemoteController.this.onNewPendingIntent(msg.arg1, (PendingIntent) msg.obj);
                    return;
                case 1:
                    RemoteController.this.onNewPlaybackInfo(msg.arg1, (PlaybackInfo) msg.obj);
                    return;
                case 2:
                    RemoteController.this.onNewTransportInfo(msg.arg1, msg.arg2);
                    return;
                case 3:
                    RemoteController.this.onNewMetadata(msg.arg1, (Bundle) msg.obj);
                    return;
                case 4:
                    RemoteController.this.onClientChange(msg.arg1, msg.arg2 == 1);
                    return;
                case 5:
                    RemoteController.this.onDisplayEnable(msg.arg1 == 1);
                    return;
                default:
                    Log.e(RemoteController.TAG, "unknown event " + msg.what);
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendMsg(Handler handler, int msg, int existingMsgPolicy, int arg1, int arg2, Object obj, int delayMs) {
        if (handler == null) {
            Log.e(TAG, "null event handler, will not deliver message " + msg);
            return;
        }
        if (existingMsgPolicy == 0) {
            handler.removeMessages(msg);
        } else if (existingMsgPolicy == 1 && handler.hasMessages(msg)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(msg, arg1, arg2, obj), delayMs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPendingIntent(int genId, PendingIntent pi) {
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != genId) {
                return;
            }
            synchronized (mInfoLock) {
                this.mClientPendingIntentCurrent = pi;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackInfo(int genId, PlaybackInfo pi) {
        OnClientUpdateListener l;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != genId) {
                return;
            }
            synchronized (mInfoLock) {
                l = this.mOnClientUpdateListener;
                this.mLastPlaybackInfo = pi;
            }
            if (l != null) {
                if (pi.mCurrentPosMs == RemoteControlClient.PLAYBACK_POSITION_ALWAYS_UNKNOWN) {
                    l.onClientPlaybackStateUpdate(pi.mState);
                } else {
                    l.onClientPlaybackStateUpdate(pi.mState, pi.mStateChangeTimeMs, pi.mCurrentPosMs, pi.mSpeed);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewTransportInfo(int genId, int transportControlFlags) {
        OnClientUpdateListener l;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != genId) {
                return;
            }
            synchronized (mInfoLock) {
                l = this.mOnClientUpdateListener;
            }
            if (l != null) {
                l.onClientTransportControlUpdate(transportControlFlags);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewMetadata(int genId, Bundle metadata) {
        OnClientUpdateListener l;
        MetadataEditor metadataEditor;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != genId) {
                return;
            }
            long editableKeys = metadata.getLong(String.valueOf((int) MediaMetadataEditor.KEY_EDITABLE_MASK), 0L);
            if (editableKeys != 0) {
                metadata.remove(String.valueOf((int) MediaMetadataEditor.KEY_EDITABLE_MASK));
            }
            synchronized (mInfoLock) {
                l = this.mOnClientUpdateListener;
                if (this.mMetadataEditor != null && this.mMetadataEditor.mEditorMetadata != null) {
                    if (this.mMetadataEditor.mEditorMetadata != metadata) {
                        this.mMetadataEditor.mEditorMetadata.putAll(metadata);
                    }
                    this.mMetadataEditor.putBitmap(100, (Bitmap) metadata.getParcelable(String.valueOf(100)));
                    this.mMetadataEditor.cleanupBitmapFromBundle(100);
                } else {
                    this.mMetadataEditor = new MetadataEditor(metadata, editableKeys);
                }
                metadataEditor = this.mMetadataEditor;
            }
            if (l != null) {
                l.onClientMetadataUpdate(metadataEditor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClientChange(int genId, boolean clearing) {
        OnClientUpdateListener l;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != genId) {
                return;
            }
            synchronized (mInfoLock) {
                l = this.mOnClientUpdateListener;
            }
            if (l != null) {
                l.onClientChange(clearing);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayEnable(boolean enabled) {
        int genId;
        synchronized (mInfoLock) {
            this.mEnabled = enabled;
            OnClientUpdateListener onClientUpdateListener = this.mOnClientUpdateListener;
        }
        if (!enabled) {
            synchronized (mGenLock) {
                genId = this.mClientGenerationIdCurrent;
            }
            PlaybackInfo pi = new PlaybackInfo(1, SystemClock.elapsedRealtime(), 0L, 0.0f);
            sendMsg(this.mEventHandler, 1, 0, genId, 0, pi, 0);
            sendMsg(this.mEventHandler, 2, 0, genId, 0, null, 0);
            Bundle metadata = new Bundle(3);
            metadata.putString(String.valueOf(7), "");
            metadata.putString(String.valueOf(2), "");
            metadata.putLong(String.valueOf(9), 0L);
            sendMsg(this.mEventHandler, 3, 2, genId, 0, metadata, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteController$PlaybackInfo.class */
    public static class PlaybackInfo {
        int mState;
        long mStateChangeTimeMs;
        long mCurrentPosMs;
        float mSpeed;

        PlaybackInfo(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
            this.mState = state;
            this.mStateChangeTimeMs = stateChangeTimeMs;
            this.mCurrentPosMs = currentPosMs;
            this.mSpeed = speed;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIsRegistered(boolean registered) {
        synchronized (mInfoLock) {
            this.mIsRegistered = registered;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RcDisplay getRcDisplay() {
        return this.mRcd;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getArtworkSize() {
        int[] size;
        synchronized (mInfoLock) {
            size = new int[]{this.mArtworkWidth, this.mArtworkHeight};
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnClientUpdateListener getUpdateListener() {
        return this.mOnClientUpdateListener;
    }
}