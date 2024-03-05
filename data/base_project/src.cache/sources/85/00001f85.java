package com.android.server.power;

import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemVibrator;
import android.os.UserHandle;
import android.os.Vibrator;
import android.os.storage.IMountService;
import android.os.storage.IMountShutdownObserver;
import android.util.Log;
import com.android.internal.R;

/* loaded from: ShutdownThread.class */
public final class ShutdownThread extends Thread {
    private static final String TAG = "ShutdownThread";
    private static final int PHONE_STATE_POLL_SLEEP_MSEC = 500;
    private static final int MAX_BROADCAST_TIME = 10000;
    private static final int MAX_SHUTDOWN_WAIT_TIME = 20000;
    private static final int MAX_RADIO_WAIT_TIME = 12000;
    private static final int SHUTDOWN_VIBRATE_MS = 500;
    private static boolean mReboot;
    private static boolean mRebootSafeMode;
    private static String mRebootReason;
    public static final String SHUTDOWN_ACTION_PROPERTY = "sys.shutdown.requested";
    public static final String REBOOT_SAFEMODE_PROPERTY = "persist.sys.safemode";
    private final Object mActionDoneSync = new Object();
    private boolean mActionDone;
    private Context mContext;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mCpuWakeLock;
    private PowerManager.WakeLock mScreenWakeLock;
    private Handler mHandler;
    private static AlertDialog sConfirmDialog;
    private static Object sIsStartedGuard = new Object();
    private static boolean sIsStarted = false;
    private static final ShutdownThread sInstance = new ShutdownThread();

    private ShutdownThread() {
    }

    public static void shutdown(Context context, boolean confirm) {
        mReboot = false;
        mRebootSafeMode = false;
        shutdownInner(context, confirm);
    }

