package org.apache.harmony.security.x509;

import java.math.BigInteger;
import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;
import org.apache.harmony.security.x501.Name;

/* loaded from: TBSCertificate.class */
public final class TBSCertificate {
    private final int version;
    private final BigInteger serialNumber;
    private final AlgorithmIdentifier signature;
    private final Name issuer;
    private final Validity validity;
    private final Name subject;
    private final SubjectPublicKeyInfo subjectPublicKeyInfo;
    private final boolean[] issuerUniqueID;
    private final boolean[] subjectUniqueID;
    private final Extensions extensions;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{new ASN1Explicit(0, ASN1Integer.getInstance()), ASN1Integer.getInstance(), AlgorithmIdentifier.ASN1, Name.ASN1, Validity.ASN1, Name.ASN1, SubjectPublicKeyInfo.ASN1, new ASN1Implicit(1, ASN1BitString.getInstance()), new ASN1Implicit(2, ASN1BitString.getInstance()), new ASN1Explicit(3, Extensions.ASN1)}) { // from class: org.apache.harmony.security.x509.TBSCertificate.1
        {
            setDefault(new byte[]{0}, 0);
            setOptional(7);
            setOptional(8);
            setOptional(9);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            boolean[] issuerUniqueID = values[7] == null ? null : ((BitString) values[7]).toBooleanArray();
            boolean[] subjectUniqueID = values[8] == null ? null : ((BitString) values[8]).toBooleanArray();
            return new TBSCertificate(ASN1Integer.toIntValue(values[0]), new BigInteger((byte[]) values[1]), (AlgorithmIdentifier) values[2], (Name) values[3], (Validity) values[4], (Name) values[5], (SubjectPublicKeyInfo) values[6], issuerUniqueID, subjectUniqueID, (Extensions) values[9], in.getEncoded());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            TBSCertificate tbs = (TBSCertificate) object;
            values[0] = ASN1Integer.fromIntValue(tbs.version);
            values[1] = tbs.serialNumber.toByteArray();
            values[2] = tbs.signature;
            values[3] = tbs.issuer;
            values[4] = tbs.validity;
            values[5] = tbs.subject;
            values[6] = tbs.subjectPublicKeyInfo;
            if (tbs.issuerUniqueID != null) {
                values[7] = new BitString(tbs.issuerUniqueID);
            }
            if (tbs.subjectUniqueID != null) {
                values[8] = new BitString(tbs.subjectUniqueID);
            }
            values[9] = tbs.extensions;
        }
    };

    public TBSCertificate(int version, BigInteger serialNumber, AlgorithmIdentifier signature, Name issuer, Validity validity, Name subject, SubjectPublicKeyInfo subjectPublicKeyInfo, boolean[] issuerUniqueID, boolean[] subjectUniqueID, Extensions extensions) {
        this.version = version;
        this.serialNumber = serialNumber;
        this.signature = signature;
        this.issuer = issuer;
        this.validity = validity;
        this.subject = subject;
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.issuerUniqueID = issuerUniqueID;
        this.subjectUniqueID = subjectUniqueID;
        this.extensions = extensions;
    }

    private TBSCertificate(int version, BigInteger serialNumber, AlgorithmIdentifier signature, Name issuer, Validity validity, Name subject, SubjectPublicKeyInfo subjectPublicKeyInfo, boolean[] issuerUniqueID, boolean[] subjectUniqueID, Extensions extensions, byte[] encoding) {
        this(version, serialNumber, signature, issuer, validity, subject, subjectPublicKeyInfo, issuerUniqueID, subjectUniqueID, extensions);
        this.encoding = encoding;
    }

    public int getVersion() {
        return this.version;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public Name getIssuer() {
        return this.issuer;
    }

    public Validity getValidity() {
        return this.validity;
    }

    public Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public boolean[] getIssuerUniqueID() {
        return this.issuerUniqueID;
    }

    public boolean[] getSubjectUniqueID() {
        return this.subjectUniqueID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public void dumpValue(StringBuilder sb) {
        sb.append('[');
        sb.append("\n  Version: V").append(this.version + 1);
        sb.append("\n  Subject: ").append(this.subject.getName("RFC2253"));
        sb.append("\n  Signature Algorithm: ");
        this.signature.dumpValue(sb);
        sb.append("\n  Key: ").append(this.subjectPublicKeyInfo.getPublicKey().toString());
        sb.append("\n  Validity: [From: ").append(this.validity.getNotBefore());
        sb.append("\n               To: ").append(this.validity.getNotAfter()).append(']');
        sb.append("\n  Issuer: ").append(this.issuer.getName("RFC2253"));
        sb.append("\n  Serial Number: ").append(this.serialNumber);
        if (this.issuerUniqueID != null) {
            sb.append("\n  Issuer Id: ");
            boolean[] arr$ = this.issuerUniqueID;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                boolean b = arr$[i$];
                sb.append(b ? '1' : '0');
            }
        }
        if (this.subjectUniqueID != null) {
            sb.append("\n  Subject Id: ");
            boolean[] arr$2 = this.subjectUniqueID;
            int len$2 = arr$2.length;
            for (int i$2 = 0; i$2 < len$2; i$2++) {
                boolean b2 = arr$2[i$2];
                sb.append(b2 ? '1' : '0');
            }
        }
        if (this.extensions != null) {
            sb.append("\n\n  Extensions: ");
            sb.append("[\n");
            this.extensions.dumpValue(sb, "    ");
            sb.append("  ]");
        }
        sb.append("\n]");
    }
}