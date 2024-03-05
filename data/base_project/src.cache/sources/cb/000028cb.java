package libcore.icu;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;

/* loaded from: NativeBreakIterator.class */
public final class NativeBreakIterator implements Cloneable {
    private static final int BI_CHAR_INSTANCE = 1;
    private static final int BI_WORD_INSTANCE = 2;
    private static final int BI_LINE_INSTANCE = 3;
    private static final int BI_SENT_INSTANCE = 4;
    private final long address;
    private final int type;
    private String string;
    private CharacterIterator charIterator = new StringCharacterIterator("");

    private static native long getCharacterInstanceImpl(String str);

    private static native long getWordInstanceImpl(String str);

    private static native long getLineInstanceImpl(String str);

    private static native long getSentenceInstanceImpl(String str);

    private static native synchronized long cloneImpl(long j);

    private static native synchronized void closeImpl(long j);

    private static native synchronized void setTextImpl(long j, String str);

    private static native synchronized int precedingImpl(long j, String str, int i);

    private static native synchronized boolean isBoundaryImpl(long j, String str, int i);

    private static native synchronized int nextImpl(long j, String str, int i);

    private static native synchronized int previousImpl(long j, String str);

    private static native synchronized int currentImpl(long j, String str);

    private static native synchronized int firstImpl(long j, String str);

    private static native synchronized int followingImpl(long j, String str, int i);

    private static native synchronized int lastImpl(long j, String str);

    private NativeBreakIterator(long address, int type) {
        this.address = address;
        this.type = type;
    }

    public Object clone() {
        long cloneAddr = cloneImpl(this.address);
        NativeBreakIterator clone = new NativeBreakIterator(cloneAddr, this.type);
        clone.string = this.string;
        clone.charIterator = this.charIterator;
        return clone;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof NativeBreakIterator)) {
            return false;
        }
        NativeBreakIterator rhs = (NativeBreakIterator) object;
        return this.type == rhs.type && this.charIterator.equals(rhs.charIterator);
    }

    public int hashCode() {
        return 42;
    }

    protected void finalize() throws Throwable {
        try {
            closeImpl(this.address);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public int current() {
        return currentImpl(this.address, this.string);
    }

    public int first() {
        return firstImpl(this.address, this.string);
    }

    public int following(int offset) {
        return followingImpl(this.address, this.string, offset);
    }

    public CharacterIterator getText() {
        int newLocation = currentImpl(this.address, this.string);
        this.charIterator.setIndex(newLocation);
        return this.charIterator;
    }

    public int last() {
        return lastImpl(this.address, this.string);
    }

    public int next(int n) {
        return nextImpl(this.address, this.string, n);
    }

    public int next() {
        return nextImpl(this.address, this.string, 1);
    }

    public int previous() {
        return previousImpl(this.address, this.string);
    }

    public void setText(CharacterIterator newText) {
        StringBuilder sb = new StringBuilder();
        char first = newText.first();
        while (true) {
            char c = first;
            if (c != 65535) {
                sb.append(c);
                first = newText.next();
            } else {
                setText(sb.toString(), newText);
                return;
            }
        }
    }

    public void setText(String newText) {
        setText(newText, new StringCharacterIterator(newText));
    }

    private void setText(String s, CharacterIterator it) {
        this.string = s;
        this.charIterator = it;
        setTextImpl(this.address, this.string);
    }

    public boolean hasText() {
        return this.string != null;
    }

    public boolean isBoundary(int offset) {
        return isBoundaryImpl(this.address, this.string, offset);
    }

    public int preceding(int offset) {
        return precedingImpl(this.address, this.string, offset);
    }

    public static NativeBreakIterator getCharacterInstance(Locale where) {
        return new NativeBreakIterator(getCharacterInstanceImpl(where.toString()), 1);
    }

    public static NativeBreakIterator getLineInstance(Locale where) {
        return new NativeBreakIterator(getLineInstanceImpl(where.toString()), 3);
    }

    public static NativeBreakIterator getSentenceInstance(Locale where) {
        return new NativeBreakIterator(getSentenceInstanceImpl(where.toString()), 4);
    }

    public static NativeBreakIterator getWordInstance(Locale where) {
        return new NativeBreakIterator(getWordInstanceImpl(where.toString()), 2);
    }
}