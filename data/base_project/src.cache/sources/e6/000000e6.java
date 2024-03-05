package android.app;

import android.content.Context;
import android.os.RemoteException;
import android.os.WorkSource;

/* loaded from: AlarmManager.class */
public class AlarmManager {
    private static final String TAG = "AlarmManager";
    public static final int RTC_WAKEUP = 0;
    public static final int RTC = 1;
    public static final int ELAPSED_REALTIME_WAKEUP = 2;
    public static final int ELAPSED_REALTIME = 3;
    public static final long WINDOW_EXACT = 0;
    public static final long WINDOW_HEURISTIC = -1;
    private final IAlarmManager mService;
    private final boolean mAlwaysExact;
    @Deprecated
    public static final long INTERVAL_FIFTEEN_MINUTES = 900000;
    @Deprecated
    public static final long INTERVAL_HALF_HOUR = 1800000;
    @Deprecated
    public static final long INTERVAL_HOUR = 3600000;
    @Deprecated
    public static final long INTERVAL_HALF_DAY = 43200000;
    @Deprecated
    public static final long INTERVAL_DAY = 86400000;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlarmManager(IAlarmManager service, Context ctx) {
        this.mService = service;
        int sdkVersion = ctx.getApplicationInfo().targetSdkVersion;
        this.mAlwaysExact = sdkVersion < 19;
    }

    private long legacyExactLength() {
        return this.mAlwaysExact ? 0L : -1L;
    }

    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, legacyExactLength(), 0L, operation, null);
    }

    public void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, legacyExactLength(), intervalMillis, operation, null);
    }

    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, PendingIntent operation) {
        setImpl(type, windowStartMillis, windowLengthMillis, 0L, operation, null);
    }

    public void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, 0L, 0L, operation, null);
    }

    public void set(int type, long triggerAtMillis, long windowMillis, long intervalMillis, PendingIntent operation, WorkSource workSource) {
        setImpl(type, triggerAtMillis, windowMillis, intervalMillis, operation, workSource);
    }

    private void setImpl(int type, long triggerAtMillis, long windowMillis, long intervalMillis, PendingIntent operation, WorkSource workSource) {
        if (triggerAtMillis < 0) {
            triggerAtMillis = 0;
        }
        try {
            this.mService.set(type, triggerAtMillis, windowMillis, intervalMillis, operation, workSource);
        } catch (RemoteException e) {
        }
    }

    @Deprecated
    public void setInexactRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, -1L, intervalMillis, operation, null);
    }

    public void cancel(PendingIntent operation) {
        try {
            this.mService.remove(operation);
        } catch (RemoteException e) {
        }
    }

    public void setTime(long millis) {
        try {
            this.mService.setTime(millis);
        } catch (RemoteException e) {
        }
    }

    public void setTimeZone(String timeZone) {
        try {
            this.mService.setTimeZone(timeZone);
        } catch (RemoteException e) {
        }
    }
}