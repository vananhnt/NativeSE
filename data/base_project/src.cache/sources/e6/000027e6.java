package javax.net.ssl;

import java.security.Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509ExtendedKeyManager.class */
public abstract class X509ExtendedKeyManager implements X509KeyManager {
    protected X509ExtendedKeyManager() {
    }

    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return null;
    }

    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        return null;
    }
}