package com.android.server.am;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Telephony;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.app.ProcessStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.server.am.ServiceRecord;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

/* loaded from: ActiveServices.class */
public final class ActiveServices {
    static final boolean DEBUG_SERVICE = false;
    static final boolean DEBUG_SERVICE_EXECUTING = false;
    static final boolean DEBUG_DELAYED_SERVICE = false;
    static final boolean DEBUG_DELAYED_STATS = false;
    static final boolean DEBUG_MU = false;
    static final String TAG = "ActivityManager";
    static final String TAG_MU = "ActivityManagerServiceMU";
    static final int SERVICE_TIMEOUT = 20000;
    static final int SERVICE_BACKGROUND_TIMEOUT = 200000;
    static final int SERVICE_RESTART_DURATION = 5000;
    static final int SERVICE_RESET_RUN_DURATION = 60000;
    static final int SERVICE_RESTART_DURATION_FACTOR = 4;
    static final int SERVICE_MIN_RESTART_TIME_BETWEEN = 10000;
    static final int MAX_SERVICE_INACTIVITY = 1800000;
    static final int BG_START_TIMEOUT = 15000;
    final ActivityManagerService mAm;
    final int mMaxStartingBackground;
    final SparseArray<ServiceMap> mServiceMap = new SparseArray<>();
    final ArrayMap<IBinder, ArrayList<ConnectionRecord>> mServiceConnections = new ArrayMap<>();
    final ArrayList<ServiceRecord> mPendingServices = new ArrayList<>();
    final ArrayList<ServiceRecord> mRestartingServices = new ArrayList<>();
    final ArrayList<ServiceRecord> mDestroyingServices = new ArrayList<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.stopServiceLocked(android.app.IApplicationThread, android.content.Intent, java.lang.String, int):int, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    int stopServiceLocked(android.app.IApplicationThread r1, android.content.Intent r2, java.lang.String r3, int r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.stopServiceLocked(android.app.IApplicationThread, android.content.Intent, java.lang.String, int):int, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.stopServiceLocked(android.app.IApplicationThread, android.content.Intent, java.lang.String, int):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.setServiceForegroundLocked(android.content.ComponentName, android.os.IBinder, int, android.app.Notification, boolean):void, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void setServiceForegroundLocked(android.content.ComponentName r1, android.os.IBinder r2, int r3, android.app.Notification r4, boolean r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.setServiceForegroundLocked(android.content.ComponentName, android.os.IBinder, int, android.app.Notification, boolean):void, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.setServiceForegroundLocked(android.content.ComponentName, android.os.IBinder, int, android.app.Notification, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.bindServiceLocked(android.app.IApplicationThread, android.os.IBinder, android.content.Intent, java.lang.String, android.app.IServiceConnection, int, int):int, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    int bindServiceLocked(android.app.IApplicationThread r1, android.os.IBinder r2, android.content.Intent r3, java.lang.String r4, android.app.IServiceConnection r5, int r6, int r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.bindServiceLocked(android.app.IApplicationThread, android.os.IBinder, android.content.Intent, java.lang.String, android.app.IServiceConnection, int, int):int, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.bindServiceLocked(android.app.IApplicationThread, android.os.IBinder, android.content.Intent, java.lang.String, android.app.IServiceConnection, int, int):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.publishServiceLocked(com.android.server.am.ServiceRecord, android.content.Intent, android.os.IBinder):void, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void publishServiceLocked(com.android.server.am.ServiceRecord r1, android.content.Intent r2, android.os.IBinder r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.publishServiceLocked(com.android.server.am.ServiceRecord, android.content.Intent, android.os.IBinder):void, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.publishServiceLocked(com.android.server.am.ServiceRecord, android.content.Intent, android.os.IBinder):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.unbindServiceLocked(android.app.IServiceConnection):boolean, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    boolean unbindServiceLocked(android.app.IServiceConnection r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.unbindServiceLocked(android.app.IServiceConnection):boolean, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.unbindServiceLocked(android.app.IServiceConnection):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.unbindFinishedLocked(com.android.server.am.ServiceRecord, android.content.Intent, boolean):void, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void unbindFinishedLocked(com.android.server.am.ServiceRecord r1, android.content.Intent r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.unbindFinishedLocked(com.android.server.am.ServiceRecord, android.content.Intent, boolean):void, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.unbindFinishedLocked(com.android.server.am.ServiceRecord, android.content.Intent, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.realStartServiceLocked(com.android.server.am.ServiceRecord, com.android.server.am.ProcessRecord, boolean):void, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private final void realStartServiceLocked(com.android.server.am.ServiceRecord r1, com.android.server.am.ProcessRecord r2, boolean r3) throws android.os.RemoteException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.realStartServiceLocked(com.android.server.am.ServiceRecord, com.android.server.am.ProcessRecord, boolean):void, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.realStartServiceLocked(com.android.server.am.ServiceRecord, com.android.server.am.ProcessRecord, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.getRunningServiceInfoLocked(int, int):java.util.List<android.app.ActivityManager$RunningServiceInfo>, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    java.util.List<android.app.ActivityManager.RunningServiceInfo> getRunningServiceInfoLocked(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.getRunningServiceInfoLocked(int, int):java.util.List<android.app.ActivityManager$RunningServiceInfo>, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.getRunningServiceInfoLocked(int, int):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.dumpServicesLocked(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], int, boolean, boolean, java.lang.String):void, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void dumpServicesLocked(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3, int r4, boolean r5, boolean r6, java.lang.String r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.dumpServicesLocked(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], int, boolean, boolean, java.lang.String):void, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.dumpServicesLocked(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[], int, boolean, boolean, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.dumpService(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, com.android.server.am.ServiceRecord, java.lang.String[], boolean):void, file: ActiveServices.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void dumpService(java.lang.String r1, java.io.FileDescriptor r2, java.io.PrintWriter r3, com.android.server.am.ServiceRecord r4, java.lang.String[] r5, boolean r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActiveServices.dumpService(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, com.android.server.am.ServiceRecord, java.lang.String[], boolean):void, file: ActiveServices.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActiveServices.dumpService(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, com.android.server.am.ServiceRecord, java.lang.String[], boolean):void");
    }

    /* loaded from: ActiveServices$DelayingProcess.class */
    static final class DelayingProcess extends ArrayList<ServiceRecord> {
        long timeoout;

        DelayingProcess() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActiveServices$ServiceMap.class */
    public class ServiceMap extends Handler {
        final int mUserId;
        final ArrayMap<ComponentName, ServiceRecord> mServicesByName;
        final ArrayMap<Intent.FilterComparison, ServiceRecord> mServicesByIntent;
        final ArrayList<ServiceRecord> mDelayedStartList;
        final ArrayList<ServiceRecord> mStartingBackground;
        static final int MSG_BG_START_TIMEOUT = 1;

        ServiceMap(Looper looper, int userId) {
            super(looper);
            this.mServicesByName = new ArrayMap<>();
            this.mServicesByIntent = new ArrayMap<>();
            this.mDelayedStartList = new ArrayList<>();
            this.mStartingBackground = new ArrayList<>();
            this.mUserId = userId;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    synchronized (ActiveServices.this.mAm) {
                        rescheduleDelayedStarts();
                    }
                    return;
                default:
                    return;
            }
        }

        void ensureNotStartingBackground(ServiceRecord r) {
            if (this.mStartingBackground.remove(r)) {
                rescheduleDelayedStarts();
            }
            if (this.mDelayedStartList.remove(r)) {
            }
        }

        void rescheduleDelayedStarts() {
            removeMessages(1);
            long now = SystemClock.uptimeMillis();
            int N = this.mStartingBackground.size();
            for (int i = 0; i < N; i++) {
                ServiceRecord r = this.mStartingBackground.get(i);
                if (r.startingBgTimeout <= now) {
                    Slog.i(ActiveServices.TAG, "Waited long enough for: " + r);
                    this.mStartingBackground.remove(i);
                    N--;
                }
            }
            while (this.mDelayedStartList.size() > 0 && this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground) {
                ServiceRecord r2 = this.mDelayedStartList.remove(0);
                if (r2.pendingStarts.size() <= 0) {
                    Slog.w(ActiveServices.TAG, "**** NO PENDING STARTS! " + r2 + " startReq=" + r2.startRequested + " delayedStop=" + r2.delayedStop);
                }
                r2.delayed = false;
                ActiveServices.this.startServiceInnerLocked(this, r2.pendingStarts.get(0).intent, r2, false, true);
            }
            if (this.mStartingBackground.size() > 0) {
                ServiceRecord next = this.mStartingBackground.get(0);
                long when = next.startingBgTimeout > now ? next.startingBgTimeout : now;
                Message msg = obtainMessage(1);
                sendMessageAtTime(msg, when);
            }
            if (this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground) {
                ActiveServices.this.mAm.backgroundServicesFinishedLocked(this.mUserId);
            }
        }
    }

    public ActiveServices(ActivityManagerService service) {
        this.mAm = service;
        this.mMaxStartingBackground = ActivityManager.isLowRamDeviceStatic() ? 1 : 3;
    }

    ServiceRecord getServiceByName(ComponentName name, int callingUser) {
        return getServiceMap(callingUser).mServicesByName.get(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasBackgroundServices(int callingUser) {
        ServiceMap smap = this.mServiceMap.get(callingUser);
        return smap != null && smap.mStartingBackground.size() >= this.mMaxStartingBackground;
    }

    private ServiceMap getServiceMap(int callingUser) {
        ServiceMap smap = this.mServiceMap.get(callingUser);
        if (smap == null) {
            smap = new ServiceMap(this.mAm.mHandler.getLooper(), callingUser);
            this.mServiceMap.put(callingUser, smap);
        }
        return smap;
    }

    ArrayMap<ComponentName, ServiceRecord> getServices(int callingUser) {
        return getServiceMap(callingUser).mServicesByName;
    }

    ComponentName startServiceLocked(IApplicationThread caller, Intent service, String resolvedType, int callingPid, int callingUid, int userId) {
        boolean callerFg;
        if (caller != null) {
            ProcessRecord callerApp = this.mAm.getRecordForAppLocked(caller);
            if (callerApp == null) {
                throw new SecurityException("Unable to find app for caller " + caller + " (pid=" + Binder.getCallingPid() + ") when starting service " + service);
            }
            callerFg = callerApp.setSchedGroup != 0;
        } else {
            callerFg = true;
        }
        ServiceLookupResult res = retrieveServiceLocked(service, resolvedType, callingPid, callingUid, userId, true, callerFg);
        if (res == null) {
            return null;
        }
        if (res.record == null) {
            return new ComponentName("!", res.permission != null ? res.permission : "private to package");
        }
        ServiceRecord r = res.record;
        ActivityManagerService$NeededUriGrants neededGrants = this.mAm.checkGrantUriPermissionFromIntentLocked(callingUid, r.packageName, service, service.getFlags(), (ActivityManagerService$NeededUriGrants) null);
        if (unscheduleServiceRestartLocked(r)) {
        }
        r.lastActivity = SystemClock.uptimeMillis();
        r.startRequested = true;
        r.delayedStop = false;
        r.pendingStarts.add(new ServiceRecord.StartItem(r, false, r.makeNextStartId(), service, neededGrants));
        ServiceMap smap = getServiceMap(r.userId);
        boolean addToStarting = false;
        if (!callerFg && r.app == null && this.mAm.mStartedUsers.get(r.userId) != null) {
            ProcessRecord proc = this.mAm.getProcessRecordLocked(r.processName, r.appInfo.uid, false);
            if (proc == null || proc.curProcState > 8) {
                if (r.delayed) {
                    return r.name;
                }
                if (smap.mStartingBackground.size() >= this.mMaxStartingBackground) {
                    Slog.i(TAG, "Delaying start of: " + r);
                    smap.mDelayedStartList.add(r);
                    r.delayed = true;
                    return r.name;
                }
                addToStarting = true;
            } else if (proc.curProcState >= 7) {
                addToStarting = true;
            }
        }
        return startServiceInnerLocked(smap, service, r, callerFg, addToStarting);
    }

    ComponentName startServiceInnerLocked(ServiceMap smap, Intent service, ServiceRecord r, boolean callerFg, boolean addToStarting) {
        ProcessStats.ServiceState stracker = r.getTracker();
        if (stracker != null) {
            stracker.setStarted(true, this.mAm.mProcessStats.getMemFactorLocked(), r.lastActivity);
        }
        r.callStart = false;
        synchronized (r.stats.getBatteryStats()) {
            r.stats.startRunningLocked();
        }
        String error = bringUpServiceLocked(r, service.getFlags(), callerFg, false);
        if (error != null) {
            return new ComponentName("!!", error);
        }
        if (r.startRequested && addToStarting) {
            boolean first = smap.mStartingBackground.size() == 0;
            smap.mStartingBackground.add(r);
            r.startingBgTimeout = SystemClock.uptimeMillis() + 15000;
            if (first) {
                smap.rescheduleDelayedStarts();
            }
        } else if (callerFg) {
            smap.ensureNotStartingBackground(r);
        }
        return r.name;
    }

    private void stopServiceLocked(ServiceRecord service) {
        if (service.delayed) {
            service.delayedStop = true;
            return;
        }
        synchronized (service.stats.getBatteryStats()) {
            service.stats.stopRunningLocked();
        }
        service.startRequested = false;
        if (service.tracker != null) {
            service.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
        }
        service.callStart = false;
        bringDownServiceIfNeededLocked(service, false, false);
    }

    IBinder peekServiceLocked(Intent service, String resolvedType) {
        ServiceLookupResult r = retrieveServiceLocked(service, resolvedType, Binder.getCallingPid(), Binder.getCallingUid(), UserHandle.getCallingUserId(), false, false);
        IBinder ret = null;
        if (r != null) {
            if (r.record == null) {
                throw new SecurityException("Permission Denial: Accessing service " + r.record.name + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + r.permission);
            }
            IntentBindRecord ib = r.record.bindings.get(r.record.intent);
            if (ib != null) {
                ret = ib.binder;
            }
        }
        return ret;
    }

    boolean stopServiceTokenLocked(ComponentName className, IBinder token, int startId) {
        ServiceRecord r = findServiceLocked(className, token, UserHandle.getCallingUserId());
        if (r != null) {
            if (startId >= 0) {
                ServiceRecord.StartItem si = r.findDeliveredStart(startId, false);
                if (si != null) {
                    while (r.deliveredStarts.size() > 0) {
                        ServiceRecord.StartItem cur = r.deliveredStarts.remove(0);
                        cur.removeUriPermissionsLocked();
                        if (cur == si) {
                            break;
                        }
                    }
                }
                if (r.getLastStartId() != startId) {
                    return false;
                }
                if (r.deliveredStarts.size() > 0) {
                    Slog.w(TAG, "stopServiceToken startId " + startId + " is last, but have " + r.deliveredStarts.size() + " remaining args");
                }
            }
            synchronized (r.stats.getBatteryStats()) {
                r.stats.stopRunningLocked();
            }
            r.startRequested = false;
            if (r.tracker != null) {
                r.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
            }
            r.callStart = false;
            long origId = Binder.clearCallingIdentity();
            bringDownServiceIfNeededLocked(r, false, false);
            Binder.restoreCallingIdentity(origId);
            return true;
        }
        return false;
    }

    private void updateServiceForegroundLocked(ProcessRecord proc, boolean oomAdj) {
        boolean anyForeground = false;
        int i = proc.services.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            ServiceRecord sr = proc.services.valueAt(i);
            if (!sr.isForeground) {
                i--;
            } else {
                anyForeground = true;
                break;
            }
        }
        if (anyForeground != proc.foregroundServices) {
            proc.foregroundServices = anyForeground;
            if (oomAdj) {
                this.mAm.updateOomAdjLocked();
            }
        }
    }

    private final ServiceRecord findServiceLocked(ComponentName name, IBinder token, int userId) {
        ServiceRecord r = getServiceByName(name, userId);
        if (r == token) {
            return r;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActiveServices$ServiceLookupResult.class */
    public final class ServiceLookupResult {
        final ServiceRecord record;
        final String permission;

        ServiceLookupResult(ServiceRecord _record, String _permission) {
            this.record = _record;
            this.permission = _permission;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActiveServices$ServiceRestarter.class */
    public class ServiceRestarter implements Runnable {
        private ServiceRecord mService;

        private ServiceRestarter() {
        }

        void setService(ServiceRecord service) {
            this.mService = service;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ActiveServices.this.mAm) {
                ActiveServices.this.performServiceRestartLocked(this.mService);
            }
        }
    }

    private ServiceLookupResult retrieveServiceLocked(Intent service, String resolvedType, int callingPid, int callingUid, int userId, boolean createIfNeeded, boolean callingFromFg) {
        BatteryStatsImpl.Uid.Pkg.Serv ss;
        ServiceRecord r = null;
        int userId2 = this.mAm.handleIncomingUser(callingPid, callingUid, userId, false, true, "service", (String) null);
        ServiceMap smap = getServiceMap(userId2);
        ComponentName comp = service.getComponent();
        if (comp != null) {
            r = smap.mServicesByName.get(comp);
        }
        if (r == null) {
            r = smap.mServicesByIntent.get(new Intent.FilterComparison(service));
        }
        if (r == null) {
            try {
                ResolveInfo rInfo = AppGlobals.getPackageManager().resolveService(service, resolvedType, 1024, userId2);
                ServiceInfo sInfo = rInfo != null ? rInfo.serviceInfo : null;
                if (sInfo == null) {
                    Slog.w(TAG, "Unable to start service " + service + " U=" + userId2 + ": not found");
                    return null;
                }
                ComponentName name = new ComponentName(sInfo.applicationInfo.packageName, sInfo.name);
                if (userId2 > 0) {
                    if (this.mAm.isSingleton(sInfo.processName, sInfo.applicationInfo, sInfo.name, sInfo.flags)) {
                        userId2 = 0;
                        smap = getServiceMap(0);
                    }
                    sInfo = new ServiceInfo(sInfo);
                    sInfo.applicationInfo = this.mAm.getAppInfoForUser(sInfo.applicationInfo, userId2);
                }
                r = smap.mServicesByName.get(name);
                if (r == null && createIfNeeded) {
                    Intent.FilterComparison filter = new Intent.FilterComparison(service.cloneFilter());
                    ServiceRestarter res = new ServiceRestarter();
                    BatteryStatsImpl stats = this.mAm.mBatteryStatsService.getActiveStatistics();
                    synchronized (stats) {
                        ss = stats.getServiceStatsLocked(sInfo.applicationInfo.uid, sInfo.packageName, sInfo.name);
                    }
                    r = new ServiceRecord(this.mAm, ss, name, filter, sInfo, callingFromFg, res);
                    res.setService(r);
                    smap.mServicesByName.put(name, r);
                    smap.mServicesByIntent.put(filter, r);
                    int N = this.mPendingServices.size();
                    int i = 0;
                    while (i < N) {
                        ServiceRecord pr = this.mPendingServices.get(i);
                        if (pr.serviceInfo.applicationInfo.uid == sInfo.applicationInfo.uid && pr.name.equals(name)) {
                            this.mPendingServices.remove(i);
                            i--;
                            N--;
                        }
                        i++;
                    }
                }
            } catch (RemoteException e) {
            }
        }
        if (r != null) {
            if (this.mAm.checkComponentPermission(r.permission, callingPid, callingUid, r.appInfo.uid, r.exported) != 0) {
                if (!r.exported) {
                    Slog.w(TAG, "Permission Denial: Accessing service " + r.name + " from pid=" + callingPid + ", uid=" + callingUid + " that is not exported from uid " + r.appInfo.uid);
                    return new ServiceLookupResult(null, "not exported from uid " + r.appInfo.uid);
                }
                Slog.w(TAG, "Permission Denial: Accessing service " + r.name + " from pid=" + callingPid + ", uid=" + callingUid + " requires " + r.permission);
                return new ServiceLookupResult(null, r.permission);
            } else if (!this.mAm.mIntentFirewall.checkService(r.name, service, callingUid, callingPid, resolvedType, r.appInfo)) {
                return null;
            } else {
                return new ServiceLookupResult(r, null);
            }
        }
        return null;
    }

    private final void bumpServiceExecutingLocked(ServiceRecord r, boolean fg, String why) {
        long now = SystemClock.uptimeMillis();
        if (r.executeNesting == 0) {
            r.executeFg = fg;
            ProcessStats.ServiceState stracker = r.getTracker();
            if (stracker != null) {
                stracker.setExecuting(true, this.mAm.mProcessStats.getMemFactorLocked(), now);
            }
            if (r.app != null) {
                r.app.executingServices.add(r);
                r.app.execServicesFg |= fg;
                if (r.app.executingServices.size() == 1) {
                    scheduleServiceTimeoutLocked(r.app);
                }
            }
        } else if (r.app != null && fg && !r.app.execServicesFg) {
            r.app.execServicesFg = true;
            scheduleServiceTimeoutLocked(r.app);
        }
        r.executeFg |= fg;
        r.executeNesting++;
        r.executingStart = now;
    }

    private final boolean requestServiceBindingLocked(ServiceRecord r, IntentBindRecord i, boolean execInFg, boolean rebind) {
        if (r.app == null || r.app.thread == null) {
            return false;
        }
        if ((!i.requested || rebind) && i.apps.size() > 0) {
            try {
                bumpServiceExecutingLocked(r, execInFg, "bind");
                r.app.forceProcessStateUpTo(7);
                r.app.thread.scheduleBindService(r, i.intent.getIntent(), rebind, r.app.repProcState);
                if (!rebind) {
                    i.requested = true;
                }
                i.hasBound = true;
                i.doRebind = false;
                return true;
            } catch (RemoteException e) {
                return false;
            }
        }
        return true;
    }

    private final boolean scheduleServiceRestartLocked(ServiceRecord r, boolean allowCancel) {
        boolean repeat;
        boolean canceled = false;
        long now = SystemClock.uptimeMillis();
        if ((r.serviceInfo.applicationInfo.flags & 8) == 0) {
            long minDuration = 5000;
            long resetTime = 60000;
            int N = r.deliveredStarts.size();
            if (N > 0) {
                for (int i = N - 1; i >= 0; i--) {
                    ServiceRecord.StartItem si = r.deliveredStarts.get(i);
                    si.removeUriPermissionsLocked();
                    if (si.intent != null) {
                        if (!allowCancel || (si.deliveryCount < 3 && si.doneExecutingCount < 6)) {
                            r.pendingStarts.add(0, si);
                            long dur = (SystemClock.uptimeMillis() - si.deliveredTime) * 2;
                            if (minDuration < dur) {
                                minDuration = dur;
                            }
                            if (resetTime < dur) {
                                resetTime = dur;
                            }
                        } else {
                            Slog.w(TAG, "Canceling start item " + si.intent + " in service " + r.name);
                            canceled = true;
                        }
                    }
                }
                r.deliveredStarts.clear();
            }
            r.totalRestartCount++;
            if (r.restartDelay == 0) {
                r.restartCount++;
                r.restartDelay = minDuration;
            } else if (now > r.restartTime + resetTime) {
                r.restartCount = 1;
                r.restartDelay = minDuration;
            } else if ((r.serviceInfo.applicationInfo.flags & 8) != 0) {
                r.restartDelay += minDuration / 2;
            } else {
                r.restartDelay *= 4;
                if (r.restartDelay < minDuration) {
                    r.restartDelay = minDuration;
                }
            }
            r.nextRestartTime = now + r.restartDelay;
            do {
                repeat = false;
                int i2 = this.mRestartingServices.size() - 1;
                while (true) {
                    if (i2 < 0) {
                        break;
                    }
                    ServiceRecord r2 = this.mRestartingServices.get(i2);
                    if (r2 != r && r.nextRestartTime >= r2.nextRestartTime - 10000 && r.nextRestartTime < r2.nextRestartTime + 10000) {
                        r.nextRestartTime = r2.nextRestartTime + 10000;
                        r.restartDelay = r.nextRestartTime - now;
                        repeat = true;
                        break;
                    }
                    i2--;
                }
            } while (repeat);
        } else {
            r.totalRestartCount++;
            r.restartCount = 0;
            r.restartDelay = 0L;
            r.nextRestartTime = now;
        }
        if (!this.mRestartingServices.contains(r)) {
            r.createdFromFg = false;
            this.mRestartingServices.add(r);
        }
        r.cancelNotification();
        this.mAm.mHandler.removeCallbacks(r.restarter);
        this.mAm.mHandler.postAtTime(r.restarter, r.nextRestartTime);
        r.nextRestartTime = SystemClock.uptimeMillis() + r.restartDelay;
        Slog.w(TAG, "Scheduling restart of crashed service " + r.shortName + " in " + r.restartDelay + "ms");
        EventLog.writeEvent((int) EventLogTags.AM_SCHEDULE_SERVICE_RESTART, Integer.valueOf(r.userId), r.shortName, Long.valueOf(r.restartDelay));
        return canceled;
    }

    final void performServiceRestartLocked(ServiceRecord r) {
        if (!this.mRestartingServices.contains(r)) {
            return;
        }
        bringUpServiceLocked(r, r.intent.getIntent().getFlags(), r.createdFromFg, true);
    }

    private final boolean unscheduleServiceRestartLocked(ServiceRecord r) {
        if (r.restartDelay == 0) {
            return false;
        }
        r.resetRestartCounter();
        this.mRestartingServices.remove(r);
        this.mAm.mHandler.removeCallbacks(r.restarter);
        return true;
    }

    private final String bringUpServiceLocked(ServiceRecord r, int intentFlags, boolean execInFg, boolean whileRestarting) {
        ProcessRecord app;
        if (r.app != null && r.app.thread != null) {
            sendServiceArgsLocked(r, execInFg, false);
            return null;
        } else if (!whileRestarting && r.restartDelay > 0) {
            return null;
        } else {
            this.mRestartingServices.remove(r);
            if (r.delayed) {
                getServiceMap(r.userId).mDelayedStartList.remove(r);
                r.delayed = false;
            }
            if (this.mAm.mStartedUsers.get(r.userId) == null) {
                String msg = "Unable to launch app " + r.appInfo.packageName + Separators.SLASH + r.appInfo.uid + " for service " + r.intent.getIntent() + ": user " + r.userId + " is stopped";
                Slog.w(TAG, msg);
                bringDownServiceLocked(r);
                return msg;
            }
            try {
                AppGlobals.getPackageManager().setPackageStoppedState(r.packageName, false, r.userId);
            } catch (RemoteException e) {
            } catch (IllegalArgumentException e2) {
                Slog.w(TAG, "Failed trying to unstop package " + r.packageName + ": " + e2);
            }
            boolean isolated = (r.serviceInfo.flags & 2) != 0;
            String procName = r.processName;
            if (!isolated) {
                app = this.mAm.getProcessRecordLocked(procName, r.appInfo.uid, false);
                if (app != null && app.thread != null) {
                    try {
                        app.addPackage(r.appInfo.packageName, this.mAm.mProcessStats);
                        realStartServiceLocked(r, app, execInFg);
                        return null;
                    } catch (RemoteException e3) {
                        Slog.w(TAG, "Exception when starting service " + r.shortName, e3);
                    }
                }
            } else {
                app = r.isolatedProc;
            }
            if (app == null) {
                ProcessRecord app2 = this.mAm.startProcessLocked(procName, r.appInfo, true, intentFlags, "service", r.name, false, isolated, false);
                if (app2 == null) {
                    String msg2 = "Unable to launch app " + r.appInfo.packageName + Separators.SLASH + r.appInfo.uid + " for service " + r.intent.getIntent() + ": process is bad";
                    Slog.w(TAG, msg2);
                    bringDownServiceLocked(r);
                    return msg2;
                } else if (isolated) {
                    r.isolatedProc = app2;
                }
            }
            if (!this.mPendingServices.contains(r)) {
                this.mPendingServices.add(r);
            }
            if (r.delayedStop) {
                r.delayedStop = false;
                if (r.startRequested) {
                    stopServiceLocked(r);
                    return null;
                }
                return null;
            }
            return null;
        }
    }

    private final void requestServiceBindingsLocked(ServiceRecord r, boolean execInFg) {
        for (int i = r.bindings.size() - 1; i >= 0; i--) {
            IntentBindRecord ibr = r.bindings.valueAt(i);
            if (!requestServiceBindingLocked(r, ibr, execInFg, false)) {
                return;
            }
        }
    }

    private final void sendServiceArgsLocked(ServiceRecord r, boolean execInFg, boolean oomAdjusted) {
        int N = r.pendingStarts.size();
        if (N == 0) {
            return;
        }
        while (r.pendingStarts.size() > 0) {
            try {
                ServiceRecord.StartItem si = r.pendingStarts.remove(0);
                if (si.intent != null || N <= 1) {
                    si.deliveredTime = SystemClock.uptimeMillis();
                    r.deliveredStarts.add(si);
                    si.deliveryCount++;
                    if (si.neededGrants != null) {
                        this.mAm.grantUriPermissionUncheckedFromIntentLocked(si.neededGrants, si.getUriPermissionsLocked());
                    }
                    bumpServiceExecutingLocked(r, execInFg, Telephony.BaseMmsColumns.START);
                    if (!oomAdjusted) {
                        oomAdjusted = true;
                        this.mAm.updateOomAdjLocked(r.app);
                    }
                    int flags = 0;
                    if (si.deliveryCount > 1) {
                        flags = 0 | 2;
                    }
                    if (si.doneExecutingCount > 0) {
                        flags |= 1;
                    }
                    r.app.thread.scheduleServiceArgs(r, si.taskRemoved, si.id, flags, si.intent);
                }
            } catch (RemoteException e) {
                return;
            } catch (Exception e2) {
                Slog.w(TAG, "Unexpected exception", e2);
                return;
            }
        }
    }

    private final boolean isServiceNeeded(ServiceRecord r, boolean knowConn, boolean hasConn) {
        if (r.startRequested) {
            return true;
        }
        if (!knowConn) {
            hasConn = r.hasAutoCreateConnections();
        }
        if (hasConn) {
            return true;
        }
        return false;
    }

    private final void bringDownServiceIfNeededLocked(ServiceRecord r, boolean knowConn, boolean hasConn) {
        if (isServiceNeeded(r, knowConn, hasConn) || this.mPendingServices.contains(r)) {
            return;
        }
        bringDownServiceLocked(r);
    }

    private final void bringDownServiceLocked(ServiceRecord r) {
        for (int conni = r.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> c = r.connections.valueAt(conni);
            for (int i = 0; i < c.size(); i++) {
                ConnectionRecord cr = c.get(i);
                cr.serviceDead = true;
                try {
                    cr.conn.connected(r.name, null);
                } catch (Exception e) {
                    Slog.w(TAG, "Failure disconnecting service " + r.name + " to connection " + c.get(i).conn.asBinder() + " (in " + c.get(i).binding.client.processName + Separators.RPAREN, e);
                }
            }
        }
        if (r.app != null && r.app.thread != null) {
            for (int i2 = r.bindings.size() - 1; i2 >= 0; i2--) {
                IntentBindRecord ibr = r.bindings.valueAt(i2);
                if (ibr.hasBound) {
                    try {
                        bumpServiceExecutingLocked(r, false, "bring down unbind");
                        this.mAm.updateOomAdjLocked(r.app);
                        ibr.hasBound = false;
                        r.app.thread.scheduleUnbindService(r, ibr.intent.getIntent());
                    } catch (Exception e2) {
                        Slog.w(TAG, "Exception when unbinding service " + r.shortName, e2);
                        serviceProcessGoneLocked(r);
                    }
                }
            }
        }
        EventLogTags.writeAmDestroyService(r.userId, System.identityHashCode(r), r.app != null ? r.app.pid : -1);
        ServiceMap smap = getServiceMap(r.userId);
        smap.mServicesByName.remove(r.name);
        smap.mServicesByIntent.remove(r.intent);
        r.totalRestartCount = 0;
        unscheduleServiceRestartLocked(r);
        int N = this.mPendingServices.size();
        int i3 = 0;
        while (i3 < N) {
            if (this.mPendingServices.get(i3) == r) {
                this.mPendingServices.remove(i3);
                i3--;
                N--;
            }
            i3++;
        }
        r.cancelNotification();
        r.isForeground = false;
        r.foregroundId = 0;
        r.foregroundNoti = null;
        r.clearDeliveredStartsLocked();
        r.pendingStarts.clear();
        if (r.app != null) {
            synchronized (r.stats.getBatteryStats()) {
                r.stats.stopLaunchedLocked();
            }
            r.app.services.remove(r);
            if (r.app.thread != null) {
                try {
                    bumpServiceExecutingLocked(r, false, "destroy");
                    this.mDestroyingServices.add(r);
                    this.mAm.updateOomAdjLocked(r.app);
                    r.app.thread.scheduleStopService(r);
                } catch (Exception e3) {
                    Slog.w(TAG, "Exception when destroying service " + r.shortName, e3);
                    serviceProcessGoneLocked(r);
                }
                updateServiceForegroundLocked(r.app, false);
            }
        }
        if (r.bindings.size() > 0) {
            r.bindings.clear();
        }
        if (r.restarter instanceof ServiceRestarter) {
            ((ServiceRestarter) r.restarter).setService(null);
        }
        int memFactor = this.mAm.mProcessStats.getMemFactorLocked();
        long now = SystemClock.uptimeMillis();
        if (r.tracker != null) {
            r.tracker.setStarted(false, memFactor, now);
            r.tracker.setBound(false, memFactor, now);
            if (r.executeNesting == 0) {
                r.tracker.clearCurrentOwner(r, false);
                r.tracker = null;
            }
        }
        smap.ensureNotStartingBackground(r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeConnectionLocked(ConnectionRecord c, ProcessRecord skipApp, ActivityRecord skipAct) {
        IBinder binder = c.conn.asBinder();
        AppBindRecord b = c.binding;
        ServiceRecord s = b.service;
        ArrayList<ConnectionRecord> clist = s.connections.get(binder);
        if (clist != null) {
            clist.remove(c);
            if (clist.size() == 0) {
                s.connections.remove(binder);
            }
        }
        b.connections.remove(c);
        if (c.activity != null && c.activity != skipAct && c.activity.connections != null) {
            c.activity.connections.remove(c);
        }
        if (b.client != skipApp) {
            b.client.connections.remove(c);
            if ((c.flags & 8) != 0) {
                b.client.updateHasAboveClientLocked();
            }
        }
        ArrayList<ConnectionRecord> clist2 = this.mServiceConnections.get(binder);
        if (clist2 != null) {
            clist2.remove(c);
            if (clist2.size() == 0) {
                this.mServiceConnections.remove(binder);
            }
        }
        if (b.connections.size() == 0) {
            b.intent.apps.remove(b.client);
        }
        if (!c.serviceDead) {
            if (s.app != null && s.app.thread != null && b.intent.apps.size() == 0 && b.intent.hasBound) {
                try {
                    bumpServiceExecutingLocked(s, false, "unbind");
                    this.mAm.updateOomAdjLocked(s.app);
                    b.intent.hasBound = false;
                    b.intent.doRebind = false;
                    s.app.thread.scheduleUnbindService(s, b.intent.intent.getIntent());
                } catch (Exception e) {
                    Slog.w(TAG, "Exception when unbinding service " + s.shortName, e);
                    serviceProcessGoneLocked(s);
                }
            }
            if ((c.flags & 1) != 0) {
                boolean hasAutoCreate = s.hasAutoCreateConnections();
                if (!hasAutoCreate && s.tracker != null) {
                    s.tracker.setBound(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
                bringDownServiceIfNeededLocked(s, true, hasAutoCreate);
            }
        }
    }

    void serviceDoneExecutingLocked(ServiceRecord r, int type, int startId, int res) {
        boolean inDestroying = this.mDestroyingServices.contains(r);
        if (r != null) {
            if (type == 1) {
                r.callStart = true;
                switch (res) {
                    case 0:
                    case 1:
                        r.findDeliveredStart(startId, true);
                        r.stopIfKilled = false;
                        break;
                    case 2:
                        r.findDeliveredStart(startId, true);
                        if (r.getLastStartId() == startId) {
                            r.stopIfKilled = true;
                            break;
                        }
                        break;
                    case 3:
                        ServiceRecord.StartItem si = r.findDeliveredStart(startId, false);
                        if (si != null) {
                            si.deliveryCount = 0;
                            si.doneExecutingCount++;
                            r.stopIfKilled = true;
                            break;
                        }
                        break;
                    case 1000:
                        r.findDeliveredStart(startId, true);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown service start result: " + res);
                }
                if (res == 0) {
                    r.callStart = false;
                }
            }
            long origId = Binder.clearCallingIdentity();
            serviceDoneExecutingLocked(r, inDestroying, inDestroying);
            Binder.restoreCallingIdentity(origId);
            return;
        }
        Slog.w(TAG, "Done executing unknown service from pid " + Binder.getCallingPid());
    }

    private void serviceProcessGoneLocked(ServiceRecord r) {
        if (r.tracker != null) {
            int memFactor = this.mAm.mProcessStats.getMemFactorLocked();
            long now = SystemClock.uptimeMillis();
            r.tracker.setExecuting(false, memFactor, now);
            r.tracker.setBound(false, memFactor, now);
        }
        serviceDoneExecutingLocked(r, true, true);
    }

    private void serviceDoneExecutingLocked(ServiceRecord r, boolean inDestroying, boolean finishing) {
        r.executeNesting--;
        if (r.executeNesting <= 0) {
            if (r.app != null) {
                r.app.execServicesFg = false;
                r.app.executingServices.remove(r);
                if (r.app.executingServices.size() == 0) {
                    this.mAm.mHandler.removeMessages(12, r.app);
                } else if (r.executeFg) {
                    int i = r.app.executingServices.size() - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        } else if (!r.app.executingServices.valueAt(i).executeFg) {
                            i--;
                        } else {
                            r.app.execServicesFg = true;
                            break;
                        }
                    }
                }
                if (inDestroying) {
                    this.mDestroyingServices.remove(r);
                    r.bindings.clear();
                }
                this.mAm.updateOomAdjLocked(r.app);
            }
            r.executeFg = false;
            if (r.tracker != null) {
                r.tracker.setExecuting(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                if (finishing) {
                    r.tracker.clearCurrentOwner(r, false);
                    r.tracker = null;
                }
            }
        }
    }

    boolean attachApplicationLocked(ProcessRecord proc, String processName) throws Exception {
        boolean didSomething = false;
        if (this.mPendingServices.size() > 0) {
            ServiceRecord sr = null;
            int i = 0;
            while (i < this.mPendingServices.size()) {
                try {
                    sr = this.mPendingServices.get(i);
                    if (proc == sr.isolatedProc || (proc.uid == sr.appInfo.uid && processName.equals(sr.processName))) {
                        this.mPendingServices.remove(i);
                        i--;
                        proc.addPackage(sr.appInfo.packageName, this.mAm.mProcessStats);
                        realStartServiceLocked(sr, proc, sr.createdFromFg);
                        didSomething = true;
                    }
                    i++;
                } catch (Exception e) {
                    Slog.w(TAG, "Exception in new application when starting service " + sr.shortName, e);
                    throw e;
                }
            }
        }
        if (this.mRestartingServices.size() > 0) {
            for (int i2 = 0; i2 < this.mRestartingServices.size(); i2++) {
                ServiceRecord sr2 = this.mRestartingServices.get(i2);
                if (proc == sr2.isolatedProc || (proc.uid == sr2.appInfo.uid && processName.equals(sr2.processName))) {
                    this.mAm.mHandler.removeCallbacks(sr2.restarter);
                    this.mAm.mHandler.post(sr2.restarter);
                }
            }
        }
        return didSomething;
    }

    void processStartTimedOutLocked(ProcessRecord proc) {
        int i = 0;
        while (i < this.mPendingServices.size()) {
            ServiceRecord sr = this.mPendingServices.get(i);
            if ((proc.uid == sr.appInfo.uid && proc.processName.equals(sr.processName)) || sr.isolatedProc == proc) {
                Slog.w(TAG, "Forcing bringing down service: " + sr);
                sr.isolatedProc = null;
                this.mPendingServices.remove(i);
                i--;
                bringDownServiceLocked(sr);
            }
            i++;
        }
    }

    private boolean collectForceStopServicesLocked(String name, int userId, boolean evenPersistent, boolean doit, ArrayMap<ComponentName, ServiceRecord> services, ArrayList<ServiceRecord> result) {
        boolean didSomething = false;
        for (int i = 0; i < services.size(); i++) {
            ServiceRecord service = services.valueAt(i);
            if ((name == null || service.packageName.equals(name)) && (service.app == null || evenPersistent || !service.app.persistent)) {
                if (!doit) {
                    return true;
                }
                didSomething = true;
                Slog.i(TAG, "  Force stopping service " + service);
                if (service.app != null) {
                    service.app.removed = true;
                }
                service.app = null;
                service.isolatedProc = null;
                result.add(service);
            }
        }
        return didSomething;
    }

    boolean forceStopLocked(String name, int userId, boolean evenPersistent, boolean doit) {
        boolean didSomething = false;
        ArrayList<ServiceRecord> services = new ArrayList<>();
        if (userId == -1) {
            for (int i = 0; i < this.mServiceMap.size(); i++) {
                didSomething |= collectForceStopServicesLocked(name, userId, evenPersistent, doit, this.mServiceMap.valueAt(i).mServicesByName, services);
                if (!doit && didSomething) {
                    return true;
                }
            }
        } else {
            ServiceMap smap = this.mServiceMap.get(userId);
            if (smap != null) {
                ArrayMap<ComponentName, ServiceRecord> items = smap.mServicesByName;
                didSomething = collectForceStopServicesLocked(name, userId, evenPersistent, doit, items, services);
            }
        }
        int N = services.size();
        for (int i2 = 0; i2 < N; i2++) {
            bringDownServiceLocked(services.get(i2));
        }
        return didSomething;
    }

    void cleanUpRemovedTaskLocked(TaskRecord tr, ComponentName component, Intent baseIntent) {
        ArrayList<ServiceRecord> services = new ArrayList<>();
        ArrayMap<ComponentName, ServiceRecord> alls = getServices(tr.userId);
        for (int i = 0; i < alls.size(); i++) {
            ServiceRecord sr = alls.valueAt(i);
            if (sr.packageName.equals(component.getPackageName())) {
                services.add(sr);
            }
        }
        for (int i2 = 0; i2 < services.size(); i2++) {
            ServiceRecord sr2 = services.get(i2);
            if (sr2.startRequested) {
                if ((sr2.serviceInfo.flags & 1) != 0) {
                    Slog.i(TAG, "Stopping service " + sr2.shortName + ": remove task");
                    stopServiceLocked(sr2);
                } else {
                    sr2.pendingStarts.add(new ServiceRecord.StartItem(sr2, true, sr2.makeNextStartId(), baseIntent, null));
                    if (sr2.app != null && sr2.app.thread != null) {
                        sendServiceArgsLocked(sr2, true, false);
                    }
                }
            }
        }
    }

    final void killServicesLocked(ProcessRecord app, boolean allowRestart) {
        for (int i = app.connections.size() - 1; i >= 0; i--) {
            ConnectionRecord r = app.connections.valueAt(i);
            removeConnectionLocked(r, app, null);
        }
        app.connections.clear();
        for (int i2 = app.services.size() - 1; i2 >= 0; i2--) {
            ServiceRecord sr = app.services.valueAt(i2);
            synchronized (sr.stats.getBatteryStats()) {
                sr.stats.stopLaunchedLocked();
            }
            sr.app = null;
            sr.isolatedProc = null;
            sr.executeNesting = 0;
            sr.forceClearTracker();
            if (this.mDestroyingServices.remove(sr)) {
            }
            int numClients = sr.bindings.size();
            for (int bindingi = numClients - 1; bindingi >= 0; bindingi--) {
                IntentBindRecord b = sr.bindings.valueAt(bindingi);
                b.binder = null;
                b.hasBound = false;
                b.received = false;
                b.requested = false;
            }
            if (sr.crashCount >= 2 && (sr.serviceInfo.applicationInfo.flags & 8) == 0) {
                Slog.w(TAG, "Service crashed " + sr.crashCount + " times, stopping: " + sr);
                EventLog.writeEvent((int) EventLogTags.AM_SERVICE_CRASHED_TOO_MUCH, Integer.valueOf(sr.userId), Integer.valueOf(sr.crashCount), sr.shortName, Integer.valueOf(app.pid));
                bringDownServiceLocked(sr);
            } else if (!allowRestart) {
                bringDownServiceLocked(sr);
            } else {
                boolean canceled = scheduleServiceRestartLocked(sr, true);
                if (sr.startRequested && ((sr.stopIfKilled || canceled) && sr.pendingStarts.size() == 0)) {
                    sr.startRequested = false;
                    if (sr.tracker != null) {
                        sr.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                    }
                    if (!sr.hasAutoCreateConnections()) {
                        bringDownServiceLocked(sr);
                    }
                }
            }
        }
        if (!allowRestart) {
            app.services.clear();
        }
        int i3 = this.mDestroyingServices.size();
        while (i3 > 0) {
            i3--;
            ServiceRecord sr2 = this.mDestroyingServices.get(i3);
            if (sr2.app == app) {
                sr2.forceClearTracker();
                this.mDestroyingServices.remove(i3);
            }
        }
        app.executingServices.clear();
    }

    ActivityManager.RunningServiceInfo makeRunningServiceInfoLocked(ServiceRecord r) {
        ActivityManager.RunningServiceInfo info = new ActivityManager.RunningServiceInfo();
        info.service = r.name;
        if (r.app != null) {
            info.pid = r.app.pid;
        }
        info.uid = r.appInfo.uid;
        info.process = r.processName;
        info.foreground = r.isForeground;
        info.activeSince = r.createTime;
        info.started = r.startRequested;
        info.clientCount = r.connections.size();
        info.crashCount = r.crashCount;
        info.lastActivityTime = r.lastActivity;
        if (r.isForeground) {
            info.flags |= 2;
        }
        if (r.startRequested) {
            info.flags |= 1;
        }
        if (r.app != null && r.app.pid == ActivityManagerService.MY_PID) {
            info.flags |= 4;
        }
        if (r.app != null && r.app.persistent) {
            info.flags |= 8;
        }
        for (int conni = r.connections.size() - 1; conni >= 0; conni--) {
            ArrayList<ConnectionRecord> connl = r.connections.valueAt(conni);
            for (int i = 0; i < connl.size(); i++) {
                ConnectionRecord conn = connl.get(i);
                if (conn.clientLabel != 0) {
                    info.clientPackage = conn.binding.client.info.packageName;
                    info.clientLabel = conn.clientLabel;
                    return info;
                }
            }
        }
        return info;
    }

    public PendingIntent getRunningServiceControlPanelLocked(ComponentName name) {
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        ServiceRecord r = getServiceByName(name, userId);
        if (r != null) {
            for (int conni = r.connections.size() - 1; conni >= 0; conni--) {
                ArrayList<ConnectionRecord> conn = r.connections.valueAt(conni);
                for (int i = 0; i < conn.size(); i++) {
                    if (conn.get(i).clientIntent != null) {
                        return conn.get(i).clientIntent;
                    }
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void serviceTimeout(ProcessRecord proc) {
        String anrMessage = null;
        synchronized (this) {
            if (proc.executingServices.size() == 0 || proc.thread == null) {
                return;
            }
            long maxTime = SystemClock.uptimeMillis() - (proc.execServicesFg ? 20000 : SERVICE_BACKGROUND_TIMEOUT);
            ServiceRecord timeout = null;
            long nextTime = 0;
            int i = proc.executingServices.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                ServiceRecord sr = proc.executingServices.valueAt(i);
                if (sr.executingStart < maxTime) {
                    timeout = sr;
                    break;
                }
                if (sr.executingStart > nextTime) {
                    nextTime = sr.executingStart;
                }
                i--;
            }
            if (timeout != null && this.mAm.mLruProcesses.contains(proc)) {
                Slog.w(TAG, "Timeout executing service: " + timeout);
                anrMessage = "Executing service " + timeout.shortName;
            } else {
                Message msg = this.mAm.mHandler.obtainMessage(12);
                msg.obj = proc;
                this.mAm.mHandler.sendMessageAtTime(msg, proc.execServicesFg ? nextTime + 20000 : nextTime + 200000);
            }
            if (anrMessage != null) {
                this.mAm.appNotResponding(proc, (ActivityRecord) null, (ActivityRecord) null, false, anrMessage);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleServiceTimeoutLocked(ProcessRecord proc) {
        if (proc.executingServices.size() == 0 || proc.thread == null) {
            return;
        }
        long now = SystemClock.uptimeMillis();
        Message msg = this.mAm.mHandler.obtainMessage(12);
        msg.obj = proc;
        this.mAm.mHandler.sendMessageAtTime(msg, proc.execServicesFg ? now + 20000 : now + 200000);
    }

    protected boolean dumpService(FileDescriptor fd, PrintWriter pw, String name, String[] args, int opti, boolean dumpAll) {
        ArrayList<ServiceRecord> services = new ArrayList<>();
        synchronized (this) {
            int[] users = this.mAm.getUsersLocked();
            if ("all".equals(name)) {
                for (int user : users) {
                    ServiceMap smap = this.mServiceMap.get(user);
                    if (smap != null) {
                        ArrayMap<ComponentName, ServiceRecord> alls = smap.mServicesByName;
                        for (int i = 0; i < alls.size(); i++) {
                            services.add(alls.valueAt(i));
                        }
                    }
                }
            } else {
                ComponentName componentName = name != null ? ComponentName.unflattenFromString(name) : null;
                int objectId = 0;
                if (componentName == null) {
                    try {
                        objectId = Integer.parseInt(name, 16);
                        name = null;
                        componentName = null;
                    } catch (RuntimeException e) {
                    }
                }
                for (int user2 : users) {
                    ServiceMap smap2 = this.mServiceMap.get(user2);
                    if (smap2 != null) {
                        ArrayMap<ComponentName, ServiceRecord> alls2 = smap2.mServicesByName;
                        for (int i2 = 0; i2 < alls2.size(); i2++) {
                            ServiceRecord r1 = alls2.valueAt(i2);
                            if (componentName != null) {
                                if (r1.name.equals(componentName)) {
                                    services.add(r1);
                                }
                            } else if (name != null) {
                                if (r1.name.flattenToString().contains(name)) {
                                    services.add(r1);
                                }
                            } else if (System.identityHashCode(r1) == objectId) {
                                services.add(r1);
                            }
                        }
                    }
                }
            }
        }
        if (services.size() <= 0) {
            return false;
        }
        boolean needSep = false;
        for (int i3 = 0; i3 < services.size(); i3++) {
            if (needSep) {
                pw.println();
            }
            needSep = true;
            dumpService("", fd, pw, services.get(i3), args, dumpAll);
        }
        return true;
    }
}