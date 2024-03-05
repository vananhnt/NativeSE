package javax.crypto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import libcore.io.Streams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CipherInputStream.class */
public class CipherInputStream extends FilterInputStream {
    private static final int I_BUFFER_SIZE = 20;
    private final Cipher cipher;
    private final byte[] inputBuffer;
    private byte[] outputBuffer;
    private int outputIndex;
    private int outputLength;
    private boolean finished;

    public CipherInputStream(InputStream is, Cipher c) {
        super(is);
        this.inputBuffer = new byte[20];
        this.cipher = c;
    }

    protected CipherInputStream(InputStream is) {
        this(is, new NullCipher());
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.finished) {
            if (this.outputIndex == this.outputLength) {
                return -1;
            }
            byte[] bArr = this.outputBuffer;
            int i = this.outputIndex;
            this.outputIndex = i + 1;
            return bArr[i] & 255;
        } else if (this.outputIndex < this.outputLength) {
            byte[] bArr2 = this.outputBuffer;
            int i2 = this.outputIndex;
            this.outputIndex = i2 + 1;
            return bArr2[i2] & 255;
        } else {
            this.outputIndex = 0;
            this.outputLength = 0;
            while (true) {
                if (this.outputLength != 0) {
                    break;
                }
                int outputSize = this.cipher.getOutputSize(this.inputBuffer.length);
                if (this.outputBuffer == null || this.outputBuffer.length < outputSize) {
                    this.outputBuffer = new byte[outputSize];
                }
                int byteCount = this.in.read(this.inputBuffer);
                if (byteCount == -1) {
                    try {
                        this.outputLength = this.cipher.doFinal(this.outputBuffer, 0);
                        this.finished = true;
                        break;
                    } catch (Exception e) {
                        throw new IOException("Error while finalizing cipher", e);
                    }
                }
                try {
                    this.outputLength = this.cipher.update(this.inputBuffer, 0, byteCount, this.outputBuffer, 0);
                } catch (ShortBufferException e2) {
                    throw new AssertionError(e2);
                }
            }
            return read();
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] buf, int off, int len) throws IOException {
        if (this.in == null) {
            throw new NullPointerException("in == null");
        }
        int i = 0;
        while (i < len) {
            int b = read();
            if (b == -1) {
                if (i == 0) {
                    return -1;
                }
                return i;
            }
            if (buf != null) {
                buf[off + i] = (byte) b;
            }
            i++;
        }
        return i;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long byteCount) throws IOException {
        return Streams.skipByReading(this, byteCount);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return 0;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        this.in.close();
        try {
            this.cipher.doFinal();
        } catch (GeneralSecurityException e) {
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }
}