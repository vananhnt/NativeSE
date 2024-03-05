package android.printservice;

import android.os.RemoteException;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.text.TextUtils;
import android.util.Log;

/* loaded from: PrintJob.class */
public final class PrintJob {
    private static final String LOG_TAG = "PrintJob";
    private final IPrintServiceClient mPrintServiceClient;
    private final PrintDocument mDocument;
    private PrintJobInfo mCachedInfo;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrintJob(PrintJobInfo jobInfo, IPrintServiceClient client) {
        this.mCachedInfo = jobInfo;
        this.mPrintServiceClient = client;
        this.mDocument = new PrintDocument(this.mCachedInfo.getId(), client, jobInfo.getDocumentInfo());
    }

    public PrintJobId getId() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mCachedInfo.getId();
    }

    public PrintJobInfo getInfo() {
        PrintService.throwIfNotCalledOnMainThread();
        if (isInImmutableState()) {
            return this.mCachedInfo;
        }
        PrintJobInfo info = null;
        try {
            info = this.mPrintServiceClient.getPrintJobInfo(this.mCachedInfo.getId());
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Couldn't get info for job: " + this.mCachedInfo.getId(), re);
        }
        if (info != null) {
            this.mCachedInfo = info;
        }
        return this.mCachedInfo;
    }

    public PrintDocument getDocument() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mDocument;
    }

    public boolean isQueued() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getState() == 2;
    }

    public boolean isStarted() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getState() == 3;
    }

    public boolean isBlocked() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getState() == 4;
    }

    public boolean isCompleted() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getState() == 5;
    }

    public boolean isFailed() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getState() == 6;
    }

    public boolean isCancelled() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getState() == 7;
    }

    public boolean start() {
        PrintService.throwIfNotCalledOnMainThread();
        int state = getInfo().getState();
        if (state == 2 || state == 4) {
            return setState(3, null);
        }
        return false;
    }

    public boolean block(String reason) {
        PrintService.throwIfNotCalledOnMainThread();
        PrintJobInfo info = getInfo();
        int state = info.getState();
        if (state == 3 || (state == 4 && !TextUtils.equals(info.getStateReason(), reason))) {
            return setState(4, reason);
        }
        return false;
    }

    public boolean complete() {
        PrintService.throwIfNotCalledOnMainThread();
        if (isStarted()) {
            return setState(5, null);
        }
        return false;
    }

    public boolean fail(String error) {
        PrintService.throwIfNotCalledOnMainThread();
        if (!isInImmutableState()) {
            return setState(6, error);
        }
        return false;
    }

    public boolean cancel() {
        PrintService.throwIfNotCalledOnMainThread();
        if (!isInImmutableState()) {
            return setState(7, null);
        }
        return false;
    }

    public boolean setTag(String tag) {
        PrintService.throwIfNotCalledOnMainThread();
        if (isInImmutableState()) {
            return false;
        }
        try {
            return this.mPrintServiceClient.setPrintJobTag(this.mCachedInfo.getId(), tag);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error setting tag for job: " + this.mCachedInfo.getId(), re);
            return false;
        }
    }

    public String getTag() {
        PrintService.throwIfNotCalledOnMainThread();
        return getInfo().getTag();
    }

    public String getAdvancedStringOption(String key) {
        PrintService.throwIfNotCalledOnMainThread();
        return null;
    }

    public boolean hasAdvancedOption(String key) {
        PrintService.throwIfNotCalledOnMainThread();
        return false;
    }

    public int getAdvancedIntOption(String key) {
        PrintService.throwIfNotCalledOnMainThread();
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintJob other = (PrintJob) obj;
        return this.mCachedInfo.getId().equals(other.mCachedInfo.getId());
    }

    public int hashCode() {
        return this.mCachedInfo.getId().hashCode();
    }

    private boolean isInImmutableState() {
        int state = this.mCachedInfo.getState();
        return state == 5 || state == 7 || state == 6;
    }

    private boolean setState(int state, String error) {
        try {
            if (this.mPrintServiceClient.setPrintJobState(this.mCachedInfo.getId(), state, error)) {
                this.mCachedInfo.setState(state);
                this.mCachedInfo.setStateReason(error);
                return true;
            }
            return false;
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "Error setting the state of job: " + this.mCachedInfo.getId(), re);
            return false;
        }
    }
}