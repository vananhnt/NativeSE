package com.android.server;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.StrictMode;
import android.util.Slog;

/* loaded from: UiThread.class */
public final class UiThread extends HandlerThread {
    private static UiThread sInstance;
    private static Handler sHandler;

    private UiThread() {
        super("android.ui", -2);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new UiThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
            sHandler.post(new Runnable() { // from class: com.android.server.UiThread.1
                @Override // java.lang.Runnable
                public void run() {
                    Process.setCanSelfBackground(false);
                    if (StrictMode.conditionallyEnableDebugLogging()) {
                        Slog.i("UiThread", "Enabled StrictMode logging for UI thread");
                    }
                }
            });
        }
    }

    public static UiThread get() {
        UiThread uiThread;
        synchronized (UiThread.class) {
            ensureThreadLocked();
            uiThread = sInstance;
        }
        return uiThread;
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