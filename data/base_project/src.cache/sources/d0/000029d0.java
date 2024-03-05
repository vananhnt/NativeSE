package org.apache.harmony.security.x509;

import java.io.IOException;

/* loaded from: AlternativeName.class */
public final class AlternativeName extends ExtensionValue {
    public static final boolean ISSUER = false;
    public static final boolean SUBJECT = true;
    private boolean which;
    private GeneralNames alternativeNames;

    public AlternativeName(boolean which, byte[] encoding) throws IOException {
        super(encoding);
        this.which = which;
        this.alternativeNames = (GeneralNames) GeneralNames.ASN1.decode(encoding);
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = GeneralNames.ASN1.encode(this.alternativeNames);
        }
        return this.encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append(this.which ? "Subject" : "Issuer").append(" Alternative Names [\n");
        this.alternativeNames.dumpValue(sb, prefix + "  ");
        sb.append(prefix).append("]\n");
    }
}