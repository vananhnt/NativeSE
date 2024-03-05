package java.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Semaphore.class */
public class Semaphore implements Serializable {
    public Semaphore(int permits) {
        throw new RuntimeException("Stub!");
    }

    public Semaphore(int permits, boolean fair) {
        throw new RuntimeException("Stub!");
    }

    public void acquire() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void acquireUninterruptibly() {
        throw new RuntimeException("Stub!");
    }

    public boolean tryAcquire() {
        throw new RuntimeException("Stub!");
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void release() {
        throw new RuntimeException("Stub!");
    }

    public void acquire(int permits) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void acquireUninterruptibly(int permits) {
        throw new RuntimeException("Stub!");
    }

    public boolean tryAcquire(int permits) {
        throw new RuntimeException("Stub!");
    }

    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void release(int permits) {
        throw new RuntimeException("Stub!");
    }

    public int availablePermits() {
        throw new RuntimeException("Stub!");
    }

    public int drainPermits() {
        throw new RuntimeException("Stub!");
    }

    protected void reducePermits(int reduction) {
        throw new RuntimeException("Stub!");
    }

    public boolean isFair() {
        throw new RuntimeException("Stub!");
    }

    public final boolean hasQueuedThreads() {
        throw new RuntimeException("Stub!");
    }

    public final int getQueueLength() {
        throw new RuntimeException("Stub!");
    }

    protected Collection<Thread> getQueuedThreads() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Semaphore$Sync.class */
    static abstract class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1192457210091910933L;

        Sync(int permits) {
            setState(permits);
        }

        final int getPermits() {
            return getState();
        }

        final int nonfairTryAcquireShared(int acquires) {
            int available;
            int remaining;
            do {
                available = getState();
                remaining = available - acquires;
                if (remaining < 0) {
                    break;
                }
            } while (!compareAndSetState(available, remaining));
            return remaining;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryReleaseShared(int releases) {
            int current;
            int next;
            do {
                current = getState();
                next = current + releases;
                if (next < current) {
                    throw new Error("Maximum permit count exceeded");
                }
            } while (!compareAndSetState(current, next));
            return true;
        }

        final void reducePermits(int reductions) {
            int current;
            int next;
            do {
                current = getState();
                next = current - reductions;
                if (next > current) {
                    throw new Error("Permit count underflow");
                }
            } while (!compareAndSetState(current, next));
        }

        final int drainPermits() {
            int current;
            do {
                current = getState();
                if (current == 0) {
                    break;
                }
            } while (!compareAndSetState(current, 0));
            return current;
        }
    }

    /* loaded from: Semaphore$NonfairSync.class */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = -2694183684443567898L;

        NonfairSync(int permits) {
            super(permits);
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected int tryAcquireShared(int acquires) {
            return nonfairTryAcquireShared(acquires);
        }
    }

    /* loaded from: Semaphore$FairSync.class */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = 2014338818796000944L;

        FairSync(int permits) {
            super(permits);
        }

        /* JADX WARN: Removed duplicated region for block: B:6:0x0009  */
        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected int tryAcquireShared(int r5) {
            /*
                r4 = this;
            L0:
                r0 = r4
                boolean r0 = r0.hasQueuedPredecessors()
                if (r0 == 0) goto L9
                r0 = -1
                return r0
            L9:
                r0 = r4
                int r0 = r0.getState()
                r6 = r0
                r0 = r6
                r1 = r5
                int r0 = r0 - r1
                r7 = r0
                r0 = r7
                if (r0 < 0) goto L1f
                r0 = r4
                r1 = r6
                r2 = r7
                boolean r0 = r0.compareAndSetState(r1, r2)
                if (r0 == 0) goto L21
            L1f:
                r0 = r7
                return r0
            L21:
                goto L0
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Semaphore.FairSync.tryAcquireShared(int):int");
        }
    }
}