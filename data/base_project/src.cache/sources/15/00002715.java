package java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Condition.class */
public interface Condition {
    void await() throws InterruptedException;

    void awaitUninterruptibly();

    long awaitNanos(long j) throws InterruptedException;

    boolean await(long j, TimeUnit timeUnit) throws InterruptedException;

    boolean awaitUntil(Date date) throws InterruptedException;

    void signal();

    void signalAll();
}