package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1Oid.class */
public class ASN1Oid extends ASN1Primitive {
    private static final ASN1Oid ASN1 = new ASN1Oid();
    private static final ASN1Oid STRING_OID = new ASN1Oid() { // from class: org.apache.harmony.security.asn1.ASN1Oid.1
        @Override // org.apache.harmony.security.asn1.ASN1Oid, org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            int element;
            StringBuilder buf = new StringBuilder();
            int octet = in.buffer[in.contentOffset];
            int element2 = octet & 127;
            int index = 0;
            while ((octet & 128) != 0) {
                index++;
                octet = in.buffer[in.contentOffset + index];
                element2 = (element2 << 7) | (octet & 127);
            }
            if (element2 > 79) {
                buf.append('2');
                buf.append('.');
                buf.append(element2 - 80);
            } else {
                buf.append(element2 / 40);
                buf.append('.');
                buf.append(element2 % 40);
            }
            for (int j = 2; j < in.oidElement; j++) {
                buf.append('.');
                index++;
                int octet2 = in.buffer[in.contentOffset + index];
                int i = octet2 & 127;
                while (true) {
                    element = i;
                    if ((octet2 & 128) != 0) {
                        index++;
                        octet2 = in.buffer[in.contentOffset + index];
                        i = (element << 7) | (octet2 & 127);
                    }
                }
                buf.append(element);
            }
            return buf.toString();
        }

        @Override // org.apache.harmony.security.asn1.ASN1Oid, org.apache.harmony.security.asn1.ASN1Type
        public void setEncodingContent(BerOutputStream out) {
            out.content = ObjectIdentifier.toIntArray((String) out.content);
            super.setEncodingContent(out);
        }
    };

    public ASN1Oid() {
        super(6);
    }

    public static ASN1Oid getInstance() {
        return ASN1;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readOID();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object getDecodedObject(BerInputStream in) throws IOException {
        int oidElement;
        int[] oid = new int[in.oidElement];
        int id = 1;
        int i = 0;
        while (id < oid.length) {
            int octet = in.buffer[in.contentOffset + i];
            int i2 = octet & 127;
            while (true) {
                oidElement = i2;
                if ((octet & 128) != 0) {
                    i++;
                    octet = in.buffer[in.contentOffset + i];
                    i2 = (oidElement << 7) | (octet & 127);
                }
            }
            oid[id] = oidElement;
            id++;
            i++;
        }
        if (oid[1] > 79) {
            oid[0] = 2;
            oid[1] = oid[1] - 80;
        } else {
            oid[0] = oid[1] / 40;
            oid[1] = oid[1] % 40;
        }
        return oid;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeContent(BerOutputStream out) {
        out.encodeOID();
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void setEncodingContent(BerOutputStream out) {
        int[] oid = (int[]) out.content;
        int length = 0;
        int elem = (oid[0] * 40) + oid[1];
        if (elem == 0) {
            length = 1;
        } else {
            while (elem > 0) {
                length++;
                elem >>= 7;
            }
        }
        for (int i = 2; i < oid.length; i++) {
            if (oid[i] == 0) {
                length++;
            } else {
                int i2 = oid[i];
                while (true) {
                    int elem2 = i2;
                    if (elem2 > 0) {
                        length++;
                        i2 = elem2 >> 7;
                    }
                }
            }
        }
        out.length = length;
    }

    public static ASN1Oid getInstanceForString() {
        return STRING_OID;
    }
}