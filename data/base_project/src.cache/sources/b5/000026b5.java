package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

/* loaded from: ForkJoinTask.class */
public abstract class ForkJoinTask<V> implements Future<V>, Serializable {
    volatile int status;
    static final int DONE_MASK = -268435456;
    static final int NORMAL = -268435456;
    static final int CANCELLED = -1073741824;
    static final int EXCEPTIONAL = Integer.MIN_VALUE;
    static final int SIGNAL = 65536;
    static final int SMASK = 65535;
    private static final int EXCEPTION_MAP_CAPACITY = 32;
    private static final long serialVersionUID = -7721805057305804111L;
    private static final Unsafe U;
    private static final long STATUS;
    private static final ReentrantLock exceptionTableLock = new ReentrantLock();
    private static final ReferenceQueue<Object> exceptionTableRefQueue = new ReferenceQueue<>();
    private static final ExceptionNode[] exceptionTable = new ExceptionNode[32];

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.recordExceptionalCompletion(java.lang.Throwable):int, file: ForkJoinTask.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    final int recordExceptionalCompletion(java.lang.Throwable r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.recordExceptionalCompletion(java.lang.Throwable):int, file: ForkJoinTask.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinTask.recordExceptionalCompletion(java.lang.Throwable):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.clearExceptionalCompletion():void, file: ForkJoinTask.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void clearExceptionalCompletion() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.clearExceptionalCompletion():void, file: ForkJoinTask.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinTask.clearExceptionalCompletion():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.getThrowableException():java.lang.Throwable, file: ForkJoinTask.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private java.lang.Throwable getThrowableException() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.getThrowableException():java.lang.Throwable, file: ForkJoinTask.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinTask.getThrowableException():java.lang.Throwable");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.helpExpungeStaleExceptions():void, file: ForkJoinTask.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    static final void helpExpungeStaleExceptions() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.helpExpungeStaleExceptions():void, file: ForkJoinTask.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinTask.helpExpungeStaleExceptions():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.get(long, java.util.concurrent.TimeUnit):V, file: ForkJoinTask.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.util.concurrent.Future
    public final V get(long r1, java.util.concurrent.TimeUnit r3) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinTask.get(long, java.util.concurrent.TimeUnit):V, file: ForkJoinTask.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinTask.get(long, java.util.concurrent.TimeUnit):java.lang.Object");
    }

    public abstract V getRawResult();

    protected abstract void setRawResult(V v);

    protected abstract boolean exec();

    private int setCompletion(int completion) {
        int s;
        do {
            s = this.status;
            if (s < 0) {
                return s;
            }
        } while (!U.compareAndSwapInt(this, STATUS, s, s | completion));
        if ((s >>> 16) != 0) {
            synchronized (this) {
                notifyAll();
            }
        }
        return completion;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int doExec() {
        int i = this.status;
        int s = i;
        if (i >= 0) {
            try {
                boolean completed = exec();
                if (completed) {
                    s = setCompletion(-268435456);
                }
            } catch (Throwable rex) {
                return setExceptionalCompletion(rex);
            }
        }
        return s;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean trySetSignal() {
        int s = this.status;
        return s >= 0 && U.compareAndSwapInt(this, STATUS, s, s | 65536);
    }

    private int externalAwaitDone() {
        int s;
        ForkJoinPool.externalHelpJoin(this);
        boolean interrupted = false;
        while (true) {
            s = this.status;
            if (s < 0) {
                break;
            } else if (U.compareAndSwapInt(this, STATUS, s, s | 65536)) {
                synchronized (this) {
                    if (this.status >= 0) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            interrupted = true;
                        }
                    } else {
                        notifyAll();
                    }
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return s;
    }

    private int externalInterruptibleAwaitDone() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        ForkJoinPool.externalHelpJoin(this);
        while (true) {
            int s = this.status;
            if (s >= 0) {
                if (U.compareAndSwapInt(this, STATUS, s, s | 65536)) {
                    synchronized (this) {
                        if (this.status >= 0) {
                            wait();
                        } else {
                            notifyAll();
                        }
                    }
                }
            } else {
                return s;
            }
        }
    }

    private int doJoin() {
        int s;
        int s2 = this.status;
        if (s2 < 0) {
            return s2;
        }
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread) t;
            ForkJoinPool.WorkQueue w = wt.workQueue;
            return (!w.tryUnpush(this) || (s = doExec()) >= 0) ? wt.pool.awaitJoin(w, this) : s;
        }
        return externalAwaitDone();
    }

