package com.android.internal.util;

import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.io.Writer;

/* loaded from: IndentingPrintWriter.class */
public class IndentingPrintWriter extends PrintWriter {
    private final String mSingleIndent;
    private final int mWrapLength;
    private StringBuilder mIndentBuilder;
    private char[] mCurrentIndent;
    private int mCurrentLength;
    private boolean mEmptyLine;

    public IndentingPrintWriter(Writer writer, String singleIndent) {
        this(writer, singleIndent, -1);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent, int wrapLength) {
        super(writer);
        this.mIndentBuilder = new StringBuilder();
        this.mEmptyLine = true;
        this.mSingleIndent = singleIndent;
        this.mWrapLength = wrapLength;
    }

    public void increaseIndent() {
        this.mIndentBuilder.append(this.mSingleIndent);
        this.mCurrentIndent = null;
    }

    public void decreaseIndent() {
        this.mIndentBuilder.delete(0, this.mSingleIndent.length());
        this.mCurrentIndent = null;
    }

    public void printPair(String key, Object value) {
        print(key + Separators.EQUALS + String.valueOf(value) + Separators.SP);
    }

    public void printHexPair(String key, int value) {
        print(key + "=0x" + Integer.toHexString(value) + Separators.SP);
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(char[] buf, int offset, int count) {
        int indentLength = this.mIndentBuilder.length();
        int bufferEnd = offset + count;
        int lineStart = offset;
        int lineEnd = offset;
        while (lineEnd < bufferEnd) {
            int i = lineEnd;
            lineEnd++;
            char ch = buf[i];
            this.mCurrentLength++;
            if (ch == '\n') {
                maybeWriteIndent();
                super.write(buf, lineStart, lineEnd - lineStart);
                lineStart = lineEnd;
                this.mEmptyLine = true;
                this.mCurrentLength = 0;
            }
            if (this.mWrapLength > 0 && this.mCurrentLength >= this.mWrapLength - indentLength) {
                if (!this.mEmptyLine) {
                    super.write(10);
                    this.mEmptyLine = true;
                    this.mCurrentLength = lineEnd - lineStart;
                } else {
                    maybeWriteIndent();
                    super.write(buf, lineStart, lineEnd - lineStart);
                    super.write(10);
                    this.mEmptyLine = true;
                    lineStart = lineEnd;
                    this.mCurrentLength = 0;
                }
            }
        }
        if (lineStart != lineEnd) {
            maybeWriteIndent();
            super.write(buf, lineStart, lineEnd - lineStart);
        }
    }

    private void maybeWriteIndent() {
        if (this.mEmptyLine) {
            this.mEmptyLine = false;
            if (this.mIndentBuilder.length() != 0) {
                if (this.mCurrentIndent == null) {
                    this.mCurrentIndent = this.mIndentBuilder.toString().toCharArray();
                }
                super.write(this.mCurrentIndent, 0, this.mCurrentIndent.length);
            }
        }
    }
}