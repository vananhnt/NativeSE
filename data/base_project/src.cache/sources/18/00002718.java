package java.util.concurrent.locks;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ReadWriteLock.class */
public interface ReadWriteLock {
    Lock readLock();

    Lock writeLock();
}