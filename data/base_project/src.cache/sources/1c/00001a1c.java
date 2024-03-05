package com.android.internal.os;

/* loaded from: SomeArgs.class */
public final class SomeArgs {
    private static final int MAX_POOL_SIZE = 10;
    private static SomeArgs sPool;
    private static int sPoolSize;
    private static Object sPoolLock = new Object();
    private SomeArgs mNext;
    private boolean mInPool;
    public Object arg1;
    public Object arg2;
    public Object arg3;
    public Object arg4;
    public Object arg5;
    public int argi1;
    public int argi2;
    public int argi3;
    public int argi4;
    public int argi5;
    public int argi6;

    private SomeArgs() {
    }

    public static SomeArgs obtain() {
        synchronized (sPoolLock) {
            if (sPoolSize > 0) {
                SomeArgs args = sPool;
                sPool = sPool.mNext;
                args.mNext = null;
                args.mInPool = false;
                sPoolSize--;
                return args;
            }
            return new SomeArgs();
        }
    }

    public void recycle() {
        if (this.mInPool) {
            throw new IllegalStateException("Already recycled.");
        }
        synchronized (sPoolLock) {
            clear();
            if (sPoolSize < 10) {
                this.mNext = sPool;
                this.mInPool = true;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    private void clear() {
        this.arg1 = null;
        this.arg2 = null;
        this.arg3 = null;
        this.arg4 = null;
        this.arg5 = null;
        this.argi1 = 0;
        this.argi2 = 0;
        this.argi3 = 0;
        this.argi4 = 0;
        this.argi5 = 0;
        this.argi6 = 0;
    }
}