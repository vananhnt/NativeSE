package com.android.server;

import android.util.EventLog;

/* loaded from: EventLogTags.class */
public class EventLogTags {
    public static final int BATTERY_LEVEL = 2722;
    public static final int BATTERY_STATUS = 2723;
    public static final int BATTERY_DISCHARGE = 2730;
    public static final int POWER_SLEEP_REQUESTED = 2724;
    public static final int POWER_SCREEN_BROADCAST_SEND = 2725;
    public static final int POWER_SCREEN_BROADCAST_DONE = 2726;
    public static final int POWER_SCREEN_BROADCAST_STOP = 2727;
    public static final int POWER_SCREEN_STATE = 2728;
    public static final int POWER_PARTIAL_WAKE_STATE = 2729;
    public static final int FREE_STORAGE_CHANGED = 2744;
    public static final int LOW_STORAGE = 2745;
    public static final int FREE_STORAGE_LEFT = 2746;
    public static final int CACHE_FILE_DELETED = 2748;
    public static final int NOTIFICATION_ENQUEUE = 2750;
    public static final int NOTIFICATION_CANCEL = 2751;
    public static final int NOTIFICATION_CANCEL_ALL = 2752;
    public static final int WATCHDOG = 2802;
    public static final int WATCHDOG_PROC_PSS = 2803;
    public static final int WATCHDOG_SOFT_RESET = 2804;
    public static final int WATCHDOG_HARD_RESET = 2805;
    public static final int WATCHDOG_PSS_STATS = 2806;
    public static final int WATCHDOG_PROC_STATS = 2807;
    public static final int WATCHDOG_SCHEDULED_REBOOT = 2808;
    public static final int WATCHDOG_MEMINFO = 2809;
    public static final int WATCHDOG_VMSTAT = 2810;
    public static final int WATCHDOG_REQUESTED_REBOOT = 2811;
    public static final int BACKUP_DATA_CHANGED = 2820;
    public static final int BACKUP_START = 2821;
    public static final int BACKUP_TRANSPORT_FAILURE = 2822;
    public static final int BACKUP_AGENT_FAILURE = 2823;
    public static final int BACKUP_PACKAGE = 2824;
    public static final int BACKUP_SUCCESS = 2825;
    public static final int BACKUP_RESET = 2826;
    public static final int BACKUP_INITIALIZE = 2827;
    public static final int RESTORE_START = 2830;
    public static final int RESTORE_TRANSPORT_FAILURE = 2831;
    public static final int RESTORE_AGENT_FAILURE = 2832;
    public static final int RESTORE_PACKAGE = 2833;
    public static final int RESTORE_SUCCESS = 2834;
    public static final int BOOT_PROGRESS_SYSTEM_RUN = 3010;
    public static final int BOOT_PROGRESS_PMS_START = 3060;
    public static final int BOOT_PROGRESS_PMS_SYSTEM_SCAN_START = 3070;
    public static final int BOOT_PROGRESS_PMS_DATA_SCAN_START = 3080;
    public static final int BOOT_PROGRESS_PMS_SCAN_END = 3090;
    public static final int BOOT_PROGRESS_PMS_READY = 3100;
    public static final int UNKNOWN_SOURCES_ENABLED = 3110;
    public static final int WM_NO_SURFACE_MEMORY = 31000;
    public static final int IMF_FORCE_RECONNECT_IME = 32000;
    public static final int CONNECTIVITY_STATE_CHANGED = 50020;
    public static final int NETSTATS_MOBILE_SAMPLE = 51100;
    public static final int NETSTATS_WIFI_SAMPLE = 51101;
    public static final int LOCKDOWN_VPN_CONNECTING = 51200;
    public static final int LOCKDOWN_VPN_CONNECTED = 51201;
    public static final int LOCKDOWN_VPN_ERROR = 51202;
    public static final int CONFIG_INSTALL_FAILED = 51300;
    public static final int IFW_INTENT_MATCHED = 51400;
    public static final int IDLE_MAINTENANCE_WINDOW_START = 2753;
    public static final int IDLE_MAINTENANCE_WINDOW_FINISH = 2754;
    public static final int FSTRIM_START = 2755;
    public static final int FSTRIM_FINISH = 2756;

