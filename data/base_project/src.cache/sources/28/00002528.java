package java.text;

import java.util.Comparator;
import java.util.Locale;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Collator.class */
public abstract class Collator implements Comparator<Object>, Cloneable {
    public static final int NO_DECOMPOSITION = 0;
    public static final int CANONICAL_DECOMPOSITION = 1;
    public static final int FULL_DECOMPOSITION = 2;
    public static final int PRIMARY = 0;
    public static final int SECONDARY = 1;
    public static final int TERTIARY = 2;
    public static final int IDENTICAL = 3;

    public abstract int compare(String str, String str2);

    public abstract CollationKey getCollationKey(String str);

    public abstract int hashCode();

    /* JADX INFO: Access modifiers changed from: protected */
    public Collator() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Comparator
    public int compare(Object object1, Object object2) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Comparator
    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(String string1, String string2) {
        throw new RuntimeException("Stub!");
    }

    public static Locale[] getAvailableLocales() {
        throw new RuntimeException("Stub!");
    }

    public int getDecomposition() {
        throw new RuntimeException("Stub!");
    }

    public static Collator getInstance() {
        throw new RuntimeException("Stub!");
    }

    public static Collator getInstance(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public int getStrength() {
        throw new RuntimeException("Stub!");
    }

    public void setDecomposition(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setStrength(int value) {
        throw new RuntimeException("Stub!");
    }
}