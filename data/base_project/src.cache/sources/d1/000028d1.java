package libcore.icu;

/* loaded from: NativeIDN.class */
public final class NativeIDN {
    private static native String convertImpl(String str, int i, boolean z);

    public static String toASCII(String s, int flags) {
        return convert(s, flags, true);
    }

    public static String toUnicode(String s, int flags) {
        try {
            return convert(s, flags, false);
        } catch (IllegalArgumentException e) {
            return s;
        }
    }

    private static String convert(String s, int flags, boolean toAscii) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }
        return convertImpl(s, flags, toAscii);
    }

    private NativeIDN() {
    }
}