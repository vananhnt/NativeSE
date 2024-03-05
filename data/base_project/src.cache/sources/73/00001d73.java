package com.android.server.am;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ProxyProperties;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.WindowManager;
import com.android.internal.R;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.util.FastPrintWriter;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/* loaded from: ActivityManagerService$2.class */
class ActivityManagerService$2 extends Handler {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$2(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        ActivityRecord root;
        ProcessRecord process;
        switch (msg.what) {
            case 1:
                HashMap<String, Object> data = (HashMap) msg.obj;
                boolean showBackground = Settings.Secure.getInt(this.this$0.mContext.getContentResolver(), Settings.Secure.ANR_SHOW_BACKGROUND, 0) != 0;
                synchronized (this.this$0) {
                    ProcessRecord proc = (ProcessRecord) data.get("app");
                    AppErrorResult res = (AppErrorResult) data.get("result");
                    if (proc != null && proc.crashDialog != null) {
                        Slog.e("ActivityManager", "App already has crash dialog: " + proc);
                        if (res != null) {
                            res.set(0);
                        }
                        return;
                    } else if (!showBackground && UserHandle.getAppId(proc.uid) >= 10000 && proc.userId != this.this$0.mCurrentUserId && proc.pid != ActivityManagerService.MY_PID) {
                        Slog.w("ActivityManager", "Skipping crash dialog of " + proc + ": background");
                        if (res != null) {
                            res.set(0);
                        }
                        return;
                    } else {
                        if (ActivityManagerService.access$000(this.this$0) && !this.this$0.mSleeping && !this.this$0.mShuttingDown) {
                            Dialog d = new AppErrorDialog(this.this$0.mContext, this.this$0, res, proc);
                            d.show();
                            proc.crashDialog = d;
                        } else if (res != null) {
                            res.set(0);
                        }
                        this.this$0.ensureBootCompleted();
                        return;
                    }
                }
            case 2:
                synchronized (this.this$0) {
                    HashMap<String, Object> data2 = (HashMap) msg.obj;
                    ProcessRecord proc2 = (ProcessRecord) data2.get("app");
                    if (proc2 != null && proc2.anrDialog != null) {
                        Slog.e("ActivityManager", "App already has anr dialog: " + proc2);
                        return;
                    }
                    Intent intent = new Intent("android.intent.action.ANR");
                    if (!this.this$0.mProcessesReady) {
                        intent.addFlags(1342177280);
                    }
                    ActivityManagerService.access$100(this.this$0, (ProcessRecord) null, (String) null, intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, (String) null, -1, false, false, ActivityManagerService.MY_PID, 1000, 0);
                    if (ActivityManagerService.access$000(this.this$0)) {
                        Dialog d2 = new AppNotRespondingDialog(this.this$0, this.this$0.mContext, proc2, (ActivityRecord) data2.get(Context.ACTIVITY_SERVICE), msg.arg1 != 0);
                        d2.show();
                        proc2.anrDialog = d2;
                    } else {
                        this.this$0.killAppAtUsersRequest(proc2, (Dialog) null);
                    }
                    this.this$0.ensureBootCompleted();
                    return;
                }
            case 3:
                new FactoryErrorDialog(this.this$0.mContext, msg.getData().getCharSequence("msg")).show();
                this.this$0.ensureBootCompleted();
                return;
            case 4:
                ContentResolver resolver = this.this$0.mContext.getContentResolver();
                Settings.System.putConfiguration(resolver, (Configuration) msg.obj);
                return;
            case 5:
                synchronized (this.this$0) {
                    this.this$0.performAppGcsIfAppropriateLocked();
                }
                return;
            case 6:
                synchronized (this.this$0) {
                    ProcessRecord app = (ProcessRecord) msg.obj;
                    if (msg.arg1 != 0) {
                        if (!app.waitedForDebugger) {
                            Dialog d3 = new AppWaitingForDebuggerDialog(this.this$0, this.this$0.mContext, app);
                            app.waitDialog = d3;
                            app.waitedForDebugger = true;
                            d3.show();
                        }
                    } else if (app.waitDialog != null) {
                        app.waitDialog.dismiss();
                        app.waitDialog = null;
                    }
                }
                return;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                return;
            case 12:
                if (this.this$0.mDidDexOpt) {
                    this.this$0.mDidDexOpt = false;
                    Message nmsg = this.this$0.mHandler.obtainMessage(12);
                    nmsg.obj = msg.obj;
                    this.this$0.mHandler.sendMessageDelayed(nmsg, 20000L);
                    return;
                }
                this.this$0.mServices.serviceTimeout((ProcessRecord) msg.obj);
                return;
            case 13:
                synchronized (this.this$0) {
                    for (int i = this.this$0.mLruProcesses.size() - 1; i >= 0; i--) {
                        ProcessRecord r = (ProcessRecord) this.this$0.mLruProcesses.get(i);
                        if (r.thread != null) {
                            try {
                                r.thread.updateTimeZone();
                            } catch (RemoteException e) {
                                Slog.w("ActivityManager", "Failed to update time zone for: " + r.info.processName);
                            }
                        }
                    }
                }
                return;
            case 14:
                Log.e("ActivityManager", "System UIDs Inconsistent: UIDs on the system are inconsistent, you need to wipe your data partition or your device will be unstable.");
                if (ActivityManagerService.access$000(this.this$0)) {
                    AlertDialog d4 = new BaseErrorDialog(this.this$0.mContext);
                    d4.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
                    d4.setCancelable(false);
                    d4.setTitle("System UIDs Inconsistent");
                    d4.setMessage("UIDs on the system are inconsistent, you need to wipe your data partition or your device will be unstable.");
                    d4.setButton(-1, "I'm Feeling Lucky", this.this$0.mHandler.obtainMessage(15));
                    this.this$0.mUidAlert = d4;
                    d4.show();
                    return;
                }
                return;
            case 15:
                if (this.this$0.mUidAlert != null) {
                    this.this$0.mUidAlert.dismiss();
                    this.this$0.mUidAlert = null;
                    return;
                }
                return;
            case 20:
                if (this.this$0.mDidDexOpt) {
                    this.this$0.mDidDexOpt = false;
                    Message nmsg2 = this.this$0.mHandler.obtainMessage(20);
                    nmsg2.obj = msg.obj;
                    this.this$0.mHandler.sendMessageDelayed(nmsg2, 10000L);
                    return;
                }
                ProcessRecord app2 = (ProcessRecord) msg.obj;
                synchronized (this.this$0) {
                    ActivityManagerService.access$200(this.this$0, app2);
                }
                return;
            case 21:
                synchronized (this.this$0) {
                    this.this$0.doPendingActivityLaunchesLocked(true);
                }
                return;
            case 22:
                synchronized (this.this$0) {
                    int appid = msg.arg1;
                    boolean restart = msg.arg2 == 1;
                    Bundle bundle = (Bundle) msg.obj;
                    String pkg = bundle.getString("pkg");
                    String reason = bundle.getString("reason");
                    ActivityManagerService.access$300(this.this$0, pkg, appid, restart, false, true, false, -1, reason);
                }
                return;
            case 23:
                ((PendingIntentRecord) msg.obj).completeFinalize();
                return;
            case 24:
                INotificationManager inm = NotificationManager.getService();
                if (inm == null || (process = (root = (ActivityRecord) msg.obj).app) == null) {
                    return;
                }
                try {
                    Context context = this.this$0.mContext.createPackageContext(process.info.packageName, 0);
                    String text = this.this$0.mContext.getString(R.string.heavy_weight_notification, context.getApplicationInfo().loadLabel(context.getPackageManager()));
                    Notification notification = new Notification();
                    notification.icon = R.drawable.stat_sys_adb;
                    notification.when = 0L;
                    notification.flags = 2;
                    notification.tickerText = text;
                    notification.defaults = 0;
                    notification.sound = null;
                    notification.vibrate = null;
                    notification.setLatestEventInfo(context, text, this.this$0.mContext.getText(R.string.heavy_weight_notification_detail), PendingIntent.getActivityAsUser(this.this$0.mContext, 0, root.intent, 268435456, null, new UserHandle(root.userId)));
                    try {
                        int[] outId = new int[1];
                        inm.enqueueNotificationWithTag("android", "android", null, R.string.heavy_weight_notification, notification, outId, root.userId);
                    } catch (RemoteException e2) {
                    } catch (RuntimeException e3) {
                        Slog.w("ActivityManager", "Error showing notification for heavy-weight app", e3);
                    }
                    return;
                } catch (PackageManager.NameNotFoundException e4) {
                    Slog.w("ActivityManager", "Unable to create context for heavy notification", e4);
                    return;
                }
            case 25:
                INotificationManager inm2 = NotificationManager.getService();
                if (inm2 == null) {
                    return;
                }
                try {
                    inm2.cancelNotificationWithTag("android", null, R.string.heavy_weight_notification, msg.arg1);
                    return;
                } catch (RemoteException e5) {
                    return;
                } catch (RuntimeException e6) {
                    Slog.w("ActivityManager", "Error canceling notification for service", e6);
                    return;
                }
            case 26:
                HashMap<String, Object> data3 = (HashMap) msg.obj;
                synchronized (this.this$0) {
                    ProcessRecord proc3 = (ProcessRecord) data3.get("app");
                    if (proc3 == null) {
                        Slog.e("ActivityManager", "App not found when showing strict mode dialog.");
                        return;
                    } else if (proc3.crashDialog != null) {
                        Slog.e("ActivityManager", "App already has strict mode dialog: " + proc3);
                        return;
                    } else {
                        AppErrorResult res2 = (AppErrorResult) data3.get("result");
                        if (ActivityManagerService.access$000(this.this$0) && !this.this$0.mSleeping && !this.this$0.mShuttingDown) {
                            Dialog d5 = new StrictModeViolationDialog(this.this$0.mContext, this.this$0, res2, proc3);
                            d5.show();
                            proc3.crashDialog = d5;
                        } else {
                            res2.set(0);
                        }
                        this.this$0.ensureBootCompleted();
                        return;
                    }
                }
            case 27:
                synchronized (this.this$0) {
                    this.this$0.checkExcessivePowerUsageLocked(true);
                    removeMessages(27);
                    sendMessageDelayed(obtainMessage(27), AlarmManager.INTERVAL_FIFTEEN_MINUTES);
                }
                return;
            case 28:
                synchronized (this.this$0) {
                    for (int i2 = this.this$0.mLruProcesses.size() - 1; i2 >= 0; i2--) {
                        ProcessRecord r2 = (ProcessRecord) this.this$0.mLruProcesses.get(i2);
                        if (r2.thread != null) {
                            try {
                                r2.thread.clearDnsCache();
                            } catch (RemoteException e7) {
                                Slog.w("ActivityManager", "Failed to clear dns cache for: " + r2.info.processName);
                            }
                        }
                    }
                }
                return;
            case 29:
                ProxyProperties proxy = (ProxyProperties) msg.obj;
                String host = "";
                String port = "";
                String exclList = "";
                String pacFileUrl = null;
                if (proxy != null) {
                    host = proxy.getHost();
                    port = Integer.toString(proxy.getPort());
                    exclList = proxy.getExclusionList();
                    pacFileUrl = proxy.getPacFileUrl();
                }
                synchronized (this.this$0) {
                    for (int i3 = this.this$0.mLruProcesses.size() - 1; i3 >= 0; i3--) {
                        ProcessRecord r3 = (ProcessRecord) this.this$0.mLruProcesses.get(i3);
                        if (r3.thread != null) {
                            try {
                                r3.thread.setHttpProxy(host, port, exclList, pacFileUrl);
                            } catch (RemoteException e8) {
                                Slog.w("ActivityManager", "Failed to update http proxy for: " + r3.info.processName);
                            }
                        }
                    }
                }
                return;
            case 30:
                synchronized (this.this$0) {
                    ActivityRecord ar = (ActivityRecord) msg.obj;
                    if (this.this$0.mCompatModeDialog != null) {
                        if (this.this$0.mCompatModeDialog.mAppInfo.packageName.equals(ar.info.applicationInfo.packageName)) {
                            return;
                        }
                        this.this$0.mCompatModeDialog.dismiss();
                        this.this$0.mCompatModeDialog = null;
                    }
                    if (ar != null) {
                    }
                    return;
                }
            case 31:
                ActivityManagerService.access$400(this.this$0);
                return;
            case 32:
                int pid = msg.arg1;
                int uid = msg.arg2;
                ActivityManagerService.access$500(this.this$0, pid, uid);
                return;
            case 33:
                final ArrayList<ProcessMemInfo> memInfos = (ArrayList) msg.obj;
                Thread thread = new Thread() { // from class: com.android.server.am.ActivityManagerService$2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        SparseArray<ProcessMemInfo> infoMap = new SparseArray<>(memInfos.size());
                        int N = memInfos.size();
                        for (int i4 = 0; i4 < N; i4++) {
                            ProcessMemInfo mi = (ProcessMemInfo) memInfos.get(i4);
                            infoMap.put(mi.pid, mi);
                        }
                        ActivityManagerService$2.this.this$0.updateCpuStatsNow();
                        synchronized (ActivityManagerService$2.this.this$0.mProcessCpuThread) {
                            int N2 = ActivityManagerService$2.this.this$0.mProcessCpuTracker.countStats();
                            for (int i5 = 0; i5 < N2; i5++) {
                                ProcessCpuTracker.Stats st = ActivityManagerService$2.this.this$0.mProcessCpuTracker.getStats(i5);
                                if (st.vsize > 0) {
                                    long pss = Debug.getPss(st.pid, null);
                                    if (pss > 0 && infoMap.indexOfKey(st.pid) < 0) {
                                        ProcessMemInfo mi2 = new ProcessMemInfo(st.name, st.pid, -17, -1, "native", null);
                                        mi2.pss = pss;
                                        memInfos.add(mi2);
                                    }
                                }
                            }
                        }
                        long totalPss = 0;
                        int N3 = memInfos.size();
                        for (int i6 = 0; i6 < N3; i6++) {
                            ProcessMemInfo mi3 = (ProcessMemInfo) memInfos.get(i6);
                            if (mi3.pss == 0) {
                                mi3.pss = Debug.getPss(mi3.pid, null);
                            }
                            totalPss += mi3.pss;
                        }
                        Collections.sort(memInfos, new Comparator<ProcessMemInfo>() { // from class: com.android.server.am.ActivityManagerService.2.1.1
                            @Override // java.util.Comparator
                            public int compare(ProcessMemInfo lhs, ProcessMemInfo rhs) {
                                if (lhs.oomAdj != rhs.oomAdj) {
                                    return lhs.oomAdj < rhs.oomAdj ? -1 : 1;
                                } else if (lhs.pss != rhs.pss) {
                                    return lhs.pss < rhs.pss ? 1 : -1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        StringBuilder tag = new StringBuilder(128);
                        StringBuilder stack = new StringBuilder(128);
                        tag.append("Low on memory -- ");
                        ActivityManagerService.appendMemBucket(tag, totalPss, "total", false);
                        ActivityManagerService.appendMemBucket(stack, totalPss, "total", true);
                        StringBuilder logBuilder = new StringBuilder(1024);
                        logBuilder.append("Low on memory:\n");
                        boolean firstLine = true;
                        int lastOomAdj = Integer.MIN_VALUE;
                        int N4 = memInfos.size();
                        for (int i7 = 0; i7 < N4; i7++) {
                            ProcessMemInfo mi4 = (ProcessMemInfo) memInfos.get(i7);
                            if (mi4.oomAdj != -17 && (mi4.oomAdj < 5 || mi4.oomAdj == 6 || mi4.oomAdj == 7)) {
                                if (lastOomAdj != mi4.oomAdj) {
                                    lastOomAdj = mi4.oomAdj;
                                    if (mi4.oomAdj <= 0) {
                                        tag.append(" / ");
                                    }
                                    if (mi4.oomAdj >= 0) {
                                        if (firstLine) {
                                            stack.append(Separators.COLON);
                                            firstLine = false;
                                        }
                                        stack.append("\n\t at ");
                                    } else {
                                        stack.append("$");
                                    }
                                } else {
                                    tag.append(Separators.SP);
                                    stack.append("$");
                                }
                                if (mi4.oomAdj <= 0) {
                                    ActivityManagerService.appendMemBucket(tag, mi4.pss, mi4.name, false);
                                }
                                ActivityManagerService.appendMemBucket(stack, mi4.pss, mi4.name, true);
                                if (mi4.oomAdj >= 0 && (i7 + 1 >= N4 || ((ProcessMemInfo) memInfos.get(i7 + 1)).oomAdj != lastOomAdj)) {
                                    stack.append(Separators.LPAREN);
                                    for (int k = 0; k < ActivityManagerService.DUMP_MEM_OOM_ADJ.length; k++) {
                                        if (ActivityManagerService.DUMP_MEM_OOM_ADJ[k] == mi4.oomAdj) {
                                            stack.append(ActivityManagerService.DUMP_MEM_OOM_LABEL[k]);
                                            stack.append(Separators.COLON);
                                            stack.append(ActivityManagerService.DUMP_MEM_OOM_ADJ[k]);
                                        }
                                    }
                                    stack.append(Separators.RPAREN);
                                }
                            }
                            logBuilder.append("  ");
                            logBuilder.append(ProcessList.makeOomAdjString(mi4.oomAdj));
                            logBuilder.append(' ');
                            logBuilder.append(ProcessList.makeProcStateString(mi4.procState));
                            logBuilder.append(' ');
                            ProcessList.appendRamKb(logBuilder, mi4.pss);
                            logBuilder.append(" kB: ");
                            logBuilder.append(mi4.name);
                            logBuilder.append(" (");
                            logBuilder.append(mi4.pid);
                            logBuilder.append(") ");
                            logBuilder.append(mi4.adjType);
                            logBuilder.append('\n');
                            if (mi4.adjReason != null) {
                                logBuilder.append("                      ");
                                logBuilder.append(mi4.adjReason);
                                logBuilder.append('\n');
                            }
                        }
                        logBuilder.append("           ");
                        ProcessList.appendRamKb(logBuilder, totalPss);
                        logBuilder.append(" kB: TOTAL\n");
                        long[] infos = new long[9];
                        Debug.getMemInfo(infos);
                        logBuilder.append("  MemInfo: ");
                        logBuilder.append(infos[5]).append(" kB slab, ");
                        logBuilder.append(infos[4]).append(" kB shmem, ");
                        logBuilder.append(infos[2]).append(" kB buffers, ");
                        logBuilder.append(infos[3]).append(" kB cached, ");
                        logBuilder.append(infos[1]).append(" kB free\n");
                        if (infos[8] != 0) {
                            logBuilder.append("  ZRAM: ");
                            logBuilder.append(infos[8]);
                            logBuilder.append(" kB RAM, ");
                            logBuilder.append(infos[6]);
                            logBuilder.append(" kB swap total, ");
                            logBuilder.append(infos[7]);
                            logBuilder.append(" kB swap free\n");
                        }
                        Slog.i("ActivityManager", logBuilder.toString());
                        StringBuilder dropBuilder = new StringBuilder(1024);
                        dropBuilder.append((CharSequence) stack);
                        dropBuilder.append('\n');
                        dropBuilder.append('\n');
                        dropBuilder.append((CharSequence) logBuilder);
                        dropBuilder.append('\n');
                        StringWriter catSw = new StringWriter();
                        synchronized (ActivityManagerService$2.this.this$0) {
                            PrintWriter catPw = new FastPrintWriter((Writer) catSw, false, 256);
                            String[] emptyArgs = new String[0];
                            catPw.println();
                            ActivityManagerService$2.this.this$0.dumpProcessesLocked((FileDescriptor) null, catPw, emptyArgs, 0, false, (String) null);
                            catPw.println();
                            ActivityManagerService$2.this.this$0.mServices.dumpServicesLocked(null, catPw, emptyArgs, 0, false, false, null);
                            catPw.println();
                            ActivityManagerService$2.this.this$0.dumpActivitiesLocked((FileDescriptor) null, catPw, emptyArgs, 0, false, false, (String) null);
                            catPw.flush();
                        }
                        dropBuilder.append(catSw.toString());
                        ActivityManagerService$2.this.this$0.addErrorToDropBox("lowmem", (ProcessRecord) null, "system_server", (ActivityRecord) null, (ActivityRecord) null, tag.toString(), dropBuilder.toString(), (File) null, (ApplicationErrorReport.CrashInfo) null);
                        synchronized (ActivityManagerService$2.this.this$0) {
                            long now = SystemClock.uptimeMillis();
                            if (ActivityManagerService$2.this.this$0.mLastMemUsageReportTime < now) {
                                ActivityManagerService$2.this.this$0.mLastMemUsageReportTime = now;
                            }
                        }
                    }
                };
                thread.start();
                return;
            case 34:
                this.this$0.dispatchUserSwitch((UserStartedState) msg.obj, msg.arg1, msg.arg2);
                return;
            case 35:
                this.this$0.continueUserSwitch((UserStartedState) msg.obj, msg.arg1, msg.arg2);
                return;
            case 36:
                this.this$0.timeoutUserSwitch((UserStartedState) msg.obj, msg.arg1, msg.arg2);
                return;
            case 37:
                boolean nextState = msg.arg1 != 0;
                if (this.this$0.mUpdateLock.isHeld() != nextState) {
                    if (nextState) {
                        this.this$0.mUpdateLock.acquire();
                        return;
                    } else {
                        this.this$0.mUpdateLock.release();
                        return;
                    }
                }
                return;
            case 38:
                ActivityManagerService.access$600(this.this$0);
                return;
            case 39:
                this.this$0.requestPssAllProcsLocked(SystemClock.uptimeMillis(), true, false);
                return;
        }
    }
}