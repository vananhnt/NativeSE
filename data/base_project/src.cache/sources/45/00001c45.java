package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;

/* loaded from: DockObserver.class */
final class DockObserver extends UEventObserver {
    private static final String TAG = DockObserver.class.getSimpleName();
    private static final String DOCK_UEVENT_MATCH = "DEVPATH=/devices/virtual/switch/dock";
    private static final String DOCK_STATE_PATH = "/sys/class/switch/dock/state";
    private static final int MSG_DOCK_STATE_CHANGED = 0;
    private boolean mSystemReady;
    private final Context mContext;
    private final PowerManager mPowerManager;
    private final PowerManager.WakeLock mWakeLock;
    private final Object mLock = new Object();
    private int mDockState = 0;
    private int mPreviousDockState = 0;
    private final Handler mHandler = new Handler(true) { // from class: com.android.server.DockObserver.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DockObserver.this.handleDockStateChange();
                    return;
                default:
                    return;
            }
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DockObserver.init():void, file: DockObserver.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void init() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DockObserver.init():void, file: DockObserver.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DockObserver.init():void");
    }

    public DockObserver(Context context) {
        this.mContext = context;
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = this.mPowerManager.newWakeLock(1, TAG);
        init();
        startObserving(DOCK_UEVENT_MATCH);
    }

    @Override // android.os.UEventObserver
    public void onUEvent(UEventObserver.UEvent event) {
        if (Log.isLoggable(TAG, 2)) {
            Slog.v(TAG, "Dock UEVENT: " + event.toString());
        }
        synchronized (this.mLock) {
            try {
                int newState = Integer.parseInt(event.get("SWITCH_STATE"));
                if (newState != this.mDockState) {
                    this.mPreviousDockState = this.mDockState;
                    this.mDockState = newState;
                    if (this.mSystemReady) {
                        this.mPowerManager.wakeUp(SystemClock.uptimeMillis());
                        updateLocked();
                    }
                }
            } catch (NumberFormatException e) {
                Slog.e(TAG, "Could not parse switch state from event " + event);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        synchronized (this.mLock) {
            if (this.mDockState != 0) {
                updateLocked();
            }
            this.mSystemReady = true;
        }
    }

    private void updateLocked() {
        this.mWakeLock.acquire();
        this.mHandler.sendEmptyMessage(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDockStateChange() {
        String soundPath;
        Uri soundUri;
        Ringtone sfx;
        synchronized (this.mLock) {
            Slog.i(TAG, "Dock state changed: " + this.mDockState);
            ContentResolver cr = this.mContext.getContentResolver();
            if (Settings.Global.getInt(cr, "device_provisioned", 0) == 0) {
                Slog.i(TAG, "Device not provisioned, skipping dock broadcast");
                return;
            }
            Intent intent = new Intent(Intent.ACTION_DOCK_EVENT);
            intent.addFlags(536870912);
            intent.putExtra(Intent.EXTRA_DOCK_STATE, this.mDockState);
            if (Settings.Global.getInt(cr, "dock_sounds_enabled", 1) == 1) {
                String whichSound = null;
                if (this.mDockState == 0) {
                    if (this.mPreviousDockState == 1 || this.mPreviousDockState == 3 || this.mPreviousDockState == 4) {
                        whichSound = "desk_undock_sound";
                    } else if (this.mPreviousDockState == 2) {
                        whichSound = "car_undock_sound";
                    }
                } else if (this.mDockState == 1 || this.mDockState == 3 || this.mDockState == 4) {
                    whichSound = "desk_dock_sound";
                } else if (this.mDockState == 2) {
                    whichSound = "car_dock_sound";
                }
                if (whichSound != null && (soundPath = Settings.Global.getString(cr, whichSound)) != null && (soundUri = Uri.parse("file://" + soundPath)) != null && (sfx = RingtoneManager.getRingtone(this.mContext, soundUri)) != null) {
                    sfx.setStreamType(1);
                    sfx.play();
                }
            }
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            this.mWakeLock.release();
        }
    }
}