package android.text;

/* loaded from: InputFilter.class */
public interface InputFilter {
    CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4);

    /* loaded from: InputFilter$AllCaps.class */
    public static class AllCaps implements InputFilter {
        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isLowerCase(source.charAt(i))) {
                    char[] v = new char[end - start];
                    TextUtils.getChars(source, start, end, v, 0);
                    String s = new String(v).toUpperCase();
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(s);
                        TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                        return sp;
                    }
                    return s;
                }
            }
            return null;
        }
    }

    /* loaded from: InputFilter$LengthFilter.class */
    public static class LengthFilter implements InputFilter {
        private int mMax;

        public LengthFilter(int max) {
            this.mMax = max;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int keep = this.mMax - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            }
            if (keep >= end - start) {
                return null;
            }
            int keep2 = keep + start;
            if (Character.isHighSurrogate(source.charAt(keep2 - 1))) {
                keep2--;
                if (keep2 == start) {
                    return "";
                }
            }
            return source.subSequence(start, keep2);
        }
    }
}