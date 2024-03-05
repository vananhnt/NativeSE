package org.apache.harmony.security.x501;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.harmony.security.utils.ObjectIdentifier;

/* loaded from: AttributeTypeAndValueComparator.class */
public class AttributeTypeAndValueComparator implements Comparator, Serializable {
    private static final long serialVersionUID = -1286471842007103132L;

    @Override // java.util.Comparator
    public int compare(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return 0;
        }
        AttributeTypeAndValue atav1 = (AttributeTypeAndValue) obj1;
        AttributeTypeAndValue atav2 = (AttributeTypeAndValue) obj2;
        String kw1 = atav1.getType().getName();
        String kw2 = atav2.getType().getName();
        if (kw1 != null && kw2 == null) {
            return -1;
        }
        if (kw1 == null && kw2 != null) {
            return 1;
        }
        if (kw1 != null && kw2 != null) {
            return kw1.compareTo(kw2);
        }
        return compateOids(atav1.getType(), atav2.getType());
    }

    private static int compateOids(ObjectIdentifier oid1, ObjectIdentifier oid2) {
        if (oid1 == oid2) {
            return 0;
        }
        int[] ioid1 = oid1.getOid();
        int[] ioid2 = oid2.getOid();
        int min = ioid1.length < ioid2.length ? ioid1.length : ioid2.length;
        for (int i = 0; i < min; i++) {
            if (ioid1[i] < ioid2[i]) {
                return -1;
            }
            if (ioid1[i] > ioid2[i]) {
                return 1;
            }
            if (i + 1 == ioid1.length && i + 1 < ioid2.length) {
                return -1;
            }
            if (i + 1 < ioid1.length && i + 1 == ioid2.length) {
                return 1;
            }
        }
        return 0;
    }
}