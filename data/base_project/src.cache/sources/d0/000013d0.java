package android.text.method;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

/* loaded from: DigitsKeyListener.class */
public class DigitsKeyListener extends NumberKeyListener {
    private char[] mAccepted;
    private boolean mSign;
    private boolean mDecimal;
    private static final int SIGN = 1;
    private static final int DECIMAL = 2;
    private static final char[][] CHARACTERS = {new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}, new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+'}, new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'}, new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+', '.'}};
    private static DigitsKeyListener[] sInstance = new DigitsKeyListener[4];

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return this.mAccepted;
    }

    private static boolean isSignChar(char c) {
        return c == '-' || c == '+';
    }

    private static boolean isDecimalPointChar(char c) {
        return c == '.';
    }

    public DigitsKeyListener() {
        this(false, false);
    }

    public DigitsKeyListener(boolean sign, boolean decimal) {
        this.mSign = sign;
        this.mDecimal = decimal;
        int kind = (sign ? (char) 1 : (char) 0) | (decimal ? (char) 2 : (char) 0);
        this.mAccepted = CHARACTERS[kind];
    }

    public static DigitsKeyListener getInstance() {
        return getInstance(false, false);
    }

    public static DigitsKeyListener getInstance(boolean sign, boolean decimal) {
        int kind = (sign ? (char) 1 : (char) 0) | (decimal ? (char) 2 : (char) 0);
        if (sInstance[kind] != null) {
            return sInstance[kind];
        }
        sInstance[kind] = new DigitsKeyListener(sign, decimal);
        return sInstance[kind];
    }

    public static DigitsKeyListener getInstance(String accepted) {
        DigitsKeyListener dim = new DigitsKeyListener();
        dim.mAccepted = new char[accepted.length()];
        accepted.getChars(0, accepted.length(), dim.mAccepted, 0);
        return dim;
    }

    @Override // android.text.method.KeyListener
    public int getInputType() {
        int contentType = 2;
        if (this.mSign) {
            contentType = 2 | 4096;
        }
        if (this.mDecimal) {
            contentType |= 8192;
        }
        return contentType;
    }

    @Override // android.text.method.NumberKeyListener, android.text.InputFilter
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence out = super.filter(source, start, end, dest, dstart, dend);
        if (!this.mSign && !this.mDecimal) {
            return out;
        }
        if (out != null) {
            source = out;
            start = 0;
            end = out.length();
        }
        int sign = -1;
        int decimal = -1;
        int dlen = dest.length();
        for (int i = 0; i < dstart; i++) {
            char c = dest.charAt(i);
            if (isSignChar(c)) {
                sign = i;
            } else if (isDecimalPointChar(c)) {
                decimal = i;
            }
        }
        for (int i2 = dend; i2 < dlen; i2++) {
            char c2 = dest.charAt(i2);
            if (isSignChar(c2)) {
                return "";
            }
            if (isDecimalPointChar(c2)) {
                decimal = i2;
            }
        }
        SpannableStringBuilder stripped = null;
        for (int i3 = end - 1; i3 >= start; i3--) {
            char c3 = source.charAt(i3);
            boolean strip = false;
            if (isSignChar(c3)) {
                if (i3 != start || dstart != 0) {
                    strip = true;
                } else if (sign >= 0) {
                    strip = true;
                } else {
                    sign = i3;
                }
            } else if (isDecimalPointChar(c3)) {
                if (decimal >= 0) {
                    strip = true;
                } else {
                    decimal = i3;
                }
            }
            if (strip) {
                if (end == start + 1) {
                    return "";
                }
                if (stripped == null) {
                    stripped = new SpannableStringBuilder(source, start, end);
                }
                stripped.delete(i3 - start, (i3 + 1) - start);
            }
        }
        if (stripped != null) {
            return stripped;
        }
        if (out != null) {
            return out;
        }
        return null;
    }
}