    private EventLogTags() {
    }

    public static void writeBatteryLevel(int level, int voltage, int temperature) {
        EventLog.writeEvent((int) BATTERY_LEVEL, Integer.valueOf(level), Integer.valueOf(voltage), Integer.valueOf(temperature));
    }

    public static void writeBatteryStatus(int status, int health, int present, int plugged, String technology) {
        EventLog.writeEvent((int) BATTERY_STATUS, Integer.valueOf(status), Integer.valueOf(health), Integer.valueOf(present), Integer.valueOf(plugged), technology);
    }

    public static void writeBatteryDischarge(long duration, int minlevel, int maxlevel) {
        EventLog.writeEvent((int) BATTERY_DISCHARGE, Long.valueOf(duration), Integer.valueOf(minlevel), Integer.valueOf(maxlevel));
    }

    public static void writePowerSleepRequested(int wakelockscleared) {
        EventLog.writeEvent((int) POWER_SLEEP_REQUESTED, wakelockscleared);
    }

    public static void writePowerScreenBroadcastSend(int wakelockcount) {
        EventLog.writeEvent((int) POWER_SCREEN_BROADCAST_SEND, wakelockcount);
    }

    public static void writePowerScreenBroadcastDone(int on, long broadcastduration, int wakelockcount) {
        EventLog.writeEvent((int) POWER_SCREEN_BROADCAST_DONE, Integer.valueOf(on), Long.valueOf(broadcastduration), Integer.valueOf(wakelockcount));
    }

    public static void writePowerScreenBroadcastStop(int which, int wakelockcount) {
        EventLog.writeEvent((int) POWER_SCREEN_BROADCAST_STOP, Integer.valueOf(which), Integer.valueOf(wakelockcount));
    }

    public static void writePowerScreenState(int offoron, int becauseofuser, long totaltouchdowntime, int touchcycles) {
        EventLog.writeEvent((int) POWER_SCREEN_STATE, Integer.valueOf(offoron), Integer.valueOf(becauseofuser), Long.valueOf(totaltouchdowntime), Integer.valueOf(touchcycles));
    }

    public static void writePowerPartialWakeState(int releasedoracquired, String tag) {
        EventLog.writeEvent((int) POWER_PARTIAL_WAKE_STATE, Integer.valueOf(releasedoracquired), tag);
    }

    public static void writeFreeStorageChanged(long data) {
        EventLog.writeEvent((int) FREE_STORAGE_CHANGED, data);
    }

    public static void writeLowStorage(long data) {
        EventLog.writeEvent((int) LOW_STORAGE, data);
    }

    public static void writeFreeStorageLeft(long data, long system, long cache) {
        EventLog.writeEvent((int) FREE_STORAGE_LEFT, Long.valueOf(data), Long.valueOf(system), Long.valueOf(cache));
    }

    public static void writeCacheFileDeleted(String path) {
        EventLog.writeEvent((int) CACHE_FILE_DELETED, path);
    }

    public static void writeNotificationEnqueue(String pkg, int id, String tag, int userid, String notification) {
        EventLog.writeEvent((int) NOTIFICATION_ENQUEUE, pkg, Integer.valueOf(id), tag, Integer.valueOf(userid), notification);
    }

    public static void writeNotificationCancel(String pkg, int id, String tag, int userid, int requiredFlags, int forbiddenFlags) {
        EventLog.writeEvent((int) NOTIFICATION_CANCEL, pkg, Integer.valueOf(id), tag, Integer.valueOf(userid), Integer.valueOf(requiredFlags), Integer.valueOf(forbiddenFlags));
    }

