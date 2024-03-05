package org.apache.harmony.security.pkcs7;

import java.util.List;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x509.AlgorithmIdentifier;
import org.apache.harmony.security.x509.Certificate;
import org.apache.harmony.security.x509.CertificateList;

/* loaded from: SignedData.class */
public final class SignedData {
    private final int version;
    private final List<?> digestAlgorithms;
    private final ContentInfo contentInfo;
    private final List<Certificate> certificates;
    private final List<CertificateList> crls;
    private final List<SignerInfo> signerInfos;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Integer.getInstance(), new ASN1SetOf(AlgorithmIdentifier.ASN1), ContentInfo.ASN1, new ASN1Implicit(0, new ASN1SetOf(Certificate.ASN1)), new ASN1Implicit(1, new ASN1SetOf(CertificateList.ASN1)), new ASN1SetOf(SignerInfo.ASN1)}) { // from class: org.apache.harmony.security.pkcs7.SignedData.1
        {
            setOptional(3);
            setOptional(4);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            SignedData sd = (SignedData) object;
            byte[] bArr = new byte[1];
            bArr[0] = (byte) sd.version;
            values[0] = bArr;
            values[1] = sd.digestAlgorithms;
            values[2] = sd.contentInfo;
            values[3] = sd.certificates;
            values[4] = sd.crls;
            values[5] = sd.signerInfos;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new SignedData(ASN1Integer.toIntValue(values[0]), (List) values[1], (ContentInfo) values[2], (List) values[3], (List) values[4], (List) values[5]);
        }
    };

    private SignedData(int version, List<?> digestAlgorithms, ContentInfo contentInfo, List<Certificate> certificates, List<CertificateList> crls, List<SignerInfo> signerInfos) {
        this.version = version;
        this.digestAlgorithms = digestAlgorithms;
        this.contentInfo = contentInfo;
        this.certificates = certificates;
        this.crls = crls;
        this.signerInfos = signerInfos;
    }

    public List<Certificate> getCertificates() {
        return this.certificates;
    }

    public List<CertificateList> getCRLs() {
        return this.crls;
    }

    public List<SignerInfo> getSignerInfos() {
        return this.signerInfos;
    }

    public int getVersion() {
        return this.version;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("---- SignedData:");
        res.append("\nversion: ");
        res.append(this.version);
        res.append("\ndigestAlgorithms: ");
        res.append(this.digestAlgorithms.toString());
        res.append("\ncontentInfo: ");
        res.append(this.contentInfo.toString());
        res.append("\ncertificates: ");
        if (this.certificates != null) {
            res.append(this.certificates.toString());
        }
        res.append("\ncrls: ");
        if (this.crls != null) {
            res.append(this.crls.toString());
        }
        res.append("\nsignerInfos:\n");
        res.append(this.signerInfos.toString());
        res.append("\n---- SignedData End\n]");
        return res.toString();
    }
}