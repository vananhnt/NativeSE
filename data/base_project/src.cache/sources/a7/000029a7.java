package org.apache.harmony.security.provider.cert;

import java.security.Provider;

/* loaded from: DRLCertFactory.class */
public final class DRLCertFactory extends Provider {
    private static final long serialVersionUID = -7269650779605195879L;

    public DRLCertFactory() {
        super("DRLCertFactory", 1.0d, "ASN.1, DER, PkiPath, PKCS7");
        put("CertificateFactory.X509", "org.apache.harmony.security.provider.cert.X509CertFactoryImpl");
        put("Alg.Alias.CertificateFactory.X.509", "X509");
    }
}