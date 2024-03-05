package android.util;

import com.android.internal.util.ArrayUtils;

/* loaded from: SparseBooleanArray.class */
public class SparseBooleanArray implements Cloneable {
    private int[] mKeys;
    private boolean[] mValues;
    private int mSize;

    public SparseBooleanArray() {
        this(10);
    }

    public SparseBooleanArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_BOOLEANS;
        } else {
            int initialCapacity2 = ArrayUtils.idealIntArraySize(initialCapacity);
            this.mKeys = new int[initialCapacity2];
            this.mValues = new boolean[initialCapacity2];
        }
        this.mSize = 0;
    }

    /* renamed from: clone */
    public SparseBooleanArray m905clone() {
        SparseBooleanArray clone = null;
        try {
            clone = (SparseBooleanArray) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (boolean[]) this.mValues.clone();
        } catch (CloneNotSupportedException e) {
        }
        return clone;
    }

    public boolean get(int key) {
        return get(key, false);
    }

    public boolean get(int key, boolean valueIfKeyNotFound) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i < 0) {
            return valueIfKeyNotFound;
        }
        return this.mValues[i];
    }

    public void delete(int key) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            System.arraycopy(this.mKeys, i + 1, this.mKeys, i, this.mSize - (i + 1));
            System.arraycopy(this.mValues, i + 1, this.mValues, i, this.mSize - (i + 1));
            this.mSize--;
        }
    }

    public void put(int key, boolean value) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        int i2 = i ^ (-1);
        if (this.mSize >= this.mKeys.length) {
            int n = ArrayUtils.idealIntArraySize(this.mSize + 1);
            int[] nkeys = new int[n];
            boolean[] nvalues = new boolean[n];
            System.arraycopy(this.mKeys, 0, nkeys, 0, this.mKeys.length);
            System.arraycopy(this.mValues, 0, nvalues, 0, this.mValues.length);
            this.mKeys = nkeys;
            this.mValues = nvalues;
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

    public boolean valueAt(int index) {
        return this.mValues[index];
    }

    public int indexOfKey(int key) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public int indexOfValue(boolean value) {
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

    public void append(int key, boolean value) {
        if (this.mSize != 0 && key <= this.mKeys[this.mSize - 1]) {
            put(key, value);
            return;
        }
        int pos = this.mSize;
        if (pos >= this.mKeys.length) {
            int n = ArrayUtils.idealIntArraySize(pos + 1);
            int[] nkeys = new int[n];
            boolean[] nvalues = new boolean[n];
            System.arraycopy(this.mKeys, 0, nkeys, 0, this.mKeys.length);
            System.arraycopy(this.mValues, 0, nvalues, 0, this.mValues.length);
            this.mKeys = nkeys;
            this.mValues = nvalues;
        }
        this.mKeys[pos] = key;
        this.mValues[pos] = value;
        this.mSize = pos + 1;
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
            boolean value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }
}