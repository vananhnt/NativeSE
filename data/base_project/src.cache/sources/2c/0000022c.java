package android.app.backup;

import android.app.backup.IBackupManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/* loaded from: BackupManager.class */
public class BackupManager {
    private static final String TAG = "BackupManager";
    private Context mContext;
    private static IBackupManager sService;

    private static void checkServiceBinder() {
        if (sService == null) {
            sService = IBackupManager.Stub.asInterface(ServiceManager.getService(Context.BACKUP_SERVICE));
        }
    }

    public BackupManager(Context context) {
        this.mContext = context;
    }

    public void dataChanged() {
        checkServiceBinder();
        if (sService != null) {
            try {
                sService.dataChanged(this.mContext.getPackageName());
            } catch (RemoteException e) {
                Log.d(TAG, "dataChanged() couldn't connect");
            }
        }
    }

    public static void dataChanged(String packageName) {
        checkServiceBinder();
        if (sService != null) {
            try {
                sService.dataChanged(packageName);
            } catch (RemoteException e) {
                Log.d(TAG, "dataChanged(pkg) couldn't connect");
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    public int requestRestore(RestoreObserver observer) {
        int result = -1;
        checkServiceBinder();
        if (sService != null) {
            RestoreSession session = null;
            try {
                try {
                    IRestoreSession binder = sService.beginRestoreSession(this.mContext.getPackageName(), null);
                    if (binder != null) {
                        session = new RestoreSession(this.mContext, binder);
                        result = session.restorePackage(this.mContext.getPackageName(), observer);
                    }
                    if (session != null) {
                        session.endRestoreSession();
                    }
                } catch (RemoteException e) {
                    Log.w(TAG, "restoreSelf() unable to contact service");
                    if (session != null) {
                        session.endRestoreSession();
                    }
                }
            } catch (Throwable th) {
                if (session != null) {
                    session.endRestoreSession();
                }
                throw th;
            }
        }
        return result;
    }

    public RestoreSession beginRestoreSession() {
        RestoreSession session = null;
        checkServiceBinder();
        if (sService != null) {
            try {
                IRestoreSession binder = sService.beginRestoreSession(null, null);
                if (binder != null) {
                    session = new RestoreSession(this.mContext, binder);
                }
            } catch (RemoteException e) {
                Log.w(TAG, "beginRestoreSession() couldn't connect");
            }
        }
        return session;
    }
}