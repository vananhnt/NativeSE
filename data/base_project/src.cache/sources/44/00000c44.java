package android.print;

import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.IPrintDocumentAdapter;
import android.print.IPrintJobStateChangeListener;
import android.print.PrintDocumentAdapter;
import android.printservice.PrintServiceInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.os.SomeArgs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import libcore.io.IoUtils;

/* loaded from: PrintManager.class */
public final class PrintManager {
    private static final String LOG_TAG = "PrintManager";
    private static final boolean DEBUG = false;
    private static final int MSG_NOTIFY_PRINT_JOB_STATE_CHANGED = 1;
    public static final String ACTION_PRINT_DIALOG = "android.print.PRINT_DIALOG";
    public static final String EXTRA_PRINT_DIALOG_INTENT = "android.print.intent.extra.EXTRA_PRINT_DIALOG_INTENT";
    public static final String EXTRA_PRINT_JOB = "android.print.intent.extra.EXTRA_PRINT_JOB";
    public static final String EXTRA_PRINT_DOCUMENT_ADAPTER = "android.print.intent.extra.EXTRA_PRINT_DOCUMENT_ADAPTER";
    public static final int APP_ID_ANY = -2;
    private final Context mContext;
    private final IPrintManager mService;
    private final int mUserId;
    private final int mAppId;
    private final Handler mHandler;
    private Map<PrintJobStateChangeListener, PrintJobStateChangeListenerWrapper> mPrintJobStateChangeListeners;

    /* loaded from: PrintManager$PrintJobStateChangeListener.class */
    public interface PrintJobStateChangeListener {
        void onPrintJobStateChanged(PrintJobId printJobId);
    }

