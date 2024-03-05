package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RunnableFuture.class */
public interface RunnableFuture<V> extends Runnable, Future<V> {
    @Override // java.lang.Runnable
    void run();
}