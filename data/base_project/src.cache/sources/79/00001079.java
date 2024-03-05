package android.support.v4.util;

/* loaded from: CircularArray.class */
public class CircularArray<E> {
    private int mCapacityBitmask;
    private E[] mElements;
    private int mHead;
    private int mTail;

    public CircularArray() {
        this(8);
    }

    public CircularArray(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        int highestOneBit = Integer.bitCount(i) != 1 ? 1 << (Integer.highestOneBit(i) + 1) : i;
        this.mCapacityBitmask = highestOneBit - 1;
        this.mElements = (E[]) new Object[highestOneBit];
    }

    private void doubleCapacity() {
        E[] eArr = this.mElements;
        int length = eArr.length;
        int i = this.mHead;
        int i2 = length - i;
        int i3 = length << 1;
        if (i3 < 0) {
            throw new RuntimeException("Too big");
        }
        Object[] objArr = new Object[i3];
        System.arraycopy(eArr, i, objArr, 0, i2);
        System.arraycopy(this.mElements, 0, objArr, i2, this.mHead);
        this.mElements = (E[]) objArr;
        this.mHead = 0;
        this.mTail = length;
        this.mCapacityBitmask = i3 - 1;
    }

    public final void addFirst(E e) {
        this.mHead = (this.mHead - 1) & this.mCapacityBitmask;
        E[] eArr = this.mElements;
        int i = this.mHead;
        eArr[i] = e;
        if (i == this.mTail) {
            doubleCapacity();
        }
    }

    public final void addLast(E e) {
        E[] eArr = this.mElements;
        int i = this.mTail;
        eArr[i] = e;
        this.mTail = this.mCapacityBitmask & (i + 1);
        if (this.mTail == this.mHead) {
            doubleCapacity();
        }
    }

    public final E get(int i) {
        if (i < 0 || i >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i2 = this.mHead;
        return this.mElements[(i2 + i) & this.mCapacityBitmask];
    }

    public final E getFirst() {
        int i = this.mHead;
        if (i != this.mTail) {
            return this.mElements[i];
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public final E getLast() {
        int i = this.mHead;
        int i2 = this.mTail;
        if (i != i2) {
            return this.mElements[(i2 - 1) & this.mCapacityBitmask];
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public final boolean isEmpty() {
        return this.mHead == this.mTail;
    }

    public final E popFirst() {
        int i = this.mHead;
        if (i != this.mTail) {
            E[] eArr = this.mElements;
            E e = eArr[i];
            eArr[i] = null;
            this.mHead = (i + 1) & this.mCapacityBitmask;
            return e;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public final E popLast() {
        int i = this.mHead;
        int i2 = this.mTail;
        if (i != i2) {
            int i3 = this.mCapacityBitmask & (i2 - 1);
            E[] eArr = this.mElements;
            E e = eArr[i3];
            eArr[i3] = null;
            this.mTail = i3;
            return e;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public final int size() {
        return (this.mTail - this.mHead) & this.mCapacityBitmask;
    }
}