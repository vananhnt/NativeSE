package android.content;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

/* loaded from: SyncContext.class */
public class SyncContext {
    private ISyncContext mSyncContext;
    private long mLastHeartbeatSendTime = 0;
    private static final long HEARTBEAT_SEND_INTERVAL_IN_MS = 1000;

    public SyncContext(ISyncContext syncContextInterface) {
        this.mSyncContext = syncContextInterface;
    }

    public void setStatusText(String message) {
        updateHeartbeat();
    }

    private void updateHeartbeat() {
        long now = SystemClock.elapsedRealtime();
        if (now < this.mLastHeartbeatSendTime + 1000) {
            return;
        }
        try {
            this.mLastHeartbeatSendTime = now;
            if (this.mSyncContext != null) {
                this.mSyncContext.sendHeartbeat();
            }
        } catch (RemoteException e) {
        }
    }

    public void onFinished(SyncResult result) {
        try {
            if (this.mSyncContext != null) {
                this.mSyncContext.onFinished(result);
            }
        } catch (RemoteException e) {
        }
    }

    public IBinder getSyncContextBinder() {
        if (this.mSyncContext == null) {
            return null;
        }
        return this.mSyncContext.asBinder();
    }
}