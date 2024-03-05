package java.util;

import java.lang.Enum;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MiniEnumSet.class */
public final class MiniEnumSet<E extends Enum<E>> extends EnumSet<E> {
    private static final int MAX_ELEMENTS = 64;
    private int size;
    private final E[] enums;
    private long bits;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractCollection, java.util.Collection
    public /* bridge */ /* synthetic */ boolean add(Object x0) {
        return add((MiniEnumSet<E>) ((Enum) x0));
    }

    MiniEnumSet(Class<E> elementType, E[] enums) {
        super(elementType);
        this.enums = enums;
    }

    /* loaded from: MiniEnumSet$MiniEnumSetIterator.class */
    private class MiniEnumSetIterator implements Iterator<E> {
        private long currentBits;
        private long mask;
        private E last;

        private MiniEnumSetIterator() {
            this.currentBits = MiniEnumSet.this.bits;
            this.mask = this.currentBits & (-this.currentBits);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mask != 0;
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.mask == 0) {
                throw new NoSuchElementException();
            }
            int ordinal = Long.numberOfTrailingZeros(this.mask);
            this.last = (E) MiniEnumSet.this.enums[ordinal];
            this.currentBits &= this.mask ^ (-1);
            this.mask = this.currentBits & (-this.currentBits);
            return this.last;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            MiniEnumSet.this.remove(this.last);
            this.last = null;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new MiniEnumSetIterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        this.bits = 0L;
        this.size = 0;
    }

    public boolean add(E element) {
        this.elementClass.cast(element);
        long oldBits = this.bits;
        long newBits = oldBits | (1 << element.ordinal());
        if (oldBits != newBits) {
            this.bits = newBits;
            this.size++;
            return true;
        }
        return false;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        if (collection.isEmpty()) {
            return false;
        }
        if (collection instanceof EnumSet) {
            EnumSet<?> set = (EnumSet) collection;
            set.elementClass.asSubclass(this.elementClass);
            MiniEnumSet<?> miniSet = (MiniEnumSet) set;
            long oldBits = this.bits;
            long newBits = oldBits | miniSet.bits;
            this.bits = newBits;
            this.size = Long.bitCount(newBits);
            return oldBits != newBits;
        }
        return super.addAll(collection);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object object) {
        if (object == null || !isValidType(object.getClass())) {
            return false;
        }
        Enum<E> element = (Enum) object;
        int ordinal = element.ordinal();
        return (this.bits & (1 << ordinal)) != 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        if (collection.isEmpty()) {
            return true;
        }
        if (!(collection instanceof MiniEnumSet)) {
            return !(collection instanceof EnumSet) && super.containsAll(collection);
        }
        MiniEnumSet<?> set = (MiniEnumSet) collection;
        long setBits = set.bits;
        return isValidType(set.elementClass) && (this.bits & setBits) == setBits;
    }

    @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection
    public boolean removeAll(Collection<?> collection) {
        if (collection.isEmpty()) {
            return false;
        }
        if (collection instanceof EnumSet) {
            EnumSet<?> set = (EnumSet) collection;
            if (!isValidType(set.elementClass)) {
                return false;
            }
            MiniEnumSet<E> miniSet = (MiniEnumSet) set;
            long oldBits = this.bits;
            long newBits = oldBits & (miniSet.bits ^ (-1));
            if (oldBits != newBits) {
                this.bits = newBits;
                this.size = Long.bitCount(newBits);
                return true;
            }
            return false;
        }
        return super.removeAll(collection);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean retainAll(Collection<?> collection) {
        if (collection instanceof EnumSet) {
            EnumSet<?> set = (EnumSet) collection;
            if (!isValidType(set.elementClass)) {
                if (this.size > 0) {
                    clear();
                    return true;
                }
                return false;
            }
            MiniEnumSet<E> miniSet = (MiniEnumSet) set;
            long oldBits = this.bits;
            long newBits = oldBits & miniSet.bits;
            if (oldBits != newBits) {
                this.bits = newBits;
                this.size = Long.bitCount(newBits);
                return true;
            }
            return false;
        }
        return super.retainAll(collection);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object object) {
        if (object == null || !isValidType(object.getClass())) {
            return false;
        }
        Enum<E> element = (Enum) object;
        int ordinal = element.ordinal();
        long oldBits = this.bits;
        long newBits = oldBits & ((1 << ordinal) ^ (-1));
        if (oldBits != newBits) {
            this.bits = newBits;
            this.size--;
            return true;
        }
        return false;
    }

    @Override // java.util.AbstractSet, java.util.Collection
    public boolean equals(Object object) {
        if (!(object instanceof EnumSet)) {
            return super.equals(object);
        }
        EnumSet<?> set = (EnumSet) object;
        return !isValidType(set.elementClass) ? this.size == 0 && set.isEmpty() : this.bits == ((MiniEnumSet) set).bits;
    }

    void complement() {
        if (this.enums.length != 0) {
            this.bits ^= -1;
            this.bits &= (-1) >>> (64 - this.enums.length);
            this.size = this.enums.length - this.size;
        }
    }

    void setRange(E start, E end) {
        int length = (end.ordinal() - start.ordinal()) + 1;
        long range = ((-1) >>> (64 - length)) << start.ordinal();
        this.bits |= range;
        this.size = Long.bitCount(this.bits);
    }
}