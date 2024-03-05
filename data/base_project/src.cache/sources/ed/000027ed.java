package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Subject.class */
public final class Subject implements Serializable {
    private static final long serialVersionUID = -8308522755600156056L;
    private static final AuthPermission _AS = new AuthPermission("doAs");
    private static final AuthPermission _AS_PRIVILEGED = new AuthPermission("doAsPrivileged");
    private static final AuthPermission _SUBJECT = new AuthPermission("getSubject");
    private static final AuthPermission _PRINCIPALS = new AuthPermission("modifyPrincipals");
    private static final AuthPermission _PRIVATE_CREDENTIALS = new AuthPermission("modifyPrivateCredentials");
    private static final AuthPermission _PUBLIC_CREDENTIALS = new AuthPermission("modifyPublicCredentials");
    private static final AuthPermission _READ_ONLY = new AuthPermission("setReadOnly");
    private final Set<Principal> principals;
    private boolean readOnly;
    private transient SecureSet<Object> privateCredentials;
    private transient SecureSet<Object> publicCredentials;

    public Subject() {
        this.principals = new SecureSet(_PRINCIPALS);
        this.publicCredentials = new SecureSet<>(_PUBLIC_CREDENTIALS);
        this.privateCredentials = new SecureSet<>(_PRIVATE_CREDENTIALS);
        this.readOnly = false;
    }

    public Subject(boolean readOnly, Set<? extends Principal> subjPrincipals, Set<?> pubCredentials, Set<?> privCredentials) {
        if (subjPrincipals == null) {
            throw new NullPointerException("subjPrincipals == null");
        }
        if (pubCredentials == null) {
            throw new NullPointerException("pubCredentials == null");
        }
        if (privCredentials == null) {
            throw new NullPointerException("privCredentials == null");
        }
        this.principals = new SecureSet(this, _PRINCIPALS, subjPrincipals);
        this.publicCredentials = new SecureSet<>(this, _PUBLIC_CREDENTIALS, pubCredentials);
        this.privateCredentials = new SecureSet<>(this, _PRIVATE_CREDENTIALS, privCredentials);
        this.readOnly = readOnly;
    }

    public static <T> T doAs(Subject subject, PrivilegedAction<T> action) {
        return (T) doAs_PrivilegedAction(subject, action, AccessController.getContext());
    }

    public static <T> T doAsPrivileged(Subject subject, PrivilegedAction<T> action, AccessControlContext context) {
        if (context == null) {
            return (T) doAs_PrivilegedAction(subject, action, new AccessControlContext(new ProtectionDomain[0]));
        }
        return (T) doAs_PrivilegedAction(subject, action, context);
    }

