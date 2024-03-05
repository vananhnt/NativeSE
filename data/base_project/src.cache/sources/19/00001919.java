package com.android.dex;

import com.android.dex.Dex;
import com.android.dex.util.Unsigned;
import gov.nist.core.Separators;

/* loaded from: MethodId.class */
public final class MethodId implements Comparable<MethodId> {
    private final Dex dex;
    private final int declaringClassIndex;
    private final int protoIndex;
    private final int nameIndex;

    public MethodId(Dex dex, int declaringClassIndex, int protoIndex, int nameIndex) {
        this.dex = dex;
        this.declaringClassIndex = declaringClassIndex;
        this.protoIndex = protoIndex;
        this.nameIndex = nameIndex;
    }

    public int getDeclaringClassIndex() {
        return this.declaringClassIndex;
    }

    public int getProtoIndex() {
        return this.protoIndex;
    }

    public int getNameIndex() {
        return this.nameIndex;
    }

    @Override // java.lang.Comparable
    public int compareTo(MethodId other) {
        if (this.declaringClassIndex != other.declaringClassIndex) {
            return Unsigned.compare(this.declaringClassIndex, other.declaringClassIndex);
        }
        if (this.nameIndex != other.nameIndex) {
            return Unsigned.compare(this.nameIndex, other.nameIndex);
        }
        return Unsigned.compare(this.protoIndex, other.protoIndex);
    }

    public void writeTo(Dex.Section out) {
        out.writeUnsignedShort(this.declaringClassIndex);
        out.writeUnsignedShort(this.protoIndex);
        out.writeInt(this.nameIndex);
    }

    public String toString() {
        if (this.dex == null) {
            return this.declaringClassIndex + Separators.SP + this.protoIndex + Separators.SP + this.nameIndex;
        }
        return this.dex.typeNames().get(this.declaringClassIndex) + Separators.DOT + this.dex.strings().get(this.nameIndex) + this.dex.readTypeList(this.dex.protoIds().get(this.protoIndex).getParametersOffset());
    }
}