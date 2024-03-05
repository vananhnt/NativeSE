package android.os;

import android.content.Context;
import android.os.IUpdateLock;
import android.util.Log;

/* loaded from: UpdateLock.class */
public class UpdateLock {
    private static final boolean DEBUG = false;
    private static final String TAG = "UpdateLock";
    private static IUpdateLock sService;
    final String mTag;
    public static final String UPDATE_LOCK_CHANGED = "android.os.UpdateLock.UPDATE_LOCK_CHANGED";
    public static final String NOW_IS_CONVENIENT = "nowisconvenient";
    public static final String TIMESTAMP = "timestamp";
    int mCount = 0;
    boolean mRefCounted = true;
    boolean mHeld = false;
    IBinder mToken = new Binder();

    private static void checkService() {
        if (sService == null) {
            sService = IUpdateLock.Stub.asInterface(ServiceManager.getService(Context.UPDATE_LOCK_SERVICE));
        }
    }

    public UpdateLock(String tag) {
        this.mTag = tag;
    }

    public void setReferenceCounted(boolean isRefCounted) {
        this.mRefCounted = isRefCounted;
    }

    public boolean isHeld() {
        boolean z;
        synchronized (this.mToken) {
            z = this.mHeld;
        }
        return z;
    }

    public void acquire() {
        checkService();
        synchronized (this.mToken) {
            acquireLocked();
        }
    }

    private void acquireLocked() {
        if (this.mRefCounted) {
            int i = this.mCount;
            this.mCount = i + 1;
            if (i != 0) {
                return;
            }
        }
        if (sService != null) {
            try {
                sService.acquireUpdateLock(this.mToken, this.mTag);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to contact service to acquire");
            }
        }
        this.mHeld = true;
    }

    public void release() {
        checkService();
        synchronized (this.mToken) {
            releaseLocked();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0012, code lost:
        if (r1 == 0) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void releaseLocked() {
        /*
            r4 = this;
            r0 = r4
            boolean r0 = r0.mRefCounted
            if (r0 == 0) goto L15
            r0 = r4
            r1 = r0
            int r1 = r1.mCount
            r2 = 1
            int r1 = r1 - r2
            r2 = r1; r1 = r0; r0 = r2; 
            r1.mCount = r2
            if (r0 != 0) goto L38
        L15:
            android.os.IUpdateLock r0 = android.os.UpdateLock.sService
            if (r0 == 0) goto L33
            android.os.IUpdateLock r0 = android.os.UpdateLock.sService     // Catch: android.os.RemoteException -> L2a
            r1 = r4
            android.os.IBinder r1 = r1.mToken     // Catch: android.os.RemoteException -> L2a
            r0.releaseUpdateLock(r1)     // Catch: android.os.RemoteException -> L2a
            goto L33
        L2a:
            r5 = move-exception
            java.lang.String r0 = "UpdateLock"
            java.lang.String r1 = "Unable to contact service to release"
            int r0 = android.util.Log.e(r0, r1)
        L33:
            r0 = r4
            r1 = 0
            r0.mHeld = r1
        L38:
            r0 = r4
            int r0 = r0.mCount
            if (r0 >= 0) goto L49
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            r1 = r0
            java.lang.String r2 = "UpdateLock under-locked"
            r1.<init>(r2)
            throw r0
        L49:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.UpdateLock.releaseLocked():void");
    }

    protected void finalize() throws Throwable {
        synchronized (this.mToken) {
            if (this.mHeld) {
                Log.wtf(TAG, "UpdateLock finalized while still held");
                try {
                    sService.releaseUpdateLock(this.mToken);
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to contact service to release");
                }
            }
        }
    }
}