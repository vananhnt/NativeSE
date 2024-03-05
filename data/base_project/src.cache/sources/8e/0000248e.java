package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Acl.class */
public interface Acl extends Owner {
    void setName(Principal principal, String str) throws NotOwnerException;

    String getName();

    boolean addEntry(Principal principal, AclEntry aclEntry) throws NotOwnerException;

    boolean removeEntry(Principal principal, AclEntry aclEntry) throws NotOwnerException;

    Enumeration<Permission> getPermissions(Principal principal);

    Enumeration<AclEntry> entries();

    boolean checkPermission(Principal principal, Permission permission);

    String toString();
}