package java.util.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Iterator;
import libcore.io.Streams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ZipFile.class */
public class ZipFile {
    public static final int OPEN_READ = 1;
    public static final int OPEN_DELETE = 4;

    public ZipFile(File file) throws ZipException, IOException {
        throw new RuntimeException("Stub!");
    }

    public ZipFile(String name) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ZipFile(File file, int mode) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Enumeration<? extends ZipEntry> entries() {
        throw new RuntimeException("Stub!");
    }

    public ZipEntry getEntry(String entryName) {
        throw new RuntimeException("Stub!");
    }

    public InputStream getInputStream(ZipEntry entry) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public int size() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.zip.ZipFile$1  reason: invalid class name */
    /* loaded from: ZipFile$1.class */
    class AnonymousClass1 implements Enumeration<ZipEntry> {
        final /* synthetic */ Iterator val$iterator;

        AnonymousClass1(Iterator it) {
            this.val$iterator = it;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            ZipFile.access$000(ZipFile.this);
            return this.val$iterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public ZipEntry nextElement() {
            ZipFile.access$000(ZipFile.this);
            return (ZipEntry) this.val$iterator.next();
        }
    }

    /* loaded from: ZipFile$RAFStream.class */
    static class RAFStream extends InputStream {
        private final RandomAccessFile sharedRaf;
        private long endOffset;
        private long offset;

        public RAFStream(RandomAccessFile raf, long initialOffset) throws IOException {
            this.sharedRaf = raf;
            this.offset = initialOffset;
            this.endOffset = raf.length();
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return this.offset < this.endOffset ? 1 : 0;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            return Streams.readSingleByte(this);
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            synchronized (this.sharedRaf) {
                long length = this.endOffset - this.offset;
                if (byteCount > length) {
                    byteCount = (int) length;
                }
                this.sharedRaf.seek(this.offset);
                int count = this.sharedRaf.read(buffer, byteOffset, byteCount);
                if (count > 0) {
                    this.offset += count;
                    return count;
                }
                return -1;
            }
        }

        @Override // java.io.InputStream
        public long skip(long byteCount) throws IOException {
            if (byteCount > this.endOffset - this.offset) {
                byteCount = this.endOffset - this.offset;
            }
            this.offset += byteCount;
            return byteCount;
        }

        public int fill(Inflater inflater, int nativeEndBufSize) throws IOException {
            int len;
            synchronized (this.sharedRaf) {
                len = Math.min((int) (this.endOffset - this.offset), nativeEndBufSize);
                int cnt = inflater.setFileInput(this.sharedRaf.getFD(), this.offset, nativeEndBufSize);
                skip(cnt);
            }
            return len;
        }
    }

    /* loaded from: ZipFile$ZipInflaterInputStream.class */
    static class ZipInflaterInputStream extends InflaterInputStream {
        private final ZipEntry entry;
        private long bytesRead;

        public ZipInflaterInputStream(InputStream is, Inflater inf, int bsize, ZipEntry entry) {
            super(is, inf, bsize);
            this.bytesRead = 0L;
            this.entry = entry;
        }

        @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            try {
                int i = super.read(buffer, byteOffset, byteCount);
                if (i == -1) {
                    if (this.entry.size != this.bytesRead) {
                        throw new IOException("Size mismatch on inflated file: " + this.bytesRead + " vs " + this.entry.size);
                    }
                } else {
                    this.bytesRead += i;
                }
                return i;
            } catch (IOException e) {
                throw new IOException("Error reading data for " + this.entry.getName() + " near offset " + this.bytesRead, e);
            }
        }

        @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
        public int available() throws IOException {
            if (this.closed || super.available() == 0) {
                return 0;
            }
            return (int) (this.entry.getSize() - this.bytesRead);
        }
    }
}