package java.util.jar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Manifest.class */
public class Manifest implements Cloneable {
    public Manifest() {
        throw new RuntimeException("Stub!");
    }

    public Manifest(InputStream is) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Manifest(Manifest man) {
        throw new RuntimeException("Stub!");
    }

    public void clear() {
        throw new RuntimeException("Stub!");
    }

    public Attributes getAttributes(String name) {
        throw new RuntimeException("Stub!");
    }

    public Map<String, Attributes> getEntries() {
        throw new RuntimeException("Stub!");
    }

    public Attributes getMainAttributes() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public void write(OutputStream os) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void read(InputStream is) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Manifest$Chunk.class */
    static class Chunk {
        int start;
        int end;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Chunk(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}