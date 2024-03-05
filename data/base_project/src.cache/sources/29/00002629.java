package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TimerTask.class */
public abstract class TimerTask implements Runnable {
    @Override // java.lang.Runnable
    public abstract void run();

    /* JADX INFO: Access modifiers changed from: protected */
    public TimerTask() {
        throw new RuntimeException("Stub!");
    }

    public boolean cancel() {
        throw new RuntimeException("Stub!");
    }

    public long scheduledExecutionTime() {
        throw new RuntimeException("Stub!");
    }
}