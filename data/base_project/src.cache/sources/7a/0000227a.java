package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Console.class */
public final class Console implements Flushable {
    Console() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Flushable
    public void flush() {
        throw new RuntimeException("Stub!");
    }

    public Console format(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public Console printf(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public Reader reader() {
        throw new RuntimeException("Stub!");
    }

    public String readLine() {
        throw new RuntimeException("Stub!");
    }

    public String readLine(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public char[] readPassword() {
        throw new RuntimeException("Stub!");
    }

    public char[] readPassword(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter writer() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Console$ConsoleReader.class */
    private static class ConsoleReader extends BufferedReader {
        public ConsoleReader(InputStream in) throws IOException {
            super(new InputStreamReader(in, System.getProperty("file.encoding")), 256);
            this.lock = Console.access$000();
        }

        @Override // java.io.BufferedReader, java.io.Reader, java.io.Closeable
        public void close() {
        }
    }

    /* loaded from: Console$ConsoleWriter.class */
    private static class ConsoleWriter extends PrintWriter {
        public ConsoleWriter(OutputStream out) {
            super(out, true);
            this.lock = Console.access$000();
        }

        @Override // java.io.PrintWriter, java.io.Writer, java.io.Closeable
        public void close() {
            flush();
        }
    }
}