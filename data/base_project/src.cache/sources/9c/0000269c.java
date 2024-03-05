package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ExecutorCompletionService.class */
public class ExecutorCompletionService<V> implements CompletionService<V> {
    public ExecutorCompletionService(Executor executor) {
        throw new RuntimeException("Stub!");
    }

    public ExecutorCompletionService(Executor executor, BlockingQueue<Future<V>> completionQueue) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.CompletionService
    public Future<V> submit(Callable<V> task) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.CompletionService
    public Future<V> submit(Runnable task, V result) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.CompletionService
    public Future<V> take() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.CompletionService
    public Future<V> poll() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.CompletionService
    public Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ExecutorCompletionService$QueueingFuture.class */
    private class QueueingFuture extends FutureTask<Void> {
        private final Future<V> task;

        QueueingFuture(RunnableFuture<V> task) {
            super(task, null);
            this.task = task;
        }

        @Override // java.util.concurrent.FutureTask
        protected void done() {
            ExecutorCompletionService.access$000(ExecutorCompletionService.this).add(this.task);
        }
    }
}