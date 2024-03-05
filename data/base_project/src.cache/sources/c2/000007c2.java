package android.media;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.IAudioService;
import android.media.IRemoteControlClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: RemoteControlClient.class */
public class RemoteControlClient {
    private static final String TAG = "RemoteControlClient";
    private static final boolean DEBUG = false;
    public static final int PLAYSTATE_STOPPED = 1;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int PLAYSTATE_FAST_FORWARDING = 4;
    public static final int PLAYSTATE_REWINDING = 5;
    public static final int PLAYSTATE_SKIPPING_FORWARDS = 6;
    public static final int PLAYSTATE_SKIPPING_BACKWARDS = 7;
    public static final int PLAYSTATE_BUFFERING = 8;
    public static final int PLAYSTATE_ERROR = 9;
    public static final int PLAYSTATE_NONE = 0;
    public static final int PLAYBACK_TYPE_LOCAL = 0;
    public static final int PLAYBACK_TYPE_REMOTE = 1;
    private static final int PLAYBACK_TYPE_MIN = 0;
    private static final int PLAYBACK_TYPE_MAX = 1;
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 1;
    public static final int PLAYBACKINFO_INVALID_VALUE = Integer.MIN_VALUE;
    public static final long PLAYBACK_POSITION_INVALID = -1;
    public static final long PLAYBACK_POSITION_ALWAYS_UNKNOWN = -9216204211029966080L;
    public static final float PLAYBACK_SPEED_1X = 1.0f;
    public static final int PLAYBACKINFO_PLAYBACK_TYPE = 1;
    public static final int PLAYBACKINFO_VOLUME = 2;
    public static final int PLAYBACKINFO_VOLUME_MAX = 3;
    public static final int PLAYBACKINFO_VOLUME_HANDLING = 4;
    public static final int PLAYBACKINFO_USES_STREAM = 5;
    public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
    public static final int FLAG_KEY_MEDIA_REWIND = 2;
    public static final int FLAG_KEY_MEDIA_PLAY = 4;
    public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
    public static final int FLAG_KEY_MEDIA_PAUSE = 16;
    public static final int FLAG_KEY_MEDIA_STOP = 32;
    public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
    public static final int FLAG_KEY_MEDIA_NEXT = 128;
    public static final int FLAG_KEY_MEDIA_POSITION_UPDATE = 256;
    public static final int FLAG_KEY_MEDIA_RATING = 512;
    public static final int FLAGS_KEY_MEDIA_NONE = 0;
    public static final int FLAG_INFORMATION_REQUEST_METADATA = 1;
    public static final int FLAG_INFORMATION_REQUEST_KEY_MEDIA = 2;
    public static final int FLAG_INFORMATION_REQUEST_PLAYSTATE = 4;
    public static final int FLAG_INFORMATION_REQUEST_ALBUM_ART = 8;
    public static int MEDIA_POSITION_READABLE = 1;
    public static int MEDIA_POSITION_WRITABLE = 2;
    public static final int DEFAULT_PLAYBACK_VOLUME_HANDLING = 1;
    public static final int DEFAULT_PLAYBACK_VOLUME = 15;
    private Bitmap mOriginalArtwork;
    private OnPlaybackPositionUpdateListener mPositionUpdateListener;
    private OnGetPlaybackPositionListener mPositionProvider;
    private OnMetadataUpdateListener mMetadataUpdateListener;
    private final PendingIntent mRcMediaIntent;
    public static final int RCSE_ID_UNREGISTERED = -1;
    private EventHandler mEventHandler;
    private static final int MSG_REQUEST_PLAYBACK_STATE = 1;
    private static final int MSG_REQUEST_METADATA = 2;
    private static final int MSG_REQUEST_TRANSPORTCONTROL = 3;
    private static final int MSG_REQUEST_ARTWORK = 4;
    private static final int MSG_NEW_INTERNAL_CLIENT_GEN = 5;
    private static final int MSG_NEW_CURRENT_CLIENT_GEN = 6;
    private static final int MSG_PLUG_DISPLAY = 7;
    private static final int MSG_UNPLUG_DISPLAY = 8;
    private static final int MSG_UPDATE_DISPLAY_ARTWORK_SIZE = 9;
    private static final int MSG_SEEK_TO = 10;
    private static final int MSG_POSITION_DRIFT_CHECK = 11;
    private static final int MSG_DISPLAY_WANTS_POS_SYNC = 12;
    private static final int MSG_UPDATE_METADATA = 13;
    private static final int MSG_REQUEST_METADATA_ARTWORK = 14;
    private static final int MSG_DISPLAY_ENABLE = 15;
    private static IAudioService sService;
    private static final long POSITION_REFRESH_PERIOD_PLAYING_MS = 15000;
    private static final long POSITION_REFRESH_PERIOD_MIN_MS = 2000;
    private static final long POSITION_DRIFT_MAX_MS = 500;
    private int mPlaybackPositionCapabilities = 0;
    private int mPlaybackType = 0;
    private int mPlaybackVolumeMax = 15;
    private int mPlaybackVolume = 15;
    private int mPlaybackVolumeHandling = 1;
    private int mPlaybackStream = 3;
    private final Object mCacheLock = new Object();
    private int mPlaybackState = 0;
    private long mPlaybackStateChangeTimeMs = 0;
    private long mPlaybackPositionMs = -1;
    private float mPlaybackSpeed = 1.0f;
    private int mTransportControlFlags = 0;
    private Bundle mMetadata = new Bundle();
    private int mCurrentClientGenId = -1;
    private int mInternalClientGenId = -2;
    private boolean mNeedsPositionSync = false;
    private ArrayList<DisplayInfoForClient> mRcDisplays = new ArrayList<>(1);
    private final IRemoteControlClient mIRCC = new IRemoteControlClient.Stub() { // from class: android.media.RemoteControlClient.1
        @Override // android.media.IRemoteControlClient
        public void onInformationRequested(int generationId, int infoFlags) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.removeMessages(5);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(5, generationId, 0));
                RemoteControlClient.this.mEventHandler.removeMessages(1);
                RemoteControlClient.this.mEventHandler.removeMessages(2);
                RemoteControlClient.this.mEventHandler.removeMessages(3);
                RemoteControlClient.this.mEventHandler.removeMessages(4);
                RemoteControlClient.this.mEventHandler.removeMessages(14);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(1, null));
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(3, null));
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(14, 0, 0, null));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void informationRequestForDisplay(IRemoteControlDisplay rcd, int w, int h) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(3, rcd));
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(1, rcd));
                if (w <= 0 || h <= 0) {
                    RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(2, rcd));
                } else {
                    RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(14, w, h, rcd));
                }
            }
        }

        @Override // android.media.IRemoteControlClient
        public void setCurrentClientGenerationId(int clientGeneration) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.removeMessages(6);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(6, clientGeneration, 0));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void plugRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) {
            if (RemoteControlClient.this.mEventHandler != null && rcd != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(7, w, h, rcd));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void unplugRemoteControlDisplay(IRemoteControlDisplay rcd) {
            if (RemoteControlClient.this.mEventHandler != null && rcd != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(8, rcd));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void setBitmapSizeForDisplay(IRemoteControlDisplay rcd, int w, int h) {
            if (RemoteControlClient.this.mEventHandler != null && rcd != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(9, w, h, rcd));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void setWantsSyncForDisplay(IRemoteControlDisplay rcd, boolean wantsSync) {
            if (RemoteControlClient.this.mEventHandler != null && rcd != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(12, wantsSync ? 1 : 0, 0, rcd));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void enableRemoteControlDisplay(IRemoteControlDisplay rcd, boolean enabled) {
            if (RemoteControlClient.this.mEventHandler != null && rcd != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(15, enabled ? 1 : 0, 0, rcd));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void seekTo(int generationId, long timeMs) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.removeMessages(10);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(10, generationId, 0, new Long(timeMs)));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void updateMetadata(int generationId, int key, Rating value) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(13, generationId, key, value));
            }
        }
    };
    private int mRcseId = -1;

    /* loaded from: RemoteControlClient$OnGetPlaybackPositionListener.class */
    public interface OnGetPlaybackPositionListener {
        long onGetPlaybackPosition();
    }

    /* loaded from: RemoteControlClient$OnMetadataUpdateListener.class */
    public interface OnMetadataUpdateListener {
        void onMetadataUpdate(int i, Object obj);
    }

    /* loaded from: RemoteControlClient$OnPlaybackPositionUpdateListener.class */
    public interface OnPlaybackPositionUpdateListener {
        void onPlaybackPositionUpdate(long j);
    }

    public RemoteControlClient(PendingIntent mediaButtonIntent) {
        this.mRcMediaIntent = mediaButtonIntent;
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
            return;
        }
        Looper looper2 = Looper.getMainLooper();
        if (looper2 != null) {
            this.mEventHandler = new EventHandler(this, looper2);
            return;
        }
        this.mEventHandler = null;
        Log.e(TAG, "RemoteControlClient() couldn't find main application thread");
    }

    public RemoteControlClient(PendingIntent mediaButtonIntent, Looper looper) {
        this.mRcMediaIntent = mediaButtonIntent;
        this.mEventHandler = new EventHandler(this, looper);
    }

    /* loaded from: RemoteControlClient$MetadataEditor.class */
    public class MetadataEditor extends MediaMetadataEditor {
        public static final int BITMAP_KEY_ARTWORK = 100;
        public static final int METADATA_KEY_ARTWORK = 100;

        private MetadataEditor() {
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putString(int key, String value) throws IllegalArgumentException {
            super.putString(key, value);
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putLong(int key, long value) throws IllegalArgumentException {
            super.putLong(key, value);
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putBitmap(int key, Bitmap bitmap) throws IllegalArgumentException {
            super.putBitmap(key, bitmap);
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void clear() {
            super.clear();
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void apply() {
            if (!this.mApplied) {
                synchronized (RemoteControlClient.this.mCacheLock) {
                    RemoteControlClient.this.mMetadata = new Bundle(this.mEditorMetadata);
                    RemoteControlClient.this.mMetadata.putLong(String.valueOf((int) MediaMetadataEditor.KEY_EDITABLE_MASK), this.mEditableKeys);
                    if (RemoteControlClient.this.mOriginalArtwork != null && !RemoteControlClient.this.mOriginalArtwork.equals(this.mEditorArtwork)) {
                        RemoteControlClient.this.mOriginalArtwork.recycle();
                    }
                    RemoteControlClient.this.mOriginalArtwork = this.mEditorArtwork;
                    this.mEditorArtwork = null;
                    if (this.mMetadataChanged & this.mArtworkChanged) {
                        RemoteControlClient.this.sendMetadataWithArtwork_syncCacheLock(null, 0, 0);
                    } else if (this.mMetadataChanged) {
                        RemoteControlClient.this.sendMetadata_syncCacheLock(null);
                    } else if (this.mArtworkChanged) {
                        RemoteControlClient.this.sendArtwork_syncCacheLock(null, 0, 0);
                    }
                    this.mApplied = true;
                }
                return;
            }
            Log.e(RemoteControlClient.TAG, "Can't apply a previously applied MetadataEditor");
        }
    }

    public MetadataEditor editMetadata(boolean startEmpty) {
        MetadataEditor editor = new MetadataEditor();
        if (startEmpty) {
            editor.mEditorMetadata = new Bundle();
            editor.mEditorArtwork = null;
            editor.mMetadataChanged = true;
            editor.mArtworkChanged = true;
            editor.mEditableKeys = 0L;
        } else {
            editor.mEditorMetadata = new Bundle(this.mMetadata);
            editor.mEditorArtwork = this.mOriginalArtwork;
            editor.mMetadataChanged = false;
            editor.mArtworkChanged = false;
        }
        return editor;
    }

    public void setPlaybackState(int state) {
        setPlaybackStateInt(state, PLAYBACK_POSITION_ALWAYS_UNKNOWN, 1.0f, false);
    }

    public void setPlaybackState(int state, long timeInMs, float playbackSpeed) {
        setPlaybackStateInt(state, timeInMs, playbackSpeed, true);
    }

    private void setPlaybackStateInt(int state, long timeInMs, float playbackSpeed, boolean hasPosition) {
        synchronized (this.mCacheLock) {
            if (this.mPlaybackState != state || this.mPlaybackPositionMs != timeInMs || this.mPlaybackSpeed != playbackSpeed) {
                this.mPlaybackState = state;
                if (hasPosition) {
                    if (timeInMs < 0) {
                        this.mPlaybackPositionMs = -1L;
                    } else {
                        this.mPlaybackPositionMs = timeInMs;
                    }
                } else {
                    this.mPlaybackPositionMs = PLAYBACK_POSITION_ALWAYS_UNKNOWN;
                }
                this.mPlaybackSpeed = playbackSpeed;
                this.mPlaybackStateChangeTimeMs = SystemClock.elapsedRealtime();
                sendPlaybackState_syncCacheLock(null);
                sendAudioServiceNewPlaybackState_syncCacheLock();
                initiateCheckForDrift_syncCacheLock();
            }
        }
    }

    private void initiateCheckForDrift_syncCacheLock() {
        if (this.mEventHandler == null) {
            return;
        }
        this.mEventHandler.removeMessages(11);
        if (this.mNeedsPositionSync && this.mPlaybackPositionMs >= 0 && playbackPositionShouldMove(this.mPlaybackState)) {
            this.mEventHandler.sendMessageDelayed(this.mEventHandler.obtainMessage(11), getCheckPeriodFromSpeed(this.mPlaybackSpeed));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPositionDriftCheck() {
        synchronized (this.mCacheLock) {
            if (this.mEventHandler == null || this.mPositionProvider == null || !this.mNeedsPositionSync) {
                return;
            }
            if (this.mPlaybackPositionMs < 0 || this.mPlaybackSpeed == 0.0f) {
                return;
            }
            long estPos = this.mPlaybackPositionMs + (((float) (SystemClock.elapsedRealtime() - this.mPlaybackStateChangeTimeMs)) / this.mPlaybackSpeed);
            long actPos = this.mPositionProvider.onGetPlaybackPosition();
            if (actPos >= 0) {
                if (Math.abs(estPos - actPos) > POSITION_DRIFT_MAX_MS) {
                    setPlaybackState(this.mPlaybackState, actPos, this.mPlaybackSpeed);
                } else {
                    this.mEventHandler.sendMessageDelayed(this.mEventHandler.obtainMessage(11), getCheckPeriodFromSpeed(this.mPlaybackSpeed));
                }
            } else {
                this.mEventHandler.removeMessages(11);
            }
        }
    }

    public void setTransportControlFlags(int transportControlFlags) {
        synchronized (this.mCacheLock) {
            this.mTransportControlFlags = transportControlFlags;
            sendTransportControlInfo_syncCacheLock(null);
        }
    }

    public void setMetadataUpdateListener(OnMetadataUpdateListener l) {
        synchronized (this.mCacheLock) {
            this.mMetadataUpdateListener = l;
        }
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener l) {
        synchronized (this.mCacheLock) {
            int oldCapa = this.mPlaybackPositionCapabilities;
            if (l != null) {
                this.mPlaybackPositionCapabilities |= MEDIA_POSITION_WRITABLE;
            } else {
                this.mPlaybackPositionCapabilities &= MEDIA_POSITION_WRITABLE ^ (-1);
            }
            this.mPositionUpdateListener = l;
            if (oldCapa != this.mPlaybackPositionCapabilities) {
                sendTransportControlInfo_syncCacheLock(null);
            }
        }
    }

    public void setOnGetPlaybackPositionListener(OnGetPlaybackPositionListener l) {
        synchronized (this.mCacheLock) {
            int oldCapa = this.mPlaybackPositionCapabilities;
            if (l != null) {
                this.mPlaybackPositionCapabilities |= MEDIA_POSITION_READABLE;
            } else {
                this.mPlaybackPositionCapabilities &= MEDIA_POSITION_READABLE ^ (-1);
            }
            this.mPositionProvider = l;
            if (oldCapa != this.mPlaybackPositionCapabilities) {
                sendTransportControlInfo_syncCacheLock(null);
            }
            if (this.mPositionProvider != null && this.mEventHandler != null && playbackPositionShouldMove(this.mPlaybackState)) {
                this.mEventHandler.sendMessageDelayed(this.mEventHandler.obtainMessage(11), 0L);
            }
        }
    }

    public void setPlaybackInformation(int what, int value) {
        synchronized (this.mCacheLock) {
            switch (what) {
                case 1:
                    if (value >= 0 && value <= 1) {
                        if (this.mPlaybackType != value) {
                            this.mPlaybackType = value;
                            sendAudioServiceNewPlaybackInfo_syncCacheLock(what, value);
                            break;
                        }
                    } else {
                        Log.w(TAG, "using invalid value for PLAYBACKINFO_PLAYBACK_TYPE");
                        break;
                    }
                    break;
                case 2:
                    if (value > -1 && value <= this.mPlaybackVolumeMax) {
                        if (this.mPlaybackVolume != value) {
                            this.mPlaybackVolume = value;
                            sendAudioServiceNewPlaybackInfo_syncCacheLock(what, value);
                            break;
                        }
                    } else {
                        Log.w(TAG, "using invalid value for PLAYBACKINFO_VOLUME");
                        break;
                    }
                    break;
                case 3:
                    if (value > 0) {
                        if (this.mPlaybackVolumeMax != value) {
                            this.mPlaybackVolumeMax = value;
                            sendAudioServiceNewPlaybackInfo_syncCacheLock(what, value);
                            break;
                        }
                    } else {
                        Log.w(TAG, "using invalid value for PLAYBACKINFO_VOLUME_MAX");
                        break;
                    }
                    break;
                case 4:
                    if (value >= 0 && value <= 1) {
                        if (this.mPlaybackVolumeHandling != value) {
                            this.mPlaybackVolumeHandling = value;
                            sendAudioServiceNewPlaybackInfo_syncCacheLock(what, value);
                            break;
                        }
                    } else {
                        Log.w(TAG, "using invalid value for PLAYBACKINFO_VOLUME_HANDLING");
                        break;
                    }
                    break;
                case 5:
                    if (value >= 0 && value < AudioSystem.getNumStreamTypes()) {
                        this.mPlaybackStream = value;
                        break;
                    } else {
                        Log.w(TAG, "using invalid value for PLAYBACKINFO_USES_STREAM");
                        break;
                    }
                    break;
                default:
                    Log.w(TAG, "setPlaybackInformation() ignoring unknown key " + what);
                    break;
            }
        }
    }

    public int getIntPlaybackInformation(int what) {
        synchronized (this.mCacheLock) {
            switch (what) {
                case 1:
                    return this.mPlaybackType;
                case 2:
                    return this.mPlaybackVolume;
                case 3:
                    return this.mPlaybackVolumeMax;
                case 4:
                    return this.mPlaybackVolumeHandling;
                case 5:
                    return this.mPlaybackStream;
                default:
                    Log.e(TAG, "getIntPlaybackInformation() unknown key " + what);
                    return Integer.MIN_VALUE;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteControlClient$DisplayInfoForClient.class */
    public class DisplayInfoForClient {
        private IRemoteControlDisplay mRcDisplay;
        private int mArtworkExpectedWidth;
        private int mArtworkExpectedHeight;
        private boolean mWantsPositionSync = false;
        private boolean mEnabled = true;

        DisplayInfoForClient(IRemoteControlDisplay rcd, int w, int h) {
            this.mRcDisplay = rcd;
            this.mArtworkExpectedWidth = w;
            this.mArtworkExpectedHeight = h;
        }
    }

    public PendingIntent getRcMediaIntent() {
        return this.mRcMediaIntent;
    }

    public IRemoteControlClient getIRemoteControlClient() {
        return this.mIRCC;
    }

    public void setRcseId(int id) {
        this.mRcseId = id;
    }

    public int getRcseId() {
        return this.mRcseId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteControlClient$EventHandler.class */
    public class EventHandler extends Handler {
        public EventHandler(RemoteControlClient rcc, Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendPlaybackState_syncCacheLock((IRemoteControlDisplay) msg.obj);
                    }
                    return;
                case 2:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendMetadata_syncCacheLock((IRemoteControlDisplay) msg.obj);
                    }
                    return;
                case 3:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendTransportControlInfo_syncCacheLock((IRemoteControlDisplay) msg.obj);
                    }
                    return;
                case 4:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendArtwork_syncCacheLock((IRemoteControlDisplay) msg.obj, msg.arg1, msg.arg2);
                    }
                    return;
                case 5:
                    RemoteControlClient.this.onNewInternalClientGen(msg.arg1);
                    return;
                case 6:
                    RemoteControlClient.this.onNewCurrentClientGen(msg.arg1);
                    return;
                case 7:
                    RemoteControlClient.this.onPlugDisplay((IRemoteControlDisplay) msg.obj, msg.arg1, msg.arg2);
                    return;
                case 8:
                    RemoteControlClient.this.onUnplugDisplay((IRemoteControlDisplay) msg.obj);
                    return;
                case 9:
                    RemoteControlClient.this.onUpdateDisplayArtworkSize((IRemoteControlDisplay) msg.obj, msg.arg1, msg.arg2);
                    return;
                case 10:
                    RemoteControlClient.this.onSeekTo(msg.arg1, ((Long) msg.obj).longValue());
                    return;
                case 11:
                    RemoteControlClient.this.onPositionDriftCheck();
                    return;
                case 12:
                    RemoteControlClient.this.onDisplayWantsSync((IRemoteControlDisplay) msg.obj, msg.arg1 == 1);
                    return;
                case 13:
                    RemoteControlClient.this.onUpdateMetadata(msg.arg1, msg.arg2, msg.obj);
                    return;
                case 14:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendMetadataWithArtwork_syncCacheLock((IRemoteControlDisplay) msg.obj, msg.arg1, msg.arg2);
                    }
                    return;
                case 15:
                    RemoteControlClient.this.onDisplayEnable((IRemoteControlDisplay) msg.obj, msg.arg1 == 1);
                    return;
                default:
                    Log.e(RemoteControlClient.TAG, "Unknown event " + msg.what + " in RemoteControlClient handler");
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPlaybackState_syncCacheLock(IRemoteControlDisplay target) {
        if (this.mCurrentClientGenId == this.mInternalClientGenId) {
            if (target != null) {
                try {
                    target.setPlaybackState(this.mInternalClientGenId, this.mPlaybackState, this.mPlaybackStateChangeTimeMs, this.mPlaybackPositionMs, this.mPlaybackSpeed);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in setPlaybackState() for dead display " + target, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                if (di.mEnabled) {
                    try {
                        di.mRcDisplay.setPlaybackState(this.mInternalClientGenId, this.mPlaybackState, this.mPlaybackStateChangeTimeMs, this.mPlaybackPositionMs, this.mPlaybackSpeed);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Error in setPlaybackState(), dead display " + di.mRcDisplay, e2);
                        displayIterator.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMetadata_syncCacheLock(IRemoteControlDisplay target) {
        if (this.mCurrentClientGenId == this.mInternalClientGenId) {
            if (target != null) {
                try {
                    target.setMetadata(this.mInternalClientGenId, this.mMetadata);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in setMetadata() for dead display " + target, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                if (di.mEnabled) {
                    try {
                        di.mRcDisplay.setMetadata(this.mInternalClientGenId, this.mMetadata);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Error in setMetadata(), dead display " + di.mRcDisplay, e2);
                        displayIterator.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTransportControlInfo_syncCacheLock(IRemoteControlDisplay target) {
        if (this.mCurrentClientGenId == this.mInternalClientGenId) {
            if (target != null) {
                try {
                    target.setTransportControlInfo(this.mInternalClientGenId, this.mTransportControlFlags, this.mPlaybackPositionCapabilities);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in setTransportControlFlags() for dead display " + target, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                if (di.mEnabled) {
                    try {
                        di.mRcDisplay.setTransportControlInfo(this.mInternalClientGenId, this.mTransportControlFlags, this.mPlaybackPositionCapabilities);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Error in setTransportControlFlags(), dead display " + di.mRcDisplay, e2);
                        displayIterator.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendArtwork_syncCacheLock(IRemoteControlDisplay target, int w, int h) {
        if (this.mCurrentClientGenId == this.mInternalClientGenId) {
            if (target != null) {
                DisplayInfoForClient di = new DisplayInfoForClient(target, w, h);
                sendArtworkToDisplay(di);
                return;
            }
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                if (!sendArtworkToDisplay(displayIterator.next())) {
                    displayIterator.remove();
                }
            }
        }
    }

    private boolean sendArtworkToDisplay(DisplayInfoForClient di) {
        if (di.mArtworkExpectedWidth > 0 && di.mArtworkExpectedHeight > 0) {
            Bitmap artwork = scaleBitmapIfTooBig(this.mOriginalArtwork, di.mArtworkExpectedWidth, di.mArtworkExpectedHeight);
            try {
                di.mRcDisplay.setArtwork(this.mInternalClientGenId, artwork);
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "Error in sendArtworkToDisplay(), dead display " + di.mRcDisplay, e);
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMetadataWithArtwork_syncCacheLock(IRemoteControlDisplay target, int w, int h) {
        if (this.mCurrentClientGenId == this.mInternalClientGenId) {
            if (target != null) {
                try {
                    if (w > 0 && h > 0) {
                        Bitmap artwork = scaleBitmapIfTooBig(this.mOriginalArtwork, w, h);
                        target.setAllMetadata(this.mInternalClientGenId, this.mMetadata, artwork);
                    } else {
                        target.setMetadata(this.mInternalClientGenId, this.mMetadata);
                    }
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in set(All)Metadata() for dead display " + target, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                try {
                    if (di.mEnabled) {
                        if (di.mArtworkExpectedWidth <= 0 || di.mArtworkExpectedHeight <= 0) {
                            di.mRcDisplay.setMetadata(this.mInternalClientGenId, this.mMetadata);
                        } else {
                            Bitmap artwork2 = scaleBitmapIfTooBig(this.mOriginalArtwork, di.mArtworkExpectedWidth, di.mArtworkExpectedHeight);
                            di.mRcDisplay.setAllMetadata(this.mInternalClientGenId, this.mMetadata, artwork2);
                        }
                    }
                } catch (RemoteException e2) {
                    Log.e(TAG, "Error when setting metadata, dead display " + di.mRcDisplay, e2);
                    displayIterator.remove();
                }
            }
        }
    }

    private static IAudioService getService() {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService(Context.AUDIO_SERVICE);
        sService = IAudioService.Stub.asInterface(b);
        return sService;
    }

    private void sendAudioServiceNewPlaybackInfo_syncCacheLock(int what, int value) {
        if (this.mRcseId == -1) {
            return;
        }
        IAudioService service = getService();
        try {
            service.setPlaybackInfoForRcc(this.mRcseId, what, value);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setPlaybackInfoForRcc", e);
        }
    }

    private void sendAudioServiceNewPlaybackState_syncCacheLock() {
        if (this.mRcseId == -1) {
            return;
        }
        IAudioService service = getService();
        try {
            service.setPlaybackStateForRcc(this.mRcseId, this.mPlaybackState, this.mPlaybackPositionMs, this.mPlaybackSpeed);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setPlaybackStateForRcc", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewInternalClientGen(int clientGeneration) {
        synchronized (this.mCacheLock) {
            this.mInternalClientGenId = clientGeneration;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewCurrentClientGen(int clientGeneration) {
        synchronized (this.mCacheLock) {
            this.mCurrentClientGenId = clientGeneration;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPlugDisplay(IRemoteControlDisplay rcd, int w, int h) {
        synchronized (this.mCacheLock) {
            boolean displayKnown = false;
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext() && !displayKnown) {
                DisplayInfoForClient di = displayIterator.next();
                displayKnown = di.mRcDisplay.asBinder().equals(rcd.asBinder());
                if (displayKnown && (di.mArtworkExpectedWidth != w || di.mArtworkExpectedHeight != h)) {
                    di.mArtworkExpectedWidth = w;
                    di.mArtworkExpectedHeight = h;
                    if (!sendArtworkToDisplay(di)) {
                        displayIterator.remove();
                    }
                }
            }
            if (!displayKnown) {
                this.mRcDisplays.add(new DisplayInfoForClient(rcd, w, h));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUnplugDisplay(IRemoteControlDisplay rcd) {
        synchronized (this.mCacheLock) {
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (true) {
                if (!displayIterator.hasNext()) {
                    break;
                }
                DisplayInfoForClient di = displayIterator.next();
                if (di.mRcDisplay.asBinder().equals(rcd.asBinder())) {
                    displayIterator.remove();
                    break;
                }
            }
            boolean oldNeedsPositionSync = this.mNeedsPositionSync;
            boolean newNeedsPositionSync = false;
            Iterator<DisplayInfoForClient> displayIterator2 = this.mRcDisplays.iterator();
            while (true) {
                if (!displayIterator2.hasNext()) {
                    break;
                }
                DisplayInfoForClient di2 = displayIterator2.next();
                if (di2.mWantsPositionSync) {
                    newNeedsPositionSync = true;
                    break;
                }
            }
            this.mNeedsPositionSync = newNeedsPositionSync;
            if (oldNeedsPositionSync != this.mNeedsPositionSync) {
                initiateCheckForDrift_syncCacheLock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateDisplayArtworkSize(IRemoteControlDisplay rcd, int w, int h) {
        synchronized (this.mCacheLock) {
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                if (di.mRcDisplay.asBinder().equals(rcd.asBinder()) && (di.mArtworkExpectedWidth != w || di.mArtworkExpectedHeight != h)) {
                    di.mArtworkExpectedWidth = w;
                    di.mArtworkExpectedHeight = h;
                    if (di.mEnabled && !sendArtworkToDisplay(di)) {
                        displayIterator.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayWantsSync(IRemoteControlDisplay rcd, boolean wantsSync) {
        synchronized (this.mCacheLock) {
            boolean oldNeedsPositionSync = this.mNeedsPositionSync;
            boolean newNeedsPositionSync = false;
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                if (di.mEnabled) {
                    if (di.mRcDisplay.asBinder().equals(rcd.asBinder())) {
                        di.mWantsPositionSync = wantsSync;
                    }
                    if (di.mWantsPositionSync) {
                        newNeedsPositionSync = true;
                    }
                }
            }
            this.mNeedsPositionSync = newNeedsPositionSync;
            if (oldNeedsPositionSync != this.mNeedsPositionSync) {
                initiateCheckForDrift_syncCacheLock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayEnable(IRemoteControlDisplay rcd, boolean enable) {
        synchronized (this.mCacheLock) {
            Iterator<DisplayInfoForClient> displayIterator = this.mRcDisplays.iterator();
            while (displayIterator.hasNext()) {
                DisplayInfoForClient di = displayIterator.next();
                if (di.mRcDisplay.asBinder().equals(rcd.asBinder())) {
                    di.mEnabled = enable;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSeekTo(int generationId, long timeMs) {
        synchronized (this.mCacheLock) {
            if (this.mCurrentClientGenId == generationId && this.mPositionUpdateListener != null) {
                this.mPositionUpdateListener.onPlaybackPositionUpdate(timeMs);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateMetadata(int generationId, int key, Object value) {
        synchronized (this.mCacheLock) {
            if (this.mCurrentClientGenId == generationId && this.mMetadataUpdateListener != null) {
                this.mMetadataUpdateListener.onMetadataUpdate(key, value);
            }
        }
    }

    private Bitmap scaleBitmapIfTooBig(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxWidth || height > maxHeight) {
                float scale = Math.min(maxWidth / width, maxHeight / height);
                int newWidth = Math.round(scale * width);
                int newHeight = Math.round(scale * height);
                Bitmap.Config newConfig = bitmap.getConfig();
                if (newConfig == null) {
                    newConfig = Bitmap.Config.ARGB_8888;
                }
                Bitmap outBitmap = Bitmap.createBitmap(newWidth, newHeight, newConfig);
                Canvas canvas = new Canvas(outBitmap);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                canvas.drawBitmap(bitmap, (Rect) null, new RectF(0.0f, 0.0f, outBitmap.getWidth(), outBitmap.getHeight()), paint);
                bitmap = outBitmap;
            }
        }
        return bitmap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean playbackPositionShouldMove(int playstate) {
        switch (playstate) {
            case 1:
            case 2:
            case 6:
            case 7:
            case 8:
            case 9:
                return false;
            case 3:
            case 4:
            case 5:
            default:
                return true;
        }
    }

    private static long getCheckPeriodFromSpeed(float speed) {
        if (Math.abs(speed) <= 1.0f) {
            return POSITION_REFRESH_PERIOD_PLAYING_MS;
        }
        return Math.max(15000.0f / Math.abs(speed), (long) POSITION_REFRESH_PERIOD_MIN_MS);
    }
}