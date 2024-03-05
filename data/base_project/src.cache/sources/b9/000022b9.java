package java.io;

import java.util.Locale;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PrintStream.class */
public class PrintStream extends FilterOutputStream implements Appendable, Closeable {
    public PrintStream(OutputStream out) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PrintStream(OutputStream out, boolean autoFlush) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PrintStream(OutputStream out, boolean autoFlush, String charsetName) throws UnsupportedEncodingException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PrintStream(File file) throws FileNotFoundException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PrintStream(File file, String charsetName) throws FileNotFoundException, UnsupportedEncodingException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PrintStream(String fileName) throws FileNotFoundException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public PrintStream(String fileName, String charsetName) throws FileNotFoundException, UnsupportedEncodingException {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public boolean checkError() {
        throw new RuntimeException("Stub!");
    }

    protected void clearError() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public synchronized void close() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public synchronized void flush() {
        throw new RuntimeException("Stub!");
    }

    public PrintStream format(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintStream format(Locale l, String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintStream printf(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintStream printf(Locale l, String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public void print(char[] chars) {
        throw new RuntimeException("Stub!");
    }

    public void print(char c) {
        throw new RuntimeException("Stub!");
    }

    public void print(double d) {
        throw new RuntimeException("Stub!");
    }

    public void print(float f) {
        throw new RuntimeException("Stub!");
    }

    public void print(int i) {
        throw new RuntimeException("Stub!");
    }

    public void print(long l) {
        throw new RuntimeException("Stub!");
    }

    public void print(Object o) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void print(String str) {
        throw new RuntimeException("Stub!");
    }

    public void print(boolean b) {
        throw new RuntimeException("Stub!");
    }

    public void println() {
        throw new RuntimeException("Stub!");
    }

    public void println(char[] chars) {
        throw new RuntimeException("Stub!");
    }

    public void println(char c) {
        throw new RuntimeException("Stub!");
    }

    public void println(double d) {
        throw new RuntimeException("Stub!");
    }

    public void println(float f) {
        throw new RuntimeException("Stub!");
    }

    public void println(int i) {
        throw new RuntimeException("Stub!");
    }

    public void println(long l) {
        throw new RuntimeException("Stub!");
    }

    public void println(Object o) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void println(String str) {
        throw new RuntimeException("Stub!");
    }

    public void println(boolean b) {
        throw new RuntimeException("Stub!");
    }

    protected void setError() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer, int offset, int length) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(int oneByte) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public PrintStream append(char c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public PrintStream append(CharSequence charSequence) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public PrintStream append(CharSequence charSequence, int start, int end) {
        throw new RuntimeException("Stub!");
    }
}