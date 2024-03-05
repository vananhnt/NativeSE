package org.apache.harmony.security.asn1;

import java.util.Collection;

/* loaded from: ASN1ValueCollection.class */
public abstract class ASN1ValueCollection extends ASN1Constructed {
    public final ASN1Type type;

    public ASN1ValueCollection(int tagNumber, ASN1Type type) {
        super(tagNumber);
        this.type = type;
    }

    public Collection<?> getValues(Object object) {
        return (Collection) object;
    }
}