package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Future.class */
public interface Future<V> {
    boolean cancel(boolean z);

    boolean isCancelled();

    boolean isDone();

    V get() throws InterruptedException, ExecutionException;

    V get(long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException;
}