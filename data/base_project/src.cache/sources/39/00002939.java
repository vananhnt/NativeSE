package org.apache.commons.codec;

import java.util.Comparator;

/* loaded from: StringEncoderComparator.class */
public class StringEncoderComparator implements Comparator {
    private StringEncoder stringEncoder;

    public StringEncoderComparator() {
    }

    public StringEncoderComparator(StringEncoder stringEncoder) {
        this.stringEncoder = stringEncoder;
    }

    @Override // java.util.Comparator
    public int compare(Object o1, Object o2) {
        int compareCode;
        try {
            Comparable s1 = (Comparable) this.stringEncoder.encode(o1);
            Comparable s2 = (Comparable) this.stringEncoder.encode(o2);
            compareCode = s1.compareTo(s2);
        } catch (EncoderException e) {
            compareCode = 0;
        }
        return compareCode;
    }
}