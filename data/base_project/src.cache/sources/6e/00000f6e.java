package android.support.v4.app;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.INotificationSideChannel;

/* loaded from: NotificationCompatSideChannelService.class */
public abstract class NotificationCompatSideChannelService extends Service {

    /* loaded from: NotificationCompatSideChannelService$NotificationSideChannelStub.class */
    private class NotificationSideChannelStub extends INotificationSideChannel.Stub {
        final NotificationCompatSideChannelService this$0;

        private NotificationSideChannelStub(NotificationCompatSideChannelService notificationCompatSideChannelService) {
            this.this$0 = notificationCompatSideChannelService;
        }

        @Override // android.support.v4.app.INotificationSideChannel
        public void cancel(String str, int i, String str2) throws RemoteException {
            this.this$0.checkPermission(getCallingUid(), str);
            long clearCallingIdentity = clearCallingIdentity();
            try {
                this.this$0.cancel(str, i, str2);
            } finally {
                restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @Override // android.support.v4.app.INotificationSideChannel
        public void cancelAll(String str) {
            this.this$0.checkPermission(getCallingUid(), str);
            long clearCallingIdentity = clearCallingIdentity();
            try {
                this.this$0.cancelAll(str);
            } finally {
                restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @Override // android.support.v4.app.INotificationSideChannel
        public void notify(String str, int i, String str2, Notification notification) throws RemoteException {
            this.this$0.checkPermission(getCallingUid(), str);
            long clearCallingIdentity = clearCallingIdentity();
            try {
                this.this$0.notify(str, i, str2, notification);
            } finally {
                restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPermission(int i, String str) {
        for (String str2 : getPackageManager().getPackagesForUid(i)) {
            if (str2.equals(str)) {
                return;
            }
        }
        throw new SecurityException("NotificationSideChannelService: Uid " + i + " is not authorized for package " + str);
    }

    public abstract void cancel(String str, int i, String str2);

    public abstract void cancelAll(String str);

    public abstract void notify(String str, int i, String str2, Notification notification);

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (!intent.getAction().equals(NotificationManagerCompat.ACTION_BIND_SIDE_CHANNEL) || Build.VERSION.SDK_INT > 19) {
            return null;
        }
        return new NotificationSideChannelStub();
    }
}