package android.text;

/* loaded from: Editable.class */
public interface Editable extends CharSequence, GetChars, Spannable, Appendable {
    Editable replace(int i, int i2, CharSequence charSequence, int i3, int i4);

    Editable replace(int i, int i2, CharSequence charSequence);

    Editable insert(int i, CharSequence charSequence, int i2, int i3);

    Editable insert(int i, CharSequence charSequence);

    Editable delete(int i, int i2);

    @Override // java.lang.Appendable
    Editable append(CharSequence charSequence);

    @Override // java.lang.Appendable
    Editable append(CharSequence charSequence, int i, int i2);

    @Override // java.lang.Appendable
    Editable append(char c);

    void clear();

    void clearSpans();

    void setFilters(InputFilter[] inputFilterArr);

    InputFilter[] getFilters();

    /* loaded from: Editable$Factory.class */
    public static class Factory {
        private static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        public Editable newEditable(CharSequence source) {
            return new SpannableStringBuilder(source);
        }
    }
}