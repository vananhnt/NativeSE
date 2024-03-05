package java.util.concurrent;

import java.lang.Thread;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import javax.sip.header.SubscriptionStateHeader;
import sun.misc.Unsafe;

/* loaded from: ForkJoinPool.class */
public class ForkJoinPool extends AbstractExecutorService {
    public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
    static final ThreadLocal<Submitter> submitters;
    private static final RuntimePermission modifyThreadPermission;
    static final ForkJoinPool commonPool;
    static final int commonPoolParallelism;
    private static int poolNumberSequence;
    private static final long IDLE_TIMEOUT = 2000000000;
    private static final long FAST_IDLE_TIMEOUT = 200000000;
    private static final long TIMEOUT_SLOP = 2000000;
    private static final int MAX_HELP = 64;
    private static final int SEED_INCREMENT = 1640531527;
    private static final int AC_SHIFT = 48;
    private static final int TC_SHIFT = 32;
    private static final int ST_SHIFT = 31;
    private static final int EC_SHIFT = 16;
    private static final int SMASK = 65535;
    private static final int MAX_CAP = 32767;
    private static final int EVENMASK = 65534;
    private static final int SQMASK = 126;
    private static final int SHORT_SIGN = 32768;
    private static final int INT_SIGN = Integer.MIN_VALUE;
    private static final long STOP_BIT = 2147483648L;
    private static final long AC_MASK = -281474976710656L;
    private static final long TC_MASK = 281470681743360L;
    private static final long TC_UNIT = 4294967296L;
    private static final long AC_UNIT = 281474976710656L;
    private static final int UAC_SHIFT = 16;
    private static final int UTC_SHIFT = 0;
    private static final int UAC_MASK = -65536;
    private static final int UTC_MASK = 65535;
    private static final int UAC_UNIT = 65536;
    private static final int UTC_UNIT = 1;
    private static final int E_MASK = Integer.MAX_VALUE;
    private static final int E_SEQ = 65536;
    private static final int SHUTDOWN = Integer.MIN_VALUE;
    private static final int PL_LOCK = 2;
    private static final int PL_SIGNAL = 1;
    private static final int PL_SPINS = 256;
    static final int LIFO_QUEUE = 0;
    static final int FIFO_QUEUE = 1;
    static final int SHARED_QUEUE = -1;
    private static final int MIN_SCAN = 511;
    private static final int MAX_SCAN = 131071;
    volatile long pad00;
    volatile long pad01;
    volatile long pad02;
    volatile long pad03;
    volatile long pad04;
    volatile long pad05;
    volatile long pad06;
    volatile long stealCount;
    volatile long ctl;
    volatile int plock;
    volatile int indexSeed;
    final int config;
    WorkQueue[] workQueues;
    final ForkJoinWorkerThreadFactory factory;
    final Thread.UncaughtExceptionHandler ueh;
    final String workerNamePrefix;
    volatile Object pad10;
    volatile Object pad11;
    volatile Object pad12;
    volatile Object pad13;
    volatile Object pad14;
    volatile Object pad15;
    volatile Object pad16;
    volatile Object pad17;
    volatile Object pad18;
    volatile Object pad19;
    volatile Object pad1a;
    volatile Object pad1b;
    private static final Unsafe U;
    private static final long CTL;
    private static final long PARKBLOCKER;
    private static final int ABASE;
    private static final int ASHIFT;
    private static final long STEALCOUNT;
    private static final long PLOCK;
    private static final long INDEXSEED;
    private static final long QLOCK;

    /* loaded from: ForkJoinPool$ForkJoinWorkerThreadFactory.class */
    public interface ForkJoinWorkerThreadFactory {
        ForkJoinWorkerThread newThread(ForkJoinPool forkJoinPool);
    }

    /* loaded from: ForkJoinPool$ManagedBlocker.class */
    public interface ManagedBlocker {
        boolean block() throws InterruptedException;

        boolean isReleasable();
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.registerWorker(java.util.concurrent.ForkJoinWorkerThread):java.util.concurrent.ForkJoinPool$WorkQueue, file: ForkJoinPool.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    final java.util.concurrent.ForkJoinPool.WorkQueue registerWorker(java.util.concurrent.ForkJoinWorkerThread r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.registerWorker(java.util.concurrent.ForkJoinWorkerThread):java.util.concurrent.ForkJoinPool$WorkQueue, file: ForkJoinPool.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.registerWorker(java.util.concurrent.ForkJoinWorkerThread):java.util.concurrent.ForkJoinPool$WorkQueue");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.deregisterWorker(java.util.concurrent.ForkJoinWorkerThread, java.lang.Throwable):void, file: ForkJoinPool.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    final void deregisterWorker(java.util.concurrent.ForkJoinWorkerThread r1, java.lang.Throwable r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.deregisterWorker(java.util.concurrent.ForkJoinWorkerThread, java.lang.Throwable):void, file: ForkJoinPool.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.deregisterWorker(java.util.concurrent.ForkJoinWorkerThread, java.lang.Throwable):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.fullExternalPush(java.util.concurrent.ForkJoinTask<?>):void, file: ForkJoinPool.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void fullExternalPush(java.util.concurrent.ForkJoinTask<?> r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.fullExternalPush(java.util.concurrent.ForkJoinTask<?>):void, file: ForkJoinPool.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.fullExternalPush(java.util.concurrent.ForkJoinTask):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>):java.util.List<java.util.concurrent.Future<T>>, file: ForkJoinPool.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public <T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>> r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>>):java.util.List<java.util.concurrent.Future<T>>, file: ForkJoinPool.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.invokeAll(java.util.Collection):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.managedBlock(java.util.concurrent.ForkJoinPool$ManagedBlocker):void, file: ForkJoinPool.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public static void managedBlock(java.util.concurrent.ForkJoinPool.ManagedBlocker r0) throws java.lang.InterruptedException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ForkJoinPool.managedBlock(java.util.concurrent.ForkJoinPool$ManagedBlocker):void, file: ForkJoinPool.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.managedBlock(java.util.concurrent.ForkJoinPool$ManagedBlocker):void");
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public /* bridge */ /* synthetic */ Future submit(Runnable x0, Object x1) {
        return submit(x0, (Runnable) x1);
    }

