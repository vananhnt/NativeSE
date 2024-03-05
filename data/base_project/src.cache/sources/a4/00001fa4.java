package com.android.server.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.print.IPrintSpooler;
import android.print.IPrintSpoolerCallbacks;
import android.print.IPrintSpoolerClient;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeoutException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: RemotePrintSpooler.class */
public final class RemotePrintSpooler {
    private static final String LOG_TAG = "RemotePrintSpooler";
    private static final boolean DEBUG = false;
    private static final long BIND_SPOOLER_SERVICE_TIMEOUT = 10000;
    private final Context mContext;
    private final UserHandle mUserHandle;
    private final PrintSpoolerCallbacks mCallbacks;
    private IPrintSpooler mRemoteInstance;
    private boolean mDestroyed;
    private boolean mCanUnbind;
    private final Object mLock = new Object();
    private final GetPrintJobInfosCaller mGetPrintJobInfosCaller = new GetPrintJobInfosCaller();
    private final GetPrintJobInfoCaller mGetPrintJobInfoCaller = new GetPrintJobInfoCaller();
    private final SetPrintJobStateCaller mSetPrintJobStatusCaller = new SetPrintJobStateCaller();
    private final SetPrintJobTagCaller mSetPrintJobTagCaller = new SetPrintJobTagCaller();
    private final ServiceConnection mServiceConnection = new MyServiceConnection();
    private final PrintSpoolerClient mClient = new PrintSpoolerClient(this);
    private final Intent mIntent = new Intent();

    /* loaded from: RemotePrintSpooler$PrintSpoolerCallbacks.class */
    public interface PrintSpoolerCallbacks {
        void onPrintJobQueued(PrintJobInfo printJobInfo);

        void onAllPrintJobsForServiceHandled(ComponentName componentName);

