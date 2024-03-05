package java.io;

import java.util.Locale;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PrintWriter.class */
public class PrintWriter extends Writer {
    protected Writer out;

    public PrintWriter(OutputStream out) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(OutputStream out, boolean autoFlush) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(Writer wr) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(Writer wr, boolean autoFlush) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(File file) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(String fileName) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public boolean checkError() {
        throw new RuntimeException("Stub!");
    }

    protected void clearError() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Closeable
    public void close() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter format(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter format(Locale l, String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter printf(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public PrintWriter printf(Locale l, String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public void print(char[] charArray) {
        throw new RuntimeException("Stub!");
    }

    public void print(char ch) {
        throw new RuntimeException("Stub!");
    }

    public void print(double dnum) {
        throw new RuntimeException("Stub!");
    }

    public void print(float fnum) {
        throw new RuntimeException("Stub!");
    }

    public void print(int inum) {
        throw new RuntimeException("Stub!");
    }

    public void print(long lnum) {
        throw new RuntimeException("Stub!");
    }

    public void print(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public void print(String str) {
        throw new RuntimeException("Stub!");
    }

    public void print(boolean bool) {
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

    public void println(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public void println(String str) {
        throw new RuntimeException("Stub!");
    }

    public void println(boolean b) {
        throw new RuntimeException("Stub!");
    }

    protected void setError() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(char[] buf) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(char[] buf, int offset, int count) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(int oneChar) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(String str) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(String str, int offset, int count) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.lang.Appendable
    public PrintWriter append(char c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.lang.Appendable
    public PrintWriter append(CharSequence csq) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.lang.Appendable
    public PrintWriter append(CharSequence csq, int start, int end) {
        throw new RuntimeException("Stub!");
    }
}