    private static void checkPermission() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(modifyThreadPermission);
        }
    }

    /* loaded from: ForkJoinPool$DefaultForkJoinWorkerThreadFactory.class */
    static final class DefaultForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
        DefaultForkJoinWorkerThreadFactory() {
        }

        @Override // java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory
        public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ForkJoinWorkerThread(pool);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinPool$Submitter.class */
    public static final class Submitter {
        int seed;

        Submitter(int s) {
            this.seed = s;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinPool$EmptyTask.class */
    public static final class EmptyTask extends ForkJoinTask<Void> {
        private static final long serialVersionUID = -7721805057305804111L;

        EmptyTask() {
            this.status = -268435456;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.ForkJoinTask
        public final Void getRawResult() {
            return null;
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final void setRawResult(Void x) {
        }

        @Override // java.util.concurrent.ForkJoinTask
        public final boolean exec() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ForkJoinPool$WorkQueue.class */
    public static final class WorkQueue {
        static final int INITIAL_QUEUE_CAPACITY = 8192;
        static final int MAXIMUM_QUEUE_CAPACITY = 67108864;
        volatile long pad00;
        volatile long pad01;
        volatile long pad02;
        volatile long pad03;
        volatile long pad04;
        volatile long pad05;
        volatile long pad06;
        int seed;
        volatile int eventCount;
        int nextWait;
        int hint;
        int poolIndex;
        final int mode;
        int nsteals;
        volatile int qlock;
        ForkJoinTask<?>[] array;
        final ForkJoinPool pool;
        final ForkJoinWorkerThread owner;
        volatile Thread parker;
        volatile ForkJoinTask<?> currentJoin;
        ForkJoinTask<?> currentSteal;
        volatile Object pad10;
        volatile Object pad11;
        volatile Object pad12;
        volatile Object pad13;
        volatile Object pad14;
        volatile Object pad15;
        volatile Object pad16;
        volatile Object pad17;
        volatile Object pad18;
        volatile Object pad19;
        volatile Object pad1a;
        volatile Object pad1b;
        volatile Object pad1c;
        volatile Object pad1d;
        private static final Unsafe U;
        private static final long QLOCK;
        private static final int ABASE;
        private static final int ASHIFT;
        int top = 4096;
        volatile int base = 4096;

        WorkQueue(ForkJoinPool pool, ForkJoinWorkerThread owner, int mode, int seed) {
            this.pool = pool;
            this.owner = owner;
            this.mode = mode;
            this.seed = seed;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final int queueSize() {
            int n = this.base - this.top;
            if (n >= 0) {
                return 0;
            }
            return -n;
        }

        final boolean isEmpty() {
            ForkJoinTask<?>[] a;
            int m;
            int i = this.base;
            int s = this.top;
            int n = i - s;
            return n >= 0 || (n == -1 && ((a = this.array) == null || (m = a.length - 1) < 0 || U.getObject(a, ((long) ((m & (s - 1)) << ASHIFT)) + ((long) ABASE)) == null));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void push(ForkJoinTask<?> task) {
            int s = this.top;
            ForkJoinTask<?>[] a = this.array;
            if (a != null) {
                int m = a.length - 1;
                int j = ((m & s) << ASHIFT) + ABASE;
                U.putOrderedObject(a, j, task);
                int i = s + 1;
                this.top = i;
                int n = i - this.base;
                if (n <= 2) {
                    ForkJoinPool p = this.pool;
                    if (p != null) {
                        p.signalWork(this);
                    }
                } else if (n >= m) {
                    growArray();
                }
            }
        }

        final ForkJoinTask<?>[] growArray() {
            int oldMask;
            ForkJoinTask<?>[] oldA = this.array;
            int size = oldA != null ? oldA.length << 1 : 8192;
            if (size > 67108864) {
                throw new RejectedExecutionException("Queue capacity exceeded");
            }
            ForkJoinTask<?>[] a = new ForkJoinTask[size];
            this.array = a;
            if (oldA != null && (oldMask = oldA.length - 1) >= 0) {
                int t = this.top;
                int i = this.base;
                int b = i;
                if (t - i > 0) {
                    int mask = size - 1;
                    do {
                        int oldj = ((b & oldMask) << ASHIFT) + ABASE;
                        int j = ((b & mask) << ASHIFT) + ABASE;
                        ForkJoinTask<?> x = (ForkJoinTask) U.getObjectVolatile(oldA, oldj);
                        if (x != null && U.compareAndSwapObject(oldA, oldj, x, null)) {
                            U.putObjectVolatile(a, j, x);
                        }
                        b++;
                    } while (b != t);
                }
            }
            return a;
        }

        final ForkJoinTask<?> pop() {
            int m;
            int s;
            long j;
            ForkJoinTask<?> t;
            ForkJoinTask<?>[] a = this.array;
            if (a != null && (m = a.length - 1) >= 0) {
                do {
                    s = this.top - 1;
                    if (s - this.base >= 0) {
                        j = ((m & s) << ASHIFT) + ABASE;
                        t = (ForkJoinTask) U.getObject(a, j);
                        if (t == null) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } while (!U.compareAndSwapObject(a, j, t, null));
                this.top = s;
                return t;
            }
            return null;
        }

        final ForkJoinTask<?> pollAt(int b) {
            ForkJoinTask<?>[] a = this.array;
            if (a != null) {
                int j = (((a.length - 1) & b) << ASHIFT) + ABASE;
                ForkJoinTask<?> t = (ForkJoinTask) U.getObjectVolatile(a, j);
                if (t != null && this.base == b && U.compareAndSwapObject(a, j, t, null)) {
                    this.base = b + 1;
                    return t;
                }
                return null;
            }
            return null;
        }

        final ForkJoinTask<?> poll() {
            ForkJoinTask<?>[] a;
            while (true) {
                int b = this.base;
                if (b - this.top < 0 && (a = this.array) != null) {
                    int j = (((a.length - 1) & b) << ASHIFT) + ABASE;
                    ForkJoinTask<?> t = (ForkJoinTask) U.getObjectVolatile(a, j);
                    if (t != null) {
                        if (this.base == b && U.compareAndSwapObject(a, j, t, null)) {
                            this.base = b + 1;
                            return t;
                        }
                    } else if (this.base != b) {
                        continue;
                    } else if (b + 1 != this.top) {
                        Thread.yield();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final ForkJoinTask<?> nextLocalTask() {
            return this.mode == 0 ? pop() : poll();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final ForkJoinTask<?> peek() {
            int m;
            ForkJoinTask<?>[] a = this.array;
            if (a == null || (m = a.length - 1) < 0) {
                return null;
            }
            int i = this.mode == 0 ? this.top - 1 : this.base;
            int j = ((i & m) << ASHIFT) + ABASE;
            return (ForkJoinTask) U.getObjectVolatile(a, j);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final boolean tryUnpush(ForkJoinTask<?> t) {
            int s;
            ForkJoinTask<?>[] a = this.array;
            if (a == null || (s = this.top) == this.base) {
                return false;
            }
            int s2 = s - 1;
            if (U.compareAndSwapObject(a, (((a.length - 1) & s2) << ASHIFT) + ABASE, t, null)) {
                this.top = s2;
                return true;
            }
            return false;
        }

        final void cancelAll() {
            ForkJoinTask.cancelIgnoringExceptions(this.currentJoin);
            ForkJoinTask.cancelIgnoringExceptions(this.currentSteal);
            while (true) {
                ForkJoinTask<?> t = poll();
                if (t != null) {
                    ForkJoinTask.cancelIgnoringExceptions(t);
                } else {
                    return;
                }
            }
        }

        final int nextSeed() {
            int r = this.seed;
            int r2 = r ^ (r << 13);
            int r3 = r2 ^ (r2 >>> 17);
            int i = r3 ^ (r3 << 5);
            this.seed = i;
            return i;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v9, types: [sun.misc.Unsafe, long] */
        private void popAndExecAll() {
            int m;
            while (true) {
                ForkJoinTask<?>[] a = this.array;
                if (a == null || (m = a.length - 1) < 0) {
                    return;
                }
                int s = this.top - 1;
                if (s - this.base < 0) {
                    return;
                }
                ?? r0 = U;
                ForkJoinTask<?> t = (ForkJoinTask) r0.getObject(a, ((m & s) << ASHIFT) + ABASE);
                if (t != null) {
                    if (U.compareAndSwapObject(a, r0, t, null)) {
                        this.top = s;
                        t.doExec();
                    }
                } else {
                    return;
                }
            }
        }

        private void pollAndExecAll() {
            while (true) {
                ForkJoinTask<?> t = poll();
                if (t != null) {
                    t.doExec();
                } else {
                    return;
                }
            }
        }

        final boolean tryRemoveAndExec(ForkJoinTask<?> task) {
            int m;
            boolean stat = true;
            boolean removed = false;
            boolean empty = true;
            ForkJoinTask<?>[] a = this.array;
            if (a != null && (m = a.length - 1) >= 0) {
                int i = this.top;
                int s = i;
                int b = this.base;
                int i2 = i - b;
                int n = i2;
                if (i2 > 0) {
                    while (true) {
                        s--;
                        int j = ((s & m) << ASHIFT) + ABASE;
                        ForkJoinTask<?> t = (ForkJoinTask) U.getObjectVolatile(a, j);
                        if (t == null) {
                            break;
                        } else if (t == task) {
                            if (s + 1 == this.top) {
                                if (U.compareAndSwapObject(a, j, task, null)) {
                                    this.top = s;
                                    removed = true;
                                }
                            } else if (this.base == b) {
                                removed = U.compareAndSwapObject(a, j, task, new EmptyTask());
                            }
                        } else {
                            if (t.status >= 0) {
                                empty = false;
                            } else if (s + 1 == this.top) {
                                if (U.compareAndSwapObject(a, j, t, null)) {
                                    this.top = s;
                                }
                            }
                            n--;
                            if (n == 0) {
                                if (!empty && this.base == b) {
                                    stat = false;
                                }
                            }
                        }
                    }
                }
            }
            if (removed) {
                task.doExec();
            }
            return stat;
        }

        final boolean pollAndExecCC(ForkJoinTask<?> root) {
            ForkJoinTask<?>[] a;
            long j;
            Object o;
            while (true) {
                int b = this.base;
                if (b - this.top < 0 && (a = this.array) != null && (o = U.getObject(a, (((a.length - 1) & b) << ASHIFT) + ABASE)) != null && (o instanceof CountedCompleter)) {
                    CountedCompleter<?> t = (CountedCompleter) o;
                    CountedCompleter<?> r = t;
                    while (r != root) {
                        CountedCompleter<?> countedCompleter = r.completer;
                        r = countedCompleter;
                        if (countedCompleter == null) {
                            return false;
                        }
                    }
                    if (this.base == b && U.compareAndSwapObject(a, j, t, null)) {
                        this.base = b + 1;
                        t.doExec();
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

        final void runTask(ForkJoinTask<?> t) {
            if (t != null) {
                this.currentSteal = t;
                t.doExec();
                this.currentSteal = null;
                this.nsteals++;
                if (this.base - this.top < 0) {
                    if (this.mode == 0) {
                        popAndExecAll();
                    } else {
                        pollAndExecAll();
                    }
                }
            }
        }

        final void runSubtask(ForkJoinTask<?> t) {
            if (t != null) {
                ForkJoinTask<?> ps = this.currentSteal;
                this.currentSteal = t;
                t.doExec();
                this.currentSteal = ps;
            }
        }

        final boolean isApparentlyUnblocked() {
            Thread wt;
            Thread.State s;
            return (this.eventCount < 0 || (wt = this.owner) == null || (s = wt.getState()) == Thread.State.BLOCKED || s == Thread.State.WAITING || s == Thread.State.TIMED_WAITING) ? false : true;
        }

        static {
            try {
                U = Unsafe.getUnsafe();
                QLOCK = U.objectFieldOffset(WorkQueue.class.getDeclaredField("qlock"));
                ABASE = U.arrayBaseOffset(ForkJoinTask[].class);
                int scale = U.arrayIndexScale(ForkJoinTask[].class);
                if ((scale & (scale - 1)) != 0) {
                    throw new Error("data type scale not a power of two");
                }
                ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private static final synchronized int nextPoolId() {
        int i = poolNumberSequence + 1;
        poolNumberSequence = i;
        return i;
    }

    private int acquirePlock() {
        WorkQueue w;
        int spins = 256;
        int r = 0;
        while (true) {
            int ps = this.plock;
            if ((ps & 2) == 0) {
                int nps = ps + 2;
                if (U.compareAndSwapInt(this, PLOCK, ps, nps)) {
                    return nps;
                }
            }
            if (r == 0) {
                Thread t = Thread.currentThread();
                if ((t instanceof ForkJoinWorkerThread) && (w = ((ForkJoinWorkerThread) t).workQueue) != null) {
                    r = w.seed;
                } else {
                    Submitter z = submitters.get();
                    if (z != null) {
                        r = z.seed;
                    } else {
                        r = 1;
                    }
                }
            } else if (spins >= 0) {
                int r2 = r ^ (r << 1);
                int r3 = r2 ^ (r2 >>> 3);
                r = r3 ^ (r3 << 10);
                if (r >= 0) {
                    spins--;
                }
            } else if (U.compareAndSwapInt(this, PLOCK, ps, ps | 1)) {
                synchronized (this) {
                    if ((this.plock & 1) != 0) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            try {
                                Thread.currentThread().interrupt();
                            } catch (SecurityException e2) {
                            }
                        }
                    } else {
                        notifyAll();
                    }
                }
            } else {
                continue;
            }
        }
    }

    private void releasePlock(int ps) {
        this.plock = ps;
        synchronized (this) {
            notifyAll();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0083, code lost:
        if (java.util.concurrent.ForkJoinPool.U.compareAndSwapInt(r7, java.util.concurrent.ForkJoinPool.PLOCK, r0, r10) == false) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void initWorkers() {
        /*
            Method dump skipped, instructions count: 202
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.initWorkers():void");
    }

    private void tryAddWorker() {
        long c;
        long nc;
        do {
            c = this.ctl;
            int u = (int) (c >>> 32);
            if (u < 0 && (u & 32768) != 0 && ((int) c) == 0) {
                nc = (((u + 1) & 65535) | ((u + 65536) & (-65536))) << 32;
            } else {
                return;
            }
        } while (!U.compareAndSwapLong(this, CTL, c, nc));
        Throwable ex = null;
        ForkJoinWorkerThread wt = null;
        try {
            ForkJoinWorkerThreadFactory fac = this.factory;
            if (fac != null) {
                ForkJoinWorkerThread newThread = fac.newThread(this);
                wt = newThread;
                if (newThread != null) {
                    wt.start();
                    return;
                }
            }
        } catch (Throwable e) {
            ex = e;
        }
        deregisterWorker(wt, ex);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void externalPush(ForkJoinTask<?> task) {
        WorkQueue[] ws;
        int m;
        WorkQueue q;
        int an;
        int n;
        Submitter z = submitters.get();
        if (z != null && this.plock > 0 && (ws = this.workQueues) != null && (m = ws.length - 1) >= 0 && (q = ws[m & z.seed & 126]) != null && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
            int b = q.base;
            int s = q.top;
            ForkJoinTask<?>[] a = q.array;
            if (a != null && (an = a.length) > (n = (s + 1) - b)) {
                int j = (((an - 1) & s) << ASHIFT) + ABASE;
                U.putOrderedObject(a, j, task);
                q.top = s + 1;
                q.qlock = 0;
                if (n <= 2) {
                    signalWork(q);
                    return;
                }
                return;
            }
            q.qlock = 0;
        }
        fullExternalPush(task);
    }

    final void incrementActiveCount() {
        Unsafe unsafe;
        long j;
        long c;
        do {
            unsafe = U;
            j = CTL;
            c = this.ctl;
        } while (!unsafe.compareAndSwapLong(this, j, c, c + AC_UNIT));
    }

    final void signalWork(WorkQueue q) {
        int i;
        WorkQueue w;
        int hint = q.poolIndex;
        do {
            long c = this.ctl;
            int u = (int) (c >>> 32);
            if (u < 0) {
                int e = (int) c;
                if (e > 0) {
                    WorkQueue[] ws = this.workQueues;
                    if (ws != null && ws.length > (i = e & 65535) && (w = ws[i]) != null && w.eventCount == (e | Integer.MIN_VALUE)) {
                        long nc = (w.nextWait & Integer.MAX_VALUE) | ((u + 65536) << 32);
                        if (U.compareAndSwapLong(this, CTL, c, nc)) {
                            w.hint = hint;
                            w.eventCount = (e + 65536) & Integer.MAX_VALUE;
                            Thread p = w.parker;
                            if (p != null) {
                                U.unpark(p);
                                return;
                            }
                            return;
                        }
                    } else {
                        return;
                    }
                } else if (((short) u) < 0) {
                    tryAddWorker();
                    return;
                } else {
                    return;
                }
            } else {
                return;
            }
        } while (q.top - q.base > 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void runWorker(WorkQueue w) {
        w.growArray();
        do {
            w.runTask(scan(w));
        } while (w.qlock >= 0);
    }

    /* JADX WARN: Type inference failed for: r0v130, types: [sun.misc.Unsafe, long] */
    private final ForkJoinTask<?> scan(WorkQueue w) {
        WorkQueue[] ws;
        int m;
        WorkQueue q;
        int e;
        int i;
        WorkQueue v;
        ForkJoinTask<?>[] a;
        int ps = this.plock;
        if (w != null && (ws = this.workQueues) != null && (m = ws.length - 1) >= 0) {
            int ec = w.eventCount;
            int r = w.seed;
            int r2 = r ^ (r << 13);
            int r3 = r2 ^ (r2 >>> 17);
            int r4 = r3 ^ (r3 << 5);
            w.seed = r4;
            w.hint = -1;
            int j = ((m + m + 1) | 511) & MAX_SCAN;
            while (true) {
                WorkQueue q2 = ws[(r4 + j) & m];
                if (q2 != null) {
                    int b = q2.base;
                    if (b - q2.top < 0 && (a = q2.array) != null) {
                        int i2 = (((a.length - 1) & b) << ASHIFT) + ABASE;
                        ForkJoinTask<?> t = (ForkJoinTask) U.getObjectVolatile(a, i2);
                        if (q2.base == b && ec >= 0 && t != null && U.compareAndSwapObject(a, i2, t, null)) {
                            int i3 = b + 1;
                            q2.base = i3;
                            if (i3 - q2.top < 0) {
                                signalWork(q2);
                            }
                            return t;
                        } else if ((ec < 0 || j < m) && ((int) (this.ctl >> 48)) <= 0) {
                            w.hint = (r4 + j) & m;
                            break;
                        }
                    }
                }
                j--;
                if (j < 0) {
                    break;
                }
            }
            int ns = w.nsteals;
            if (ns != 0) {
                ?? r0 = U;
                long j2 = STEALCOUNT;
                long sc = this.stealCount;
                if (r0.compareAndSwapLong(this, r0, sc, sc + ns)) {
                    w.nsteals = 0;
                    return null;
                }
                return null;
            } else if (this.plock == ps) {
                long c = this.ctl;
                int e2 = (int) c;
                if (e2 < 0) {
                    w.qlock = -1;
                    return null;
                }
                int i4 = w.hint;
                int h = i4;
                if (i4 < 0) {
                    if (ec >= 0) {
                        long nc = ec | ((c - AC_UNIT) & (-4294967296L));
                        w.nextWait = e2;
                        w.eventCount = ec | Integer.MIN_VALUE;
                        if (this.ctl != c || !U.compareAndSwapLong(this, CTL, c, nc)) {
                            w.eventCount = ec;
                        } else if (((int) (c >> 48)) == 1 - (this.config & 65535)) {
                            idleAwaitWork(w, nc, c);
                        }
                    } else if (w.eventCount < 0 && !tryTerminate(false, false) && this.ctl == c) {
                        Thread wt = Thread.currentThread();
                        Thread.interrupted();
                        U.putObject(wt, PARKBLOCKER, this);
                        w.parker = wt;
                        if (w.eventCount < 0) {
                            U.park(false, 0L);
                        }
                        w.parker = null;
                        U.putObject(wt, PARKBLOCKER, null);
                    }
                }
                if (h < 0) {
                    int i5 = w.hint;
                    h = i5;
                    if (i5 < 0) {
                        return null;
                    }
                }
                WorkQueue[] ws2 = this.workQueues;
                if (ws2 != null && h < ws2.length && (q = ws2[h]) != null) {
                    int n = (this.config & 65535) >>> 1;
                    do {
                        int idleCount = w.eventCount < 0 ? 0 : -1;
                        int s = (idleCount - q.base) + q.top;
                        if (s <= n) {
                            n = s;
                            if (s <= 0) {
                                return null;
                            }
                        }
                        long c2 = this.ctl;
                        int u = (int) (c2 >>> 32);
                        if (u < 0 && (e = (int) c2) > 0 && m >= (i = e & 65535) && (v = ws2[i]) != null) {
                            long nc2 = (v.nextWait & Integer.MAX_VALUE) | ((u + 65536) << 32);
                            if (v.eventCount == (e | Integer.MIN_VALUE) && U.compareAndSwapLong(this, CTL, c2, nc2)) {
                                v.hint = h;
                                v.eventCount = (e + 65536) & Integer.MAX_VALUE;
                                Thread p = v.parker;
                                if (p != null) {
                                    U.unpark(p);
                                }
                                n--;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } while (n > 0);
                    return null;
                }
                return null;
            } else {
                return null;
            }
        }
        return null;
    }

    private void idleAwaitWork(WorkQueue w, long currentCtl, long prevCtl) {
        if (w != null && w.eventCount < 0 && !tryTerminate(false, false) && ((int) prevCtl) != 0) {
            int dc = -((short) (currentCtl >>> 32));
            long parkTime = dc < 0 ? FAST_IDLE_TIMEOUT : (dc + 1) * IDLE_TIMEOUT;
            long deadline = (System.nanoTime() + parkTime) - TIMEOUT_SLOP;
            Thread wt = Thread.currentThread();
            while (this.ctl == currentCtl) {
                Thread.interrupted();
                U.putObject(wt, PARKBLOCKER, this);
                w.parker = wt;
                if (this.ctl == currentCtl) {
                    U.park(false, parkTime);
                }
                w.parker = null;
                U.putObject(wt, PARKBLOCKER, null);
                if (this.ctl == currentCtl) {
                    if (deadline - System.nanoTime() <= 0 && U.compareAndSwapLong(this, CTL, currentCtl, prevCtl)) {
                        w.eventCount = (w.eventCount + 65536) | Integer.MAX_VALUE;
                        w.qlock = -1;
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x0116, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void helpSignal(java.util.concurrent.ForkJoinTask<?> r10, int r11) {
        /*
            Method dump skipped, instructions count: 285
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.helpSignal(java.util.concurrent.ForkJoinTask, int):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:87:0x000d, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int tryHelpStealer(java.util.concurrent.ForkJoinPool.WorkQueue r8, java.util.concurrent.ForkJoinTask<?> r9) {
        /*
            Method dump skipped, instructions count: 431
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.tryHelpStealer(java.util.concurrent.ForkJoinPool$WorkQueue, java.util.concurrent.ForkJoinTask):int");
    }

    private int helpComplete(ForkJoinTask<?> task, int mode) {
        WorkQueue[] ws;
        int m;
        int u;
        if (task != null && (ws = this.workQueues) != null && (m = ws.length - 1) >= 0) {
            int j = 1;
            int origin = 1;
            while (true) {
                int s = task.status;
                if (s < 0) {
                    return s;
                }
                WorkQueue q = ws[j & m];
                if (q != null && q.pollAndExecCC(task)) {
                    origin = j;
                    if (mode == -1 && ((u = (int) (this.ctl >>> 32)) >= 0 || (u >> 16) >= 0)) {
                        return 0;
                    }
                } else {
                    int i = (j + 2) & m;
                    j = i;
                    if (i == origin) {
                        return 0;
                    }
                }
            }
        } else {
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean tryCompensate() {
        long c;
        int e;
        int i;
        WorkQueue w;
        int pc = this.config & 65535;
        WorkQueue[] ws = this.workQueues;
        if (ws != null && (e = (int) (c = this.ctl)) >= 0) {
            if (e != 0 && (i = e & 65535) < ws.length && (w = ws[i]) != null && w.eventCount == (e | Integer.MIN_VALUE)) {
                long nc = (w.nextWait & Integer.MAX_VALUE) | (c & (-4294967296L));
                if (U.compareAndSwapLong(this, CTL, c, nc)) {
                    w.eventCount = (e + 65536) & Integer.MAX_VALUE;
                    Thread p = w.parker;
                    if (p != null) {
                        U.unpark(p);
                        return true;
                    }
                    return true;
                }
                return false;
            }
            int tc = (short) (c >>> 32);
            if (tc >= 0 && ((int) (c >> 48)) + pc > 1) {
                long nc2 = ((c - AC_UNIT) & AC_MASK) | (c & 281474976710655L);
                if (U.compareAndSwapLong(this, CTL, c, nc2)) {
                    return true;
                }
                return false;
            } else if (tc + pc < 32767) {
                long nc3 = ((c + TC_UNIT) & TC_MASK) | (c & (-281470681743361L));
                if (U.compareAndSwapLong(this, CTL, c, nc3)) {
                    Throwable ex = null;
                    ForkJoinWorkerThread wt = null;
                    try {
                        ForkJoinWorkerThreadFactory fac = this.factory;
                        if (fac != null) {
                            ForkJoinWorkerThread newThread = fac.newThread(this);
                            wt = newThread;
                            if (newThread != null) {
                                wt.start();
                                return true;
                            }
                        }
                    } catch (Throwable rex) {
                        ex = rex;
                    }
                    deregisterWorker(wt, ex);
                    return false;
                }
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v27, types: [sun.misc.Unsafe, long] */
    public final int awaitJoin(WorkQueue joiner, ForkJoinTask<?> task) {
        ?? r0;
        long c;
        int s = 0;
        if (joiner != null && task != null) {
            int i = task.status;
            s = i;
            if (i >= 0) {
                ForkJoinTask<?> prevJoin = joiner.currentJoin;
                joiner.currentJoin = task;
                do {
                    int i2 = task.status;
                    s = i2;
                    if (i2 < 0 || joiner.isEmpty()) {
                        break;
                    }
                } while (joiner.tryRemoveAndExec(task));
                if (s >= 0) {
                    int i3 = task.status;
                    s = i3;
                    if (i3 >= 0) {
                        helpSignal(task, joiner.poolIndex);
                        int i4 = task.status;
                        s = i4;
                        if (i4 >= 0 && (task instanceof CountedCompleter)) {
                            s = helpComplete(task, 0);
                        }
                    }
                }
                while (s >= 0) {
                    int i5 = task.status;
                    s = i5;
                    if (i5 < 0) {
                        break;
                    }
                    if (joiner.isEmpty()) {
                        int tryHelpStealer = tryHelpStealer(joiner, task);
                        s = tryHelpStealer;
                        if (tryHelpStealer != 0) {
                            continue;
                        }
                    }
                    int i6 = task.status;
                    s = i6;
                    if (i6 >= 0) {
                        helpSignal(task, joiner.poolIndex);
                        int i7 = task.status;
                        s = i7;
                        if (i7 >= 0 && tryCompensate()) {
                            if (task.trySetSignal()) {
                                int i8 = task.status;
                                s = i8;
                                if (i8 >= 0) {
                                    synchronized (task) {
                                        if (task.status >= 0) {
                                            try {
                                                task.wait();
                                            } catch (InterruptedException e) {
                                            }
                                        } else {
                                            task.notifyAll();
                                        }
                                    }
                                }
                            }
                            do {
                                r0 = U;
                                long j = CTL;
                                c = this.ctl;
                            } while (!r0.compareAndSwapLong(this, r0, c, c + AC_UNIT));
                        }
                    } else {
                        continue;
                    }
                }
                joiner.currentJoin = prevJoin;
            }
        }
        return s;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void helpJoinOnce(WorkQueue joiner, ForkJoinTask<?> task) {
        int s;
        if (joiner != null && task != null && task.status >= 0) {
            ForkJoinTask<?> prevJoin = joiner.currentJoin;
            joiner.currentJoin = task;
            do {
                int i = task.status;
                s = i;
                if (i < 0 || joiner.isEmpty()) {
                    break;
                }
            } while (joiner.tryRemoveAndExec(task));
            if (s >= 0) {
                int i2 = task.status;
                s = i2;
                if (i2 >= 0) {
                    helpSignal(task, joiner.poolIndex);
                    int i3 = task.status;
                    s = i3;
                    if (i3 >= 0 && (task instanceof CountedCompleter)) {
                        s = helpComplete(task, 0);
                    }
                }
            }
            if (s >= 0 && joiner.isEmpty()) {
                while (task.status >= 0 && tryHelpStealer(joiner, task) > 0) {
                }
            }
            joiner.currentJoin = prevJoin;
        }
    }

    private WorkQueue findNonEmptyStealQueue(int r) {
        int ps;
        int m;
        int n;
        do {
            ps = this.plock;
            WorkQueue[] ws = this.workQueues;
            if (ws == null || (m = ws.length - 1) < 1) {
                return null;
            }
            int j = (m + 1) << 2;
            do {
                WorkQueue q = ws[(((r + j) << 1) | 1) & m];
                if (q != null && (n = q.base - q.top) < 0) {
                    if (n < -1) {
                        signalWork(q);
                    }
                    return q;
                }
                j--;
            } while (j >= 0);
        } while (this.plock != ps);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v29, types: [sun.misc.Unsafe, java.lang.Object] */
    public final void helpQuiescePool(WorkQueue w) {
        int b;
        ForkJoinTask<?> t;
        Unsafe unsafe;
        long j;
        long c;
        long c2;
        Unsafe unsafe2;
        long j2;
        long c3;
        ?? r0;
        long j3;
        long c4;
        boolean active = true;
        while (true) {
            ForkJoinTask<?> localTask = w.nextLocalTask();
            if (localTask != null) {
                localTask.doExec();
            } else {
                WorkQueue q = findNonEmptyStealQueue(w.nextSeed());
                if (q != null) {
                    if (!active) {
                        active = true;
                        do {
                            unsafe = U;
                            j = CTL;
                            c = this.ctl;
                        } while (!unsafe.compareAndSwapLong(this, j, c, c + AC_UNIT));
                        b = q.base;
                        if (b - q.top < 0 && (t = q.pollAt(b)) != null) {
                            w.runSubtask(t);
                        }
                    } else {
                        b = q.base;
                        if (b - q.top < 0) {
                            w.runSubtask(t);
                        }
                    }
                } else {
                    if (active) {
                        active = false;
                        do {
                            r0 = U;
                            j3 = CTL;
                            c4 = this.ctl;
                            c2 = r0;
                        } while (!r0.compareAndSwapLong(r0, j3, c4, c4 - AC_UNIT));
                    } else {
                        c2 = this.ctl;
                    }
                    if (((int) (c2 >> 48)) + (this.config & 65535) == 0) {
                        break;
                    }
                }
            }
        }
        do {
            unsafe2 = U;
            j2 = CTL;
            c3 = this.ctl;
        } while (!unsafe2.compareAndSwapLong(unsafe2, j2, c3, c3 + AC_UNIT));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ForkJoinTask<?> nextTaskFor(WorkQueue w) {
        ForkJoinTask<?> t;
        while (true) {
            ForkJoinTask<?> t2 = w.nextLocalTask();
            if (t2 != null) {
                return t2;
            }
            WorkQueue q = findNonEmptyStealQueue(w.nextSeed());
            if (q == null) {
                return null;
            }
            int b = q.base;
            if (b - q.top < 0 && (t = q.pollAt(b)) != null) {
                return t;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSurplusQueuedTaskCount() {
        int i;
        Thread t = Thread.currentThread();
        if (t instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread wt = (ForkJoinWorkerThread) t;
            ForkJoinPool pool = wt.pool;
            int p = pool.config & 65535;
            WorkQueue q = wt.workQueue;
            int n = q.top - q.base;
            int a = ((int) (pool.ctl >> 48)) + p;
            int p2 = p >>> 1;
            if (a > p2) {
                i = 0;
            } else {
                int p3 = p2 >>> 1;
                if (a > p3) {
                    i = 1;
                } else {
                    int p4 = p3 >>> 1;
                    i = a > p4 ? 2 : a > (p4 >>> 1) ? 4 : 8;
                }
            }
            return n - i;
        }
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x006c, code lost:
        if (java.util.concurrent.ForkJoinPool.U.compareAndSwapInt(r11, java.util.concurrent.ForkJoinPool.PLOCK, r0, r16) == false) goto L18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean tryTerminate(boolean r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 498
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ForkJoinPool.tryTerminate(boolean, boolean):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WorkQueue commonSubmitterQueue() {
        ForkJoinPool p;
        WorkQueue[] ws;
        int m;
        Submitter z = submitters.get();
        if (z == null || (p = commonPool) == null || (ws = p.workQueues) == null || (m = ws.length - 1) < 0) {
            return null;
        }
        return ws[m & z.seed & 126];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean tryExternalUnpush(ForkJoinTask<?> t) {
        Submitter z;
        ForkJoinPool p;
        WorkQueue[] ws;
        int m;
        WorkQueue q;
        int s;
        ForkJoinTask<?>[] a;
        if (t != null && (z = submitters.get()) != null && (p = commonPool) != null && (ws = p.workQueues) != null && (m = ws.length - 1) >= 0 && (q = ws[m & z.seed & 126]) != null && (s = q.top) != q.base && (a = q.array) != null) {
            long j = (((a.length - 1) & (s - 1)) << ASHIFT) + ABASE;
            if (U.getObject(a, j) == t && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                if (q.array == a && q.top == s && U.compareAndSwapObject(a, j, t, null)) {
                    q.top = s - 1;
                    q.qlock = 0;
                    return true;
                }
                q.qlock = 0;
                return false;
            }
            return false;
        }
        return false;
    }

    private void externalHelpComplete(WorkQueue q, ForkJoinTask<?> root) {
        ForkJoinTask<?>[] a;
        int m;
        CountedCompleter<?> task;
        int u;
        long j;
        Object o;
        if (q != null && (a = q.array) != null && (m = a.length - 1) >= 0 && root != null && root.status >= 0) {
            do {
                task = null;
                int s = q.top;
                if (s - q.base > 0 && (o = U.getObject(a, ((m & (s - 1)) << ASHIFT) + ABASE)) != null && (o instanceof CountedCompleter)) {
                    CountedCompleter<?> t = (CountedCompleter) o;
                    CountedCompleter<?> r = t;
                    while (true) {
                        if (r == root) {
                            if (U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                                if (q.array == a && q.top == s && U.compareAndSwapObject(a, j, t, null)) {
                                    q.top = s - 1;
                                    task = t;
                                }
                                q.qlock = 0;
                            }
                        } else {
                            CountedCompleter<?> countedCompleter = r.completer;
                            r = countedCompleter;
                            if (countedCompleter == null) {
                                break;
                            }
                        }
                    }
                }
                if (task != null) {
                    task.doExec();
                }
                if (root.status < 0 || (u = (int) (this.ctl >>> 32)) >= 0 || (u >> 16) >= 0) {
                    return;
                }
            } while (task != null);
            helpSignal(root, q.poolIndex);
            if (root.status >= 0) {
                helpComplete(root, -1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void externalHelpJoin(ForkJoinTask<?> t) {
        Submitter z;
        ForkJoinPool p;
        WorkQueue[] ws;
        int m;
        WorkQueue q;
        ForkJoinTask<?>[] a;
        if (t != null && (z = submitters.get()) != null && (p = commonPool) != null && (ws = p.workQueues) != null && (m = ws.length - 1) >= 0 && (q = ws[m & z.seed & 126]) != null && (a = q.array) != null) {
            int am = a.length - 1;
            int s = q.top;
            if (s != q.base) {
                long j = ((am & (s - 1)) << ASHIFT) + ABASE;
                if (U.getObject(a, j) == t && U.compareAndSwapInt(q, QLOCK, 0, 1)) {
                    if (q.array == a && q.top == s && U.compareAndSwapObject(a, j, t, null)) {
                        q.top = s - 1;
                        q.qlock = 0;
                        t.doExec();
                    } else {
                        q.qlock = 0;
                    }
                }
            }
            if (t.status >= 0) {
                if (t instanceof CountedCompleter) {
                    p.externalHelpComplete(q, t);
                } else {
                    p.helpSignal(t, q.poolIndex);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void externalHelpQuiescePool() {
        WorkQueue q;
        ForkJoinTask<?> t;
        ForkJoinPool p = commonPool;
        if (p == null || (q = p.findNonEmptyStealQueue(1)) == null) {
            return;
        }
        int b = q.base;
        if (b - q.top < 0 && (t = q.pollAt(b)) != null) {
            t.doExec();
        }
    }

    public ForkJoinPool() {
        this(Math.min(32767, Runtime.getRuntime().availableProcessors()), defaultForkJoinWorkerThreadFactory, (Thread.UncaughtExceptionHandler) null, false);
    }

    public ForkJoinPool(int parallelism) {
        this(parallelism, defaultForkJoinWorkerThreadFactory, (Thread.UncaughtExceptionHandler) null, false);
    }

    public ForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, boolean asyncMode) {
        checkPermission();
        if (factory == null) {
            throw new NullPointerException();
        }
        if (parallelism <= 0 || parallelism > 32767) {
            throw new IllegalArgumentException();
        }
        this.factory = factory;
        this.ueh = handler;
        this.config = parallelism | (asyncMode ? 65536 : 0);
        long np = -parallelism;
        this.ctl = ((np << 48) & AC_MASK) | ((np << 32) & TC_MASK);
        int pn = nextPoolId();
        this.workerNamePrefix = "ForkJoinPool-" + Integer.toString(pn) + "-worker-";
    }

    ForkJoinPool(int parallelism, long ctl, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler) {
        this.config = parallelism;
        this.ctl = ctl;
        this.factory = factory;
        this.ueh = handler;
        this.workerNamePrefix = "ForkJoinPool.commonPool-worker-";
    }

    public static ForkJoinPool commonPool() {
        return commonPool;
    }

    public <T> T invoke(ForkJoinTask<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        externalPush(task);
        return task.join();
    }

    public void execute(ForkJoinTask<?> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        externalPush(task);
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable task) {
        ForkJoinTask<?> job;
        if (task == null) {
            throw new NullPointerException();
        }
        if (task instanceof ForkJoinTask) {
            job = (ForkJoinTask) task;
        } else {
            job = new ForkJoinTask.AdaptedRunnableAction(task);
        }
        externalPush(job);
    }

    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        externalPush(task);
        return task;
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public <T> ForkJoinTask<T> submit(Callable<T> task) {
        ForkJoinTask<T> job = new ForkJoinTask.AdaptedCallable<>(task);
        externalPush(job);
        return job;
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public <T> ForkJoinTask<T> submit(Runnable task, T result) {
        ForkJoinTask<T> job = new ForkJoinTask.AdaptedRunnable<>(task, result);
        externalPush(job);
        return job;
    }

    @Override // java.util.concurrent.AbstractExecutorService, java.util.concurrent.ExecutorService
    public ForkJoinTask<?> submit(Runnable task) {
        ForkJoinTask<?> job;
        if (task == null) {
            throw new NullPointerException();
        }
        if (task instanceof ForkJoinTask) {
            job = (ForkJoinTask) task;
        } else {
            job = new ForkJoinTask.AdaptedRunnableAction(task);
        }
        externalPush(job);
        return job;
    }

    public ForkJoinWorkerThreadFactory getFactory() {
        return this.factory;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.ueh;
    }

    public int getParallelism() {
        return this.config & 65535;
    }

    public static int getCommonPoolParallelism() {
        return commonPoolParallelism;
    }

    public int getPoolSize() {
        return (this.config & 65535) + ((short) (this.ctl >>> 32));
    }

    public boolean getAsyncMode() {
        return (this.config >>> 16) == 1;
    }

    public int getRunningThreadCount() {
        int rc = 0;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 1; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w != null && w.isApparentlyUnblocked()) {
                    rc++;
                }
            }
        }
        return rc;
    }

    public int getActiveThreadCount() {
        int r = (this.config & 65535) + ((int) (this.ctl >> 48));
        if (r <= 0) {
            return 0;
        }
        return r;
    }

    public boolean isQuiescent() {
        return ((int) (this.ctl >> 48)) + (this.config & 65535) == 0;
    }

    public long getStealCount() {
        long count = this.stealCount;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 1; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w != null) {
                    count += w.nsteals;
                }
            }
        }
        return count;
    }

    public long getQueuedTaskCount() {
        long count = 0;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 1; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w != null) {
                    count += w.queueSize();
                }
            }
        }
        return count;
    }

    public int getQueuedSubmissionCount() {
        int count = 0;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 0; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w != null) {
                    count += w.queueSize();
                }
            }
        }
        return count;
    }

    public boolean hasQueuedSubmissions() {
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 0; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w != null && !w.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected ForkJoinTask<?> pollSubmission() {
        ForkJoinTask<?> t;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 0; i < ws.length; i += 2) {
                WorkQueue w = ws[i];
                if (w != null && (t = w.poll()) != null) {
                    return t;
                }
            }
            return null;
        }
        return null;
    }

    protected int drainTasksTo(Collection<? super ForkJoinTask<?>> c) {
        int count = 0;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (WorkQueue w : ws) {
                if (w != null) {
                    while (true) {
                        ForkJoinTask<?> t = w.poll();
                        if (t != null) {
                            c.add(t);
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public String toString() {
        String level;
        long qt = 0;
        long qs = 0;
        int rc = 0;
        long st = this.stealCount;
        long c = this.ctl;
        WorkQueue[] ws = this.workQueues;
        if (ws != null) {
            for (int i = 0; i < ws.length; i++) {
                WorkQueue w = ws[i];
                if (w != null) {
                    int size = w.queueSize();
                    if ((i & 1) == 0) {
                        qs += size;
                    } else {
                        qt += size;
                        st += w.nsteals;
                        if (w.isApparentlyUnblocked()) {
                            rc++;
                        }
                    }
                }
            }
        }
        int pc = this.config & 65535;
        int tc = pc + ((short) (c >>> 32));
        int ac = pc + ((int) (c >> 48));
        if (ac < 0) {
            ac = 0;
        }
        if ((c & 2147483648L) != 0) {
            level = tc == 0 ? SubscriptionStateHeader.TERMINATED : "Terminating";
        } else {
            level = this.plock < 0 ? "Shutting down" : "Running";
        }
        return super.toString() + "[" + level + ", parallelism = " + pc + ", size = " + tc + ", active = " + ac + ", running = " + rc + ", steals = " + st + ", tasks = " + qt + ", submissions = " + qs + "]";
    }

    @Override // java.util.concurrent.ExecutorService
    public void shutdown() {
        checkPermission();
        tryTerminate(false, true);
    }

    @Override // java.util.concurrent.ExecutorService
    public List<Runnable> shutdownNow() {
        checkPermission();
        tryTerminate(true, true);
        return Collections.emptyList();
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isTerminated() {
        long c = this.ctl;
        return (c & 2147483648L) != 0 && ((short) ((int) (c >>> 32))) == (-(this.config & 65535));
    }

    public boolean isTerminating() {
        long c = this.ctl;
        return ((c & 2147483648L) == 0 || ((short) ((int) (c >>> 32))) == (-(this.config & 65535))) ? false : true;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean isShutdown() {
        return this.plock < 0;
    }

    @Override // java.util.concurrent.ExecutorService
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        if (isTerminated()) {
            return true;
        }
        if (nanos <= 0) {
            return false;
        }
        long deadline = System.nanoTime() + nanos;
        synchronized (this) {
            while (!isTerminated()) {
                if (nanos <= 0) {
                    return false;
                }
                long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
                wait(millis > 0 ? millis : 1L);
                nanos = deadline - System.nanoTime();
            }
            return true;
        }
    }

    @Override // java.util.concurrent.AbstractExecutorService
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ForkJoinTask.AdaptedRunnable(runnable, value);
    }

    @Override // java.util.concurrent.AbstractExecutorService
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ForkJoinTask.AdaptedCallable(callable);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static {
        try {
            U = Unsafe.getUnsafe();
            CTL = U.objectFieldOffset(ForkJoinPool.class.getDeclaredField("ctl"));
            STEALCOUNT = U.objectFieldOffset(ForkJoinPool.class.getDeclaredField("stealCount"));
            PLOCK = U.objectFieldOffset(ForkJoinPool.class.getDeclaredField("plock"));
            INDEXSEED = U.objectFieldOffset(ForkJoinPool.class.getDeclaredField("indexSeed"));
            PARKBLOCKER = U.objectFieldOffset(Thread.class.getDeclaredField("parkBlocker"));
            QLOCK = U.objectFieldOffset(WorkQueue.class.getDeclaredField("qlock"));
            ABASE = U.arrayBaseOffset(ForkJoinTask[].class);
            int scale = U.arrayIndexScale(ForkJoinTask[].class);
            if ((scale & (scale - 1)) != 0) {
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
            submitters = new ThreadLocal<>();
            ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory2 = new DefaultForkJoinWorkerThreadFactory();
            defaultForkJoinWorkerThreadFactory = defaultForkJoinWorkerThreadFactory2;
            ForkJoinWorkerThreadFactory fac = defaultForkJoinWorkerThreadFactory2;
            modifyThreadPermission = new RuntimePermission("modifyThread");
            int par = 0;
            Thread.UncaughtExceptionHandler handler = null;
            try {
                String pp = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
                String hp = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
                String fp = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
                if (fp != null) {
                    fac = (ForkJoinWorkerThreadFactory) ClassLoader.getSystemClassLoader().loadClass(fp).newInstance();
                }
                if (hp != null) {
                    handler = (Thread.UncaughtExceptionHandler) ClassLoader.getSystemClassLoader().loadClass(hp).newInstance();
                }
                if (pp != null) {
                    par = Integer.parseInt(pp);
                }
            } catch (Exception e) {
            }
            if (par <= 0) {
                par = Runtime.getRuntime().availableProcessors();
            }
            if (par > 32767) {
                par = 32767;
            }
            commonPoolParallelism = par;
            long np = -par;
            long ct = ((np << 48) & AC_MASK) | ((np << 32) & TC_MASK);
            commonPool = new ForkJoinPool(par, ct, fac, handler);
        } catch (Exception e2) {
            throw new Error(e2);
        }
    }
}