package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Lock.class */
public interface Lock {
    void lock();

    void lockInterruptibly() throws InterruptedException;

    boolean tryLock();

    boolean tryLock(long j, TimeUnit timeUnit) throws InterruptedException;

    void unlock();

    Condition newCondition();
}