package android.app;

import android.app.INotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.Log;
import gov.nist.core.Separators;

/* loaded from: NotificationManager.class */
public class NotificationManager {
    private static String TAG = "NotificationManager";
    private static boolean localLOGV = false;
    private static INotificationManager sService;
    private Context mContext;

    public static INotificationManager getService() {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService(Context.NOTIFICATION_SERVICE);
        sService = INotificationManager.Stub.asInterface(b);
        return sService;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationManager(Context context, Handler handler) {
        this.mContext = context;
    }

    public static NotificationManager from(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(int id, Notification notification) {
        notify(null, id, notification);
    }

    public void notify(String tag, int id, Notification notification) {
        int[] idOut = new int[1];
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (notification.sound != null) {
            notification.sound = notification.sound.getCanonicalUri();
            if (StrictMode.vmFileUriExposureEnabled()) {
                notification.sound.checkFileUriExposed("Notification.sound");
            }
        }
        if (localLOGV) {
            Log.v(TAG, pkg + ": notify(" + id + ", " + notification + Separators.RPAREN);
        }
        try {
            service.enqueueNotificationWithTag(pkg, this.mContext.getOpPackageName(), tag, id, notification, idOut, UserHandle.myUserId());
            if (id != idOut[0]) {
                Log.w(TAG, "notify: id corrupted: sent " + id + ", got back " + idOut[0]);
            }
        } catch (RemoteException e) {
        }
    }

    public void notifyAsUser(String tag, int id, Notification notification, UserHandle user) {
        int[] idOut = new int[1];
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (notification.sound != null) {
            notification.sound = notification.sound.getCanonicalUri();
            if (StrictMode.vmFileUriExposureEnabled()) {
                notification.sound.checkFileUriExposed("Notification.sound");
            }
        }
        if (localLOGV) {
            Log.v(TAG, pkg + ": notify(" + id + ", " + notification + Separators.RPAREN);
        }
        try {
            service.enqueueNotificationWithTag(pkg, this.mContext.getOpPackageName(), tag, id, notification, idOut, user.getIdentifier());
            if (id != idOut[0]) {
                Log.w(TAG, "notify: id corrupted: sent " + id + ", got back " + idOut[0]);
            }
        } catch (RemoteException e) {
        }
    }

    public void cancel(int id) {
        cancel(null, id);
    }

    public void cancel(String tag, int id) {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (localLOGV) {
            Log.v(TAG, pkg + ": cancel(" + id + Separators.RPAREN);
        }
        try {
            service.cancelNotificationWithTag(pkg, tag, id, UserHandle.myUserId());
        } catch (RemoteException e) {
        }
    }

    public void cancelAsUser(String tag, int id, UserHandle user) {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (localLOGV) {
            Log.v(TAG, pkg + ": cancel(" + id + Separators.RPAREN);
        }
        try {
            service.cancelNotificationWithTag(pkg, tag, id, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void cancelAll() {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (localLOGV) {
            Log.v(TAG, pkg + ": cancelAll()");
        }
        try {
            service.cancelAllNotifications(pkg, UserHandle.myUserId());
        } catch (RemoteException e) {
        }
    }
}