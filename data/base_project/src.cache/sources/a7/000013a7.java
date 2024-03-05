package android.text;

/* loaded from: Spannable.class */
public interface Spannable extends Spanned {
    void setSpan(Object obj, int i, int i2, int i3);

    void removeSpan(Object obj);

    /* loaded from: Spannable$Factory.class */
    public static class Factory {
        private static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        public Spannable newSpannable(CharSequence source) {
            return new SpannableString(source);
        }
    }
}