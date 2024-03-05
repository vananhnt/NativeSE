package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;

/* loaded from: IdleMaintenanceService.class */
public class IdleMaintenanceService extends BroadcastReceiver {
    private static final boolean DEBUG = false;
    private static final int LAST_USER_ACTIVITY_TIME_INVALID = -1;
    private static final long MIN_IDLE_MAINTENANCE_INTERVAL_MILLIS = 86400000;
    private static final int MIN_BATTERY_LEVEL_IDLE_MAINTENANCE_START_CHARGING = 30;
    private static final int MIN_BATTERY_LEVEL_IDLE_MAINTENANCE_START_NOT_CHARGING = 80;
    private static final int MIN_BATTERY_LEVEL_IDLE_MAINTENANCE_RUNNING = 20;
    private static final long MIN_USER_INACTIVITY_IDLE_MAINTENANCE_START = 4260000;
    private static final long MAX_IDLE_MAINTENANCE_DURATION = 4260000;
    private static final String ACTION_UPDATE_IDLE_MAINTENANCE_STATE = "com.android.server.IdleMaintenanceService.action.UPDATE_IDLE_MAINTENANCE_STATE";
    private static final String ACTION_FORCE_IDLE_MAINTENANCE = "com.android.server.IdleMaintenanceService.action.FORCE_IDLE_MAINTENANCE";
    private static final Intent sIdleMaintenanceEndIntent;
    private final AlarmManager mAlarmService;
    private final BatteryService mBatteryService;
    private final PendingIntent mUpdateIdleMaintenanceStatePendingIntent;
    private final Context mContext;
    private final PowerManager.WakeLock mWakeLock;
    private final Handler mHandler;
    private long mLastIdleMaintenanceStartTimeMillis;
    private long mLastUserActivityElapsedTimeMillis = -1;
    private boolean mIdleMaintenanceStarted;
    private static final String LOG_TAG = IdleMaintenanceService.class.getSimpleName();
    private static final Intent sIdleMaintenanceStartIntent = new Intent(Intent.ACTION_IDLE_MAINTENANCE_START);

    static {
        sIdleMaintenanceStartIntent.setFlags(1073741824);
        sIdleMaintenanceEndIntent = new Intent(Intent.ACTION_IDLE_MAINTENANCE_END);
        sIdleMaintenanceEndIntent.setFlags(1073741824);
    }

