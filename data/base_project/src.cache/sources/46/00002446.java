package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: Identity.class */
public abstract class Identity implements Principal, Serializable {
    /* JADX INFO: Access modifiers changed from: protected */
    public Identity() {
        throw new RuntimeException("Stub!");
    }

    public Identity(String name) {
        throw new RuntimeException("Stub!");
    }

    public Identity(String name, IdentityScope scope) throws KeyManagementException {
        throw new RuntimeException("Stub!");
    }

    public void addCertificate(Certificate certificate) throws KeyManagementException {
        throw new RuntimeException("Stub!");
    }

    public void removeCertificate(Certificate certificate) throws KeyManagementException {
        throw new RuntimeException("Stub!");
    }

    public Certificate[] certificates() {
        throw new RuntimeException("Stub!");
    }

    protected boolean identityEquals(Identity identity) {
        throw new RuntimeException("Stub!");
    }

    public String toString(boolean detailed) {
        throw new RuntimeException("Stub!");
    }

    public final IdentityScope getScope() {
        throw new RuntimeException("Stub!");
    }

    public void setPublicKey(PublicKey key) throws KeyManagementException {
        throw new RuntimeException("Stub!");
    }

    public PublicKey getPublicKey() {
        throw new RuntimeException("Stub!");
    }

    public void setInfo(String info) {
        throw new RuntimeException("Stub!");
    }

    public String getInfo() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Principal
    public final boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Principal
    public final String getName() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Principal
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.Principal
    public String toString() {
        throw new RuntimeException("Stub!");
    }
}