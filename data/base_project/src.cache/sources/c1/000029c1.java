package org.apache.harmony.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.pkcs7.ContentInfo;
import org.apache.harmony.security.pkcs7.SignedData;
import org.apache.harmony.security.pkcs7.SignerInfo;
import org.apache.harmony.security.provider.cert.X509CertImpl;
import org.apache.harmony.security.x501.AttributeTypeAndValue;

/* loaded from: JarUtils.class */
public class JarUtils {
    private static final int[] MESSAGE_DIGEST_OID = {1, 2, 840, 113549, 1, 9, 4};

    public static Certificate[] verifySignature(InputStream signature, InputStream signatureBlock) throws IOException, GeneralSecurityException {
        BerInputStream bis = new BerInputStream(signatureBlock);
        ContentInfo info = (ContentInfo) ContentInfo.ASN1.decode(bis);
        SignedData signedData = info.getSignedData();
        if (signedData == null) {
            throw new IOException("No SignedData found");
        }
        Collection<org.apache.harmony.security.x509.Certificate> encCerts = signedData.getCertificates();
        if (encCerts.isEmpty()) {
            return null;
        }
        X509Certificate[] certs = new X509Certificate[encCerts.size()];
        int i = 0;
        for (org.apache.harmony.security.x509.Certificate encCert : encCerts) {
            int i2 = i;
            i++;
            certs[i2] = new X509CertImpl(encCert);
        }
        List<SignerInfo> sigInfos = signedData.getSignerInfos();
        if (!sigInfos.isEmpty()) {
            SignerInfo sigInfo = sigInfos.get(0);
            X500Principal issuer = sigInfo.getIssuer();
            BigInteger snum = sigInfo.getSerialNumber();
            int issuerSertIndex = 0;
            int i3 = 0;
            while (true) {
                if (i3 >= certs.length) {
                    break;
                } else if (!issuer.equals(certs[i3].getIssuerDN()) || !snum.equals(certs[i3].getSerialNumber())) {
                    i3++;
                } else {
                    issuerSertIndex = i3;
                    break;
                }
            }
            if (i3 == certs.length) {
                return null;
            }
            if (certs[issuerSertIndex].hasUnsupportedCriticalExtension()) {
                throw new SecurityException("Can not recognize a critical extension");
            }
            String daOid = sigInfo.getDigestAlgorithm();
            String daName = sigInfo.getDigestAlgorithmName();
            String deaOid = sigInfo.getDigestEncryptionAlgorithm();
            Signature sig = null;
            if (daOid != null && deaOid != null) {
                String alg = daOid + "with" + deaOid;
                try {
                    sig = Signature.getInstance(alg);
                } catch (NoSuchAlgorithmException e) {
                }
                if (sig == null) {
                    String deaName = sigInfo.getDigestEncryptionAlgorithmName();
                    String alg2 = daName + "with" + deaName;
                    try {
                        sig = Signature.getInstance(alg2);
                    } catch (NoSuchAlgorithmException e2) {
                    }
                }
            }
            if (sig == null && daOid != null) {
                try {
                    sig = Signature.getInstance(daOid);
                } catch (NoSuchAlgorithmException e3) {
                }
                if (sig == null && daName != null) {
                    try {
                        sig = Signature.getInstance(daName);
                    } catch (NoSuchAlgorithmException e4) {
                    }
                }
            }
            if (sig == null) {
                return null;
            }
            sig.initVerify(certs[issuerSertIndex]);
            List<AttributeTypeAndValue> atr = sigInfo.getAuthenticatedAttributes();
            byte[] sfBytes = new byte[signature.available()];
            signature.read(sfBytes);
            if (atr == null) {
                sig.update(sfBytes);
            } else {
                sig.update(sigInfo.getEncodedAuthenticatedAttributes());
                byte[] existingDigest = null;
                for (AttributeTypeAndValue a : atr) {
                    if (Arrays.equals(a.getType().getOid(), MESSAGE_DIGEST_OID)) {
                        if (existingDigest != null) {
                            throw new SecurityException("Too many MessageDigest attributes");
                        }
                        Collection<?> entries = a.getValue().getValues(ASN1OctetString.getInstance());
                        if (entries.size() != 1) {
                            throw new SecurityException("Too many values for MessageDigest attribute");
                        }
                        existingDigest = (byte[]) entries.iterator().next();
                    }
                }
                if (existingDigest == null) {
                    throw new SecurityException("Missing MessageDigest in Authenticated Attributes");
                }
                MessageDigest md = null;
                if (daOid != null) {
                    md = MessageDigest.getInstance(daOid);
                }
                if (md == null && daName != null) {
                    md = MessageDigest.getInstance(daName);
                }
                if (md == null) {
                    return null;
                }
                byte[] computedDigest = md.digest(sfBytes);
                if (!Arrays.equals(existingDigest, computedDigest)) {
                    throw new SecurityException("Incorrect MD");
                }
            }
            if (!sig.verify(sigInfo.getEncryptedDigest())) {
                throw new SecurityException("Incorrect signature");
            }
            return createChain(certs[issuerSertIndex], certs);
        }
        return null;
    }

    private static X509Certificate[] createChain(X509Certificate signer, X509Certificate[] candidates) {
        LinkedList chain = new LinkedList();
        chain.add(0, signer);
        if (signer.getSubjectDN().equals(signer.getIssuerDN())) {
            return (X509Certificate[]) chain.toArray(new X509Certificate[1]);
        }
        Principal issuer = signer.getIssuerDN();
        int count = 1;
        while (true) {
            X509Certificate issuerCert = findCert(issuer, candidates);
            if (issuerCert == null) {
                break;
            }
            chain.add(issuerCert);
            count++;
            if (issuerCert.getSubjectDN().equals(issuerCert.getIssuerDN())) {
                break;
            }
            issuer = issuerCert.getIssuerDN();
        }
        return (X509Certificate[]) chain.toArray(new X509Certificate[count]);
    }

    private static X509Certificate findCert(Principal issuer, X509Certificate[] candidates) {
        for (int i = 0; i < candidates.length; i++) {
            if (issuer.equals(candidates[i].getSubjectDN())) {
                return candidates[i];
            }
        }
        return null;
    }
}