    public static void writeNotificationCancelAll(String pkg, int userid, int requiredFlags, int forbiddenFlags) {
        EventLog.writeEvent((int) NOTIFICATION_CANCEL_ALL, pkg, Integer.valueOf(userid), Integer.valueOf(requiredFlags), Integer.valueOf(forbiddenFlags));
    }

    public static void writeWatchdog(String service) {
        EventLog.writeEvent((int) WATCHDOG, service);
    }

    public static void writeWatchdogProcPss(String process, int pid, int pss) {
        EventLog.writeEvent((int) WATCHDOG_PROC_PSS, process, Integer.valueOf(pid), Integer.valueOf(pss));
    }

    public static void writeWatchdogSoftReset(String process, int pid, int maxpss, int pss, String skip) {
        EventLog.writeEvent((int) WATCHDOG_SOFT_RESET, process, Integer.valueOf(pid), Integer.valueOf(maxpss), Integer.valueOf(pss), skip);
    }

    public static void writeWatchdogHardReset(String process, int pid, int maxpss, int pss) {
        EventLog.writeEvent((int) WATCHDOG_HARD_RESET, process, Integer.valueOf(pid), Integer.valueOf(maxpss), Integer.valueOf(pss));
    }

    public static void writeWatchdogPssStats(int emptypss, int emptycount, int backgroundpss, int backgroundcount, int servicepss, int servicecount, int visiblepss, int visiblecount, int foregroundpss, int foregroundcount, int nopsscount) {
        EventLog.writeEvent((int) WATCHDOG_PSS_STATS, Integer.valueOf(emptypss), Integer.valueOf(emptycount), Integer.valueOf(backgroundpss), Integer.valueOf(backgroundcount), Integer.valueOf(servicepss), Integer.valueOf(servicecount), Integer.valueOf(visiblepss), Integer.valueOf(visiblecount), Integer.valueOf(foregroundpss), Integer.valueOf(foregroundcount), Integer.valueOf(nopsscount));
    }

    public static void writeWatchdogProcStats(int deathsinone, int deathsintwo, int deathsinthree, int deathsinfour, int deathsinfive) {
        EventLog.writeEvent((int) WATCHDOG_PROC_STATS, Integer.valueOf(deathsinone), Integer.valueOf(deathsintwo), Integer.valueOf(deathsinthree), Integer.valueOf(deathsinfour), Integer.valueOf(deathsinfive));
    }

    public static void writeWatchdogScheduledReboot(long now, int interval, int starttime, int window, String skip) {
        EventLog.writeEvent((int) WATCHDOG_SCHEDULED_REBOOT, Long.valueOf(now), Integer.valueOf(interval), Integer.valueOf(starttime), Integer.valueOf(window), skip);
    }

    public static void writeWatchdogMeminfo(int memfree, int buffers, int cached, int active, int inactive, int anonpages, int mapped, int slab, int sreclaimable, int sunreclaim, int pagetables) {
        EventLog.writeEvent((int) WATCHDOG_MEMINFO, Integer.valueOf(memfree), Integer.valueOf(buffers), Integer.valueOf(cached), Integer.valueOf(active), Integer.valueOf(inactive), Integer.valueOf(anonpages), Integer.valueOf(mapped), Integer.valueOf(slab), Integer.valueOf(sreclaimable), Integer.valueOf(sunreclaim), Integer.valueOf(pagetables));
    }

    public static void writeWatchdogVmstat(long runtime, int pgfree, int pgactivate, int pgdeactivate, int pgfault, int pgmajfault) {
        EventLog.writeEvent((int) WATCHDOG_VMSTAT, Long.valueOf(runtime), Integer.valueOf(pgfree), Integer.valueOf(pgactivate), Integer.valueOf(pgdeactivate), Integer.valueOf(pgfault), Integer.valueOf(pgmajfault));
    }

    public static void writeWatchdogRequestedReboot(int nowait, int scheduleinterval, int recheckinterval, int starttime, int window, int minscreenoff, int minnextalarm) {
        EventLog.writeEvent((int) WATCHDOG_REQUESTED_REBOOT, Integer.valueOf(nowait), Integer.valueOf(scheduleinterval), Integer.valueOf(recheckinterval), Integer.valueOf(starttime), Integer.valueOf(window), Integer.valueOf(minscreenoff), Integer.valueOf(minnextalarm));
    }

