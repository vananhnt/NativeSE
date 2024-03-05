package com.android.server;

import android.Manifest;
import android.app.ApplicationErrorReport;
import android.app.IActivityController;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.ActivityRecord;
import com.android.server.am.ProcessRecord;
import com.android.server.power.PowerManagerService;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: Watchdog.class */
public class Watchdog extends Thread {
    static final String TAG = "Watchdog";
    static final boolean localLOGV = false;
    static final boolean DB = false;
    static final boolean RECORD_KERNEL_THREADS = true;
    static final int TIME_TO_WAIT = 30000;
    static final String[] NATIVE_STACKS_OF_INTEREST = {"/system/bin/mediaserver", "/system/bin/sdcard", "/system/bin/surfaceflinger"};
    static Watchdog sWatchdog;
    final ArrayList<HandlerChecker> mHandlerCheckers;
    final HandlerChecker mMonitorChecker;
    ContentResolver mResolver;
    BatteryService mBattery;
    PowerManagerService mPower;
    AlarmManagerService mAlarm;
    ActivityManagerService mActivity;
    int mPhonePid;
    IActivityController mController;
    boolean mAllowRestart;

    /* loaded from: Watchdog$Monitor.class */
    public interface Monitor {
        void monitor();
    }

    private native void native_dumpKernelStacks(String str);

    /* loaded from: Watchdog$HandlerChecker.class */
    public final class HandlerChecker implements Runnable {
        private final Handler mHandler;
        private final String mName;
        private final ArrayList<Monitor> mMonitors = new ArrayList<>();
        private boolean mCompleted;
        private Monitor mCurrentMonitor;

        HandlerChecker(Handler handler, String name) {
            this.mHandler = handler;
            this.mName = name;
        }

        public void addMonitor(Monitor monitor) {
            this.mMonitors.add(monitor);
        }

        public void scheduleCheckLocked() {
            if (this.mMonitors.size() == 0 && this.mHandler.getLooper().isIdling()) {
                this.mCompleted = true;
                return;
            }
            this.mCompleted = false;
            this.mCurrentMonitor = null;
            this.mHandler.postAtFrontOfQueue(this);
        }

        public boolean isCompletedLocked() {
            return this.mCompleted;
        }

        public Thread getThread() {
            return this.mHandler.getLooper().getThread();
        }

        public String getName() {
            return this.mName;
        }

        public String describeBlockedStateLocked() {
            if (this.mCurrentMonitor == null) {
                return "Blocked in handler on " + this.mName + " (" + getThread().getName() + Separators.RPAREN;
            }
            return "Blocked in monitor " + this.mCurrentMonitor.getClass().getName() + " on " + this.mName + " (" + getThread().getName() + Separators.RPAREN;
        }

        @Override // java.lang.Runnable
        public void run() {
            int size = this.mMonitors.size();
            for (int i = 0; i < size; i++) {
                synchronized (Watchdog.this) {
                    this.mCurrentMonitor = this.mMonitors.get(i);
                }
                this.mCurrentMonitor.monitor();
            }
            synchronized (Watchdog.this) {
                this.mCompleted = true;
                this.mCurrentMonitor = null;
            }
        }
    }

