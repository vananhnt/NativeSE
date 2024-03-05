package com.android.internal.os;

import android.os.IBinder;
import android.os.SystemClock;
import android.util.EventLog;
import java.lang.ref.WeakReference;

/* loaded from: BinderInternal.class */
public class BinderInternal {
    static WeakReference<GcWatcher> mGcWatcher = new WeakReference<>(new GcWatcher());
    static long mLastGcTime;

    public static final native void joinThreadPool();

    public static final native IBinder getContextObject();

    public static final native void disableBackgroundScheduling(boolean z);

    static final native void handleGc();

    /* loaded from: BinderInternal$GcWatcher.class */
    static final class GcWatcher {
        GcWatcher() {
        }

        protected void finalize() throws Throwable {
            BinderInternal.handleGc();
            BinderInternal.mLastGcTime = SystemClock.uptimeMillis();
            BinderInternal.mGcWatcher = new WeakReference<>(new GcWatcher());
        }
    }

    public static long getLastGcTime() {
        return mLastGcTime;
    }

    public static void forceGc(String reason) {
        EventLog.writeEvent(2741, reason);
        Runtime.getRuntime().gc();
    }

    static void forceBinderGc() {
        forceGc("Binder");
    }
}