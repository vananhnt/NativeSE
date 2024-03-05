package java.security;

import java.util.Enumeration;

/* loaded from: AllPermissionCollection.class */
final class AllPermissionCollection extends PermissionCollection {
    AllPermissionCollection() {
    }

    @Override // java.security.PermissionCollection
    public void add(Permission permission) {
    }

    @Override // java.security.PermissionCollection
    public Enumeration<Permission> elements() {
        return null;
    }

    @Override // java.security.PermissionCollection
    public boolean implies(Permission permission) {
        return true;
    }
}