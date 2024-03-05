package com.android.server.power;

import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.EventLog;
import android.view.WindowManagerPolicy;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.server.EventLogTags;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Notifier.class */
public final class Notifier {
    private static final String TAG = "PowerManagerNotifier";
    private static final boolean DEBUG = false;
    private static final int POWER_STATE_UNKNOWN = 0;
    private static final int POWER_STATE_AWAKE = 1;
    private static final int POWER_STATE_ASLEEP = 2;
    private static final int MSG_USER_ACTIVITY = 1;
    private static final int MSG_BROADCAST = 2;
    private static final int MSG_WIRELESS_CHARGING_STARTED = 3;
    private final Context mContext;
    private final IBatteryStats mBatteryStats;
    private final IAppOpsService mAppOps;
    private final SuspendBlocker mSuspendBlocker;
    private final ScreenOnBlocker mScreenOnBlocker;
    private final WindowManagerPolicy mPolicy;
    private final NotifierHandler mHandler;
    private final Intent mScreenOffIntent;
    private int mActualPowerState;
    private int mLastGoToSleepReason;
    private boolean mPendingWakeUpBroadcast;
    private boolean mPendingGoToSleepBroadcast;
    private int mBroadcastedPowerState;
    private boolean mBroadcastInProgress;
    private long mBroadcastStartTime;
    private boolean mUserActivityPending;
    private boolean mScreenOnBlockerAcquired;
    private final Object mLock = new Object();
    private final WindowManagerPolicy.ScreenOnListener mScreenOnListener = new WindowManagerPolicy.ScreenOnListener() { // from class: com.android.server.power.Notifier.1
        @Override // android.view.WindowManagerPolicy.ScreenOnListener
        public void onScreenOn() {
            synchronized (Notifier.this.mLock) {
                if (Notifier.this.mScreenOnBlockerAcquired && !Notifier.this.mPendingWakeUpBroadcast) {
                    Notifier.this.mScreenOnBlockerAcquired = false;
                    Notifier.this.mScreenOnBlocker.release();
                }
            }
        }
    };
    private final BroadcastReceiver mWakeUpBroadcastDone = new BroadcastReceiver() { // from class: com.android.server.power.Notifier.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_BROADCAST_DONE, 1, Long.valueOf(SystemClock.uptimeMillis() - Notifier.this.mBroadcastStartTime), 1);
            Notifier.this.sendNextBroadcast();
        }
    };
    private final BroadcastReceiver mGoToSleepBroadcastDone = new BroadcastReceiver() { // from class: com.android.server.power.Notifier.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_BROADCAST_DONE, 0, Long.valueOf(SystemClock.uptimeMillis() - Notifier.this.mBroadcastStartTime), 1);
            Notifier.this.sendNextBroadcast();
        }
    };
    private final Intent mScreenOnIntent = new Intent(Intent.ACTION_SCREEN_ON);

    public Notifier(Looper looper, Context context, IBatteryStats batteryStats, IAppOpsService appOps, SuspendBlocker suspendBlocker, ScreenOnBlocker screenOnBlocker, WindowManagerPolicy policy) {
        this.mContext = context;
        this.mBatteryStats = batteryStats;
        this.mAppOps = appOps;
        this.mSuspendBlocker = suspendBlocker;
        this.mScreenOnBlocker = screenOnBlocker;
        this.mPolicy = policy;
        this.mHandler = new NotifierHandler(looper);
        this.mScreenOnIntent.addFlags(1342177280);
        this.mScreenOffIntent = new Intent(Intent.ACTION_SCREEN_OFF);
        this.mScreenOffIntent.addFlags(1342177280);
    }

    public void onWakeLockAcquired(int flags, String tag, String packageName, int ownerUid, int ownerPid, WorkSource workSource) {
        try {
            int monitorType = getBatteryStatsWakeLockMonitorType(flags);
            if (workSource != null) {
                this.mBatteryStats.noteStartWakelockFromSource(workSource, ownerPid, tag, monitorType);
            } else {
                this.mBatteryStats.noteStartWakelock(ownerUid, ownerPid, tag, monitorType);
                this.mAppOps.startOperation(AppOpsManager.getToken(this.mAppOps), 40, ownerUid, packageName);
            }
        } catch (RemoteException e) {
        }
    }

    public void onWakeLockReleased(int flags, String tag, String packageName, int ownerUid, int ownerPid, WorkSource workSource) {
        try {
            int monitorType = getBatteryStatsWakeLockMonitorType(flags);
            if (workSource != null) {
                this.mBatteryStats.noteStopWakelockFromSource(workSource, ownerPid, tag, monitorType);
            } else {
                this.mBatteryStats.noteStopWakelock(ownerUid, ownerPid, tag, monitorType);
                this.mAppOps.finishOperation(AppOpsManager.getToken(this.mAppOps), 40, ownerUid, packageName);
            }
        } catch (RemoteException e) {
        }
    }

    private static int getBatteryStatsWakeLockMonitorType(int flags) {
        switch (flags & 65535) {
            case 1:
            case 32:
                return 0;
            default:
                return 1;
        }
    }

    public void onScreenOn() {
        try {
            this.mBatteryStats.noteScreenOn();
        } catch (RemoteException e) {
        }
    }

    public void onScreenOff() {
        try {
            this.mBatteryStats.noteScreenOff();
        } catch (RemoteException e) {
        }
    }

    public void onScreenBrightness(int brightness) {
        try {
            this.mBatteryStats.noteScreenBrightness(brightness);
        } catch (RemoteException e) {
        }
    }

    public void onWakeUpStarted() {
        synchronized (this.mLock) {
            if (this.mActualPowerState != 1) {
                this.mActualPowerState = 1;
                this.mPendingWakeUpBroadcast = true;
                if (!this.mScreenOnBlockerAcquired) {
                    this.mScreenOnBlockerAcquired = true;
                    this.mScreenOnBlocker.acquire();
                }
                updatePendingBroadcastLocked();
            }
        }
    }

    public void onWakeUpFinished() {
    }

    public void onGoToSleepStarted(int reason) {
        synchronized (this.mLock) {
            this.mLastGoToSleepReason = reason;
        }
    }

    public void onGoToSleepFinished() {
        synchronized (this.mLock) {
            if (this.mActualPowerState != 2) {
                this.mActualPowerState = 2;
                this.mPendingGoToSleepBroadcast = true;
                if (this.mUserActivityPending) {
                    this.mUserActivityPending = false;
                    this.mHandler.removeMessages(1);
                }
                updatePendingBroadcastLocked();
            }
        }
    }

    public void onUserActivity(int event, int uid) {
        try {
            this.mBatteryStats.noteUserActivity(uid, event);
        } catch (RemoteException e) {
        }
        synchronized (this.mLock) {
            if (!this.mUserActivityPending) {
                this.mUserActivityPending = true;
                Message msg = this.mHandler.obtainMessage(1);
                msg.setAsynchronous(true);
                this.mHandler.sendMessage(msg);
            }
        }
    }

    public void onWirelessChargingStarted() {
        this.mSuspendBlocker.acquire();
        Message msg = this.mHandler.obtainMessage(3);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    private void updatePendingBroadcastLocked() {
        if (this.mBroadcastInProgress || this.mActualPowerState == 0) {
            return;
        }
        if (this.mPendingWakeUpBroadcast || this.mPendingGoToSleepBroadcast || this.mActualPowerState != this.mBroadcastedPowerState) {
            this.mBroadcastInProgress = true;
            this.mSuspendBlocker.acquire();
            Message msg = this.mHandler.obtainMessage(2);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    private void finishPendingBroadcastLocked() {
        this.mBroadcastInProgress = false;
        this.mSuspendBlocker.release();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUserActivity() {
        synchronized (this.mLock) {
            if (this.mUserActivityPending) {
                this.mUserActivityPending = false;
                this.mPolicy.userActivity();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNextBroadcast() {
        synchronized (this.mLock) {
            if (this.mBroadcastedPowerState == 0) {
                this.mPendingWakeUpBroadcast = false;
                this.mBroadcastedPowerState = 1;
            } else if (this.mBroadcastedPowerState == 1) {
                if (this.mPendingWakeUpBroadcast || this.mPendingGoToSleepBroadcast || this.mActualPowerState == 2) {
                    this.mPendingGoToSleepBroadcast = false;
                    this.mBroadcastedPowerState = 2;
                } else {
                    finishPendingBroadcastLocked();
                    return;
                }
            } else if (this.mPendingWakeUpBroadcast || this.mPendingGoToSleepBroadcast || this.mActualPowerState == 1) {
                this.mPendingWakeUpBroadcast = false;
                this.mBroadcastedPowerState = 1;
            } else {
                finishPendingBroadcastLocked();
                return;
            }
            this.mBroadcastStartTime = SystemClock.uptimeMillis();
            int powerState = this.mBroadcastedPowerState;
            int goToSleepReason = this.mLastGoToSleepReason;
            EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_BROADCAST_SEND, 1);
            if (powerState == 1) {
                sendWakeUpBroadcast();
            } else {
                sendGoToSleepBroadcast(goToSleepReason);
            }
        }
    }

    private void sendWakeUpBroadcast() {
        EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_STATE, 1, 0, 0, 0);
        this.mPolicy.screenTurningOn(this.mScreenOnListener);
        try {
            ActivityManagerNative.getDefault().wakingUp();
        } catch (RemoteException e) {
        }
        if (ActivityManagerNative.isSystemReady()) {
            this.mContext.sendOrderedBroadcastAsUser(this.mScreenOnIntent, UserHandle.ALL, null, this.mWakeUpBroadcastDone, this.mHandler, 0, null, null);
            return;
        }
        EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_BROADCAST_STOP, 2, 1);
        sendNextBroadcast();
    }

    private void sendGoToSleepBroadcast(int reason) {
        int why = 2;
        switch (reason) {
            case 1:
                why = 1;
                break;
            case 2:
                why = 3;
                break;
        }
        EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_STATE, 0, Integer.valueOf(why), 0, 0);
        this.mPolicy.screenTurnedOff(why);
        try {
            ActivityManagerNative.getDefault().goingToSleep();
        } catch (RemoteException e) {
        }
        if (ActivityManagerNative.isSystemReady()) {
            this.mContext.sendOrderedBroadcastAsUser(this.mScreenOffIntent, UserHandle.ALL, null, this.mGoToSleepBroadcastDone, this.mHandler, 0, null, null);
            return;
        }
        EventLog.writeEvent((int) EventLogTags.POWER_SCREEN_BROADCAST_STOP, 3, 1);
        sendNextBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playWirelessChargingStartedSound() {
        Uri soundUri;
        Ringtone sfx;
        String soundPath = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.WIRELESS_CHARGING_STARTED_SOUND);
        if (soundPath != null && (soundUri = Uri.parse("file://" + soundPath)) != null && (sfx = RingtoneManager.getRingtone(this.mContext, soundUri)) != null) {
            sfx.setStreamType(1);
            sfx.play();
        }
        this.mSuspendBlocker.release();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Notifier$NotifierHandler.class */
    public final class NotifierHandler extends Handler {
        public NotifierHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Notifier.this.sendUserActivity();
                    return;
                case 2:
                    Notifier.this.sendNextBroadcast();
                    return;
                case 3:
                    Notifier.this.playWirelessChargingStartedSound();
                    return;
                default:
                    return;
            }
        }
    }
}