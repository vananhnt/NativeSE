package java.security.cert;

import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509Extension.class */
public interface X509Extension {
    Set<String> getCriticalExtensionOIDs();

    byte[] getExtensionValue(String str);

    Set<String> getNonCriticalExtensionOIDs();

    boolean hasUnsupportedCriticalExtension();
}