package com.android.server.am;

import android.util.EventLog;

/* loaded from: EventLogTags.class */
public class EventLogTags {
    public static final int CONFIGURATION_CHANGED = 2719;
    public static final int CPU = 2721;
    public static final int BOOT_PROGRESS_AMS_READY = 3040;
    public static final int BOOT_PROGRESS_ENABLE_SCREEN = 3050;
    public static final int AM_FINISH_ACTIVITY = 30001;
    public static final int AM_TASK_TO_FRONT = 30002;
    public static final int AM_NEW_INTENT = 30003;
    public static final int AM_CREATE_TASK = 30004;
    public static final int AM_CREATE_ACTIVITY = 30005;
    public static final int AM_RESTART_ACTIVITY = 30006;
    public static final int AM_RESUME_ACTIVITY = 30007;
    public static final int AM_ANR = 30008;
    public static final int AM_ACTIVITY_LAUNCH_TIME = 30009;
    public static final int AM_PROC_BOUND = 30010;
    public static final int AM_PROC_DIED = 30011;
    public static final int AM_FAILED_TO_PAUSE = 30012;
    public static final int AM_PAUSE_ACTIVITY = 30013;
    public static final int AM_PROC_START = 30014;
    public static final int AM_PROC_BAD = 30015;
    public static final int AM_PROC_GOOD = 30016;
    public static final int AM_LOW_MEMORY = 30017;
    public static final int AM_DESTROY_ACTIVITY = 30018;
    public static final int AM_RELAUNCH_RESUME_ACTIVITY = 30019;
    public static final int AM_RELAUNCH_ACTIVITY = 30020;
    public static final int AM_ON_PAUSED_CALLED = 30021;
    public static final int AM_ON_RESUME_CALLED = 30022;
    public static final int AM_KILL = 30023;
    public static final int AM_BROADCAST_DISCARD_FILTER = 30024;
    public static final int AM_BROADCAST_DISCARD_APP = 30025;
    public static final int AM_CREATE_SERVICE = 30030;
    public static final int AM_DESTROY_SERVICE = 30031;
    public static final int AM_PROCESS_CRASHED_TOO_MUCH = 30032;
    public static final int AM_DROP_PROCESS = 30033;
    public static final int AM_SERVICE_CRASHED_TOO_MUCH = 30034;
    public static final int AM_SCHEDULE_SERVICE_RESTART = 30035;
    public static final int AM_PROVIDER_LOST_PROCESS = 30036;
    public static final int AM_PROCESS_START_TIMEOUT = 30037;
    public static final int AM_CRASH = 30039;
    public static final int AM_WTF = 30040;
    public static final int AM_SWITCH_USER = 30041;
    public static final int AM_ACTIVITY_FULLY_DRAWN_TIME = 30042;

    private EventLogTags() {
    }

    public static void writeConfigurationChanged(int configMask) {
        EventLog.writeEvent((int) CONFIGURATION_CHANGED, configMask);
    }

    public static void writeCpu(int total, int user, int system, int iowait, int irq, int softirq) {
        EventLog.writeEvent((int) CPU, Integer.valueOf(total), Integer.valueOf(user), Integer.valueOf(system), Integer.valueOf(iowait), Integer.valueOf(irq), Integer.valueOf(softirq));
    }

    public static void writeBootProgressAmsReady(long time) {
        EventLog.writeEvent(3040, time);
    }

    public static void writeBootProgressEnableScreen(long time) {
        EventLog.writeEvent((int) BOOT_PROGRESS_ENABLE_SCREEN, time);
    }

    public static void writeAmFinishActivity(int user, int token, int taskId, String componentName, String reason) {
        EventLog.writeEvent((int) AM_FINISH_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName, reason);
    }

    public static void writeAmTaskToFront(int user, int task) {
        EventLog.writeEvent((int) AM_TASK_TO_FRONT, Integer.valueOf(user), Integer.valueOf(task));
    }

    public static void writeAmNewIntent(int user, int token, int taskId, String componentName, String action, String mimeType, String uri, int flags) {
        EventLog.writeEvent((int) AM_NEW_INTENT, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName, action, mimeType, uri, Integer.valueOf(flags));
    }

