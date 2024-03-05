package com.android.server.am;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.IApplicationThread;
import android.app.IInstrumentationWatcher;
import android.app.IUiAutomationConnection;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.PrintWriterPrinter;
import android.util.TimeUtils;
import com.android.internal.app.ProcessStats;
import com.android.internal.os.BatteryStatsImpl;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ProcessRecord.class */
public final class ProcessRecord {
    final BatteryStatsImpl.Uid.Proc batteryStats;
    final ApplicationInfo info;
    final boolean isolated;
    final int uid;
    final int userId;
    final String processName;
    IApplicationThread thread;
    ProcessStats.ProcessState baseProcessTracker;
    int pid;
    boolean starting;
    long lastActivityTime;
    long lastPssTime;
    long nextPssTime;
    long lastStateTime;
    long initialIdlePss;
    long lastPss;
    long lastCachedPss;
    int maxAdj;
    int curRawAdj;
    int setRawAdj;
    int curAdj;
    int setAdj;
    int curSchedGroup;
    int setSchedGroup;
    int trimMemoryLevel;
    int memImportance;
    boolean serviceb;
    boolean serviceHighRam;
    boolean keeping;
    boolean setIsForeground;
    boolean notCachedSinceIdle;
    boolean hasActivities;
    boolean hasClientActivities;
    boolean hasStartedServices;
    boolean foregroundServices;
    boolean foregroundActivities;
    boolean systemNoUi;
    boolean hasShownUi;
    boolean pendingUiClean;
    boolean hasAboveClient;
    boolean bad;
    boolean killedByAm;
    boolean procStateChanged;
    String waitingToKill;
    IBinder forcingToForeground;
    int adjSeq;
    int lruSeq;
    CompatibilityInfo compat;
    IBinder.DeathRecipient deathRecipient;
    ComponentName instrumentationClass;
    ApplicationInfo instrumentationInfo;
    String instrumentationProfileFile;
    IInstrumentationWatcher instrumentationWatcher;
    IUiAutomationConnection instrumentationUiAutomationConnection;
    Bundle instrumentationArguments;
    ComponentName instrumentationResultClass;
    boolean usingWrapper;
    BroadcastRecord curReceiver;
    long lastWakeTime;
    long lastCpuTime;
    long curCpuTime;
    long lastRequestedGc;
    long lastLowMemory;
    boolean reportLowMemory;
    boolean empty;
    boolean cached;
    String adjType;
    int adjTypeCode;
    Object adjSource;
    int adjSourceOom;
    Object adjTarget;
    boolean execServicesFg;
    boolean persistent;
    boolean crashing;
    Dialog crashDialog;
    boolean forceCrashReport;
    boolean notResponding;
    Dialog anrDialog;
    boolean removed;
    boolean debugging;
    boolean waitedForDebugger;
    Dialog waitDialog;
    String shortStringName;
    String stringName;
    ActivityManager.ProcessErrorStateInfo crashingReport;
    ActivityManager.ProcessErrorStateInfo notRespondingReport;
    ComponentName errorReportReceiver;
    final ArrayMap<String, ProcessStats.ProcessState> pkgList = new ArrayMap<>();
    int curProcState = -1;
    int repProcState = -1;
    int setProcState = -1;
    int pssProcState = -1;
    final ArrayList<ActivityRecord> activities = new ArrayList<>();
    final ArraySet<ServiceRecord> services = new ArraySet<>();
    final ArraySet<ServiceRecord> executingServices = new ArraySet<>();
    final ArraySet<ConnectionRecord> connections = new ArraySet<>();
    final ArraySet<ReceiverList> receivers = new ArraySet<>();
    final ArrayMap<String, ContentProviderRecord> pubProviders = new ArrayMap<>();
    final ArrayList<ContentProviderConnection> conProviders = new ArrayList<>();

