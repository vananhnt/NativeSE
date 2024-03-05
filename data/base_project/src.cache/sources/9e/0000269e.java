package java.util.concurrent;

import java.util.Collection;
import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ExecutorService.class */
public interface ExecutorService extends Executor {
    void shutdown();

    List<Runnable> shutdownNow();

    boolean isShutdown();

    boolean isTerminated();

    boolean awaitTermination(long j, TimeUnit timeUnit) throws InterruptedException;

    <T> Future<T> submit(Callable<T> callable);

    <T> Future<T> submit(Runnable runnable, T t);

    Future<?> submit(Runnable runnable);

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException;

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long j, TimeUnit timeUnit) throws InterruptedException;

    <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException;

    <T> T invokeAny(Collection<? extends Callable<T>> collection, long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException;
}