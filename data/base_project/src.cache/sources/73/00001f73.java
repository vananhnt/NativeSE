package com.android.server.power;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.media.videoeditor.MediaProperties;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemService;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.WindowManagerPolicy;
import com.android.internal.R;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.RILConstants;
import com.android.server.BatteryService;
import com.android.server.EventLogTags;
import com.android.server.LightsService;
import com.android.server.TwilightService;
import com.android.server.Watchdog;
import com.android.server.am.ActivityManagerService;
import com.android.server.display.DisplayManagerService;
import com.android.server.dreams.DreamManagerService;
import com.android.server.power.DisplayPowerController;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import libcore.util.Objects;

/* loaded from: PowerManagerService.class */
public final class PowerManagerService extends IPowerManager.Stub implements Watchdog.Monitor {
    private static final String TAG = "PowerManagerService";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_SPEW = false;
    private static final int MSG_USER_ACTIVITY_TIMEOUT = 1;
    private static final int MSG_SANDMAN = 2;
    private static final int MSG_SCREEN_ON_BLOCKER_RELEASED = 3;
    private static final int MSG_CHECK_IF_BOOT_ANIMATION_FINISHED = 4;
    private static final int DIRTY_WAKE_LOCKS = 1;
    private static final int DIRTY_WAKEFULNESS = 2;
    private static final int DIRTY_USER_ACTIVITY = 4;
    private static final int DIRTY_ACTUAL_DISPLAY_POWER_STATE_UPDATED = 8;
    private static final int DIRTY_BOOT_COMPLETED = 16;
    private static final int DIRTY_SETTINGS = 32;
    private static final int DIRTY_IS_POWERED = 64;
    private static final int DIRTY_STAY_ON = 128;
    private static final int DIRTY_BATTERY_STATE = 256;
    private static final int DIRTY_PROXIMITY_POSITIVE = 512;
    private static final int DIRTY_SCREEN_ON_BLOCKER_RELEASED = 1024;
    private static final int DIRTY_DOCK_STATE = 2048;
    private static final int WAKEFULNESS_ASLEEP = 0;
    private static final int WAKEFULNESS_AWAKE = 1;
    private static final int WAKEFULNESS_NAPPING = 2;
    private static final int WAKEFULNESS_DREAMING = 3;
    private static final int WAKE_LOCK_CPU = 1;
    private static final int WAKE_LOCK_SCREEN_BRIGHT = 2;
    private static final int WAKE_LOCK_SCREEN_DIM = 4;
    private static final int WAKE_LOCK_BUTTON_BRIGHT = 8;
    private static final int WAKE_LOCK_PROXIMITY_SCREEN_OFF = 16;
    private static final int WAKE_LOCK_STAY_AWAKE = 32;
    private static final int USER_ACTIVITY_SCREEN_BRIGHT = 1;
    private static final int USER_ACTIVITY_SCREEN_DIM = 2;
    private static final int DEFAULT_SCREEN_OFF_TIMEOUT = 15000;
    private static final int MINIMUM_SCREEN_OFF_TIMEOUT = 10000;
    private static final int SCREEN_DIM_DURATION = 7000;
    private static final float MAXIMUM_SCREEN_DIM_RATIO = 0.2f;
    private static final String BOOT_ANIMATION_SERVICE = "bootanim";
    private static final int BOOT_ANIMATION_POLL_INTERVAL = 200;
    private static final int DREAM_BATTERY_LEVEL_DRAIN_CUTOFF = 5;
    private Context mContext;
    private LightsService mLightsService;
    private BatteryService mBatteryService;
    private DisplayManagerService mDisplayManagerService;
    private IBatteryStats mBatteryStats;
    private IAppOpsService mAppOps;
    private HandlerThread mHandlerThread;
    private PowerManagerHandler mHandler;
    private WindowManagerPolicy mPolicy;
    private Notifier mNotifier;
    private DisplayPowerController mDisplayPowerController;
    private WirelessChargerDetector mWirelessChargerDetector;
    private SettingsObserver mSettingsObserver;
    private DreamManagerService mDreamManager;
    private LightsService.Light mAttentionLight;
    private int mDirty;
    private int mWakefulness;
    private boolean mSandmanScheduled;
    private int mWakeLockSummary;
    private boolean mRequestWaitForNegativeProximity;
    private long mLastWakeTime;
    private long mLastSleepTime;
    private boolean mSendWakeUpFinishedNotificationWhenReady;
    private boolean mSendGoToSleepFinishedNotificationWhenReady;
    private long mLastUserActivityTime;
    private long mLastUserActivityTimeNoChangeLights;
    private int mUserActivitySummary;
    private long mLastScreenOffEventElapsedRealTime;
    private boolean mDisplayReady;
    private final SuspendBlocker mWakeLockSuspendBlocker;
    private boolean mHoldingWakeLockSuspendBlocker;
    private final SuspendBlocker mDisplaySuspendBlocker;
    private boolean mHoldingDisplaySuspendBlocker;
    private final ScreenOnBlockerImpl mScreenOnBlocker;
    private final DisplayBlankerImpl mDisplayBlanker;
    private boolean mSystemReady;
    private boolean mBootCompleted;
    private boolean mIsPowered;
    private int mPlugType;
    private int mBatteryLevel;
    private int mBatteryLevelWhenDreamStarted;
    private boolean mWakeUpWhenPluggedOrUnpluggedConfig;
    private boolean mSuspendWhenScreenOffDueToProximityConfig;
    private boolean mDreamsSupportedConfig;
    private boolean mDreamsEnabledByDefaultConfig;
    private boolean mDreamsActivatedOnSleepByDefaultConfig;
    private boolean mDreamsActivatedOnDockByDefaultConfig;
    private boolean mDreamsEnabledSetting;
    private boolean mDreamsActivateOnSleepSetting;
    private boolean mDreamsActivateOnDockSetting;
    private int mScreenOffTimeoutSetting;
    private int mStayOnWhilePluggedInSetting;
    private boolean mStayOn;
    private boolean mProximityPositive;
    private int mScreenBrightnessSettingMinimum;
    private int mScreenBrightnessSettingMaximum;
    private int mScreenBrightnessSettingDefault;
    private int mScreenBrightnessSetting;
    private float mScreenAutoBrightnessAdjustmentSetting;
    private int mScreenBrightnessModeSetting;
    private final Object mLock = new Object();
    private final ArrayList<SuspendBlocker> mSuspendBlockers = new ArrayList<>();
    private final ArrayList<WakeLock> mWakeLocks = new ArrayList<>();
    private final DisplayPowerRequest mDisplayPowerRequest = new DisplayPowerRequest();
    private int mDockState = 0;
    private int mMaximumScreenOffTimeoutFromDeviceAdmin = Integer.MAX_VALUE;
    private int mScreenBrightnessOverrideFromWindowManager = -1;
    private long mUserActivityTimeoutOverrideFromWindowManager = -1;
    private int mTemporaryScreenBrightnessSettingOverride = -1;
    private float mTemporaryScreenAutoBrightnessAdjustmentSettingOverride = Float.NaN;
    private long mLastWarningAboutUserActivityPermission = Long.MIN_VALUE;
    private final DisplayPowerController.Callbacks mDisplayPowerControllerCallbacks = new DisplayPowerController.Callbacks() { // from class: com.android.server.power.PowerManagerService.1
        @Override // com.android.server.power.DisplayPowerController.Callbacks
        public void onStateChanged() {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.access$876(PowerManagerService.this, 8);
                PowerManagerService.this.updatePowerStateLocked();
            }
        }

