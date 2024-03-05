package com.android.server.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.IPrintService;
import android.printservice.IPrintServiceClient;
import android.util.Slog;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: RemotePrintService.class */
public final class RemotePrintService implements IBinder.DeathRecipient {
    private static final String LOG_TAG = "RemotePrintService";
    private static final boolean DEBUG = false;
    private final Context mContext;
    private final ComponentName mComponentName;
    private final Intent mIntent;
    private final RemotePrintSpooler mSpooler;
    private final PrintServiceCallbacks mCallbacks;
    private final int mUserId;
    private final List<Runnable> mPendingCommands = new ArrayList();
    private final ServiceConnection mServiceConnection = new RemoteServiceConneciton();
    private final RemotePrintServiceClient mPrintServiceClient = new RemotePrintServiceClient(this);
    private final Handler mHandler;
    private IPrintService mPrintService;
    private boolean mBinding;
    private boolean mDestroyed;
    private boolean mHasActivePrintJobs;
    private boolean mHasPrinterDiscoverySession;
    private boolean mServiceDied;
    private List<PrinterId> mDiscoveryPriorityList;
    private List<PrinterId> mTrackedPrinterList;

    /* loaded from: RemotePrintService$PrintServiceCallbacks.class */
    public interface PrintServiceCallbacks {
        void onPrintersAdded(List<PrinterInfo> list);

        void onPrintersRemoved(List<PrinterId> list);

        void onServiceDied(RemotePrintService remotePrintService);
    }

    static /* synthetic */ ComponentName access$2600(RemotePrintService x0) {
        return x0.mComponentName;
    }

    static /* synthetic */ RemotePrintSpooler access$2700(RemotePrintService x0) {
        return x0.mSpooler;
    }

    static /* synthetic */ PrintServiceCallbacks access$2800(RemotePrintService x0) {
        return x0.mCallbacks;
    }