    void dump(PrintWriter pw, String prefix) {
        long wtime;
        long now = SystemClock.uptimeMillis();
        pw.print(prefix);
        pw.print("user #");
        pw.print(this.userId);
        pw.print(" uid=");
        pw.print(this.info.uid);
        if (this.uid != this.info.uid) {
            pw.print(" ISOLATED uid=");
            pw.print(this.uid);
        }
        pw.println();
        if (this.info.className != null) {
            pw.print(prefix);
            pw.print("class=");
            pw.println(this.info.className);
        }
        if (this.info.manageSpaceActivityName != null) {
            pw.print(prefix);
            pw.print("manageSpaceActivityName=");
            pw.println(this.info.manageSpaceActivityName);
        }
        pw.print(prefix);
        pw.print("dir=");
        pw.print(this.info.sourceDir);
        pw.print(" publicDir=");
        pw.print(this.info.publicSourceDir);
        pw.print(" data=");
        pw.println(this.info.dataDir);
        pw.print(prefix);
        pw.print("packageList={");
        for (int i = 0; i < this.pkgList.size(); i++) {
            if (i > 0) {
                pw.print(", ");
            }
            pw.print(this.pkgList.keyAt(i));
        }
        pw.println("}");
        pw.print(prefix);
        pw.print("compat=");
        pw.println(this.compat);
        if (this.instrumentationClass != null || this.instrumentationProfileFile != null || this.instrumentationArguments != null) {
            pw.print(prefix);
            pw.print("instrumentationClass=");
            pw.print(this.instrumentationClass);
            pw.print(" instrumentationProfileFile=");
            pw.println(this.instrumentationProfileFile);
            pw.print(prefix);
            pw.print("instrumentationArguments=");
            pw.println(this.instrumentationArguments);
            pw.print(prefix);
            pw.print("instrumentationInfo=");
            pw.println(this.instrumentationInfo);
            if (this.instrumentationInfo != null) {
                this.instrumentationInfo.dump(new PrintWriterPrinter(pw), prefix + "  ");
            }
        }
        pw.print(prefix);
        pw.print("thread=");
        pw.println(this.thread);
        pw.print(prefix);
        pw.print("pid=");
        pw.print(this.pid);
        pw.print(" starting=");
        pw.println(this.starting);
        pw.print(prefix);
        pw.print("lastActivityTime=");
        TimeUtils.formatDuration(this.lastActivityTime, now, pw);
        pw.print(" lastPssTime=");
        TimeUtils.formatDuration(this.lastPssTime, now, pw);
        pw.print(" nextPssTime=");
        TimeUtils.formatDuration(this.nextPssTime, now, pw);
        pw.println();
        pw.print(prefix);
        pw.print("adjSeq=");
        pw.print(this.adjSeq);
        pw.print(" lruSeq=");
        pw.print(this.lruSeq);
        pw.print(" lastPss=");
        pw.print(this.lastPss);
        pw.print(" lastCachedPss=");
        pw.println(this.lastCachedPss);
        pw.print(prefix);
        pw.print("keeping=");
        pw.print(this.keeping);
        pw.print(" cached=");
        pw.print(this.cached);
        pw.print(" empty=");
        pw.println(this.empty);
        if (this.serviceb) {
            pw.print(prefix);
            pw.print("serviceb=");
            pw.print(this.serviceb);
            pw.print(" serviceHighRam=");
            pw.println(this.serviceHighRam);
        }
        if (this.notCachedSinceIdle) {
            pw.print(prefix);
            pw.print("notCachedSinceIdle=");
            pw.print(this.notCachedSinceIdle);
            pw.print(" initialIdlePss=");
            pw.println(this.initialIdlePss);
        }
        pw.print(prefix);
        pw.print("oom: max=");
        pw.print(this.maxAdj);
        pw.print(" curRaw=");
        pw.print(this.curRawAdj);
        pw.print(" setRaw=");
        pw.print(this.setRawAdj);
        pw.print(" cur=");
        pw.print(this.curAdj);
        pw.print(" set=");
        pw.println(this.setAdj);
        pw.print(prefix);
        pw.print("curSchedGroup=");
        pw.print(this.curSchedGroup);
        pw.print(" setSchedGroup=");
        pw.print(this.setSchedGroup);
        pw.print(" systemNoUi=");
        pw.print(this.systemNoUi);
        pw.print(" trimMemoryLevel=");
        pw.println(this.trimMemoryLevel);
        pw.print(prefix);
        pw.print("curProcState=");
        pw.print(this.curProcState);
        pw.print(" repProcState=");
        pw.print(this.repProcState);
        pw.print(" pssProcState=");
        pw.print(this.pssProcState);
        pw.print(" setProcState=");
        pw.print(this.setProcState);
        pw.print(" lastStateTime=");
        TimeUtils.formatDuration(this.lastStateTime, now, pw);
        pw.println();
        if (this.hasShownUi || this.pendingUiClean || this.hasAboveClient) {
            pw.print(prefix);
            pw.print("hasShownUi=");
            pw.print(this.hasShownUi);
            pw.print(" pendingUiClean=");
            pw.print(this.pendingUiClean);
            pw.print(" hasAboveClient=");
            pw.println(this.hasAboveClient);
        }
        if (this.setIsForeground || this.foregroundServices || this.forcingToForeground != null) {
            pw.print(prefix);
            pw.print("setIsForeground=");
            pw.print(this.setIsForeground);
            pw.print(" foregroundServices=");
            pw.print(this.foregroundServices);
            pw.print(" forcingToForeground=");
            pw.println(this.forcingToForeground);
        }
        if (this.persistent || this.removed) {
            pw.print(prefix);
            pw.print("persistent=");
            pw.print(this.persistent);
            pw.print(" removed=");
            pw.println(this.removed);
        }
        if (this.hasActivities || this.hasClientActivities || this.foregroundActivities) {
            pw.print(prefix);
            pw.print("hasActivities=");
            pw.print(this.hasActivities);
            pw.print(" hasClientActivities=");
            pw.print(this.hasClientActivities);
            pw.print(" foregroundActivities=");
            pw.println(this.foregroundActivities);
        }
        if (this.hasStartedServices) {
            pw.print(prefix);
            pw.print("hasStartedServices=");
            pw.println(this.hasStartedServices);
        }
        if (!this.keeping) {
            synchronized (this.batteryStats.getBatteryStats()) {
                wtime = this.batteryStats.getBatteryStats().getProcessWakeTime(this.info.uid, this.pid, SystemClock.elapsedRealtime());
            }
            long timeUsed = wtime - this.lastWakeTime;
            pw.print(prefix);
            pw.print("lastWakeTime=");
            pw.print(this.lastWakeTime);
            pw.print(" timeUsed=");
            TimeUtils.formatDuration(timeUsed, pw);
            pw.println("");
            pw.print(prefix);
            pw.print("lastCpuTime=");
            pw.print(this.lastCpuTime);
            pw.print(" timeUsed=");
            TimeUtils.formatDuration(this.curCpuTime - this.lastCpuTime, pw);
            pw.println("");
        }
        pw.print(prefix);
        pw.print("lastRequestedGc=");
        TimeUtils.formatDuration(this.lastRequestedGc, now, pw);
        pw.print(" lastLowMemory=");
        TimeUtils.formatDuration(this.lastLowMemory, now, pw);
        pw.print(" reportLowMemory=");
        pw.println(this.reportLowMemory);
        if (this.killedByAm || this.waitingToKill != null) {
            pw.print(prefix);
            pw.print("killedByAm=");
            pw.print(this.killedByAm);
            pw.print(" waitingToKill=");
            pw.println(this.waitingToKill);
        }
        if (this.debugging || this.crashing || this.crashDialog != null || this.notResponding || this.anrDialog != null || this.bad) {
            pw.print(prefix);
            pw.print("debugging=");
            pw.print(this.debugging);
            pw.print(" crashing=");
            pw.print(this.crashing);
            pw.print(Separators.SP);
            pw.print(this.crashDialog);
            pw.print(" notResponding=");
            pw.print(this.notResponding);
            pw.print(Separators.SP);
            pw.print(this.anrDialog);
            pw.print(" bad=");
            pw.print(this.bad);
            if (this.errorReportReceiver != null) {
                pw.print(" errorReportReceiver=");
                pw.print(this.errorReportReceiver.flattenToShortString());
            }
            pw.println();
        }
        if (this.activities.size() > 0) {
            pw.print(prefix);
            pw.println("Activities:");
            for (int i2 = 0; i2 < this.activities.size(); i2++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.activities.get(i2));
            }
        }
        if (this.services.size() > 0) {
            pw.print(prefix);
            pw.println("Services:");
            for (int i3 = 0; i3 < this.services.size(); i3++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.services.valueAt(i3));
            }
        }
        if (this.executingServices.size() > 0) {
            pw.print(prefix);
            pw.print("Executing Services (fg=");
            pw.print(this.execServicesFg);
            pw.println(Separators.RPAREN);
            for (int i4 = 0; i4 < this.executingServices.size(); i4++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.executingServices.valueAt(i4));
            }
        }
        if (this.connections.size() > 0) {
            pw.print(prefix);
            pw.println("Connections:");
            for (int i5 = 0; i5 < this.connections.size(); i5++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.connections.valueAt(i5));
            }
        }
        if (this.pubProviders.size() > 0) {
            pw.print(prefix);
            pw.println("Published Providers:");
            for (int i6 = 0; i6 < this.pubProviders.size(); i6++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.pubProviders.keyAt(i6));
                pw.print(prefix);
                pw.print("    -> ");
                pw.println(this.pubProviders.valueAt(i6));
            }
        }
        if (this.conProviders.size() > 0) {
            pw.print(prefix);
            pw.println("Connected Providers:");
            for (int i7 = 0; i7 < this.conProviders.size(); i7++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.conProviders.get(i7).toShortString());
            }
        }
        if (this.curReceiver != null) {
            pw.print(prefix);
            pw.print("curReceiver=");
            pw.println(this.curReceiver);
        }
        if (this.receivers.size() > 0) {
            pw.print(prefix);
            pw.println("Receivers:");
            for (int i8 = 0; i8 < this.receivers.size(); i8++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.receivers.valueAt(i8));
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v2, types: [com.android.server.am.ProcessRecord, long] */
    ProcessRecord(BatteryStatsImpl.Uid.Proc _batteryStats, ApplicationInfo _info, String _processName, int _uid) {
        this.batteryStats = _batteryStats;
        this.info = _info;
        this.isolated = _info.uid != _uid;
        this.uid = _uid;
        this.userId = UserHandle.getUserId(_uid);
        this.processName = _processName;
        this.pkgList.put(_info.packageName, null);
        this.maxAdj = 16;
        this.setRawAdj = -100;
        this.curRawAdj = -100;
        this.setAdj = -100;
        this.curAdj = -100;
        this.persistent = false;
        this.removed = false;
        ?? uptimeMillis = SystemClock.uptimeMillis();
        this.nextPssTime = uptimeMillis;
        this.lastPssTime = uptimeMillis;
        uptimeMillis.lastStateTime = this;
    }

    public void setPid(int _pid) {
        this.pid = _pid;
        this.shortStringName = null;
        this.stringName = null;
    }

    public void makeActive(IApplicationThread _thread, ProcessStatsService tracker) {
        if (this.thread == null) {
            ProcessStats.ProcessState origBase = this.baseProcessTracker;
            if (origBase != null) {
                origBase.setState(-1, tracker.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList);
                origBase.makeInactive();
            }
            this.baseProcessTracker = tracker.getProcessStateLocked(this.info.packageName, this.info.uid, this.processName);
            this.baseProcessTracker.makeActive();
            for (int i = 0; i < this.pkgList.size(); i++) {
                ProcessStats.ProcessState ps = this.pkgList.valueAt(i);
                if (ps != null && ps != origBase) {
                    ps.makeInactive();
                }
                ProcessStats.ProcessState ps2 = tracker.getProcessStateLocked(this.pkgList.keyAt(i), this.info.uid, this.processName);
                if (ps2 != this.baseProcessTracker) {
                    ps2.makeActive();
                }
                this.pkgList.setValueAt(i, ps2);
            }
        }
        this.thread = _thread;
    }

    public void makeInactive(ProcessStatsService tracker) {
        this.thread = null;
        ProcessStats.ProcessState origBase = this.baseProcessTracker;
        if (origBase != null) {
            if (origBase != null) {
                origBase.setState(-1, tracker.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList);
                origBase.makeInactive();
            }
            this.baseProcessTracker = null;
            for (int i = 0; i < this.pkgList.size(); i++) {
                ProcessStats.ProcessState ps = this.pkgList.valueAt(i);
                if (ps != null && ps != origBase) {
                    ps.makeInactive();
                }
                this.pkgList.setValueAt(i, null);
            }
        }
    }

    public boolean isInterestingToUserLocked() {
        int size = this.activities.size();
        for (int i = 0; i < size; i++) {
            ActivityRecord r = this.activities.get(i);
            if (r.isInterestingToUserLocked()) {
                return true;
            }
        }
        return false;
    }

    public void stopFreezingAllLocked() {
        int i = this.activities.size();
        while (i > 0) {
            i--;
            this.activities.get(i).stopFreezingScreenLocked(true);
        }
    }

    public void unlinkDeathRecipient() {
        if (this.deathRecipient != null && this.thread != null) {
            this.thread.asBinder().unlinkToDeath(this.deathRecipient, 0);
        }
        this.deathRecipient = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateHasAboveClientLocked() {
        this.hasAboveClient = false;
        for (int i = this.connections.size() - 1; i >= 0; i--) {
            ConnectionRecord cr = this.connections.valueAt(i);
            if ((cr.flags & 8) != 0) {
                this.hasAboveClient = true;
                return;
            }
        }
    }

    int modifyRawOomAdj(int adj) {
        if (this.hasAboveClient && adj >= 0) {
            if (adj < 1) {
                adj = 1;
            } else if (adj < 2) {
                adj = 2;
            } else if (adj < 9) {
                adj = 9;
            } else if (adj < 15) {
                adj++;
            }
        }
        return adj;
    }

    public String toShortString() {
        if (this.shortStringName != null) {
            return this.shortStringName;
        }
        StringBuilder sb = new StringBuilder(128);
        toShortString(sb);
        String sb2 = sb.toString();
        this.shortStringName = sb2;
        return sb2;
    }

    void toShortString(StringBuilder sb) {
        sb.append(this.pid);
        sb.append(':');
        sb.append(this.processName);
        sb.append('/');
        if (this.info.uid < 10000) {
            sb.append(this.uid);
            return;
        }
        sb.append('u');
        sb.append(this.userId);
        int appId = UserHandle.getAppId(this.info.uid);
        if (appId >= 10000) {
            sb.append('a');
            sb.append(appId - 10000);
        } else {
            sb.append('s');
            sb.append(appId);
        }
        if (this.uid != this.info.uid) {
            sb.append('i');
            sb.append(UserHandle.getAppId(this.uid) - Process.FIRST_ISOLATED_UID);
        }
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ProcessRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        toShortString(sb);
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public String makeAdjReason() {
        if (this.adjSource != null || this.adjTarget != null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(' ');
            if (this.adjTarget instanceof ComponentName) {
                sb.append(((ComponentName) this.adjTarget).flattenToShortString());
            } else if (this.adjTarget != null) {
                sb.append(this.adjTarget.toString());
            } else {
                sb.append("{null}");
            }
            sb.append("<=");
            if (this.adjSource instanceof ProcessRecord) {
                sb.append("Proc{");
                sb.append(((ProcessRecord) this.adjSource).toShortString());
                sb.append("}");
            } else if (this.adjSource != null) {
                sb.append(this.adjSource.toString());
            } else {
                sb.append("{null}");
            }
            return sb.toString();
        }
        return null;
    }

    public boolean addPackage(String pkg, ProcessStatsService tracker) {
        if (!this.pkgList.containsKey(pkg)) {
            if (this.baseProcessTracker != null) {
                ProcessStats.ProcessState state = tracker.getProcessStateLocked(pkg, this.info.uid, this.processName);
                this.pkgList.put(pkg, state);
                if (state != this.baseProcessTracker) {
                    state.makeActive();
                    return true;
                }
                return true;
            }
            this.pkgList.put(pkg, null);
            return true;
        }
        return false;
    }

    public int getSetAdjWithServices() {
        if (this.setAdj >= 9 && this.hasStartedServices) {
            return 8;
        }
        return this.setAdj;
    }

    public void forceProcessStateUpTo(int newState) {
        if (this.repProcState > newState) {
            this.repProcState = newState;
            this.curProcState = newState;
        }
    }

    public void resetPackageList(ProcessStatsService tracker) {
        int N = this.pkgList.size();
        if (this.baseProcessTracker == null) {
            if (N != 1) {
                this.pkgList.clear();
                this.pkgList.put(this.info.packageName, null);
                return;
            }
            return;
        }
        long now = SystemClock.uptimeMillis();
        this.baseProcessTracker.setState(-1, tracker.getMemFactorLocked(), now, this.pkgList);
        if (N != 1) {
            for (int i = 0; i < N; i++) {
                ProcessStats.ProcessState ps = this.pkgList.valueAt(i);
                if (ps != null && ps != this.baseProcessTracker) {
                    ps.makeInactive();
                }
            }
            this.pkgList.clear();
            ProcessStats.ProcessState ps2 = tracker.getProcessStateLocked(this.info.packageName, this.info.uid, this.processName);
            this.pkgList.put(this.info.packageName, ps2);
            if (ps2 != this.baseProcessTracker) {
                ps2.makeActive();
            }
        }
    }

    public String[] getPackageList() {
        int size = this.pkgList.size();
        if (size == 0) {
            return null;
        }
        String[] list = new String[size];
        for (int i = 0; i < this.pkgList.size(); i++) {
            list[i] = this.pkgList.keyAt(i);
        }
        return list;
    }
}