    public static void writeAmCreateTask(int user, int taskId) {
        EventLog.writeEvent((int) AM_CREATE_TASK, Integer.valueOf(user), Integer.valueOf(taskId));
    }

    public static void writeAmCreateActivity(int user, int token, int taskId, String componentName, String action, String mimeType, String uri, int flags) {
        EventLog.writeEvent((int) AM_CREATE_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName, action, mimeType, uri, Integer.valueOf(flags));
    }

    public static void writeAmRestartActivity(int user, int token, int taskId, String componentName) {
        EventLog.writeEvent((int) AM_RESTART_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName);
    }

    public static void writeAmResumeActivity(int user, int token, int taskId, String componentName) {
        EventLog.writeEvent((int) AM_RESUME_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName);
    }

    public static void writeAmAnr(int user, int pid, String packageName, int flags, String reason) {
        EventLog.writeEvent((int) AM_ANR, Integer.valueOf(user), Integer.valueOf(pid), packageName, Integer.valueOf(flags), reason);
    }

    public static void writeAmActivityLaunchTime(int user, int token, String componentName, long time) {
        EventLog.writeEvent((int) AM_ACTIVITY_LAUNCH_TIME, Integer.valueOf(user), Integer.valueOf(token), componentName, Long.valueOf(time));
    }

    public static void writeAmProcBound(int user, int pid, String processName) {
        EventLog.writeEvent((int) AM_PROC_BOUND, Integer.valueOf(user), Integer.valueOf(pid), processName);
    }

    public static void writeAmProcDied(int user, int pid, String processName) {
        EventLog.writeEvent((int) AM_PROC_DIED, Integer.valueOf(user), Integer.valueOf(pid), processName);
    }

    public static void writeAmFailedToPause(int user, int token, String wantingToPause, String currentlyPausing) {
        EventLog.writeEvent((int) AM_FAILED_TO_PAUSE, Integer.valueOf(user), Integer.valueOf(token), wantingToPause, currentlyPausing);
    }

