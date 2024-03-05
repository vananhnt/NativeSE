package com.android.dex;

import com.android.dex.util.ByteInput;
import com.android.dex.util.ByteOutput;

/* loaded from: EncodedValueCodec.class */
public final class EncodedValueCodec {
    private EncodedValueCodec() {
    }

    public static void writeSignedIntegralValue(ByteOutput out, int type, long value) {
        int requiredBits = 65 - Long.numberOfLeadingZeros(value ^ (value >> 63));
        int requiredBytes = (requiredBits + 7) >> 3;
        out.writeByte(type | ((requiredBytes - 1) << 5));
        while (requiredBytes > 0) {
            out.writeByte((byte) value);
            value >>= 8;
            requiredBytes--;
        }
    }

    public static void writeUnsignedIntegralValue(ByteOutput out, int type, long value) {
        int requiredBits = 64 - Long.numberOfLeadingZeros(value);
        if (requiredBits == 0) {
            requiredBits = 1;
        }
        int requiredBytes = (requiredBits + 7) >> 3;
        out.writeByte(type | ((requiredBytes - 1) << 5));
        while (requiredBytes > 0) {
            out.writeByte((byte) value);
            value >>= 8;
            requiredBytes--;
        }
    }

    public static void writeRightZeroExtendedValue(ByteOutput out, int type, long value) {
        int requiredBits = 64 - Long.numberOfTrailingZeros(value);
        if (requiredBits == 0) {
            requiredBits = 1;
        }
        int requiredBytes = (requiredBits + 7) >> 3;
        long value2 = value >> (64 - (requiredBytes * 8));
        out.writeByte(type | ((requiredBytes - 1) << 5));
        while (requiredBytes > 0) {
            out.writeByte((byte) value2);
            value2 >>= 8;
            requiredBytes--;
        }
    }

    public static int readSignedInt(ByteInput in, int zwidth) {
        int result = 0;
        for (int i = zwidth; i >= 0; i--) {
            result = (result >>> 8) | ((in.readByte() & 255) << 24);
        }
        return result >> ((3 - zwidth) * 8);
    }

    public static int readUnsignedInt(ByteInput in, int zwidth, boolean fillOnRight) {
        int result = 0;
        if (!fillOnRight) {
            for (int i = zwidth; i >= 0; i--) {
                result = (result >>> 8) | ((in.readByte() & 255) << 24);
            }
            result >>>= (3 - zwidth) * 8;
        } else {
            for (int i2 = zwidth; i2 >= 0; i2--) {
                result = (result >>> 8) | ((in.readByte() & 255) << 24);
            }
        }
        return result;
    }

    public static long readSignedLong(ByteInput in, int zwidth) {
        long result = 0;
        for (int i = zwidth; i >= 0; i--) {
            result = (result >>> 8) | ((in.readByte() & 255) << 56);
        }
        return result >> ((7 - zwidth) * 8);
    }

    public static long readUnsignedLong(ByteInput in, int zwidth, boolean fillOnRight) {
        long result = 0;
        if (!fillOnRight) {
            for (int i = zwidth; i >= 0; i--) {
                result = (result >>> 8) | ((in.readByte() & 255) << 56);
            }
            result >>>= (7 - zwidth) * 8;
        } else {
            for (int i2 = zwidth; i2 >= 0; i2--) {
                result = (result >>> 8) | ((in.readByte() & 255) << 56);
            }
        }
        return result;
    }
}