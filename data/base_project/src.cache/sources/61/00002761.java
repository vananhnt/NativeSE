package java.util.regex;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Pattern.class */
public final class Pattern implements Serializable {
    public static final int UNIX_LINES = 1;
    public static final int CASE_INSENSITIVE = 2;
    public static final int COMMENTS = 4;
    public static final int MULTILINE = 8;
    public static final int LITERAL = 16;
    public static final int DOTALL = 32;
    public static final int UNICODE_CASE = 64;
    public static final int CANON_EQ = 128;

    Pattern() {
        throw new RuntimeException("Stub!");
    }

    public Matcher matcher(CharSequence input) {
        throw new RuntimeException("Stub!");
    }

    public String[] split(CharSequence input, int limit) {
        throw new RuntimeException("Stub!");
    }

    public String[] split(CharSequence input) {
        throw new RuntimeException("Stub!");
    }

    public String pattern() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public int flags() {
        throw new RuntimeException("Stub!");
    }

    public static Pattern compile(String regularExpression, int flags) throws PatternSyntaxException {
        throw new RuntimeException("Stub!");
    }

    public static Pattern compile(String pattern) {
        throw new RuntimeException("Stub!");
    }

    public static boolean matches(String regularExpression, CharSequence input) {
        throw new RuntimeException("Stub!");
    }

    public static String quote(String string) {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }
}