package org.apache.harmony.security.asn1;

/* loaded from: BerOutputStream.class */
public class BerOutputStream {
    public byte[] encoded;
    protected int offset;
    public int length;
    public Object content;

    public final void encodeTag(int tag) {
        byte[] bArr = this.encoded;
        int i = this.offset;
        this.offset = i + 1;
        bArr[i] = (byte) tag;
        if (this.length > 127) {
            byte numOctets = 1;
            for (int eLen = this.length >> 8; eLen > 0; eLen >>= 8) {
                numOctets = (byte) (numOctets + 1);
            }
            this.encoded[this.offset] = (byte) (numOctets | 128);
            this.offset++;
            int eLen2 = this.length;
            int numOffset = (this.offset + numOctets) - 1;
            int i2 = 0;
            while (i2 < numOctets) {
                this.encoded[numOffset - i2] = (byte) eLen2;
                i2++;
                eLen2 >>= 8;
            }
            this.offset += numOctets;
            return;
        }
        byte[] bArr2 = this.encoded;
        int i3 = this.offset;
        this.offset = i3 + 1;
        bArr2[i3] = (byte) this.length;
    }

    public void encodeANY() {
        System.arraycopy(this.content, 0, this.encoded, this.offset, this.length);
        this.offset += this.length;
    }

    public void encodeBitString() {
        BitString bStr = (BitString) this.content;
        this.encoded[this.offset] = (byte) bStr.unusedBits;
        System.arraycopy(bStr.bytes, 0, this.encoded, this.offset + 1, this.length - 1);
        this.offset += this.length;
    }

    public void encodeBoolean() {
        if (((Boolean) this.content).booleanValue()) {
            this.encoded[this.offset] = -1;
        } else {
            this.encoded[this.offset] = 0;
        }
        this.offset++;
    }

    public void encodeChoice(ASN1Choice choice) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void encodeExplicit(ASN1Explicit explicit) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void encodeGeneralizedTime() {
        System.arraycopy(this.content, 0, this.encoded, this.offset, this.length);
        this.offset += this.length;
    }

    public void encodeUTCTime() {
        System.arraycopy(this.content, 0, this.encoded, this.offset, this.length);
        this.offset += this.length;
    }

    public void encodeInteger() {
        System.arraycopy(this.content, 0, this.encoded, this.offset, this.length);
        this.offset += this.length;
    }

    public void encodeOctetString() {
        System.arraycopy(this.content, 0, this.encoded, this.offset, this.length);
        this.offset += this.length;
    }

    public void encodeOID() {
        int[] oid = (int[]) this.content;
        int oidLen = this.length;
        int i = oid.length - 1;
        while (i > 1) {
            int elem = oid[i];
            if (elem > 127) {
                this.encoded[(this.offset + oidLen) - 1] = (byte) (elem & 127);
                while (true) {
                    elem >>= 7;
                    if (elem > 0) {
                        oidLen--;
                        this.encoded[(this.offset + oidLen) - 1] = (byte) (elem | 128);
                    }
                }
            } else {
                this.encoded[(this.offset + oidLen) - 1] = (byte) elem;
            }
            i--;
            oidLen--;
        }
        int elem2 = (oid[0] * 40) + oid[1];
        if (elem2 > 127) {
            this.encoded[(this.offset + oidLen) - 1] = (byte) (elem2 & 127);
            while (true) {
                elem2 >>= 7;
                if (elem2 <= 0) {
                    break;
                }
                oidLen--;
                this.encoded[(this.offset + oidLen) - 1] = (byte) (elem2 | 128);
            }
        } else {
            this.encoded[(this.offset + oidLen) - 1] = (byte) elem2;
        }
        this.offset += this.length;
    }

    public void encodeSequence(ASN1Sequence sequence) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void encodeSequenceOf(ASN1SequenceOf sequenceOf) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void encodeSet(ASN1Set set) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void encodeSetOf(ASN1SetOf setOf) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void encodeString() {
        System.arraycopy(this.content, 0, this.encoded, this.offset, this.length);
        this.offset += this.length;
    }

    public void getChoiceLength(ASN1Choice choice) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void getExplicitLength(ASN1Explicit sequence) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void getSequenceLength(ASN1Sequence sequence) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void getSequenceOfLength(ASN1SequenceOf sequence) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void getSetLength(ASN1Set set) {
        throw new RuntimeException("Is not implemented yet");
    }

    public void getSetOfLength(ASN1SetOf setOf) {
        throw new RuntimeException("Is not implemented yet");
    }
}