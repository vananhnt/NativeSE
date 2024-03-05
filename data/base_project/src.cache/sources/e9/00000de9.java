package android.security;

import android.security.KeyStore;
import com.android.org.bouncycastle.x509.X509V3CertificateGenerator;
import com.android.org.conscrypt.OpenSSLEngine;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: AndroidKeyPairGenerator.class */
public class AndroidKeyPairGenerator extends KeyPairGeneratorSpi {
    private KeyStore mKeyStore;
    private KeyPairGeneratorSpec mSpec;

    @Override // java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (this.mKeyStore == null || this.mSpec == null) {
            throw new IllegalStateException("Must call initialize with an android.security.KeyPairGeneratorSpec first");
        }
        if ((this.mSpec.getFlags() & 1) != 0 && this.mKeyStore.state() != KeyStore.State.UNLOCKED) {
            throw new IllegalStateException("Android keystore must be in initialized and unlocked state if encryption is required");
        }
        String alias = this.mSpec.getKeystoreAlias();
        Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
        int keyType = KeyStore.getKeyTypeForAlgorithm(this.mSpec.getKeyType());
        byte[][] args = getArgsForKeyType(keyType, this.mSpec.getAlgorithmParameterSpec());
        String privateKeyAlias = Credentials.USER_PRIVATE_KEY + alias;
        if (!this.mKeyStore.generate(privateKeyAlias, -1, keyType, this.mSpec.getKeySize(), this.mSpec.getFlags(), args)) {
            throw new IllegalStateException("could not generate key in keystore");
        }
        OpenSSLEngine engine = OpenSSLEngine.getInstance("keystore");
        try {
            PrivateKey privKey = engine.getPrivateKeyById(privateKeyAlias);
            byte[] pubKeyBytes = this.mKeyStore.getPubkey(privateKeyAlias);
            try {
                KeyFactory keyFact = KeyFactory.getInstance(this.mSpec.getKeyType());
                PublicKey pubKey = keyFact.generatePublic(new X509EncodedKeySpec(pubKeyBytes));
                X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
                certGen.setPublicKey(pubKey);
                certGen.setSerialNumber(this.mSpec.getSerialNumber());
                certGen.setSubjectDN(this.mSpec.getSubjectDN());
                certGen.setIssuerDN(this.mSpec.getSubjectDN());
                certGen.setNotBefore(this.mSpec.getStartDate());
                certGen.setNotAfter(this.mSpec.getEndDate());
                certGen.setSignatureAlgorithm(getDefaultSignatureAlgorithmForKeyType(this.mSpec.getKeyType()));
                try {
                    X509Certificate cert = certGen.generate(privKey);
                    try {
                        byte[] certBytes = cert.getEncoded();
                        if (!this.mKeyStore.put(Credentials.USER_CERTIFICATE + alias, certBytes, -1, this.mSpec.getFlags())) {
                            Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                            throw new IllegalStateException("Can't store certificate in AndroidKeyStore");
                        }
                        return new KeyPair(pubKey, privKey);
                    } catch (CertificateEncodingException e) {
                        Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                        throw new IllegalStateException("Can't get encoding of certificate", e);
                    }
                } catch (Exception e2) {
                    Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                    throw new IllegalStateException("Can't generate certificate", e2);
                }
            } catch (NoSuchAlgorithmException e3) {
                throw new IllegalStateException("Can't instantiate key generator", e3);
            } catch (InvalidKeySpecException e4) {
                throw new IllegalStateException("keystore returned invalid key encoding", e4);
            }
        } catch (InvalidKeyException e5) {
            throw new RuntimeException("Can't get key", e5);
        }
    }

    private static String getDefaultSignatureAlgorithmForKeyType(String keyType) {
        if ("RSA".equalsIgnoreCase(keyType)) {
            return "sha256WithRSA";
        }
        if ("DSA".equalsIgnoreCase(keyType)) {
            return "sha1WithDSA";
        }
        if ("EC".equalsIgnoreCase(keyType)) {
            return "sha256WithECDSA";
        }
        throw new IllegalArgumentException("Unsupported key type " + keyType);
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [byte[], byte[][]] */
    /* JADX WARN: Type inference failed for: r0v8, types: [byte[], byte[][]] */
    private static byte[][] getArgsForKeyType(int keyType, AlgorithmParameterSpec spec) {
        switch (keyType) {
            case 6:
                if (spec instanceof RSAKeyGenParameterSpec) {
                    RSAKeyGenParameterSpec rsaSpec = (RSAKeyGenParameterSpec) spec;
                    return new byte[]{rsaSpec.getPublicExponent().toByteArray()};
                }
                break;
            case 116:
                if (spec instanceof DSAParameterSpec) {
                    DSAParameterSpec dsaSpec = (DSAParameterSpec) spec;
                    return new byte[]{dsaSpec.getG().toByteArray(), dsaSpec.getP().toByteArray(), dsaSpec.getQ().toByteArray()};
                }
                break;
        }
        return null;
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(int keysize, SecureRandom random) {
        throw new IllegalArgumentException("cannot specify keysize with AndroidKeyPairGenerator");
    }

    @Override // java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("must supply params of type android.security.KeyPairGeneratorSpec");
        }
        if (!(params instanceof KeyPairGeneratorSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type android.security.KeyPairGeneratorSpec");
        }
        KeyPairGeneratorSpec spec = (KeyPairGeneratorSpec) params;
        this.mSpec = spec;
        this.mKeyStore = KeyStore.getInstance();
    }
}