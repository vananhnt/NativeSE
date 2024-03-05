package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Exchanger.class */
public class Exchanger<V> {
    public Exchanger() {
        throw new RuntimeException("Stub!");
    }

    public V exchange(V x) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public V exchange(V x, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Exchanger$Node.class */
    public static final class Node {
        int index;
        int bound;
        int collides;
        int hash;
        Object item;
        volatile Object match;
        volatile Thread parked;
        Object p0;
        Object p1;
        Object p2;
        Object p3;
        Object p4;
        Object p5;
        Object p6;
        Object p7;
        Object p8;
        Object p9;
        Object pa;
        Object pb;
        Object pc;
        Object pd;
        Object pe;
        Object pf;
        Object q0;
        Object q1;
        Object q2;
        Object q3;
        Object q4;
        Object q5;
        Object q6;
        Object q7;
        Object q8;
        Object q9;
        Object qa;
        Object qb;
        Object qc;
        Object qd;
        Object qe;
        Object qf;

        Node() {
        }
    }

    /* loaded from: Exchanger$Participant.class */
    static final class Participant extends ThreadLocal<Node> {
        Participant() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Node initialValue() {
            return new Node();
        }
    }
}