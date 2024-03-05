package android.support.v4.media;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.view.KeyEventCompat;
import android.view.KeyEvent;
import android.view.View;
import java.util.ArrayList;

/* loaded from: TransportMediator.class */
public class TransportMediator extends TransportController {
    public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
    public static final int FLAG_KEY_MEDIA_NEXT = 128;
    public static final int FLAG_KEY_MEDIA_PAUSE = 16;
    public static final int FLAG_KEY_MEDIA_PLAY = 4;
    public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
    public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
    public static final int FLAG_KEY_MEDIA_REWIND = 2;
    public static final int FLAG_KEY_MEDIA_STOP = 32;
    public static final int KEYCODE_MEDIA_PAUSE = 127;
    public static final int KEYCODE_MEDIA_PLAY = 126;
    public static final int KEYCODE_MEDIA_RECORD = 130;
    final AudioManager mAudioManager;
    final TransportPerformer mCallbacks;
    final Context mContext;
    final TransportMediatorJellybeanMR2 mController;
    final Object mDispatcherState;
    final KeyEvent.Callback mKeyEventCallback;
    final ArrayList<TransportStateListener> mListeners;
    final TransportMediatorCallback mTransportKeyCallback;
    final View mView;

    public TransportMediator(Activity activity, TransportPerformer transportPerformer) {
        this(activity, null, transportPerformer);
    }

    private TransportMediator(Activity activity, View view, TransportPerformer transportPerformer) {
        this.mListeners = new ArrayList<>();
        this.mTransportKeyCallback = new TransportMediatorCallback(this) { // from class: android.support.v4.media.TransportMediator.1
            final TransportMediator this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v4.media.TransportMediatorCallback
            public long getPlaybackPosition() {
                return this.this$0.mCallbacks.onGetCurrentPosition();
            }

            @Override // android.support.v4.media.TransportMediatorCallback
            public void handleAudioFocusChange(int i) {
                this.this$0.mCallbacks.onAudioFocusChange(i);
            }

            @Override // android.support.v4.media.TransportMediatorCallback
            public void handleKey(KeyEvent keyEvent) {
                keyEvent.dispatch(this.this$0.mKeyEventCallback);
            }

            @Override // android.support.v4.media.TransportMediatorCallback
            public void playbackPositionUpdate(long j) {
                this.this$0.mCallbacks.onSeekTo(j);
            }
        };
        this.mKeyEventCallback = new KeyEvent.Callback(this) { // from class: android.support.v4.media.TransportMediator.2
            final TransportMediator this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.KeyEvent.Callback
            public boolean onKeyDown(int i, KeyEvent keyEvent) {
                return TransportMediator.isMediaKey(i) ? this.this$0.mCallbacks.onMediaButtonDown(i, keyEvent) : false;
            }

            @Override // android.view.KeyEvent.Callback
            public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
                return false;
            }

            @Override // android.view.KeyEvent.Callback
            public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
                return false;
            }

