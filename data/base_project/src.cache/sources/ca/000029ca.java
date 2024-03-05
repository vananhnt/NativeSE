package org.apache.harmony.security.x501;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.DerInputStream;
import org.apache.harmony.security.x509.DNParser;

/* loaded from: Name.class */
public final class Name {
    private volatile byte[] encoded;
    private String rfc1779String;
    private String rfc2253String;
    private String canonicalString;
    private List<List<AttributeTypeAndValue>> rdn;
    public static final ASN1SetOf ASN1_RDN = new ASN1SetOf(AttributeTypeAndValue.ASN1);
    public static final ASN1SequenceOf ASN1 = new ASN1SequenceOf(ASN1_RDN) { // from class: org.apache.harmony.security.x501.Name.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            return new Name((List) in.content);
        }

        @Override // org.apache.harmony.security.asn1.ASN1ValueCollection
        public Collection getValues(Object object) {
            return ((Name) object).rdn;
        }
    };

    public Name(byte[] encoding) throws IOException {
        DerInputStream in = new DerInputStream(encoding);
        if (in.getEndOffset() != encoding.length) {
            throw new IOException("Wrong content length");
        }
        ASN1.decode(in);
        this.rdn = (List) in.content;
    }

    public Name(String name) throws IOException {
        this.rdn = new DNParser(name).parse();
    }

    private Name(List<List<AttributeTypeAndValue>> rdn) {
        this.rdn = rdn;
    }

    public X500Principal getX500Principal() {
        return new X500Principal(getEncoded());
    }

    public String getName(String format) {
        if ("RFC1779".equals(format)) {
            if (this.rfc1779String == null) {
                this.rfc1779String = getName0(format);
            }
            return this.rfc1779String;
        } else if ("RFC2253".equals(format)) {
            if (this.rfc2253String == null) {
                this.rfc2253String = getName0(format);
            }
            return this.rfc2253String;
        } else if ("CANONICAL".equals(format)) {
            if (this.canonicalString == null) {
                this.canonicalString = getName0(format);
            }
            return this.canonicalString;
        } else if ("RFC1779".equalsIgnoreCase(format)) {
            if (this.rfc1779String == null) {
                this.rfc1779String = getName0("RFC1779");
            }
            return this.rfc1779String;
        } else if ("RFC2253".equalsIgnoreCase(format)) {
            if (this.rfc2253String == null) {
                this.rfc2253String = getName0("RFC2253");
            }
            return this.rfc2253String;
        } else if ("CANONICAL".equalsIgnoreCase(format)) {
            if (this.canonicalString == null) {
                this.canonicalString = getName0("CANONICAL");
            }
            return this.canonicalString;
        } else {
            throw new IllegalArgumentException("Illegal format: " + format);
        }
    }

    private String getName0(String format) {
        StringBuilder name = new StringBuilder();
        for (int i = this.rdn.size() - 1; i >= 0; i--) {
            List<AttributeTypeAndValue> atavList = this.rdn.get(i);
            if ("CANONICAL" == format) {
                atavList = new ArrayList<>(atavList);
                Collections.sort(atavList, new AttributeTypeAndValueComparator());
            }
            Iterator<AttributeTypeAndValue> it = atavList.iterator();
            while (it.hasNext()) {
                AttributeTypeAndValue attributeTypeAndValue = it.next();
                attributeTypeAndValue.appendName(format, name);
                if (it.hasNext()) {
                    if ("RFC1779" == format) {
                        name.append(" + ");
                    } else {
                        name.append('+');
                    }
                }
            }
            if (i != 0) {
                name.append(',');
                if (format == "RFC1779") {
                    name.append(' ');
                }
            }
        }
        String sName = name.toString();
        if ("CANONICAL".equals(format)) {
            sName = sName.toLowerCase(Locale.US);
        }
        return sName;
    }

    public byte[] getEncoded() {
        if (this.encoded == null) {
            this.encoded = ASN1.encode(this);
        }
        return this.encoded;
    }
}