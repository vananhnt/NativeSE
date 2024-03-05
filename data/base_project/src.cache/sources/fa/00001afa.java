package com.android.internal.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Slog;

/* loaded from: AsyncService.class */
public abstract class AsyncService extends Service {
    private static final String TAG = "AsyncService";
    protected static final boolean DBG = true;
    public static final int CMD_ASYNC_SERVICE_ON_START_INTENT = 16777215;
    public static final int CMD_ASYNC_SERVICE_DESTROY = 16777216;
    protected Messenger mMessenger;
    Handler mHandler;
    AsyncServiceInfo mAsyncServiceInfo;

    /* loaded from: AsyncService$AsyncServiceInfo.class */
    public static final class AsyncServiceInfo {
        public Handler mHandler;
        public int mRestartFlags;
    }

    public abstract AsyncServiceInfo createHandler();

    public Handler getHandler() {
        return this.mHandler;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mAsyncServiceInfo = createHandler();
        this.mHandler = this.mAsyncServiceInfo.mHandler;
        this.mMessenger = new Messenger(this.mHandler);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Slog.d(TAG, "onStartCommand");
        Message msg = this.mHandler.obtainMessage();
        msg.what = 16777215;
        msg.arg1 = flags;
        msg.arg2 = startId;
        msg.obj = intent;
        this.mHandler.sendMessage(msg);
        return this.mAsyncServiceInfo.mRestartFlags;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Slog.d(TAG, "onDestroy");
        Message msg = this.mHandler.obtainMessage();
        msg.what = 16777216;
        this.mHandler.sendMessage(msg);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }
}