    public IdleMaintenanceService(Context context, BatteryService batteryService) {
        this.mContext = context;
        this.mBatteryService = batteryService;
        this.mAlarmService = (AlarmManager) context.getSystemService("alarm");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = powerManager.newWakeLock(1, LOG_TAG);
        this.mHandler = new Handler(this.mContext.getMainLooper());
        Intent intent = new Intent(ACTION_UPDATE_IDLE_MAINTENANCE_STATE);
        intent.setFlags(1073741824);
        this.mUpdateIdleMaintenanceStatePendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 134217728);
        register(this.mHandler);
    }

    public void register(Handler handler) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_IDLE_MAINTENANCE_STATE);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_DREAMING_STARTED);
        intentFilter.addAction(Intent.ACTION_DREAMING_STOPPED);
        this.mContext.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, null, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(ACTION_FORCE_IDLE_MAINTENANCE);
        this.mContext.registerReceiverAsUser(this, UserHandle.ALL, intentFilter2, Manifest.permission.SET_ACTIVITY_WATCHER, this.mHandler);
    }

    private void scheduleUpdateIdleMaintenanceState(long delayMillis) {
        long triggetRealTimeMillis = SystemClock.elapsedRealtime() + delayMillis;
        this.mAlarmService.set(2, triggetRealTimeMillis, this.mUpdateIdleMaintenanceStatePendingIntent);
    }

    private void unscheduleUpdateIdleMaintenanceState() {
        this.mAlarmService.cancel(this.mUpdateIdleMaintenanceStatePendingIntent);
    }

    private void updateIdleMaintenanceState(boolean noisy) {
        if (this.mIdleMaintenanceStarted) {
            if (!lastUserActivityPermitsIdleMaintenanceRunning() || !batteryLevelAndMaintenanceTimeoutPermitsIdleMaintenanceRunning()) {
                unscheduleUpdateIdleMaintenanceState();
                this.mIdleMaintenanceStarted = false;
                EventLogTags.writeIdleMaintenanceWindowFinish(SystemClock.elapsedRealtime(), this.mLastUserActivityElapsedTimeMillis, this.mBatteryService.getBatteryLevel(), isBatteryCharging() ? 1 : 0);
                sendIdleMaintenanceEndIntent();
                if (!batteryLevelAndMaintenanceTimeoutPermitsIdleMaintenanceRunning()) {
                    scheduleUpdateIdleMaintenanceState(getNextIdleMaintenanceIntervalStartFromNow());
                }
            }
        } else if (deviceStatePermitsIdleMaintenanceStart(noisy) && lastUserActivityPermitsIdleMaintenanceStart(noisy) && lastRunPermitsIdleMaintenanceStart(noisy)) {
            scheduleUpdateIdleMaintenanceState(4260000L);
            this.mIdleMaintenanceStarted = true;
            EventLogTags.writeIdleMaintenanceWindowStart(SystemClock.elapsedRealtime(), this.mLastUserActivityElapsedTimeMillis, this.mBatteryService.getBatteryLevel(), isBatteryCharging() ? 1 : 0);
            this.mLastIdleMaintenanceStartTimeMillis = SystemClock.elapsedRealtime();
            sendIdleMaintenanceStartIntent();
        } else if (lastUserActivityPermitsIdleMaintenanceStart(noisy)) {
            if (lastRunPermitsIdleMaintenanceStart(noisy)) {
                scheduleUpdateIdleMaintenanceState(4260000L);
            } else {
                scheduleUpdateIdleMaintenanceState(getNextIdleMaintenanceIntervalStartFromNow());
            }
        }
    }

    private long getNextIdleMaintenanceIntervalStartFromNow() {
        return (this.mLastIdleMaintenanceStartTimeMillis + 86400000) - SystemClock.elapsedRealtime();
    }

    private void sendIdleMaintenanceStartIntent() {
        this.mWakeLock.acquire();
        try {
            ActivityManagerNative.getDefault().performIdleMaintenance();
        } catch (RemoteException e) {
        }
        this.mContext.sendOrderedBroadcastAsUser(sIdleMaintenanceStartIntent, UserHandle.ALL, null, this, this.mHandler, -1, null, null);
    }

    private void sendIdleMaintenanceEndIntent() {
        this.mWakeLock.acquire();
        this.mContext.sendOrderedBroadcastAsUser(sIdleMaintenanceEndIntent, UserHandle.ALL, null, this, this.mHandler, -1, null, null);
    }

    private boolean deviceStatePermitsIdleMaintenanceStart(boolean noisy) {
        int minBatteryLevel = isBatteryCharging() ? 30 : 80;
        boolean allowed = this.mLastUserActivityElapsedTimeMillis != -1 && this.mBatteryService.getBatteryLevel() > minBatteryLevel;
        if (!allowed && noisy) {
            Slog.i("IdleMaintenance", "Idle maintenance not allowed due to power");
        }
        return allowed;
    }

    private boolean lastUserActivityPermitsIdleMaintenanceStart(boolean noisy) {
        boolean allowed = this.mLastUserActivityElapsedTimeMillis != -1 && SystemClock.elapsedRealtime() - this.mLastUserActivityElapsedTimeMillis > 4260000;
        if (!allowed && noisy) {
            Slog.i("IdleMaintenance", "Idle maintenance not allowed due to last user activity");
        }
        return allowed;
    }

    private boolean lastRunPermitsIdleMaintenanceStart(boolean noisy) {
        boolean allowed = SystemClock.elapsedRealtime() - this.mLastIdleMaintenanceStartTimeMillis > 86400000;
        if (!allowed && noisy) {
            Slog.i("IdleMaintenance", "Idle maintenance not allowed due time since last");
        }
        return allowed;
    }

    private boolean lastUserActivityPermitsIdleMaintenanceRunning() {
        return this.mLastUserActivityElapsedTimeMillis != -1;
    }

    private boolean batteryLevelAndMaintenanceTimeoutPermitsIdleMaintenanceRunning() {
        return this.mBatteryService.getBatteryLevel() > 20 && this.mLastIdleMaintenanceStartTimeMillis + 4260000 > SystemClock.elapsedRealtime();
    }

    private boolean isBatteryCharging() {
        return this.mBatteryService.getPlugType() > 0 && this.mBatteryService.getInvalidCharger() == 0;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            if (this.mIdleMaintenanceStarted) {
                updateIdleMaintenanceState(false);
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_DREAMING_STOPPED.equals(action)) {
            this.mLastUserActivityElapsedTimeMillis = -1L;
            unscheduleUpdateIdleMaintenanceState();
            updateIdleMaintenanceState(false);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_DREAMING_STARTED.equals(action)) {
            this.mLastUserActivityElapsedTimeMillis = SystemClock.elapsedRealtime();
            scheduleUpdateIdleMaintenanceState(4260000L);
        } else if (ACTION_UPDATE_IDLE_MAINTENANCE_STATE.equals(action)) {
            updateIdleMaintenanceState(false);
        } else if (ACTION_FORCE_IDLE_MAINTENANCE.equals(action)) {
            long now = SystemClock.elapsedRealtime() - 1;
            this.mLastUserActivityElapsedTimeMillis = now - 4260000;
            this.mLastIdleMaintenanceStartTimeMillis = now - 86400000;
            updateIdleMaintenanceState(true);
        } else if (Intent.ACTION_IDLE_MAINTENANCE_START.equals(action) || Intent.ACTION_IDLE_MAINTENANCE_END.equals(action)) {
            this.mWakeLock.release();
        }
    }
}