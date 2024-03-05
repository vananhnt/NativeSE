package android.text;

/* loaded from: SpannedString.class */
public final class SpannedString extends SpannableStringInternal implements CharSequence, GetChars, Spanned {
    @Override // android.text.SpannableStringInternal
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // android.text.SpannableStringInternal
    public /* bridge */ /* synthetic */ boolean equals(Object x0) {
        return super.equals(x0);
    }

    @Override // android.text.SpannableStringInternal, android.text.Spanned
    public /* bridge */ /* synthetic */ int nextSpanTransition(int x0, int x1, Class x2) {
        return super.nextSpanTransition(x0, x1, x2);
    }

    @Override // android.text.SpannableStringInternal, android.text.Spanned
    public /* bridge */ /* synthetic */ Object[] getSpans(int x0, int x1, Class x2) {
        return super.getSpans(x0, x1, x2);
    }

    @Override // android.text.SpannableStringInternal, android.text.Spanned
    public /* bridge */ /* synthetic */ int getSpanFlags(Object x0) {
        return super.getSpanFlags(x0);
    }

    @Override // android.text.SpannableStringInternal, android.text.Spanned
    public /* bridge */ /* synthetic */ int getSpanEnd(Object x0) {
        return super.getSpanEnd(x0);
    }

    @Override // android.text.SpannableStringInternal, android.text.Spanned
    public /* bridge */ /* synthetic */ int getSpanStart(Object x0) {
        return super.getSpanStart(x0);
    }

    public SpannedString(CharSequence source) {
        super(source, 0, source.length());
    }

    private SpannedString(CharSequence source, int start, int end) {
        super(source, start, end);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return new SpannedString(this, start, end);
    }

    public static SpannedString valueOf(CharSequence source) {
        if (source instanceof SpannedString) {
            return (SpannedString) source;
        }
        return new SpannedString(source);
    }
}