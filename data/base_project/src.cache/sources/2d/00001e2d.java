package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import com.android.server.display.DisplayManagerService;
import java.io.PrintWriter;

/* loaded from: DisplayAdapter.class */
abstract class DisplayAdapter {
    private final DisplayManagerService.SyncRoot mSyncRoot;
    private final Context mContext;
    private final Handler mHandler;
    private final Listener mListener;
    private final String mName;
    public static final int DISPLAY_DEVICE_EVENT_ADDED = 1;
    public static final int DISPLAY_DEVICE_EVENT_CHANGED = 2;
    public static final int DISPLAY_DEVICE_EVENT_REMOVED = 3;

    /* loaded from: DisplayAdapter$Listener.class */
    public interface Listener {
        void onDisplayDeviceEvent(DisplayDevice displayDevice, int i);

        void onTraversalRequested();
    }

    public DisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, Listener listener, String name) {
        this.mSyncRoot = syncRoot;
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        this.mName = name;
    }

    public final DisplayManagerService.SyncRoot getSyncRoot() {
        return this.mSyncRoot;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final Handler getHandler() {
        return this.mHandler;
    }

    public final String getName() {
        return this.mName;
    }

    public void registerLocked() {
    }

    public void dumpLocked(PrintWriter pw) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void sendDisplayDeviceEventLocked(final DisplayDevice device, final int event) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.DisplayAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                DisplayAdapter.this.mListener.onDisplayDeviceEvent(device, event);
            }
        });
    }

    protected final void sendTraversalRequestLocked() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.display.DisplayAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                DisplayAdapter.this.mListener.onTraversalRequested();
            }
        });
    }
}