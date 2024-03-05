package java.io;

import java.nio.charset.Charset;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FileReader.class */
public class FileReader extends InputStreamReader {
    public FileReader(File file) throws FileNotFoundException {
        super((InputStream) null, (Charset) null);
        throw new RuntimeException("Stub!");
    }

    public FileReader(FileDescriptor fd) {
        super((InputStream) null, (Charset) null);
        throw new RuntimeException("Stub!");
    }

    public FileReader(String filename) throws FileNotFoundException {
        super((InputStream) null, (Charset) null);
        throw new RuntimeException("Stub!");
    }
}