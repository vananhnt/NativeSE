package android.text;

import com.android.internal.util.ArrayUtils;

/* loaded from: PackedIntVector.class */
class PackedIntVector {
    private final int mColumns;
    private int mRows = 0;
    private int mRowGapStart = 0;
    private int mRowGapLength = this.mRows;
    private int[] mValues = null;
    private int[] mValueGap;

    public PackedIntVector(int columns) {
        this.mColumns = columns;
        this.mValueGap = new int[2 * columns];
    }

    public int getValue(int row, int column) {
        int columns = this.mColumns;
        if ((row | column) < 0 || row >= size() || column >= columns) {
            throw new IndexOutOfBoundsException(row + ", " + column);
        }
        if (row >= this.mRowGapStart) {
            row += this.mRowGapLength;
        }
        int value = this.mValues[(row * columns) + column];
        int[] valuegap = this.mValueGap;
        if (row >= valuegap[column]) {
            value += valuegap[column + columns];
        }
        return value;
    }

    public void setValue(int row, int column, int value) {
        if ((row | column) < 0 || row >= size() || column >= this.mColumns) {
            throw new IndexOutOfBoundsException(row + ", " + column);
        }
        if (row >= this.mRowGapStart) {
            row += this.mRowGapLength;
        }
        int[] valuegap = this.mValueGap;
        if (row >= valuegap[column]) {
            value -= valuegap[column + this.mColumns];
        }
        this.mValues[(row * this.mColumns) + column] = value;
    }

    private void setValueInternal(int row, int column, int value) {
        if (row >= this.mRowGapStart) {
            row += this.mRowGapLength;
        }
        int[] valuegap = this.mValueGap;
        if (row >= valuegap[column]) {
            value -= valuegap[column + this.mColumns];
        }
        this.mValues[(row * this.mColumns) + column] = value;
    }

    public void adjustValuesBelow(int startRow, int column, int delta) {
        if ((startRow | column) < 0 || startRow > size() || column >= width()) {
            throw new IndexOutOfBoundsException(startRow + ", " + column);
        }
        if (startRow >= this.mRowGapStart) {
            startRow += this.mRowGapLength;
        }
        moveValueGapTo(column, startRow);
        int[] iArr = this.mValueGap;
        int i = column + this.mColumns;
        iArr[i] = iArr[i] + delta;
    }

    public void insertAt(int row, int[] values) {
        if (row < 0 || row > size()) {
            throw new IndexOutOfBoundsException("row " + row);
        }
        if (values != null && values.length < width()) {
            throw new IndexOutOfBoundsException("value count " + values.length);
        }
        moveRowGapTo(row);
        if (this.mRowGapLength == 0) {
            growBuffer();
        }
        this.mRowGapStart++;
        this.mRowGapLength--;
        if (values == null) {
            for (int i = this.mColumns - 1; i >= 0; i--) {
                setValueInternal(row, i, 0);
            }
            return;
        }
        for (int i2 = this.mColumns - 1; i2 >= 0; i2--) {
            setValueInternal(row, i2, values[i2]);
        }
    }

    public void deleteAt(int row, int count) {
        if ((row | count) < 0 || row + count > size()) {
            throw new IndexOutOfBoundsException(row + ", " + count);
        }
        moveRowGapTo(row + count);
        this.mRowGapStart -= count;
        this.mRowGapLength += count;
    }

    public int size() {
        return this.mRows - this.mRowGapLength;
    }

    public int width() {
        return this.mColumns;
    }

    private final void growBuffer() {
        int columns = this.mColumns;
        int newsize = ArrayUtils.idealIntArraySize((size() + 1) * columns) / columns;
        int[] newvalues = new int[newsize * columns];
        int[] valuegap = this.mValueGap;
        int rowgapstart = this.mRowGapStart;
        int after = this.mRows - (rowgapstart + this.mRowGapLength);
        if (this.mValues != null) {
            System.arraycopy(this.mValues, 0, newvalues, 0, columns * rowgapstart);
            System.arraycopy(this.mValues, (this.mRows - after) * columns, newvalues, (newsize - after) * columns, after * columns);
        }
        for (int i = 0; i < columns; i++) {
            if (valuegap[i] >= rowgapstart) {
                int i2 = i;
                valuegap[i2] = valuegap[i2] + (newsize - this.mRows);
                if (valuegap[i] < rowgapstart) {
                    valuegap[i] = rowgapstart;
                }
            }
        }
        this.mRowGapLength += newsize - this.mRows;
        this.mRows = newsize;
        this.mValues = newvalues;
    }

    private final void moveValueGapTo(int column, int where) {
        int[] valuegap = this.mValueGap;
        int[] values = this.mValues;
        int columns = this.mColumns;
        if (where == valuegap[column]) {
            return;
        }
        if (where > valuegap[column]) {
            for (int i = valuegap[column]; i < where; i++) {
                int i2 = (i * columns) + column;
                values[i2] = values[i2] + valuegap[column + columns];
            }
        } else {
            for (int i3 = where; i3 < valuegap[column]; i3++) {
                int i4 = (i3 * columns) + column;
                values[i4] = values[i4] - valuegap[column + columns];
            }
        }
        valuegap[column] = where;
    }

    private final void moveRowGapTo(int where) {
        if (where == this.mRowGapStart) {
            return;
        }
        if (where > this.mRowGapStart) {
            int moving = (where + this.mRowGapLength) - (this.mRowGapStart + this.mRowGapLength);
            int columns = this.mColumns;
            int[] valuegap = this.mValueGap;
            int[] values = this.mValues;
            int gapend = this.mRowGapStart + this.mRowGapLength;
            for (int i = gapend; i < gapend + moving; i++) {
                int destrow = (i - gapend) + this.mRowGapStart;
                for (int j = 0; j < columns; j++) {
                    int val = values[(i * columns) + j];
                    if (i >= valuegap[j]) {
                        val += valuegap[j + columns];
                    }
                    if (destrow >= valuegap[j]) {
                        val -= valuegap[j + columns];
                    }
                    values[(destrow * columns) + j] = val;
                }
            }
        } else {
            int moving2 = this.mRowGapStart - where;
            int columns2 = this.mColumns;
            int[] valuegap2 = this.mValueGap;
            int[] values2 = this.mValues;
            int gapend2 = this.mRowGapStart + this.mRowGapLength;
            for (int i2 = (where + moving2) - 1; i2 >= where; i2--) {
                int destrow2 = ((i2 - where) + gapend2) - moving2;
                for (int j2 = 0; j2 < columns2; j2++) {
                    int val2 = values2[(i2 * columns2) + j2];
                    if (i2 >= valuegap2[j2]) {
                        val2 += valuegap2[j2 + columns2];
                    }
                    if (destrow2 >= valuegap2[j2]) {
                        val2 -= valuegap2[j2 + columns2];
                    }
                    values2[(destrow2 * columns2) + j2] = val2;
                }
            }
        }
        this.mRowGapStart = where;
    }
}