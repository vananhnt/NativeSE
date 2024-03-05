package com.android.server;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.BatteryProperties;
import android.os.Binder;
import android.os.Handler;
import android.os.IBatteryPropertiesListener;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.app.IBatteryStats;
import com.android.server.LightsService;
import com.android.server.am.BatteryStatsService;
import java.io.File;

/* loaded from: BatteryService.class */
public final class BatteryService extends Binder {
    private static final boolean DEBUG = false;
    private static final int BATTERY_SCALE = 100;
    private int mCriticalBatteryLevel;
    private static final int DUMP_MAX_LENGTH = 24576;
    private static final String DUMPSYS_DATA_PATH = "/data/system/";
    private static final int BATTERY_PLUGGED_NONE = 0;
    private final Context mContext;
    private BatteryProperties mBatteryProps;
    private boolean mBatteryLevelCritical;
    private int mLastBatteryStatus;
    private int mLastBatteryHealth;
    private boolean mLastBatteryPresent;
    private int mLastBatteryLevel;
    private int mLastBatteryVoltage;
    private int mLastBatteryTemperature;
    private boolean mLastBatteryLevelCritical;
    private int mInvalidCharger;
    private int mLastInvalidCharger;
    private int mLowBatteryWarningLevel;
    private int mLowBatteryCloseWarningLevel;
    private int mShutdownBatteryTemperature;
    private int mPlugType;
    private long mDischargeStartTime;
    private int mDischargeStartLevel;
    private boolean mUpdatesStopped;
    private Led mLed;
    private BatteryListener mBatteryPropertiesListener;
    private IBatteryPropertiesRegistrar mBatteryPropertiesRegistrar;
    private static final String TAG = BatteryService.class.getSimpleName();
    private static final String[] DUMPSYS_ARGS = {"--checkin", "--unplugged"};
    private final Object mLock = new Object();
    private int mLastPlugType = -1;
    private boolean mSentLowBatteryBroadcast = false;
    private final UEventObserver mInvalidChargerObserver = new UEventObserver() { // from class: com.android.server.BatteryService.8
        @Override // android.os.UEventObserver
        public void onUEvent(UEventObserver.UEvent event) {
            int invalidCharger = "1".equals(event.get("SWITCH_STATE")) ? 1 : 0;
            synchronized (BatteryService.this.mLock) {
                if (BatteryService.this.mInvalidCharger != invalidCharger) {
                    BatteryService.this.mInvalidCharger = invalidCharger;
                }
            }
        }
    };
    private final Handler mHandler = new Handler(true);
    private final IBatteryStats mBatteryStats = BatteryStatsService.getService();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BatteryService.logBatteryStatsLocked():void, file: BatteryService.class
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
    private void logBatteryStatsLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BatteryService.logBatteryStatsLocked():void, file: BatteryService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BatteryService.logBatteryStatsLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BatteryService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: BatteryService.class
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
    @Override // android.os.Binder
    protected void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BatteryService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: BatteryService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BatteryService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    public BatteryService(Context context, LightsService lights) {
        this.mContext = context;
        this.mLed = new Led(context, lights);
        this.mCriticalBatteryLevel = this.mContext.getResources().getInteger(R.integer.config_criticalBatteryWarningLevel);
        this.mLowBatteryWarningLevel = this.mContext.getResources().getInteger(R.integer.config_lowBatteryWarningLevel);
        this.mLowBatteryCloseWarningLevel = this.mContext.getResources().getInteger(R.integer.config_lowBatteryCloseWarningLevel);
        this.mShutdownBatteryTemperature = this.mContext.getResources().getInteger(R.integer.config_shutdownBatteryTemperature);
        if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
            this.mInvalidChargerObserver.startObserving("DEVPATH=/devices/virtual/switch/invalid_charger");
        }
        this.mBatteryPropertiesListener = new BatteryListener();
        IBinder b = ServiceManager.getService("batterypropreg");
        this.mBatteryPropertiesRegistrar = IBatteryPropertiesRegistrar.Stub.asInterface(b);
        try {
            this.mBatteryPropertiesRegistrar.registerListener(this.mBatteryPropertiesListener);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        synchronized (this.mLock) {
            shutdownIfNoPowerLocked();
            shutdownIfOverTempLocked();
        }
    }

    public boolean isPowered(int plugTypeSet) {
        boolean isPoweredLocked;
        synchronized (this.mLock) {
            isPoweredLocked = isPoweredLocked(plugTypeSet);
        }
        return isPoweredLocked;
    }

