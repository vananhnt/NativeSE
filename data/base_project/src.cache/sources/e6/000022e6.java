package java.lang;

import java.io.Serializable;
import java.lang.Enum;

/* loaded from: Enum.class */
public abstract class Enum<E extends Enum<E>> implements Serializable, Comparable<E> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(Object x0) {
        return compareTo((Enum<E>) x0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Enum(String name, int ordinal) {
        throw new RuntimeException("Stub!");
    }

    public final String name() {
        throw new RuntimeException("Stub!");
    }

    public final int ordinal() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public final boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public final int hashCode() {
        throw new RuntimeException("Stub!");
    }

    protected final Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    public final int compareTo(E o) {
        throw new RuntimeException("Stub!");
    }

    public final Class<E> getDeclaringClass() {
        throw new RuntimeException("Stub!");
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        throw new RuntimeException("Stub!");
    }

    protected final void finalize() {
        throw new RuntimeException("Stub!");
    }
}