    public static void writeAmPauseActivity(int user, int token, String componentName) {
        EventLog.writeEvent((int) AM_PAUSE_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), componentName);
    }

    public static void writeAmProcStart(int user, int pid, int uid, String processName, String type, String component) {
        EventLog.writeEvent((int) AM_PROC_START, Integer.valueOf(user), Integer.valueOf(pid), Integer.valueOf(uid), processName, type, component);
    }

    public static void writeAmProcBad(int user, int uid, String processName) {
        EventLog.writeEvent((int) AM_PROC_BAD, Integer.valueOf(user), Integer.valueOf(uid), processName);
    }

    public static void writeAmProcGood(int user, int uid, String processName) {
        EventLog.writeEvent((int) AM_PROC_GOOD, Integer.valueOf(user), Integer.valueOf(uid), processName);
    }

    public static void writeAmLowMemory(int numProcesses) {
        EventLog.writeEvent((int) AM_LOW_MEMORY, numProcesses);
    }

    public static void writeAmDestroyActivity(int user, int token, int taskId, String componentName, String reason) {
        EventLog.writeEvent((int) AM_DESTROY_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName, reason);
    }

    public static void writeAmRelaunchResumeActivity(int user, int token, int taskId, String componentName) {
        EventLog.writeEvent((int) AM_RELAUNCH_RESUME_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName);
    }

    public static void writeAmRelaunchActivity(int user, int token, int taskId, String componentName) {
        EventLog.writeEvent((int) AM_RELAUNCH_ACTIVITY, Integer.valueOf(user), Integer.valueOf(token), Integer.valueOf(taskId), componentName);
    }

    public static void writeAmOnPausedCalled(int user, String componentName) {
        EventLog.writeEvent((int) AM_ON_PAUSED_CALLED, Integer.valueOf(user), componentName);
    }

    public static void writeAmOnResumeCalled(int user, String componentName) {
        EventLog.writeEvent((int) AM_ON_RESUME_CALLED, Integer.valueOf(user), componentName);
    }

    public static void writeAmKill(int user, int pid, String processName, int oomadj, String reason) {
        EventLog.writeEvent((int) AM_KILL, Integer.valueOf(user), Integer.valueOf(pid), processName, Integer.valueOf(oomadj), reason);
    }

    public static void writeAmBroadcastDiscardFilter(int user, int broadcast, String action, int receiverNumber, int broadcastfilter) {
        EventLog.writeEvent((int) AM_BROADCAST_DISCARD_FILTER, Integer.valueOf(user), Integer.valueOf(broadcast), action, Integer.valueOf(receiverNumber), Integer.valueOf(broadcastfilter));
    }

    public static void writeAmBroadcastDiscardApp(int user, int broadcast, String action, int receiverNumber, String app) {
        EventLog.writeEvent((int) AM_BROADCAST_DISCARD_APP, Integer.valueOf(user), Integer.valueOf(broadcast), action, Integer.valueOf(receiverNumber), app);
    }

    public static void writeAmCreateService(int user, int serviceRecord, String name, int uid, int pid) {
        EventLog.writeEvent((int) AM_CREATE_SERVICE, Integer.valueOf(user), Integer.valueOf(serviceRecord), name, Integer.valueOf(uid), Integer.valueOf(pid));
    }

    public static void writeAmDestroyService(int user, int serviceRecord, int pid) {
        EventLog.writeEvent((int) AM_DESTROY_SERVICE, Integer.valueOf(user), Integer.valueOf(serviceRecord), Integer.valueOf(pid));
    }

    public static void writeAmProcessCrashedTooMuch(int user, String name, int pid) {
        EventLog.writeEvent((int) AM_PROCESS_CRASHED_TOO_MUCH, Integer.valueOf(user), name, Integer.valueOf(pid));
    }

    public static void writeAmDropProcess(int pid) {
        EventLog.writeEvent((int) AM_DROP_PROCESS, pid);
    }

    public static void writeAmServiceCrashedTooMuch(int user, int crashCount, String componentName, int pid) {
        EventLog.writeEvent((int) AM_SERVICE_CRASHED_TOO_MUCH, Integer.valueOf(user), Integer.valueOf(crashCount), componentName, Integer.valueOf(pid));
    }

    public static void writeAmScheduleServiceRestart(int user, String componentName, long time) {
        EventLog.writeEvent((int) AM_SCHEDULE_SERVICE_RESTART, Integer.valueOf(user), componentName, Long.valueOf(time));
    }

    public static void writeAmProviderLostProcess(int user, String packageName, int uid, String name) {
        EventLog.writeEvent((int) AM_PROVIDER_LOST_PROCESS, Integer.valueOf(user), packageName, Integer.valueOf(uid), name);
    }

    public static void writeAmProcessStartTimeout(int user, int pid, int uid, String processName) {
        EventLog.writeEvent((int) AM_PROCESS_START_TIMEOUT, Integer.valueOf(user), Integer.valueOf(pid), Integer.valueOf(uid), processName);
    }

    public static void writeAmCrash(int user, int pid, String processName, int flags, String exception, String message, String file, int line) {
        EventLog.writeEvent((int) AM_CRASH, Integer.valueOf(user), Integer.valueOf(pid), processName, Integer.valueOf(flags), exception, message, file, Integer.valueOf(line));
    }

    public static void writeAmWtf(int user, int pid, String processName, int flags, String tag, String message) {
        EventLog.writeEvent((int) AM_WTF, Integer.valueOf(user), Integer.valueOf(pid), processName, Integer.valueOf(flags), tag, message);
    }

    public static void writeAmSwitchUser(int id) {
        EventLog.writeEvent((int) AM_SWITCH_USER, id);
    }

    public static void writeAmActivityFullyDrawnTime(int user, int token, String componentName, long time) {
        EventLog.writeEvent((int) AM_ACTIVITY_FULLY_DRAWN_TIME, Integer.valueOf(user), Integer.valueOf(token), componentName, Long.valueOf(time));
    }
}