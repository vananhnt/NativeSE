package java.util.concurrent;

import java.util.Random;

/* loaded from: ThreadLocalRandom.class */
public class ThreadLocalRandom extends Random {
    private static final long multiplier = 25214903917L;
    private static final long addend = 11;
    private static final long mask = 281474976710655L;
    private long rnd;
    boolean initialized = true;
    private long pad0;
    private long pad1;
    private long pad2;
    private long pad3;
    private long pad4;
    private long pad5;
    private long pad6;
    private long pad7;
    private static final ThreadLocal<ThreadLocalRandom> localRandom = new ThreadLocal<ThreadLocalRandom>() { // from class: java.util.concurrent.ThreadLocalRandom.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public ThreadLocalRandom initialValue() {
            return new ThreadLocalRandom();
        }
    };
    private static final long serialVersionUID = -5851777807851030925L;

    ThreadLocalRandom() {
    }

    public static ThreadLocalRandom current() {
        return localRandom.get();
    }

    @Override // java.util.Random
    public void setSeed(long seed) {
        if (this.initialized) {
            throw new UnsupportedOperationException();
        }
        this.rnd = (seed ^ multiplier) & mask;
    }

    @Override // java.util.Random
    protected int next(int bits) {
        this.rnd = ((this.rnd * multiplier) + addend) & mask;
        return (int) (this.rnd >>> (48 - bits));
    }

    public int nextInt(int least, int bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return nextInt(bound - least) + least;
    }

    public long nextLong(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        long offset = 0;
        while (n >= 2147483647L) {
            int bits = next(2);
            long half = n >>> 1;
            long nextn = (bits & 2) == 0 ? half : n - half;
            if ((bits & 1) == 0) {
                offset += n - nextn;
            }
            n = nextn;
        }
        return offset + nextInt((int) n);
    }

    public long nextLong(long least, long bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return nextLong(bound - least) + least;
    }

    public double nextDouble(double n) {
        if (n <= 0.0d) {
            throw new IllegalArgumentException("n must be positive");
        }
        return nextDouble() * n;
    }

    public double nextDouble(double least, double bound) {
        if (least >= bound) {
            throw new IllegalArgumentException();
        }
        return (nextDouble() * (bound - least)) + least;
    }
}