    public RemotePrintService(Context context, ComponentName componentName, int userId, RemotePrintSpooler spooler, PrintServiceCallbacks callbacks) {
        this.mContext = context;
        this.mCallbacks = callbacks;
        this.mComponentName = componentName;
        this.mIntent = new Intent().setComponent(this.mComponentName);
        this.mUserId = userId;
        this.mSpooler = spooler;
        this.mHandler = new MyHandler(context.getMainLooper());
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public void destroy() {
        this.mHandler.sendEmptyMessage(11);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroy() {
        throwIfDestroyed();
        if (this.mTrackedPrinterList != null) {
            int trackedPrinterCount = this.mTrackedPrinterList.size();
            for (int i = 0; i < trackedPrinterCount; i++) {
                PrinterId printerId = this.mTrackedPrinterList.get(i);
                if (printerId.getServiceName().equals(this.mComponentName)) {
                    handleStopPrinterStateTracking(printerId);
                }
            }
        }
        if (this.mDiscoveryPriorityList != null) {
            handleStopPrinterDiscovery();
        }
        if (this.mHasPrinterDiscoverySession) {
            handleDestroyPrinterDiscoverySession();
        }
        ensureUnbound();
        this.mDestroyed = true;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.mHandler.sendEmptyMessage(12);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBinderDied() {
        this.mPrintService.asBinder().unlinkToDeath(this, 0);
        this.mPrintService = null;
        this.mServiceDied = true;
        this.mCallbacks.onServiceDied(this);
    }

    public void onAllPrintJobsHandled() {
        this.mHandler.sendEmptyMessage(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnAllPrintJobsHandled() {
        throwIfDestroyed();
        this.mHasActivePrintJobs = false;
        if (!isBound()) {
            if (this.mServiceDied && !this.mHasPrinterDiscoverySession) {
                ensureUnbound();
                return;
            }
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.1
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleOnAllPrintJobsHandled();
                }
            });
        } else if (!this.mHasPrinterDiscoverySession) {
            ensureUnbound();
        }
    }

    public void onRequestCancelPrintJob(PrintJobInfo printJob) {
        this.mHandler.obtainMessage(9, printJob).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRequestCancelPrintJob(final PrintJobInfo printJob) {
        throwIfDestroyed();
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.2
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleRequestCancelPrintJob(printJob);
                }
            });
            return;
        }
        try {
            this.mPrintService.requestCancelPrintJob(printJob);
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error canceling a pring job.", re);
        }
    }

    public void onPrintJobQueued(PrintJobInfo printJob) {
        this.mHandler.obtainMessage(10, printJob).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnPrintJobQueued(final PrintJobInfo printJob) {
        throwIfDestroyed();
        this.mHasActivePrintJobs = true;
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.3
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleOnPrintJobQueued(printJob);
                }
            });
            return;
        }
        try {
            this.mPrintService.onPrintJobQueued(printJob);
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error announcing queued pring job.", re);
        }
    }

    public void createPrinterDiscoverySession() {
        this.mHandler.sendEmptyMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCreatePrinterDiscoverySession() {
        throwIfDestroyed();
        this.mHasPrinterDiscoverySession = true;
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.4
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleCreatePrinterDiscoverySession();
                }
            });
            return;
        }
        try {
            this.mPrintService.createPrinterDiscoverySession();
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error creating printer dicovery session.", re);
        }
    }

    public void destroyPrinterDiscoverySession() {
        this.mHandler.sendEmptyMessage(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyPrinterDiscoverySession() {
        throwIfDestroyed();
        this.mHasPrinterDiscoverySession = false;
        if (!isBound()) {
            if (this.mServiceDied && !this.mHasActivePrintJobs) {
                ensureUnbound();
                return;
            }
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.5
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleDestroyPrinterDiscoverySession();
                }
            });
            return;
        }
        try {
            this.mPrintService.destroyPrinterDiscoverySession();
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error destroying printer dicovery session.", re);
        }
        if (!this.mHasActivePrintJobs) {
            ensureUnbound();
        }
    }

    public void startPrinterDiscovery(List<PrinterId> priorityList) {
        this.mHandler.obtainMessage(3, priorityList).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStartPrinterDiscovery(final List<PrinterId> priorityList) {
        throwIfDestroyed();
        this.mDiscoveryPriorityList = new ArrayList();
        if (priorityList != null) {
            this.mDiscoveryPriorityList.addAll(priorityList);
        }
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.6
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleStartPrinterDiscovery(priorityList);
                }
            });
            return;
        }
        try {
            this.mPrintService.startPrinterDiscovery(priorityList);
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error starting printer dicovery.", re);
        }
    }

    public void stopPrinterDiscovery() {
        this.mHandler.sendEmptyMessage(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopPrinterDiscovery() {
        throwIfDestroyed();
        this.mDiscoveryPriorityList = null;
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.7
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleStopPrinterDiscovery();
                }
            });
            return;
        }
        try {
            this.mPrintService.stopPrinterDiscovery();
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error stopping printer dicovery.", re);
        }
    }

    public void validatePrinters(List<PrinterId> printerIds) {
        this.mHandler.obtainMessage(5, printerIds).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleValidatePrinters(final List<PrinterId> printerIds) {
        throwIfDestroyed();
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.8
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleValidatePrinters(printerIds);
                }
            });
            return;
        }
        try {
            this.mPrintService.validatePrinters(printerIds);
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error requesting printers validation.", re);
        }
    }

    public void startPrinterStateTracking(PrinterId printerId) {
        this.mHandler.obtainMessage(6, printerId).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStartPrinterStateTracking(final PrinterId printerId) {
        throwIfDestroyed();
        if (this.mTrackedPrinterList == null) {
            this.mTrackedPrinterList = new ArrayList();
        }
        this.mTrackedPrinterList.add(printerId);
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.9
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleStartPrinterStateTracking(printerId);
                }
            });
            return;
        }
        try {
            this.mPrintService.startPrinterStateTracking(printerId);
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error requesting start printer tracking.", re);
        }
    }

    public void stopPrinterStateTracking(PrinterId printerId) {
        this.mHandler.obtainMessage(7, printerId).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopPrinterStateTracking(final PrinterId printerId) {
        throwIfDestroyed();
        if (this.mTrackedPrinterList == null || !this.mTrackedPrinterList.remove(printerId)) {
            return;
        }
        if (this.mTrackedPrinterList.isEmpty()) {
            this.mTrackedPrinterList = null;
        }
        if (!isBound()) {
            ensureBound();
            this.mPendingCommands.add(new Runnable() { // from class: com.android.server.print.RemotePrintService.10
                @Override // java.lang.Runnable
                public void run() {
                    RemotePrintService.this.handleStopPrinterStateTracking(printerId);
                }
            });
            return;
        }
        try {
            this.mPrintService.stopPrinterStateTracking(printerId);
        } catch (RemoteException re) {
            Slog.e(LOG_TAG, "Error requesting stop printer tracking.", re);
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.append((CharSequence) prefix).append("service:").println();
        pw.append((CharSequence) prefix).append("  ").append("componentName=").append((CharSequence) this.mComponentName.flattenToString()).println();
        pw.append((CharSequence) prefix).append("  ").append("destroyed=").append((CharSequence) String.valueOf(this.mDestroyed)).println();
        pw.append((CharSequence) prefix).append("  ").append("bound=").append((CharSequence) String.valueOf(isBound())).println();
        pw.append((CharSequence) prefix).append("  ").append("hasDicoverySession=").append((CharSequence) String.valueOf(this.mHasPrinterDiscoverySession)).println();
        pw.append((CharSequence) prefix).append("  ").append("hasActivePrintJobs=").append((CharSequence) String.valueOf(this.mHasActivePrintJobs)).println();
        pw.append((CharSequence) prefix).append("  ").append("isDiscoveringPrinters=").append((CharSequence) String.valueOf(this.mDiscoveryPriorityList != null)).println();
        pw.append((CharSequence) prefix).append("  ").append("trackedPrinters=").append((CharSequence) (this.mTrackedPrinterList != null ? this.mTrackedPrinterList.toString() : "null"));
    }

    private boolean isBound() {
        return this.mPrintService != null;
    }

    private void ensureBound() {
        if (isBound() || this.mBinding) {
            return;
        }
        this.mBinding = true;
        this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 1, new UserHandle(this.mUserId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureUnbound() {
        if (!isBound() && !this.mBinding) {
            return;
        }
        this.mBinding = false;
        this.mPendingCommands.clear();
        this.mHasActivePrintJobs = false;
        this.mHasPrinterDiscoverySession = false;
        this.mDiscoveryPriorityList = null;
        this.mTrackedPrinterList = null;
        if (isBound()) {
            try {
                this.mPrintService.setClient(null);
            } catch (RemoteException e) {
            }
            this.mPrintService.asBinder().unlinkToDeath(this, 0);
            this.mPrintService = null;
            this.mContext.unbindService(this.mServiceConnection);
        }
    }

    private void throwIfDestroyed() {
        if (this.mDestroyed) {
            throw new IllegalStateException("Cannot interact with a destroyed service");
        }
    }

    /* loaded from: RemotePrintService$RemoteServiceConneciton.class */
    private class RemoteServiceConneciton implements ServiceConnection {
        private RemoteServiceConneciton() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (RemotePrintService.this.mDestroyed || !RemotePrintService.this.mBinding) {
                RemotePrintService.this.mContext.unbindService(RemotePrintService.this.mServiceConnection);
                return;
            }
            RemotePrintService.this.mBinding = false;
            RemotePrintService.this.mPrintService = IPrintService.Stub.asInterface(service);
            try {
                service.linkToDeath(RemotePrintService.this, 0);
                try {
                    RemotePrintService.this.mPrintService.setClient(RemotePrintService.this.mPrintServiceClient);
                    if (RemotePrintService.this.mServiceDied && RemotePrintService.this.mHasPrinterDiscoverySession) {
                        RemotePrintService.this.handleCreatePrinterDiscoverySession();
                    }
                    if (RemotePrintService.this.mServiceDied && RemotePrintService.this.mDiscoveryPriorityList != null) {
                        RemotePrintService.this.handleStartPrinterDiscovery(RemotePrintService.this.mDiscoveryPriorityList);
                    }
                    if (RemotePrintService.this.mServiceDied && RemotePrintService.this.mTrackedPrinterList != null) {
                        int trackedPrinterCount = RemotePrintService.this.mTrackedPrinterList.size();
                        for (int i = 0; i < trackedPrinterCount; i++) {
                            RemotePrintService.this.handleStartPrinterStateTracking((PrinterId) RemotePrintService.this.mTrackedPrinterList.get(i));
                        }
                    }
                    while (!RemotePrintService.this.mPendingCommands.isEmpty()) {
                        Runnable pendingCommand = (Runnable) RemotePrintService.this.mPendingCommands.remove(0);
                        pendingCommand.run();
                    }
                    if (!RemotePrintService.this.mHasPrinterDiscoverySession && !RemotePrintService.this.mHasActivePrintJobs) {
                        RemotePrintService.this.ensureUnbound();
                    }
                    RemotePrintService.this.mServiceDied = false;
                } catch (RemoteException re) {
                    Slog.e(RemotePrintService.LOG_TAG, "Error setting client for: " + service, re);
                    RemotePrintService.this.handleBinderDied();
                }
            } catch (RemoteException e) {
                RemotePrintService.this.handleBinderDied();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            RemotePrintService.this.mBinding = true;
        }
    }

    /* loaded from: RemotePrintService$MyHandler.class */
    private final class MyHandler extends Handler {
        public static final int MSG_CREATE_PRINTER_DISCOVERY_SESSION = 1;
        public static final int MSG_DESTROY_PRINTER_DISCOVERY_SESSION = 2;
        public static final int MSG_START_PRINTER_DISCOVERY = 3;
        public static final int MSG_STOP_PRINTER_DISCOVERY = 4;
        public static final int MSG_VALIDATE_PRINTERS = 5;
        public static final int MSG_START_PRINTER_STATE_TRACKING = 6;
        public static final int MSG_STOP_PRINTER_STATE_TRACKING = 7;
        public static final int MSG_ON_ALL_PRINT_JOBS_HANDLED = 8;
        public static final int MSG_ON_REQUEST_CANCEL_PRINT_JOB = 9;
        public static final int MSG_ON_PRINT_JOB_QUEUED = 10;
        public static final int MSG_DESTROY = 11;
        public static final int MSG_BINDER_DIED = 12;

        public MyHandler(Looper looper) {
            super(looper, null, false);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    RemotePrintService.this.handleCreatePrinterDiscoverySession();
                    return;
                case 2:
                    RemotePrintService.this.handleDestroyPrinterDiscoverySession();
                    return;
                case 3:
                    List<PrinterId> priorityList = (ArrayList) message.obj;
                    RemotePrintService.this.handleStartPrinterDiscovery(priorityList);
                    return;
                case 4:
                    RemotePrintService.this.handleStopPrinterDiscovery();
                    return;
                case 5:
                    List<PrinterId> printerIds = (List) message.obj;
                    RemotePrintService.this.handleValidatePrinters(printerIds);
                    return;
                case 6:
                    PrinterId printerId = (PrinterId) message.obj;
                    RemotePrintService.this.handleStartPrinterStateTracking(printerId);
                    return;
                case 7:
                    PrinterId printerId2 = (PrinterId) message.obj;
                    RemotePrintService.this.handleStopPrinterStateTracking(printerId2);
                    return;
                case 8:
                    RemotePrintService.this.handleOnAllPrintJobsHandled();
                    return;
                case 9:
                    PrintJobInfo printJob = (PrintJobInfo) message.obj;
                    RemotePrintService.this.handleRequestCancelPrintJob(printJob);
                    return;
                case 10:
                    PrintJobInfo printJob2 = (PrintJobInfo) message.obj;
                    RemotePrintService.this.handleOnPrintJobQueued(printJob2);
                    return;
                case 11:
                    RemotePrintService.this.handleDestroy();
                    return;
                case 12:
                    RemotePrintService.this.handleBinderDied();
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: RemotePrintService$RemotePrintServiceClient.class */
    private static final class RemotePrintServiceClient extends IPrintServiceClient.Stub {
        private final WeakReference<RemotePrintService> mWeakService;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.getPrintJobInfos():java.util.List<android.print.PrintJobInfo>, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public java.util.List<android.print.PrintJobInfo> getPrintJobInfos() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.getPrintJobInfos():java.util.List<android.print.PrintJobInfo>, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.getPrintJobInfos():java.util.List");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.getPrintJobInfo(android.print.PrintJobId):android.print.PrintJobInfo, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public android.print.PrintJobInfo getPrintJobInfo(android.print.PrintJobId r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.getPrintJobInfo(android.print.PrintJobId):android.print.PrintJobInfo, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.getPrintJobInfo(android.print.PrintJobId):android.print.PrintJobInfo");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.setPrintJobState(android.print.PrintJobId, int, java.lang.String):boolean, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public boolean setPrintJobState(android.print.PrintJobId r1, int r2, java.lang.String r3) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.setPrintJobState(android.print.PrintJobId, int, java.lang.String):boolean, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.setPrintJobState(android.print.PrintJobId, int, java.lang.String):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.setPrintJobTag(android.print.PrintJobId, java.lang.String):boolean, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public boolean setPrintJobTag(android.print.PrintJobId r1, java.lang.String r2) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.setPrintJobTag(android.print.PrintJobId, java.lang.String):boolean, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.setPrintJobTag(android.print.PrintJobId, java.lang.String):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.writePrintJobData(android.os.ParcelFileDescriptor, android.print.PrintJobId):void, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public void writePrintJobData(android.os.ParcelFileDescriptor r1, android.print.PrintJobId r2) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.writePrintJobData(android.os.ParcelFileDescriptor, android.print.PrintJobId):void, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.writePrintJobData(android.os.ParcelFileDescriptor, android.print.PrintJobId):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.onPrintersAdded(android.content.pm.ParceledListSlice):void, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public void onPrintersAdded(android.content.pm.ParceledListSlice r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.onPrintersAdded(android.content.pm.ParceledListSlice):void, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.onPrintersAdded(android.content.pm.ParceledListSlice):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.onPrintersRemoved(android.content.pm.ParceledListSlice):void, file: RemotePrintService$RemotePrintServiceClient.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.printservice.IPrintServiceClient
        public void onPrintersRemoved(android.content.pm.ParceledListSlice r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintService.RemotePrintServiceClient.onPrintersRemoved(android.content.pm.ParceledListSlice):void, file: RemotePrintService$RemotePrintServiceClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintService.RemotePrintServiceClient.onPrintersRemoved(android.content.pm.ParceledListSlice):void");
        }

        public RemotePrintServiceClient(RemotePrintService service) {
            this.mWeakService = new WeakReference<>(service);
        }

        private void throwIfPrinterIdsForPrinterInfoTampered(ComponentName serviceName, List<PrinterInfo> printerInfos) {
            int printerInfoCount = printerInfos.size();
            for (int i = 0; i < printerInfoCount; i++) {
                PrinterId printerId = printerInfos.get(i).getId();
                throwIfPrinterIdTampered(serviceName, printerId);
            }
        }

        private void throwIfPrinterIdsTampered(ComponentName serviceName, List<PrinterId> printerIds) {
            int printerIdCount = printerIds.size();
            for (int i = 0; i < printerIdCount; i++) {
                PrinterId printerId = printerIds.get(i);
                throwIfPrinterIdTampered(serviceName, printerId);
            }
        }

        private void throwIfPrinterIdTampered(ComponentName serviceName, PrinterId printerId) {
            if (printerId == null || printerId.getServiceName() == null || !printerId.getServiceName().equals(serviceName)) {
                throw new IllegalArgumentException("Invalid printer id: " + printerId);
            }
        }
    }
}