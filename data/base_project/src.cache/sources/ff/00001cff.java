package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.IUiModeManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.dreams.Sandman;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.app.DisableCarModeActivity;
import com.android.server.TwilightService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: UiModeManagerService.class */
final class UiModeManagerService extends IUiModeManager.Stub {
    private static final String TAG = UiModeManager.class.getSimpleName();
    private static final boolean LOG = false;
    private static final boolean ENABLE_LAUNCH_CAR_DOCK_APP = true;
    private static final boolean ENABLE_LAUNCH_DESK_DOCK_APP = true;
    private final Context mContext;
    private final TwilightService mTwilightService;
    private int mNightMode;
    private final int mDefaultUiModeType;
    private final boolean mCarModeKeepsScreenOn;
    private final boolean mDeskModeKeepsScreenOn;
    private final boolean mTelevision;
    private boolean mComputedNightMode;
    private boolean mSystemReady;
    private NotificationManager mNotificationManager;
    private StatusBarManager mStatusBarManager;
    private final PowerManager mPowerManager;
    private final PowerManager.WakeLock mWakeLock;
    private final Handler mHandler = new Handler();
    final Object mLock = new Object();
    private int mDockState = 0;
    private int mLastBroadcastState = 0;
    private boolean mCarModeEnabled = false;
    private boolean mCharging = false;
    private int mCurUiMode = 0;
    private int mSetUiMode = 0;
    private boolean mHoldingConfiguration = false;
    private Configuration mConfiguration = new Configuration();
    private final BroadcastReceiver mResultReceiver = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() != -1) {
                return;
            }
            int enableFlags = intent.getIntExtra("enableFlags", 0);
            int disableFlags = intent.getIntExtra("disableFlags", 0);
            synchronized (UiModeManagerService.this.mLock) {
                UiModeManagerService.this.updateAfterBroadcastLocked(intent.getAction(), enableFlags, disableFlags);
            }
        }
    };
    private final BroadcastReceiver mDockModeReceiver = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, 0);
            UiModeManagerService.this.updateDockState(state);
        }
    };
    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            UiModeManagerService.this.mCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.mSystemReady) {
                    UiModeManagerService.this.updateLocked(0, 0);
                }
            }
        }
    };
    private final TwilightService.TwilightListener mTwilightListener = new TwilightService.TwilightListener() { // from class: com.android.server.UiModeManagerService.4
        @Override // com.android.server.TwilightService.TwilightListener
        public void onTwilightStateChanged() {
            UiModeManagerService.this.updateTwilight();
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.disableCarMode(int):void, file: UiModeManagerService.class
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
    @Override // android.app.IUiModeManager
    public void disableCarMode(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.disableCarMode(int):void, file: UiModeManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.disableCarMode(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.enableCarMode(int):void, file: UiModeManagerService.class
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
    @Override // android.app.IUiModeManager
    public void enableCarMode(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.enableCarMode(int):void, file: UiModeManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.enableCarMode(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.getCurrentModeType():int, file: UiModeManagerService.class
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
    @Override // android.app.IUiModeManager
    public int getCurrentModeType() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.getCurrentModeType():int, file: UiModeManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.getCurrentModeType():int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.setNightMode(int):void, file: UiModeManagerService.class
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
    @Override // android.app.IUiModeManager
    public void setNightMode(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UiModeManagerService.setNightMode(int):void, file: UiModeManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.setNightMode(int):void");
    }

    static Intent buildHomeIntent(String category) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(category);
        intent.setFlags(270532608);
        return intent;
    }

    public UiModeManagerService(Context context, TwilightService twilight) {
        this.mNightMode = 1;
        this.mContext = context;
        this.mTwilightService = twilight;
        ServiceManager.addService(Context.UI_MODE_SERVICE, this);
        this.mContext.registerReceiver(this.mDockModeReceiver, new IntentFilter(Intent.ACTION_DOCK_EVENT));
        this.mContext.registerReceiver(this.mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = this.mPowerManager.newWakeLock(26, TAG);
        this.mConfiguration.setToDefaults();
        this.mDefaultUiModeType = context.getResources().getInteger(R.integer.config_defaultUiModeType);
        this.mCarModeKeepsScreenOn = context.getResources().getInteger(R.integer.config_carDockKeepsScreenOn) == 1;
        this.mDeskModeKeepsScreenOn = context.getResources().getInteger(R.integer.config_deskDockKeepsScreenOn) == 1;
        this.mTelevision = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEVISION);
        this.mNightMode = Settings.Secure.getInt(this.mContext.getContentResolver(), Settings.Secure.UI_NIGHT_MODE, 0);
        this.mTwilightService.registerListener(this.mTwilightListener, this.mHandler);
    }

    @Override // android.app.IUiModeManager
    public int getNightMode() {
        int i;
        synchronized (this.mLock) {
            i = this.mNightMode;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        synchronized (this.mLock) {
            this.mSystemReady = true;
            this.mCarModeEnabled = this.mDockState == 2;
            updateComputedNightModeLocked();
            updateLocked(0, 0);
        }
    }

    private boolean isDoingNightModeLocked() {
        return this.mCarModeEnabled || this.mDockState != 0;
    }

    private void setCarModeLocked(boolean enabled) {
        if (this.mCarModeEnabled != enabled) {
            this.mCarModeEnabled = enabled;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDockState(int newState) {
        synchronized (this.mLock) {
            if (newState != this.mDockState) {
                this.mDockState = newState;
                setCarModeLocked(this.mDockState == 2);
                if (this.mSystemReady) {
                    updateLocked(1, 0);
                }
            }
        }
    }

    private static boolean isDeskDockState(int state) {
        switch (state) {
            case 1:
            case 3:
            case 4:
                return true;
            case 2:
            default:
                return false;
        }
    }

    private void updateConfigurationLocked() {
        int uiMode;
        int uiMode2 = this.mTelevision ? 4 : this.mDefaultUiModeType;
        if (this.mCarModeEnabled) {
            uiMode2 = 3;
        } else if (isDeskDockState(this.mDockState)) {
            uiMode2 = 2;
        }
        if (this.mCarModeEnabled) {
            if (this.mNightMode == 0) {
                updateComputedNightModeLocked();
                uiMode = uiMode2 | (this.mComputedNightMode ? 32 : 16);
            } else {
                uiMode = uiMode2 | (this.mNightMode << 4);
            }
        } else {
            uiMode = (uiMode2 & (-49)) | 16;
        }
        this.mCurUiMode = uiMode;
        if (!this.mHoldingConfiguration) {
            this.mConfiguration.uiMode = uiMode;
        }
    }

    private void sendConfigurationLocked() {
        if (this.mSetUiMode != this.mConfiguration.uiMode) {
            this.mSetUiMode = this.mConfiguration.uiMode;
            try {
                ActivityManagerNative.getDefault().updateConfiguration(this.mConfiguration);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failure communicating with activity manager", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLocked(int enableFlags, int disableFlags) {
        String action = null;
        String oldAction = null;
        if (this.mLastBroadcastState == 2) {
            adjustStatusBarCarModeLocked();
            oldAction = UiModeManager.ACTION_EXIT_CAR_MODE;
        } else if (isDeskDockState(this.mLastBroadcastState)) {
            oldAction = UiModeManager.ACTION_EXIT_DESK_MODE;
        }
        if (this.mCarModeEnabled) {
            if (this.mLastBroadcastState != 2) {
                adjustStatusBarCarModeLocked();
                if (oldAction != null) {
                    this.mContext.sendBroadcastAsUser(new Intent(oldAction), UserHandle.ALL);
                }
                this.mLastBroadcastState = 2;
                action = UiModeManager.ACTION_ENTER_CAR_MODE;
            }
        } else if (isDeskDockState(this.mDockState)) {
            if (!isDeskDockState(this.mLastBroadcastState)) {
                if (oldAction != null) {
                    this.mContext.sendBroadcastAsUser(new Intent(oldAction), UserHandle.ALL);
                }
                this.mLastBroadcastState = this.mDockState;
                action = UiModeManager.ACTION_ENTER_DESK_MODE;
            }
        } else {
            this.mLastBroadcastState = 0;
            action = oldAction;
        }
        if (action != null) {
            Intent intent = new Intent(action);
            intent.putExtra("enableFlags", enableFlags);
            intent.putExtra("disableFlags", disableFlags);
            this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT, null, this.mResultReceiver, null, -1, null, null);
            this.mHoldingConfiguration = true;
            updateConfigurationLocked();
        } else {
            String category = null;
            if (this.mCarModeEnabled) {
                if ((enableFlags & 1) != 0) {
                    category = Intent.CATEGORY_CAR_DOCK;
                }
            } else if (isDeskDockState(this.mDockState)) {
                if ((enableFlags & 1) != 0) {
                    category = Intent.CATEGORY_DESK_DOCK;
                }
            } else if ((disableFlags & 1) != 0) {
                category = Intent.CATEGORY_HOME;
            }
            sendConfigurationAndStartDreamOrDockAppLocked(category);
        }
        boolean keepScreenOn = this.mCharging && ((this.mCarModeEnabled && this.mCarModeKeepsScreenOn) || (this.mCurUiMode == 2 && this.mDeskModeKeepsScreenOn));
        if (keepScreenOn != this.mWakeLock.isHeld()) {
            if (keepScreenOn) {
                this.mWakeLock.acquire();
            } else {
                this.mWakeLock.release();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAfterBroadcastLocked(String action, int enableFlags, int disableFlags) {
        String category = null;
        if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(action)) {
            if ((enableFlags & 1) != 0) {
                category = Intent.CATEGORY_CAR_DOCK;
            }
        } else if (UiModeManager.ACTION_ENTER_DESK_MODE.equals(action)) {
            if ((enableFlags & 1) != 0) {
                category = Intent.CATEGORY_DESK_DOCK;
            }
        } else if ((disableFlags & 1) != 0) {
            category = Intent.CATEGORY_HOME;
        }
        sendConfigurationAndStartDreamOrDockAppLocked(category);
    }

    private void sendConfigurationAndStartDreamOrDockAppLocked(String category) {
        this.mHoldingConfiguration = false;
        updateConfigurationLocked();
        boolean dockAppStarted = false;
        if (category != null) {
            Intent homeIntent = buildHomeIntent(category);
            if (Sandman.shouldStartDockApp(this.mContext, homeIntent)) {
                try {
                    int result = ActivityManagerNative.getDefault().startActivityWithConfig(null, null, homeIntent, null, null, null, 0, 0, this.mConfiguration, null, -2);
                    if (result >= 0) {
                        dockAppStarted = true;
                    } else if (result != -1) {
                        Slog.e(TAG, "Could not start dock app: " + homeIntent + ", startActivityWithConfig result " + result);
                    }
                } catch (RemoteException ex) {
                    Slog.e(TAG, "Could not start dock app: " + homeIntent, ex);
                }
            }
        }
        sendConfigurationLocked();
        if (category != null && !dockAppStarted) {
            Sandman.startDreamWhenDockedIfAppropriate(this.mContext);
        }
    }

    private void adjustStatusBarCarModeLocked() {
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager) this.mContext.getSystemService(Context.STATUS_BAR_SERVICE);
        }
        if (this.mStatusBarManager != null) {
            this.mStatusBarManager.disable(this.mCarModeEnabled ? 524288 : 0);
        }
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (this.mNotificationManager != null) {
            if (this.mCarModeEnabled) {
                Intent carModeOffIntent = new Intent(this.mContext, DisableCarModeActivity.class);
                Notification n = new Notification();
                n.icon = R.drawable.stat_notify_car_mode;
                n.defaults = 4;
                n.flags = 2;
                n.when = 0L;
                n.setLatestEventInfo(this.mContext, this.mContext.getString(R.string.car_mode_disable_notification_title), this.mContext.getString(R.string.car_mode_disable_notification_message), PendingIntent.getActivityAsUser(this.mContext, 0, carModeOffIntent, 0, null, UserHandle.CURRENT));
                this.mNotificationManager.notifyAsUser(null, R.string.car_mode_disable_notification_title, n, UserHandle.ALL);
                return;
            }
            this.mNotificationManager.cancelAsUser(null, R.string.car_mode_disable_notification_title, UserHandle.ALL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTwilight() {
        synchronized (this.mLock) {
            if (isDoingNightModeLocked() && this.mNightMode == 0) {
                updateComputedNightModeLocked();
                updateLocked(0, 0);
            }
        }
    }

    private void updateComputedNightModeLocked() {
        TwilightService.TwilightState state = this.mTwilightService.getCurrentState();
        if (state != null) {
            this.mComputedNightMode = state.isNight();
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump uimode service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mLock) {
            pw.println("Current UI Mode Service state:");
            pw.print("  mDockState=");
            pw.print(this.mDockState);
            pw.print(" mLastBroadcastState=");
            pw.println(this.mLastBroadcastState);
            pw.print("  mNightMode=");
            pw.print(this.mNightMode);
            pw.print(" mCarModeEnabled=");
            pw.print(this.mCarModeEnabled);
            pw.print(" mComputedNightMode=");
            pw.println(this.mComputedNightMode);
            pw.print("  mCurUiMode=0x");
            pw.print(Integer.toHexString(this.mCurUiMode));
            pw.print(" mSetUiMode=0x");
            pw.println(Integer.toHexString(this.mSetUiMode));
            pw.print("  mHoldingConfiguration=");
            pw.print(this.mHoldingConfiguration);
            pw.print(" mSystemReady=");
            pw.println(this.mSystemReady);
            pw.print("  mTwilightService.getCurrentState()=");
            pw.println(this.mTwilightService.getCurrentState());
        }
    }
}