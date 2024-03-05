package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ReentrantReadWriteLock.class */
public class ReentrantReadWriteLock implements ReadWriteLock, Serializable {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ReentrantReadWriteLock$ReadLock.class */
    public static class ReadLock implements Lock, Serializable {
        protected ReadLock(ReentrantReadWriteLock lock) {
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

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ReentrantReadWriteLock$WriteLock.class */
    public static class WriteLock implements Lock, Serializable {
        protected WriteLock(ReentrantReadWriteLock lock) {
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

        public String toString() {
            throw new RuntimeException("Stub!");
        }

        public boolean isHeldByCurrentThread() {
            throw new RuntimeException("Stub!");
        }

        public int getHoldCount() {
            throw new RuntimeException("Stub!");
        }
    }

    public ReentrantReadWriteLock() {
        throw new RuntimeException("Stub!");
    }

    public ReentrantReadWriteLock(boolean fair) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.ReadWriteLock
    public WriteLock writeLock() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.locks.ReadWriteLock
    public ReadLock readLock() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isFair() {
        throw new RuntimeException("Stub!");
    }

    protected Thread getOwner() {
        throw new RuntimeException("Stub!");
    }

    public int getReadLockCount() {
        throw new RuntimeException("Stub!");
    }

    public boolean isWriteLocked() {
        throw new RuntimeException("Stub!");
    }

    public boolean isWriteLockedByCurrentThread() {
        throw new RuntimeException("Stub!");
    }

    public int getWriteHoldCount() {
        throw new RuntimeException("Stub!");
    }

    public int getReadHoldCount() {
        throw new RuntimeException("Stub!");
    }

    protected Collection<Thread> getQueuedWriterThreads() {
        throw new RuntimeException("Stub!");
    }

    protected Collection<Thread> getQueuedReaderThreads() {
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

    /* loaded from: ReentrantReadWriteLock$Sync.class */
    static abstract class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 6317671515068378041L;
        static final int SHARED_SHIFT = 16;
        static final int SHARED_UNIT = 65536;
        static final int MAX_COUNT = 65535;
        static final int EXCLUSIVE_MASK = 65535;
        private transient HoldCounter cachedHoldCounter;
        private transient int firstReaderHoldCount;
        private transient Thread firstReader = null;
        private transient ThreadLocalHoldCounter readHolds = new ThreadLocalHoldCounter();

        abstract boolean readerShouldBlock();

        abstract boolean writerShouldBlock();

        static int sharedCount(int c) {
            return c >>> 16;
        }

        static int exclusiveCount(int c) {
            return c & 65535;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ReentrantReadWriteLock$Sync$HoldCounter.class */
        public static final class HoldCounter {
            int count = 0;
            final long tid = Thread.currentThread().getId();

            HoldCounter() {
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ReentrantReadWriteLock$Sync$ThreadLocalHoldCounter.class */
        public static final class ThreadLocalHoldCounter extends ThreadLocal<HoldCounter> {
            ThreadLocalHoldCounter() {
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public HoldCounter initialValue() {
                return new HoldCounter();
            }
        }

        Sync() {
            setState(getState());
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryRelease(int releases) {
            if (!isHeldExclusively()) {
                throw new IllegalMonitorStateException();
            }
            int nextc = getState() - releases;
            boolean free = exclusiveCount(nextc) == 0;
            if (free) {
                setExclusiveOwnerThread(null);
            }
            setState(nextc);
            return free;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if (c != 0) {
                if (w == 0 || current != getExclusiveOwnerThread()) {
                    return false;
                }
                if (w + exclusiveCount(acquires) > 65535) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(c + acquires);
                return true;
            } else if (writerShouldBlock() || !compareAndSetState(c, c + acquires)) {
                return false;
            } else {
                setExclusiveOwnerThread(current);
                return true;
            }
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean tryReleaseShared(int unused) {
            int c;
            int nextc;
            Thread current = Thread.currentThread();
            if (this.firstReader == current) {
                if (this.firstReaderHoldCount == 1) {
                    this.firstReader = null;
                } else {
                    this.firstReaderHoldCount--;
                }
            } else {
                HoldCounter rh = this.cachedHoldCounter;
                if (rh == null || rh.tid != current.getId()) {
                    rh = this.readHolds.get();
                }
                int count = rh.count;
                if (count <= 1) {
                    this.readHolds.remove();
                    if (count <= 0) {
                        throw unmatchedUnlockException();
                    }
                }
                rh.count--;
            }
            do {
                c = getState();
                nextc = c - 65536;
            } while (!compareAndSetState(c, nextc));
            return nextc == 0;
        }

        private IllegalMonitorStateException unmatchedUnlockException() {
            return new IllegalMonitorStateException("attempt to unlock read lock, not locked by current thread");
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final int tryAcquireShared(int unused) {
            Thread current = Thread.currentThread();
            int c = getState();
            if (exclusiveCount(c) != 0 && getExclusiveOwnerThread() != current) {
                return -1;
            }
            int r = sharedCount(c);
            if (!readerShouldBlock() && r < 65535 && compareAndSetState(c, c + 65536)) {
                if (r == 0) {
                    this.firstReader = current;
                    this.firstReaderHoldCount = 1;
                    return 1;
                } else if (this.firstReader == current) {
                    this.firstReaderHoldCount++;
                    return 1;
                } else {
                    HoldCounter rh = this.cachedHoldCounter;
                    if (rh == null || rh.tid != current.getId()) {
                        HoldCounter holdCounter = this.readHolds.get();
                        rh = holdCounter;
                        this.cachedHoldCounter = holdCounter;
                    } else if (rh.count == 0) {
                        this.readHolds.set(rh);
                    }
                    rh.count++;
                    return 1;
                }
            }
            return fullTryAcquireShared(current);
        }

        final int fullTryAcquireShared(Thread current) {
            int c;
            HoldCounter rh = null;
            do {
                c = getState();
                if (exclusiveCount(c) != 0) {
                    if (getExclusiveOwnerThread() != current) {
                        return -1;
                    }
                } else if (readerShouldBlock() && this.firstReader != current) {
                    if (rh == null) {
                        rh = this.cachedHoldCounter;
                        if (rh == null || rh.tid != current.getId()) {
                            rh = this.readHolds.get();
                            if (rh.count == 0) {
                                this.readHolds.remove();
                            }
                        }
                    }
                    if (rh.count == 0) {
                        return -1;
                    }
                }
                if (sharedCount(c) == 65535) {
                    throw new Error("Maximum lock count exceeded");
                }
            } while (!compareAndSetState(c, c + 65536));
            if (sharedCount(c) == 0) {
                this.firstReader = current;
                this.firstReaderHoldCount = 1;
                return 1;
            } else if (this.firstReader == current) {
                this.firstReaderHoldCount++;
                return 1;
            } else {
                if (rh == null) {
                    rh = this.cachedHoldCounter;
                }
                if (rh == null || rh.tid != current.getId()) {
                    rh = this.readHolds.get();
                } else if (rh.count == 0) {
                    this.readHolds.set(rh);
                }
                rh.count++;
                this.cachedHoldCounter = rh;
                return 1;
            }
        }

        final boolean tryWriteLock() {
            Thread current = Thread.currentThread();
            int c = getState();
            if (c != 0) {
                int w = exclusiveCount(c);
                if (w == 0 || current != getExclusiveOwnerThread()) {
                    return false;
                }
                if (w == 65535) {
                    throw new Error("Maximum lock count exceeded");
                }
            }
            if (!compareAndSetState(c, c + 1)) {
                return false;
            }
            setExclusiveOwnerThread(current);
            return true;
        }

        final boolean tryReadLock() {
            int c;
            int r;
            Thread current = Thread.currentThread();
            do {
                c = getState();
                if (exclusiveCount(c) != 0 && getExclusiveOwnerThread() != current) {
                    return false;
                }
                r = sharedCount(c);
                if (r == 65535) {
                    throw new Error("Maximum lock count exceeded");
                }
            } while (!compareAndSetState(c, c + 65536));
            if (r == 0) {
                this.firstReader = current;
                this.firstReaderHoldCount = 1;
                return true;
            } else if (this.firstReader == current) {
                this.firstReaderHoldCount++;
                return true;
            } else {
                HoldCounter rh = this.cachedHoldCounter;
                if (rh == null || rh.tid != current.getId()) {
                    HoldCounter holdCounter = this.readHolds.get();
                    rh = holdCounter;
                    this.cachedHoldCounter = holdCounter;
                } else if (rh.count == 0) {
                    this.readHolds.set(rh);
                }
                rh.count++;
                return true;
            }
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final AbstractQueuedSynchronizer.ConditionObject newCondition() {
            return new AbstractQueuedSynchronizer.ConditionObject();
        }

        final Thread getOwner() {
            if (exclusiveCount(getState()) == 0) {
                return null;
            }
            return getExclusiveOwnerThread();
        }

        final int getReadLockCount() {
            return sharedCount(getState());
        }

        final boolean isWriteLocked() {
            return exclusiveCount(getState()) != 0;
        }

        final int getWriteHoldCount() {
            if (isHeldExclusively()) {
                return exclusiveCount(getState());
            }
            return 0;
        }

        final int getReadHoldCount() {
            if (getReadLockCount() == 0) {
                return 0;
            }
            Thread current = Thread.currentThread();
            if (this.firstReader == current) {
                return this.firstReaderHoldCount;
            }
            HoldCounter rh = this.cachedHoldCounter;
            if (rh != null && rh.tid == current.getId()) {
                return rh.count;
            }
            int count = this.readHolds.get().count;
            if (count == 0) {
                this.readHolds.remove();
            }
            return count;
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            this.readHolds = new ThreadLocalHoldCounter();
            setState(0);
        }

        final int getCount() {
            return getState();
        }
    }

    /* loaded from: ReentrantReadWriteLock$NonfairSync.class */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = -8159625535654395037L;

        NonfairSync() {
        }

        @Override // java.util.concurrent.locks.ReentrantReadWriteLock.Sync
        final boolean writerShouldBlock() {
            return false;
        }

        @Override // java.util.concurrent.locks.ReentrantReadWriteLock.Sync
        final boolean readerShouldBlock() {
            return apparentlyFirstQueuedIsExclusive();
        }
    }

    /* loaded from: ReentrantReadWriteLock$FairSync.class */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -2274990926593161451L;

        FairSync() {
        }

        @Override // java.util.concurrent.locks.ReentrantReadWriteLock.Sync
        final boolean writerShouldBlock() {
            return hasQueuedPredecessors();
        }

        @Override // java.util.concurrent.locks.ReentrantReadWriteLock.Sync
        final boolean readerShouldBlock() {
            return hasQueuedPredecessors();
        }
    }
}