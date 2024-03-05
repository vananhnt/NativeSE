package org.apache.harmony.security.provider.crypto;

import java.security.Provider;

/* loaded from: CryptoProvider.class */
public final class CryptoProvider extends Provider {
    private static final long serialVersionUID = 7991202868423459598L;

    public CryptoProvider() {
        super("Crypto", 1.0d, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
        put("MessageDigest.SHA-1", "org.apache.harmony.security.provider.crypto.SHA1_MessageDigestImpl");
        put("MessageDigest.SHA-1 ImplementedIn", "Software");
        put("Alg.Alias.MessageDigest.SHA1", "SHA-1");
        put("Alg.Alias.MessageDigest.SHA", "SHA-1");
        put("SecureRandom.SHA1PRNG", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
        put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        put("Signature.SHA1withDSA", "org.apache.harmony.security.provider.crypto.SHA1withDSA_SignatureImpl");
        put("Signature.SHA1withDSA ImplementedIn", "Software");
        put("Alg.Alias.Signature.SHAwithDSA", "SHA1withDSA");
        put("Alg.Alias.Signature.DSAwithSHA1", "SHA1withDSA");
        put("Alg.Alias.Signature.SHA1/DSA", "SHA1withDSA");
        put("Alg.Alias.Signature.SHA/DSA", "SHA1withDSA");
        put("Alg.Alias.Signature.SHA-1/DSA", "SHA1withDSA");
        put("Alg.Alias.Signature.DSA", "SHA1withDSA");
        put("Alg.Alias.Signature.DSS", "SHA1withDSA");
        put("Alg.Alias.Signature.OID.1.2.840.10040.4.3", "SHA1withDSA");
        put("Alg.Alias.Signature.1.2.840.10040.4.3", "SHA1withDSA");
        put("Alg.Alias.Signature.1.3.14.3.2.13", "SHA1withDSA");
        put("Alg.Alias.Signature.1.3.14.3.2.27", "SHA1withDSA");
        put("KeyFactory.DSA", "org.apache.harmony.security.provider.crypto.DSAKeyFactoryImpl");
        put("KeyFactory.DSA ImplementedIn", "Software");
        put("Alg.Alias.KeyFactory.1.3.14.3.2.12", "DSA");
        put("Alg.Alias.KeyFactory.1.2.840.10040.4.1", "DSA");
    }
}