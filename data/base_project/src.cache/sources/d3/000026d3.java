package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RunnableScheduledFuture.class */
public interface RunnableScheduledFuture<V> extends RunnableFuture<V>, ScheduledFuture<V> {
    boolean isPeriodic();
}