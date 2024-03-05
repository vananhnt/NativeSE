package com.android.internal.util;

import gov.nist.core.Separators;

/* loaded from: BitwiseInputStream.class */
public class BitwiseInputStream {
    private byte[] mBuf;
    private int mPos = 0;
    private int mEnd;

    /* loaded from: BitwiseInputStream$AccessException.class */
    public static class AccessException extends Exception {
        public AccessException(String s) {
            super("BitwiseInputStream access failed: " + s);
        }
    }

    public BitwiseInputStream(byte[] buf) {
        this.mBuf = buf;
        this.mEnd = buf.length << 3;
    }

    public int available() {
        return this.mEnd - this.mPos;
    }

    public int read(int bits) throws AccessException {
        int index = this.mPos >>> 3;
        int offset = (16 - (this.mPos & 7)) - bits;
        if (bits < 0 || bits > 8 || this.mPos + bits > this.mEnd) {
            throw new AccessException("illegal read (pos " + this.mPos + ", end " + this.mEnd + ", bits " + bits + Separators.RPAREN);
        }
        int data = (this.mBuf[index] & 255) << 8;
        if (offset < 8) {
            data |= this.mBuf[index + 1] & 255;
        }
        int data2 = (data >>> offset) & ((-1) >>> (32 - bits));
        this.mPos += bits;
        return data2;
    }

    public byte[] readByteArray(int bits) throws AccessException {
        int bytes = (bits >>> 3) + ((bits & 7) > 0 ? 1 : 0);
        byte[] arr = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            int increment = Math.min(8, bits - (i << 3));
            arr[i] = (byte) (read(increment) << (8 - increment));
        }
        return arr;
    }

    public void skip(int bits) throws AccessException {
        if (this.mPos + bits > this.mEnd) {
            throw new AccessException("illegal skip (pos " + this.mPos + ", end " + this.mEnd + ", bits " + bits + Separators.RPAREN);
        }
        this.mPos += bits;
    }
}