package java.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.callback.CallbackHandler;
import libcore.io.IoUtils;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyStore.class */
public class KeyStore {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$Entry.class */
    public interface Entry {
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$LoadStoreParameter.class */
    public interface LoadStoreParameter {
        ProtectionParameter getProtectionParameter();
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$ProtectionParameter.class */
    public interface ProtectionParameter {
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$Builder.class */
    public static abstract class Builder {
        public abstract KeyStore getKeyStore() throws KeyStoreException;

        public abstract ProtectionParameter getProtectionParameter(String str) throws KeyStoreException;

        protected Builder() {
            throw new RuntimeException("Stub!");
        }

        public static Builder newInstance(KeyStore keyStore, ProtectionParameter protectionParameter) {
            throw new RuntimeException("Stub!");
        }

        public static Builder newInstance(String type, Provider provider, File file, ProtectionParameter protectionParameter) {
            throw new RuntimeException("Stub!");
        }

        public static Builder newInstance(String type, Provider provider, ProtectionParameter protectionParameter) {
            throw new RuntimeException("Stub!");
        }

        /* loaded from: KeyStore$Builder$BuilderImpl.class */
        private static class BuilderImpl extends Builder {
            private KeyStore keyStore;
            private ProtectionParameter protParameter;
            private final String typeForKeyStore;
            private final Provider providerForKeyStore;
            private final File fileForLoad;
            private boolean isGetKeyStore;
            private KeyStoreException lastException = null;

            BuilderImpl(KeyStore ks, ProtectionParameter pp, File file, String type, Provider provider) {
                this.isGetKeyStore = false;
                this.keyStore = ks;
                this.protParameter = pp;
                this.fileForLoad = file;
                this.typeForKeyStore = type;
                this.providerForKeyStore = provider;
                this.isGetKeyStore = false;
            }

            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r8v0 */
            /* JADX WARN: Type inference failed for: r8v1 */
            /* JADX WARN: Type inference failed for: r8v2, types: [java.lang.AutoCloseable, java.io.InputStream] */
            @Override // java.security.KeyStore.Builder
            public synchronized KeyStore getKeyStore() throws KeyStoreException {
                char[] passwd;
                if (this.lastException != null) {
                    throw this.lastException;
                }
                if (this.keyStore != null) {
                    this.isGetKeyStore = true;
                    return this.keyStore;
                }
                try {
                    KeyStore ks = this.providerForKeyStore == null ? KeyStore.getInstance(this.typeForKeyStore) : KeyStore.getInstance(this.typeForKeyStore, this.providerForKeyStore);
                    if (this.protParameter instanceof PasswordProtection) {
                        passwd = ((PasswordProtection) this.protParameter).getPassword();
                    } else if (this.protParameter instanceof CallbackHandlerProtection) {
                        passwd = KeyStoreSpi.getPasswordFromCallBack(this.protParameter);
                    } else {
                        throw new KeyStoreException("protectionParameter is neither PasswordProtection nor CallbackHandlerProtection instance");
                    }
                    if (this.fileForLoad != null) {
                        ?? r8 = 0;
                        try {
                            r8 = new FileInputStream(this.fileForLoad);
                            ks.load(r8, passwd);
                            IoUtils.closeQuietly((AutoCloseable) r8);
                        } catch (Throwable th) {
                            IoUtils.closeQuietly((AutoCloseable) r8);
                            throw th;
                        }
                    } else {
                        ks.load(new TmpLSParameter(this.protParameter));
                    }
                    this.isGetKeyStore = true;
                    return ks;
                } catch (KeyStoreException e) {
                    this.lastException = e;
                    throw e;
                } catch (Exception e2) {
                    KeyStoreException keyStoreException = new KeyStoreException(e2);
                    this.lastException = keyStoreException;
                    throw keyStoreException;
                }
            }

            @Override // java.security.KeyStore.Builder
            public synchronized ProtectionParameter getProtectionParameter(String alias) throws KeyStoreException {
                if (alias == null) {
                    throw new NullPointerException("alias == null");
                }
                if (!this.isGetKeyStore) {
                    throw new IllegalStateException("getKeyStore() was not invoked");
                }
                return this.protParameter;
            }
        }

