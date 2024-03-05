package org.apache.harmony.security.x509;

import java.io.IOException;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.Name;

/* loaded from: CertificateIssuer.class */
public final class CertificateIssuer extends ExtensionValue {
    private X500Principal issuer;
    public static final ASN1Type ASN1 = new ASN1Sequence(new ASN1Type[]{GeneralName.ASN1}) { // from class: org.apache.harmony.security.x509.CertificateIssuer.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            return ((Name) ((GeneralName) ((Object[]) in.content)[0]).getName()).getX500Principal();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            values[0] = object;
        }
    };

    public CertificateIssuer(byte[] encoding) {
        super(encoding);
    }

    public X500Principal getIssuer() throws IOException {
        if (this.issuer == null) {
            this.issuer = (X500Principal) ASN1.decode(getEncoded());
        }
        return this.issuer;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("Certificate Issuer: ");
        if (this.issuer == null) {
            try {
                this.issuer = getIssuer();
            } catch (IOException e) {
                sb.append("Unparseable (incorrect!) extension value:\n");
                super.dumpValue(sb);
            }
        }
        sb.append(this.issuer).append('\n');
    }
}