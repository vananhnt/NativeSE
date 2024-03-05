package com.android.internal.view;

import android.os.Looper;

/* loaded from: WindowManagerPolicyThread.class */
public class WindowManagerPolicyThread {
    static Thread mThread;
    static Looper mLooper;

    public static void set(Thread thread, Looper looper) {
        mThread = thread;
        mLooper = looper;
    }

    public static Thread getThread() {
        return mThread;
    }

    public static Looper getLooper() {
        return mLooper;
    }
}