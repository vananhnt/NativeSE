package android.os;

import android.content.Context;
import android.util.Log;
import com.android.internal.R;

/* loaded from: PowerManager.class */
public final class PowerManager {
    private static final String TAG = "PowerManager";
    public static final int PARTIAL_WAKE_LOCK = 1;
    @Deprecated
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    @Deprecated
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
    @Deprecated
    public static final int FULL_WAKE_LOCK = 26;
    public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int WAKE_LOCK_LEVEL_MASK = 65535;
    public static final int ACQUIRE_CAUSES_WAKEUP = 268435456;
    public static final int ON_AFTER_RELEASE = 536870912;
    public static final int WAIT_FOR_PROXIMITY_NEGATIVE = 1;
    public static final int BRIGHTNESS_ON = 255;
    public static final int BRIGHTNESS_OFF = 0;
    public static final int USER_ACTIVITY_EVENT_OTHER = 0;
    public static final int USER_ACTIVITY_EVENT_BUTTON = 1;
    public static final int USER_ACTIVITY_EVENT_TOUCH = 2;
    public static final int USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS = 1;
    public static final int GO_TO_SLEEP_REASON_USER = 0;
    public static final int GO_TO_SLEEP_REASON_DEVICE_ADMIN = 1;
    public static final int GO_TO_SLEEP_REASON_TIMEOUT = 2;
    final Context mContext;
    final IPowerManager mService;
    final Handler mHandler;

    public PowerManager(Context context, IPowerManager service, Handler handler) {
        this.mContext = context;
        this.mService = service;
        this.mHandler = handler;
    }