        /* loaded from: KeyStore$Builder$TmpLSParameter.class */
        private static class TmpLSParameter implements LoadStoreParameter {
            private final ProtectionParameter protPar;

            public TmpLSParameter(ProtectionParameter protPar) {
                this.protPar = protPar;
            }

            @Override // java.security.KeyStore.LoadStoreParameter
            public ProtectionParameter getProtectionParameter() {
                return this.protPar;
            }
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$CallbackHandlerProtection.class */
    public static class CallbackHandlerProtection implements ProtectionParameter {
        public CallbackHandlerProtection(CallbackHandler handler) {
            throw new RuntimeException("Stub!");
        }

        public CallbackHandler getCallbackHandler() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$PasswordProtection.class */
    public static class PasswordProtection implements ProtectionParameter, Destroyable {
        public PasswordProtection(char[] password) {
            throw new RuntimeException("Stub!");
        }

        public synchronized char[] getPassword() {
            throw new RuntimeException("Stub!");
        }

        @Override // javax.security.auth.Destroyable
        public synchronized void destroy() throws DestroyFailedException {
            throw new RuntimeException("Stub!");
        }

        @Override // javax.security.auth.Destroyable
        public synchronized boolean isDestroyed() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$PrivateKeyEntry.class */
    public static final class PrivateKeyEntry implements Entry {
        public PrivateKeyEntry(PrivateKey privateKey, java.security.cert.Certificate[] chain) {
            throw new RuntimeException("Stub!");
        }

        public PrivateKey getPrivateKey() {
            throw new RuntimeException("Stub!");
        }

        public java.security.cert.Certificate[] getCertificateChain() {
            throw new RuntimeException("Stub!");
        }

        public java.security.cert.Certificate getCertificate() {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$SecretKeyEntry.class */
    public static final class SecretKeyEntry implements Entry {
        public SecretKeyEntry(SecretKey secretKey) {
            throw new RuntimeException("Stub!");
        }

        public SecretKey getSecretKey() {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: KeyStore$TrustedCertificateEntry.class */
    public static final class TrustedCertificateEntry implements Entry {
        public TrustedCertificateEntry(java.security.cert.Certificate trustCertificate) {
            throw new RuntimeException("Stub!");
        }

        public java.security.cert.Certificate getTrustedCertificate() {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    protected KeyStore(KeyStoreSpi keyStoreSpi, Provider provider, String type) {
        throw new RuntimeException("Stub!");
    }

    public static KeyStore getInstance(String type) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public static KeyStore getInstance(String type, String provider) throws KeyStoreException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static KeyStore getInstance(String type, Provider provider) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public static final String getDefaultType() {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final String getType() {
        throw new RuntimeException("Stub!");
    }

    public final Key getKey(String alias, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        throw new RuntimeException("Stub!");
    }

    public final java.security.cert.Certificate[] getCertificateChain(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final java.security.cert.Certificate getCertificate(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final Date getCreationDate(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final void setKeyEntry(String alias, Key key, char[] password, java.security.cert.Certificate[] chain) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final void setKeyEntry(String alias, byte[] key, java.security.cert.Certificate[] chain) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final void setCertificateEntry(String alias, java.security.cert.Certificate cert) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final void deleteEntry(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final Enumeration<String> aliases() throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final boolean containsAlias(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final int size() throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final boolean isKeyEntry(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final boolean isCertificateEntry(String alias) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final String getCertificateAlias(java.security.cert.Certificate cert) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final void store(OutputStream stream, char[] password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final void store(LoadStoreParameter param) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final void load(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final void load(LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new RuntimeException("Stub!");
    }

    public final Entry getEntry(String alias, ProtectionParameter param) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final void setEntry(String alias, Entry entry, ProtectionParameter param) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public final boolean entryInstanceOf(String alias, Class<? extends Entry> entryClass) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }
}