package java.util.jar;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarVerifier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import libcore.io.Streams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: JarFile.class */
public class JarFile extends ZipFile {
    public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";

    public JarFile(File file) throws IOException {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    public JarFile(File file, boolean verify) throws IOException {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    public JarFile(File file, boolean verify, int mode) throws IOException {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    public JarFile(String filename) throws IOException {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    public JarFile(String filename, boolean verify) throws IOException {
        super(null, 0);
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipFile
    public Enumeration<JarEntry> entries() {
        throw new RuntimeException("Stub!");
    }

    public JarEntry getJarEntry(String name) {
        throw new RuntimeException("Stub!");
    }

    public Manifest getManifest() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipFile
    public InputStream getInputStream(ZipEntry ze) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipFile
    public ZipEntry getEntry(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.zip.ZipFile
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: JarFile$JarFileInputStream.class */
    static final class JarFileInputStream extends FilterInputStream {
        private long count;
        private ZipEntry zipEntry;
        private JarVerifier.VerifierEntry entry;
        private boolean done;

        JarFileInputStream(InputStream is, ZipEntry ze, JarVerifier.VerifierEntry e) {
            super(is);
            this.done = false;
            this.zipEntry = ze;
            this.count = this.zipEntry.getSize();
            this.entry = e;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            if (this.done) {
                return -1;
            }
            if (this.count > 0) {
                int r = super.read();
                if (r != -1) {
                    this.entry.write(r);
                    this.count--;
                } else {
                    this.count = 0L;
                }
                if (this.count == 0) {
                    this.done = true;
                    this.entry.verify();
                }
                return r;
            }
            this.done = true;
            this.entry.verify();
            return -1;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (this.done) {
                return -1;
            }
            if (this.count > 0) {
                int r = super.read(buffer, byteOffset, byteCount);
                if (r != -1) {
                    int size = r;
                    if (this.count < size) {
                        size = (int) this.count;
                    }
                    this.entry.write(buffer, byteOffset, size);
                    this.count -= size;
                } else {
                    this.count = 0L;
                }
                if (this.count == 0) {
                    this.done = true;
                    this.entry.verify();
                }
                return r;
            }
            this.done = true;
            this.entry.verify();
            return -1;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int available() throws IOException {
            if (this.done) {
                return 0;
            }
            return super.available();
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public long skip(long byteCount) throws IOException {
            return Streams.skipByReading(this, byteCount);
        }
    }

    /* renamed from: java.util.jar.JarFile$1JarFileEnumerator  reason: invalid class name */
    /* loaded from: JarFile$1JarFileEnumerator.class */
    class C1JarFileEnumerator implements Enumeration<JarEntry> {
        Enumeration<? extends ZipEntry> ze;
        JarFile jf;

        C1JarFileEnumerator(Enumeration<? extends ZipEntry> zenum, JarFile jf) {
            this.ze = zenum;
            this.jf = jf;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.ze.hasMoreElements();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public JarEntry nextElement() {
            JarEntry je = new JarEntry(this.ze.nextElement());
            je.parentJar = this.jf;
            return je;
        }
    }
}