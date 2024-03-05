package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ListResourceBundle.class */
public abstract class ListResourceBundle extends ResourceBundle {
    protected abstract Object[][] getContents();

    public ListResourceBundle() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.ResourceBundle
    public Enumeration<String> getKeys() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.ResourceBundle
    public final Object handleGetObject(String key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.ResourceBundle
    protected Set<String> handleKeySet() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.ListResourceBundle$1  reason: invalid class name */
    /* loaded from: ListResourceBundle$1.class */
    class AnonymousClass1 implements Enumeration<String> {
        Iterator<String> local;
        Enumeration<String> pEnum;
        String nextElement;

        AnonymousClass1() {
            this.local = ListResourceBundle.this.table.keySet().iterator();
            this.pEnum = ListResourceBundle.this.parent.getKeys();
        }

        private boolean findNext() {
            if (this.nextElement != null) {
                return true;
            }
            while (this.pEnum.hasMoreElements()) {
                String next = this.pEnum.nextElement();
                if (!ListResourceBundle.this.table.containsKey(next)) {
                    this.nextElement = next;
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            if (this.local.hasNext()) {
                return true;
            }
            return findNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public String nextElement() {
            if (this.local.hasNext()) {
                return this.local.next();
            }
            if (findNext()) {
                String result = this.nextElement;
                this.nextElement = null;
                return result;
            }
            return this.pEnum.nextElement();
        }
    }

    /* renamed from: java.util.ListResourceBundle$2  reason: invalid class name */
    /* loaded from: ListResourceBundle$2.class */
    class AnonymousClass2 implements Enumeration<String> {
        Iterator<String> it;

        AnonymousClass2() {
            this.it = ListResourceBundle.this.table.keySet().iterator();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.it.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public String nextElement() {
            return this.it.next();
        }
    }
}