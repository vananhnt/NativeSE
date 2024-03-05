package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CompletionService.class */
public interface CompletionService<V> {
    Future<V> submit(Callable<V> callable);

    Future<V> submit(Runnable runnable, V v);

    Future<V> take() throws InterruptedException;

    Future<V> poll();

    Future<V> poll(long j, TimeUnit timeUnit) throws InterruptedException;
}