package android.support.v4.media;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

/* loaded from: TransportMediatorJellybeanMR2.class */
class TransportMediatorJellybeanMR2 implements RemoteControlClient.OnGetPlaybackPositionListener, RemoteControlClient.OnPlaybackPositionUpdateListener {
    boolean mAudioFocused;
    final AudioManager mAudioManager;
    final Context mContext;
    boolean mFocused;
    final Intent mIntent;
    PendingIntent mPendingIntent;
    final String mReceiverAction;
    final IntentFilter mReceiverFilter;
    RemoteControlClient mRemoteControl;
    final View mTargetView;
    final TransportMediatorCallback mTransportCallback;
    final ViewTreeObserver.OnWindowAttachListener mWindowAttachListener = new ViewTreeObserver.OnWindowAttachListener(this) { // from class: android.support.v4.media.TransportMediatorJellybeanMR2.1
        final TransportMediatorJellybeanMR2 this$0;

        {
            this.this$0 = this;
        }

        @Override // android.view.ViewTreeObserver.OnWindowAttachListener
        public void onWindowAttached() {
            this.this$0.windowAttached();
        }

        @Override // android.view.ViewTreeObserver.OnWindowAttachListener
        public void onWindowDetached() {
            this.this$0.windowDetached();
        }
    };
    final ViewTreeObserver.OnWindowFocusChangeListener mWindowFocusListener = new ViewTreeObserver.OnWindowFocusChangeListener(this) { // from class: android.support.v4.media.TransportMediatorJellybeanMR2.2
        final TransportMediatorJellybeanMR2 this$0;

        {
            this.this$0 = this;
        }

        @Override // android.view.ViewTreeObserver.OnWindowFocusChangeListener
        public void onWindowFocusChanged(boolean z) {
            if (z) {
                this.this$0.gainFocus();
            } else {
                this.this$0.loseFocus();
            }
        }
    };
    final BroadcastReceiver mMediaButtonReceiver = new BroadcastReceiver(this) { // from class: android.support.v4.media.TransportMediatorJellybeanMR2.3
        final TransportMediatorJellybeanMR2 this$0;

        {
            this.this$0 = this;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                this.this$0.mTransportCallback.handleKey((KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT));
            } catch (ClassCastException e) {
                Log.w("TransportController", e);
            }
        }
    };
    AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener(this) { // from class: android.support.v4.media.TransportMediatorJellybeanMR2.4
        final TransportMediatorJellybeanMR2 this$0;

        {
            this.this$0 = this;
        }

        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int i) {
            this.this$0.mTransportCallback.handleAudioFocusChange(i);
        }
    };
    int mPlayState = 0;

    public TransportMediatorJellybeanMR2(Context context, AudioManager audioManager, View view, TransportMediatorCallback transportMediatorCallback) {
        this.mContext = context;
        this.mAudioManager = audioManager;
        this.mTargetView = view;
        this.mTransportCallback = transportMediatorCallback;
        this.mReceiverAction = context.getPackageName() + ":transport:" + System.identityHashCode(this);
        this.mIntent = new Intent(this.mReceiverAction);
        this.mIntent.setPackage(context.getPackageName());
        this.mReceiverFilter = new IntentFilter();
        this.mReceiverFilter.addAction(this.mReceiverAction);
        this.mTargetView.getViewTreeObserver().addOnWindowAttachListener(this.mWindowAttachListener);
        this.mTargetView.getViewTreeObserver().addOnWindowFocusChangeListener(this.mWindowFocusListener);
    }

    public void destroy() {
        windowDetached();
        this.mTargetView.getViewTreeObserver().removeOnWindowAttachListener(this.mWindowAttachListener);
        this.mTargetView.getViewTreeObserver().removeOnWindowFocusChangeListener(this.mWindowFocusListener);
    }

    void dropAudioFocus() {
        if (this.mAudioFocused) {
            this.mAudioFocused = false;
            this.mAudioManager.abandonAudioFocus(this.mAudioFocusChangeListener);
        }
    }

    void gainFocus() {
        if (this.mFocused) {
            return;
        }
        this.mFocused = true;
        this.mAudioManager.registerMediaButtonEventReceiver(this.mPendingIntent);
        this.mAudioManager.registerRemoteControlClient(this.mRemoteControl);
        if (this.mPlayState == 3) {
            takeAudioFocus();
        }
    }

    public Object getRemoteControlClient() {
        return this.mRemoteControl;
    }

    void loseFocus() {
        dropAudioFocus();
        if (this.mFocused) {
            this.mFocused = false;
            this.mAudioManager.unregisterRemoteControlClient(this.mRemoteControl);
            this.mAudioManager.unregisterMediaButtonEventReceiver(this.mPendingIntent);
        }
    }

    @Override // android.media.RemoteControlClient.OnGetPlaybackPositionListener
    public long onGetPlaybackPosition() {
        return this.mTransportCallback.getPlaybackPosition();
    }

    @Override // android.media.RemoteControlClient.OnPlaybackPositionUpdateListener
    public void onPlaybackPositionUpdate(long j) {
        this.mTransportCallback.playbackPositionUpdate(j);
    }

    public void pausePlaying() {
        if (this.mPlayState == 3) {
            this.mPlayState = 2;
            this.mRemoteControl.setPlaybackState(2);
        }
        dropAudioFocus();
    }

    public void refreshState(boolean z, long j, int i) {
        RemoteControlClient remoteControlClient = this.mRemoteControl;
        if (remoteControlClient != null) {
            remoteControlClient.setPlaybackState(z ? 3 : 1, j, z ? 1.0f : 0.0f);
            this.mRemoteControl.setTransportControlFlags(i);
        }
    }

    public void startPlaying() {
        if (this.mPlayState != 3) {
            this.mPlayState = 3;
            this.mRemoteControl.setPlaybackState(3);
        }
        if (this.mFocused) {
            takeAudioFocus();
        }
    }

    public void stopPlaying() {
        if (this.mPlayState != 1) {
            this.mPlayState = 1;
            this.mRemoteControl.setPlaybackState(1);
        }
        dropAudioFocus();
    }

    void takeAudioFocus() {
        if (this.mAudioFocused) {
            return;
        }
        this.mAudioFocused = true;
        this.mAudioManager.requestAudioFocus(this.mAudioFocusChangeListener, 3, 1);
    }

    void windowAttached() {
        this.mContext.registerReceiver(this.mMediaButtonReceiver, this.mReceiverFilter);
        this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, this.mIntent, 268435456);
        this.mRemoteControl = new RemoteControlClient(this.mPendingIntent);
        this.mRemoteControl.setOnGetPlaybackPositionListener(this);
        this.mRemoteControl.setPlaybackPositionUpdateListener(this);
    }

    void windowDetached() {
        loseFocus();
        if (this.mPendingIntent != null) {
            this.mContext.unregisterReceiver(this.mMediaButtonReceiver);
            this.mPendingIntent.cancel();
            this.mPendingIntent = null;
            this.mRemoteControl = null;
        }
    }
}