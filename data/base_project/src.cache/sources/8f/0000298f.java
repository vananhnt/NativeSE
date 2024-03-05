package org.apache.harmony.security.asn1;

import libcore.util.Objects;

/* loaded from: DerOutputStream.class */
public final class DerOutputStream extends BerOutputStream {
    private static final int initSize = 32;
    private int index;
    private int[][] len = new int[32];
    private Object[][] val = new Object[32];

    /* JADX WARN: Type inference failed for: r1v1, types: [int[], int[][]] */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.lang.Object[], java.lang.Object[][]] */
    public DerOutputStream(ASN1Type asn1, Object object) {
        this.content = object;
        this.index = -1;
        asn1.setEncodingContent(this);
        this.encoded = new byte[asn1.getEncodedLength(this)];
        this.index = 0;
        asn1.encodeASN(this);
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void encodeChoice(ASN1Choice choice) {
        ASN1Type type = (ASN1Type) this.val[this.index][0];
        this.content = this.val[this.index][1];
        this.index++;
        type.encodeASN(this);
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void encodeExplicit(ASN1Explicit explicit) {
        this.content = this.val[this.index][0];
        this.length = this.len[this.index][0];
        this.index++;
        explicit.type.encodeASN(this);
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void encodeSequence(ASN1Sequence sequence) {
        ASN1Type[] type = sequence.type;
        Object[] values = this.val[this.index];
        int[] compLens = this.len[this.index];
        this.index++;
        for (int i = 0; i < type.length; i++) {
            if (values[i] != null) {
                this.content = values[i];
                this.length = compLens[i];
                type[i].encodeASN(this);
            }
        }
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void encodeSequenceOf(ASN1SequenceOf sequenceOf) {
        encodeValueCollection(sequenceOf);
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void encodeSetOf(ASN1SetOf setOf) {
        encodeValueCollection(setOf);
    }

    private void encodeValueCollection(ASN1ValueCollection collection) {
        Object[] values = this.val[this.index];
        int[] compLens = this.len[this.index];
        this.index++;
        for (int i = 0; i < values.length; i++) {
            this.content = values[i];
            this.length = compLens[i];
            collection.type.encodeASN(this);
        }
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [int[], int[][], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v19, types: [java.lang.Object[], java.lang.Object, java.lang.Object[][]] */
    private void push(int[] lengths, Object[] values) {
        this.index++;
        if (this.index == this.val.length) {
            ?? r0 = new int[this.val.length * 2];
            System.arraycopy(this.len, 0, r0, 0, this.val.length);
            this.len = r0;
            ?? r02 = new Object[this.val.length * 2];
            System.arraycopy(this.val, 0, r02, 0, this.val.length);
            this.val = r02;
        }
        this.len[this.index] = lengths;
        this.val[this.index] = values;
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void getChoiceLength(ASN1Choice choice) {
        int i = choice.getIndex(this.content);
        this.content = choice.getObjectToEncode(this.content);
        Object[] values = {choice.type[i], this.content};
        push(null, values);
        choice.type[i].setEncodingContent(this);
        values[1] = this.content;
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void getExplicitLength(ASN1Explicit explicit) {
        Object[] values = {this.content};
        push(compLens, values);
        explicit.type.setEncodingContent(this);
        values[0] = this.content;
        int[] compLens = {this.length};
        this.length = explicit.type.getEncodedLength(this);
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void getSequenceLength(ASN1Sequence sequence) {
        ASN1Type[] type = sequence.type;
        Object[] values = new Object[type.length];
        int[] compLens = new int[type.length];
        sequence.getValues(this.content, values);
        push(compLens, values);
        int seqLen = 0;
        for (int i = 0; i < type.length; i++) {
            if (values[i] == null) {
                if (!sequence.OPTIONAL[i]) {
                    throw new RuntimeException();
                }
            } else if (Objects.equal(sequence.DEFAULT[i], values[i])) {
                values[i] = null;
            } else {
                this.content = values[i];
                type[i].setEncodingContent(this);
                compLens[i] = this.length;
                values[i] = this.content;
                seqLen += type[i].getEncodedLength(this);
            }
        }
        this.length = seqLen;
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void getSequenceOfLength(ASN1SequenceOf sequence) {
        getValueOfLength(sequence);
    }

    @Override // org.apache.harmony.security.asn1.BerOutputStream
    public void getSetOfLength(ASN1SetOf setOf) {
        getValueOfLength(setOf);
    }

    private void getValueOfLength(ASN1ValueCollection collection) {
        Object[] cv = collection.getValues(this.content).toArray();
        Object[] values = new Object[cv.length];
        int[] compLens = new int[values.length];
        push(compLens, values);
        int seqLen = 0;
        for (int i = 0; i < values.length; i++) {
            this.content = cv[i];
            collection.type.setEncodingContent(this);
            compLens[i] = this.length;
            values[i] = this.content;
            seqLen += collection.type.getEncodedLength(this);
        }
        this.length = seqLen;
    }
}