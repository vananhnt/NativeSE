package com.android.dex;

import com.android.dex.util.ByteInput;
import com.android.dex.util.ByteOutput;

/* loaded from: Leb128.class */
public final class Leb128 {
    private Leb128() {
    }

    public static int unsignedLeb128Size(int value) {
        int remaining = value >> 7;
        int count = 0;
        while (remaining != 0) {
            remaining >>= 7;
            count++;
        }
        return count + 1;
    }

    public static int signedLeb128Size(int value) {
        int remaining = value >> 7;
        int count = 0;
        boolean hasMore = true;
        int end = (value & Integer.MIN_VALUE) == 0 ? 0 : -1;
        while (hasMore) {
            hasMore = (remaining == end && (remaining & 1) == ((value >> 6) & 1)) ? false : true;
            value = remaining;
            remaining >>= 7;
            count++;
        }
        return count;
    }

    public static int readSignedLeb128(ByteInput in) {
        int cur;
        int result = 0;
        int count = 0;
        int signBits = -1;
        do {
            cur = in.readByte() & 255;
            result |= (cur & 127) << (count * 7);
            signBits <<= 7;
            count++;
            if ((cur & 128) != 128) {
                break;
            }
        } while (count < 5);
        if ((cur & 128) == 128) {
            throw new DexException("invalid LEB128 sequence");
        }
        if (((signBits >> 1) & result) != 0) {
            result |= signBits;
        }
        return result;
    }

    public static int readUnsignedLeb128(ByteInput in) {
        int cur;
        int result = 0;
        int count = 0;
        do {
            cur = in.readByte() & 255;
            result |= (cur & 127) << (count * 7);
            count++;
            if ((cur & 128) != 128) {
                break;
            }
        } while (count < 5);
        if ((cur & 128) == 128) {
            throw new DexException("invalid LEB128 sequence");
        }
        return result;
    }

    public static void writeUnsignedLeb128(ByteOutput out, int value) {
        int i = value;
        while (true) {
            int remaining = i >>> 7;
            if (remaining != 0) {
                out.writeByte((byte) ((value & 127) | 128));
                value = remaining;
                i = remaining;
            } else {
                out.writeByte((byte) (value & 127));
                return;
            }
        }
    }

    public static void writeSignedLeb128(ByteOutput out, int value) {
        int remaining = value >> 7;
        boolean hasMore = true;
        int end = (value & Integer.MIN_VALUE) == 0 ? 0 : -1;
        while (hasMore) {
            hasMore = (remaining == end && (remaining & 1) == ((value >> 6) & 1)) ? false : true;
            out.writeByte((byte) ((value & 127) | (hasMore ? 128 : 0)));
            value = remaining;
            remaining >>= 7;
        }
    }
}