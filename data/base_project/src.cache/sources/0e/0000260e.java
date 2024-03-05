package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PropertyResourceBundle.class */
public class PropertyResourceBundle extends ResourceBundle {
    public PropertyResourceBundle(InputStream stream) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public PropertyResourceBundle(Reader reader) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.ResourceBundle
    protected Set<String> handleKeySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.ResourceBundle
    public Enumeration<String> getKeys() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.ResourceBundle
    public Object handleGetObject(String key) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.PropertyResourceBundle$1  reason: invalid class name */
    /* loaded from: PropertyResourceBundle$1.class */
    class AnonymousClass1 implements Enumeration<String> {
        Enumeration<String> local;
        Enumeration<String> pEnum;
        String nextElement;

        AnonymousClass1() {
            this.local = PropertyResourceBundle.access$000(PropertyResourceBundle.this);
            this.pEnum = PropertyResourceBundle.this.parent.getKeys();
        }

        private boolean findNext() {
            if (this.nextElement != null) {
                return true;
            }
            while (this.pEnum.hasMoreElements()) {
                String next = this.pEnum.nextElement();
                if (!PropertyResourceBundle.this.resources.containsKey(next)) {
                    this.nextElement = next;
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            if (this.local.hasMoreElements()) {
                return true;
            }
            return findNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public String nextElement() {
            if (this.local.hasMoreElements()) {
                return this.local.nextElement();
            }
            if (findNext()) {
                String result = this.nextElement;
                this.nextElement = null;
                return result;
            }
            return this.pEnum.nextElement();
        }
    }
}