        @Override // com.android.server.power.DisplayPowerController.Callbacks
        public void onProximityPositive() {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.mProximityPositive = true;
                PowerManagerService.access$876(PowerManagerService.this, 512);
                PowerManagerService.this.updatePowerStateLocked();
            }
        }

        @Override // com.android.server.power.DisplayPowerController.Callbacks
        public void onProximityNegative() {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.mProximityPositive = false;
                PowerManagerService.access$876(PowerManagerService.this, 512);
                PowerManagerService.this.userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 0, 0, 1000);
                PowerManagerService.this.updatePowerStateLocked();
            }
        }
    };

    private native void nativeInit();

    private static native void nativeSetPowerState(boolean z, boolean z2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeAcquireSuspendBlocker(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeReleaseSuspendBlocker(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetInteractive(boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetAutoSuspend(boolean z);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.acquireWakeLock(android.os.IBinder, int, java.lang.String, java.lang.String, android.os.WorkSource):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void acquireWakeLock(android.os.IBinder r1, int r2, java.lang.String r3, java.lang.String r4, android.os.WorkSource r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.acquireWakeLock(android.os.IBinder, int, java.lang.String, java.lang.String, android.os.WorkSource):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.acquireWakeLock(android.os.IBinder, int, java.lang.String, java.lang.String, android.os.WorkSource):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.releaseWakeLock(android.os.IBinder, int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void releaseWakeLock(android.os.IBinder r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.releaseWakeLock(android.os.IBinder, int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.releaseWakeLock(android.os.IBinder, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.updateWakeLockWorkSource(android.os.IBinder, android.os.WorkSource):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void updateWakeLockWorkSource(android.os.IBinder r1, android.os.WorkSource r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.updateWakeLockWorkSource(android.os.IBinder, android.os.WorkSource):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.updateWakeLockWorkSource(android.os.IBinder, android.os.WorkSource):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.isWakeLockLevelSupported(int):boolean, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public boolean isWakeLockLevelSupported(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.isWakeLockLevelSupported(int):boolean, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.isWakeLockLevelSupported(int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.userActivity(long, int, int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void userActivity(long r1, int r3, int r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.userActivity(long, int, int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.userActivity(long, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.wakeUp(long):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void wakeUp(long r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.wakeUp(long):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.wakeUp(long):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.goToSleep(long, int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void goToSleep(long r1, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.goToSleep(long, int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.goToSleep(long, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.nap(long):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void nap(long r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.nap(long):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.nap(long):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.isScreenOn():boolean, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public boolean isScreenOn() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.isScreenOn():boolean, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.isScreenOn():boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.reboot(boolean, java.lang.String, boolean):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void reboot(boolean r1, java.lang.String r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.reboot(boolean, java.lang.String, boolean):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.reboot(boolean, java.lang.String, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.shutdown(boolean, boolean):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void shutdown(boolean r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.shutdown(boolean, boolean):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.shutdown(boolean, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.crash(java.lang.String):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void crash(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.crash(java.lang.String):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.crash(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setStayOnSetting(int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void setStayOnSetting(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setStayOnSetting(int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setStayOnSetting(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setMaximumScreenOffTimeoutFromDeviceAdmin(int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void setMaximumScreenOffTimeoutFromDeviceAdmin(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setMaximumScreenOffTimeoutFromDeviceAdmin(int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setMaximumScreenOffTimeoutFromDeviceAdmin(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setAttentionLight(boolean, int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void setAttentionLight(boolean r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setAttentionLight(boolean, int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setAttentionLight(boolean, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setScreenBrightnessOverrideFromWindowManager(int):void, file: PowerManagerService.class
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
    public void setScreenBrightnessOverrideFromWindowManager(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setScreenBrightnessOverrideFromWindowManager(int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setScreenBrightnessOverrideFromWindowManager(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setUserActivityTimeoutOverrideFromWindowManager(long):void, file: PowerManagerService.class
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
    public void setUserActivityTimeoutOverrideFromWindowManager(long r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setUserActivityTimeoutOverrideFromWindowManager(long):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setUserActivityTimeoutOverrideFromWindowManager(long):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setTemporaryScreenBrightnessSettingOverride(int):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void setTemporaryScreenBrightnessSettingOverride(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setTemporaryScreenBrightnessSettingOverride(int):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setTemporaryScreenBrightnessSettingOverride(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float):void, file: PowerManagerService.class
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
    @Override // android.os.IPowerManager
    public void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float):void, file: PowerManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float):void");
    }

    static /* synthetic */ int access$876(PowerManagerService x0, int x1) {
        int i = x0.mDirty | x1;
        x0.mDirty = i;
        return i;
    }

    static /* synthetic */ void access$2400(String x0) {
        nativeReleaseSuspendBlocker(x0);
    }

    public PowerManagerService() {
        synchronized (this.mLock) {
            this.mWakeLockSuspendBlocker = createSuspendBlockerLocked("PowerManagerService.WakeLocks");
            this.mDisplaySuspendBlocker = createSuspendBlockerLocked("PowerManagerService.Display");
            this.mDisplaySuspendBlocker.acquire();
            this.mHoldingDisplaySuspendBlocker = true;
            this.mScreenOnBlocker = new ScreenOnBlockerImpl();
            this.mDisplayBlanker = new DisplayBlankerImpl();
            this.mWakefulness = 1;
        }
        nativeInit();
        nativeSetPowerState(true, true);
    }

    public void init(Context context, LightsService ls, ActivityManagerService am, BatteryService bs, IBatteryStats bss, IAppOpsService appOps, DisplayManagerService dm) {
        this.mContext = context;
        this.mLightsService = ls;
        this.mBatteryService = bs;
        this.mBatteryStats = bss;
        this.mAppOps = appOps;
        this.mDisplayManagerService = dm;
        this.mHandlerThread = new HandlerThread(TAG);
        this.mHandlerThread.start();
        this.mHandler = new PowerManagerHandler(this.mHandlerThread.getLooper());
        Watchdog.getInstance().addMonitor(this);
        Watchdog.getInstance().addThread(this.mHandler, this.mHandlerThread.getName());
        this.mDisplayBlanker.unblankAllDisplays();
    }

    public void setPolicy(WindowManagerPolicy policy) {
        synchronized (this.mLock) {
            this.mPolicy = policy;
        }
    }

    public void systemReady(TwilightService twilight, DreamManagerService dreamManager) {
        synchronized (this.mLock) {
            this.mSystemReady = true;
            this.mDreamManager = dreamManager;
            PowerManager pm = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
            this.mScreenBrightnessSettingMinimum = pm.getMinimumScreenBrightnessSetting();
            this.mScreenBrightnessSettingMaximum = pm.getMaximumScreenBrightnessSetting();
            this.mScreenBrightnessSettingDefault = pm.getDefaultScreenBrightnessSetting();
            SensorManager sensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
            this.mNotifier = new Notifier(Looper.getMainLooper(), this.mContext, this.mBatteryStats, this.mAppOps, createSuspendBlockerLocked("PowerManagerService.Broadcasts"), this.mScreenOnBlocker, this.mPolicy);
            this.mDisplayPowerController = new DisplayPowerController(this.mHandler.getLooper(), this.mContext, this.mNotifier, this.mLightsService, twilight, sensorManager, this.mDisplayManagerService, this.mDisplaySuspendBlocker, this.mDisplayBlanker, this.mDisplayPowerControllerCallbacks, this.mHandler);
            this.mWirelessChargerDetector = new WirelessChargerDetector(sensorManager, createSuspendBlockerLocked("PowerManagerService.WirelessChargerDetector"), this.mHandler);
            this.mSettingsObserver = new SettingsObserver(this.mHandler);
            this.mAttentionLight = this.mLightsService.getLight(5);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            this.mContext.registerReceiver(new BatteryReceiver(), filter, null, this.mHandler);
            IntentFilter filter2 = new IntentFilter();
            filter2.addAction(Intent.ACTION_BOOT_COMPLETED);
            this.mContext.registerReceiver(new BootCompletedReceiver(), filter2, null, this.mHandler);
            IntentFilter filter3 = new IntentFilter();
            filter3.addAction(Intent.ACTION_DREAMING_STARTED);
            filter3.addAction(Intent.ACTION_DREAMING_STOPPED);
            this.mContext.registerReceiver(new DreamReceiver(), filter3, null, this.mHandler);
            IntentFilter filter4 = new IntentFilter();
            filter4.addAction(Intent.ACTION_USER_SWITCHED);
            this.mContext.registerReceiver(new UserSwitchedReceiver(), filter4, null, this.mHandler);
            IntentFilter filter5 = new IntentFilter();
            filter5.addAction(Intent.ACTION_DOCK_EVENT);
            this.mContext.registerReceiver(new DockReceiver(), filter5, null, this.mHandler);
            ContentResolver resolver = this.mContext.getContentResolver();
            resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.SCREENSAVER_ENABLED), false, this.mSettingsObserver, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.SCREENSAVER_ACTIVATE_ON_SLEEP), false, this.mSettingsObserver, -1);
            resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.SCREENSAVER_ACTIVATE_ON_DOCK), false, this.mSettingsObserver, -1);
            resolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT), false, this.mSettingsObserver, -1);
            resolver.registerContentObserver(Settings.Global.getUriFor("stay_on_while_plugged_in"), false, this.mSettingsObserver, -1);
            resolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, this.mSettingsObserver, -1);
            resolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, this.mSettingsObserver, -1);
            readConfigurationLocked();
            updateSettingsLocked();
            this.mDirty |= 256;
            updatePowerStateLocked();
        }
    }

    private void readConfigurationLocked() {
        Resources resources = this.mContext.getResources();
        this.mWakeUpWhenPluggedOrUnpluggedConfig = resources.getBoolean(R.bool.config_unplugTurnsOnScreen);
        this.mSuspendWhenScreenOffDueToProximityConfig = resources.getBoolean(R.bool.config_suspendWhenScreenOffDueToProximity);
        this.mDreamsSupportedConfig = resources.getBoolean(R.bool.config_dreamsSupported);
        this.mDreamsEnabledByDefaultConfig = resources.getBoolean(R.bool.config_dreamsEnabledByDefault);
        this.mDreamsActivatedOnSleepByDefaultConfig = resources.getBoolean(R.bool.config_dreamsActivatedOnSleepByDefault);
        this.mDreamsActivatedOnDockByDefaultConfig = resources.getBoolean(R.bool.config_dreamsActivatedOnDockByDefault);
    }

    private void updateSettingsLocked() {
        ContentResolver resolver = this.mContext.getContentResolver();
        this.mDreamsEnabledSetting = Settings.Secure.getIntForUser(resolver, Settings.Secure.SCREENSAVER_ENABLED, this.mDreamsEnabledByDefaultConfig ? 1 : 0, -2) != 0;
        this.mDreamsActivateOnSleepSetting = Settings.Secure.getIntForUser(resolver, Settings.Secure.SCREENSAVER_ACTIVATE_ON_SLEEP, this.mDreamsActivatedOnSleepByDefaultConfig ? 1 : 0, -2) != 0;
        this.mDreamsActivateOnDockSetting = Settings.Secure.getIntForUser(resolver, Settings.Secure.SCREENSAVER_ACTIVATE_ON_DOCK, this.mDreamsActivatedOnDockByDefaultConfig ? 1 : 0, -2) != 0;
        this.mScreenOffTimeoutSetting = Settings.System.getIntForUser(resolver, Settings.System.SCREEN_OFF_TIMEOUT, 15000, -2);
        this.mStayOnWhilePluggedInSetting = Settings.Global.getInt(resolver, "stay_on_while_plugged_in", 1);
        int oldScreenBrightnessSetting = this.mScreenBrightnessSetting;
        this.mScreenBrightnessSetting = Settings.System.getIntForUser(resolver, Settings.System.SCREEN_BRIGHTNESS, this.mScreenBrightnessSettingDefault, -2);
        if (oldScreenBrightnessSetting != this.mScreenBrightnessSetting) {
            this.mTemporaryScreenBrightnessSettingOverride = -1;
        }
        float oldScreenAutoBrightnessAdjustmentSetting = this.mScreenAutoBrightnessAdjustmentSetting;
        this.mScreenAutoBrightnessAdjustmentSetting = Settings.System.getFloatForUser(resolver, Settings.System.SCREEN_AUTO_BRIGHTNESS_ADJ, 0.0f, -2);
        if (oldScreenAutoBrightnessAdjustmentSetting != this.mScreenAutoBrightnessAdjustmentSetting) {
            this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride = Float.NaN;
        }
        this.mScreenBrightnessModeSetting = Settings.System.getIntForUser(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, 0, -2);
        this.mDirty |= 32;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSettingsChangedLocked() {
        updateSettingsLocked();
        updatePowerStateLocked();
    }

    @Override // android.os.IPowerManager
    public void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName, int uid) {
        acquireWakeLock(lock, flags, tag, packageName, new WorkSource(uid));
    }

    private void acquireWakeLockInternal(IBinder lock, int flags, String tag, String packageName, WorkSource ws, int uid, int pid) {
        WakeLock wakeLock;
        synchronized (this.mLock) {
            int index = findWakeLockIndexLocked(lock);
            if (index >= 0) {
                wakeLock = this.mWakeLocks.get(index);
                if (!wakeLock.hasSameProperties(flags, tag, ws, uid, pid)) {
                    notifyWakeLockReleasedLocked(wakeLock);
                    wakeLock.updateProperties(flags, tag, packageName, ws, uid, pid);
                    notifyWakeLockAcquiredLocked(wakeLock);
                }
            } else {
                wakeLock = new WakeLock(lock, flags, tag, packageName, ws, uid, pid);
                try {
                    lock.linkToDeath(wakeLock, 0);
                    notifyWakeLockAcquiredLocked(wakeLock);
                    this.mWakeLocks.add(wakeLock);
                } catch (RemoteException e) {
                    throw new IllegalArgumentException("Wake lock is already dead.");
                }
            }
            applyWakeLockFlagsOnAcquireLocked(wakeLock);
            this.mDirty |= 1;
            updatePowerStateLocked();
        }
    }

    private static boolean isScreenLock(WakeLock wakeLock) {
        switch (wakeLock.mFlags & 65535) {
            case 6:
            case 10:
            case 26:
                return true;
            default:
                return false;
        }
    }

    private void applyWakeLockFlagsOnAcquireLocked(WakeLock wakeLock) {
        if ((wakeLock.mFlags & 268435456) != 0 && isScreenLock(wakeLock)) {
            wakeUpNoUpdateLocked(SystemClock.uptimeMillis());
        }
    }

    private void releaseWakeLockInternal(IBinder lock, int flags) {
        synchronized (this.mLock) {
            int index = findWakeLockIndexLocked(lock);
            if (index < 0) {
                return;
            }
            WakeLock wakeLock = this.mWakeLocks.get(index);
            this.mWakeLocks.remove(index);
            notifyWakeLockReleasedLocked(wakeLock);
            wakeLock.mLock.unlinkToDeath(wakeLock, 0);
            if ((flags & 1) != 0) {
                this.mRequestWaitForNegativeProximity = true;
            }
            applyWakeLockFlagsOnReleaseLocked(wakeLock);
            this.mDirty |= 1;
            updatePowerStateLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWakeLockDeath(WakeLock wakeLock) {
        synchronized (this.mLock) {
            int index = this.mWakeLocks.indexOf(wakeLock);
            if (index < 0) {
                return;
            }
            this.mWakeLocks.remove(index);
            notifyWakeLockReleasedLocked(wakeLock);
            applyWakeLockFlagsOnReleaseLocked(wakeLock);
            this.mDirty |= 1;
            updatePowerStateLocked();
        }
    }

    private void applyWakeLockFlagsOnReleaseLocked(WakeLock wakeLock) {
        if ((wakeLock.mFlags & 536870912) != 0 && isScreenLock(wakeLock)) {
            userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 0, 1, wakeLock.mOwnerUid);
        }
    }

    private void updateWakeLockWorkSourceInternal(IBinder lock, WorkSource ws) {
        synchronized (this.mLock) {
            int index = findWakeLockIndexLocked(lock);
            if (index < 0) {
                throw new IllegalArgumentException("Wake lock not active");
            }
            WakeLock wakeLock = this.mWakeLocks.get(index);
            if (!wakeLock.hasSameWorkSource(ws)) {
                notifyWakeLockReleasedLocked(wakeLock);
                wakeLock.updateWorkSource(ws);
                notifyWakeLockAcquiredLocked(wakeLock);
            }
        }
    }

    private int findWakeLockIndexLocked(IBinder lock) {
        int count = this.mWakeLocks.size();
        for (int i = 0; i < count; i++) {
            if (this.mWakeLocks.get(i).mLock == lock) {
                return i;
            }
        }
        return -1;
    }

    private void notifyWakeLockAcquiredLocked(WakeLock wakeLock) {
        if (this.mSystemReady) {
            wakeLock.mNotifiedAcquired = true;
            this.mNotifier.onWakeLockAcquired(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource);
        }
    }

    private void notifyWakeLockReleasedLocked(WakeLock wakeLock) {
        if (this.mSystemReady && wakeLock.mNotifiedAcquired) {
            wakeLock.mNotifiedAcquired = false;
            this.mNotifier.onWakeLockReleased(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource);
        }
    }

    private boolean isWakeLockLevelSupportedInternal(int level) {
        synchronized (this.mLock) {
            switch (level) {
                case 1:
                case 6:
                case 10:
                case 26:
                    return true;
                case 32:
                    return this.mSystemReady && this.mDisplayPowerController.isProximitySensorAvailable();
                default:
                    return false;
            }
        }
    }

    private void userActivityFromNative(long eventTime, int event, int flags) {
        userActivityInternal(eventTime, event, flags, 1000);
    }

    private void userActivityInternal(long eventTime, int event, int flags, int uid) {
        synchronized (this.mLock) {
            if (userActivityNoUpdateLocked(eventTime, event, flags, uid)) {
                updatePowerStateLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean userActivityNoUpdateLocked(long eventTime, int event, int flags, int uid) {
        if (eventTime < this.mLastSleepTime || eventTime < this.mLastWakeTime || this.mWakefulness == 0 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        this.mNotifier.onUserActivity(event, uid);
        if ((flags & 1) != 0) {
            if (eventTime > this.mLastUserActivityTimeNoChangeLights && eventTime > this.mLastUserActivityTime) {
                this.mLastUserActivityTimeNoChangeLights = eventTime;
                this.mDirty |= 4;
                return true;
            }
            return false;
        } else if (eventTime > this.mLastUserActivityTime) {
            this.mLastUserActivityTime = eventTime;
            this.mDirty |= 4;
            return true;
        } else {
            return false;
        }
    }

    private void wakeUpFromNative(long eventTime) {
        wakeUpInternal(eventTime);
    }

    private void wakeUpInternal(long eventTime) {
        synchronized (this.mLock) {
            if (wakeUpNoUpdateLocked(eventTime)) {
                updatePowerStateLocked();
            }
        }
    }

    private boolean wakeUpNoUpdateLocked(long eventTime) {
        if (eventTime < this.mLastSleepTime || this.mWakefulness == 1 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        switch (this.mWakefulness) {
            case 0:
                Slog.i(TAG, "Waking up from sleep...");
                sendPendingNotificationsLocked();
                this.mNotifier.onWakeUpStarted();
                this.mSendWakeUpFinishedNotificationWhenReady = true;
                break;
            case 2:
                Slog.i(TAG, "Waking up from nap...");
                break;
            case 3:
                Slog.i(TAG, "Waking up from dream...");
                break;
        }
        this.mLastWakeTime = eventTime;
        this.mWakefulness = 1;
        this.mDirty |= 2;
        userActivityNoUpdateLocked(eventTime, 0, 0, 1000);
        return true;
    }

    private void goToSleepFromNative(long eventTime, int reason) {
        goToSleepInternal(eventTime, reason);
    }

    private void goToSleepInternal(long eventTime, int reason) {
        synchronized (this.mLock) {
            if (goToSleepNoUpdateLocked(eventTime, reason)) {
                updatePowerStateLocked();
            }
        }
    }

    private boolean goToSleepNoUpdateLocked(long eventTime, int reason) {
        if (eventTime < this.mLastWakeTime || this.mWakefulness == 0 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        switch (reason) {
            case 1:
                Slog.i(TAG, "Going to sleep due to device administration policy...");
                break;
            case 2:
                Slog.i(TAG, "Going to sleep due to screen timeout...");
                break;
            default:
                Slog.i(TAG, "Going to sleep by user request...");
                reason = 0;
                break;
        }
        sendPendingNotificationsLocked();
        this.mNotifier.onGoToSleepStarted(reason);
        this.mSendGoToSleepFinishedNotificationWhenReady = true;
        this.mLastSleepTime = eventTime;
        this.mDirty |= 2;
        this.mWakefulness = 0;
        int numWakeLocksCleared = 0;
        int numWakeLocks = this.mWakeLocks.size();
        for (int i = 0; i < numWakeLocks; i++) {
            WakeLock wakeLock = this.mWakeLocks.get(i);
            switch (wakeLock.mFlags & 65535) {
                case 6:
                case 10:
                case 26:
                    numWakeLocksCleared++;
                    break;
            }
        }
        EventLog.writeEvent((int) EventLogTags.POWER_SLEEP_REQUESTED, numWakeLocksCleared);
        return true;
    }

    private void napInternal(long eventTime) {
        synchronized (this.mLock) {
            if (napNoUpdateLocked(eventTime)) {
                updatePowerStateLocked();
            }
        }
    }

    private boolean napNoUpdateLocked(long eventTime) {
        if (eventTime < this.mLastWakeTime || this.mWakefulness != 1 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        Slog.i(TAG, "Nap time...");
        this.mDirty |= 2;
        this.mWakefulness = 2;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePowerStateLocked() {
        int dirtyPhase1;
        if (!this.mSystemReady || this.mDirty == 0) {
            return;
        }
        updateIsPoweredLocked(this.mDirty);
        updateStayOnLocked(this.mDirty);
        long now = SystemClock.uptimeMillis();
        int dirtyPhase2 = 0;
        do {
            dirtyPhase1 = this.mDirty;
            dirtyPhase2 |= dirtyPhase1;
            this.mDirty = 0;
            updateWakeLockSummaryLocked(dirtyPhase1);
            updateUserActivitySummaryLocked(now, dirtyPhase1);
        } while (updateWakefulnessLocked(dirtyPhase1));
        updateDreamLocked(dirtyPhase2);
        updateDisplayPowerStateLocked(dirtyPhase2);
        if (this.mDisplayReady) {
            sendPendingNotificationsLocked();
        }
        updateSuspendBlockerLocked();
    }

    private void sendPendingNotificationsLocked() {
        if (this.mSendWakeUpFinishedNotificationWhenReady) {
            this.mSendWakeUpFinishedNotificationWhenReady = false;
            this.mNotifier.onWakeUpFinished();
        }
        if (this.mSendGoToSleepFinishedNotificationWhenReady) {
            this.mSendGoToSleepFinishedNotificationWhenReady = false;
            this.mNotifier.onGoToSleepFinished();
        }
    }

    private void updateIsPoweredLocked(int dirty) {
        if ((dirty & 256) != 0) {
            boolean wasPowered = this.mIsPowered;
            int oldPlugType = this.mPlugType;
            this.mIsPowered = this.mBatteryService.isPowered(7);
            this.mPlugType = this.mBatteryService.getPlugType();
            this.mBatteryLevel = this.mBatteryService.getBatteryLevel();
            if (wasPowered != this.mIsPowered || oldPlugType != this.mPlugType) {
                this.mDirty |= 64;
                boolean dockedOnWirelessCharger = this.mWirelessChargerDetector.update(this.mIsPowered, this.mPlugType, this.mBatteryLevel);
                long now = SystemClock.uptimeMillis();
                if (shouldWakeUpWhenPluggedOrUnpluggedLocked(wasPowered, oldPlugType, dockedOnWirelessCharger)) {
                    wakeUpNoUpdateLocked(now);
                }
                userActivityNoUpdateLocked(now, 0, 0, 1000);
                if (dockedOnWirelessCharger) {
                    this.mNotifier.onWirelessChargingStarted();
                }
            }
        }
    }

    private boolean shouldWakeUpWhenPluggedOrUnpluggedLocked(boolean wasPowered, int oldPlugType, boolean dockedOnWirelessCharger) {
        if (!this.mWakeUpWhenPluggedOrUnpluggedConfig) {
            return false;
        }
        if (wasPowered && !this.mIsPowered && oldPlugType == 4) {
            return false;
        }
        if (!wasPowered && this.mIsPowered && this.mPlugType == 4 && !dockedOnWirelessCharger) {
            return false;
        }
        if (this.mIsPowered) {
            if (this.mWakefulness == 2 || this.mWakefulness == 3) {
                return false;
            }
            return true;
        }
        return true;
    }

    private void updateStayOnLocked(int dirty) {
        if ((dirty & MediaProperties.HEIGHT_288) != 0) {
            boolean wasStayOn = this.mStayOn;
            if (this.mStayOnWhilePluggedInSetting != 0 && !isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
                this.mStayOn = this.mBatteryService.isPowered(this.mStayOnWhilePluggedInSetting);
            } else {
                this.mStayOn = false;
            }
            if (this.mStayOn != wasStayOn) {
                this.mDirty |= 128;
            }
        }
    }

    private void updateWakeLockSummaryLocked(int dirty) {
        if ((dirty & 3) != 0) {
            this.mWakeLockSummary = 0;
            int numWakeLocks = this.mWakeLocks.size();
            for (int i = 0; i < numWakeLocks; i++) {
                WakeLock wakeLock = this.mWakeLocks.get(i);
                switch (wakeLock.mFlags & 65535) {
                    case 1:
                        this.mWakeLockSummary |= 1;
                        break;
                    case 6:
                        if (this.mWakefulness != 0) {
                            this.mWakeLockSummary |= 5;
                            if (this.mWakefulness == 1) {
                                this.mWakeLockSummary |= 32;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 10:
                        if (this.mWakefulness != 0) {
                            this.mWakeLockSummary |= 3;
                            if (this.mWakefulness == 1) {
                                this.mWakeLockSummary |= 32;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 26:
                        if (this.mWakefulness != 0) {
                            this.mWakeLockSummary |= 11;
                            if (this.mWakefulness == 1) {
                                this.mWakeLockSummary |= 32;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    case 32:
                        if (this.mWakefulness != 0) {
                            this.mWakeLockSummary |= 16;
                            break;
                        } else {
                            break;
                        }
                }
            }
        }
    }

    private void updateUserActivitySummaryLocked(long now, int dirty) {
        if ((dirty & 38) != 0) {
            this.mHandler.removeMessages(1);
            long nextTimeout = 0;
            if (this.mWakefulness != 0) {
                int screenOffTimeout = getScreenOffTimeoutLocked();
                int screenDimDuration = getScreenDimDurationLocked(screenOffTimeout);
                this.mUserActivitySummary = 0;
                if (this.mLastUserActivityTime >= this.mLastWakeTime) {
                    nextTimeout = (this.mLastUserActivityTime + screenOffTimeout) - screenDimDuration;
                    if (now < nextTimeout) {
                        this.mUserActivitySummary |= 1;
                    } else {
                        nextTimeout = this.mLastUserActivityTime + screenOffTimeout;
                        if (now < nextTimeout) {
                            this.mUserActivitySummary |= 2;
                        }
                    }
                }
                if (this.mUserActivitySummary == 0 && this.mLastUserActivityTimeNoChangeLights >= this.mLastWakeTime) {
                    nextTimeout = this.mLastUserActivityTimeNoChangeLights + screenOffTimeout;
                    if (now < nextTimeout && this.mDisplayPowerRequest.screenState != 0) {
                        this.mUserActivitySummary = this.mDisplayPowerRequest.screenState == 2 ? 1 : 2;
                    }
                }
                if (this.mUserActivitySummary != 0) {
                    Message msg = this.mHandler.obtainMessage(1);
                    msg.setAsynchronous(true);
                    this.mHandler.sendMessageAtTime(msg, nextTimeout);
                    return;
                }
                return;
            }
            this.mUserActivitySummary = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUserActivityTimeout() {
        synchronized (this.mLock) {
            this.mDirty |= 4;
            updatePowerStateLocked();
        }
    }

    private int getScreenOffTimeoutLocked() {
        int timeout = this.mScreenOffTimeoutSetting;
        if (isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
            timeout = Math.min(timeout, this.mMaximumScreenOffTimeoutFromDeviceAdmin);
        }
        if (this.mUserActivityTimeoutOverrideFromWindowManager >= 0) {
            timeout = (int) Math.min(timeout, this.mUserActivityTimeoutOverrideFromWindowManager);
        }
        return Math.max(timeout, 10000);
    }

    private int getScreenDimDurationLocked(int screenOffTimeout) {
        return Math.min((int) SCREEN_DIM_DURATION, (int) (screenOffTimeout * 0.2f));
    }

    private boolean updateWakefulnessLocked(int dirty) {
        boolean changed = false;
        if ((dirty & 2711) != 0 && this.mWakefulness == 1 && isItBedTimeYetLocked()) {
            long time = SystemClock.uptimeMillis();
            changed = shouldNapAtBedTimeLocked() ? napNoUpdateLocked(time) : goToSleepNoUpdateLocked(time, 2);
        }
        return changed;
    }

    private boolean shouldNapAtBedTimeLocked() {
        return this.mDreamsActivateOnSleepSetting || (this.mDreamsActivateOnDockSetting && this.mDockState != 0);
    }

    private boolean isItBedTimeYetLocked() {
        return this.mBootCompleted && !isBeingKeptAwakeLocked();
    }

    private boolean isBeingKeptAwakeLocked() {
        return this.mStayOn || this.mProximityPositive || (this.mWakeLockSummary & 32) != 0 || (this.mUserActivitySummary & 3) != 0;
    }

    private void updateDreamLocked(int dirty) {
        if ((dirty & RILConstants.RIL_UNSOL_STK_CALL_SETUP) != 0) {
            scheduleSandmanLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleSandmanLocked() {
        if (!this.mSandmanScheduled) {
            this.mSandmanScheduled = true;
            Message msg = this.mHandler.obtainMessage(2);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSandman() {
        boolean startDreaming = false;
        synchronized (this.mLock) {
            this.mSandmanScheduled = false;
            boolean canDream = canDreamLocked();
            if (canDream && this.mWakefulness == 2) {
                startDreaming = true;
            }
        }
        boolean isDreaming = false;
        if (this.mDreamManager != null) {
            if (startDreaming) {
                this.mDreamManager.startDream();
            }
            isDreaming = this.mDreamManager.isDreaming();
        }
        boolean continueDreaming = false;
        synchronized (this.mLock) {
            if (isDreaming) {
                if (canDreamLocked()) {
                    if (this.mWakefulness == 2) {
                        this.mWakefulness = 3;
                        this.mDirty |= 2;
                        this.mBatteryLevelWhenDreamStarted = this.mBatteryLevel;
                        updatePowerStateLocked();
                        continueDreaming = true;
                    } else if (this.mWakefulness == 3) {
                        if (!isBeingKeptAwakeLocked() && this.mBatteryLevel < this.mBatteryLevelWhenDreamStarted - 5) {
                            Slog.i(TAG, "Stopping dream because the battery appears to be draining faster than it is charging.  Battery level when dream started: " + this.mBatteryLevelWhenDreamStarted + "%.  Battery level now: " + this.mBatteryLevel + "%.");
                        } else {
                            continueDreaming = true;
                        }
                    }
                }
            }
            if (!continueDreaming) {
                handleDreamFinishedLocked();
            }
        }
        if (this.mDreamManager != null && !continueDreaming) {
            this.mDreamManager.stopDream();
        }
    }

    private boolean canDreamLocked() {
        return this.mDreamsSupportedConfig && this.mDreamsEnabledSetting && this.mDisplayPowerRequest.screenState != 0 && this.mBootCompleted && (this.mIsPowered || isBeingKeptAwakeLocked());
    }

    private void handleDreamFinishedLocked() {
        if (this.mWakefulness == 2 || this.mWakefulness == 3) {
            if (isItBedTimeYetLocked()) {
                goToSleepNoUpdateLocked(SystemClock.uptimeMillis(), 2);
                updatePowerStateLocked();
                return;
            }
            wakeUpNoUpdateLocked(SystemClock.uptimeMillis());
            updatePowerStateLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenOnBlockerReleased() {
        synchronized (this.mLock) {
            this.mDirty |= 1024;
            updatePowerStateLocked();
        }
    }

    private void updateDisplayPowerStateLocked(int dirty) {
        if ((dirty & 1087) != 0) {
            int newScreenState = getDesiredScreenPowerStateLocked();
            if (newScreenState != this.mDisplayPowerRequest.screenState) {
                if (newScreenState == 0 && this.mDisplayPowerRequest.screenState != 0) {
                    this.mLastScreenOffEventElapsedRealTime = SystemClock.elapsedRealtime();
                }
                this.mDisplayPowerRequest.screenState = newScreenState;
                nativeSetPowerState(newScreenState != 0, newScreenState == 2);
            }
            int screenBrightness = this.mScreenBrightnessSettingDefault;
            float screenAutoBrightnessAdjustment = 0.0f;
            boolean autoBrightness = this.mScreenBrightnessModeSetting == 1;
            if (isValidBrightness(this.mScreenBrightnessOverrideFromWindowManager)) {
                screenBrightness = this.mScreenBrightnessOverrideFromWindowManager;
                autoBrightness = false;
            } else if (isValidBrightness(this.mTemporaryScreenBrightnessSettingOverride)) {
                screenBrightness = this.mTemporaryScreenBrightnessSettingOverride;
            } else if (isValidBrightness(this.mScreenBrightnessSetting)) {
                screenBrightness = this.mScreenBrightnessSetting;
            }
            if (autoBrightness) {
                screenBrightness = this.mScreenBrightnessSettingDefault;
                if (isValidAutoBrightnessAdjustment(this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride)) {
                    screenAutoBrightnessAdjustment = this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride;
                } else if (isValidAutoBrightnessAdjustment(this.mScreenAutoBrightnessAdjustmentSetting)) {
                    screenAutoBrightnessAdjustment = this.mScreenAutoBrightnessAdjustmentSetting;
                }
            }
            int screenBrightness2 = Math.max(Math.min(screenBrightness, this.mScreenBrightnessSettingMaximum), this.mScreenBrightnessSettingMinimum);
            float screenAutoBrightnessAdjustment2 = Math.max(Math.min(screenAutoBrightnessAdjustment, 1.0f), -1.0f);
            this.mDisplayPowerRequest.screenBrightness = screenBrightness2;
            this.mDisplayPowerRequest.screenAutoBrightnessAdjustment = screenAutoBrightnessAdjustment2;
            this.mDisplayPowerRequest.useAutoBrightness = autoBrightness;
            this.mDisplayPowerRequest.useProximitySensor = shouldUseProximitySensorLocked();
            this.mDisplayPowerRequest.blockScreenOn = this.mScreenOnBlocker.isHeld();
            this.mDisplayReady = this.mDisplayPowerController.requestPowerState(this.mDisplayPowerRequest, this.mRequestWaitForNegativeProximity);
            this.mRequestWaitForNegativeProximity = false;
        }
    }

    private static boolean isValidBrightness(int value) {
        return value >= 0 && value <= 255;
    }

    private static boolean isValidAutoBrightnessAdjustment(float value) {
        return value >= -1.0f && value <= 1.0f;
    }

    private int getDesiredScreenPowerStateLocked() {
        if (this.mWakefulness == 0) {
            return 0;
        }
        if ((this.mWakeLockSummary & 2) != 0 || (this.mUserActivitySummary & 1) != 0 || !this.mBootCompleted) {
            return 2;
        }
        return 1;
    }

    private boolean shouldUseProximitySensorLocked() {
        return (this.mWakeLockSummary & 16) != 0;
    }

    private void updateSuspendBlockerLocked() {
        boolean needWakeLockSuspendBlocker = (this.mWakeLockSummary & 1) != 0;
        boolean needDisplaySuspendBlocker = needDisplaySuspendBlocker();
        if (needWakeLockSuspendBlocker && !this.mHoldingWakeLockSuspendBlocker) {
            this.mWakeLockSuspendBlocker.acquire();
            this.mHoldingWakeLockSuspendBlocker = true;
        }
        if (needDisplaySuspendBlocker && !this.mHoldingDisplaySuspendBlocker) {
            this.mDisplaySuspendBlocker.acquire();
            this.mHoldingDisplaySuspendBlocker = true;
        }
        if (!needWakeLockSuspendBlocker && this.mHoldingWakeLockSuspendBlocker) {
            this.mWakeLockSuspendBlocker.release();
            this.mHoldingWakeLockSuspendBlocker = false;
        }
        if (!needDisplaySuspendBlocker && this.mHoldingDisplaySuspendBlocker) {
            this.mDisplaySuspendBlocker.release();
            this.mHoldingDisplaySuspendBlocker = false;
        }
    }

    private boolean needDisplaySuspendBlocker() {
        if (!this.mDisplayReady) {
            return true;
        }
        if (this.mDisplayPowerRequest.screenState != 0) {
            if (!this.mDisplayPowerRequest.useProximitySensor || !this.mProximityPositive || !this.mSuspendWhenScreenOffDueToProximityConfig) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isScreenOnInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mSystemReady && this.mDisplayPowerRequest.screenState == 0) ? false : true;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBatteryStateChangedLocked() {
        this.mDirty |= 256;
        updatePowerStateLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWatchingForBootAnimationFinished() {
        this.mHandler.sendEmptyMessage(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfBootAnimationFinished() {
        if (SystemService.isRunning(BOOT_ANIMATION_SERVICE)) {
            this.mHandler.sendEmptyMessageDelayed(4, 200L);
            return;
        }
        synchronized (this.mLock) {
            if (!this.mBootCompleted) {
                Slog.i(TAG, "Boot animation finished.");
                handleBootCompletedLocked();
            }
        }
    }

    private void handleBootCompletedLocked() {
        long now = SystemClock.uptimeMillis();
        this.mBootCompleted = true;
        this.mDirty |= 16;
        userActivityNoUpdateLocked(now, 0, 0, 1000);
        updatePowerStateLocked();
    }

    private void shutdownOrRebootInternal(final boolean shutdown, final boolean confirm, final String reason, boolean wait) {
        if (this.mHandler == null || !this.mSystemReady) {
            throw new IllegalStateException("Too early to call shutdown() or reboot()");
        }
        Runnable runnable = new Runnable() { // from class: com.android.server.power.PowerManagerService.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (this) {
                    if (shutdown) {
                        ShutdownThread.shutdown(PowerManagerService.this.mContext, confirm);
                    } else {
                        ShutdownThread.reboot(PowerManagerService.this.mContext, reason, confirm);
                    }
                }
            }
        };
        Message msg = Message.obtain(this.mHandler, runnable);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
        if (wait) {
            synchronized (runnable) {
                while (true) {
                    try {
                        runnable.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private void crashInternal(final String message) {
        Thread t = new Thread("PowerManagerService.crash()") { // from class: com.android.server.power.PowerManagerService.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                throw new RuntimeException(message);
            }
        };
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            Log.wtf(TAG, e);
        }
    }

    private void setStayOnSettingInternal(int val) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", val);
    }

    private void setMaximumScreenOffTimeoutFromDeviceAdminInternal(int timeMs) {
        synchronized (this.mLock) {
            this.mMaximumScreenOffTimeoutFromDeviceAdmin = timeMs;
            this.mDirty |= 32;
            updatePowerStateLocked();
        }
    }

    private boolean isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() {
        return this.mMaximumScreenOffTimeoutFromDeviceAdmin >= 0 && this.mMaximumScreenOffTimeoutFromDeviceAdmin < Integer.MAX_VALUE;
    }

    private void setAttentionLightInternal(boolean on, int color) {
        synchronized (this.mLock) {
            if (this.mSystemReady) {
                LightsService.Light light = this.mAttentionLight;
                light.setFlashing(color, 2, on ? 3 : 0, 0);
            }
        }
    }

    public long timeSinceScreenWasLastOn() {
        synchronized (this.mLock) {
            if (this.mDisplayPowerRequest.screenState != 0) {
                return 0L;
            }
            return SystemClock.elapsedRealtime() - this.mLastScreenOffEventElapsedRealTime;
        }
    }

    private void setScreenBrightnessOverrideFromWindowManagerInternal(int brightness) {
        synchronized (this.mLock) {
            if (this.mScreenBrightnessOverrideFromWindowManager != brightness) {
                this.mScreenBrightnessOverrideFromWindowManager = brightness;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    public void setButtonBrightnessOverrideFromWindowManager(int brightness) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DEVICE_POWER, null);
    }

    private void setUserActivityTimeoutOverrideFromWindowManagerInternal(long timeoutMillis) {
        synchronized (this.mLock) {
            if (this.mUserActivityTimeoutOverrideFromWindowManager != timeoutMillis) {
                this.mUserActivityTimeoutOverrideFromWindowManager = timeoutMillis;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    private void setTemporaryScreenBrightnessSettingOverrideInternal(int brightness) {
        synchronized (this.mLock) {
            if (this.mTemporaryScreenBrightnessSettingOverride != brightness) {
                this.mTemporaryScreenBrightnessSettingOverride = brightness;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    private void setTemporaryScreenAutoBrightnessAdjustmentSettingOverrideInternal(float adj) {
        synchronized (this.mLock) {
            if (this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride != adj) {
                this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride = adj;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    public static void lowLevelShutdown() {
        SystemProperties.set("sys.powerctl", "shutdown");
    }

    public static void lowLevelReboot(String reason) {
        if (reason == null) {
            reason = "";
        }
        SystemProperties.set("sys.powerctl", "reboot," + reason);
        try {
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mLock) {
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        DisplayPowerController dpc;
        WirelessChargerDetector wcd;
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump PowerManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("POWER MANAGER (dumpsys power)\n");
        synchronized (this.mLock) {
            pw.println("Power Manager State:");
            pw.println("  mDirty=0x" + Integer.toHexString(this.mDirty));
            pw.println("  mWakefulness=" + wakefulnessToString(this.mWakefulness));
            pw.println("  mIsPowered=" + this.mIsPowered);
            pw.println("  mPlugType=" + this.mPlugType);
            pw.println("  mBatteryLevel=" + this.mBatteryLevel);
            pw.println("  mBatteryLevelWhenDreamStarted=" + this.mBatteryLevelWhenDreamStarted);
            pw.println("  mDockState=" + this.mDockState);
            pw.println("  mStayOn=" + this.mStayOn);
            pw.println("  mProximityPositive=" + this.mProximityPositive);
            pw.println("  mBootCompleted=" + this.mBootCompleted);
            pw.println("  mSystemReady=" + this.mSystemReady);
            pw.println("  mWakeLockSummary=0x" + Integer.toHexString(this.mWakeLockSummary));
            pw.println("  mUserActivitySummary=0x" + Integer.toHexString(this.mUserActivitySummary));
            pw.println("  mRequestWaitForNegativeProximity=" + this.mRequestWaitForNegativeProximity);
            pw.println("  mSandmanScheduled=" + this.mSandmanScheduled);
            pw.println("  mLastWakeTime=" + TimeUtils.formatUptime(this.mLastWakeTime));
            pw.println("  mLastSleepTime=" + TimeUtils.formatUptime(this.mLastSleepTime));
            pw.println("  mSendWakeUpFinishedNotificationWhenReady=" + this.mSendWakeUpFinishedNotificationWhenReady);
            pw.println("  mSendGoToSleepFinishedNotificationWhenReady=" + this.mSendGoToSleepFinishedNotificationWhenReady);
            pw.println("  mLastUserActivityTime=" + TimeUtils.formatUptime(this.mLastUserActivityTime));
            pw.println("  mLastUserActivityTimeNoChangeLights=" + TimeUtils.formatUptime(this.mLastUserActivityTimeNoChangeLights));
            pw.println("  mDisplayReady=" + this.mDisplayReady);
            pw.println("  mHoldingWakeLockSuspendBlocker=" + this.mHoldingWakeLockSuspendBlocker);
            pw.println("  mHoldingDisplaySuspendBlocker=" + this.mHoldingDisplaySuspendBlocker);
            pw.println();
            pw.println("Settings and Configuration:");
            pw.println("  mWakeUpWhenPluggedOrUnpluggedConfig=" + this.mWakeUpWhenPluggedOrUnpluggedConfig);
            pw.println("  mSuspendWhenScreenOffDueToProximityConfig=" + this.mSuspendWhenScreenOffDueToProximityConfig);
            pw.println("  mDreamsSupportedConfig=" + this.mDreamsSupportedConfig);
            pw.println("  mDreamsEnabledByDefaultConfig=" + this.mDreamsEnabledByDefaultConfig);
            pw.println("  mDreamsActivatedOnSleepByDefaultConfig=" + this.mDreamsActivatedOnSleepByDefaultConfig);
            pw.println("  mDreamsActivatedOnDockByDefaultConfig=" + this.mDreamsActivatedOnDockByDefaultConfig);
            pw.println("  mDreamsEnabledSetting=" + this.mDreamsEnabledSetting);
            pw.println("  mDreamsActivateOnSleepSetting=" + this.mDreamsActivateOnSleepSetting);
            pw.println("  mDreamsActivateOnDockSetting=" + this.mDreamsActivateOnDockSetting);
            pw.println("  mScreenOffTimeoutSetting=" + this.mScreenOffTimeoutSetting);
            pw.println("  mMaximumScreenOffTimeoutFromDeviceAdmin=" + this.mMaximumScreenOffTimeoutFromDeviceAdmin + " (enforced=" + isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() + Separators.RPAREN);
            pw.println("  mStayOnWhilePluggedInSetting=" + this.mStayOnWhilePluggedInSetting);
            pw.println("  mScreenBrightnessSetting=" + this.mScreenBrightnessSetting);
            pw.println("  mScreenAutoBrightnessAdjustmentSetting=" + this.mScreenAutoBrightnessAdjustmentSetting);
            pw.println("  mScreenBrightnessModeSetting=" + this.mScreenBrightnessModeSetting);
            pw.println("  mScreenBrightnessOverrideFromWindowManager=" + this.mScreenBrightnessOverrideFromWindowManager);
            pw.println("  mUserActivityTimeoutOverrideFromWindowManager=" + this.mUserActivityTimeoutOverrideFromWindowManager);
            pw.println("  mTemporaryScreenBrightnessSettingOverride=" + this.mTemporaryScreenBrightnessSettingOverride);
            pw.println("  mTemporaryScreenAutoBrightnessAdjustmentSettingOverride=" + this.mTemporaryScreenAutoBrightnessAdjustmentSettingOverride);
            pw.println("  mScreenBrightnessSettingMinimum=" + this.mScreenBrightnessSettingMinimum);
            pw.println("  mScreenBrightnessSettingMaximum=" + this.mScreenBrightnessSettingMaximum);
            pw.println("  mScreenBrightnessSettingDefault=" + this.mScreenBrightnessSettingDefault);
            int screenOffTimeout = getScreenOffTimeoutLocked();
            int screenDimDuration = getScreenDimDurationLocked(screenOffTimeout);
            pw.println();
            pw.println("Screen off timeout: " + screenOffTimeout + " ms");
            pw.println("Screen dim duration: " + screenDimDuration + " ms");
            pw.println();
            pw.println("Wake Locks: size=" + this.mWakeLocks.size());
            Iterator i$ = this.mWakeLocks.iterator();
            while (i$.hasNext()) {
                WakeLock wl = i$.next();
                pw.println("  " + wl);
            }
            pw.println();
            pw.println("Suspend Blockers: size=" + this.mSuspendBlockers.size());
            Iterator i$2 = this.mSuspendBlockers.iterator();
            while (i$2.hasNext()) {
                SuspendBlocker sb = i$2.next();
                pw.println("  " + sb);
            }
            pw.println();
            pw.println("Screen On Blocker: " + this.mScreenOnBlocker);
            pw.println();
            pw.println("Display Blanker: " + this.mDisplayBlanker);
            dpc = this.mDisplayPowerController;
            wcd = this.mWirelessChargerDetector;
        }
        if (dpc != null) {
            dpc.dump(pw);
        }
        if (wcd != null) {
            wcd.dump(pw);
        }
    }

    private SuspendBlocker createSuspendBlockerLocked(String name) {
        SuspendBlocker suspendBlocker = new SuspendBlockerImpl(name);
        this.mSuspendBlockers.add(suspendBlocker);
        return suspendBlocker;
    }

    private static String wakefulnessToString(int wakefulness) {
        switch (wakefulness) {
            case 0:
                return "Asleep";
            case 1:
                return "Awake";
            case 2:
                return "Napping";
            case 3:
                return "Dreaming";
            default:
                return Integer.toString(wakefulness);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static WorkSource copyWorkSource(WorkSource workSource) {
        if (workSource != null) {
            return new WorkSource(workSource);
        }
        return null;
    }

    /* loaded from: PowerManagerService$BatteryReceiver.class */
    private final class BatteryReceiver extends BroadcastReceiver {
        private BatteryReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleBatteryStateChangedLocked();
            }
        }
    }

    /* loaded from: PowerManagerService$BootCompletedReceiver.class */
    private final class BootCompletedReceiver extends BroadcastReceiver {
        private BootCompletedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PowerManagerService.this.startWatchingForBootAnimationFinished();
        }
    }

    /* loaded from: PowerManagerService$DreamReceiver.class */
    private final class DreamReceiver extends BroadcastReceiver {
        private DreamReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.scheduleSandmanLocked();
            }
        }
    }

    /* loaded from: PowerManagerService$UserSwitchedReceiver.class */
    private final class UserSwitchedReceiver extends BroadcastReceiver {
        private UserSwitchedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleSettingsChangedLocked();
            }
        }
    }

    /* loaded from: PowerManagerService$DockReceiver.class */
    private final class DockReceiver extends BroadcastReceiver {
        private DockReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                int dockState = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, 0);
                if (PowerManagerService.this.mDockState != dockState) {
                    PowerManagerService.this.mDockState = dockState;
                    PowerManagerService.access$876(PowerManagerService.this, 2048);
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }
        }
    }

    /* loaded from: PowerManagerService$SettingsObserver.class */
    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleSettingsChangedLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PowerManagerService$PowerManagerHandler.class */
    public final class PowerManagerHandler extends Handler {
        public PowerManagerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PowerManagerService.this.handleUserActivityTimeout();
                    return;
                case 2:
                    PowerManagerService.this.handleSandman();
                    return;
                case 3:
                    PowerManagerService.this.handleScreenOnBlockerReleased();
                    return;
                case 4:
                    PowerManagerService.this.checkIfBootAnimationFinished();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PowerManagerService$WakeLock.class */
    public final class WakeLock implements IBinder.DeathRecipient {
        public final IBinder mLock;
        public int mFlags;
        public String mTag;
        public final String mPackageName;
        public WorkSource mWorkSource;
        public final int mOwnerUid;
        public final int mOwnerPid;
        public boolean mNotifiedAcquired;

        public WakeLock(IBinder lock, int flags, String tag, String packageName, WorkSource workSource, int ownerUid, int ownerPid) {
            this.mLock = lock;
            this.mFlags = flags;
            this.mTag = tag;
            this.mPackageName = packageName;
            this.mWorkSource = PowerManagerService.copyWorkSource(workSource);
            this.mOwnerUid = ownerUid;
            this.mOwnerPid = ownerPid;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            PowerManagerService.this.handleWakeLockDeath(this);
        }

        public boolean hasSameProperties(int flags, String tag, WorkSource workSource, int ownerUid, int ownerPid) {
            return this.mFlags == flags && this.mTag.equals(tag) && hasSameWorkSource(workSource) && this.mOwnerUid == ownerUid && this.mOwnerPid == ownerPid;
        }

        public void updateProperties(int flags, String tag, String packageName, WorkSource workSource, int ownerUid, int ownerPid) {
            if (!this.mPackageName.equals(packageName)) {
                throw new IllegalStateException("Existing wake lock package name changed: " + this.mPackageName + " to " + packageName);
            }
            if (this.mOwnerUid != ownerUid) {
                throw new IllegalStateException("Existing wake lock uid changed: " + this.mOwnerUid + " to " + ownerUid);
            }
            if (this.mOwnerPid != ownerPid) {
                throw new IllegalStateException("Existing wake lock pid changed: " + this.mOwnerPid + " to " + ownerPid);
            }
            this.mFlags = flags;
            this.mTag = tag;
            updateWorkSource(workSource);
        }

        public boolean hasSameWorkSource(WorkSource workSource) {
            return Objects.equal(this.mWorkSource, workSource);
        }

        public void updateWorkSource(WorkSource workSource) {
            this.mWorkSource = PowerManagerService.copyWorkSource(workSource);
        }

        public String toString() {
            return getLockLevelString() + " '" + this.mTag + Separators.QUOTE + getLockFlagsString() + " (uid=" + this.mOwnerUid + ", pid=" + this.mOwnerPid + ", ws=" + this.mWorkSource + Separators.RPAREN;
        }

        private String getLockLevelString() {
            switch (this.mFlags & 65535) {
                case 1:
                    return "PARTIAL_WAKE_LOCK             ";
                case 6:
                    return "SCREEN_DIM_WAKE_LOCK          ";
                case 10:
                    return "SCREEN_BRIGHT_WAKE_LOCK       ";
                case 26:
                    return "FULL_WAKE_LOCK                ";
                case 32:
                    return "PROXIMITY_SCREEN_OFF_WAKE_LOCK";
                default:
                    return "???                           ";
            }
        }

        private String getLockFlagsString() {
            String result = "";
            if ((this.mFlags & 268435456) != 0) {
                result = result + " ACQUIRE_CAUSES_WAKEUP";
            }
            if ((this.mFlags & 536870912) != 0) {
                result = result + " ON_AFTER_RELEASE";
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PowerManagerService$SuspendBlockerImpl.class */
    public final class SuspendBlockerImpl implements SuspendBlocker {
        private final String mName;
        private int mReferenceCount;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.SuspendBlockerImpl.finalize():void, file: PowerManagerService$SuspendBlockerImpl.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        protected void finalize() throws java.lang.Throwable {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.PowerManagerService.SuspendBlockerImpl.finalize():void, file: PowerManagerService$SuspendBlockerImpl.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.SuspendBlockerImpl.finalize():void");
        }

        public SuspendBlockerImpl(String name) {
            this.mName = name;
        }

        @Override // com.android.server.power.SuspendBlocker
        public void acquire() {
            synchronized (this) {
                this.mReferenceCount++;
                if (this.mReferenceCount == 1) {
                    PowerManagerService.nativeAcquireSuspendBlocker(this.mName);
                }
            }
        }

        @Override // com.android.server.power.SuspendBlocker
        public void release() {
            synchronized (this) {
                this.mReferenceCount--;
                if (this.mReferenceCount == 0) {
                    PowerManagerService.nativeReleaseSuspendBlocker(this.mName);
                } else if (this.mReferenceCount < 0) {
                    Log.wtf(PowerManagerService.TAG, "Suspend blocker \"" + this.mName + "\" was released without being acquired!", new Throwable());
                    this.mReferenceCount = 0;
                }
            }
        }

        public String toString() {
            String str;
            synchronized (this) {
                str = this.mName + ": ref count=" + this.mReferenceCount;
            }
            return str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PowerManagerService$ScreenOnBlockerImpl.class */
    public final class ScreenOnBlockerImpl implements ScreenOnBlocker {
        private int mNestCount;

        private ScreenOnBlockerImpl() {
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this) {
                z = this.mNestCount != 0;
            }
            return z;
        }

        @Override // com.android.server.power.ScreenOnBlocker
        public void acquire() {
            synchronized (this) {
                this.mNestCount++;
            }
        }

        @Override // com.android.server.power.ScreenOnBlocker
        public void release() {
            synchronized (this) {
                this.mNestCount--;
                if (this.mNestCount < 0) {
                    Log.wtf(PowerManagerService.TAG, "Screen on blocker was released without being acquired!", new Throwable());
                    this.mNestCount = 0;
                }
                if (this.mNestCount == 0) {
                    PowerManagerService.this.mHandler.sendEmptyMessage(3);
                }
            }
        }

        public String toString() {
            String str;
            synchronized (this) {
                str = "held=" + (this.mNestCount != 0) + ", mNestCount=" + this.mNestCount;
            }
            return str;
        }
    }

    /* loaded from: PowerManagerService$DisplayBlankerImpl.class */
    private final class DisplayBlankerImpl implements DisplayBlanker {
        private boolean mBlanked;

        private DisplayBlankerImpl() {
        }

        @Override // com.android.server.power.DisplayBlanker
        public void blankAllDisplays() {
            synchronized (this) {
                this.mBlanked = true;
                PowerManagerService.this.mDisplayManagerService.blankAllDisplaysFromPowerManager();
                PowerManagerService.nativeSetInteractive(false);
                PowerManagerService.nativeSetAutoSuspend(true);
            }
        }

        @Override // com.android.server.power.DisplayBlanker
        public void unblankAllDisplays() {
            synchronized (this) {
                PowerManagerService.nativeSetAutoSuspend(false);
                PowerManagerService.nativeSetInteractive(true);
                PowerManagerService.this.mDisplayManagerService.unblankAllDisplaysFromPowerManager();
                this.mBlanked = false;
            }
        }

        public String toString() {
            String str;
            synchronized (this) {
                str = "blanked=" + this.mBlanked;
            }
            return str;
        }
    }
}