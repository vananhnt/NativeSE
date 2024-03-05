package javax.security.auth;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Destroyable.class */
public interface Destroyable {
    void destroy() throws DestroyFailedException;

    boolean isDestroyed();
}