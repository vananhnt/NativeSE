package com.android.internal.util;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ims.AuthorizationHeaderIms;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: FastXmlSerializer.class */
public class FastXmlSerializer implements XmlSerializer {
    private static final int BUFFER_LEN = 8192;
    private int mPos;
    private Writer mWriter;
    private OutputStream mOutputStream;
    private CharsetEncoder mCharset;
    private boolean mInTag;
    private static final String[] ESCAPE_TABLE = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&quot;", null, null, null, "&amp;", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&lt;", null, "&gt;", null};
    private static String sSpace = "                                                              ";
    private final char[] mText = new char[8192];
    private ByteBuffer mBytes = ByteBuffer.allocate(8192);
    private boolean mIndent = false;
    private int mNesting = 0;
    private boolean mLineStart = true;

    private void append(char c) throws IOException {
        int pos = this.mPos;
        if (pos >= 8191) {
            flush();
            pos = this.mPos;
        }
        this.mText[pos] = c;
        this.mPos = pos + 1;
    }

    private void append(String str, int i, int length) throws IOException {
        if (length > 8192) {
            int end = i + length;
            while (i < end) {
                int next = i + 8192;
                append(str, i, next < end ? 8192 : end - i);
                i = next;
            }
            return;
        }
        int pos = this.mPos;
        if (pos + length > 8192) {
            flush();
            pos = this.mPos;
        }
        str.getChars(i, i + length, this.mText, pos);
        this.mPos = pos + length;
    }

    private void append(char[] buf, int i, int length) throws IOException {
        if (length > 8192) {
            int end = i + length;
            while (i < end) {
                int next = i + 8192;
                append(buf, i, next < end ? 8192 : end - i);
                i = next;
            }
            return;
        }
        int pos = this.mPos;
        if (pos + length > 8192) {
            flush();
            pos = this.mPos;
        }
        System.arraycopy(buf, i, this.mText, pos, length);
        this.mPos = pos + length;
    }

    private void append(String str) throws IOException {
        append(str, 0, str.length());
    }

    private void appendIndent(int indent) throws IOException {
        int indent2 = indent * 4;
        if (indent2 > sSpace.length()) {
            indent2 = sSpace.length();
        }
        append(sSpace, 0, indent2);
    }

    private void escapeAndAppendString(String string) throws IOException {
        String escape;
        int N = string.length();
        char NE = (char) ESCAPE_TABLE.length;
        String[] escapes = ESCAPE_TABLE;
        int lastPos = 0;
        int pos = 0;
        while (pos < N) {
            char c = string.charAt(pos);
            if (c < NE && (escape = escapes[c]) != null) {
                if (lastPos < pos) {
                    append(string, lastPos, pos - lastPos);
                }
                lastPos = pos + 1;
                append(escape);
            }
            pos++;
        }
        if (lastPos < pos) {
            append(string, lastPos, pos - lastPos);
        }
    }

    private void escapeAndAppendString(char[] buf, int start, int len) throws IOException {
        String escape;
        char NE = (char) ESCAPE_TABLE.length;
        String[] escapes = ESCAPE_TABLE;
        int end = start + len;
        int lastPos = start;
        int pos = start;
        while (pos < end) {
            char c = buf[pos];
            if (c < NE && (escape = escapes[c]) != null) {
                if (lastPos < pos) {
                    append(buf, lastPos, pos - lastPos);
                }
                lastPos = pos + 1;
                append(escape);
            }
            pos++;
        }
        if (lastPos < pos) {
            append(buf, lastPos, pos - lastPos);
        }
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer attribute(String namespace, String name, String value) throws IOException, IllegalArgumentException, IllegalStateException {
        append(' ');
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        append("=\"");
        escapeAndAppendString(value);
        append('\"');
        this.mLineStart = false;
        return this;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void comment(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void docdecl(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
        flush();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer endTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
        this.mNesting--;
        if (this.mInTag) {
            append(" />\n");
        } else {
            if (this.mIndent && this.mLineStart) {
                appendIndent(this.mNesting);
            }
            append("</");
            if (namespace != null) {
                append(namespace);
                append(':');
            }
            append(name);
            append(">\n");
        }
        this.mLineStart = true;
        this.mInTag = false;
        return this;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void entityRef(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    private void flushBytes() throws IOException {
        int position = this.mBytes.position();
        if (position > 0) {
            this.mBytes.flip();
            this.mOutputStream.write(this.mBytes.array(), 0, position);
            this.mBytes.clear();
        }
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void flush() throws IOException {
        if (this.mPos > 0) {
            if (this.mOutputStream != null) {
                CharBuffer charBuffer = CharBuffer.wrap(this.mText, 0, this.mPos);
                CoderResult encode = this.mCharset.encode(charBuffer, this.mBytes, true);
                while (true) {
                    CoderResult result = encode;
                    if (result.isError()) {
                        throw new IOException(result.toString());
                    }
                    if (result.isOverflow()) {
                        flushBytes();
                        encode = this.mCharset.encode(charBuffer, this.mBytes, true);
                    } else {
                        flushBytes();
                        this.mOutputStream.flush();
                        break;
                    }
                }
            } else {
                this.mWriter.write(this.mText, 0, this.mPos);
                this.mWriter.flush();
            }
            this.mPos = 0;
        }
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public int getDepth() {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getNamespace() {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getPrefix(String namespace, boolean generatePrefix) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public Object getProperty(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void ignorableWhitespace(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void processingInstruction(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
        if (name.equals("http://xmlpull.org/v1/doc/features.html#indent-output")) {
            this.mIndent = true;
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setOutput(OutputStream os, String encoding) throws IOException, IllegalArgumentException, IllegalStateException {
        if (os == null) {
            throw new IllegalArgumentException();
        }
        try {
            this.mCharset = Charset.forName(encoding).newEncoder();
            this.mOutputStream = os;
        } catch (IllegalCharsetNameException e) {
            throw ((UnsupportedEncodingException) new UnsupportedEncodingException(encoding).initCause(e));
        } catch (UnsupportedCharsetException e2) {
            throw ((UnsupportedEncodingException) new UnsupportedEncodingException(encoding).initCause(e2));
        }
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        this.mWriter = writer;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setPrefix(String prefix, String namespace) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void startDocument(String encoding, Boolean standalone) throws IOException, IllegalArgumentException, IllegalStateException {
        append("<?xml version='1.0' encoding='utf-8' standalone='" + (standalone.booleanValue() ? AuthorizationHeaderIms.YES : AuthorizationHeaderIms.NO) + "' ?>\n");
        this.mLineStart = true;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer startTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.mInTag) {
            append(">\n");
        }
        if (this.mIndent) {
            appendIndent(this.mNesting);
        }
        this.mNesting++;
        append('<');
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        this.mInTag = true;
        this.mLineStart = false;
        return this;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer text(char[] buf, int start, int len) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.mInTag) {
            append(Separators.GREATER_THAN);
            this.mInTag = false;
        }
        escapeAndAppendString(buf, start, len);
        if (this.mIndent) {
            this.mLineStart = buf[(start + len) - 1] == '\n';
        }
        return this;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.mInTag) {
            append(Separators.GREATER_THAN);
            this.mInTag = false;
        }
        escapeAndAppendString(text);
        if (this.mIndent) {
            this.mLineStart = text.length() > 0 && text.charAt(text.length() - 1) == '\n';
        }
        return this;
    }
}