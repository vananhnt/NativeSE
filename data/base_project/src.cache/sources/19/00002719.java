package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ReentrantLock.class */
public class ReentrantLock implements Lock, Serializable {
    public ReentrantLock() {
        throw new RuntimeException("Stub!");
    }

    public ReentrantLock(boolean fair) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.Lock
    public void lock() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.Lock
    public void lockInterruptibly() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.Lock
    public boolean tryLock() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.Lock
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.Lock
    public void unlock() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.Lock
    public Condition newCondition() {
        throw new RuntimeException("Stub!");
    }

    public int getHoldCount() {
        throw new RuntimeException("Stub!");
    }

    public boolean isHeldByCurrentThread() {
        throw new RuntimeException("Stub!");
    }

    public boolean isLocked() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isFair() {
        throw new RuntimeException("Stub!");
    }

    protected Thread getOwner() {
        throw new RuntimeException("Stub!");
    }

    public final boolean hasQueuedThreads() {
        throw new RuntimeException("Stub!");
    }

    public final boolean hasQueuedThread(Thread thread) {
        throw new RuntimeException("Stub!");
    }

    public final int getQueueLength() {
        throw new RuntimeException("Stub!");
    }

    protected Collection<Thread> getQueuedThreads() {
        throw new RuntimeException("Stub!");
    }

    public boolean hasWaiters(Condition condition) {
        throw new RuntimeException("Stub!");
    }

    public int getWaitQueueLength(Condition condition) {
        throw new RuntimeException("Stub!");
    }

    protected Collection<Thread> getWaitingThreads(Condition condition) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ReentrantLock$Sync.class */
    static abstract class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        abstract void lock();

        Sync() {
        }

        final boolean nonfairTryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
                return false;
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            } else {
                return false;
            }
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final AbstractQueuedSynchronizer.ConditionObject newCondition() {
            return new AbstractQueuedSynchronizer.ConditionObject();
        }

        final Thread getOwner() {
            if (getState() == 0) {
                return null;
            }
            return getExclusiveOwnerThread();
        }

        final int getHoldCount() {
            if (isHeldExclusively()) {
                return getState();
            }
            return 0;
        }

        final boolean isLocked() {
            return getState() != 0;
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0);
        }
    }

    /* loaded from: ReentrantLock$NonfairSync.class */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        NonfairSync() {
        }

        @Override // java.util.concurrent.locks.ReentrantLock.Sync
        final void lock() {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
            } else {
                acquire(1);
            }
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /* loaded from: ReentrantLock$FairSync.class */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        FairSync() {
        }

        @Override // java.util.concurrent.locks.ReentrantLock.Sync
        final void lock() {
            acquire(1);
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
                return false;
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            } else {
                return false;
            }
        }
    }
}