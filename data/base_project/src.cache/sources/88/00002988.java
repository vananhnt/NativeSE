package org.apache.harmony.security.asn1;

/* loaded from: ASN1TypeCollection.class */
public abstract class ASN1TypeCollection extends ASN1Constructed {
    public final ASN1Type[] type;
    public final boolean[] OPTIONAL;
    public final Object[] DEFAULT;

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1TypeCollection(int tagNumber, ASN1Type[] type) {
        super(tagNumber);
        this.type = type;
        this.OPTIONAL = new boolean[type.length];
        this.DEFAULT = new Object[type.length];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setOptional(int index) {
        this.OPTIONAL[index] = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setDefault(Object object, int index) {
        this.OPTIONAL[index] = true;
        this.DEFAULT[index] = object;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void getValues(Object object, Object[] values) {
        throw new RuntimeException("ASN.1 type is not designed to be encoded: " + getClass().getName());
    }
}