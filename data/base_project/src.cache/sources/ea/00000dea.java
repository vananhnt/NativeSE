package android.security;

import android.util.Log;
import com.android.org.conscrypt.OpenSSLEngine;
import com.android.org.conscrypt.OpenSSLKeyHolder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/* loaded from: AndroidKeyStore.class */
public class AndroidKeyStore extends KeyStoreSpi {
    public static final String NAME = "AndroidKeyStore";
    private KeyStore mKeyStore;

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (!isKeyEntry(alias)) {
            return null;
        }
        OpenSSLEngine engine = OpenSSLEngine.getInstance("keystore");
        try {
            return engine.getPrivateKeyById(Credentials.USER_PRIVATE_KEY + alias);
        } catch (InvalidKeyException e) {
            UnrecoverableKeyException t = new UnrecoverableKeyException("Can't get key");
            t.initCause(e);
            throw t;
        }
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        Certificate[] caList;
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        X509Certificate leaf = (X509Certificate) engineGetCertificate(alias);
        if (leaf == null) {
            return null;
        }
        byte[] caBytes = this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias);
        if (caBytes != null) {
            Collection<X509Certificate> caChain = toCertificates(caBytes);
            caList = new Certificate[caChain.size() + 1];
            int i = 1;
            for (X509Certificate x509Certificate : caChain) {
                int i2 = i;
                i++;
                caList[i2] = x509Certificate;
            }
        } else {
            caList = new Certificate[1];
        }
        caList[0] = leaf;
        return caList;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        byte[] certificate = this.mKeyStore.get(Credentials.USER_CERTIFICATE + alias);
        if (certificate != null) {
            return toCertificate(certificate);
        }
        byte[] certificate2 = this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias);
        if (certificate2 != null) {
            return toCertificate(certificate2);
        }
        return null;
    }

    private static X509Certificate toCertificate(byte[] bytes) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            Log.w("AndroidKeyStore", "Couldn't parse certificate in keystore", e);
            return null;
        }
    }

    private static Collection<X509Certificate> toCertificates(byte[] bytes) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return certFactory.generateCertificates(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            Log.w("AndroidKeyStore", "Couldn't parse certificates in keystore", e);
            return new ArrayList();
        }
    }

    private Date getModificationDate(String alias) {
        long epochMillis = this.mKeyStore.getmtime(alias);
        if (epochMillis == -1) {
            return null;
        }
        return new Date(epochMillis);
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        Date d = getModificationDate(Credentials.USER_PRIVATE_KEY + alias);
        if (d != null) {
            return d;
        }
        Date d2 = getModificationDate(Credentials.USER_CERTIFICATE + alias);
        if (d2 != null) {
            return d2;
        }
        return getModificationDate(Credentials.CA_CERTIFICATE + alias);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (password != null && password.length > 0) {
            throw new KeyStoreException("entries cannot be protected with passwords");
        }
        if (key instanceof PrivateKey) {
            setPrivateKeyEntry(alias, (PrivateKey) key, chain, null);
            return;
        }
        throw new KeyStoreException("Only PrivateKeys are supported");
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setPrivateKeyEntry(String alias, PrivateKey key, Certificate[] chain, KeyStoreParameter params) throws KeyStoreException {
        String pkeyAlias;
        boolean shouldReplacePrivateKey;
        byte[] chainBytes;
        byte[] keyBytes = null;
        if (key instanceof OpenSSLKeyHolder) {
            pkeyAlias = ((OpenSSLKeyHolder) key).getOpenSSLKey().getAlias();
        } else {
            pkeyAlias = null;
        }
        if (pkeyAlias != null && pkeyAlias.startsWith(Credentials.USER_PRIVATE_KEY)) {
            String keySubalias = pkeyAlias.substring(Credentials.USER_PRIVATE_KEY.length());
            if (!alias.equals(keySubalias)) {
                throw new KeyStoreException("Can only replace keys with same alias: " + alias + " != " + keySubalias);
            }
            shouldReplacePrivateKey = false;
        } else {
            String keyFormat = key.getFormat();
            if (keyFormat == null || !"PKCS#8".equals(keyFormat)) {
                throw new KeyStoreException("Only PrivateKeys that can be encoded into PKCS#8 are supported");
            }
            keyBytes = key.getEncoded();
            if (keyBytes == null) {
                throw new KeyStoreException("PrivateKey has no encoding");
            }
            shouldReplacePrivateKey = true;
        }
        if (chain == null || chain.length == 0) {
            throw new KeyStoreException("Must supply at least one Certificate with PrivateKey");
        }
        X509Certificate[] x509chain = new X509Certificate[chain.length];
        for (int i = 0; i < chain.length; i++) {
            if (!"X.509".equals(chain[i].getType())) {
                throw new KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i);
            }
            if (!(chain[i] instanceof X509Certificate)) {
                throw new KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i);
            }
            x509chain[i] = (X509Certificate) chain[i];
        }
        try {
            byte[] userCertBytes = x509chain[0].getEncoded();
            if (chain.length > 1) {
                byte[] bArr = new byte[x509chain.length - 1];
                int totalCertLength = 0;
                for (int i2 = 0; i2 < bArr.length; i2++) {
                    try {
                        bArr[i2] = x509chain[i2 + 1].getEncoded();
                        totalCertLength += bArr[i2].length;
                    } catch (CertificateEncodingException e) {
                        throw new KeyStoreException("Can't encode Certificate #" + i2, e);
                    }
                }
                chainBytes = new byte[totalCertLength];
                int outputOffset = 0;
                for (int i3 = 0; i3 < bArr.length; i3++) {
                    int certLength = bArr[i3].length;
                    System.arraycopy(bArr[i3], 0, chainBytes, outputOffset, certLength);
                    outputOffset += certLength;
                    bArr[i3] = 0;
                }
            } else {
                chainBytes = null;
            }
            if (shouldReplacePrivateKey) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
            } else {
                Credentials.deleteCertificateTypesForAlias(this.mKeyStore, alias);
            }
            int flags = params == null ? 0 : params.getFlags();
            if (shouldReplacePrivateKey && !this.mKeyStore.importKey(Credentials.USER_PRIVATE_KEY + alias, keyBytes, -1, flags)) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                throw new KeyStoreException("Couldn't put private key in keystore");
            } else if (!this.mKeyStore.put(Credentials.USER_CERTIFICATE + alias, userCertBytes, -1, flags)) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                throw new KeyStoreException("Couldn't put certificate #1 in keystore");
            } else if (chainBytes != null && !this.mKeyStore.put(Credentials.CA_CERTIFICATE + alias, chainBytes, -1, flags)) {
                Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                throw new KeyStoreException("Couldn't put certificate chain in keystore");
            }
        } catch (CertificateEncodingException e2) {
            throw new KeyStoreException("Couldn't encode certificate #1", e2);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] userKey, Certificate[] chain) throws KeyStoreException {
        throw new KeyStoreException("Operation not supported because key encoding is unknown");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        if (isKeyEntry(alias)) {
            throw new KeyStoreException("Entry exists and is not a trusted certificate");
        }
        if (cert == null) {
            throw new NullPointerException("cert == null");
        }
        try {
            byte[] encoded = cert.getEncoded();
            if (!this.mKeyStore.put(Credentials.CA_CERTIFICATE + alias, encoded, -1, 0)) {
                throw new KeyStoreException("Couldn't insert certificate; is KeyStore initialized?");
            }
        } catch (CertificateEncodingException e) {
            throw new KeyStoreException(e);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        if ((isKeyEntry(alias) || isCertificateEntry(alias)) && !Credentials.deleteAllTypesForAlias(this.mKeyStore, alias)) {
            throw new KeyStoreException("No such entry " + alias);
        }
    }

    private Set<String> getUniqueAliases() {
        String[] rawAliases = this.mKeyStore.saw("");
        if (rawAliases == null) {
            return new HashSet();
        }
        Set<String> aliases = new HashSet<>(rawAliases.length);
        for (String alias : rawAliases) {
            int idx = alias.indexOf(95);
            if (idx == -1 || alias.length() <= idx) {
                Log.e("AndroidKeyStore", "invalid alias: " + alias);
            } else {
                aliases.add(new String(alias.substring(idx + 1)));
            }
        }
        return aliases;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(getUniqueAliases());
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        return this.mKeyStore.contains(new StringBuilder().append(Credentials.USER_PRIVATE_KEY).append(alias).toString()) || this.mKeyStore.contains(new StringBuilder().append(Credentials.USER_CERTIFICATE).append(alias).toString()) || this.mKeyStore.contains(new StringBuilder().append(Credentials.CA_CERTIFICATE).append(alias).toString());
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return getUniqueAliases().size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        return isKeyEntry(alias);
    }

    private boolean isKeyEntry(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        return this.mKeyStore.contains(Credentials.USER_PRIVATE_KEY + alias);
    }

    private boolean isCertificateEntry(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        return this.mKeyStore.contains(Credentials.CA_CERTIFICATE + alias);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        return !isKeyEntry(alias) && isCertificateEntry(alias);
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate cert) {
        if (cert == null) {
            return null;
        }
        Set<String> nonCaEntries = new HashSet<>();
        String[] certAliases = this.mKeyStore.saw(Credentials.USER_CERTIFICATE);
        if (certAliases != null) {
            for (String alias : certAliases) {
                byte[] certBytes = this.mKeyStore.get(Credentials.USER_CERTIFICATE + alias);
                if (certBytes != null) {
                    Certificate c = toCertificate(certBytes);
                    nonCaEntries.add(alias);
                    if (cert.equals(c)) {
                        return alias;
                    }
                }
            }
        }
        String[] caAliases = this.mKeyStore.saw(Credentials.CA_CERTIFICATE);
        if (certAliases != null) {
            for (String alias2 : caAliases) {
                if (!nonCaEntries.contains(alias2) && this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias2) != null) {
                    Certificate c2 = toCertificate(this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias2));
                    if (cert.equals(c2)) {
                        return alias2;
                    }
                }
            }
            return null;
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new UnsupportedOperationException("Can not serialize AndroidKeyStore to OutputStream");
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (stream != null) {
            throw new IllegalArgumentException("InputStream not supported");
        }
        if (password != null) {
            throw new IllegalArgumentException("password not supported");
        }
        this.mKeyStore = KeyStore.getInstance();
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetEntry(String alias, KeyStore.Entry entry, KeyStore.ProtectionParameter param) throws KeyStoreException {
        if (entry == null) {
            throw new KeyStoreException("entry == null");
        }
        if (engineContainsAlias(alias)) {
            engineDeleteEntry(alias);
        }
        if (entry instanceof KeyStore.TrustedCertificateEntry) {
            KeyStore.TrustedCertificateEntry trE = (KeyStore.TrustedCertificateEntry) entry;
            engineSetCertificateEntry(alias, trE.getTrustedCertificate());
        } else if (param != null && !(param instanceof KeyStoreParameter)) {
            throw new KeyStoreException("protParam should be android.security.KeyStoreParameter; was: " + param.getClass().getName());
        } else {
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                KeyStore.PrivateKeyEntry prE = (KeyStore.PrivateKeyEntry) entry;
                setPrivateKeyEntry(alias, prE.getPrivateKey(), prE.getCertificateChain(), (KeyStoreParameter) param);
                return;
            }
            throw new KeyStoreException("Entry must be a PrivateKeyEntry or TrustedCertificateEntry; was " + entry);
        }
    }
}