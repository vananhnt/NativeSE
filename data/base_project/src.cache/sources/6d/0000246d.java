package java.security;

import java.io.Serializable;
import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PermissionCollection.class */
public abstract class PermissionCollection implements Serializable {
    public abstract void add(Permission permission);

    public abstract Enumeration<Permission> elements();

    public abstract boolean implies(Permission permission);

    public PermissionCollection() {
        throw new RuntimeException("Stub!");
    }

    public boolean isReadOnly() {
        throw new RuntimeException("Stub!");
    }

    public void setReadOnly() {
        throw new RuntimeException("Stub!");
    }
}