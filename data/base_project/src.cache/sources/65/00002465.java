package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyStoreSpi.class */
public abstract class KeyStoreSpi {
    public abstract Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException;

    public abstract java.security.cert.Certificate[] engineGetCertificateChain(String str);

    public abstract java.security.cert.Certificate engineGetCertificate(String str);

    public abstract Date engineGetCreationDate(String str);

    public abstract void engineSetKeyEntry(String str, Key key, char[] cArr, java.security.cert.Certificate[] certificateArr) throws KeyStoreException;

    public abstract void engineSetKeyEntry(String str, byte[] bArr, java.security.cert.Certificate[] certificateArr) throws KeyStoreException;

    public abstract void engineSetCertificateEntry(String str, java.security.cert.Certificate certificate) throws KeyStoreException;

    public abstract void engineDeleteEntry(String str) throws KeyStoreException;

    public abstract Enumeration<String> engineAliases();

    public abstract boolean engineContainsAlias(String str);

    public abstract int engineSize();

    public abstract boolean engineIsKeyEntry(String str);

    public abstract boolean engineIsCertificateEntry(String str);

    public abstract String engineGetCertificateAlias(java.security.cert.Certificate certificate);

    public abstract void engineStore(OutputStream outputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException;

    public abstract void engineLoad(InputStream inputStream, char[] cArr) throws IOException, NoSuchAlgorithmException, CertificateException;

    public KeyStoreSpi() {
        throw new RuntimeException("Stub!");
    }

    public void engineStore(KeyStore.LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new RuntimeException("Stub!");
    }

    public void engineLoad(KeyStore.LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new RuntimeException("Stub!");
    }

    public KeyStore.Entry engineGetEntry(String alias, KeyStore.ProtectionParameter protParam) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        throw new RuntimeException("Stub!");
    }

    public void engineSetEntry(String alias, KeyStore.Entry entry, KeyStore.ProtectionParameter protParam) throws KeyStoreException {
        throw new RuntimeException("Stub!");
    }

    public boolean engineEntryInstanceOf(String alias, Class<? extends KeyStore.Entry> entryClass) {
        throw new RuntimeException("Stub!");
    }
}