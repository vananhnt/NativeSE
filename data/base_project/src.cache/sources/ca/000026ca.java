package java.util.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

/* loaded from: Phaser.class */
public class Phaser {
    private volatile long state;
    private static final int MAX_PARTIES = 65535;
    private static final int MAX_PHASE = Integer.MAX_VALUE;
    private static final int PARTIES_SHIFT = 16;
    private static final int PHASE_SHIFT = 32;
    private static final int UNARRIVED_MASK = 65535;
    private static final long PARTIES_MASK = 4294901760L;
    private static final long COUNTS_MASK = 4294967295L;
    private static final long TERMINATION_BIT = Long.MIN_VALUE;
    private static final int ONE_ARRIVAL = 1;
    private static final int ONE_PARTY = 65536;
    private static final int ONE_DEREGISTER = 65537;
    private static final int EMPTY = 1;
    private final Phaser parent;
    private final Phaser root;
    private final AtomicReference<QNode> evenQ;
    private final AtomicReference<QNode> oddQ;
    private static final int NCPU = Runtime.getRuntime().availableProcessors();
    static final int SPINS_PER_ARRIVAL;
    private static final Unsafe UNSAFE;
    private static final long stateOffset;

    private static int unarrivedOf(long s) {
        int counts = (int) s;
        if (counts == 1) {
            return 0;
        }
        return counts & 65535;
    }

    private static int partiesOf(long s) {
        return ((int) s) >>> 16;
    }

    private static int phaseOf(long s) {
        return (int) (s >>> 32);
    }

    private static int arrivedOf(long s) {
        int counts = (int) s;
        if (counts == 1) {
            return 0;
        }
        return (counts >>> 16) - (counts & 65535);
    }

    private AtomicReference<QNode> queueFor(int phase) {
        return (phase & 1) == 0 ? this.evenQ : this.oddQ;
    }

    private String badArrive(long s) {
        return "Attempted arrival of unregistered party for " + stateToString(s);
    }

