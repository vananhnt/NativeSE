package android.content;

import android.os.IBinder;

/* loaded from: ServiceConnection.class */
public interface ServiceConnection {
    void onServiceConnected(ComponentName componentName, IBinder iBinder);

    void onServiceDisconnected(ComponentName componentName);
}