package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Provider.class */
public abstract class Provider extends Properties {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Provider$Service.class */
    public static class Service {
        public Service(Provider provider, String type, String algorithm, String className, List<String> aliases, Map<String, String> attributes) {
            throw new RuntimeException("Stub!");
        }

        public final String getType() {
            throw new RuntimeException("Stub!");
        }

        public final String getAlgorithm() {
            throw new RuntimeException("Stub!");
        }

        public final Provider getProvider() {
            throw new RuntimeException("Stub!");
        }

        public final String getClassName() {
            throw new RuntimeException("Stub!");
        }

        public final String getAttribute(String name) {
            throw new RuntimeException("Stub!");
        }

        public Object newInstance(Object constructorParameter) throws NoSuchAlgorithmException {
            throw new RuntimeException("Stub!");
        }

        public boolean supportsParameter(Object parameter) {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Provider(String name, double version, String info) {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public double getVersion() {
        throw new RuntimeException("Stub!");
    }

    public String getInfo() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Properties
    public synchronized void load(InputStream inStream) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized void putAll(Map<?, ?> t) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized Set<Map.Entry<Object, Object>> entrySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Map
    public Set<Object> keySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Map
    public Collection<Object> values() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized Object put(Object key, Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized Object remove(Object key) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Service getService(String type, String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Set<Service> getServices() {
        throw new RuntimeException("Stub!");
    }

    protected synchronized void putService(Service s) {
        throw new RuntimeException("Stub!");
    }

    protected synchronized void removeService(Service s) {
        throw new RuntimeException("Stub!");
    }
}