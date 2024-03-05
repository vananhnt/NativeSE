package java.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CountDownLatch.class */
public class CountDownLatch {
    public CountDownLatch(int count) {
        throw new RuntimeException("Stub!");
    }

    public void await() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void countDown() {
        throw new RuntimeException("Stub!");
    }

    public long getCount() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: CountDownLatch$Sync.class */
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected int tryAcquireShared(int acquires) {
            return getState() == 0 ? 1 : -1;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean tryReleaseShared(int releases) {
            int c;
            int nextc;
            do {
                c = getState();
                if (c == 0) {
                    return false;
                }
                nextc = c - 1;
            } while (!compareAndSetState(c, nextc));
            return nextc == 0;
        }
    }
}