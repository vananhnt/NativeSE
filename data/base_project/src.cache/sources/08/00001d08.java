package com.android.server;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.os.BatteryStats;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IVibratorService;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.Slog;
import android.view.InputDevice;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/* loaded from: VibratorService.class */
public class VibratorService extends IVibratorService.Stub implements InputManager.InputDeviceListener {
    private static final String TAG = "VibratorService";
    private final LinkedList<Vibration> mVibrations;
    private Vibration mCurrentVibration;
    private final Context mContext;
    private final PowerManager.WakeLock mWakeLock;
    private final IAppOpsService mAppOpsService;
    private final IBatteryStats mBatteryStatsService;
    private InputManager mIm;
    volatile VibrateThread mThread;
    private boolean mVibrateInputDevicesSetting;
    private boolean mInputDeviceListenerRegistered;
    private final WorkSource mTmpWorkSource = new WorkSource();
    private final Handler mH = new Handler();
    private final ArrayList<Vibrator> mInputDeviceVibrators = new ArrayList<>();
    private int mCurVibUid = -1;
    private final Runnable mVibrationRunnable = new Runnable() { // from class: com.android.server.VibratorService.3
        @Override // java.lang.Runnable
        public void run() {
            synchronized (VibratorService.this.mVibrations) {
                VibratorService.this.doCancelVibrateLocked();
                VibratorService.this.startNextVibrationLocked();
            }
        }
    };
    BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.VibratorService.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                synchronized (VibratorService.this.mVibrations) {
                    VibratorService.this.doCancelVibrateLocked();
                    int size = VibratorService.this.mVibrations.size();
                    for (int i = 0; i < size; i++) {
                        VibratorService.this.unlinkVibration((Vibration) VibratorService.this.mVibrations.get(i));
                    }
                    VibratorService.this.mVibrations.clear();
                }
            }
        }
    };

    static native boolean vibratorExists();

    static native void vibratorOn(long j);

    static native void vibratorOff();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.VibratorService.vibrate(int, java.lang.String, long, android.os.IBinder):void, file: VibratorService.class
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
    @Override // android.os.IVibratorService
    public void vibrate(int r1, java.lang.String r2, long r3, android.os.IBinder r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.VibratorService.vibrate(int, java.lang.String, long, android.os.IBinder):void, file: VibratorService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.vibrate(int, java.lang.String, long, android.os.IBinder):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.VibratorService.vibratePattern(int, java.lang.String, long[], int, android.os.IBinder):void, file: VibratorService.class
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
    @Override // android.os.IVibratorService
    public void vibratePattern(int r1, java.lang.String r2, long[] r3, int r4, android.os.IBinder r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.VibratorService.vibratePattern(int, java.lang.String, long[], int, android.os.IBinder):void, file: VibratorService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.vibratePattern(int, java.lang.String, long[], int, android.os.IBinder):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.VibratorService.cancelVibrate(android.os.IBinder):void, file: VibratorService.class
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
    @Override // android.os.IVibratorService
    public void cancelVibrate(android.os.IBinder r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.VibratorService.cancelVibrate(android.os.IBinder):void, file: VibratorService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.cancelVibrate(android.os.IBinder):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VibratorService$Vibration.class */
    public class Vibration implements IBinder.DeathRecipient {
        private final IBinder mToken;
        private final long mTimeout;
        private final long mStartTime;
        private final long[] mPattern;
        private final int mRepeat;
        private final int mUid;
        private final String mPackageName;

        Vibration(VibratorService vibratorService, IBinder token, long millis, int uid, String packageName) {
            this(token, millis, null, 0, uid, packageName);
        }

        Vibration(VibratorService vibratorService, IBinder token, long[] pattern, int repeat, int uid, String packageName) {
            this(token, 0L, pattern, repeat, uid, packageName);
        }

        private Vibration(IBinder token, long millis, long[] pattern, int repeat, int uid, String packageName) {
            this.mToken = token;
            this.mTimeout = millis;
            this.mStartTime = SystemClock.uptimeMillis();
            this.mPattern = pattern;
            this.mRepeat = repeat;
            this.mUid = uid;
            this.mPackageName = packageName;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (VibratorService.this.mVibrations) {
                VibratorService.this.mVibrations.remove(this);
                if (this == VibratorService.this.mCurrentVibration) {
                    VibratorService.this.doCancelVibrateLocked();
                    VibratorService.this.startNextVibrationLocked();
                }
            }
        }

        public boolean hasLongerTimeout(long millis) {
            if (this.mTimeout == 0 || this.mStartTime + this.mTimeout < SystemClock.uptimeMillis() + millis) {
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VibratorService(Context context) {
        vibratorOff();
        this.mContext = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, "*vibrator*");
        this.mWakeLock.setReferenceCounted(true);
        this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService(Context.APP_OPS_SERVICE));
        this.mBatteryStatsService = IBatteryStats.Stub.asInterface(ServiceManager.getService(BatteryStats.SERVICE_NAME));
        this.mVibrations = new LinkedList<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(this.mIntentReceiver, filter);
    }

    public void systemReady() {
        this.mIm = (InputManager) this.mContext.getSystemService(Context.INPUT_SERVICE);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VIBRATE_INPUT_DEVICES), true, new ContentObserver(this.mH) { // from class: com.android.server.VibratorService.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                VibratorService.this.updateInputDeviceVibrators();
            }
        }, -1);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.VibratorService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                VibratorService.this.updateInputDeviceVibrators();
            }
        }, new IntentFilter(Intent.ACTION_USER_SWITCHED), null, this.mH);
        updateInputDeviceVibrators();
    }

    @Override // android.os.IVibratorService
    public boolean hasVibrator() {
        return doVibratorExists();
    }

    private void verifyIncomingUid(int uid) {
        if (uid == Binder.getCallingUid() || Binder.getCallingPid() == Process.myPid()) {
            return;
        }
        this.mContext.enforcePermission(Manifest.permission.UPDATE_APP_OPS_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
    }

    private boolean isAll0(long[] pattern) {
        int N = pattern.length;
        for (int i = 0; i < N; i++) {
            if (pattern[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doCancelVibrateLocked() {
        if (this.mThread != null) {
            synchronized (this.mThread) {
                this.mThread.mDone = true;
                this.mThread.notify();
            }
            this.mThread = null;
        }
        doVibratorOff();
        this.mH.removeCallbacks(this.mVibrationRunnable);
        reportFinishVibrationLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNextVibrationLocked() {
        if (this.mVibrations.size() <= 0) {
            reportFinishVibrationLocked();
            this.mCurrentVibration = null;
            return;
        }
        this.mCurrentVibration = this.mVibrations.getFirst();
        startVibrationLocked(this.mCurrentVibration);
    }

    private void startVibrationLocked(Vibration vib) {
        try {
            int mode = this.mAppOpsService.startOperation(AppOpsManager.getToken(this.mAppOpsService), 3, vib.mUid, vib.mPackageName);
            if (mode != 0) {
                if (mode == 2) {
                    Slog.w(TAG, "Would be an error: vibrate from uid " + vib.mUid);
                }
                this.mH.post(this.mVibrationRunnable);
                return;
            }
        } catch (RemoteException e) {
        }
        if (vib.mTimeout != 0) {
            doVibratorOn(vib.mTimeout, vib.mUid);
            this.mH.postDelayed(this.mVibrationRunnable, vib.mTimeout);
            return;
        }
        this.mThread = new VibrateThread(vib);
        this.mThread.start();
    }

    private void reportFinishVibrationLocked() {
        if (this.mCurrentVibration != null) {
            try {
                this.mAppOpsService.finishOperation(AppOpsManager.getToken(this.mAppOpsService), 3, this.mCurrentVibration.mUid, this.mCurrentVibration.mPackageName);
            } catch (RemoteException e) {
            }
            this.mCurrentVibration = null;
        }
    }

    private Vibration removeVibrationLocked(IBinder token) {
        ListIterator<Vibration> iter = this.mVibrations.listIterator(0);
        while (iter.hasNext()) {
            Vibration vib = iter.next();
            if (vib.mToken == token) {
                iter.remove();
                unlinkVibration(vib);
                return vib;
            }
        }
        if (this.mCurrentVibration != null && this.mCurrentVibration.mToken == token) {
            unlinkVibration(this.mCurrentVibration);
            return this.mCurrentVibration;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unlinkVibration(Vibration vib) {
        if (vib.mPattern != null) {
            vib.mToken.unlinkToDeath(vib, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInputDeviceVibrators() {
        synchronized (this.mVibrations) {
            doCancelVibrateLocked();
            synchronized (this.mInputDeviceVibrators) {
                this.mVibrateInputDevicesSetting = false;
                try {
                    this.mVibrateInputDevicesSetting = Settings.System.getIntForUser(this.mContext.getContentResolver(), Settings.System.VIBRATE_INPUT_DEVICES, -2) > 0;
                } catch (Settings.SettingNotFoundException e) {
                }
                if (this.mVibrateInputDevicesSetting) {
                    if (!this.mInputDeviceListenerRegistered) {
                        this.mInputDeviceListenerRegistered = true;
                        this.mIm.registerInputDeviceListener(this, this.mH);
                    }
                } else if (this.mInputDeviceListenerRegistered) {
                    this.mInputDeviceListenerRegistered = false;
                    this.mIm.unregisterInputDeviceListener(this);
                }
                this.mInputDeviceVibrators.clear();
                if (this.mVibrateInputDevicesSetting) {
                    int[] ids = this.mIm.getInputDeviceIds();
                    for (int i : ids) {
                        InputDevice device = this.mIm.getInputDevice(i);
                        Vibrator vibrator = device.getVibrator();
                        if (vibrator.hasVibrator()) {
                            this.mInputDeviceVibrators.add(vibrator);
                        }
                    }
                }
            }
            startNextVibrationLocked();
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int deviceId) {
        updateInputDeviceVibrators();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int deviceId) {
        updateInputDeviceVibrators();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int deviceId) {
        updateInputDeviceVibrators();
    }

    private boolean doVibratorExists() {
        return vibratorExists();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doVibratorOn(long millis, int uid) {
        synchronized (this.mInputDeviceVibrators) {
            try {
                this.mBatteryStatsService.noteVibratorOn(uid, millis);
                this.mCurVibUid = uid;
            } catch (RemoteException e) {
            }
            int vibratorCount = this.mInputDeviceVibrators.size();
            if (vibratorCount != 0) {
                for (int i = 0; i < vibratorCount; i++) {
                    this.mInputDeviceVibrators.get(i).vibrate(millis);
                }
            } else {
                vibratorOn(millis);
            }
        }
    }

    private void doVibratorOff() {
        synchronized (this.mInputDeviceVibrators) {
            if (this.mCurVibUid >= 0) {
                try {
                    this.mBatteryStatsService.noteVibratorOff(this.mCurVibUid);
                } catch (RemoteException e) {
                }
                this.mCurVibUid = -1;
            }
            int vibratorCount = this.mInputDeviceVibrators.size();
            if (vibratorCount != 0) {
                for (int i = 0; i < vibratorCount; i++) {
                    this.mInputDeviceVibrators.get(i).cancel();
                }
            } else {
                vibratorOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VibratorService$VibrateThread.class */
    public class VibrateThread extends Thread {
        final Vibration mVibration;
        boolean mDone;

        VibrateThread(Vibration vib) {
            this.mVibration = vib;
            VibratorService.this.mTmpWorkSource.set(vib.mUid);
            VibratorService.this.mWakeLock.setWorkSource(VibratorService.this.mTmpWorkSource);
            VibratorService.this.mWakeLock.acquire();
        }

        private void delay(long duration) {
            if (duration > 0) {
                long bedtime = duration + SystemClock.uptimeMillis();
                do {
                    try {
                        wait(duration);
                    } catch (InterruptedException e) {
                    }
                    if (!this.mDone) {
                        duration = bedtime - SystemClock.uptimeMillis();
                    } else {
                        return;
                    }
                } while (duration > 0);
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Process.setThreadPriority(-8);
            synchronized (this) {
                long[] pattern = this.mVibration.mPattern;
                int len = pattern.length;
                int repeat = this.mVibration.mRepeat;
                int uid = this.mVibration.mUid;
                int index = 0;
                long duration = 0;
                while (!this.mDone) {
                    if (index < len) {
                        int i = index;
                        index++;
                        duration += pattern[i];
                    }
                    delay(duration);
                    if (this.mDone) {
                        break;
                    } else if (index < len) {
                        int i2 = index;
                        index++;
                        duration = pattern[i2];
                        if (duration > 0) {
                            VibratorService.this.doVibratorOn(duration, uid);
                        }
                    } else if (repeat < 0) {
                        break;
                    } else {
                        index = repeat;
                        duration = 0;
                    }
                }
                VibratorService.this.mWakeLock.release();
            }
            synchronized (VibratorService.this.mVibrations) {
                if (VibratorService.this.mThread == this) {
                    VibratorService.this.mThread = null;
                }
                if (!this.mDone) {
                    VibratorService.this.mVibrations.remove(this.mVibration);
                    VibratorService.this.unlinkVibration(this.mVibration);
                    VibratorService.this.startNextVibrationLocked();
                }
            }
        }
    }
}