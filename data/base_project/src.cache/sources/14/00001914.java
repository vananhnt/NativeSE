package com.android.dex;

import com.android.dex.Dex;
import com.android.dex.util.ByteArrayByteInput;
import com.android.dex.util.ByteInput;
import gov.nist.core.Separators;

/* loaded from: EncodedValue.class */
public final class EncodedValue implements Comparable<EncodedValue> {
    private final byte[] data;

    public EncodedValue(byte[] data) {
        this.data = data;
    }

    public ByteInput asByteInput() {
        return new ByteArrayByteInput(this.data);
    }

    public byte[] getBytes() {
        return this.data;
    }

    public void writeTo(Dex.Section out) {
        out.write(this.data);
    }

    @Override // java.lang.Comparable
    public int compareTo(EncodedValue other) {
        int size = Math.min(this.data.length, other.data.length);
        for (int i = 0; i < size; i++) {
            if (this.data[i] != other.data[i]) {
                return (this.data[i] & 255) - (other.data[i] & 255);
            }
        }
        return this.data.length - other.data.length;
    }

    public String toString() {
        return Integer.toHexString(this.data[0] & 255) + "...(" + this.data.length + Separators.RPAREN;
    }
}