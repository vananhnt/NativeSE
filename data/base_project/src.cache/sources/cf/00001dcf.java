package com.android.server.am;

import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.R;
import com.android.internal.app.ProcessStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.server.NotificationManagerService;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* loaded from: ServiceRecord.class */
final class ServiceRecord extends Binder {
    static final int MAX_DELIVERY_COUNT = 3;
    static final int MAX_DONE_EXECUTING_COUNT = 6;
    final ActivityManagerService ams;
    final BatteryStatsImpl.Uid.Pkg.Serv stats;
    final ComponentName name;
    final String shortName;
    final Intent.FilterComparison intent;
    final ServiceInfo serviceInfo;
    final ApplicationInfo appInfo;
    final int userId;
    final String packageName;
    final String processName;
    final String permission;
    final String baseDir;
    final String resDir;
    final String dataDir;
    final boolean exported;
    final Runnable restarter;
    ProcessRecord app;
    ProcessRecord isolatedProc;
    ProcessStats.ServiceState tracker;
    boolean delayed;
    boolean isForeground;
    int foregroundId;
    Notification foregroundNoti;
    long startingBgTimeout;
    boolean startRequested;
    boolean delayedStop;
    boolean stopIfKilled;
    boolean callStart;
    int executeNesting;
    boolean executeFg;
    long executingStart;
    boolean createdFromFg;
    int crashCount;
    int totalRestartCount;
    int restartCount;
    long restartDelay;
    long restartTime;
    long nextRestartTime;
    String stringName;
    private int lastStartId;
    final ArrayMap<Intent.FilterComparison, IntentBindRecord> bindings = new ArrayMap<>();
    final ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = new ArrayMap<>();
    final ArrayList<StartItem> deliveredStarts = new ArrayList<>();
    final ArrayList<StartItem> pendingStarts = new ArrayList<>();
    final long createTime = SystemClock.elapsedRealtime();
    long lastActivity = SystemClock.uptimeMillis();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ServiceRecord$StartItem.class */
    public static class StartItem {
        final ServiceRecord sr;
        final boolean taskRemoved;
        final int id;
        final Intent intent;
        final ActivityManagerService$NeededUriGrants neededGrants;
        long deliveredTime;
        int deliveryCount;
        int doneExecutingCount;
        UriPermissionOwner uriPermissions;
        String stringName;

