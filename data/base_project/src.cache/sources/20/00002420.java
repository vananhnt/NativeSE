package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Charset.class */
public abstract class Charset implements Comparable<Charset> {
    public abstract boolean contains(Charset charset);

    public abstract CharsetEncoder newEncoder();

    public abstract CharsetDecoder newDecoder();

    /* JADX INFO: Access modifiers changed from: protected */
    public Charset(String canonicalName, String[] aliases) {
        throw new RuntimeException("Stub!");
    }

    public static SortedMap<String, Charset> availableCharsets() {
        throw new RuntimeException("Stub!");
    }

    public static Charset forName(String charsetName) {
        throw new RuntimeException("Stub!");
    }

    public static boolean isSupported(String charsetName) {
        throw new RuntimeException("Stub!");
    }

    public final String name() {
        throw new RuntimeException("Stub!");
    }

    public final Set<String> aliases() {
        throw new RuntimeException("Stub!");
    }

    public String displayName() {
        throw new RuntimeException("Stub!");
    }

    public String displayName(Locale l) {
        throw new RuntimeException("Stub!");
    }

    public final boolean isRegistered() {
        throw new RuntimeException("Stub!");
    }

    public boolean canEncode() {
        throw new RuntimeException("Stub!");
    }

    public final ByteBuffer encode(CharBuffer buffer) {
        throw new RuntimeException("Stub!");
    }

    public final ByteBuffer encode(String s) {
        throw new RuntimeException("Stub!");
    }

    public final CharBuffer decode(ByteBuffer buffer) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public final int compareTo(Charset charset) {
        throw new RuntimeException("Stub!");
    }

    public final boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public final int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }

    public static Charset defaultCharset() {
        throw new RuntimeException("Stub!");
    }
}