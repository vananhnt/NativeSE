package java.security.acl;

import java.security.Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Owner.class */
public interface Owner {
    boolean addOwner(Principal principal, Principal principal2) throws NotOwnerException;

    boolean deleteOwner(Principal principal, Principal principal2) throws NotOwnerException, LastOwnerException;

    boolean isOwner(Principal principal);
}