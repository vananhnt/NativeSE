package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FutureTask.class */
public class FutureTask<V> implements RunnableFuture<V> {
    public FutureTask(Callable<V> callable) {
        throw new RuntimeException("Stub!");
    }

    public FutureTask(Runnable runnable, V result) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.Future
    public V get() throws InterruptedException, ExecutionException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.Future
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new RuntimeException("Stub!");
    }

    protected void done() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void set(V v) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setException(Throwable t) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.RunnableFuture, java.lang.Runnable
    public void run() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean runAndReset() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: FutureTask$WaitNode.class */
    static final class WaitNode {
        volatile Thread thread = Thread.currentThread();
        volatile WaitNode next;

        WaitNode() {
        }
    }
}