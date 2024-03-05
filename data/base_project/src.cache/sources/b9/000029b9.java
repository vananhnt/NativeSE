package org.apache.harmony.security.provider.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandomSpi;
import libcore.io.Streams;
import libcore.util.EmptyArray;

/* loaded from: SHA1PRNG_SecureRandomImpl.class */
public class SHA1PRNG_SecureRandomImpl extends SecureRandomSpi implements Serializable {
    private static final long serialVersionUID = 283736797212159675L;
    private static FileInputStream devURandom;
    private static final int[] END_FLAGS;
    private static final int[] RIGHT1;
    private static final int[] RIGHT2;
    private static final int[] LEFT;
    private static final int[] MASK;
    private static final int HASHBYTES_TO_USE = 20;
    private static final int FRAME_LENGTH = 16;
    private static final int COUNTER_BASE = 0;
    private static final int HASHCOPY_OFFSET = 0;
    private static final int EXTRAFRAME_OFFSET = 5;
    private static final int FRAME_OFFSET = 21;
    private static final int MAX_BYTES = 48;
    private static final int UNDEFINED = 0;
    private static final int SET_SEED = 1;
    private static final int NEXT_BYTES = 2;
    private static SHA1PRNG_SecureRandomImpl myRandom;
    private transient int[] seed = new int[87];
    private transient long seedLength;
    private transient int[] copies;
    private transient byte[] nextBytes;
    private transient int nextBIndex;
    private transient long counter;
    private transient int state;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl.getRandomBytes(int):byte[], file: SHA1PRNG_SecureRandomImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private static byte[] getRandomBytes(int r0) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl.getRandomBytes(int):byte[], file: SHA1PRNG_SecureRandomImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl.getRandomBytes(int):byte[]");
    }

    static {
        try {
            devURandom = new FileInputStream(new File("/dev/urandom"));
            END_FLAGS = new int[]{Integer.MIN_VALUE, 8388608, 32768, 128};
            RIGHT1 = new int[]{0, 40, 48, 56};
            RIGHT2 = new int[]{0, 8, 16, 24};
            LEFT = new int[]{0, 24, 16, 8};
            MASK = new int[]{-1, 16777215, 65535, 255};
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public SHA1PRNG_SecureRandomImpl() {
        this.seed[82] = 1732584193;
        this.seed[83] = -271733879;
        this.seed[84] = -1732584194;
        this.seed[85] = 271733878;
        this.seed[86] = -1009589776;
        this.seedLength = 0L;
        this.copies = new int[37];
        this.nextBytes = new byte[20];
        this.nextBIndex = 20;
        this.counter = 0L;
        this.state = 0;
    }

    private void updateSeed(byte[] bytes) {
        SHA1Impl.updateHash(this.seed, bytes, 0, bytes.length - 1);
        this.seedLength += bytes.length;
    }

    @Override // java.security.SecureRandomSpi
    protected synchronized void engineSetSeed(byte[] seed) {
        if (seed == null) {
            throw new NullPointerException("seed == null");
        }
        if (this.state == 2) {
            System.arraycopy(this.copies, 0, this.seed, 82, 5);
        }
        this.state = 1;
        if (seed.length != 0) {
            updateSeed(seed);
        }
    }

    @Override // java.security.SecureRandomSpi
    protected synchronized byte[] engineGenerateSeed(int numBytes) {
        if (numBytes < 0) {
            throw new NegativeArraySizeException(Integer.toString(numBytes));
        }
        if (numBytes == 0) {
            return EmptyArray.BYTE;
        }
        if (myRandom == null) {
            myRandom = new SHA1PRNG_SecureRandomImpl();
            myRandom.engineSetSeed(getRandomBytes(20));
        }
        byte[] myBytes = new byte[numBytes];
        myRandom.engineNextBytes(myBytes);
        return myBytes;
    }

    @Override // java.security.SecureRandomSpi
    protected synchronized void engineNextBytes(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        int lastWord = this.seed[81] == 0 ? 0 : (this.seed[81] + 7) >> 2;
        if (this.state == 0) {
            updateSeed(getRandomBytes(20));
            this.nextBIndex = 20;
            lastWord = this.seed[81] == 0 ? 0 : (this.seed[81] + 7) >> 2;
        } else if (this.state == 1) {
            System.arraycopy(this.seed, 82, this.copies, 0, 5);
            for (int i = lastWord + 3; i < 18; i++) {
                this.seed[i] = 0;
            }
            long bits = (this.seedLength << 3) + 64;
            if (this.seed[81] < 48) {
                this.seed[14] = (int) (bits >>> 32);
                this.seed[15] = (int) (bits & (-1));
            } else {
                this.copies[19] = (int) (bits >>> 32);
                this.copies[20] = (int) (bits & (-1));
            }
            this.nextBIndex = 20;
        }
        this.state = 2;
        if (bytes.length == 0) {
            return;
        }
        int nextByteToReturn = 0;
        int n = 20 - this.nextBIndex < bytes.length - 0 ? 20 - this.nextBIndex : bytes.length - 0;
        if (n > 0) {
            System.arraycopy(this.nextBytes, this.nextBIndex, bytes, 0, n);
            this.nextBIndex += n;
            nextByteToReturn = 0 + n;
        }
        if (nextByteToReturn >= bytes.length) {
            return;
        }
        int n2 = this.seed[81] & 3;
        do {
            if (n2 == 0) {
                this.seed[lastWord] = (int) (this.counter >>> 32);
                this.seed[lastWord + 1] = (int) (this.counter & (-1));
                this.seed[lastWord + 2] = END_FLAGS[0];
            } else {
                int[] iArr = this.seed;
                int i2 = lastWord;
                iArr[i2] = iArr[i2] | ((int) ((this.counter >>> RIGHT1[n2]) & MASK[n2]));
                this.seed[lastWord + 1] = (int) ((this.counter >>> RIGHT2[n2]) & (-1));
                this.seed[lastWord + 2] = (int) ((this.counter << LEFT[n2]) | END_FLAGS[n2]);
            }
            if (this.seed[81] > 48) {
                this.copies[5] = this.seed[16];
                this.copies[6] = this.seed[17];
            }
            SHA1Impl.computeHash(this.seed);
            if (this.seed[81] > 48) {
                System.arraycopy(this.seed, 0, this.copies, 21, 16);
                System.arraycopy(this.copies, 5, this.seed, 0, 16);
                SHA1Impl.computeHash(this.seed);
                System.arraycopy(this.copies, 21, this.seed, 0, 16);
            }
            this.counter++;
            int j = 0;
            for (int i3 = 0; i3 < 5; i3++) {
                int k = this.seed[82 + i3];
                this.nextBytes[j] = (byte) (k >>> 24);
                this.nextBytes[j + 1] = (byte) (k >>> 16);
                this.nextBytes[j + 2] = (byte) (k >>> 8);
                this.nextBytes[j + 3] = (byte) k;
                j += 4;
            }
            this.nextBIndex = 0;
            int j2 = 20 < bytes.length - nextByteToReturn ? 20 : bytes.length - nextByteToReturn;
            if (j2 > 0) {
                System.arraycopy(this.nextBytes, 0, bytes, nextByteToReturn, j2);
                nextByteToReturn += j2;
                this.nextBIndex += j2;
            }
        } while (nextByteToReturn < bytes.length);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        int[] intData;
        oos.writeLong(this.seedLength);
        oos.writeLong(this.counter);
        oos.writeInt(this.state);
        oos.writeInt(this.seed[81]);
        int nRemaining = (this.seed[81] + 3) >> 2;
        if (this.state != 2) {
            intData = new int[5 + nRemaining];
            System.arraycopy(this.seed, 0, intData, 0, nRemaining);
            System.arraycopy(this.seed, 82, intData, nRemaining, 5);
        } else {
            int offset = 0;
            if (this.seed[81] < 48) {
                intData = new int[26 + nRemaining];
            } else {
                intData = new int[42 + nRemaining];
                intData[0] = this.seed[16];
                intData[0 + 1] = this.seed[17];
                intData[0 + 2] = this.seed[30];
                intData[0 + 3] = this.seed[31];
                offset = 0 + 4;
            }
            System.arraycopy(this.seed, 0, intData, offset, 16);
            int offset2 = offset + 16;
            System.arraycopy(this.copies, 21, intData, offset2, nRemaining);
            int offset3 = offset2 + nRemaining;
            System.arraycopy(this.copies, 0, intData, offset3, 5);
            System.arraycopy(this.seed, 82, intData, offset3 + 5, 5);
        }
        for (int i : intData) {
            oos.writeInt(i);
        }
        oos.writeInt(this.nextBIndex);
        oos.write(this.nextBytes, this.nextBIndex, 20 - this.nextBIndex);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        this.seed = new int[87];
        this.copies = new int[37];
        this.nextBytes = new byte[20];
        this.seedLength = ois.readLong();
        this.counter = ois.readLong();
        this.state = ois.readInt();
        this.seed[81] = ois.readInt();
        int nRemaining = (this.seed[81] + 3) >> 2;
        if (this.state != 2) {
            for (int i = 0; i < nRemaining; i++) {
                this.seed[i] = ois.readInt();
            }
            for (int i2 = 0; i2 < 5; i2++) {
                this.seed[82 + i2] = ois.readInt();
            }
        } else {
            if (this.seed[81] >= 48) {
                this.seed[16] = ois.readInt();
                this.seed[17] = ois.readInt();
                this.seed[30] = ois.readInt();
                this.seed[31] = ois.readInt();
            }
            for (int i3 = 0; i3 < 16; i3++) {
                this.seed[i3] = ois.readInt();
            }
            for (int i4 = 0; i4 < nRemaining; i4++) {
                this.copies[21 + i4] = ois.readInt();
            }
            for (int i5 = 0; i5 < 5; i5++) {
                this.copies[i5] = ois.readInt();
            }
            for (int i6 = 0; i6 < 5; i6++) {
                this.seed[82 + i6] = ois.readInt();
            }
        }
        this.nextBIndex = ois.readInt();
        Streams.readFully(ois, this.nextBytes, this.nextBIndex, 20 - this.nextBIndex);
    }
}