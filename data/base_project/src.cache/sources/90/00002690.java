package java.util.concurrent;

import sun.misc.Unsafe;

/* loaded from: CountedCompleter.class */
public abstract class CountedCompleter<T> extends ForkJoinTask<T> {
    private static final long serialVersionUID = 5232453752276485070L;
    final CountedCompleter<?> completer;
    volatile int pending;
    private static final Unsafe U;
    private static final long PENDING;

    public abstract void compute();

    protected CountedCompleter(CountedCompleter<?> completer, int initialPendingCount) {
        this.completer = completer;
        this.pending = initialPendingCount;
    }

    protected CountedCompleter(CountedCompleter<?> completer) {
        this.completer = completer;
    }

    protected CountedCompleter() {
        this.completer = null;
    }

    public void onCompletion(CountedCompleter<?> caller) {
    }

    public boolean onExceptionalCompletion(Throwable ex, CountedCompleter<?> caller) {
        return true;
    }

    public final CountedCompleter<?> getCompleter() {
        return this.completer;
    }

    public final int getPendingCount() {
        return this.pending;
    }

    public final void setPendingCount(int count) {
        this.pending = count;
    }

    public final void addToPendingCount(int delta) {
        Unsafe unsafe;
        long j;
        int c;
        do {
            unsafe = U;
            j = PENDING;
            c = this.pending;
        } while (!unsafe.compareAndSwapInt(this, j, c, c + delta));
    }

    public final boolean compareAndSetPendingCount(int expected, int count) {
        return U.compareAndSwapInt(this, PENDING, expected, count);
    }

    public final int decrementPendingCountUnlessZero() {
        int c;
        do {
            c = this.pending;
            if (c == 0) {
                break;
            }
        } while (!U.compareAndSwapInt(this, PENDING, c, c - 1));
        return c;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final CountedCompleter<?> getRoot() {
        CountedCompleter<?> countedCompleter = this;
        while (true) {
            CountedCompleter countedCompleter2 = countedCompleter;
            CountedCompleter<?> p = countedCompleter2.completer;
            if (p != null) {
                countedCompleter = p;
            } else {
                return countedCompleter2;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void tryComplete() {
        CountedCompleter countedCompleter = this;
        CountedCompleter countedCompleter2 = countedCompleter;
        while (true) {
            int c = countedCompleter.pending;
            if (c == 0) {
                countedCompleter.onCompletion(countedCompleter2);
                CountedCompleter countedCompleter3 = countedCompleter;
                countedCompleter2 = countedCompleter3;
                CountedCompleter countedCompleter4 = countedCompleter3.completer;
                countedCompleter = countedCompleter4;
                if (countedCompleter4 == null) {
                    countedCompleter2.quietlyComplete();
                    return;
                }
            } else if (U.compareAndSwapInt(countedCompleter, PENDING, c, c - 1)) {
                return;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void propagateCompletion() {
        CountedCompleter countedCompleter = this;
        while (true) {
            int c = countedCompleter.pending;
            if (c == 0) {
                CountedCompleter countedCompleter2 = countedCompleter;
                CountedCompleter countedCompleter3 = countedCompleter2.completer;
                countedCompleter = countedCompleter3;
                if (countedCompleter3 == null) {
                    countedCompleter2.quietlyComplete();
                    return;
                }
            } else if (U.compareAndSwapInt(countedCompleter, PENDING, c, c - 1)) {
                return;
            }
        }
    }

    @Override // java.util.concurrent.ForkJoinTask
    public void complete(T rawResult) {
        setRawResult(rawResult);
        onCompletion(this);
        quietlyComplete();
        CountedCompleter<?> p = this.completer;
        if (p != null) {
            p.tryComplete();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final CountedCompleter<?> firstComplete() {
        int c;
        do {
            c = this.pending;
            if (c == 0) {
                return this;
            }
        } while (!U.compareAndSwapInt(this, PENDING, c, c - 1));
        return null;
    }

    public final CountedCompleter<?> nextComplete() {
        CountedCompleter<?> p = this.completer;
        if (p != null) {
            return p.firstComplete();
        }
        quietlyComplete();
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void quietlyCompleteRoot() {
        CountedCompleter<?> countedCompleter = this;
        while (true) {
            CountedCompleter countedCompleter2 = countedCompleter;
            CountedCompleter<?> p = countedCompleter2.completer;
            if (p == null) {
                countedCompleter2.quietlyComplete();
                return;
            }
            countedCompleter = p;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.ForkJoinTask
    void internalPropagateException(Throwable ex) {
        CountedCompleter countedCompleter = this;
        CountedCompleter countedCompleter2 = countedCompleter;
        while (countedCompleter.onExceptionalCompletion(ex, countedCompleter2)) {
            CountedCompleter countedCompleter3 = countedCompleter;
            countedCompleter2 = countedCompleter3;
            CountedCompleter countedCompleter4 = countedCompleter3.completer;
            countedCompleter = countedCompleter4;
            if (countedCompleter4 != null && countedCompleter.status >= 0) {
                countedCompleter.recordExceptionalCompletion(ex);
            } else {
                return;
            }
        }
    }

    @Override // java.util.concurrent.ForkJoinTask
    protected final boolean exec() {
        compute();
        return false;
    }

    @Override // java.util.concurrent.ForkJoinTask
    public T getRawResult() {
        return null;
    }

    @Override // java.util.concurrent.ForkJoinTask
    protected void setRawResult(T t) {
    }

    static {
        try {
            U = Unsafe.getUnsafe();
            PENDING = U.objectFieldOffset(CountedCompleter.class.getDeclaredField("pending"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}