    private String badRegister(long s) {
        return "Attempt to register more than 65535 parties for " + stateToString(s);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v19, types: [sun.misc.Unsafe, long] */
    private int doArrive(int adjust) {
        long s;
        int phase;
        int unarrived;
        ?? r0;
        long n;
        Phaser root = this.root;
        do {
            s = root == this ? this.state : reconcileState();
            phase = (int) (s >>> 32);
            if (phase < 0) {
                return phase;
            }
            int counts = (int) s;
            unarrived = counts == 1 ? 0 : counts & 65535;
            if (unarrived <= 0) {
                throw new IllegalStateException(badArrive(s));
            }
            r0 = UNSAFE;
        } while (!r0.compareAndSwapLong(this, stateOffset, s, s - adjust));
        if (unarrived == 1) {
            long n2 = r0 & PARTIES_MASK;
            int nextUnarrived = ((int) n2) >>> 16;
            if (root == this) {
                if (onAdvance(phase, nextUnarrived)) {
                    n = n2 | Long.MIN_VALUE;
                } else if (nextUnarrived == 0) {
                    n = n2 | 1;
                } else {
                    n = n2 | nextUnarrived;
                }
                int nextPhase = (phase + 1) & Integer.MAX_VALUE;
                UNSAFE.compareAndSwapLong(this, stateOffset, r0, n | (nextPhase << 32));
                releaseWaiters(phase);
            } else if (nextUnarrived == 0) {
                phase = this.parent.doArrive(ONE_DEREGISTER);
                UNSAFE.compareAndSwapLong(this, stateOffset, r0, r0 | 1);
            } else {
                phase = this.parent.doArrive(1);
            }
        }
        return phase;
    }

    /* JADX WARN: Code restructure failed: missing block: B:57:0x0127, code lost:
        return r16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int doRegister(int r12) {
        /*
            Method dump skipped, instructions count: 296
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Phaser.doRegister(int):int");
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [sun.misc.Unsafe] */
    private long reconcileState() {
        long j;
        Phaser root = this.root;
        long s = this.state;
        if (root != this) {
            while (true) {
                int phase = (int) (root.state >>> 32);
                if (phase == ((int) (s >>> 32))) {
                    break;
                }
                ?? r0 = UNSAFE;
                long j2 = stateOffset;
                long j3 = s;
                long j4 = phase << 32;
                if (phase < 0) {
                    j = s & 4294967295L;
                } else {
                    int p = ((int) s) >>> 16;
                    j = p == 0 ? 1L : (s & PARTIES_MASK) | p;
                }
                s = r0;
                if (r0.compareAndSwapLong(this, j2, j3, j4 | j)) {
                    break;
                }
                s = this.state;
            }
        }
        return s;
    }

    public Phaser() {
        this(null, 0);
    }

    public Phaser(int parties) {
        this(null, parties);
    }

    public Phaser(Phaser parent) {
        this(parent, 0);
    }

    public Phaser(Phaser parent, int parties) {
        if ((parties >>> 16) != 0) {
            throw new IllegalArgumentException("Illegal number of parties");
        }
        int phase = 0;
        this.parent = parent;
        if (parent != null) {
            Phaser root = parent.root;
            this.root = root;
            this.evenQ = root.evenQ;
            this.oddQ = root.oddQ;
            if (parties != 0) {
                phase = parent.doRegister(1);
            }
        } else {
            this.root = this;
            this.evenQ = new AtomicReference<>();
            this.oddQ = new AtomicReference<>();
        }
        this.state = parties == 0 ? 1L : (phase << 32) | (parties << 16) | parties;
    }

    public int register() {
        return doRegister(1);
    }

    public int bulkRegister(int parties) {
        if (parties < 0) {
            throw new IllegalArgumentException();
        }
        if (parties == 0) {
            return getPhase();
        }
        return doRegister(parties);
    }

    public int arrive() {
        return doArrive(1);
    }

    public int arriveAndDeregister() {
        return doArrive(ONE_DEREGISTER);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v19, types: [sun.misc.Unsafe, long] */
    public int arriveAndAwaitAdvance() {
        long s;
        int phase;
        int unarrived;
        ?? r0;
        long n;
        Phaser root = this.root;
        do {
            s = root == this ? this.state : reconcileState();
            phase = (int) (s >>> 32);
            if (phase < 0) {
                return phase;
            }
            int counts = (int) s;
            unarrived = counts == 1 ? 0 : counts & 65535;
            if (unarrived <= 0) {
                throw new IllegalStateException(badArrive(s));
            }
            r0 = UNSAFE;
        } while (!r0.compareAndSwapLong(this, stateOffset, s, s - 1));
        if (unarrived > 1) {
            return root.internalAwaitAdvance(phase, null);
        }
        if (root != this) {
            return this.parent.arriveAndAwaitAdvance();
        }
        long n2 = r0 & PARTIES_MASK;
        int nextUnarrived = ((int) n2) >>> 16;
        if (onAdvance(phase, nextUnarrived)) {
            n = n2 | Long.MIN_VALUE;
        } else if (nextUnarrived == 0) {
            n = n2 | 1;
        } else {
            n = n2 | nextUnarrived;
        }
        int nextPhase = (phase + 1) & Integer.MAX_VALUE;
        if (!UNSAFE.compareAndSwapLong(this, stateOffset, r0, n | (nextPhase << 32))) {
            return (int) (this.state >>> 32);
        }
        releaseWaiters(phase);
        return nextPhase;
    }

    public int awaitAdvance(int phase) {
        Phaser root = this.root;
        long s = root == this ? this.state : reconcileState();
        int p = (int) (s >>> 32);
        if (phase < 0) {
            return phase;
        }
        if (p == phase) {
            return root.internalAwaitAdvance(phase, null);
        }
        return p;
    }

    public int awaitAdvanceInterruptibly(int phase) throws InterruptedException {
        Phaser root = this.root;
        long s = root == this ? this.state : reconcileState();
        int p = (int) (s >>> 32);
        if (phase < 0) {
            return phase;
        }
        if (p == phase) {
            QNode node = new QNode(this, phase, true, false, 0L);
            p = root.internalAwaitAdvance(phase, node);
            if (node.wasInterrupted) {
                throw new InterruptedException();
            }
        }
        return p;
    }

    public int awaitAdvanceInterruptibly(int phase, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        long nanos = unit.toNanos(timeout);
        Phaser root = this.root;
        long s = root == this ? this.state : reconcileState();
        int p = (int) (s >>> 32);
        if (phase < 0) {
            return phase;
        }
        if (p == phase) {
            QNode node = new QNode(this, phase, true, true, nanos);
            p = root.internalAwaitAdvance(phase, node);
            if (node.wasInterrupted) {
                throw new InterruptedException();
            }
            if (p == phase) {
                throw new TimeoutException();
            }
        }
        return p;
    }

    public void forceTermination() {
        long s;
        Phaser root = this.root;
        do {
            s = root.state;
            if (s < 0) {
                return;
            }
        } while (!UNSAFE.compareAndSwapLong(root, stateOffset, s, s | Long.MIN_VALUE));
        releaseWaiters(0);
        releaseWaiters(1);
    }

    public final int getPhase() {
        return (int) (this.root.state >>> 32);
    }

    public int getRegisteredParties() {
        return partiesOf(this.state);
    }

    public int getArrivedParties() {
        return arrivedOf(reconcileState());
    }

    public int getUnarrivedParties() {
        return unarrivedOf(reconcileState());
    }

    public Phaser getParent() {
        return this.parent;
    }

    public Phaser getRoot() {
        return this.root;
    }

    public boolean isTerminated() {
        return this.root.state < 0;
    }

    protected boolean onAdvance(int phase, int registeredParties) {
        return registeredParties == 0;
    }

    public String toString() {
        return stateToString(reconcileState());
    }

    private String stateToString(long s) {
        return super.toString() + "[phase = " + phaseOf(s) + " parties = " + partiesOf(s) + " arrived = " + arrivedOf(s) + "]";
    }

    private void releaseWaiters(int phase) {
        Thread t;
        AtomicReference<QNode> head = (phase & 1) == 0 ? this.evenQ : this.oddQ;
        while (true) {
            QNode q = head.get();
            if (q != null && q.phase != ((int) (this.root.state >>> 32))) {
                if (head.compareAndSet(q, q.next) && (t = q.thread) != null) {
                    q.thread = null;
                    LockSupport.unpark(t);
                }
            } else {
                return;
            }
        }
    }

    private int abortWait(int phase) {
        int p;
        Thread t;
        AtomicReference<QNode> head = (phase & 1) == 0 ? this.evenQ : this.oddQ;
        while (true) {
            QNode q = head.get();
            p = (int) (this.root.state >>> 32);
            if (q == null || ((t = q.thread) != null && q.phase == p)) {
                break;
            } else if (head.compareAndSet(q, q.next) && t != null) {
                q.thread = null;
                LockSupport.unpark(t);
            }
        }
        return p;
    }

    static {
        SPINS_PER_ARRIVAL = NCPU < 2 ? 1 : 256;
        try {
            UNSAFE = Unsafe.getUnsafe();
            stateOffset = UNSAFE.objectFieldOffset(Phaser.class.getDeclaredField("state"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private int internalAwaitAdvance(int phase, QNode node) {
        int p;
        releaseWaiters(phase - 1);
        boolean queued = false;
        int lastUnarrived = 0;
        int spins = SPINS_PER_ARRIVAL;
        while (true) {
            long s = this.state;
            int i = (int) (s >>> 32);
            p = i;
            if (i != phase) {
                break;
            } else if (node == null) {
                int unarrived = ((int) s) & 65535;
                if (unarrived != lastUnarrived) {
                    lastUnarrived = unarrived;
                    if (unarrived < NCPU) {
                        spins += SPINS_PER_ARRIVAL;
                    }
                }
                boolean interrupted = Thread.interrupted();
                if (!interrupted) {
                    spins--;
                    if (spins < 0) {
                    }
                }
                node = new QNode(this, phase, false, false, 0L);
                node.wasInterrupted = interrupted;
            } else if (node.isReleasable()) {
                break;
            } else if (!queued) {
                AtomicReference<QNode> head = (phase & 1) == 0 ? this.evenQ : this.oddQ;
                QNode q = head.get();
                node.next = q;
                if (q == null || q.phase == phase) {
                    if (((int) (this.state >>> 32)) == phase) {
                        queued = head.compareAndSet(q, node);
                    }
                }
            } else {
                try {
                    ForkJoinPool.managedBlock(node);
                } catch (InterruptedException e) {
                    node.wasInterrupted = true;
                }
            }
        }
        if (node != null) {
            if (node.thread != null) {
                node.thread = null;
            }
            if (node.wasInterrupted && !node.interruptible) {
                Thread.currentThread().interrupt();
            }
            if (p == phase) {
                int i2 = (int) (this.state >>> 32);
                p = i2;
                if (i2 == phase) {
                    return abortWait(phase);
                }
            }
        }
        releaseWaiters(phase);
        return p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Phaser$QNode.class */
    public static final class QNode implements ForkJoinPool.ManagedBlocker {
        final Phaser phaser;
        final int phase;
        final boolean interruptible;
        final boolean timed;
        boolean wasInterrupted;
        long nanos;
        final long deadline;
        volatile Thread thread;
        QNode next;

        QNode(Phaser phaser, int phase, boolean interruptible, boolean timed, long nanos) {
            this.phaser = phaser;
            this.phase = phase;
            this.interruptible = interruptible;
            this.nanos = nanos;
            this.timed = timed;
            this.deadline = timed ? System.nanoTime() + nanos : 0L;
            this.thread = Thread.currentThread();
        }

        @Override // java.util.concurrent.ForkJoinPool.ManagedBlocker
        public boolean isReleasable() {
            if (this.thread == null) {
                return true;
            }
            if (this.phaser.getPhase() != this.phase) {
                this.thread = null;
                return true;
            }
            if (Thread.interrupted()) {
                this.wasInterrupted = true;
            }
            if (this.wasInterrupted && this.interruptible) {
                this.thread = null;
                return true;
            } else if (this.timed) {
                if (this.nanos > 0) {
                    this.nanos = this.deadline - System.nanoTime();
                }
                if (this.nanos <= 0) {
                    this.thread = null;
                    return true;
                }
                return false;
            } else {
                return false;
            }
        }

        @Override // java.util.concurrent.ForkJoinPool.ManagedBlocker
        public boolean block() {
            if (isReleasable()) {
                return true;
            }
            if (!this.timed) {
                LockSupport.park(this);
            } else if (this.nanos > 0) {
                LockSupport.parkNanos(this, this.nanos);
            }
            return isReleasable();
        }
    }
}