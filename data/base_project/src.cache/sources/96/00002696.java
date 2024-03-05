package java.util.concurrent;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Delayed.class */
public interface Delayed extends Comparable<Delayed> {
    long getDelay(TimeUnit timeUnit);
}