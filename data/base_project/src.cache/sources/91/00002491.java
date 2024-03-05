package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Group.class */
public interface Group extends Principal {
    boolean addMember(Principal principal);

    boolean removeMember(Principal principal);

    boolean isMember(Principal principal);

    Enumeration<? extends Principal> members();
}