    public static void writeBackupDataChanged(String package_) {
        EventLog.writeEvent((int) BACKUP_DATA_CHANGED, package_);
    }

    public static void writeBackupStart(String transport) {
        EventLog.writeEvent((int) BACKUP_START, transport);
    }

    public static void writeBackupTransportFailure(String package_) {
        EventLog.writeEvent((int) BACKUP_TRANSPORT_FAILURE, package_);
    }

    public static void writeBackupAgentFailure(String package_, String message) {
        EventLog.writeEvent((int) BACKUP_AGENT_FAILURE, package_, message);
    }

    public static void writeBackupPackage(String package_, int size) {
        EventLog.writeEvent((int) BACKUP_PACKAGE, package_, Integer.valueOf(size));
    }

    public static void writeBackupSuccess(int packages, int time) {
        EventLog.writeEvent((int) BACKUP_SUCCESS, Integer.valueOf(packages), Integer.valueOf(time));
    }

    public static void writeBackupReset(String transport) {
        EventLog.writeEvent((int) BACKUP_RESET, transport);
    }

    public static void writeBackupInitialize() {
        EventLog.writeEvent((int) BACKUP_INITIALIZE, new Object[0]);
    }

    public static void writeRestoreStart(String transport, long source) {
        EventLog.writeEvent((int) RESTORE_START, transport, Long.valueOf(source));
    }

    public static void writeRestoreTransportFailure() {
        EventLog.writeEvent((int) RESTORE_TRANSPORT_FAILURE, new Object[0]);
    }

    public static void writeRestoreAgentFailure(String package_, String message) {
        EventLog.writeEvent(2832, package_, message);
    }

    public static void writeRestorePackage(String package_, int size) {
        EventLog.writeEvent(2833, package_, Integer.valueOf(size));
    }

    public static void writeRestoreSuccess(int packages, int time) {
        EventLog.writeEvent(2834, Integer.valueOf(packages), Integer.valueOf(time));
    }

    public static void writeBootProgressSystemRun(long time) {
        EventLog.writeEvent(3010, time);
    }

    public static void writeBootProgressPmsStart(long time) {
        EventLog.writeEvent((int) BOOT_PROGRESS_PMS_START, time);
    }

    public static void writeBootProgressPmsSystemScanStart(long time) {
        EventLog.writeEvent((int) BOOT_PROGRESS_PMS_SYSTEM_SCAN_START, time);
    }

    public static void writeBootProgressPmsDataScanStart(long time) {
        EventLog.writeEvent((int) BOOT_PROGRESS_PMS_DATA_SCAN_START, time);
    }

    public static void writeBootProgressPmsScanEnd(long time) {
        EventLog.writeEvent((int) BOOT_PROGRESS_PMS_SCAN_END, time);
    }

    public static void writeBootProgressPmsReady(long time) {
        EventLog.writeEvent((int) BOOT_PROGRESS_PMS_READY, time);
    }

    public static void writeUnknownSourcesEnabled(int value) {
        EventLog.writeEvent((int) UNKNOWN_SOURCES_ENABLED, value);
    }

    public static void writeWmNoSurfaceMemory(String window, int pid, String operation) {
        EventLog.writeEvent((int) WM_NO_SURFACE_MEMORY, window, Integer.valueOf(pid), operation);
    }

    public static void writeImfForceReconnectIme(Object[] ime, long timeSinceConnect, int showing) {
        EventLog.writeEvent(32000, ime, Long.valueOf(timeSinceConnect), Integer.valueOf(showing));
    }

    public static void writeConnectivityStateChanged(int type, int subtype, int state) {
        EventLog.writeEvent((int) CONNECTIVITY_STATE_CHANGED, Integer.valueOf(type), Integer.valueOf(subtype), Integer.valueOf(state));
    }

