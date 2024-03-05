package org.apache.harmony.security.provider.crypto;

import java.security.DigestException;
import java.security.MessageDigestSpi;
import java.util.Arrays;

/* loaded from: SHA1_MessageDigestImpl.class */
public class SHA1_MessageDigestImpl extends MessageDigestSpi implements Cloneable {
    private int[] buffer = new int[87];
    private byte[] oneByte = new byte[1];
    private long messageLength;

    public SHA1_MessageDigestImpl() {
        engineReset();
    }

    private void processDigest(byte[] digest, int offset) {
        long nBits = this.messageLength << 3;
        engineUpdate(Byte.MIN_VALUE);
        int i = 0;
        int lastWord = (this.buffer[81] + 3) >> 2;
        if (this.buffer[81] != 0) {
            if (lastWord < 15) {
                i = lastWord;
            } else {
                if (lastWord == 15) {
                    this.buffer[15] = 0;
                }
                SHA1Impl.computeHash(this.buffer);
                i = 0;
            }
        }
        Arrays.fill(this.buffer, i, 14, 0);
        this.buffer[14] = (int) (nBits >>> 32);
        this.buffer[15] = (int) (nBits & (-1));
        SHA1Impl.computeHash(this.buffer);
        int j = offset;
        for (int i2 = 82; i2 < 87; i2++) {
            int k = this.buffer[i2];
            digest[j] = (byte) (k >>> 24);
            digest[j + 1] = (byte) (k >>> 16);
            digest[j + 2] = (byte) (k >>> 8);
            digest[j + 3] = (byte) k;
            j += 4;
        }
        engineReset();
    }

    @Override // java.security.MessageDigestSpi
    public Object clone() throws CloneNotSupportedException {
        SHA1_MessageDigestImpl cloneObj = (SHA1_MessageDigestImpl) super.clone();
        cloneObj.buffer = (int[]) this.buffer.clone();
        cloneObj.oneByte = (byte[]) this.oneByte.clone();
        return cloneObj;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.MessageDigestSpi
    public byte[] engineDigest() {
        byte[] hash = new byte[20];
        processDigest(hash, 0);
        return hash;
    }

    @Override // java.security.MessageDigestSpi
    protected int engineDigest(byte[] buf, int offset, int len) throws DigestException {
        if (buf == null) {
            throw new IllegalArgumentException("buf == null");
        }
        if (offset > buf.length || len > buf.length || len + offset > buf.length) {
            throw new IllegalArgumentException();
        }
        if (len < 20) {
            throw new DigestException("len < DIGEST_LENGTH");
        }
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        processDigest(buf, offset);
        return 20;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.MessageDigestSpi
    public int engineGetDigestLength() {
        return 20;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.MessageDigestSpi
    public void engineReset() {
        this.messageLength = 0L;
        this.buffer[81] = 0;
        this.buffer[82] = 1732584193;
        this.buffer[83] = -271733879;
        this.buffer[84] = -1732584194;
        this.buffer[85] = 271733878;
        this.buffer[86] = -1009589776;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte input) {
        this.oneByte[0] = input;
        SHA1Impl.updateHash(this.buffer, this.oneByte, 0, 0);
        this.messageLength++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte[] input, int offset, int len) {
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        }
        if (len <= 0) {
            return;
        }
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        if (offset > input.length || len > input.length || len + offset > input.length) {
            throw new IllegalArgumentException();
        }
        SHA1Impl.updateHash(this.buffer, input, offset, (offset + len) - 1);
        this.messageLength += len;
    }
}