    public PrintManager(Context context, IPrintManager service, int userId, int appId) {
        this.mContext = context;
        this.mService = service;
        this.mUserId = userId;
        this.mAppId = appId;
        this.mHandler = new Handler(context.getMainLooper(), null, false) { // from class: android.print.PrintManager.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        SomeArgs args = (SomeArgs) message.obj;
                        PrintJobStateChangeListenerWrapper wrapper = (PrintJobStateChangeListenerWrapper) args.arg1;
                        PrintJobStateChangeListener listener = wrapper.getListener();
                        if (listener != null) {
                            PrintJobId printJobId = (PrintJobId) args.arg2;
                            listener.onPrintJobStateChanged(printJobId);
                        }
                        args.recycle();
                        return;
                    default:
                        return;
                }
            }
        };
    }

    public PrintManager getGlobalPrintManagerForUser(int userId) {
        return new PrintManager(this.mContext, this.mService, userId, -2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrintJobInfo getPrintJobInfo(PrintJobId printJobId) {
        try {
            return this.mService.getPrintJobInfo(printJobId, this.mAppId, this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error getting a print job info:" + printJobId, re);
            return null;
        }
    }

    public void addPrintJobStateChangeListener(PrintJobStateChangeListener listener) {
        if (this.mPrintJobStateChangeListeners == null) {
            this.mPrintJobStateChangeListeners = new ArrayMap();
        }
        PrintJobStateChangeListenerWrapper wrappedListener = new PrintJobStateChangeListenerWrapper(listener, this.mHandler);
        try {
            this.mService.addPrintJobStateChangeListener(wrappedListener, this.mAppId, this.mUserId);
            this.mPrintJobStateChangeListeners.put(listener, wrappedListener);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error adding print job state change listener", re);
        }
    }

    public void removePrintJobStateChangeListener(PrintJobStateChangeListener listener) {
        PrintJobStateChangeListenerWrapper wrappedListener;
        if (this.mPrintJobStateChangeListeners == null || (wrappedListener = this.mPrintJobStateChangeListeners.remove(listener)) == null) {
            return;
        }
        if (this.mPrintJobStateChangeListeners.isEmpty()) {
            this.mPrintJobStateChangeListeners = null;
        }
        wrappedListener.destroy();
        try {
            this.mService.removePrintJobStateChangeListener(wrappedListener, this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error removing print job state change listener", re);
        }
    }

    public PrintJob getPrintJob(PrintJobId printJobId) {
        try {
            PrintJobInfo printJob = this.mService.getPrintJobInfo(printJobId, this.mAppId, this.mUserId);
            if (printJob != null) {
                return new PrintJob(printJob, this);
            }
            return null;
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error getting print job", re);
            return null;
        }
    }

    public List<PrintJob> getPrintJobs() {
        try {
            List<PrintJobInfo> printJobInfos = this.mService.getPrintJobInfos(this.mAppId, this.mUserId);
            if (printJobInfos == null) {
                return Collections.emptyList();
            }
            int printJobCount = printJobInfos.size();
            List<PrintJob> printJobs = new ArrayList<>(printJobCount);
            for (int i = 0; i < printJobCount; i++) {
                printJobs.add(new PrintJob(printJobInfos.get(i), this));
            }
            return printJobs;
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error getting print jobs", re);
            return Collections.emptyList();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelPrintJob(PrintJobId printJobId) {
        try {
            this.mService.cancelPrintJob(printJobId, this.mAppId, this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error cancleing a print job: " + printJobId, re);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restartPrintJob(PrintJobId printJobId) {
        try {
            this.mService.restartPrintJob(printJobId, this.mAppId, this.mUserId);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error restarting a print job: " + printJobId, re);
        }
    }

    public PrintJob print(String printJobName, PrintDocumentAdapter documentAdapter, PrintAttributes attributes) {
        if (TextUtils.isEmpty(printJobName)) {
            throw new IllegalArgumentException("priintJobName cannot be empty");
        }
        PrintDocumentAdapterDelegate delegate = new PrintDocumentAdapterDelegate(documentAdapter, this.mContext.getMainLooper());
        try {
            Bundle result = this.mService.print(printJobName, delegate, attributes, this.mContext.getPackageName(), this.mAppId, this.mUserId);
            if (result != null) {
                PrintJobInfo printJob = (PrintJobInfo) result.getParcelable(EXTRA_PRINT_JOB);
                IntentSender intent = (IntentSender) result.getParcelable(EXTRA_PRINT_DIALOG_INTENT);
                if (printJob == null || intent == null) {
                    return null;
                }
                try {
                    this.mContext.startIntentSender(intent, null, 0, 0, 0);
                    return new PrintJob(printJob, this);
                } catch (IntentSender.SendIntentException sie) {
                    Log.e(LOG_TAG, "Couldn't start print job config activity.", sie);
                }
            }
            return null;
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error creating a print job", re);
            return null;
        }
    }

    public List<PrintServiceInfo> getEnabledPrintServices() {
        try {
            List<PrintServiceInfo> enabledServices = this.mService.getEnabledPrintServices(this.mUserId);
            if (enabledServices != null) {
                return enabledServices;
            }
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error getting the enabled print services", re);
        }
        return Collections.emptyList();
    }

    public List<PrintServiceInfo> getInstalledPrintServices() {
        try {
            List<PrintServiceInfo> installedServices = this.mService.getInstalledPrintServices(this.mUserId);
            if (installedServices != null) {
                return installedServices;
            }
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error getting the installed print services", re);
        }
        return Collections.emptyList();
    }

    public PrinterDiscoverySession createPrinterDiscoverySession() {
        return new PrinterDiscoverySession(this.mService, this.mContext, this.mUserId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PrintManager$PrintDocumentAdapterDelegate.class */
    public static final class PrintDocumentAdapterDelegate extends IPrintDocumentAdapter.Stub {
        private final Object mLock = new Object();
        private CancellationSignal mLayoutOrWriteCancellation;
        private PrintDocumentAdapter mDocumentAdapter;
        private Handler mHandler;
        private LayoutSpec mLastLayoutSpec;
        private WriteSpec mLastWriteSpec;
        private boolean mStartReqeusted;
        private boolean mStarted;
        private boolean mFinishRequested;
        private boolean mFinished;

        public PrintDocumentAdapterDelegate(PrintDocumentAdapter documentAdapter, Looper looper) {
            this.mDocumentAdapter = documentAdapter;
            this.mHandler = new MyHandler(looper);
        }

        @Override // android.print.IPrintDocumentAdapter
        public void start() {
            synchronized (this.mLock) {
                if (this.mStartReqeusted || this.mFinishRequested) {
                    return;
                }
                this.mStartReqeusted = true;
                doPendingWorkLocked();
            }
        }

        /* JADX WARN: Type inference failed for: r0v29, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
        @Override // android.print.IPrintDocumentAdapter
        public void layout(PrintAttributes oldAttributes, PrintAttributes newAttributes, ILayoutResultCallback callback, Bundle metadata, int sequence) {
            synchronized (this.mLock) {
                if (!this.mStartReqeusted || this.mFinishRequested) {
                    return;
                }
                if (this.mLastWriteSpec != null) {
                    IoUtils.closeQuietly((AutoCloseable) this.mLastWriteSpec.fd);
                    this.mLastWriteSpec = null;
                }
                this.mLastLayoutSpec = new LayoutSpec();
                this.mLastLayoutSpec.callback = callback;
                this.mLastLayoutSpec.oldAttributes = oldAttributes;
                this.mLastLayoutSpec.newAttributes = newAttributes;
                this.mLastLayoutSpec.metadata = metadata;
                this.mLastLayoutSpec.sequence = sequence;
                if (cancelPreviousCancellableOperationLocked()) {
                    return;
                }
                doPendingWorkLocked();
            }
        }

        /* JADX WARN: Type inference failed for: r0v27, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
        @Override // android.print.IPrintDocumentAdapter
        public void write(PageRange[] pages, ParcelFileDescriptor fd, IWriteResultCallback callback, int sequence) {
            synchronized (this.mLock) {
                if (!this.mStartReqeusted || this.mFinishRequested) {
                    return;
                }
                if (this.mLastWriteSpec != null) {
                    IoUtils.closeQuietly((AutoCloseable) this.mLastWriteSpec.fd);
                    this.mLastWriteSpec = null;
                }
                this.mLastWriteSpec = new WriteSpec();
                this.mLastWriteSpec.callback = callback;
                this.mLastWriteSpec.pages = pages;
                this.mLastWriteSpec.fd = fd;
                this.mLastWriteSpec.sequence = sequence;
                if (cancelPreviousCancellableOperationLocked()) {
                    return;
                }
                doPendingWorkLocked();
            }
        }

        @Override // android.print.IPrintDocumentAdapter
        public void finish() {
            synchronized (this.mLock) {
                if (!this.mStartReqeusted || this.mFinishRequested) {
                    return;
                }
                this.mFinishRequested = true;
                if (this.mLastLayoutSpec == null && this.mLastWriteSpec == null) {
                    doPendingWorkLocked();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isFinished() {
            return this.mDocumentAdapter == null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void doFinish() {
            this.mDocumentAdapter = null;
            this.mHandler = null;
            synchronized (this.mLock) {
                this.mLayoutOrWriteCancellation = null;
            }
        }

        private boolean cancelPreviousCancellableOperationLocked() {
            if (this.mLayoutOrWriteCancellation != null) {
                this.mLayoutOrWriteCancellation.cancel();
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void doPendingWorkLocked() {
            if (this.mStartReqeusted && !this.mStarted) {
                this.mStarted = true;
                this.mHandler.sendEmptyMessage(1);
            } else if (this.mLastLayoutSpec != null) {
                this.mHandler.sendEmptyMessage(2);
            } else if (this.mLastWriteSpec != null) {
                this.mHandler.sendEmptyMessage(3);
            } else if (this.mFinishRequested && !this.mFinished) {
                this.mFinished = true;
                this.mHandler.sendEmptyMessage(4);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: PrintManager$PrintDocumentAdapterDelegate$LayoutSpec.class */
        public class LayoutSpec {
            ILayoutResultCallback callback;
            PrintAttributes oldAttributes;
            PrintAttributes newAttributes;
            Bundle metadata;
            int sequence;

            private LayoutSpec() {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: PrintManager$PrintDocumentAdapterDelegate$WriteSpec.class */
        public class WriteSpec {
            IWriteResultCallback callback;
            PageRange[] pages;
            ParcelFileDescriptor fd;
            int sequence;

            private WriteSpec() {
            }
        }

        /* loaded from: PrintManager$PrintDocumentAdapterDelegate$MyHandler.class */
        private final class MyHandler extends Handler {
            public static final int MSG_START = 1;
            public static final int MSG_LAYOUT = 2;
            public static final int MSG_WRITE = 3;
            public static final int MSG_FINISH = 4;

            public MyHandler(Looper looper) {
                super(looper, null, true);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                WriteSpec writeSpec;
                CancellationSignal cancellation;
                LayoutSpec layoutSpec;
                CancellationSignal cancellation2;
                if (!PrintDocumentAdapterDelegate.this.isFinished()) {
                    switch (message.what) {
                        case 1:
                            PrintDocumentAdapterDelegate.this.mDocumentAdapter.onStart();
                            return;
                        case 2:
                            synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                                layoutSpec = PrintDocumentAdapterDelegate.this.mLastLayoutSpec;
                                PrintDocumentAdapterDelegate.this.mLastLayoutSpec = null;
                                cancellation2 = new CancellationSignal();
                                PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = cancellation2;
                            }
                            if (layoutSpec != null) {
                                PrintDocumentAdapterDelegate.this.mDocumentAdapter.onLayout(layoutSpec.oldAttributes, layoutSpec.newAttributes, cancellation2, new MyLayoutResultCallback(layoutSpec.callback, layoutSpec.sequence), layoutSpec.metadata);
                                return;
                            }
                            return;
                        case 3:
                            synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                                writeSpec = PrintDocumentAdapterDelegate.this.mLastWriteSpec;
                                PrintDocumentAdapterDelegate.this.mLastWriteSpec = null;
                                cancellation = new CancellationSignal();
                                PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = cancellation;
                            }
                            if (writeSpec != null) {
                                PrintDocumentAdapterDelegate.this.mDocumentAdapter.onWrite(writeSpec.pages, writeSpec.fd, cancellation, new MyWriteResultCallback(writeSpec.callback, writeSpec.fd, writeSpec.sequence));
                                return;
                            }
                            return;
                        case 4:
                            PrintDocumentAdapterDelegate.this.mDocumentAdapter.onFinish();
                            PrintDocumentAdapterDelegate.this.doFinish();
                            return;
                        default:
                            throw new IllegalArgumentException("Unknown message: " + message.what);
                    }
                }
            }
        }

        /* loaded from: PrintManager$PrintDocumentAdapterDelegate$MyLayoutResultCallback.class */
        private final class MyLayoutResultCallback extends PrintDocumentAdapter.LayoutResultCallback {
            private ILayoutResultCallback mCallback;
            private final int mSequence;

            public MyLayoutResultCallback(ILayoutResultCallback callback, int sequence) {
                this.mCallback = callback;
                this.mSequence = sequence;
            }

            @Override // android.print.PrintDocumentAdapter.LayoutResultCallback
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                ILayoutResultCallback callback;
                if (info != null) {
                    synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                        callback = this.mCallback;
                        clearLocked();
                    }
                    if (callback != null) {
                        try {
                            callback.onLayoutFinished(info, changed, this.mSequence);
                            return;
                        } catch (RemoteException re) {
                            Log.e(PrintManager.LOG_TAG, "Error calling onLayoutFinished", re);
                            return;
                        }
                    }
                    return;
                }
                throw new NullPointerException("document info cannot be null");
            }

            @Override // android.print.PrintDocumentAdapter.LayoutResultCallback
            public void onLayoutFailed(CharSequence error) {
                ILayoutResultCallback callback;
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    callback = this.mCallback;
                    clearLocked();
                }
                if (callback != null) {
                    try {
                        callback.onLayoutFailed(error, this.mSequence);
                    } catch (RemoteException re) {
                        Log.e(PrintManager.LOG_TAG, "Error calling onLayoutFailed", re);
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.LayoutResultCallback
            public void onLayoutCancelled() {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    clearLocked();
                }
            }

            private void clearLocked() {
                PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = null;
                this.mCallback = null;
                PrintDocumentAdapterDelegate.this.doPendingWorkLocked();
            }
        }

        /* loaded from: PrintManager$PrintDocumentAdapterDelegate$MyWriteResultCallback.class */
        private final class MyWriteResultCallback extends PrintDocumentAdapter.WriteResultCallback {
            private ParcelFileDescriptor mFd;
            private int mSequence;
            private IWriteResultCallback mCallback;

            public MyWriteResultCallback(IWriteResultCallback callback, ParcelFileDescriptor fd, int sequence) {
                this.mFd = fd;
                this.mSequence = sequence;
                this.mCallback = callback;
            }

            @Override // android.print.PrintDocumentAdapter.WriteResultCallback
            public void onWriteFinished(PageRange[] pages) {
                IWriteResultCallback callback;
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    callback = this.mCallback;
                    clearLocked();
                }
                if (pages == null) {
                    throw new IllegalArgumentException("pages cannot be null");
                }
                if (pages.length == 0) {
                    throw new IllegalArgumentException("pages cannot be empty");
                }
                if (callback != null) {
                    try {
                        callback.onWriteFinished(pages, this.mSequence);
                    } catch (RemoteException re) {
                        Log.e(PrintManager.LOG_TAG, "Error calling onWriteFinished", re);
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.WriteResultCallback
            public void onWriteFailed(CharSequence error) {
                IWriteResultCallback callback;
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    callback = this.mCallback;
                    clearLocked();
                }
                if (callback != null) {
                    try {
                        callback.onWriteFailed(error, this.mSequence);
                    } catch (RemoteException re) {
                        Log.e(PrintManager.LOG_TAG, "Error calling onWriteFailed", re);
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.WriteResultCallback
            public void onWriteCancelled() {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    clearLocked();
                }
            }

            /* JADX WARN: Type inference failed for: r0v4, types: [android.os.ParcelFileDescriptor, java.lang.AutoCloseable] */
            private void clearLocked() {
                PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = null;
                IoUtils.closeQuietly((AutoCloseable) this.mFd);
                this.mCallback = null;
                this.mFd = null;
                PrintDocumentAdapterDelegate.this.doPendingWorkLocked();
            }
        }
    }

    /* loaded from: PrintManager$PrintJobStateChangeListenerWrapper.class */
    private static final class PrintJobStateChangeListenerWrapper extends IPrintJobStateChangeListener.Stub {
        private final WeakReference<PrintJobStateChangeListener> mWeakListener;
        private final WeakReference<Handler> mWeakHandler;

        public PrintJobStateChangeListenerWrapper(PrintJobStateChangeListener listener, Handler handler) {
            this.mWeakListener = new WeakReference<>(listener);
            this.mWeakHandler = new WeakReference<>(handler);
        }

        @Override // android.print.IPrintJobStateChangeListener
        public void onPrintJobStateChanged(PrintJobId printJobId) {
            Handler handler = this.mWeakHandler.get();
            PrintJobStateChangeListener listener = this.mWeakListener.get();
            if (handler != null && listener != null) {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = this;
                args.arg2 = printJobId;
                handler.obtainMessage(1, args).sendToTarget();
            }
        }

        public void destroy() {
            this.mWeakListener.clear();
        }

        public PrintJobStateChangeListener getListener() {
            return this.mWeakListener.get();
        }
    }
}