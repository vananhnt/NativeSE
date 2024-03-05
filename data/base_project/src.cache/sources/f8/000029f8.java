package org.apache.harmony.security.x509;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: InfoAccessSyntax.class */
public final class InfoAccessSyntax extends ExtensionValue {
    private final List<?> accessDescriptions;
    public static final ASN1Type ASN1 = new ASN1SequenceOf(AccessDescription.ASN1) { // from class: org.apache.harmony.security.x509.InfoAccessSyntax.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            return new InfoAccessSyntax((List) in.content, in.getEncoded());
        }

        @Override // org.apache.harmony.security.asn1.ASN1ValueCollection
        public Collection getValues(Object object) {
            return ((InfoAccessSyntax) object).accessDescriptions;
        }
    };

    private InfoAccessSyntax(List<?> accessDescriptions, byte[] encoding) throws IOException {
        if (accessDescriptions == null || accessDescriptions.isEmpty()) {
            throw new IOException("AccessDescriptions list is null or empty");
        }
        this.accessDescriptions = accessDescriptions;
        this.encoding = encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public static InfoAccessSyntax decode(byte[] encoding) throws IOException {
        return (InfoAccessSyntax) ASN1.decode(encoding);
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("\n---- InfoAccessSyntax:");
        if (this.accessDescriptions != null) {
            for (Object accessDescription : this.accessDescriptions) {
                res.append('\n');
                res.append(accessDescription);
            }
        }
        res.append("\n---- InfoAccessSyntax END\n");
        return res.toString();
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("AccessDescriptions:\n");
        if (this.accessDescriptions == null || this.accessDescriptions.isEmpty()) {
            sb.append("NULL\n");
            return;
        }
        for (Object accessDescription : this.accessDescriptions) {
            sb.append(accessDescription.toString());
        }
    }
}