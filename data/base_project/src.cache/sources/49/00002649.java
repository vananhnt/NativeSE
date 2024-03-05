package java.util;

import java.lang.reflect.Array;

/* loaded from: UnsafeArrayList.class */
public class UnsafeArrayList<T> extends AbstractList<T> {
    private final Class<T> elementType;
    private T[] array;
    private int size;

    public UnsafeArrayList(Class<T> elementType, int initialCapacity) {
        this.array = (T[]) ((Object[]) Array.newInstance((Class<?>) elementType, initialCapacity));
        this.elementType = elementType;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public boolean add(T element) {
        if (this.size == this.array.length) {
            T[] newArray = (T[]) ((Object[]) Array.newInstance((Class<?>) this.elementType, this.size * 2));
            System.arraycopy(this.array, 0, newArray, 0, this.size);
            this.array = newArray;
        }
        T[] tArr = this.array;
        int i = this.size;
        this.size = i + 1;
        tArr[i] = element;
        this.modCount++;
        return true;
    }

    public T[] array() {
        return this.array;
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int i) {
        return this.array[i];
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }
}