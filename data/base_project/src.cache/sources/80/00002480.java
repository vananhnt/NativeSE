package java.security;

import java.security.Provider;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.harmony.security.fortress.SecurityAccess;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Security.class */
public final class Security {
    Security() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static String getAlgorithmProperty(String algName, String propName) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized int insertProviderAt(Provider provider, int position) {
        throw new RuntimeException("Stub!");
    }

    public static int addProvider(Provider provider) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized void removeProvider(String name) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized Provider[] getProviders() {
        throw new RuntimeException("Stub!");
    }

    public static synchronized Provider getProvider(String name) {
        throw new RuntimeException("Stub!");
    }

    public static Provider[] getProviders(String filter) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized Provider[] getProviders(Map<String, String> filter) {
        throw new RuntimeException("Stub!");
    }

    public static String getProperty(String key) {
        throw new RuntimeException("Stub!");
    }

    public static void setProperty(String key, String value) {
        throw new RuntimeException("Stub!");
    }

    public static Set<String> getAlgorithms(String serviceName) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Security$SecurityDoor.class */
    private static class SecurityDoor implements SecurityAccess {
        private SecurityDoor() {
        }

        @Override // org.apache.harmony.security.fortress.SecurityAccess
        public void renumProviders() {
            Security.access$100();
        }

        @Override // org.apache.harmony.security.fortress.SecurityAccess
        public List<String> getAliases(Provider.Service s) {
            return s.getAliases();
        }

        @Override // org.apache.harmony.security.fortress.SecurityAccess
        public Provider.Service getService(Provider p, String type) {
            return p.getService(type);
        }
    }
}