        void onPrintJobStateChanged(PrintJobInfo printJobInfo);
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.getPrintJobInfos(android.content.ComponentName, int, int):java.util.List<android.print.PrintJobInfo>, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final java.util.List<android.print.PrintJobInfo> getPrintJobInfos(android.content.ComponentName r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.getPrintJobInfos(android.content.ComponentName, int, int):java.util.List<android.print.PrintJobInfo>, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.getPrintJobInfos(android.content.ComponentName, int, int):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.createPrintJob(android.print.PrintJobInfo):void, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final void createPrintJob(android.print.PrintJobInfo r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.createPrintJob(android.print.PrintJobInfo):void, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.createPrintJob(android.print.PrintJobInfo):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.writePrintJobData(android.os.ParcelFileDescriptor, android.print.PrintJobId):void, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final void writePrintJobData(android.os.ParcelFileDescriptor r1, android.print.PrintJobId r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.writePrintJobData(android.os.ParcelFileDescriptor, android.print.PrintJobId):void, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.writePrintJobData(android.os.ParcelFileDescriptor, android.print.PrintJobId):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.getPrintJobInfo(android.print.PrintJobId, int):android.print.PrintJobInfo, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final android.print.PrintJobInfo getPrintJobInfo(android.print.PrintJobId r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.getPrintJobInfo(android.print.PrintJobId, int):android.print.PrintJobInfo, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.getPrintJobInfo(android.print.PrintJobId, int):android.print.PrintJobInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.setPrintJobState(android.print.PrintJobId, int, java.lang.String):boolean, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final boolean setPrintJobState(android.print.PrintJobId r1, int r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.setPrintJobState(android.print.PrintJobId, int, java.lang.String):boolean, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.setPrintJobState(android.print.PrintJobId, int, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.setPrintJobTag(android.print.PrintJobId, java.lang.String):boolean, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final boolean setPrintJobTag(android.print.PrintJobId r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.setPrintJobTag(android.print.PrintJobId, java.lang.String):boolean, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.setPrintJobTag(android.print.PrintJobId, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.setPrintJobCancelling(android.print.PrintJobId, boolean):void, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final void setPrintJobCancelling(android.print.PrintJobId r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.setPrintJobCancelling(android.print.PrintJobId, boolean):void, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.setPrintJobCancelling(android.print.PrintJobId, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.removeObsoletePrintJobs():void, file: RemotePrintSpooler.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public final void removeObsoletePrintJobs() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.removeObsoletePrintJobs():void, file: RemotePrintSpooler.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.removeObsoletePrintJobs():void");
    }

    static /* synthetic */ PrintSpoolerCallbacks access$600(RemotePrintSpooler x0) {
        return x0.mCallbacks;
    }

    static /* synthetic */ void access$700(RemotePrintSpooler x0) {
        x0.onAllPrintJobsHandled();
    }

    static /* synthetic */ void access$800(RemotePrintSpooler x0, PrintJobInfo x1) {
        x0.onPrintJobStateChanged(x1);
    }

    public RemotePrintSpooler(Context context, int userId, PrintSpoolerCallbacks callbacks) {
        this.mContext = context;
        this.mUserHandle = new UserHandle(userId);
        this.mCallbacks = callbacks;
        this.mIntent.setComponent(new ComponentName("com.android.printspooler", "com.android.printspooler.PrintSpoolerService"));
    }

    public final void destroy() {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            unbindLocked();
            this.mDestroyed = true;
            this.mCanUnbind = false;
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String prefix) {
        synchronized (this.mLock) {
            pw.append((CharSequence) prefix).append("destroyed=").append((CharSequence) String.valueOf(this.mDestroyed)).println();
            pw.append((CharSequence) prefix).append("bound=").append((CharSequence) (this.mRemoteInstance != null ? "true" : "false")).println();
            pw.flush();
            try {
                getRemoteInstanceLazy().asBinder().dump(fd, new String[]{prefix});
            } catch (RemoteException e) {
            } catch (TimeoutException e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAllPrintJobsHandled() {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            unbindLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPrintJobStateChanged(PrintJobInfo printJob) {
        this.mCallbacks.onPrintJobStateChanged(printJob);
    }

    private IPrintSpooler getRemoteInstanceLazy() throws TimeoutException {
        synchronized (this.mLock) {
            if (this.mRemoteInstance != null) {
                return this.mRemoteInstance;
            }
            bindLocked();
            return this.mRemoteInstance;
        }
    }

    private void bindLocked() throws TimeoutException {
        if (this.mRemoteInstance != null) {
            return;
        }
        this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 1, this.mUserHandle);
        long startMillis = SystemClock.uptimeMillis();
        while (this.mRemoteInstance == null) {
            long elapsedMillis = SystemClock.uptimeMillis() - startMillis;
            long remainingMillis = BIND_SPOOLER_SERVICE_TIMEOUT - elapsedMillis;
            if (remainingMillis <= 0) {
                throw new TimeoutException("Cannot get spooler!");
            }
            try {
                this.mLock.wait(remainingMillis);
            } catch (InterruptedException e) {
            }
        }
        this.mCanUnbind = true;
        this.mLock.notifyAll();
    }

    private void unbindLocked() {
        if (this.mRemoteInstance == null) {
            return;
        }
        while (!this.mCanUnbind) {
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
            }
        }
        clearClientLocked();
        this.mRemoteInstance = null;
        this.mContext.unbindService(this.mServiceConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClientLocked() {
        try {
            this.mRemoteInstance.setClient(this.mClient);
        } catch (RemoteException re) {
            Slog.d(LOG_TAG, "Error setting print spooler client", re);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearClientLocked() {
        try {
            this.mRemoteInstance.setClient(null);
        } catch (RemoteException re) {
            Slog.d(LOG_TAG, "Error clearing print spooler client", re);
        }
    }

    private void throwIfDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("Cannot interact with a destroyed instance.");
        }
    }

    private void throwIfCalledOnMainThread() {
        if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
            throw new RuntimeException("Cannot invoke on the main thread");
        }
    }

    /* loaded from: RemotePrintSpooler$MyServiceConnection.class */
    private final class MyServiceConnection implements ServiceConnection {
        private MyServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (RemotePrintSpooler.this.mLock) {
                RemotePrintSpooler.this.mRemoteInstance = IPrintSpooler.Stub.asInterface(service);
                RemotePrintSpooler.this.setClientLocked();
                RemotePrintSpooler.this.mLock.notifyAll();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            synchronized (RemotePrintSpooler.this.mLock) {
                RemotePrintSpooler.this.clearClientLocked();
                RemotePrintSpooler.this.mRemoteInstance = null;
            }
        }
    }

    /* loaded from: RemotePrintSpooler$GetPrintJobInfosCaller.class */
    private static final class GetPrintJobInfosCaller extends TimedRemoteCaller<List<PrintJobInfo>> {
        private final IPrintSpoolerCallbacks mCallback;

        public GetPrintJobInfosCaller() {
            super(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.GetPrintJobInfosCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks, android.print.IPrintSpoolerCallbacks
                public void onGetPrintJobInfosResult(List<PrintJobInfo> printJobs, int sequence) {
                    GetPrintJobInfosCaller.this.onRemoteMethodResult(printJobs, sequence);
                }
            };
        }

        public List<PrintJobInfo> getPrintJobInfos(IPrintSpooler target, ComponentName componentName, int state, int appId) throws RemoteException, TimeoutException {
            int sequence = onBeforeRemoteCall();
            target.getPrintJobInfos(this.mCallback, componentName, state, appId, sequence);
            return getResultTimed(sequence);
        }
    }

    /* loaded from: RemotePrintSpooler$GetPrintJobInfoCaller.class */
    private static final class GetPrintJobInfoCaller extends TimedRemoteCaller<PrintJobInfo> {
        private final IPrintSpoolerCallbacks mCallback;

        public GetPrintJobInfoCaller() {
            super(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.GetPrintJobInfoCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks, android.print.IPrintSpoolerCallbacks
                public void onGetPrintJobInfoResult(PrintJobInfo printJob, int sequence) {
                    GetPrintJobInfoCaller.this.onRemoteMethodResult(printJob, sequence);
                }
            };
        }

        public PrintJobInfo getPrintJobInfo(IPrintSpooler target, PrintJobId printJobId, int appId) throws RemoteException, TimeoutException {
            int sequence = onBeforeRemoteCall();
            target.getPrintJobInfo(printJobId, this.mCallback, appId, sequence);
            return getResultTimed(sequence);
        }
    }

    /* loaded from: RemotePrintSpooler$SetPrintJobStateCaller.class */
    private static final class SetPrintJobStateCaller extends TimedRemoteCaller<Boolean> {
        private final IPrintSpoolerCallbacks mCallback;

        public SetPrintJobStateCaller() {
            super(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.SetPrintJobStateCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks, android.print.IPrintSpoolerCallbacks
                public void onSetPrintJobStateResult(boolean success, int sequence) {
                    SetPrintJobStateCaller.this.onRemoteMethodResult(Boolean.valueOf(success), sequence);
                }
            };
        }

        public boolean setPrintJobState(IPrintSpooler target, PrintJobId printJobId, int status, String error) throws RemoteException, TimeoutException {
            int sequence = onBeforeRemoteCall();
            target.setPrintJobState(printJobId, status, error, this.mCallback, sequence);
            return getResultTimed(sequence).booleanValue();
        }
    }

    /* loaded from: RemotePrintSpooler$SetPrintJobTagCaller.class */
    private static final class SetPrintJobTagCaller extends TimedRemoteCaller<Boolean> {
        private final IPrintSpoolerCallbacks mCallback;

        public SetPrintJobTagCaller() {
            super(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.SetPrintJobTagCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks, android.print.IPrintSpoolerCallbacks
                public void onSetPrintJobTagResult(boolean success, int sequence) {
                    SetPrintJobTagCaller.this.onRemoteMethodResult(Boolean.valueOf(success), sequence);
                }
            };
        }

        public boolean setPrintJobTag(IPrintSpooler target, PrintJobId printJobId, String tag) throws RemoteException, TimeoutException {
            int sequence = onBeforeRemoteCall();
            target.setPrintJobTag(printJobId, tag, this.mCallback, sequence);
            return getResultTimed(sequence).booleanValue();
        }
    }

    /* loaded from: RemotePrintSpooler$BasePrintSpoolerServiceCallbacks.class */
    private static abstract class BasePrintSpoolerServiceCallbacks extends IPrintSpoolerCallbacks.Stub {
        private BasePrintSpoolerServiceCallbacks() {
        }

        @Override // android.print.IPrintSpoolerCallbacks
        public void onGetPrintJobInfosResult(List<PrintJobInfo> printJobIds, int sequence) {
        }

        @Override // android.print.IPrintSpoolerCallbacks
        public void onGetPrintJobInfoResult(PrintJobInfo printJob, int sequence) {
        }

        @Override // android.print.IPrintSpoolerCallbacks
        public void onCancelPrintJobResult(boolean canceled, int sequence) {
        }

        @Override // android.print.IPrintSpoolerCallbacks
        public void onSetPrintJobStateResult(boolean success, int sequece) {
        }

        @Override // android.print.IPrintSpoolerCallbacks
        public void onSetPrintJobTagResult(boolean success, int sequence) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemotePrintSpooler$PrintSpoolerClient.class */
    public static final class PrintSpoolerClient extends IPrintSpoolerClient.Stub {
        private final WeakReference<RemotePrintSpooler> mWeakSpooler;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onPrintJobQueued(android.print.PrintJobInfo):void, file: RemotePrintSpooler$PrintSpoolerClient.class
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
        @Override // android.print.IPrintSpoolerClient
        public void onPrintJobQueued(android.print.PrintJobInfo r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onPrintJobQueued(android.print.PrintJobInfo):void, file: RemotePrintSpooler$PrintSpoolerClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onPrintJobQueued(android.print.PrintJobInfo):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onAllPrintJobsForServiceHandled(android.content.ComponentName):void, file: RemotePrintSpooler$PrintSpoolerClient.class
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
        @Override // android.print.IPrintSpoolerClient
        public void onAllPrintJobsForServiceHandled(android.content.ComponentName r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onAllPrintJobsForServiceHandled(android.content.ComponentName):void, file: RemotePrintSpooler$PrintSpoolerClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onAllPrintJobsForServiceHandled(android.content.ComponentName):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onAllPrintJobsHandled():void, file: RemotePrintSpooler$PrintSpoolerClient.class
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
        @Override // android.print.IPrintSpoolerClient
        public void onAllPrintJobsHandled() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onAllPrintJobsHandled():void, file: RemotePrintSpooler$PrintSpoolerClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onAllPrintJobsHandled():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onPrintJobStateChanged(android.print.PrintJobInfo):void, file: RemotePrintSpooler$PrintSpoolerClient.class
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
        @Override // android.print.IPrintSpoolerClient
        public void onPrintJobStateChanged(android.print.PrintJobInfo r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onPrintJobStateChanged(android.print.PrintJobInfo):void, file: RemotePrintSpooler$PrintSpoolerClient.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.RemotePrintSpooler.PrintSpoolerClient.onPrintJobStateChanged(android.print.PrintJobInfo):void");
        }

        public PrintSpoolerClient(RemotePrintSpooler spooler) {
            this.mWeakSpooler = new WeakReference<>(spooler);
        }
    }
}