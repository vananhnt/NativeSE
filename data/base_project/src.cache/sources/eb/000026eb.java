package java.util.concurrent;

import java.util.List;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ThreadPoolExecutor.class */
public class ThreadPoolExecutor extends AbstractExecutorService {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ThreadPoolExecutor$CallerRunsPolicy.class */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        public CallerRunsPolicy() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ThreadPoolExecutor$AbortPolicy.class */
    public static class AbortPolicy implements RejectedExecutionHandler {
        public AbortPolicy() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ThreadPoolExecutor$DiscardPolicy.class */
    public static class DiscardPolicy implements RejectedExecutionHandler {
        public DiscardPolicy() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ThreadPoolExecutor$DiscardOldestPolicy.class */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        public DiscardOldestPolicy() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.concurrent.RejectedExecutionHandler
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RuntimeException("Stub!");
        }
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        throw new RuntimeException("Stub!");
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        throw new RuntimeException("Stub!");
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ExecutorService
    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        throw new RuntimeException("Stub!");
    }

    public boolean isTerminating() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() {
        throw new RuntimeException("Stub!");
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public ThreadFactory getThreadFactory() {
        throw new RuntimeException("Stub!");
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        throw new RuntimeException("Stub!");
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        throw new RuntimeException("Stub!");
    }

    public void setCorePoolSize(int corePoolSize) {
        throw new RuntimeException("Stub!");
    }

    public int getCorePoolSize() {
        throw new RuntimeException("Stub!");
    }

    public boolean prestartCoreThread() {
        throw new RuntimeException("Stub!");
    }

    public int prestartAllCoreThreads() {
        throw new RuntimeException("Stub!");
    }

    public boolean allowsCoreThreadTimeOut() {
        throw new RuntimeException("Stub!");
    }

    public void allowCoreThreadTimeOut(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        throw new RuntimeException("Stub!");
    }

    public int getMaximumPoolSize() {
        throw new RuntimeException("Stub!");
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    public long getKeepAliveTime(TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    public BlockingQueue<Runnable> getQueue() {
        throw new RuntimeException("Stub!");
    }

    public boolean remove(Runnable task) {
        throw new RuntimeException("Stub!");
    }

    public void purge() {
        throw new RuntimeException("Stub!");
    }

    public int getPoolSize() {
        throw new RuntimeException("Stub!");
    }

    public int getActiveCount() {
        throw new RuntimeException("Stub!");
    }

    public int getLargestPoolSize() {
        throw new RuntimeException("Stub!");
    }

    public long getTaskCount() {
        throw new RuntimeException("Stub!");
    }

    public long getCompletedTaskCount() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    protected void beforeExecute(Thread t, Runnable r) {
        throw new RuntimeException("Stub!");
    }

    protected void afterExecute(Runnable r, Throwable t) {
        throw new RuntimeException("Stub!");
    }

    protected void terminated() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ThreadPoolExecutor$Worker.class */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        private static final long serialVersionUID = 6138294804551838833L;
        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1);
            this.firstTask = firstTask;
            this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            ThreadPoolExecutor.this.runWorker(this);
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override // java.util.concurrent.locks.AbstractQueuedSynchronizer
        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = this.thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException e) {
                }
            }
        }
    }
}