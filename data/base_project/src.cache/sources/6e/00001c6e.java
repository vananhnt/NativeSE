package com.android.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

/* loaded from: IoThread.class */
public final class IoThread extends HandlerThread {
    private static IoThread sInstance;
    private static Handler sHandler;

    private IoThread() {
        super("android.io", 0);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new IoThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
            sHandler.post(new Runnable() { // from class: com.android.server.IoThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Process.setCanSelfBackground(false);
                }
            });
        }
    }

    public static IoThread get() {
        IoThread ioThread;
        synchronized (IoThread.class) {
            ensureThreadLocked();
            ioThread = sInstance;
        }
        return ioThread;
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (IoThread.class) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }
}