    private int doInvoke() {
        int s = doExec();
        if (s < 0) {
            return s;
        }
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread) t;
            return wt.pool.awaitJoin(wt.workQueue, this);
        }
        return externalAwaitDone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinTask$ExceptionNode.class */
    public static final class ExceptionNode extends WeakReference<ForkJoinTask<?>> {
        final Throwable ex;
        ExceptionNode next;
        final long thrower;

        ExceptionNode(ForkJoinTask<?> task, Throwable ex, ExceptionNode next) {
            super(task, ForkJoinTask.exceptionTableRefQueue);
            this.ex = ex;
            this.next = next;
            this.thrower = Thread.currentThread().getId();
        }
    }

    private int setExceptionalCompletion(Throwable ex) {
        int s = recordExceptionalCompletion(ex);
        if ((s & (-268435456)) == Integer.MIN_VALUE) {
            internalPropagateException(ex);
        }
        return s;
    }

    void internalPropagateException(Throwable ex) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void cancelIgnoringExceptions(ForkJoinTask<?> t) {
        if (t != null && t.status >= 0) {
            try {
                t.cancel(false);
            } catch (Throwable th) {
            }
        }
    }

    private static void expungeStaleExceptions() {
        while (true) {
            Object x = exceptionTableRefQueue.poll();
            if (x != null) {
                if (x instanceof ExceptionNode) {
                    ForkJoinTask<?> key = ((ExceptionNode) x).get();
                    ExceptionNode[] t = exceptionTable;
                    int i = System.identityHashCode(key) & (t.length - 1);
                    ExceptionNode e = t[i];
                    ExceptionNode pred = null;
                    while (true) {
                        if (e != null) {
                            ExceptionNode next = e.next;
                            if (e == x) {
                                if (pred == null) {
                                    t[i] = next;
                                } else {
                                    pred.next = next;
                                }
                            } else {
                                pred = e;
                                e = next;
                            }
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    static void rethrow(Throwable ex) {
        if (ex != null) {
            if (ex instanceof Error) {
                throw ((Error) ex);
            }
            if (ex instanceof RuntimeException) {
                throw ((RuntimeException) ex);
            }
            throw ((RuntimeException) uncheckedThrowable(ex, RuntimeException.class));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <T extends Throwable> T uncheckedThrowable(Throwable t, Class<T> c) {
        return t;
    }

    private void reportException(int s) {
        if (s == -1073741824) {
            throw new CancellationException();
        }
        if (s == Integer.MIN_VALUE) {
            rethrow(getThrowableException());
        }
    }

    public final ForkJoinTask<V> fork() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ((ForkJoinWorkerThread) t).workQueue.push(this);
        } else {
            ForkJoinPool.commonPool.externalPush(this);
        }
        return this;
    }

    public final V join() {
        int s = doJoin() & (-268435456);
        if (s != -268435456) {
            reportException(s);
        }
        return getRawResult();
    }

    public final V invoke() {
        int s = doInvoke() & (-268435456);
        if (s != -268435456) {
            reportException(s);
        }
        return getRawResult();
    }

    public static void invokeAll(ForkJoinTask<?> t1, ForkJoinTask<?> t2) {
        t2.fork();
        int s1 = t1.doInvoke() & (-268435456);
        if (s1 != -268435456) {
            t1.reportException(s1);
        }
        int s2 = t2.doJoin() & (-268435456);
        if (s2 != -268435456) {
            t2.reportException(s2);
        }
    }

    public static void invokeAll(ForkJoinTask<?>... tasks) {
        Throwable ex = null;
        int last = tasks.length - 1;
        for (int i = last; i >= 0; i--) {
            ForkJoinTask<?> t = tasks[i];
            if (t == null) {
                if (ex == null) {
                    ex = new NullPointerException();
                }
            } else if (i != 0) {
                t.fork();
            } else if (t.doInvoke() < -268435456 && ex == null) {
                ex = t.getException();
            }
        }
        for (int i2 = 1; i2 <= last; i2++) {
            ForkJoinTask<?> t2 = tasks[i2];
            if (t2 != null) {
                if (ex != null) {
                    t2.cancel(false);
                } else if (t2.doJoin() < -268435456) {
                    ex = t2.getException();
                }
            }
        }
        if (ex != null) {
            rethrow(ex);
        }
    }

    public static <T extends ForkJoinTask<?>> Collection<T> invokeAll(Collection<T> tasks) {
        if (!(tasks instanceof RandomAccess) || !(tasks instanceof List)) {
            invokeAll((ForkJoinTask[]) tasks.toArray(new ForkJoinTask[tasks.size()]));
            return tasks;
        }
        List<? extends ForkJoinTask<?>> ts = (List) tasks;
        Throwable ex = null;
        int last = ts.size() - 1;
        for (int i = last; i >= 0; i--) {
            ForkJoinTask<?> t = ts.get(i);
            if (t == null) {
                if (ex == null) {
                    ex = new NullPointerException();
                }
            } else if (i != 0) {
                t.fork();
            } else if (t.doInvoke() < -268435456 && ex == null) {
                ex = t.getException();
            }
        }
        for (int i2 = 1; i2 <= last; i2++) {
            ForkJoinTask<?> t2 = ts.get(i2);
            if (t2 != null) {
                if (ex != null) {
                    t2.cancel(false);
                } else if (t2.doJoin() < -268435456) {
                    ex = t2.getException();
                }
            }
        }
        if (ex != null) {
            rethrow(ex);
        }
        return tasks;
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return (setCompletion(-1073741824) & (-268435456)) == -1073741824;
    }

    @Override // java.util.concurrent.Future
    public final boolean isDone() {
        return this.status < 0;
    }

    @Override // java.util.concurrent.Future
    public final boolean isCancelled() {
        return (this.status & (-268435456)) == -1073741824;
    }

    public final boolean isCompletedAbnormally() {
        return this.status < -268435456;
    }

    public final boolean isCompletedNormally() {
        return (this.status & (-268435456)) == -268435456;
    }

    public final Throwable getException() {
        int s = this.status & (-268435456);
        if (s >= -268435456) {
            return null;
        }
        return s == -1073741824 ? new CancellationException() : getThrowableException();
    }

    public void completeExceptionally(Throwable ex) {
        setExceptionalCompletion(((ex instanceof RuntimeException) || (ex instanceof Error)) ? ex : new RuntimeException(ex));
    }

    public void complete(V value) {
        try {
            setRawResult(value);
            setCompletion(-268435456);
        } catch (Throwable rex) {
            setExceptionalCompletion(rex);
        }
    }

    public final void quietlyComplete() {
        setCompletion(-268435456);
    }

    @Override // java.util.concurrent.Future
    public final V get() throws InterruptedException, ExecutionException {
        Throwable ex;
        int s = (Thread.currentThread() instanceof ForkJoinWorkerThread ? doJoin() : externalInterruptibleAwaitDone()) & (-268435456);
        if (s == -1073741824) {
            throw new CancellationException();
        }
        if (s == Integer.MIN_VALUE && (ex = getThrowableException()) != null) {
            throw new ExecutionException(ex);
        }
        return getRawResult();
    }

    public final void quietlyJoin() {
        doJoin();
    }

    public final void quietlyInvoke() {
        doInvoke();
    }

    public static void helpQuiesce() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread) t;
            wt.pool.helpQuiescePool(wt.workQueue);
            return;
        }
        ForkJoinPool.externalHelpQuiescePool();
    }

    public void reinitialize() {
        if ((this.status & (-268435456)) == Integer.MIN_VALUE) {
            clearExceptionalCompletion();
        } else {
            this.status = 0;
        }
    }

    public static ForkJoinPool getPool() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            return ((ForkJoinWorkerThread) t).pool;
        }
        return null;
    }

    public static boolean inForkJoinPool() {
        return Thread.currentThread() instanceof ForkJoinWorkerThread;
    }

    public boolean tryUnfork() {
        Thread t = Thread.currentThread();
        return t instanceof ForkJoinWorkerThread ? ((ForkJoinWorkerThread) t).workQueue.tryUnpush(this) : ForkJoinPool.tryExternalUnpush(this);
    }

    public static int getQueuedTaskCount() {
        ForkJoinPool.WorkQueue q;
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            q = ((ForkJoinWorkerThread) t).workQueue;
        } else {
            q = ForkJoinPool.commonSubmitterQueue();
        }
        if (q == null) {
            return 0;
        }
        return q.queueSize();
    }

    public static int getSurplusQueuedTaskCount() {
        return ForkJoinPool.getSurplusQueuedTaskCount();
    }

    protected static ForkJoinTask<?> peekNextLocalTask() {
        ForkJoinPool.WorkQueue q;
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            q = ((ForkJoinWorkerThread) t).workQueue;
        } else {
            q = ForkJoinPool.commonSubmitterQueue();
        }
        if (q == null) {
            return null;
        }
        return q.peek();
    }

    protected static ForkJoinTask<?> pollNextLocalTask() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            return ((ForkJoinWorkerThread) t).workQueue.nextLocalTask();
        }
        return null;
    }

    protected static ForkJoinTask<?> pollTask() {
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread) t;
            return wt.pool.nextTaskFor(wt.workQueue);
        }
        return null;
    }

    public final short getForkJoinTaskTag() {
        return (short) this.status;
    }

    public final short setForkJoinTaskTag(short tag) {
        Unsafe unsafe;
        long j;
        int s;
        do {
            unsafe = U;
            j = STATUS;
            s = this.status;
        } while (!unsafe.compareAndSwapInt(this, j, s, (s & (-65536)) | (tag & 65535)));
        return (short) s;
    }

    public final boolean compareAndSetForkJoinTaskTag(short e, short tag) {
        int s;
        do {
            s = this.status;
            if (((short) s) != e) {
                return false;
            }
        } while (!U.compareAndSwapInt(this, STATUS, s, (s & (-65536)) | (tag & 65535)));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinTask$AdaptedRunnable.class */
    public static final class AdaptedRunnable<T> extends ForkJoinTask<T> implements RunnableFuture<T> {
        final Runnable runnable;
        T result;
        private static final long serialVersionUID = 5232453952276885070L;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AdaptedRunnable(Runnable runnable, T result) {
            if (runnable == null) {
                throw new NullPointerException();
            }
            this.runnable = runnable;
            this.result = result;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final T getRawResult() {
            return this.result;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final void setRawResult(T v) {
            this.result = v;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        @Override // java.util.concurrent.RunnableFuture, java.lang.Runnable
        public final void run() {
            invoke();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinTask$AdaptedRunnableAction.class */
    public static final class AdaptedRunnableAction extends ForkJoinTask<Void> implements RunnableFuture<Void> {
        final Runnable runnable;
        private static final long serialVersionUID = 5232453952276885070L;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AdaptedRunnableAction(Runnable runnable) {
            if (runnable == null) {
                throw new NullPointerException();
            }
            this.runnable = runnable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.ForkJoinTask
        public final Void getRawResult() {
            return null;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final void setRawResult(Void v) {
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final boolean exec() {
            this.runnable.run();
            return true;
        }

        @Override // java.util.concurrent.RunnableFuture, java.lang.Runnable
        public final void run() {
            invoke();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinTask$AdaptedCallable.class */
    public static final class AdaptedCallable<T> extends ForkJoinTask<T> implements RunnableFuture<T> {
        final Callable<? extends T> callable;
        T result;
        private static final long serialVersionUID = 2838392045355241008L;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AdaptedCallable(Callable<? extends T> callable) {
            if (callable == null) {
                throw new NullPointerException();
            }
            this.callable = callable;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final T getRawResult() {
            return this.result;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final void setRawResult(T v) {
            this.result = v;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final boolean exec() {
            try {
                this.result = this.callable.call();
                return true;
            } catch (Error err) {
                throw err;
            } catch (RuntimeException rex) {
                throw rex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override // java.util.concurrent.RunnableFuture, java.lang.Runnable
        public final void run() {
            invoke();
        }
    }

    public static ForkJoinTask<?> adapt(Runnable runnable) {
        return new AdaptedRunnableAction(runnable);
    }

    public static <T> ForkJoinTask<T> adapt(Runnable runnable, T result) {
        return new AdaptedRunnable(runnable, result);
    }

    public static <T> ForkJoinTask<T> adapt(Callable<? extends T> callable) {
        return new AdaptedCallable(callable);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(getException());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Object ex = s.readObject();
        if (ex != null) {
            setExceptionalCompletion((Throwable) ex);
        }
    }

    static {
        try {
            U = Unsafe.getUnsafe();
            STATUS = U.objectFieldOffset(ForkJoinTask.class.getDeclaredField("status"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}