            @Override // android.view.KeyEvent.Callback
            public boolean onKeyUp(int i, KeyEvent keyEvent) {
                return TransportMediator.isMediaKey(i) ? this.this$0.mCallbacks.onMediaButtonUp(i, keyEvent) : false;
            }
        };
        this.mContext = activity != null ? activity : view.getContext();
        this.mCallbacks = transportPerformer;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        this.mView = activity != null ? activity.getWindow().getDecorView() : view;
        this.mDispatcherState = KeyEventCompat.getKeyDispatcherState(this.mView);
        if (Build.VERSION.SDK_INT >= 18) {
            this.mController = new TransportMediatorJellybeanMR2(this.mContext, this.mAudioManager, this.mView, this.mTransportKeyCallback);
        } else {
            this.mController = null;
        }
    }

    public TransportMediator(View view, TransportPerformer transportPerformer) {
        this(null, view, transportPerformer);
    }

    private TransportStateListener[] getListeners() {
        if (this.mListeners.size() <= 0) {
            return null;
        }
        TransportStateListener[] transportStateListenerArr = new TransportStateListener[this.mListeners.size()];
        this.mListeners.toArray(transportStateListenerArr);
        return transportStateListenerArr;
    }

    static boolean isMediaKey(int i) {
        if (i == 79 || i == 130) {
            return true;
        }
        switch (i) {
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
                return true;
            default:
                switch (i) {
                    case 126:
                    case 127:
                        return true;
                    default:
                        return false;
                }
        }
    }

    private void pushControllerState() {
        TransportMediatorJellybeanMR2 transportMediatorJellybeanMR2 = this.mController;
        if (transportMediatorJellybeanMR2 != null) {
            transportMediatorJellybeanMR2.refreshState(this.mCallbacks.onIsPlaying(), this.mCallbacks.onGetCurrentPosition(), this.mCallbacks.onGetTransportControlFlags());
        }
    }

    private void reportPlayingChanged() {
        TransportStateListener[] listeners = getListeners();
        if (listeners != null) {
            for (TransportStateListener transportStateListener : listeners) {
                transportStateListener.onPlayingChanged(this);
            }
        }
    }

    private void reportTransportControlsChanged() {
        TransportStateListener[] listeners = getListeners();
        if (listeners != null) {
            for (TransportStateListener transportStateListener : listeners) {
                transportStateListener.onTransportControlsChanged(this);
            }
        }
    }

    public void destroy() {
        this.mController.destroy();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return KeyEventCompat.dispatch(keyEvent, this.mKeyEventCallback, this.mDispatcherState, this);
    }

    @Override // android.support.v4.media.TransportController
    public int getBufferPercentage() {
        return this.mCallbacks.onGetBufferPercentage();
    }

    @Override // android.support.v4.media.TransportController
    public long getCurrentPosition() {
        return this.mCallbacks.onGetCurrentPosition();
    }

    @Override // android.support.v4.media.TransportController
    public long getDuration() {
        return this.mCallbacks.onGetDuration();
    }

    public Object getRemoteControlClient() {
        TransportMediatorJellybeanMR2 transportMediatorJellybeanMR2 = this.mController;
        return transportMediatorJellybeanMR2 != null ? transportMediatorJellybeanMR2.getRemoteControlClient() : null;
    }

    @Override // android.support.v4.media.TransportController
    public int getTransportControlFlags() {
        return this.mCallbacks.onGetTransportControlFlags();
    }

    @Override // android.support.v4.media.TransportController
    public boolean isPlaying() {
        return this.mCallbacks.onIsPlaying();
    }

    @Override // android.support.v4.media.TransportController
    public void pausePlaying() {
        TransportMediatorJellybeanMR2 transportMediatorJellybeanMR2 = this.mController;
        if (transportMediatorJellybeanMR2 != null) {
            transportMediatorJellybeanMR2.pausePlaying();
        }
        this.mCallbacks.onPause();
        pushControllerState();
        reportPlayingChanged();
    }

    public void refreshState() {
        pushControllerState();
        reportPlayingChanged();
        reportTransportControlsChanged();
    }

    @Override // android.support.v4.media.TransportController
    public void registerStateListener(TransportStateListener transportStateListener) {
        this.mListeners.add(transportStateListener);
    }

    @Override // android.support.v4.media.TransportController
    public void seekTo(long j) {
        this.mCallbacks.onSeekTo(j);
    }

    @Override // android.support.v4.media.TransportController
    public void startPlaying() {
        TransportMediatorJellybeanMR2 transportMediatorJellybeanMR2 = this.mController;
        if (transportMediatorJellybeanMR2 != null) {
            transportMediatorJellybeanMR2.startPlaying();
        }
        this.mCallbacks.onStart();
        pushControllerState();
        reportPlayingChanged();
    }

    @Override // android.support.v4.media.TransportController
    public void stopPlaying() {
        TransportMediatorJellybeanMR2 transportMediatorJellybeanMR2 = this.mController;
        if (transportMediatorJellybeanMR2 != null) {
            transportMediatorJellybeanMR2.stopPlaying();
        }
        this.mCallbacks.onStop();
        pushControllerState();
        reportPlayingChanged();
    }

    @Override // android.support.v4.media.TransportController
    public void unregisterStateListener(TransportStateListener transportStateListener) {
        this.mListeners.remove(transportStateListener);
    }
}