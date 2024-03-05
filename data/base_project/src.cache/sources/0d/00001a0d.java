package com.android.internal.os;

import gov.nist.core.Separators;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Formatter;
import java.util.Locale;

/* loaded from: LoggingPrintStream.class */
abstract class LoggingPrintStream extends PrintStream {
    private final StringBuilder builder;
    private ByteBuffer encodedBytes;
    private CharBuffer decodedChars;
    private CharsetDecoder decoder;
    private final Formatter formatter;

    protected abstract void log(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public LoggingPrintStream() {
        super(new OutputStream() { // from class: com.android.internal.os.LoggingPrintStream.1
            @Override // java.io.OutputStream
            public void write(int oneByte) throws IOException {
                throw new AssertionError();
            }
        });
        this.builder = new StringBuilder();
        this.formatter = new Formatter(this.builder, (Locale) null);
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public synchronized void flush() {
        flush(true);
    }

    private void flush(boolean completely) {
        int start;
        int nextBreak;
        int length = this.builder.length();
        int i = 0;
        while (true) {
            start = i;
            if (start >= length || (nextBreak = this.builder.indexOf(Separators.RETURN, start)) == -1) {
                break;
            }
            log(this.builder.substring(start, nextBreak));
            i = nextBreak + 1;
        }
        if (completely) {
            if (start < length) {
                log(this.builder.substring(start));
            }
            this.builder.setLength(0);
            return;
        }
        this.builder.delete(0, start);
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(int oneByte) {
        write(new byte[]{(byte) oneByte}, 0, 1);
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer) {
        write(buffer, 0, buffer.length);
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream
    public synchronized void write(byte[] bytes, int start, int count) {
        CoderResult coderResult;
        if (this.decoder == null) {
            this.encodedBytes = ByteBuffer.allocate(80);
            this.decodedChars = CharBuffer.allocate(80);
            this.decoder = Charset.defaultCharset().newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        }
        int end = start + count;
        while (start < end) {
            int numBytes = Math.min(this.encodedBytes.remaining(), end - start);
            this.encodedBytes.put(bytes, start, numBytes);
            start += numBytes;
            this.encodedBytes.flip();
            do {
                coderResult = this.decoder.decode(this.encodedBytes, this.decodedChars, false);
                this.decodedChars.flip();
                this.builder.append((CharSequence) this.decodedChars);
                this.decodedChars.clear();
            } while (coderResult.isOverflow());
            this.encodedBytes.compact();
        }
        flush(false);
    }

    @Override // java.io.PrintStream
    public boolean checkError() {
        return false;
    }

    @Override // java.io.PrintStream
    protected void setError() {
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public void close() {
    }

    @Override // java.io.PrintStream
    public PrintStream format(String format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }

    @Override // java.io.PrintStream
    public PrintStream printf(String format, Object... args) {
        return format(format, args);
    }

    @Override // java.io.PrintStream
    public PrintStream printf(Locale l, String format, Object... args) {
        return format(l, format, args);
    }

    @Override // java.io.PrintStream
    public synchronized PrintStream format(Locale l, String format, Object... args) {
        if (format == null) {
            throw new NullPointerException("format");
        }
        this.formatter.format(l, format, args);
        flush(false);
        return this;
    }

    @Override // java.io.PrintStream
    public synchronized void print(char[] charArray) {
        this.builder.append(charArray);
        flush(false);
    }

    @Override // java.io.PrintStream
    public synchronized void print(char ch) {
        this.builder.append(ch);
        if (ch == '\n') {
            flush(false);
        }
    }

    @Override // java.io.PrintStream
    public synchronized void print(double dnum) {
        this.builder.append(dnum);
    }

    @Override // java.io.PrintStream
    public synchronized void print(float fnum) {
        this.builder.append(fnum);
    }

    @Override // java.io.PrintStream
    public synchronized void print(int inum) {
        this.builder.append(inum);
    }

    @Override // java.io.PrintStream
    public synchronized void print(long lnum) {
        this.builder.append(lnum);
    }

    @Override // java.io.PrintStream
    public synchronized void print(Object obj) {
        this.builder.append(obj);
        flush(false);
    }

    @Override // java.io.PrintStream
    public synchronized void print(String str) {
        this.builder.append(str);
        flush(false);
    }

    @Override // java.io.PrintStream
    public synchronized void print(boolean bool) {
        this.builder.append(bool);
    }

    @Override // java.io.PrintStream
    public synchronized void println() {
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(char[] charArray) {
        this.builder.append(charArray);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(char ch) {
        this.builder.append(ch);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(double dnum) {
        this.builder.append(dnum);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(float fnum) {
        this.builder.append(fnum);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(int inum) {
        this.builder.append(inum);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(long lnum) {
        this.builder.append(lnum);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(Object obj) {
        this.builder.append(obj);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(String s) {
        int start;
        int nextBreak;
        if (this.builder.length() == 0) {
            int length = s.length();
            int i = 0;
            while (true) {
                start = i;
                if (start >= length || (nextBreak = s.indexOf(10, start)) == -1) {
                    break;
                }
                log(s.substring(start, nextBreak));
                i = nextBreak + 1;
            }
            if (start < length) {
                log(s.substring(start));
                return;
            }
            return;
        }
        this.builder.append(s);
        flush(true);
    }

    @Override // java.io.PrintStream
    public synchronized void println(boolean bool) {
        this.builder.append(bool);
        flush(true);
    }

    @Override // java.io.PrintStream, java.lang.Appendable
    public synchronized PrintStream append(char c) {
        print(c);
        return this;
    }

    @Override // java.io.PrintStream, java.lang.Appendable
    public synchronized PrintStream append(CharSequence csq) {
        this.builder.append(csq);
        flush(false);
        return this;
    }

    @Override // java.io.PrintStream, java.lang.Appendable
    public synchronized PrintStream append(CharSequence csq, int start, int end) {
        this.builder.append(csq, start, end);
        flush(false);
        return this;
    }
}