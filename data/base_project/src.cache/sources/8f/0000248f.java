package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AclEntry.class */
public interface AclEntry extends Cloneable {
    boolean setPrincipal(Principal principal);

    Principal getPrincipal();

    void setNegativePermissions();

    boolean isNegative();

    boolean addPermission(Permission permission);

    boolean removePermission(Permission permission);

    boolean checkPermission(Permission permission);

    Enumeration<Permission> permissions();

    String toString();

    Object clone();
}