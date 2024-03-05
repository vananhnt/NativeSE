package java.util.concurrent.locks;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractOwnableSynchronizer.class */
public abstract class AbstractOwnableSynchronizer implements Serializable {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractOwnableSynchronizer() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setExclusiveOwnerThread(Thread t) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Thread getExclusiveOwnerThread() {
        throw new RuntimeException("Stub!");
    }
}