    public int getMinimumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(R.integer.config_screenBrightnessSettingMinimum);
    }

    public int getMaximumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(R.integer.config_screenBrightnessSettingMaximum);
    }

    public int getDefaultScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(R.integer.config_screenBrightnessSettingDefault);
    }

    public static boolean useScreenAutoBrightnessAdjustmentFeature() {
        return SystemProperties.getBoolean("persist.power.useautobrightadj", false);
    }

    public static boolean useTwilightAdjustmentFeature() {
        return SystemProperties.getBoolean("persist.power.usetwilightadj", false);
    }

    public WakeLock newWakeLock(int levelAndFlags, String tag) {
        validateWakeLockParameters(levelAndFlags, tag);
        return new WakeLock(levelAndFlags, tag, this.mContext.getOpPackageName());
    }

    public static void validateWakeLockParameters(int levelAndFlags, String tag) {
        switch (levelAndFlags & 65535) {
            case 1:
            case 6:
            case 10:
            case 26:
            case 32:
                if (tag == null) {
                    throw new IllegalArgumentException("The tag must not be null.");
                }
                return;
            default:
                throw new IllegalArgumentException("Must specify a valid wake lock level.");
        }
    }

    public void userActivity(long when, boolean noChangeLights) {
        try {
            this.mService.userActivity(when, 0, noChangeLights ? 1 : 0);
        } catch (RemoteException e) {
        }
    }

    public void goToSleep(long time) {
        try {
            this.mService.goToSleep(time, 0);
        } catch (RemoteException e) {
        }
    }

    public void wakeUp(long time) {
        try {
            this.mService.wakeUp(time);
        } catch (RemoteException e) {
        }
    }

    public void nap(long time) {
        try {
            this.mService.nap(time);
        } catch (RemoteException e) {
        }
    }

    public void setBacklightBrightness(int brightness) {
        try {
            this.mService.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
        }
    }

    public boolean isWakeLockLevelSupported(int level) {
        try {
            return this.mService.isWakeLockLevelSupported(level);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isScreenOn() {
        try {
            return this.mService.isScreenOn();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void reboot(String reason) {
        try {
            this.mService.reboot(false, reason, true);
        } catch (RemoteException e) {
        }
    }

    /* loaded from: PowerManager$WakeLock.class */
    public final class WakeLock {
        private final int mFlags;
        private final String mTag;
        private final String mPackageName;
        private int mCount;
        private boolean mHeld;
        private WorkSource mWorkSource;
        private boolean mRefCounted = true;
        private final Runnable mReleaser = new Runnable() { // from class: android.os.PowerManager.WakeLock.1
            @Override // java.lang.Runnable
            public void run() {
                WakeLock.this.release();
            }
        };
        private final IBinder mToken = new Binder();

        WakeLock(int flags, String tag, String packageName) {
            this.mFlags = flags;
            this.mTag = tag;
            this.mPackageName = packageName;
        }

        protected void finalize() throws Throwable {
            synchronized (this.mToken) {
                if (this.mHeld) {
                    Log.wtf(PowerManager.TAG, "WakeLock finalized while still held: " + this.mTag);
                    try {
                        PowerManager.this.mService.releaseWakeLock(this.mToken, 0);
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public void setReferenceCounted(boolean value) {
            synchronized (this.mToken) {
                this.mRefCounted = value;
            }
        }

        public void acquire() {
            synchronized (this.mToken) {
                acquireLocked();
            }
        }

        public void acquire(long timeout) {
            synchronized (this.mToken) {
                acquireLocked();
                PowerManager.this.mHandler.postDelayed(this.mReleaser, timeout);
            }
        }

        private void acquireLocked() {
            if (this.mRefCounted) {
                int i = this.mCount;
                this.mCount = i + 1;
                if (i != 0) {
                    return;
                }
            }
            PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
            try {
                PowerManager.this.mService.acquireWakeLock(this.mToken, this.mFlags, this.mTag, this.mPackageName, this.mWorkSource);
            } catch (RemoteException e) {
            }
            this.mHeld = true;
        }

        public void release() {
            release(0);
        }

        /* JADX WARN: Code restructure failed: missing block: B:7:0x0019, code lost:
            if (r1 == 0) goto L17;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void release(int r6) {
            /*
                r5 = this;
                r0 = r5
                android.os.IBinder r0 = r0.mToken
                r1 = r0
                r7 = r1
                monitor-enter(r0)
                r0 = r5
                boolean r0 = r0.mRefCounted     // Catch: java.lang.Throwable -> L75
                if (r0 == 0) goto L1c
                r0 = r5
                r1 = r0
                int r1 = r1.mCount     // Catch: java.lang.Throwable -> L75
                r2 = 1
                int r1 = r1 - r2
                r2 = r1; r1 = r0; r0 = r2;      // Catch: java.lang.Throwable -> L75
                r1.mCount = r2     // Catch: java.lang.Throwable -> L75
                if (r0 != 0) goto L4b
            L1c:
                r0 = r5
                android.os.PowerManager r0 = android.os.PowerManager.this     // Catch: java.lang.Throwable -> L75
                android.os.Handler r0 = r0.mHandler     // Catch: java.lang.Throwable -> L75
                r1 = r5
                java.lang.Runnable r1 = r1.mReleaser     // Catch: java.lang.Throwable -> L75
                r0.removeCallbacks(r1)     // Catch: java.lang.Throwable -> L75
                r0 = r5
                boolean r0 = r0.mHeld     // Catch: java.lang.Throwable -> L75
                if (r0 == 0) goto L4b
                r0 = r5
                android.os.PowerManager r0 = android.os.PowerManager.this     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L75
                android.os.IPowerManager r0 = r0.mService     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L75
                r1 = r5
                android.os.IBinder r1 = r1.mToken     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L75
                r2 = r6
                r0.releaseWakeLock(r1, r2)     // Catch: android.os.RemoteException -> L45 java.lang.Throwable -> L75
                goto L46
            L45:
                r8 = move-exception
            L46:
                r0 = r5
                r1 = 0
                r0.mHeld = r1     // Catch: java.lang.Throwable -> L75
            L4b:
                r0 = r5
                int r0 = r0.mCount     // Catch: java.lang.Throwable -> L75
                if (r0 >= 0) goto L70
                java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L75
                r1 = r0
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L75
                r3 = r2
                r3.<init>()     // Catch: java.lang.Throwable -> L75
                java.lang.String r3 = "WakeLock under-locked "
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L75
                r3 = r5
                java.lang.String r3 = r3.mTag     // Catch: java.lang.Throwable -> L75
                java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> L75
                java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L75
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L75
                throw r0     // Catch: java.lang.Throwable -> L75
            L70:
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L75
                goto L7c
            L75:
                r9 = move-exception
                r0 = r7
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L75
                r0 = r9
                throw r0
            L7c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.PowerManager.WakeLock.release(int):void");
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mToken) {
                z = this.mHeld;
            }
            return z;
        }

        public void setWorkSource(WorkSource ws) {
            boolean changed;
            synchronized (this.mToken) {
                if (ws != null) {
                    if (ws.size() == 0) {
                        ws = null;
                    }
                }
                if (ws == null) {
                    changed = this.mWorkSource != null;
                    this.mWorkSource = null;
                } else if (this.mWorkSource == null) {
                    changed = true;
                    this.mWorkSource = new WorkSource(ws);
                } else {
                    changed = this.mWorkSource.diff(ws);
                    if (changed) {
                        this.mWorkSource.set(ws);
                    }
                }
                if (changed && this.mHeld) {
                    try {
                        PowerManager.this.mService.updateWakeLockWorkSource(this.mToken, this.mWorkSource);
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public String toString() {
            String str;
            synchronized (this.mToken) {
                str = "WakeLock{" + Integer.toHexString(System.identityHashCode(this)) + " held=" + this.mHeld + ", refCount=" + this.mCount + "}";
            }
            return str;
        }
    }
}