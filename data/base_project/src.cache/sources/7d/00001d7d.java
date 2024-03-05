package com.android.server.am;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/* loaded from: ActivityManagerService$3.class */
class ActivityManagerService$3 extends Handler {
    final /* synthetic */ ActivityManagerService this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ActivityManagerService$3(ActivityManagerService activityManagerService, Looper x0) {
        super(x0);
        this.this$0 = activityManagerService;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        ProcessRecord proc;
        int procState;
        int pid;
        switch (msg.what) {
            case 1:
                int i = 0;
                int num = 0;
                SystemClock.uptimeMillis();
                long[] tmp = new long[1];
                while (true) {
                    synchronized (this.this$0) {
                        if (i >= this.this$0.mPendingPssProcesses.size()) {
                            this.this$0.mPendingPssProcesses.clear();
                            return;
                        }
                        proc = (ProcessRecord) this.this$0.mPendingPssProcesses.get(i);
                        procState = proc.pssProcState;
                        if (proc.thread != null && procState == proc.setProcState) {
                            pid = proc.pid;
                        } else {
                            proc = null;
                            pid = 0;
                        }
                        i++;
                    }
                    if (proc != null) {
                        long pss = Debug.getPss(pid, tmp);
                        synchronized (this.this$0) {
                            if (proc.thread != null && proc.setProcState == procState && proc.pid == pid) {
                                num++;
                                proc.lastPssTime = SystemClock.uptimeMillis();
                                proc.baseProcessTracker.addPss(pss, tmp[0], true, proc.pkgList);
                                if (proc.initialIdlePss == 0) {
                                    proc.initialIdlePss = pss;
                                }
                                proc.lastPss = pss;
                                if (procState >= 9) {
                                    proc.lastCachedPss = pss;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                return;
        }
    }
}