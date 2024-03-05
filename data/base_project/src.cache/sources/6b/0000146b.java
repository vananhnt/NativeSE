package android.util;

import java.util.AbstractSet;
import java.util.Iterator;

/* loaded from: FastImmutableArraySet.class */
public final class FastImmutableArraySet<T> extends AbstractSet<T> {
    FastIterator<T> mIterator;
    T[] mContents;

    public FastImmutableArraySet(T[] contents) {
        this.mContents = contents;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        FastIterator<T> it = this.mIterator;
        if (it == null) {
            it = new FastIterator<>(this.mContents);
            this.mIterator = it;
        } else {
            it.mIndex = 0;
        }
        return it;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.mContents.length;
    }

    /* loaded from: FastImmutableArraySet$FastIterator.class */
    private static final class FastIterator<T> implements Iterator<T> {
        private final T[] mContents;
        int mIndex;

        public FastIterator(T[] contents) {
            this.mContents = contents;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mIndex != this.mContents.length;
        }

        @Override // java.util.Iterator
        public T next() {
            T[] tArr = this.mContents;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return tArr[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}