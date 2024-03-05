package com.android.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

/* loaded from: FgThread.class */
public final class FgThread extends HandlerThread {
    private static FgThread sInstance;
    private static Handler sHandler;

    private FgThread() {
        super("android.fg", 0);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new FgThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
            sHandler.post(new Runnable() { // from class: com.android.server.FgThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Process.setCanSelfBackground(false);
                }
            });
        }
    }

    public static FgThread get() {
        FgThread fgThread;
        synchronized (UiThread.class) {
            ensureThreadLocked();
            fgThread = sInstance;
        }
        return fgThread;
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (UiThread.class) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }
}