        /* JADX INFO: Access modifiers changed from: package-private */
        public StartItem(ServiceRecord _sr, boolean _taskRemoved, int _id, Intent _intent, ActivityManagerService$NeededUriGrants _neededGrants) {
            this.sr = _sr;
            this.taskRemoved = _taskRemoved;
            this.id = _id;
            this.intent = _intent;
            this.neededGrants = _neededGrants;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public UriPermissionOwner getUriPermissionsLocked() {
            if (this.uriPermissions == null) {
                this.uriPermissions = new UriPermissionOwner(this.sr.ams, this);
            }
            return this.uriPermissions;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void removeUriPermissionsLocked() {
            if (this.uriPermissions != null) {
                this.uriPermissions.removeUriPermissionsLocked();
                this.uriPermissions = null;
            }
        }

        public String toString() {
            if (this.stringName != null) {
                return this.stringName;
            }
            StringBuilder sb = new StringBuilder(128);
            sb.append("ServiceRecord{").append(Integer.toHexString(System.identityHashCode(this.sr))).append(' ').append(this.sr.shortName).append(" StartItem ").append(Integer.toHexString(System.identityHashCode(this))).append(" id=").append(this.id).append('}');
            String sb2 = sb.toString();
            this.stringName = sb2;
            return sb2;
        }
    }

    void dumpStartList(PrintWriter pw, String prefix, List<StartItem> list, long now) {
        int N = list.size();
        for (int i = 0; i < N; i++) {
            StartItem si = list.get(i);
            pw.print(prefix);
            pw.print(Separators.POUND);
            pw.print(i);
            pw.print(" id=");
            pw.print(si.id);
            if (now != 0) {
                pw.print(" dur=");
                TimeUtils.formatDuration(si.deliveredTime, now, pw);
            }
            if (si.deliveryCount != 0) {
                pw.print(" dc=");
                pw.print(si.deliveryCount);
            }
            if (si.doneExecutingCount != 0) {
                pw.print(" dxc=");
                pw.print(si.doneExecutingCount);
            }
            pw.println("");
            pw.print(prefix);
            pw.print("  intent=");
            if (si.intent != null) {
                pw.println(si.intent.toString());
            } else {
                pw.println("null");
            }
            if (si.neededGrants != null) {
                pw.print(prefix);
                pw.print("  neededGrants=");
                pw.println(si.neededGrants);
            }
            if (si.uriPermissions != null) {
                if (si.uriPermissions.readUriPermissions != null) {
                    pw.print(prefix);
                    pw.print("  readUriPermissions=");
                    pw.println(si.uriPermissions.readUriPermissions);
                }
                if (si.uriPermissions.writeUriPermissions != null) {
                    pw.print(prefix);
                    pw.print("  writeUriPermissions=");
                    pw.println(si.uriPermissions.writeUriPermissions);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("intent={");
        pw.print(this.intent.getIntent().toShortString(false, true, false, true));
        pw.println('}');
        pw.print(prefix);
        pw.print("packageName=");
        pw.println(this.packageName);
        pw.print(prefix);
        pw.print("processName=");
        pw.println(this.processName);
        if (this.permission != null) {
            pw.print(prefix);
            pw.print("permission=");
            pw.println(this.permission);
        }
        long now = SystemClock.uptimeMillis();
        long nowReal = SystemClock.elapsedRealtime();
        pw.print(prefix);
        pw.print("baseDir=");
        pw.println(this.baseDir);
        if (!this.resDir.equals(this.baseDir)) {
            pw.print(prefix);
            pw.print("resDir=");
            pw.println(this.resDir);
        }
        pw.print(prefix);
        pw.print("dataDir=");
        pw.println(this.dataDir);
        pw.print(prefix);
        pw.print("app=");
        pw.println(this.app);
        if (this.isolatedProc != null) {
            pw.print(prefix);
            pw.print("isolatedProc=");
            pw.println(this.isolatedProc);
        }
        if (this.delayed) {
            pw.print(prefix);
            pw.print("delayed=");
            pw.println(this.delayed);
        }
        if (this.isForeground || this.foregroundId != 0) {
            pw.print(prefix);
            pw.print("isForeground=");
            pw.print(this.isForeground);
            pw.print(" foregroundId=");
            pw.print(this.foregroundId);
            pw.print(" foregroundNoti=");
            pw.println(this.foregroundNoti);
        }
        pw.print(prefix);
        pw.print("createTime=");
        TimeUtils.formatDuration(this.createTime, nowReal, pw);
        pw.print(" startingBgTimeout=");
        TimeUtils.formatDuration(this.startingBgTimeout, now, pw);
        pw.println();
        pw.print(prefix);
        pw.print("lastActivity=");
        TimeUtils.formatDuration(this.lastActivity, now, pw);
        pw.print(" restartTime=");
        TimeUtils.formatDuration(this.restartTime, now, pw);
        pw.print(" createdFromFg=");
        pw.println(this.createdFromFg);
        if (this.startRequested || this.delayedStop || this.lastStartId != 0) {
            pw.print(prefix);
            pw.print("startRequested=");
            pw.print(this.startRequested);
            pw.print(" delayedStop=");
            pw.print(this.delayedStop);
            pw.print(" stopIfKilled=");
            pw.print(this.stopIfKilled);
            pw.print(" callStart=");
            pw.print(this.callStart);
            pw.print(" lastStartId=");
            pw.println(this.lastStartId);
        }
        if (this.executeNesting != 0) {
            pw.print(prefix);
            pw.print("executeNesting=");
            pw.print(this.executeNesting);
            pw.print(" executeFg=");
            pw.print(this.executeFg);
            pw.print(" executingStart=");
            TimeUtils.formatDuration(this.executingStart, now, pw);
            pw.println();
        }
        if (this.crashCount != 0 || this.restartCount != 0 || this.restartDelay != 0 || this.nextRestartTime != 0) {
            pw.print(prefix);
            pw.print("restartCount=");
            pw.print(this.restartCount);
            pw.print(" restartDelay=");
            TimeUtils.formatDuration(this.restartDelay, now, pw);
            pw.print(" nextRestartTime=");
            TimeUtils.formatDuration(this.nextRestartTime, now, pw);
            pw.print(" crashCount=");
            pw.println(this.crashCount);
        }
        if (this.deliveredStarts.size() > 0) {
            pw.print(prefix);
            pw.println("Delivered Starts:");
            dumpStartList(pw, prefix, this.deliveredStarts, now);
        }
        if (this.pendingStarts.size() > 0) {
            pw.print(prefix);
            pw.println("Pending Starts:");
            dumpStartList(pw, prefix, this.pendingStarts, 0L);
        }
        if (this.bindings.size() > 0) {
            pw.print(prefix);
            pw.println("Bindings:");
            for (int i = 0; i < this.bindings.size(); i++) {
                IntentBindRecord b = this.bindings.valueAt(i);
                pw.print(prefix);
                pw.print("* IntentBindRecord{");
                pw.print(Integer.toHexString(System.identityHashCode(b)));
                if ((b.collectFlags() & 1) != 0) {
                    pw.append(" CREATE");
                }
                pw.println("}:");
                b.dumpInService(pw, prefix + "  ");
            }
        }
        if (this.connections.size() > 0) {
            pw.print(prefix);
            pw.println("All Connections:");
            for (int conni = 0; conni < this.connections.size(); conni++) {
                ArrayList<ConnectionRecord> c = this.connections.valueAt(conni);
                for (int i2 = 0; i2 < c.size(); i2++) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.println(c.get(i2));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServiceRecord(ActivityManagerService ams, BatteryStatsImpl.Uid.Pkg.Serv servStats, ComponentName name, Intent.FilterComparison intent, ServiceInfo sInfo, boolean callerIsFg, Runnable restarter) {
        this.ams = ams;
        this.stats = servStats;
        this.name = name;
        this.shortName = name.flattenToShortString();
        this.intent = intent;
        this.serviceInfo = sInfo;
        this.appInfo = sInfo.applicationInfo;
        this.packageName = sInfo.applicationInfo.packageName;
        this.processName = sInfo.processName;
        this.permission = sInfo.permission;
        this.baseDir = sInfo.applicationInfo.sourceDir;
        this.resDir = sInfo.applicationInfo.publicSourceDir;
        this.dataDir = sInfo.applicationInfo.dataDir;
        this.exported = sInfo.exported;
        this.restarter = restarter;
        this.userId = UserHandle.getUserId(this.appInfo.uid);
        this.createdFromFg = callerIsFg;
    }

    public ProcessStats.ServiceState getTracker() {
        if (this.tracker != null) {
            return this.tracker;
        }
        if ((this.serviceInfo.applicationInfo.flags & 8) == 0) {
            this.tracker = this.ams.mProcessStats.getServiceStateLocked(this.serviceInfo.packageName, this.serviceInfo.applicationInfo.uid, this.serviceInfo.processName, this.serviceInfo.name);
            this.tracker.applyNewOwner(this);
        }
        return this.tracker;
    }

    public void forceClearTracker() {
        if (this.tracker != null) {
            this.tracker.clearCurrentOwner(this, true);
            this.tracker = null;
        }
    }

    public AppBindRecord retrieveAppBindingLocked(Intent intent, ProcessRecord app) {
        Intent.FilterComparison filter = new Intent.FilterComparison(intent);
        IntentBindRecord i = this.bindings.get(filter);
        if (i == null) {
            i = new IntentBindRecord(this, filter);
            this.bindings.put(filter, i);
        }
        AppBindRecord a = i.apps.get(app);
        if (a != null) {
            return a;
        }
        AppBindRecord a2 = new AppBindRecord(this, i, app);
        i.apps.put(app, a2);
        return a2;
    }

    public boolean hasAutoCreateConnections() {
        for (int conni = this.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> cr = this.connections.valueAt(conni);
            for (int i = 0; i < cr.size(); i++) {
                if ((cr.get(i).flags & 1) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetRestartCounter() {
        this.restartCount = 0;
        this.restartDelay = 0L;
        this.restartTime = 0L;
    }

    public StartItem findDeliveredStart(int id, boolean remove) {
        int N = this.deliveredStarts.size();
        for (int i = 0; i < N; i++) {
            StartItem si = this.deliveredStarts.get(i);
            if (si.id == id) {
                if (remove) {
                    this.deliveredStarts.remove(i);
                }
                return si;
            }
        }
        return null;
    }

    public int getLastStartId() {
        return this.lastStartId;
    }

    public int makeNextStartId() {
        this.lastStartId++;
        if (this.lastStartId < 1) {
            this.lastStartId = 1;
        }
        return this.lastStartId;
    }

    public void postNotification() {
        final int appUid = this.appInfo.uid;
        final int appPid = this.app.pid;
        if (this.foregroundId != 0 && this.foregroundNoti != null) {
            final String localPackageName = this.packageName;
            final int localForegroundId = this.foregroundId;
            final Notification localForegroundNoti = this.foregroundNoti;
            this.ams.mHandler.post(new Runnable() { // from class: com.android.server.am.ServiceRecord.1
                @Override // java.lang.Runnable
                public void run() {
                    NotificationManagerService nm = (NotificationManagerService) NotificationManager.getService();
                    if (nm == null) {
                        return;
                    }
                    try {
                        if (localForegroundNoti.icon == 0) {
                            localForegroundNoti.icon = ServiceRecord.this.appInfo.icon;
                            localForegroundNoti.contentView = null;
                            localForegroundNoti.bigContentView = null;
                            CharSequence appName = ServiceRecord.this.appInfo.loadLabel(ServiceRecord.this.ams.mContext.getPackageManager());
                            if (appName == null) {
                                appName = ServiceRecord.this.appInfo.packageName;
                            }
                            try {
                                Context ctx = ServiceRecord.this.ams.mContext.createPackageContext(ServiceRecord.this.appInfo.packageName, 0);
                                Intent runningIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                runningIntent.setData(Uri.fromParts(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, ServiceRecord.this.appInfo.packageName, null));
                                PendingIntent pi = PendingIntent.getActivity(ServiceRecord.this.ams.mContext, 0, runningIntent, 134217728);
                                localForegroundNoti.setLatestEventInfo(ctx, ServiceRecord.this.ams.mContext.getString(R.string.app_running_notification_title, appName), ServiceRecord.this.ams.mContext.getString(R.string.app_running_notification_text, appName), pi);
                            } catch (PackageManager.NameNotFoundException e) {
                                localForegroundNoti.icon = 0;
                            }
                        }
                        if (localForegroundNoti.icon == 0) {
                            throw new RuntimeException("icon must be non-zero");
                        }
                        int[] outId = new int[1];
                        nm.enqueueNotificationInternal(localPackageName, localPackageName, appUid, appPid, null, localForegroundId, localForegroundNoti, outId, ServiceRecord.this.userId);
                    } catch (RuntimeException e2) {
                        Slog.w("ActivityManager", "Error showing notification for service", e2);
                        ServiceRecord.this.ams.setServiceForeground(ServiceRecord.this.name, ServiceRecord.this, 0, (Notification) null, true);
                        ServiceRecord.this.ams.crashApplication(appUid, appPid, localPackageName, "Bad notification for startForeground: " + e2);
                    }
                }
            });
        }
    }

    public void cancelNotification() {
        if (this.foregroundId != 0) {
            final String localPackageName = this.packageName;
            final int localForegroundId = this.foregroundId;
            this.ams.mHandler.post(new Runnable() { // from class: com.android.server.am.ServiceRecord.2
                @Override // java.lang.Runnable
                public void run() {
                    INotificationManager inm = NotificationManager.getService();
                    if (inm == null) {
                        return;
                    }
                    try {
                        inm.cancelNotificationWithTag(localPackageName, null, localForegroundId, ServiceRecord.this.userId);
                    } catch (RemoteException e) {
                    } catch (RuntimeException e2) {
                        Slog.w("ActivityManager", "Error canceling notification for service", e2);
                    }
                }
            });
        }
    }

    public void clearDeliveredStartsLocked() {
        for (int i = this.deliveredStarts.size() - 1; i >= 0; i--) {
            this.deliveredStarts.get(i).removeUriPermissionsLocked();
        }
        this.deliveredStarts.clear();
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ServiceRecord{").append(Integer.toHexString(System.identityHashCode(this))).append(" u").append(this.userId).append(' ').append(this.shortName).append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}