    /* loaded from: Watchdog$RebootRequestReceiver.class */
    final class RebootRequestReceiver extends BroadcastReceiver {
        RebootRequestReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context c, Intent intent) {
            if (intent.getIntExtra("nowait", 0) != 0) {
                Watchdog.this.rebootSystem("Received ACTION_REBOOT broadcast");
            } else {
                Slog.w(Watchdog.TAG, "Unsupported ACTION_REBOOT broadcast: " + intent);
            }
        }
    }

    public static Watchdog getInstance() {
        if (sWatchdog == null) {
            sWatchdog = new Watchdog();
        }
        return sWatchdog;
    }

    private Watchdog() {
        super("watchdog");
        this.mHandlerCheckers = new ArrayList<>();
        this.mAllowRestart = true;
        this.mMonitorChecker = new HandlerChecker(FgThread.getHandler(), "foreground thread");
        this.mHandlerCheckers.add(this.mMonitorChecker);
        this.mHandlerCheckers.add(new HandlerChecker(new Handler(Looper.getMainLooper()), "main thread"));
        this.mHandlerCheckers.add(new HandlerChecker(UiThread.getHandler(), "ui thread"));
        this.mHandlerCheckers.add(new HandlerChecker(IoThread.getHandler(), "i/o thread"));
    }

    public void init(Context context, BatteryService battery, PowerManagerService power, AlarmManagerService alarm, ActivityManagerService activity) {
        this.mResolver = context.getContentResolver();
        this.mBattery = battery;
        this.mPower = power;
        this.mAlarm = alarm;
        this.mActivity = activity;
        context.registerReceiver(new RebootRequestReceiver(), new IntentFilter(Intent.ACTION_REBOOT), Manifest.permission.REBOOT, null);
    }

    public void processStarted(String name, int pid) {
        synchronized (this) {
            if ("com.android.phone".equals(name)) {
                this.mPhonePid = pid;
            }
        }
    }

    public void setActivityController(IActivityController controller) {
        synchronized (this) {
            this.mController = controller;
        }
    }

    public void setAllowRestart(boolean allowRestart) {
        synchronized (this) {
            this.mAllowRestart = allowRestart;
        }
    }

    public void addMonitor(Monitor monitor) {
        synchronized (this) {
            if (isAlive()) {
                throw new RuntimeException("Monitors can't be added once the Watchdog is running");
            }
            this.mMonitorChecker.addMonitor(monitor);
        }
    }

    public void addThread(Handler thread, String name) {
        synchronized (this) {
            if (isAlive()) {
                throw new RuntimeException("Threads can't be added once the Watchdog is running");
            }
            this.mHandlerCheckers.add(new HandlerChecker(thread, name));
        }
    }

    void rebootSystem(String reason) {
        Slog.i(TAG, "Rebooting system because: " + reason);
        PowerManagerService pms = (PowerManagerService) ServiceManager.getService(Context.POWER_SERVICE);
        pms.reboot(false, reason, false);
    }

    private boolean haveAllCheckersCompletedLocked() {
        for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
            HandlerChecker hc = this.mHandlerCheckers.get(i);
            if (!hc.isCompletedLocked()) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<HandlerChecker> getBlockedCheckersLocked() {
        ArrayList<HandlerChecker> checkers = new ArrayList<>();
        for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
            HandlerChecker hc = this.mHandlerCheckers.get(i);
            if (!hc.isCompletedLocked()) {
                checkers.add(hc);
            }
        }
        return checkers;
    }

    private String describeCheckersLocked(ArrayList<HandlerChecker> checkers) {
        StringBuilder builder = new StringBuilder(128);
        for (int i = 0; i < checkers.size(); i++) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(checkers.get(i).describeBlockedStateLocked());
        }
        return builder.toString();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        IActivityController controller;
        boolean waitedHalf = false;
        while (true) {
            synchronized (this) {
                if (!waitedHalf) {
                    for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
                        HandlerChecker hc = this.mHandlerCheckers.get(i);
                        hc.scheduleCheckLocked();
                    }
                }
                long start = SystemClock.uptimeMillis();
                for (long timeout = 30000; timeout > 0; timeout = LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS - (SystemClock.uptimeMillis() - start)) {
                    try {
                        wait(timeout);
                    } catch (InterruptedException e) {
                        Log.wtf(TAG, e);
                    }
                }
                if (haveAllCheckersCompletedLocked()) {
                    waitedHalf = false;
                } else if (!waitedHalf) {
                    ArrayList<Integer> pids = new ArrayList<>();
                    pids.add(Integer.valueOf(Process.myPid()));
                    ActivityManagerService.dumpStackTraces(true, pids, (ProcessCpuTracker) null, (SparseArray) null, NATIVE_STACKS_OF_INTEREST);
                    waitedHalf = true;
                } else {
                    ArrayList<HandlerChecker> blockedCheckers = getBlockedCheckersLocked();
                    final String subject = describeCheckersLocked(blockedCheckers);
                    boolean allowRestart = this.mAllowRestart;
                    EventLog.writeEvent((int) EventLogTags.WATCHDOG, subject);
                    ArrayList<Integer> pids2 = new ArrayList<>();
                    pids2.add(Integer.valueOf(Process.myPid()));
                    if (this.mPhonePid > 0) {
                        pids2.add(Integer.valueOf(this.mPhonePid));
                    }
                    final File stack = ActivityManagerService.dumpStackTraces(!waitedHalf, pids2, (ProcessCpuTracker) null, (SparseArray) null, NATIVE_STACKS_OF_INTEREST);
                    SystemClock.sleep(2000L);
                    dumpKernelStackTraces();
                    try {
                        FileWriter sysrq_trigger = new FileWriter("/proc/sysrq-trigger");
                        sysrq_trigger.write("w");
                        sysrq_trigger.close();
                    } catch (IOException e2) {
                        Slog.e(TAG, "Failed to write to /proc/sysrq-trigger");
                        Slog.e(TAG, e2.getMessage());
                    }
                    Thread dropboxThread = new Thread("watchdogWriteToDropbox") { // from class: com.android.server.Watchdog.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            Watchdog.this.mActivity.addErrorToDropBox("watchdog", (ProcessRecord) null, "system_server", (ActivityRecord) null, (ActivityRecord) null, subject, (String) null, stack, (ApplicationErrorReport.CrashInfo) null);
                        }
                    };
                    dropboxThread.start();
                    try {
                        dropboxThread.join(2000L);
                    } catch (InterruptedException e3) {
                    }
                    synchronized (this) {
                        controller = this.mController;
                    }
                    if (controller != null) {
                        Slog.i(TAG, "Reporting stuck state to activity controller");
                        try {
                            Binder.setDumpDisabled("Service dumps disabled due to hung system process.");
                            int res = controller.systemNotResponding(subject);
                            if (res >= 0) {
                                Slog.i(TAG, "Activity controller requested to coninue to wait");
                                waitedHalf = false;
                            }
                        } catch (RemoteException e4) {
                        }
                    }
                    if (Debug.isDebuggerConnected()) {
                        Slog.w(TAG, "Debugger connected: Watchdog is *not* killing the system process");
                    } else if (!allowRestart) {
                        Slog.w(TAG, "Restart not allowed: Watchdog is *not* killing the system process");
                    } else {
                        Slog.w(TAG, "*** WATCHDOG KILLING SYSTEM PROCESS: " + subject);
                        for (int i2 = 0; i2 < blockedCheckers.size(); i2++) {
                            Slog.w(TAG, blockedCheckers.get(i2).getName() + " stack trace:");
                            StackTraceElement[] stackTrace = blockedCheckers.get(i2).getThread().getStackTrace();
                            for (StackTraceElement element : stackTrace) {
                                Slog.w(TAG, "    at " + element);
                            }
                        }
                        Slog.w(TAG, "*** GOODBYE!");
                        Process.killProcess(Process.myPid());
                        System.exit(10);
                    }
                    waitedHalf = false;
                }
            }
        }
    }

    private File dumpKernelStackTraces() {
        String tracesPath = SystemProperties.get("dalvik.vm.stack-trace-file", null);
        if (tracesPath == null || tracesPath.length() == 0) {
            return null;
        }
        native_dumpKernelStacks(tracesPath);
        return new File(tracesPath);
    }
}