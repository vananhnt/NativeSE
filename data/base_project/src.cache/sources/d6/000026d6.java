package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ScheduledThreadPoolExecutor.class */
public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService {
    public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(0, 0, 0L, null, null, null, null);
        throw new RuntimeException("Stub!");
    }

    public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(0, 0, 0L, null, null, null, null);
        throw new RuntimeException("Stub!");
    }

    public ScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(0, 0, 0L, null, null, null, null);
        throw new RuntimeException("Stub!");
    }

    public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(0, 0, 0L, null, null, null, null);
        throw new RuntimeException("Stub!");
    }

    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        throw new RuntimeException("Stub!");
    }

    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ScheduledExecutorService
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ScheduledExecutorService
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ScheduledExecutorService
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ScheduledExecutorService
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
    public void execute(Runnable command) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public Future<?> submit(Runnable task) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public <T> Future<T> submit(Runnable task, T result) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public <T> Future<T> submit(Callable<T> task) {
        throw new RuntimeException("Stub!");
    }

    public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
        throw new RuntimeException("Stub!");
    }

    public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ThreadPoolExecutor
    public BlockingQueue<Runnable> getQueue() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ScheduledThreadPoolExecutor$ScheduledFutureTask.class */
    public class ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
        private final long sequenceNumber;
        private long time;
        private final long period;
        RunnableScheduledFuture<V> outerTask;
        int heapIndex;

        ScheduledFutureTask(Runnable r, V result, long ns) {
            super(r, result);
            this.outerTask = this;
            this.time = ns;
            this.period = 0L;
            this.sequenceNumber = ScheduledThreadPoolExecutor.access$000().getAndIncrement();
        }

        ScheduledFutureTask(Runnable r, V result, long ns, long period) {
            super(r, result);
            this.outerTask = this;
            this.time = ns;
            this.period = period;
            this.sequenceNumber = ScheduledThreadPoolExecutor.access$000().getAndIncrement();
        }

        ScheduledFutureTask(Callable<V> callable, long ns) {
            super(callable);
            this.outerTask = this;
            this.time = ns;
            this.period = 0L;
            this.sequenceNumber = ScheduledThreadPoolExecutor.access$000().getAndIncrement();
        }

        @Override // java.util.concurrent.Delayed
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.time - ScheduledThreadPoolExecutor.this.now(), TimeUnit.NANOSECONDS);
        }

        @Override // java.lang.Comparable
        public int compareTo(Delayed other) {
            if (other == this) {
                return 0;
            }
            if (other instanceof ScheduledFutureTask) {
                ScheduledFutureTask<?> x = (ScheduledFutureTask) other;
                long diff = this.time - x.time;
                if (diff < 0) {
                    return -1;
                }
                if (diff <= 0 && this.sequenceNumber < x.sequenceNumber) {
                    return -1;
                }
                return 1;
            }
            long diff2 = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);
            if (diff2 < 0) {
                return -1;
            }
            return diff2 > 0 ? 1 : 0;
        }

        @Override // java.util.concurrent.RunnableScheduledFuture
        public boolean isPeriodic() {
            return this.period != 0;
        }

        private void setNextRunTime() {
            long p = this.period;
            if (p > 0) {
                this.time += p;
            } else {
                this.time = ScheduledThreadPoolExecutor.this.triggerTime(-p);
            }
        }

        @Override // java.util.concurrent.FutureTask, java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancelled = super.cancel(mayInterruptIfRunning);
            if (cancelled && ScheduledThreadPoolExecutor.access$100(ScheduledThreadPoolExecutor.this) && this.heapIndex >= 0) {
                ScheduledThreadPoolExecutor.this.remove(this);
            }
            return cancelled;
        }

        @Override // java.util.concurrent.FutureTask, java.util.concurrent.RunnableFuture, java.lang.Runnable
        public void run() {
            boolean periodic = isPeriodic();
            if (!ScheduledThreadPoolExecutor.this.canRunInCurrentRunState(periodic)) {
                cancel(false);
            } else if (!periodic) {
                super.run();
            } else if (super.runAndReset()) {
                setNextRunTime();
                ScheduledThreadPoolExecutor.this.reExecutePeriodic(this.outerTask);
            }
        }
    }

    /* loaded from: ScheduledThreadPoolExecutor$DelayedWorkQueue.class */
    static class DelayedWorkQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {
        private static final int INITIAL_CAPACITY = 16;
        private RunnableScheduledFuture<?>[] queue = new RunnableScheduledFuture[16];
        private final ReentrantLock lock = new ReentrantLock();
        private int size = 0;
        private Thread leader = null;
        private final Condition available = this.lock.newCondition();

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.contains(java.lang.Object):boolean, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(java.lang.Object r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.contains(java.lang.Object):boolean, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.contains(java.lang.Object):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.remove(java.lang.Object):boolean, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(java.lang.Object r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.remove(java.lang.Object):boolean, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.remove(java.lang.Object):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.size():int, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.size():int, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.size():int");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.peek():java.util.concurrent.RunnableScheduledFuture<?>, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.Queue
        public java.util.concurrent.RunnableScheduledFuture<?> peek() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.peek():java.util.concurrent.RunnableScheduledFuture<?>, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.peek():java.util.concurrent.RunnableScheduledFuture");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.offer(java.lang.Runnable):boolean, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.Queue
        public boolean offer(java.lang.Runnable r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.offer(java.lang.Runnable):boolean, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.offer(java.lang.Runnable):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.poll():java.util.concurrent.RunnableScheduledFuture<?>, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.Queue
        public java.util.concurrent.RunnableScheduledFuture<?> poll() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.poll():java.util.concurrent.RunnableScheduledFuture<?>, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.poll():java.util.concurrent.RunnableScheduledFuture");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.take():java.lang.Runnable, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.concurrent.BlockingQueue
        /* renamed from: take */
        public java.lang.Runnable take2() throws java.lang.InterruptedException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.take():java.lang.Runnable, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.take2():java.util.concurrent.RunnableScheduledFuture");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.poll(long, java.util.concurrent.TimeUnit):java.lang.Runnable, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.concurrent.BlockingQueue
        /* renamed from: poll */
        public java.lang.Runnable poll2(long r1, java.util.concurrent.TimeUnit r3) throws java.lang.InterruptedException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.poll(long, java.util.concurrent.TimeUnit):java.lang.Runnable, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.poll2(long, java.util.concurrent.TimeUnit):java.util.concurrent.RunnableScheduledFuture");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.clear():void, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
        public void clear() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.clear():void, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.clear():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.drainTo(java.util.Collection<? super java.lang.Runnable>):int, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.concurrent.BlockingQueue
        public int drainTo(java.util.Collection<? super java.lang.Runnable> r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.drainTo(java.util.Collection<? super java.lang.Runnable>):int, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.drainTo(java.util.Collection):int");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.drainTo(java.util.Collection<? super java.lang.Runnable>, int):int, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.concurrent.BlockingQueue
        public int drainTo(java.util.Collection<? super java.lang.Runnable> r1, int r2) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.drainTo(java.util.Collection<? super java.lang.Runnable>, int):int, file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.drainTo(java.util.Collection, int):int");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.toArray():java.lang.Object[], file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.AbstractCollection, java.util.Collection
        public java.lang.Object[] toArray() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.toArray():java.lang.Object[], file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.toArray():java.lang.Object[]");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.toArray(T[]):T[], file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.toArray(T[]):T[], file: ScheduledThreadPoolExecutor$DelayedWorkQueue.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ScheduledThreadPoolExecutor.DelayedWorkQueue.toArray(java.lang.Object[]):java.lang.Object[]");
        }

        DelayedWorkQueue() {
        }

        private void setIndex(RunnableScheduledFuture<?> f, int idx) {
            if (f instanceof ScheduledFutureTask) {
                ((ScheduledFutureTask) f).heapIndex = idx;
            }
        }

        private void siftUp(int k, RunnableScheduledFuture<?> key) {
            while (k > 0) {
                int parent = (k - 1) >>> 1;
                RunnableScheduledFuture<?> e = this.queue[parent];
                if (key.compareTo(e) >= 0) {
                    break;
                }
                this.queue[k] = e;
                setIndex(e, k);
                k = parent;
            }
            this.queue[k] = key;
            setIndex(key, k);
        }

        private void siftDown(int k, RunnableScheduledFuture<?> key) {
            int half = this.size >>> 1;
            while (k < half) {
                int child = (k << 1) + 1;
                RunnableScheduledFuture<?> c = this.queue[child];
                int right = child + 1;
                if (right < this.size && c.compareTo(this.queue[right]) > 0) {
                    child = right;
                    c = this.queue[right];
                }
                if (key.compareTo(c) <= 0) {
                    break;
                }
                this.queue[k] = c;
                setIndex(c, k);
                k = child;
            }
            this.queue[k] = key;
            setIndex(key, k);
        }

        private void grow() {
            int oldCapacity = this.queue.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            }
            this.queue = (RunnableScheduledFuture[]) Arrays.copyOf(this.queue, newCapacity);
        }

        private int indexOf(Object x) {
            if (x != null) {
                if (x instanceof ScheduledFutureTask) {
                    int i = ((ScheduledFutureTask) x).heapIndex;
                    if (i >= 0 && i < this.size && this.queue[i] == x) {
                        return i;
                    }
                    return -1;
                }
                for (int i2 = 0; i2 < this.size; i2++) {
                    if (x.equals(this.queue[i2])) {
                        return i2;
                    }
                }
                return -1;
            }
            return -1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override // java.util.concurrent.BlockingQueue
        public int remainingCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override // java.util.concurrent.BlockingQueue
        public void put(Runnable e) {
            offer(e);
        }

        @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
        public boolean add(Runnable e) {
            return offer(e);
        }

        @Override // java.util.concurrent.BlockingQueue
        public boolean offer(Runnable e, long timeout, TimeUnit unit) {
            return offer(e);
        }

        private RunnableScheduledFuture<?> finishPoll(RunnableScheduledFuture<?> f) {
            int s = this.size - 1;
            this.size = s;
            RunnableScheduledFuture<?> x = this.queue[s];
            this.queue[s] = null;
            if (s != 0) {
                siftDown(0, x);
            }
            setIndex(f, -1);
            return f;
        }

        private RunnableScheduledFuture<?> peekExpired() {
            RunnableScheduledFuture<?> first = this.queue[0];
            if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
                return null;
            }
            return first;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Runnable> iterator() {
            return new Itr((RunnableScheduledFuture[]) Arrays.copyOf(this.queue, this.size));
        }

        /* loaded from: ScheduledThreadPoolExecutor$DelayedWorkQueue$Itr.class */
        private class Itr implements Iterator<Runnable> {
            final RunnableScheduledFuture[] array;
            int cursor = 0;
            int lastRet = -1;

            Itr(RunnableScheduledFuture[] array) {
                this.array = array;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.cursor < this.array.length;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public Runnable next() {
                if (this.cursor >= this.array.length) {
                    throw new NoSuchElementException();
                }
                this.lastRet = this.cursor;
                RunnableScheduledFuture[] runnableScheduledFutureArr = this.array;
                int i = this.cursor;
                this.cursor = i + 1;
                return runnableScheduledFutureArr[i];
            }

            @Override // java.util.Iterator
            public void remove() {
                if (this.lastRet < 0) {
                    throw new IllegalStateException();
                }
                DelayedWorkQueue.this.remove(this.array[this.lastRet]);
                this.lastRet = -1;
            }
        }
    }
}