    static void shutdownInner(final Context context, boolean confirm) {
        synchronized (sIsStartedGuard) {
            if (sIsStarted) {
                Log.d(TAG, "Request to shutdown already running, returning.");
                return;
            }
            int longPressBehavior = context.getResources().getInteger(R.integer.config_longPressOnPowerBehavior);
            int resourceId = mRebootSafeMode ? R.string.reboot_safemode_confirm : longPressBehavior == 2 ? R.string.shutdown_confirm_question : R.string.shutdown_confirm;
            Log.d(TAG, "Notifying thread to start shutdown longPressBehavior=" + longPressBehavior);
            if (confirm) {
                CloseDialogReceiver closer = new CloseDialogReceiver(context);
                if (sConfirmDialog != null) {
                    sConfirmDialog.dismiss();
                }
                sConfirmDialog = new AlertDialog.Builder(context).setTitle(mRebootSafeMode ? R.string.reboot_safemode_title : R.string.power_off).setMessage(resourceId).setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.android.server.power.ShutdownThread.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        ShutdownThread.beginShutdownSequence(Context.this);
                    }
                }).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).create();
                closer.dialog = sConfirmDialog;
                sConfirmDialog.setOnDismissListener(closer);
                sConfirmDialog.getWindow().setType(2009);
                sConfirmDialog.show();
                return;
            }
            beginShutdownSequence(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ShutdownThread$CloseDialogReceiver.class */
    public static class CloseDialogReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        private Context mContext;
        public Dialog dialog;

        CloseDialogReceiver(Context context) {
            this.mContext = context;
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.registerReceiver(this, filter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.dialog.cancel();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface unused) {
            this.mContext.unregisterReceiver(this);
        }
    }

    public static void reboot(Context context, String reason, boolean confirm) {
        mReboot = true;
        mRebootSafeMode = false;
        mRebootReason = reason;
        shutdownInner(context, confirm);
    }

    public static void rebootSafeMode(Context context, boolean confirm) {
        mReboot = true;
        mRebootSafeMode = true;
        mRebootReason = null;
        shutdownInner(context, confirm);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void beginShutdownSequence(Context context) {
        synchronized (sIsStartedGuard) {
            if (sIsStarted) {
                Log.d(TAG, "Shutdown sequence already running, returning.");
                return;
            }
            sIsStarted = true;
            ProgressDialog pd = new ProgressDialog(context);
            pd.setTitle(context.getText(R.string.power_off));
            pd.setMessage(context.getText(R.string.shutdown_progress));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.getWindow().setType(2009);
            pd.show();
            sInstance.mContext = context;
            sInstance.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            sInstance.mCpuWakeLock = null;
            try {
                sInstance.mCpuWakeLock = sInstance.mPowerManager.newWakeLock(1, "ShutdownThread-cpu");
                sInstance.mCpuWakeLock.setReferenceCounted(false);
                sInstance.mCpuWakeLock.acquire();
            } catch (SecurityException e) {
                Log.w(TAG, "No permission to acquire wake lock", e);
                sInstance.mCpuWakeLock = null;
            }
            sInstance.mScreenWakeLock = null;
            if (sInstance.mPowerManager.isScreenOn()) {
                try {
                    sInstance.mScreenWakeLock = sInstance.mPowerManager.newWakeLock(26, "ShutdownThread-screen");
                    sInstance.mScreenWakeLock.setReferenceCounted(false);
                    sInstance.mScreenWakeLock.acquire();
                } catch (SecurityException e2) {
                    Log.w(TAG, "No permission to acquire wake lock", e2);
                    sInstance.mScreenWakeLock = null;
                }
            }
            sInstance.mHandler = new Handler() { // from class: com.android.server.power.ShutdownThread.2
            };
            sInstance.start();
        }
    }

    void actionDone() {
        synchronized (this.mActionDoneSync) {
            this.mActionDone = true;
            this.mActionDoneSync.notifyAll();
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        BroadcastReceiver br = new BroadcastReceiver() { // from class: com.android.server.power.ShutdownThread.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                ShutdownThread.this.actionDone();
            }
        };
        String reason = (mReboot ? "1" : "0") + (mRebootReason != null ? mRebootReason : "");
        SystemProperties.set(SHUTDOWN_ACTION_PROPERTY, reason);
        if (mRebootSafeMode) {
            SystemProperties.set(REBOOT_SAFEMODE_PROPERTY, "1");
        }
        Log.i(TAG, "Sending shutdown broadcast...");
        this.mActionDone = false;
        Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
        intent.addFlags(268435456);
        this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, null, br, this.mHandler, 0, null, null);
        long endTime = SystemClock.elapsedRealtime() + 10000;
        synchronized (this.mActionDoneSync) {
            while (true) {
                if (this.mActionDone) {
                    break;
                }
                long delay = endTime - SystemClock.elapsedRealtime();
                if (delay <= 0) {
                    Log.w(TAG, "Shutdown broadcast timed out");
                    break;
                }
                try {
                    this.mActionDoneSync.wait(delay);
                } catch (InterruptedException e) {
                }
            }
        }
        Log.i(TAG, "Shutting down activity manager...");
        IActivityManager am = ActivityManagerNative.asInterface(ServiceManager.checkService(Context.ACTIVITY_SERVICE));
        if (am != null) {
            try {
                am.shutdown(10000);
            } catch (RemoteException e2) {
            }
        }
        shutdownRadios(12000);
        IMountShutdownObserver observer = new IMountShutdownObserver.Stub() { // from class: com.android.server.power.ShutdownThread.4
            @Override // android.os.storage.IMountShutdownObserver
            public void onShutDownComplete(int statusCode) throws RemoteException {
                Log.w(ShutdownThread.TAG, "Result code " + statusCode + " from MountService.shutdown");
                ShutdownThread.this.actionDone();
            }
        };
        Log.i(TAG, "Shutting down MountService");
        this.mActionDone = false;
        long endShutTime = SystemClock.elapsedRealtime() + 20000;
        synchronized (this.mActionDoneSync) {
            try {
                IMountService mount = IMountService.Stub.asInterface(ServiceManager.checkService("mount"));
                if (mount != null) {
                    mount.shutdown(observer);
                } else {
                    Log.w(TAG, "MountService unavailable for shutdown");
                }
            } catch (Exception e3) {
                Log.e(TAG, "Exception during MountService shutdown", e3);
            }
            while (true) {
                if (this.mActionDone) {
                    break;
                }
                long delay2 = endShutTime - SystemClock.elapsedRealtime();
                if (delay2 <= 0) {
                    Log.w(TAG, "Shutdown wait timed out");
                    break;
                }
                try {
                    this.mActionDoneSync.wait(delay2);
                } catch (InterruptedException e4) {
                }
            }
        }
        rebootOrShutdown(mReboot, mRebootReason);
    }

    private void shutdownRadios(int timeout) {
        final long endTime = SystemClock.elapsedRealtime() + timeout;
        final boolean[] done = new boolean[1];
        Thread t = new Thread() { // from class: com.android.server.power.ShutdownThread.5
            /* JADX WARN: Removed duplicated region for block: B:11:0x0038 A[Catch: RemoteException -> 0x004c, TryCatch #2 {RemoteException -> 0x004c, blocks: (B:5:0x0023, B:11:0x0038), top: B:91:0x0023 }] */
            /* JADX WARN: Removed duplicated region for block: B:23:0x0073 A[Catch: RemoteException -> 0x0087, TryCatch #5 {RemoteException -> 0x0087, blocks: (B:17:0x005f, B:23:0x0073), top: B:97:0x005f }] */
            /* JADX WARN: Removed duplicated region for block: B:35:0x00ae A[Catch: RemoteException -> 0x00c2, TryCatch #0 {RemoteException -> 0x00c2, blocks: (B:29:0x009a, B:35:0x00ae), top: B:87:0x009a }] */
            /* JADX WARN: Removed duplicated region for block: B:42:0x00e3  */
            /* JADX WARN: Removed duplicated region for block: B:87:0x009a A[EXC_TOP_SPLITTER, SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:97:0x005f A[EXC_TOP_SPLITTER, SYNTHETIC] */
            @Override // java.lang.Thread, java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 415
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ShutdownThread.AnonymousClass5.run():void");
            }
        };
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException e) {
        }
        if (!done[0]) {
            Log.w(TAG, "Timed out waiting for NFC, Radio and Bluetooth shutdown.");
        }
    }

    public static void rebootOrShutdown(boolean reboot, String reason) {
        if (reboot) {
            Log.i(TAG, "Rebooting, reason: " + reason);
            PowerManagerService.lowLevelReboot(reason);
            Log.e(TAG, "Reboot failed, will attempt shutdown instead");
        } else {
            Vibrator vibrator = new SystemVibrator();
            try {
                vibrator.vibrate(500L);
            } catch (Exception e) {
                Log.w(TAG, "Failed to vibrate during shutdown.", e);
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e2) {
            }
        }
        Log.i(TAG, "Performing low-level shutdown...");
        PowerManagerService.lowLevelShutdown();
    }
}