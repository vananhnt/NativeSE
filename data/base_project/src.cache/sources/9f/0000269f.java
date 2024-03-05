package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Executors.class */
public class Executors {
    Executors() {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService newFixedThreadPool(int nThreads) {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService newSingleThreadExecutor() {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService newCachedThreadPool() {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        throw new RuntimeException("Stub!");
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        throw new RuntimeException("Stub!");
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
        throw new RuntimeException("Stub!");
    }

    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        throw new RuntimeException("Stub!");
    }

    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        throw new RuntimeException("Stub!");
    }

    public static ThreadFactory defaultThreadFactory() {
        throw new RuntimeException("Stub!");
    }

    public static ThreadFactory privilegedThreadFactory() {
        throw new RuntimeException("Stub!");
    }

    public static <T> Callable<T> callable(Runnable task, T result) {
        throw new RuntimeException("Stub!");
    }

    public static Callable<Object> callable(Runnable task) {
        throw new RuntimeException("Stub!");
    }

    public static Callable<Object> callable(PrivilegedAction<?> action) {
        throw new RuntimeException("Stub!");
    }

    public static Callable<Object> callable(PrivilegedExceptionAction<?> action) {
        throw new RuntimeException("Stub!");
    }

    public static <T> Callable<T> privilegedCallable(Callable<T> callable) {
        throw new RuntimeException("Stub!");
    }

    public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.concurrent.Executors$1  reason: invalid class name */
    /* loaded from: Executors$1.class */
    static class AnonymousClass1 implements Callable<Object> {
        final /* synthetic */ PrivilegedAction val$action;

        AnonymousClass1(PrivilegedAction privilegedAction) {
            this.val$action = privilegedAction;
        }

        @Override // java.util.concurrent.Callable
        public Object call() {
            return this.val$action.run();
        }
    }

    /* renamed from: java.util.concurrent.Executors$2  reason: invalid class name */
    /* loaded from: Executors$2.class */
    static class AnonymousClass2 implements Callable<Object> {
        final /* synthetic */ PrivilegedExceptionAction val$action;

        AnonymousClass2(PrivilegedExceptionAction privilegedExceptionAction) {
            this.val$action = privilegedExceptionAction;
        }

        @Override // java.util.concurrent.Callable
        public Object call() throws Exception {
            return this.val$action.run();
        }
    }

    /* loaded from: Executors$RunnableAdapter.class */
    static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;

        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }

        @Override // java.util.concurrent.Callable
        public T call() {
            this.task.run();
            return this.result;
        }
    }

    /* loaded from: Executors$PrivilegedCallable.class */
    static final class PrivilegedCallable<T> implements Callable<T> {
        private final Callable<T> task;
        private final AccessControlContext acc = AccessController.getContext();

        PrivilegedCallable(Callable<T> task) {
            this.task = task;
        }

        @Override // java.util.concurrent.Callable
        public T call() throws Exception {
            try {
                return (T) AccessController.doPrivileged(new PrivilegedExceptionAction<T>() { // from class: java.util.concurrent.Executors.PrivilegedCallable.1
                    @Override // java.security.PrivilegedExceptionAction
                    public T run() throws Exception {
                        return (T) PrivilegedCallable.this.task.call();
                    }
                }, this.acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /* loaded from: Executors$PrivilegedCallableUsingCurrentClassLoader.class */
    static final class PrivilegedCallableUsingCurrentClassLoader<T> implements Callable<T> {
        private final Callable<T> task;
        private final AccessControlContext acc = AccessController.getContext();
        private final ClassLoader ccl = Thread.currentThread().getContextClassLoader();

        static /* synthetic */ ClassLoader access$100(PrivilegedCallableUsingCurrentClassLoader x0) {
            return x0.ccl;
        }

        static /* synthetic */ Callable access$200(PrivilegedCallableUsingCurrentClassLoader x0) {
            return x0.task;
        }

        PrivilegedCallableUsingCurrentClassLoader(Callable<T> task) {
            this.task = task;
        }

        @Override // java.util.concurrent.Callable
        public T call() throws Exception {
            try {
                return (T) AccessController.doPrivileged(new PrivilegedExceptionAction<T>() { // from class: java.util.concurrent.Executors.PrivilegedCallableUsingCurrentClassLoader.1
                    /*  JADX ERROR: Method load error
                        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.Executors.PrivilegedCallableUsingCurrentClassLoader.1.run():T, file: Executors$PrivilegedCallableUsingCurrentClassLoader$1.class
                        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                        	... 2 more
                        */
                    @Override // java.security.PrivilegedExceptionAction
                    public T run() throws java.lang.Exception {
                        /*
                        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.Executors.PrivilegedCallableUsingCurrentClassLoader.1.run():T, file: Executors$PrivilegedCallableUsingCurrentClassLoader$1.class
                        */
                        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Executors.PrivilegedCallableUsingCurrentClassLoader.AnonymousClass1.run():java.lang.Object");
                    }
                }, this.acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /* loaded from: Executors$DefaultThreadFactory.class */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Executors$PrivilegedThreadFactory.class */
    public static class PrivilegedThreadFactory extends DefaultThreadFactory {
        private final AccessControlContext acc = AccessController.getContext();
        private final ClassLoader ccl = Thread.currentThread().getContextClassLoader();

        PrivilegedThreadFactory() {
        }

        @Override // java.util.concurrent.Executors.DefaultThreadFactory, java.util.concurrent.ThreadFactory
        public Thread newThread(final Runnable r) {
            return super.newThread(new Runnable() { // from class: java.util.concurrent.Executors.PrivilegedThreadFactory.1
                @Override // java.lang.Runnable
                public void run() {
                    AccessController.doPrivileged(new PrivilegedAction<Void>() { // from class: java.util.concurrent.Executors.PrivilegedThreadFactory.1.1
                        /* JADX WARN: Can't rename method to resolve collision */
                        @Override // java.security.PrivilegedAction
                        public Void run() {
                            Thread.currentThread().setContextClassLoader(PrivilegedThreadFactory.this.ccl);
                            r.run();
                            return null;
                        }
                    }, PrivilegedThreadFactory.this.acc);
                }
            });
        }
    }

    /* loaded from: Executors$DelegatedExecutorService.class */
    static class DelegatedExecutorService extends AbstractExecutorService {
        private final ExecutorService e;

        DelegatedExecutorService(ExecutorService executor) {
            this.e = executor;
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable command) {
            this.e.execute(command);
        }

        @Override // java.util.concurrent.ExecutorService
        public void shutdown() {
            this.e.shutdown();
        }

        @Override // java.util.concurrent.ExecutorService
        public List<Runnable> shutdownNow() {
            return this.e.shutdownNow();
        }

        @Override // java.util.concurrent.ExecutorService
        public boolean isShutdown() {
            return this.e.isShutdown();
        }

        @Override // java.util.concurrent.ExecutorService
        public boolean isTerminated() {
            return this.e.isTerminated();
        }

        @Override // java.util.concurrent.ExecutorService
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return this.e.awaitTermination(timeout, unit);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public Future<?> submit(Runnable task) {
            return this.e.submit(task);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public <T> Future<T> submit(Callable<T> task) {
            return this.e.submit(task);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public <T> Future<T> submit(Runnable task, T result) {
            return this.e.submit(task, result);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return this.e.invokeAll(tasks);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return this.e.invokeAll(tasks, timeout, unit);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return (T) this.e.invokeAny(tasks);
        }

        @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return (T) this.e.invokeAny(tasks, timeout, unit);
        }
    }

    /* loaded from: Executors$FinalizableDelegatedExecutorService.class */
    static class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
        FinalizableDelegatedExecutorService(ExecutorService executor) {
            super(executor);
        }

        protected void finalize() {
            super.shutdown();
        }
    }

    /* loaded from: Executors$DelegatedScheduledExecutorService.class */
    static class DelegatedScheduledExecutorService extends DelegatedExecutorService implements ScheduledExecutorService {
        private final ScheduledExecutorService e;

        DelegatedScheduledExecutorService(ScheduledExecutorService executor) {
            super(executor);
            this.e = executor;
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return this.e.schedule(command, delay, unit);
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return this.e.schedule(callable, delay, unit);
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return this.e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        @Override // java.util.concurrent.ScheduledExecutorService
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return this.e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }
}