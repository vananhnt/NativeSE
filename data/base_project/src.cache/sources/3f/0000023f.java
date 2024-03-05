package android.app.backup;

import android.app.backup.IRestoreObserver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

/* loaded from: RestoreSession.class */
public class RestoreSession {
    static final String TAG = "RestoreSession";
    final Context mContext;
    IRestoreSession mBinder;
    RestoreObserverWrapper mObserver = null;

    public int getAvailableRestoreSets(RestoreObserver observer) {
        int err = -1;
        RestoreObserverWrapper obsWrapper = new RestoreObserverWrapper(this.mContext, observer);
        try {
            err = this.mBinder.getAvailableRestoreSets(obsWrapper);
        } catch (RemoteException e) {
            Log.d(TAG, "Can't contact server to get available sets");
        }
        return err;
    }

    public int restoreAll(long token, RestoreObserver observer) {
        int err = -1;
        if (this.mObserver != null) {
            Log.d(TAG, "restoreAll() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, observer);
        try {
            err = this.mBinder.restoreAll(token, this.mObserver);
        } catch (RemoteException e) {
            Log.d(TAG, "Can't contact server to restore");
        }
        return err;
    }

    public int restoreSome(long token, RestoreObserver observer, String[] packages) {
        int err = -1;
        if (this.mObserver != null) {
            Log.d(TAG, "restoreAll() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, observer);
        try {
            err = this.mBinder.restoreSome(token, this.mObserver, packages);
        } catch (RemoteException e) {
            Log.d(TAG, "Can't contact server to restore packages");
        }
        return err;
    }

    public int restorePackage(String packageName, RestoreObserver observer) {
        int err = -1;
        if (this.mObserver != null) {
            Log.d(TAG, "restorePackage() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, observer);
        try {
            err = this.mBinder.restorePackage(packageName, this.mObserver);
        } catch (RemoteException e) {
            Log.d(TAG, "Can't contact server to restore package");
        }
        return err;
    }

    public void endRestoreSession() {
        try {
            try {
                this.mBinder.endRestoreSession();
                this.mBinder = null;
            } catch (RemoteException e) {
                Log.d(TAG, "Can't contact server to get available sets");
                this.mBinder = null;
            }
        } catch (Throwable th) {
            this.mBinder = null;
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RestoreSession(Context context, IRestoreSession binder) {
        this.mContext = context;
        this.mBinder = binder;
    }

    /* loaded from: RestoreSession$RestoreObserverWrapper.class */
    private class RestoreObserverWrapper extends IRestoreObserver.Stub {
        final Handler mHandler;
        final RestoreObserver mAppObserver;
        static final int MSG_RESTORE_STARTING = 1;
        static final int MSG_UPDATE = 2;
        static final int MSG_RESTORE_FINISHED = 3;
        static final int MSG_RESTORE_SETS_AVAILABLE = 4;

        RestoreObserverWrapper(Context context, RestoreObserver appObserver) {
            this.mHandler = new Handler(context.getMainLooper()) { // from class: android.app.backup.RestoreSession.RestoreObserverWrapper.1
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            RestoreObserverWrapper.this.mAppObserver.restoreStarting(msg.arg1);
                            return;
                        case 2:
                            RestoreObserverWrapper.this.mAppObserver.onUpdate(msg.arg1, (String) msg.obj);
                            return;
                        case 3:
                            RestoreObserverWrapper.this.mAppObserver.restoreFinished(msg.arg1);
                            return;
                        case 4:
                            RestoreObserverWrapper.this.mAppObserver.restoreSetsAvailable((RestoreSet[]) msg.obj);
                            return;
                        default:
                            return;
                    }
                }
            };
            this.mAppObserver = appObserver;
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreSetsAvailable(RestoreSet[] result) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(4, result));
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreStarting(int numPackages) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, numPackages, 0));
        }

        @Override // android.app.backup.IRestoreObserver
        public void onUpdate(int nowBeingRestored, String currentPackage) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, nowBeingRestored, 0, currentPackage));
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreFinished(int error) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3, error, 0));
        }
    }
}