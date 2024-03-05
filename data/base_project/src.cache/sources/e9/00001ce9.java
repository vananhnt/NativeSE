package com.android.server;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Slog;
import com.android.internal.os.SamplingProfilerIntegration;
import dalvik.system.VMRuntime;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: SystemServer.class */
public class SystemServer {
    private static final String TAG = "SystemServer";
    public static final int FACTORY_TEST_OFF = 0;
    public static final int FACTORY_TEST_LOW_LEVEL = 1;
    public static final int FACTORY_TEST_HIGH_LEVEL = 2;
    static Timer timer;
    static final long SNAPSHOT_INTERVAL = 3600000;
    private static final long EARLIEST_SUPPORTED_TIME = 86400000;

    private static native void nativeInit();

    public static void main(String[] args) {
        if (System.currentTimeMillis() < 86400000) {
            Slog.w(TAG, "System clock is before 1970; setting to 1970.");
            SystemClock.setCurrentTimeMillis(86400000L);
        }
        if (SamplingProfilerIntegration.isEnabled()) {
            SamplingProfilerIntegration.start();
            timer = new Timer();
            timer.schedule(new TimerTask() { // from class: com.android.server.SystemServer.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    SamplingProfilerIntegration.writeSnapshot("system_server", null);
                }
            }, 3600000L, 3600000L);
        }
        VMRuntime.getRuntime().clearGrowthLimit();
        VMRuntime.getRuntime().setTargetHeapUtilization(0.8f);
        Environment.setUserRequired(true);
        System.loadLibrary("android_servers");
        Slog.i(TAG, "Entered the Android system server!");
        nativeInit();
        ServerThread thr = new ServerThread();
        thr.initAndLoop();
    }
}