    private static <T> T doAs_PrivilegedAction(Subject subject, PrivilegedAction<T> action, final AccessControlContext context) {
        SubjectDomainCombiner combiner;
        if (subject == null) {
            combiner = null;
        } else {
            combiner = new SubjectDomainCombiner(subject);
        }
        final SubjectDomainCombiner subjectDomainCombiner = combiner;
        PrivilegedAction dccAction = new PrivilegedAction() { // from class: javax.security.auth.Subject.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return new AccessControlContext(AccessControlContext.this, subjectDomainCombiner);
            }
        };
        AccessControlContext newContext = (AccessControlContext) AccessController.doPrivileged(dccAction);
        return (T) AccessController.doPrivileged(action, newContext);
    }

    public static <T> T doAs(Subject subject, PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
        return (T) doAs_PrivilegedExceptionAction(subject, action, AccessController.getContext());
    }

    public static <T> T doAsPrivileged(Subject subject, PrivilegedExceptionAction<T> action, AccessControlContext context) throws PrivilegedActionException {
        if (context == null) {
            return (T) doAs_PrivilegedExceptionAction(subject, action, new AccessControlContext(new ProtectionDomain[0]));
        }
        return (T) doAs_PrivilegedExceptionAction(subject, action, context);
    }

    private static <T> T doAs_PrivilegedExceptionAction(Subject subject, PrivilegedExceptionAction<T> action, final AccessControlContext context) throws PrivilegedActionException {
        SubjectDomainCombiner combiner;
        if (subject == null) {
            combiner = null;
        } else {
            combiner = new SubjectDomainCombiner(subject);
        }
        final SubjectDomainCombiner subjectDomainCombiner = combiner;
        PrivilegedAction<AccessControlContext> dccAction = new PrivilegedAction<AccessControlContext>() { // from class: javax.security.auth.Subject.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public AccessControlContext run() {
                return new AccessControlContext(AccessControlContext.this, subjectDomainCombiner);
            }
        };
        AccessControlContext newContext = (AccessControlContext) AccessController.doPrivileged(dccAction);
        return (T) AccessController.doPrivileged(action, newContext);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Subject that = (Subject) obj;
        if (this.principals.equals(that.principals) && this.publicCredentials.equals(that.publicCredentials) && this.privateCredentials.equals(that.privateCredentials)) {
            return true;
        }
        return false;
    }

    public Set<Principal> getPrincipals() {
        return this.principals;
    }

    public <T extends Principal> Set<T> getPrincipals(Class<T> c) {
        return ((SecureSet) this.principals).get(c);
    }

    public Set<Object> getPrivateCredentials() {
        return this.privateCredentials;
    }

    public <T> Set<T> getPrivateCredentials(Class<T> c) {
        return (Set<T>) this.privateCredentials.get(c);
    }

    public Set<Object> getPublicCredentials() {
        return this.publicCredentials;
    }

    public <T> Set<T> getPublicCredentials(Class<T> c) {
        return (Set<T>) this.publicCredentials.get(c);
    }

    public int hashCode() {
        return this.principals.hashCode() + this.privateCredentials.hashCode() + this.publicCredentials.hashCode();
    }

    public void setReadOnly() {
        this.readOnly = true;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("Subject:\n");
        for (Principal principal : this.principals) {
            buf.append("\tPrincipal: ");
            buf.append(principal);
            buf.append('\n');
        }
        Iterator<?> it = this.publicCredentials.iterator();
        while (it.hasNext()) {
            buf.append("\tPublic Credential: ");
            buf.append(it.next());
            buf.append('\n');
        }
        int offset = buf.length() - 1;
        Iterator<?> it2 = this.privateCredentials.iterator();
        while (it2.hasNext()) {
            try {
                buf.append("\tPrivate Credential: ");
                buf.append(it2.next());
                buf.append('\n');
            } catch (SecurityException e) {
                buf.delete(offset, buf.length());
                buf.append("\tPrivate Credentials: no accessible information\n");
            }
        }
        return buf.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.publicCredentials = new SecureSet<>(_PUBLIC_CREDENTIALS);
        this.privateCredentials = new SecureSet<>(_PRIVATE_CREDENTIALS);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public static Subject getSubject(final AccessControlContext context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        PrivilegedAction<DomainCombiner> action = new PrivilegedAction<DomainCombiner>() { // from class: javax.security.auth.Subject.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public DomainCombiner run() {
                return AccessControlContext.this.getDomainCombiner();
            }
        };
        DomainCombiner combiner = (DomainCombiner) AccessController.doPrivileged(action);
        if (combiner == null || !(combiner instanceof SubjectDomainCombiner)) {
            return null;
        }
        return ((SubjectDomainCombiner) combiner).getSubject();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkState() {
        if (this.readOnly) {
            throw new IllegalStateException("Set is read-only");
        }
    }

    /* loaded from: Subject$SecureSet.class */
    private final class SecureSet<SST> extends AbstractSet<SST> implements Serializable {
        private static final long serialVersionUID = 7911754171111800359L;
        private LinkedList<SST> elements;
        private int setType;
        private static final int SET_Principal = 0;
        private static final int SET_PrivCred = 1;
        private static final int SET_PubCred = 2;
        private transient AuthPermission permission;

        protected SecureSet(AuthPermission perm) {
            this.permission = perm;
            this.elements = new LinkedList<>();
        }

        protected SecureSet(Subject subject, AuthPermission perm, Collection<? extends SST> s) {
            this(perm);
            boolean trust = s.getClass().getClassLoader() == null;
            for (SST o : s) {
                verifyElement(o);
                if (trust || !this.elements.contains(o)) {
                    this.elements.add(o);
                }
            }
        }

        private void verifyElement(Object o) {
            if (o != null) {
                if (this.permission == Subject._PRINCIPALS && !Principal.class.isAssignableFrom(o.getClass())) {
                    throw new IllegalArgumentException("Element is not instance of java.security.Principal");
                }
                return;
            }
            throw new NullPointerException("o == null");
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean add(SST o) {
            verifyElement(o);
            Subject.this.checkState();
            if (!this.elements.contains(o)) {
                this.elements.add(o);
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<SST> iterator() {
            if (this.permission == Subject._PRIVATE_CREDENTIALS) {
                return new SecureIterator(this.elements.iterator()) { // from class: javax.security.auth.Subject.SecureSet.1
                    @Override // javax.security.auth.Subject.SecureSet.SecureIterator, java.util.Iterator
                    public SST next() {
                        SST obj = this.iterator.next();
                        return obj;
                    }
                };
            }
            return new SecureIterator(this.elements.iterator());
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            if (c == null) {
                throw new NullPointerException("c == null");
            }
            return super.retainAll(c);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.elements.size();
        }

        protected final <E> Set<E> get(final Class<E> c) {
            if (c == null) {
                throw new NullPointerException("c == null");
            }
            AbstractSet<E> s = new AbstractSet<E>() { // from class: javax.security.auth.Subject.SecureSet.2
                private LinkedList<E> elements = new LinkedList<>();

                @Override // java.util.AbstractCollection, java.util.Collection
                public boolean add(E o) {
                    if (!c.isAssignableFrom(o.getClass())) {
                        throw new IllegalArgumentException("Invalid type: " + o.getClass());
                    }
                    if (this.elements.contains(o)) {
                        return false;
                    }
                    this.elements.add(o);
                    return true;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
                public Iterator<E> iterator() {
                    return this.elements.iterator();
                }

                @Override // java.util.AbstractCollection, java.util.Collection
                public boolean retainAll(Collection<?> c2) {
                    if (c2 == null) {
                        throw new NullPointerException("c == null");
                    }
                    return super.retainAll(c2);
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
                public int size() {
                    return this.elements.size();
                }
            };
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                SST o = i$.next();
                if (c.isAssignableFrom(o.getClass())) {
                    s.add(c.cast(o));
                }
            }
            return s;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            switch (this.setType) {
                case 0:
                    this.permission = Subject._PRINCIPALS;
                    break;
                case 1:
                    this.permission = Subject._PRIVATE_CREDENTIALS;
                    break;
                case 2:
                    this.permission = Subject._PUBLIC_CREDENTIALS;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            Iterator i$ = this.elements.iterator();
            while (i$.hasNext()) {
                SST element = i$.next();
                verifyElement(element);
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            if (this.permission != Subject._PRIVATE_CREDENTIALS) {
                if (this.permission == Subject._PRINCIPALS) {
                    this.setType = 0;
                } else {
                    this.setType = 2;
                }
            } else {
                this.setType = 1;
            }
            out.defaultWriteObject();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: Subject$SecureSet$SecureIterator.class */
        public class SecureIterator implements Iterator<SST> {
            protected Iterator<SST> iterator;

            protected SecureIterator(Iterator<SST> iterator) {
                this.iterator = iterator;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override // java.util.Iterator
            public SST next() {
                return this.iterator.next();
            }

            @Override // java.util.Iterator
            public void remove() {
                Subject.this.checkState();
                this.iterator.remove();
            }
        }
    }
}