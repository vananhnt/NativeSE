package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RefQueueWorker.class */
public class RefQueueWorker implements Runnable {
    protected final ReferenceQueue<?> refQueue;
    protected final RefQueueHandler refHandler;
    protected volatile Thread workerThread;

    public RefQueueWorker(ReferenceQueue<?> queue, RefQueueHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Runnable
    public void run() {
        throw new RuntimeException("Stub!");
    }

    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}