package android.util;

import com.android.internal.util.ArrayUtils;

/* loaded from: SparseLongArray.class */
public class SparseLongArray implements Cloneable {
    private int[] mKeys;
    private long[] mValues;
    private int mSize;

    public SparseLongArray() {
        this(10);
    }

    public SparseLongArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_LONGS;
        } else {
            int initialCapacity2 = ArrayUtils.idealLongArraySize(initialCapacity);
            this.mKeys = new int[initialCapacity2];
            this.mValues = new long[initialCapacity2];
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public SparseLongArray m907clone() {
        SparseLongArray clone = null;
        try {
            clone = (SparseLongArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (long[]) this.mValues.clone();
        } catch (CloneNotSupportedException e) {
        }
        return clone;
    }

    public long get(int key) {
        return get(key, 0L);
    }

    public long get(int key, long valueIfKeyNotFound) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(int key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            removeAt(i);
        }
    }

    public void removeAt(int index) {
        System.arraycopy(this.mKeys, index + 1, this.mKeys, index, this.mSize - (index + 1));
        System.arraycopy(this.mValues, index + 1, this.mValues, index, this.mSize - (index + 1));
        this.mSize--;
    }

    public void put(int key, long value) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        int i2 = i ^ (-1);
        if (this.mSize >= this.mKeys.length) {
            growKeyAndValueArrays(this.mSize + 1);
        }
        if (this.mSize - i2 != 0) {
            System.arraycopy(this.mKeys, i2, this.mKeys, i2 + 1, this.mSize - i2);
            System.arraycopy(this.mValues, i2, this.mValues, i2 + 1, this.mSize - i2);
        }
        this.mKeys[i2] = key;
        this.mValues[i2] = value;
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int index) {
        return this.mKeys[index];
    }

    public long valueAt(int index) {
        return this.mValues[index];
    }

    public int indexOfKey(int key) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(long value) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int key, long value) {
        if (this.mSize != 0 && key <= this.mKeys[this.mSize - 1]) {
            put(key, value);
            return;
        }
        int pos = this.mSize;
        if (pos >= this.mKeys.length) {
            growKeyAndValueArrays(pos + 1);
        }
        this.mKeys[pos] = key;
        this.mValues[pos] = value;
        this.mSize = pos + 1;
    }

    private void growKeyAndValueArrays(int minNeededSize) {
        int n = ArrayUtils.idealLongArraySize(minNeededSize);
        int[] nkeys = new int[n];
        long[] nvalues = new long[n];
        System.arraycopy(this.mKeys, 0, nkeys, 0, this.mKeys.length);
        System.arraycopy(this.mValues, 0, nvalues, 0, this.mValues.length);
        this.mKeys = nkeys;
        this.mValues = nvalues;
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 28);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            int key = keyAt(i);
            buffer.append(key);
            buffer.append('=');
            long value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }
}