package android.service.notification;

import android.app.INotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.INotificationListener;
import android.util.Log;

/* loaded from: NotificationListenerService.class */
public abstract class NotificationListenerService extends Service {
    private final String TAG = NotificationListenerService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
    private INotificationListenerWrapper mWrapper = null;
    private INotificationManager mNoMan;
    public static final String SERVICE_INTERFACE = "android.service.notification.NotificationListenerService";

    public abstract void onNotificationPosted(StatusBarNotification statusBarNotification);

    public abstract void onNotificationRemoved(StatusBarNotification statusBarNotification);

    private final INotificationManager getNotificationInterface() {
        if (this.mNoMan == null) {
            this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        }
        return this.mNoMan;
    }

    public final void cancelNotification(String pkg, String tag, int id) {
        try {
            getNotificationInterface().cancelNotificationFromListener(this.mWrapper, pkg, tag, id);
        } catch (RemoteException ex) {
            Log.v(this.TAG, "Unable to contact notification manager", ex);
        }
    }

    public final void cancelAllNotifications() {
        try {
            getNotificationInterface().cancelAllNotificationsFromListener(this.mWrapper);
        } catch (RemoteException ex) {
            Log.v(this.TAG, "Unable to contact notification manager", ex);
        }
    }

    public StatusBarNotification[] getActiveNotifications() {
        try {
            return getNotificationInterface().getActiveNotificationsFromListener(this.mWrapper);
        } catch (RemoteException ex) {
            Log.v(this.TAG, "Unable to contact notification manager", ex);
            return null;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (this.mWrapper == null) {
            this.mWrapper = new INotificationListenerWrapper();
        }
        return this.mWrapper;
    }

    /* loaded from: NotificationListenerService$INotificationListenerWrapper.class */
    private class INotificationListenerWrapper extends INotificationListener.Stub {
        private INotificationListenerWrapper() {
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationPosted(StatusBarNotification sbn) {
            try {
                NotificationListenerService.this.onNotificationPosted(sbn);
            } catch (Throwable t) {
                Log.w(NotificationListenerService.this.TAG, "Error running onNotificationPosted", t);
            }
        }

        @Override // android.service.notification.INotificationListener
        public void onNotificationRemoved(StatusBarNotification sbn) {
            try {
                NotificationListenerService.this.onNotificationRemoved(sbn);
            } catch (Throwable t) {
                Log.w(NotificationListenerService.this.TAG, "Error running onNotificationRemoved", t);
            }
        }
    }
}