    private boolean isPoweredLocked(int plugTypeSet) {
        if (this.mBatteryProps.batteryStatus == 1) {
            return true;
        }
        if ((plugTypeSet & 1) != 0 && this.mBatteryProps.chargerAcOnline) {
            return true;
        }
        if ((plugTypeSet & 2) != 0 && this.mBatteryProps.chargerUsbOnline) {
            return true;
        }
        if ((plugTypeSet & 4) != 0 && this.mBatteryProps.chargerWirelessOnline) {
            return true;
        }
        return false;
    }

    public int getPlugType() {
        int i;
        synchronized (this.mLock) {
            i = this.mPlugType;
        }
        return i;
    }

    public int getBatteryLevel() {
        int i;
        synchronized (this.mLock) {
            i = this.mBatteryProps.batteryLevel;
        }
        return i;
    }

    public boolean isBatteryLow() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mBatteryProps.batteryPresent && this.mBatteryProps.batteryLevel <= this.mLowBatteryWarningLevel;
        }
        return z;
    }

    public int getInvalidCharger() {
        int i;
        synchronized (this.mLock) {
            i = this.mInvalidCharger;
        }
        return i;
    }

    private void shutdownIfNoPowerLocked() {
        if (this.mBatteryProps.batteryLevel == 0 && !isPoweredLocked(7)) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ActivityManagerNative.isSystemReady()) {
                        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
                        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
                        intent.setFlags(268435456);
                        BatteryService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                    }
                }
            });
        }
    }

    private void shutdownIfOverTempLocked() {
        if (this.mBatteryProps.batteryTemperature > this.mShutdownBatteryTemperature) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.2
                @Override // java.lang.Runnable
                public void run() {
                    if (ActivityManagerNative.isSystemReady()) {
                        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
                        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
                        intent.setFlags(268435456);
                        BatteryService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update(BatteryProperties props) {
        synchronized (this.mLock) {
            if (!this.mUpdatesStopped) {
                this.mBatteryProps = props;
                processValuesLocked();
            }
        }
    }

    private void processValuesLocked() {
        boolean logOutlier = false;
        long dischargeDuration = 0;
        this.mBatteryLevelCritical = this.mBatteryProps.batteryLevel <= this.mCriticalBatteryLevel;
        if (this.mBatteryProps.chargerAcOnline) {
            this.mPlugType = 1;
        } else if (this.mBatteryProps.chargerUsbOnline) {
            this.mPlugType = 2;
        } else if (this.mBatteryProps.chargerWirelessOnline) {
            this.mPlugType = 4;
        } else {
            this.mPlugType = 0;
        }
        try {
            this.mBatteryStats.setBatteryState(this.mBatteryProps.batteryStatus, this.mBatteryProps.batteryHealth, this.mPlugType, this.mBatteryProps.batteryLevel, this.mBatteryProps.batteryTemperature, this.mBatteryProps.batteryVoltage);
        } catch (RemoteException e) {
        }
        shutdownIfNoPowerLocked();
        shutdownIfOverTempLocked();
        if (this.mBatteryProps.batteryStatus != this.mLastBatteryStatus || this.mBatteryProps.batteryHealth != this.mLastBatteryHealth || this.mBatteryProps.batteryPresent != this.mLastBatteryPresent || this.mBatteryProps.batteryLevel != this.mLastBatteryLevel || this.mPlugType != this.mLastPlugType || this.mBatteryProps.batteryVoltage != this.mLastBatteryVoltage || this.mBatteryProps.batteryTemperature != this.mLastBatteryTemperature || this.mInvalidCharger != this.mLastInvalidCharger) {
            if (this.mPlugType != this.mLastPlugType) {
                if (this.mLastPlugType == 0) {
                    if (this.mDischargeStartTime != 0 && this.mDischargeStartLevel != this.mBatteryProps.batteryLevel) {
                        dischargeDuration = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
                        logOutlier = true;
                        EventLog.writeEvent((int) EventLogTags.BATTERY_DISCHARGE, Long.valueOf(dischargeDuration), Integer.valueOf(this.mDischargeStartLevel), Integer.valueOf(this.mBatteryProps.batteryLevel));
                        this.mDischargeStartTime = 0L;
                    }
                } else if (this.mPlugType == 0) {
                    this.mDischargeStartTime = SystemClock.elapsedRealtime();
                    this.mDischargeStartLevel = this.mBatteryProps.batteryLevel;
                }
            }
            if (this.mBatteryProps.batteryStatus != this.mLastBatteryStatus || this.mBatteryProps.batteryHealth != this.mLastBatteryHealth || this.mBatteryProps.batteryPresent != this.mLastBatteryPresent || this.mPlugType != this.mLastPlugType) {
                Object[] objArr = new Object[5];
                objArr[0] = Integer.valueOf(this.mBatteryProps.batteryStatus);
                objArr[1] = Integer.valueOf(this.mBatteryProps.batteryHealth);
                objArr[2] = Integer.valueOf(this.mBatteryProps.batteryPresent ? 1 : 0);
                objArr[3] = Integer.valueOf(this.mPlugType);
                objArr[4] = this.mBatteryProps.batteryTechnology;
                EventLog.writeEvent((int) EventLogTags.BATTERY_STATUS, objArr);
            }
            if (this.mBatteryProps.batteryLevel != this.mLastBatteryLevel) {
                EventLog.writeEvent((int) EventLogTags.BATTERY_LEVEL, Integer.valueOf(this.mBatteryProps.batteryLevel), Integer.valueOf(this.mBatteryProps.batteryVoltage), Integer.valueOf(this.mBatteryProps.batteryTemperature));
            }
            if (this.mBatteryLevelCritical && !this.mLastBatteryLevelCritical && this.mPlugType == 0) {
                dischargeDuration = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
                logOutlier = true;
            }
            boolean plugged = this.mPlugType != 0;
            boolean oldPlugged = this.mLastPlugType != 0;
            boolean sendBatteryLow = !plugged && this.mBatteryProps.batteryStatus != 1 && this.mBatteryProps.batteryLevel <= this.mLowBatteryWarningLevel && (oldPlugged || this.mLastBatteryLevel > this.mLowBatteryWarningLevel);
            sendIntentLocked();
            if (this.mPlugType != 0 && this.mLastPlugType == 0) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.3
                    @Override // java.lang.Runnable
                    public void run() {
                        Intent statusIntent = new Intent(Intent.ACTION_POWER_CONNECTED);
                        statusIntent.setFlags(67108864);
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
                    }
                });
            } else if (this.mPlugType == 0 && this.mLastPlugType != 0) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.4
                    @Override // java.lang.Runnable
                    public void run() {
                        Intent statusIntent = new Intent(Intent.ACTION_POWER_DISCONNECTED);
                        statusIntent.setFlags(67108864);
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
                    }
                });
            }
            if (sendBatteryLow) {
                this.mSentLowBatteryBroadcast = true;
                this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.5
                    @Override // java.lang.Runnable
                    public void run() {
                        Intent statusIntent = new Intent(Intent.ACTION_BATTERY_LOW);
                        statusIntent.setFlags(67108864);
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
                    }
                });
            } else if (this.mSentLowBatteryBroadcast && this.mLastBatteryLevel >= this.mLowBatteryCloseWarningLevel) {
                this.mSentLowBatteryBroadcast = false;
                this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.6
                    @Override // java.lang.Runnable
                    public void run() {
                        Intent statusIntent = new Intent(Intent.ACTION_BATTERY_OKAY);
                        statusIntent.setFlags(67108864);
                        BatteryService.this.mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
                    }
                });
            }
            this.mLed.updateLightsLocked();
            if (logOutlier && dischargeDuration != 0) {
                logOutlierLocked(dischargeDuration);
            }
            this.mLastBatteryStatus = this.mBatteryProps.batteryStatus;
            this.mLastBatteryHealth = this.mBatteryProps.batteryHealth;
            this.mLastBatteryPresent = this.mBatteryProps.batteryPresent;
            this.mLastBatteryLevel = this.mBatteryProps.batteryLevel;
            this.mLastPlugType = this.mPlugType;
            this.mLastBatteryVoltage = this.mBatteryProps.batteryVoltage;
            this.mLastBatteryTemperature = this.mBatteryProps.batteryTemperature;
            this.mLastBatteryLevelCritical = this.mBatteryLevelCritical;
            this.mLastInvalidCharger = this.mInvalidCharger;
        }
    }

    private void sendIntentLocked() {
        final Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
        intent.addFlags(1610612736);
        int icon = getIconLocked(this.mBatteryProps.batteryLevel);
        intent.putExtra("status", this.mBatteryProps.batteryStatus);
        intent.putExtra(BatteryManager.EXTRA_HEALTH, this.mBatteryProps.batteryHealth);
        intent.putExtra(BatteryManager.EXTRA_PRESENT, this.mBatteryProps.batteryPresent);
        intent.putExtra(BatteryManager.EXTRA_LEVEL, this.mBatteryProps.batteryLevel);
        intent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        intent.putExtra(BatteryManager.EXTRA_ICON_SMALL, icon);
        intent.putExtra(BatteryManager.EXTRA_PLUGGED, this.mPlugType);
        intent.putExtra(BatteryManager.EXTRA_VOLTAGE, this.mBatteryProps.batteryVoltage);
        intent.putExtra(BatteryManager.EXTRA_TEMPERATURE, this.mBatteryProps.batteryTemperature);
        intent.putExtra(BatteryManager.EXTRA_TECHNOLOGY, this.mBatteryProps.batteryTechnology);
        intent.putExtra(BatteryManager.EXTRA_INVALID_CHARGER, this.mInvalidCharger);
        this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.7
            @Override // java.lang.Runnable
            public void run() {
                ActivityManagerNative.broadcastStickyIntent(intent, null, -1);
            }
        });
    }

    private void logOutlierLocked(long duration) {
        ContentResolver cr = this.mContext.getContentResolver();
        String dischargeThresholdString = Settings.Global.getString(cr, Settings.Global.BATTERY_DISCHARGE_THRESHOLD);
        String durationThresholdString = Settings.Global.getString(cr, Settings.Global.BATTERY_DISCHARGE_DURATION_THRESHOLD);
        if (dischargeThresholdString != null && durationThresholdString != null) {
            try {
                long durationThreshold = Long.parseLong(durationThresholdString);
                int dischargeThreshold = Integer.parseInt(dischargeThresholdString);
                if (duration <= durationThreshold && this.mDischargeStartLevel - this.mBatteryProps.batteryLevel >= dischargeThreshold) {
                    logBatteryStatsLocked();
                }
            } catch (NumberFormatException e) {
                Slog.e(TAG, "Invalid DischargeThresholds GService string: " + durationThresholdString + " or " + dischargeThresholdString);
            }
        }
    }

    private int getIconLocked(int level) {
        if (this.mBatteryProps.batteryStatus == 2) {
            return R.drawable.stat_sys_battery_charge;
        }
        if (this.mBatteryProps.batteryStatus == 3) {
            return R.drawable.stat_sys_battery;
        }
        if (this.mBatteryProps.batteryStatus == 4 || this.mBatteryProps.batteryStatus == 5) {
            if (isPoweredLocked(7) && this.mBatteryProps.batteryLevel >= 100) {
                return R.drawable.stat_sys_battery_charge;
            }
            return R.drawable.stat_sys_battery;
        }
        return R.drawable.stat_sys_battery_unknown;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BatteryService$Led.class */
    public final class Led {
        private final LightsService.Light mBatteryLight;
        private final int mBatteryLowARGB;
        private final int mBatteryMediumARGB;
        private final int mBatteryFullARGB;
        private final int mBatteryLedOn;
        private final int mBatteryLedOff;

        public Led(Context context, LightsService lights) {
            this.mBatteryLight = lights.getLight(3);
            this.mBatteryLowARGB = context.getResources().getInteger(R.integer.config_notificationsBatteryLowARGB);
            this.mBatteryMediumARGB = context.getResources().getInteger(R.integer.config_notificationsBatteryMediumARGB);
            this.mBatteryFullARGB = context.getResources().getInteger(R.integer.config_notificationsBatteryFullARGB);
            this.mBatteryLedOn = context.getResources().getInteger(R.integer.config_notificationsBatteryLedOn);
            this.mBatteryLedOff = context.getResources().getInteger(R.integer.config_notificationsBatteryLedOff);
        }

        public void updateLightsLocked() {
            int level = BatteryService.this.mBatteryProps.batteryLevel;
            int status = BatteryService.this.mBatteryProps.batteryStatus;
            if (level < BatteryService.this.mLowBatteryWarningLevel) {
                if (status == 2) {
                    this.mBatteryLight.setColor(this.mBatteryLowARGB);
                } else {
                    this.mBatteryLight.setFlashing(this.mBatteryLowARGB, 1, this.mBatteryLedOn, this.mBatteryLedOff);
                }
            } else if (status == 2 || status == 5) {
                if (status == 5 || level >= 90) {
                    this.mBatteryLight.setColor(this.mBatteryFullARGB);
                } else {
                    this.mBatteryLight.setColor(this.mBatteryMediumARGB);
                }
            } else {
                this.mBatteryLight.turnOff();
            }
        }
    }

    /* loaded from: BatteryService$BatteryListener.class */
    private final class BatteryListener extends IBatteryPropertiesListener.Stub {
        private BatteryListener() {
        }

        @Override // android.os.IBatteryPropertiesListener
        public void batteryPropertiesChanged(BatteryProperties props) {
            BatteryService.this.update(props);
        }
    }
}