package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.security.Credentials;
import android.security.KeyChain;
import android.security.KeyStore;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/* loaded from: WifiEnterpriseConfig.class */
public class WifiEnterpriseConfig implements Parcelable {
    private static final String TAG = "WifiEnterpriseConfig";
    private static final boolean DBG = false;
    private static final String OLD_PRIVATE_KEY_NAME = "private_key";
    private static final String ENGINE_ID_KEYSTORE = "keystore";
    private static final String KEYSTORE_URI = "keystore://";
    private static final String ENGINE_ENABLE = "1";
    private static final String ENGINE_DISABLE = "0";
    private static final String CA_CERT_PREFIX = "keystore://CACERT_";
    private static final String CLIENT_CERT_PREFIX = "keystore://USRCERT_";
    private static final String EAP_KEY = "eap";
    private static final String PHASE2_KEY = "phase2";
    private static final String IDENTITY_KEY = "identity";
    private static final String ANON_IDENTITY_KEY = "anonymous_identity";
    private static final String PASSWORD_KEY = "password";
    private static final String CLIENT_CERT_KEY = "client_cert";
    private static final String CA_CERT_KEY = "ca_cert";
    private static final String SUBJECT_MATCH_KEY = "subject_match";
    private static final String ENGINE_KEY = "engine";
    private static final String ENGINE_ID_KEY = "engine_id";
    private static final String PRIVATE_KEY_ID_KEY = "key_id";
    private static final String OPP_KEY_CACHING = "proactive_key_caching";
    private X509Certificate mCaCert;
    private PrivateKey mClientPrivateKey;
    private X509Certificate mClientCertificate;
    static final String EMPTY_VALUE = "NULL";
    public static final Parcelable.Creator<WifiEnterpriseConfig> CREATOR = new Parcelable.Creator<WifiEnterpriseConfig>() { // from class: android.net.wifi.WifiEnterpriseConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiEnterpriseConfig createFromParcel(Parcel in) {
            WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                String key = in.readString();
                String value = in.readString();
                enterpriseConfig.mFields.put(key, value);
            }
            enterpriseConfig.mCaCert = readCertificate(in);
            PrivateKey userKey = null;
            int len = in.readInt();
            if (len > 0) {
                try {
                    byte[] bytes = new byte[len];
                    in.readByteArray(bytes);
                    String algorithm = in.readString();
                    KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
                    userKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
                } catch (NoSuchAlgorithmException e) {
                    userKey = null;
                } catch (InvalidKeySpecException e2) {
                    userKey = null;
                }
            }
            enterpriseConfig.mClientPrivateKey = userKey;
            enterpriseConfig.mClientCertificate = readCertificate(in);
            return enterpriseConfig;
        }

        private X509Certificate readCertificate(Parcel in) {
            X509Certificate cert = null;
            int len = in.readInt();
            if (len > 0) {
                try {
                    byte[] bytes = new byte[len];
                    in.readByteArray(bytes);
                    CertificateFactory cFactory = CertificateFactory.getInstance("X.509");
                    cert = (X509Certificate) cFactory.generateCertificate(new ByteArrayInputStream(bytes));
                } catch (CertificateException e) {
                    cert = null;
                }
            }
            return cert;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiEnterpriseConfig[] newArray(int size) {
            return new WifiEnterpriseConfig[size];
        }
    };
    private HashMap<String, String> mFields = new HashMap<>();
    private boolean mNeedsSoftwareKeystore = false;

    public WifiEnterpriseConfig() {
    }

    public WifiEnterpriseConfig(WifiEnterpriseConfig source) {
        for (String key : source.mFields.keySet()) {
            this.mFields.put(key, source.mFields.get(key));
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mFields.size());
        for (Map.Entry<String, String> entry : this.mFields.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        writeCertificate(dest, this.mCaCert);
        if (this.mClientPrivateKey != null) {
            String algorithm = this.mClientPrivateKey.getAlgorithm();
            byte[] userKeyBytes = this.mClientPrivateKey.getEncoded();
            dest.writeInt(userKeyBytes.length);
            dest.writeByteArray(userKeyBytes);
            dest.writeString(algorithm);
        } else {
            dest.writeInt(0);
        }
        writeCertificate(dest, this.mClientCertificate);
    }

    private void writeCertificate(Parcel dest, X509Certificate cert) {
        if (cert != null) {
            try {
                byte[] certBytes = cert.getEncoded();
                dest.writeInt(certBytes.length);
                dest.writeByteArray(certBytes);
                return;
            } catch (CertificateEncodingException e) {
                dest.writeInt(0);
                return;
            }
        }
        dest.writeInt(0);
    }

    /* loaded from: WifiEnterpriseConfig$Eap.class */
    public static final class Eap {
        public static final int NONE = -1;
        public static final int PEAP = 0;
        public static final int TLS = 1;
        public static final int TTLS = 2;
        public static final int PWD = 3;
        public static final String[] strings = {"PEAP", "TLS", "TTLS", "PWD"};

        private Eap() {
        }
    }

    /* loaded from: WifiEnterpriseConfig$Phase2.class */
    public static final class Phase2 {
        public static final int NONE = 0;
        public static final int PAP = 1;
        public static final int MSCHAP = 2;
        public static final int MSCHAPV2 = 3;
        public static final int GTC = 4;
        private static final String PREFIX = "auth=";
        public static final String[] strings = {WifiEnterpriseConfig.EMPTY_VALUE, "PAP", "MSCHAP", "MSCHAPV2", "GTC"};

        private Phase2() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashMap<String, String> getFields() {
        return this.mFields;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] getSupplicantKeys() {
        return new String[]{EAP_KEY, PHASE2_KEY, "identity", ANON_IDENTITY_KEY, "password", CLIENT_CERT_KEY, CA_CERT_KEY, SUBJECT_MATCH_KEY, "engine", ENGINE_ID_KEY, PRIVATE_KEY_ID_KEY};
    }

    public void setEapMethod(int eapMethod) {
        switch (eapMethod) {
            case 0:
            case 1:
            case 2:
            case 3:
                this.mFields.put(EAP_KEY, Eap.strings[eapMethod]);
                this.mFields.put(OPP_KEY_CACHING, ENGINE_ENABLE);
                return;
            default:
                throw new IllegalArgumentException("Unknown EAP method");
        }
    }

    public int getEapMethod() {
        String eapMethod = this.mFields.get(EAP_KEY);
        return getStringIndex(Eap.strings, eapMethod, -1);
    }

    public void setPhase2Method(int phase2Method) {
        switch (phase2Method) {
            case 0:
                this.mFields.put(PHASE2_KEY, EMPTY_VALUE);
                return;
            case 1:
            case 2:
            case 3:
            case 4:
                this.mFields.put(PHASE2_KEY, convertToQuotedString("auth=" + Phase2.strings[phase2Method]));
                return;
            default:
                throw new IllegalArgumentException("Unknown Phase 2 method");
        }
    }

    public int getPhase2Method() {
        String phase2Method = removeDoubleQuotes(this.mFields.get(PHASE2_KEY));
        if (phase2Method.startsWith("auth=")) {
            phase2Method = phase2Method.substring("auth=".length());
        }
        return getStringIndex(Phase2.strings, phase2Method, 0);
    }

    public void setIdentity(String identity) {
        setFieldValue("identity", identity, "");
    }

    public String getIdentity() {
        return getFieldValue("identity", "");
    }

    public void setAnonymousIdentity(String anonymousIdentity) {
        setFieldValue(ANON_IDENTITY_KEY, anonymousIdentity, "");
    }

    public String getAnonymousIdentity() {
        return getFieldValue(ANON_IDENTITY_KEY, "");
    }

    public void setPassword(String password) {
        setFieldValue("password", password, "");
    }

    public String getPassword() {
        return getFieldValue("password", "");
    }

    public void setCaCertificateAlias(String alias) {
        setFieldValue(CA_CERT_KEY, alias, CA_CERT_PREFIX);
    }

    public String getCaCertificateAlias() {
        return getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
    }

    public void setCaCertificate(X509Certificate cert) {
        if (cert != null) {
            if (cert.getBasicConstraints() >= 0) {
                this.mCaCert = cert;
                return;
            }
            throw new IllegalArgumentException("Not a CA certificate");
        }
        this.mCaCert = null;
    }

    public X509Certificate getCaCertificate() {
        return this.mCaCert;
    }

    public void setClientCertificateAlias(String alias) {
        setFieldValue(CLIENT_CERT_KEY, alias, CLIENT_CERT_PREFIX);
        setFieldValue(PRIVATE_KEY_ID_KEY, alias, Credentials.USER_PRIVATE_KEY);
        if (TextUtils.isEmpty(alias)) {
            this.mFields.put("engine", ENGINE_DISABLE);
            this.mFields.put(ENGINE_ID_KEY, EMPTY_VALUE);
            return;
        }
        this.mFields.put("engine", ENGINE_ENABLE);
        this.mFields.put(ENGINE_ID_KEY, convertToQuotedString(ENGINE_ID_KEYSTORE));
    }

    public String getClientCertificateAlias() {
        return getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
    }

    public void setClientKeyEntry(PrivateKey privateKey, X509Certificate clientCertificate) {
        if (clientCertificate != null) {
            if (clientCertificate.getBasicConstraints() != -1) {
                throw new IllegalArgumentException("Cannot be a CA certificate");
            }
            if (privateKey == null) {
                throw new IllegalArgumentException("Client cert without a private key");
            }
            if (privateKey.getEncoded() == null) {
                throw new IllegalArgumentException("Private key cannot be encoded");
            }
        }
        this.mClientPrivateKey = privateKey;
        this.mClientCertificate = clientCertificate;
    }

    public X509Certificate getClientCertificate() {
        return this.mClientCertificate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needsKeyStore() {
        return (this.mClientCertificate == null && this.mCaCert == null) ? false : true;
    }

    static boolean isHardwareBackedKey(PrivateKey key) {
        return KeyChain.isBoundKeyAlgorithm(key.getAlgorithm());
    }

    static boolean hasHardwareBackedKey(Certificate certificate) {
        return KeyChain.isBoundKeyAlgorithm(certificate.getPublicKey().getAlgorithm());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needsSoftwareBackedKeyStore() {
        return this.mNeedsSoftwareKeystore;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean installKeys(KeyStore keyStore, String name) {
        boolean ret;
        boolean ret2 = true;
        String privKeyName = Credentials.USER_PRIVATE_KEY + name;
        String userCertName = Credentials.USER_CERTIFICATE + name;
        String caCertName = Credentials.CA_CERTIFICATE + name;
        if (this.mClientCertificate != null) {
            byte[] privKeyData = this.mClientPrivateKey.getEncoded();
            if (isHardwareBackedKey(this.mClientPrivateKey)) {
                ret = keyStore.importKey(privKeyName, privKeyData, 1010, 0);
            } else {
                ret = keyStore.importKey(privKeyName, privKeyData, 1010, 1);
                this.mNeedsSoftwareKeystore = true;
            }
            if (!ret) {
                return ret;
            }
            ret2 = putCertInKeyStore(keyStore, userCertName, this.mClientCertificate);
            if (!ret2) {
                keyStore.delKey(privKeyName, 1010);
                return ret2;
            }
        }
        if (this.mCaCert != null) {
            ret2 = putCertInKeyStore(keyStore, caCertName, this.mCaCert);
            if (!ret2) {
                if (this.mClientCertificate != null) {
                    keyStore.delKey(privKeyName, 1010);
                    keyStore.delete(userCertName, 1010);
                }
                return ret2;
            }
        }
        if (this.mClientCertificate != null) {
            setClientCertificateAlias(name);
            this.mClientPrivateKey = null;
            this.mClientCertificate = null;
        }
        if (this.mCaCert != null) {
            setCaCertificateAlias(name);
            this.mCaCert = null;
        }
        return ret2;
    }

    private boolean putCertInKeyStore(KeyStore keyStore, String name, Certificate cert) {
        try {
            byte[] certData = Credentials.convertToPem(cert);
            return keyStore.put(name, certData, 1010, 0);
        } catch (IOException e) {
            return false;
        } catch (CertificateException e2) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeKeys(KeyStore keyStore) {
        String client = getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
        if (!TextUtils.isEmpty(client)) {
            keyStore.delKey(Credentials.USER_PRIVATE_KEY + client, 1010);
            keyStore.delete(Credentials.USER_CERTIFICATE + client, 1010);
        }
        String ca = getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
        if (!TextUtils.isEmpty(ca)) {
            keyStore.delete(Credentials.CA_CERTIFICATE + ca, 1010);
        }
    }

    public void setSubjectMatch(String subjectMatch) {
        setFieldValue(SUBJECT_MATCH_KEY, subjectMatch, "");
    }

    public String getSubjectMatch() {
        return getFieldValue(SUBJECT_MATCH_KEY, "");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getKeyId(WifiEnterpriseConfig current) {
        String eap = this.mFields.get(EAP_KEY);
        String phase2 = this.mFields.get(PHASE2_KEY);
        if (TextUtils.isEmpty(eap)) {
            eap = current.mFields.get(EAP_KEY);
        }
        if (TextUtils.isEmpty(phase2)) {
            phase2 = current.mFields.get(PHASE2_KEY);
        }
        return eap + "_" + phase2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean migrateOldEapTlsNative(WifiNative wifiNative, int netId) {
        String keyName;
        String oldPrivateKey = wifiNative.getNetworkVariable(netId, OLD_PRIVATE_KEY_NAME);
        if (TextUtils.isEmpty(oldPrivateKey)) {
            return false;
        }
        String oldPrivateKey2 = removeDoubleQuotes(oldPrivateKey);
        if (TextUtils.isEmpty(oldPrivateKey2)) {
            return false;
        }
        this.mFields.put("engine", ENGINE_ENABLE);
        this.mFields.put(ENGINE_ID_KEY, convertToQuotedString(ENGINE_ID_KEYSTORE));
        if (oldPrivateKey2.startsWith(KEYSTORE_URI)) {
            keyName = new String(oldPrivateKey2.substring(KEYSTORE_URI.length()));
        } else {
            keyName = oldPrivateKey2;
        }
        this.mFields.put(PRIVATE_KEY_ID_KEY, convertToQuotedString(keyName));
        wifiNative.setNetworkVariable(netId, "engine", this.mFields.get("engine"));
        wifiNative.setNetworkVariable(netId, ENGINE_ID_KEY, this.mFields.get(ENGINE_ID_KEY));
        wifiNative.setNetworkVariable(netId, PRIVATE_KEY_ID_KEY, this.mFields.get(PRIVATE_KEY_ID_KEY));
        wifiNative.setNetworkVariable(netId, OLD_PRIVATE_KEY_NAME, EMPTY_VALUE);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void migrateCerts(KeyStore keyStore) {
        String client = getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
        if (!TextUtils.isEmpty(client) && !keyStore.contains(Credentials.USER_PRIVATE_KEY + client, 1010)) {
            keyStore.duplicate(Credentials.USER_PRIVATE_KEY + client, -1, Credentials.USER_PRIVATE_KEY + client, 1010);
            keyStore.duplicate(Credentials.USER_CERTIFICATE + client, -1, Credentials.USER_CERTIFICATE + client, 1010);
        }
        String ca = getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
        if (!TextUtils.isEmpty(ca) && !keyStore.contains(Credentials.CA_CERTIFICATE + ca, 1010)) {
            keyStore.duplicate(Credentials.CA_CERTIFICATE + ca, -1, Credentials.CA_CERTIFICATE + ca, 1010);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initializeSoftwareKeystoreFlag(KeyStore keyStore) {
        String client = getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
        if (!TextUtils.isEmpty(client)) {
            this.mNeedsSoftwareKeystore = true;
        }
    }

    private String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private String convertToQuotedString(String string) {
        return Separators.DOUBLE_QUOTE + string + Separators.DOUBLE_QUOTE;
    }

    private int getStringIndex(String[] arr, String toBeFound, int defaultIndex) {
        if (TextUtils.isEmpty(toBeFound)) {
            return defaultIndex;
        }
        for (int i = 0; i < arr.length; i++) {
            if (toBeFound.equals(arr[i])) {
                return i;
            }
        }
        return defaultIndex;
    }

    private String getFieldValue(String key, String prefix) {
        String value = this.mFields.get(key);
        if (TextUtils.isEmpty(value) || EMPTY_VALUE.equals(value)) {
            return "";
        }
        String value2 = removeDoubleQuotes(value);
        if (value2.startsWith(prefix)) {
            return value2.substring(prefix.length());
        }
        return value2;
    }

    private void setFieldValue(String key, String value, String prefix) {
        if (TextUtils.isEmpty(value)) {
            this.mFields.put(key, EMPTY_VALUE);
        } else {
            this.mFields.put(key, convertToQuotedString(prefix + value));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String key : this.mFields.keySet()) {
            sb.append(key).append(Separators.SP).append(this.mFields.get(key)).append(Separators.RETURN);
        }
        return sb.toString();
    }
}