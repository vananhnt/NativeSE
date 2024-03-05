package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: GuardedObject.class */
public class GuardedObject implements Serializable {
    public GuardedObject(Object object, Guard guard) {
        throw new RuntimeException("Stub!");
    }

    public Object getObject() throws SecurityException {
        throw new RuntimeException("Stub!");
    }
}