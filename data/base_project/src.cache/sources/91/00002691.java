package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CyclicBarrier.class */
public class CyclicBarrier {
    public CyclicBarrier(int parties, Runnable barrierAction) {
        throw new RuntimeException("Stub!");
    }

    public CyclicBarrier(int parties) {
        throw new RuntimeException("Stub!");
    }

    public int getParties() {
        throw new RuntimeException("Stub!");
    }

    public int await() throws InterruptedException, BrokenBarrierException {
        throw new RuntimeException("Stub!");
    }

    public int await(long timeout, TimeUnit unit) throws InterruptedException, BrokenBarrierException, TimeoutException {
        throw new RuntimeException("Stub!");
    }

    public boolean isBroken() {
        throw new RuntimeException("Stub!");
    }

    public void reset() {
        throw new RuntimeException("Stub!");
    }

    public int getNumberWaiting() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: CyclicBarrier$Generation.class */
    private static class Generation {
        boolean broken;

        private Generation() {
            this.broken = false;
        }
    }
}