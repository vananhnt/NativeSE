package android.text;

/* loaded from: AlteredCharSequence.class */
public class AlteredCharSequence implements CharSequence, GetChars {
    private int mStart;
    private int mEnd;
    private char[] mChars;
    private CharSequence mSource;

    public static AlteredCharSequence make(CharSequence source, char[] sub, int substart, int subend) {
        if (source instanceof Spanned) {
            return new AlteredSpanned(source, sub, substart, subend);
        }
        return new AlteredCharSequence(source, sub, substart, subend);
    }

    private AlteredCharSequence(CharSequence source, char[] sub, int substart, int subend) {
        this.mSource = source;
        this.mChars = sub;
        this.mStart = substart;
        this.mEnd = subend;
    }

    void update(char[] sub, int substart, int subend) {
        this.mChars = sub;
        this.mStart = substart;
        this.mEnd = subend;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AlteredCharSequence$AlteredSpanned.class */
    public static class AlteredSpanned extends AlteredCharSequence implements Spanned {
        private Spanned mSpanned;

        private AlteredSpanned(CharSequence source, char[] sub, int substart, int subend) {
            super(source, sub, substart, subend);
            this.mSpanned = (Spanned) source;
        }

        @Override // android.text.Spanned
        public <T> T[] getSpans(int start, int end, Class<T> kind) {
            return (T[]) this.mSpanned.getSpans(start, end, kind);
        }

        @Override // android.text.Spanned
        public int getSpanStart(Object span) {
            return this.mSpanned.getSpanStart(span);
        }

        @Override // android.text.Spanned
        public int getSpanEnd(Object span) {
            return this.mSpanned.getSpanEnd(span);
        }

        @Override // android.text.Spanned
        public int getSpanFlags(Object span) {
            return this.mSpanned.getSpanFlags(span);
        }

        @Override // android.text.Spanned
        public int nextSpanTransition(int start, int end, Class kind) {
            return this.mSpanned.nextSpanTransition(start, end, kind);
        }
    }

    @Override // java.lang.CharSequence
    public char charAt(int off) {
        if (off >= this.mStart && off < this.mEnd) {
            return this.mChars[off - this.mStart];
        }
        return this.mSource.charAt(off);
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.mSource.length();
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return make(this.mSource.subSequence(start, end), this.mChars, this.mStart - start, this.mEnd - start);
    }

    @Override // android.text.GetChars
    public void getChars(int start, int end, char[] dest, int off) {
        TextUtils.getChars(this.mSource, start, end, dest, off);
        int start2 = Math.max(this.mStart, start);
        int end2 = Math.min(this.mEnd, end);
        if (start2 > end2) {
            System.arraycopy(this.mChars, start2 - this.mStart, dest, off, end2 - start2);
        }
    }

    @Override // java.lang.CharSequence
    public String toString() {
        int len = length();
        char[] ret = new char[len];
        getChars(0, len, ret, 0);
        return String.valueOf(ret);
    }
}