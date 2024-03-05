package android.printservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.printservice.IPrintService;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: PrintService.class */
public abstract class PrintService extends Service {
    private static final String LOG_TAG = "PrintService";
    private static final boolean DEBUG = false;
    public static final String SERVICE_INTERFACE = "android.printservice.PrintService";
    public static final String SERVICE_META_DATA = "android.printservice";
    public static final String EXTRA_PRINT_JOB_INFO = "android.intent.extra.print.PRINT_JOB_INFO";
    private Handler mHandler;
    private IPrintServiceClient mClient;
    private int mLastSessionId = -1;
    private PrinterDiscoverySession mDiscoverySession;

    protected abstract PrinterDiscoverySession onCreatePrinterDiscoverySession();

    protected abstract void onRequestCancelPrintJob(PrintJob printJob);

    protected abstract void onPrintJobQueued(PrintJob printJob);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.content.ContextWrapper
    public final void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.mHandler = new ServiceHandler(base.getMainLooper());
    }

    protected void onConnected() {
    }

    protected void onDisconnected() {
    }

    public final List<PrintJob> getActivePrintJobs() {
        throwIfNotCalledOnMainThread();
        if (this.mClient == null) {
            return Collections.emptyList();
        }
        try {
            List<PrintJob> printJobs = null;
            List<PrintJobInfo> printJobInfos = this.mClient.getPrintJobInfos();
            if (printJobInfos != null) {
                int printJobInfoCount = printJobInfos.size();
                printJobs = new ArrayList<>(printJobInfoCount);
                for (int i = 0; i < printJobInfoCount; i++) {
                    printJobs.add(new PrintJob(printJobInfos.get(i), this.mClient));
                }
            }
            if (printJobs != null) {
                return printJobs;
            }
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error calling getPrintJobs()", re);
        }
        return Collections.emptyList();
    }

    public final PrinterId generatePrinterId(String localId) {
        throwIfNotCalledOnMainThread();
        return new PrinterId(new ComponentName(getPackageName(), getClass().getName()), localId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwIfNotCalledOnMainThread() {
        if (!Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalAccessError("must be called from the main thread");
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IPrintService.Stub() { // from class: android.printservice.PrintService.1
            @Override // android.printservice.IPrintService
            public void createPrinterDiscoverySession() {
                PrintService.this.mHandler.sendEmptyMessage(1);
            }

            @Override // android.printservice.IPrintService
            public void destroyPrinterDiscoverySession() {
                PrintService.this.mHandler.sendEmptyMessage(2);
            }

            @Override // android.printservice.IPrintService
            public void startPrinterDiscovery(List<PrinterId> priorityList) {
                PrintService.this.mHandler.obtainMessage(3, priorityList).sendToTarget();
            }

            @Override // android.printservice.IPrintService
            public void stopPrinterDiscovery() {
                PrintService.this.mHandler.sendEmptyMessage(4);
            }

            @Override // android.printservice.IPrintService
            public void validatePrinters(List<PrinterId> printerIds) {
                PrintService.this.mHandler.obtainMessage(5, printerIds).sendToTarget();
            }

            @Override // android.printservice.IPrintService
            public void startPrinterStateTracking(PrinterId printerId) {
                PrintService.this.mHandler.obtainMessage(6, printerId).sendToTarget();
            }

            @Override // android.printservice.IPrintService
            public void stopPrinterStateTracking(PrinterId printerId) {
                PrintService.this.mHandler.obtainMessage(7, printerId).sendToTarget();
            }

            @Override // android.printservice.IPrintService
            public void setClient(IPrintServiceClient client) {
                PrintService.this.mHandler.obtainMessage(10, client).sendToTarget();
            }

            @Override // android.printservice.IPrintService
            public void requestCancelPrintJob(PrintJobInfo printJobInfo) {
                PrintService.this.mHandler.obtainMessage(9, printJobInfo).sendToTarget();
            }

            @Override // android.printservice.IPrintService
            public void onPrintJobQueued(PrintJobInfo printJobInfo) {
                PrintService.this.mHandler.obtainMessage(8, printJobInfo).sendToTarget();
            }
        };
    }

    /* loaded from: PrintService$ServiceHandler.class */
    private final class ServiceHandler extends Handler {
        public static final int MSG_CREATE_PRINTER_DISCOVERY_SESSION = 1;
        public static final int MSG_DESTROY_PRINTER_DISCOVERY_SESSION = 2;
        public static final int MSG_START_PRINTER_DISCOVERY = 3;
        public static final int MSG_STOP_PRINTER_DISCOVERY = 4;
        public static final int MSG_VALIDATE_PRINTERS = 5;
        public static final int MSG_START_PRINTER_STATE_TRACKING = 6;
        public static final int MSG_STOP_PRINTER_STATE_TRACKING = 7;
        public static final int MSG_ON_PRINTJOB_QUEUED = 8;
        public static final int MSG_ON_REQUEST_CANCEL_PRINTJOB = 9;
        public static final int MSG_SET_CLEINT = 10;

        public ServiceHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int action = message.what;
            switch (action) {
                case 1:
                    PrinterDiscoverySession session = PrintService.this.onCreatePrinterDiscoverySession();
                    if (session != null) {
                        if (session.getId() != PrintService.this.mLastSessionId) {
                            PrintService.this.mDiscoverySession = session;
                            PrintService.this.mLastSessionId = session.getId();
                            session.setObserver(PrintService.this.mClient);
                            return;
                        }
                        throw new IllegalStateException("cannot reuse session instances");
                    }
                    throw new NullPointerException("session cannot be null");
                case 2:
                    if (PrintService.this.mDiscoverySession != null) {
                        PrintService.this.mDiscoverySession.destroy();
                        PrintService.this.mDiscoverySession = null;
                        return;
                    }
                    return;
                case 3:
                    if (PrintService.this.mDiscoverySession != null) {
                        List<PrinterId> priorityList = (ArrayList) message.obj;
                        PrintService.this.mDiscoverySession.startPrinterDiscovery(priorityList);
                        return;
                    }
                    return;
                case 4:
                    if (PrintService.this.mDiscoverySession != null) {
                        PrintService.this.mDiscoverySession.stopPrinterDiscovery();
                        return;
                    }
                    return;
                case 5:
                    if (PrintService.this.mDiscoverySession != null) {
                        List<PrinterId> printerIds = (List) message.obj;
                        PrintService.this.mDiscoverySession.validatePrinters(printerIds);
                        return;
                    }
                    return;
                case 6:
                    if (PrintService.this.mDiscoverySession != null) {
                        PrinterId printerId = (PrinterId) message.obj;
                        PrintService.this.mDiscoverySession.startPrinterStateTracking(printerId);
                        return;
                    }
                    return;
                case 7:
                    if (PrintService.this.mDiscoverySession != null) {
                        PrinterId printerId2 = (PrinterId) message.obj;
                        PrintService.this.mDiscoverySession.stopPrinterStateTracking(printerId2);
                        return;
                    }
                    return;
                case 8:
                    PrintJobInfo printJobInfo = (PrintJobInfo) message.obj;
                    PrintService.this.onPrintJobQueued(new PrintJob(printJobInfo, PrintService.this.mClient));
                    return;
                case 9:
                    PrintJobInfo printJobInfo2 = (PrintJobInfo) message.obj;
                    PrintService.this.onRequestCancelPrintJob(new PrintJob(printJobInfo2, PrintService.this.mClient));
                    return;
                case 10:
                    PrintService.this.mClient = (IPrintServiceClient) message.obj;
                    if (PrintService.this.mClient != null) {
                        PrintService.this.onConnected();
                        return;
                    } else {
                        PrintService.this.onDisconnected();
                        return;
                    }
                default:
                    throw new IllegalArgumentException("Unknown message: " + action);
            }
        }
    }
}