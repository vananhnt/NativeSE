package java.util;

import java.io.Serializable;
import java.lang.Enum;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: EnumSet.class */
public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, Serializable {
    EnumSet() {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> elementType) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> s) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> c) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> s) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4, E e5) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> of(E start, E... others) {
        throw new RuntimeException("Stub!");
    }

    public static <E extends Enum<E>> EnumSet<E> range(E start, E end) {
        throw new RuntimeException("Stub!");
    }

    @Override // 
    /* renamed from: clone */
    public EnumSet<E> mo1344clone() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: EnumSet$SerializationProxy.class */
    private static class SerializationProxy<E extends Enum<E>> implements Serializable {
        private static final long serialVersionUID = 362491234563181265L;
        private Class<E> elementType;
        private E[] elements;

        private SerializationProxy() {
        }

        private Object readResolve() {
            E[] eArr;
            EnumSet<E> set = EnumSet.noneOf(this.elementType);
            for (E e : this.elements) {
                set.add(e);
            }
            return set;
        }
    }
}