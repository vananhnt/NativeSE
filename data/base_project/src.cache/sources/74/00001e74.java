package com.android.server.dreams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamService;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import android.view.IWindowManager;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DreamController.class */
public final class DreamController {
    private static final String TAG = "DreamController";
    private static final int DREAM_CONNECTION_TIMEOUT = 5000;
    private final Context mContext;
    private final Handler mHandler;
    private final Listener mListener;
    private DreamRecord mCurrentDream;
    private final Intent mDreamingStartedIntent = new Intent(Intent.ACTION_DREAMING_STARTED).addFlags(1073741824);
    private final Intent mDreamingStoppedIntent = new Intent(Intent.ACTION_DREAMING_STOPPED).addFlags(1073741824);
    private final Intent mCloseNotificationShadeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    private final Runnable mStopUnconnectedDreamRunnable = new Runnable() { // from class: com.android.server.dreams.DreamController.1
        @Override // java.lang.Runnable
        public void run() {
            if (DreamController.this.mCurrentDream != null && DreamController.this.mCurrentDream.mBound && !DreamController.this.mCurrentDream.mConnected) {
                Slog.w(DreamController.TAG, "Bound dream did not connect in the time allotted");
                DreamController.this.stopDream();
            }
        }
    };
    private final IWindowManager mIWindowManager = WindowManagerGlobal.getWindowManagerService();

    /* loaded from: DreamController$Listener.class */
    public interface Listener {
        void onDreamStopped(Binder binder);
    }

    public DreamController(Context context, Handler handler, Listener listener) {
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
    }

    public void dump(PrintWriter pw) {
        pw.println("Dreamland:");
        if (this.mCurrentDream != null) {
            pw.println("  mCurrentDream:");
            pw.println("    mToken=" + this.mCurrentDream.mToken);
            pw.println("    mName=" + this.mCurrentDream.mName);
            pw.println("    mIsTest=" + this.mCurrentDream.mIsTest);
            pw.println("    mUserId=" + this.mCurrentDream.mUserId);
            pw.println("    mBound=" + this.mCurrentDream.mBound);
            pw.println("    mService=" + this.mCurrentDream.mService);
            pw.println("    mSentStartBroadcast=" + this.mCurrentDream.mSentStartBroadcast);
            return;
        }
        pw.println("  mCurrentDream: null");
    }

    public void startDream(Binder token, ComponentName name, boolean isTest, int userId) {
        stopDream();
        this.mContext.sendBroadcastAsUser(this.mCloseNotificationShadeIntent, UserHandle.ALL);
        Slog.i(TAG, "Starting dream: name=" + name + ", isTest=" + isTest + ", userId=" + userId);
        this.mCurrentDream = new DreamRecord(token, name, isTest, userId);
        try {
            this.mIWindowManager.addWindowToken(token, WindowManager.LayoutParams.TYPE_DREAM);
            Intent intent = new Intent(DreamService.SERVICE_INTERFACE);
            intent.setComponent(name);
            intent.addFlags(8388608);
            try {
                if (!this.mContext.bindServiceAsUser(intent, this.mCurrentDream, 1, new UserHandle(userId))) {
                    Slog.e(TAG, "Unable to bind dream service: " + intent);
                    stopDream();
                    return;
                }
                this.mCurrentDream.mBound = true;
                this.mHandler.postDelayed(this.mStopUnconnectedDreamRunnable, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            } catch (SecurityException ex) {
                Slog.e(TAG, "Unable to bind dream service: " + intent, ex);
                stopDream();
            }
        } catch (RemoteException ex2) {
            Slog.e(TAG, "Unable to add window token for dream.", ex2);
            stopDream();
        }
    }

    public void stopDream() {
        if (this.mCurrentDream == null) {
            return;
        }
        final DreamRecord oldDream = this.mCurrentDream;
        this.mCurrentDream = null;
        Slog.i(TAG, "Stopping dream: name=" + oldDream.mName + ", isTest=" + oldDream.mIsTest + ", userId=" + oldDream.mUserId);
        this.mHandler.removeCallbacks(this.mStopUnconnectedDreamRunnable);
        if (oldDream.mSentStartBroadcast) {
            this.mContext.sendBroadcastAsUser(this.mDreamingStoppedIntent, UserHandle.ALL);
        }
        if (oldDream.mService != null) {
            try {
                oldDream.mService.detach();
            } catch (RemoteException e) {
            }
            try {
                oldDream.mService.asBinder().unlinkToDeath(oldDream, 0);
            } catch (NoSuchElementException e2) {
            }
            oldDream.mService = null;
        }
        if (oldDream.mBound) {
            this.mContext.unbindService(oldDream);
        }
        try {
            this.mIWindowManager.removeWindowToken(oldDream.mToken);
        } catch (RemoteException ex) {
            Slog.w(TAG, "Error removing window token for dream.", ex);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController.2
            @Override // java.lang.Runnable
            public void run() {
                DreamController.this.mListener.onDreamStopped(oldDream.mToken);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attach(IDreamService service) {
        try {
            service.asBinder().linkToDeath(this.mCurrentDream, 0);
            service.attach(this.mCurrentDream.mToken);
            this.mCurrentDream.mService = service;
            if (!this.mCurrentDream.mIsTest) {
                this.mContext.sendBroadcastAsUser(this.mDreamingStartedIntent, UserHandle.ALL);
                this.mCurrentDream.mSentStartBroadcast = true;
            }
        } catch (RemoteException ex) {
            Slog.e(TAG, "The dream service died unexpectedly.", ex);
            stopDream();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DreamController$DreamRecord.class */
    public final class DreamRecord implements IBinder.DeathRecipient, ServiceConnection {
        public final Binder mToken;
        public final ComponentName mName;
        public final boolean mIsTest;
        public final int mUserId;
        public boolean mBound;
        public boolean mConnected;
        public IDreamService mService;
        public boolean mSentStartBroadcast;

        public DreamRecord(Binder token, ComponentName name, boolean isTest, int userId) {
            this.mToken = token;
            this.mName = name;
            this.mIsTest = isTest;
            this.mUserId = userId;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            DreamController.this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController.DreamRecord.1
                @Override // java.lang.Runnable
                public void run() {
                    DreamRecord.this.mService = null;
                    if (DreamController.this.mCurrentDream == DreamRecord.this) {
                        DreamController.this.stopDream();
                    }
                }
            });
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, final IBinder service) {
            DreamController.this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController.DreamRecord.2
                @Override // java.lang.Runnable
                public void run() {
                    DreamRecord.this.mConnected = true;
                    if (DreamController.this.mCurrentDream == DreamRecord.this && DreamRecord.this.mService == null) {
                        DreamController.this.attach(IDreamService.Stub.asInterface(service));
                    }
                }
            });
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            DreamController.this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController.DreamRecord.3
                @Override // java.lang.Runnable
                public void run() {
                    DreamRecord.this.mService = null;
                    if (DreamController.this.mCurrentDream == DreamRecord.this) {
                        DreamController.this.stopDream();
                    }
                }
            });
        }
    }
}