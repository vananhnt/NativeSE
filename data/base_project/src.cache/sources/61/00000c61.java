package android.printservice;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.PrintDocumentInfo;
import android.print.PrintJobId;
import android.util.Log;
import java.io.IOException;

/* loaded from: PrintDocument.class */
public final class PrintDocument {
    private static final String LOG_TAG = "PrintDocument";
    private final PrintJobId mPrintJobId;
    private final IPrintServiceClient mPrintServiceClient;
    private final PrintDocumentInfo mInfo;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrintDocument(PrintJobId printJobId, IPrintServiceClient printServiceClient, PrintDocumentInfo info) {
        this.mPrintJobId = printJobId;
        this.mPrintServiceClient = printServiceClient;
        this.mInfo = info;
    }

    public PrintDocumentInfo getInfo() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mInfo;
    }

    public ParcelFileDescriptor getData() {
        PrintService.throwIfNotCalledOnMainThread();
        ParcelFileDescriptor sink = null;
        try {
            try {
                ParcelFileDescriptor[] fds = ParcelFileDescriptor.createPipe();
                ParcelFileDescriptor source = fds[0];
                sink = fds[1];
                this.mPrintServiceClient.writePrintJobData(sink, this.mPrintJobId);
                if (sink != null) {
                    try {
                        sink.close();
                    } catch (IOException e) {
                    }
                }
                return source;
            } catch (RemoteException re) {
                Log.e(LOG_TAG, "Error calling getting print job data!", re);
                if (sink != null) {
                    try {
                        sink.close();
                        return null;
                    } catch (IOException e2) {
                        return null;
                    }
                }
                return null;
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Error calling getting print job data!", ioe);
                if (sink != null) {
                    try {
                        sink.close();
                        return null;
                    } catch (IOException e3) {
                        return null;
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (sink != null) {
                try {
                    sink.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }
}