    public static void writeNetstatsMobileSample(long devRxBytes, long devTxBytes, long devRxPkts, long devTxPkts, long xtRxBytes, long xtTxBytes, long xtRxPkts, long xtTxPkts, long uidRxBytes, long uidTxBytes, long uidRxPkts, long uidTxPkts, long trustedTime) {
        EventLog.writeEvent((int) NETSTATS_MOBILE_SAMPLE, Long.valueOf(devRxBytes), Long.valueOf(devTxBytes), Long.valueOf(devRxPkts), Long.valueOf(devTxPkts), Long.valueOf(xtRxBytes), Long.valueOf(xtTxBytes), Long.valueOf(xtRxPkts), Long.valueOf(xtTxPkts), Long.valueOf(uidRxBytes), Long.valueOf(uidTxBytes), Long.valueOf(uidRxPkts), Long.valueOf(uidTxPkts), Long.valueOf(trustedTime));
    }

    public static void writeNetstatsWifiSample(long devRxBytes, long devTxBytes, long devRxPkts, long devTxPkts, long xtRxBytes, long xtTxBytes, long xtRxPkts, long xtTxPkts, long uidRxBytes, long uidTxBytes, long uidRxPkts, long uidTxPkts, long trustedTime) {
        EventLog.writeEvent((int) NETSTATS_WIFI_SAMPLE, Long.valueOf(devRxBytes), Long.valueOf(devTxBytes), Long.valueOf(devRxPkts), Long.valueOf(devTxPkts), Long.valueOf(xtRxBytes), Long.valueOf(xtTxBytes), Long.valueOf(xtRxPkts), Long.valueOf(xtTxPkts), Long.valueOf(uidRxBytes), Long.valueOf(uidTxBytes), Long.valueOf(uidRxPkts), Long.valueOf(uidTxPkts), Long.valueOf(trustedTime));
    }

    public static void writeLockdownVpnConnecting(int egressNet) {
        EventLog.writeEvent((int) LOCKDOWN_VPN_CONNECTING, egressNet);
    }

    public static void writeLockdownVpnConnected(int egressNet) {
        EventLog.writeEvent((int) LOCKDOWN_VPN_CONNECTED, egressNet);
    }

    public static void writeLockdownVpnError(int egressNet) {
        EventLog.writeEvent((int) LOCKDOWN_VPN_ERROR, egressNet);
    }

    public static void writeConfigInstallFailed(String dir) {
        EventLog.writeEvent((int) CONFIG_INSTALL_FAILED, dir);
    }

    public static void writeIfwIntentMatched(int intentType, String componentName, int callerUid, int callerPkgCount, String callerPkgs, String action, String mimeType, String uri, int flags) {
        EventLog.writeEvent((int) IFW_INTENT_MATCHED, Integer.valueOf(intentType), componentName, Integer.valueOf(callerUid), Integer.valueOf(callerPkgCount), callerPkgs, action, mimeType, uri, Integer.valueOf(flags));
    }

    public static void writeIdleMaintenanceWindowStart(long time, long lastuseractivity, int batterylevel, int batterycharging) {
        EventLog.writeEvent((int) IDLE_MAINTENANCE_WINDOW_START, Long.valueOf(time), Long.valueOf(lastuseractivity), Integer.valueOf(batterylevel), Integer.valueOf(batterycharging));
    }

    public static void writeIdleMaintenanceWindowFinish(long time, long lastuseractivity, int batterylevel, int batterycharging) {
        EventLog.writeEvent((int) IDLE_MAINTENANCE_WINDOW_FINISH, Long.valueOf(time), Long.valueOf(lastuseractivity), Integer.valueOf(batterylevel), Integer.valueOf(batterycharging));
    }

    public static void writeFstrimStart(long time) {
        EventLog.writeEvent((int) FSTRIM_START, time);
    }

    public static void writeFstrimFinish(long time) {
        EventLog.writeEvent((int) FSTRIM_FINISH, time);
    }
}