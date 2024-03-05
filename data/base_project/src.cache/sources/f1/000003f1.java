package android.database;

import java.util.Iterator;

/* loaded from: CursorJoiner.class */
public final class CursorJoiner implements Iterator<Result>, Iterable<Result> {
    private Cursor mCursorLeft;
    private Cursor mCursorRight;
    private boolean mCompareResultIsValid;
    private Result mCompareResult;
    private int[] mColumnsLeft;
    private int[] mColumnsRight;
    private String[] mValues;
    static final /* synthetic */ boolean $assertionsDisabled;

    /* loaded from: CursorJoiner$Result.class */
    public enum Result {
        RIGHT,
        LEFT,
        BOTH
    }

    static {
        $assertionsDisabled = !CursorJoiner.class.desiredAssertionStatus();
    }

    public CursorJoiner(Cursor cursorLeft, String[] columnNamesLeft, Cursor cursorRight, String[] columnNamesRight) {
        if (columnNamesLeft.length != columnNamesRight.length) {
            throw new IllegalArgumentException("you must have the same number of columns on the left and right, " + columnNamesLeft.length + " != " + columnNamesRight.length);
        }
        this.mCursorLeft = cursorLeft;
        this.mCursorRight = cursorRight;
        this.mCursorLeft.moveToFirst();
        this.mCursorRight.moveToFirst();
        this.mCompareResultIsValid = false;
        this.mColumnsLeft = buildColumnIndiciesArray(cursorLeft, columnNamesLeft);
        this.mColumnsRight = buildColumnIndiciesArray(cursorRight, columnNamesRight);
        this.mValues = new String[this.mColumnsLeft.length * 2];
    }

    @Override // java.lang.Iterable
    public Iterator<Result> iterator() {
        return this;
    }

    private int[] buildColumnIndiciesArray(Cursor cursor, String[] columnNames) {
        int[] columns = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columns[i] = cursor.getColumnIndexOrThrow(columnNames[i]);
        }
        return columns;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (!this.mCompareResultIsValid) {
            return (this.mCursorLeft.isAfterLast() && this.mCursorRight.isAfterLast()) ? false : true;
        }
        switch (this.mCompareResult) {
            case BOTH:
                return (this.mCursorLeft.isLast() && this.mCursorRight.isLast()) ? false : true;
            case LEFT:
                return (this.mCursorLeft.isLast() && this.mCursorRight.isAfterLast()) ? false : true;
            case RIGHT:
                return (this.mCursorLeft.isAfterLast() && this.mCursorRight.isLast()) ? false : true;
            default:
                throw new IllegalStateException("bad value for mCompareResult, " + this.mCompareResult);
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public Result next() {
        if (!hasNext()) {
            throw new IllegalStateException("you must only call next() when hasNext() is true");
        }
        incrementCursors();
        if ($assertionsDisabled || hasNext()) {
            boolean hasLeft = !this.mCursorLeft.isAfterLast();
            boolean hasRight = !this.mCursorRight.isAfterLast();
            if (hasLeft && hasRight) {
                populateValues(this.mValues, this.mCursorLeft, this.mColumnsLeft, 0);
                populateValues(this.mValues, this.mCursorRight, this.mColumnsRight, 1);
                switch (compareStrings(this.mValues)) {
                    case -1:
                        this.mCompareResult = Result.LEFT;
                        break;
                    case 0:
                        this.mCompareResult = Result.BOTH;
                        break;
                    case 1:
                        this.mCompareResult = Result.RIGHT;
                        break;
                }
            } else if (hasLeft) {
                this.mCompareResult = Result.LEFT;
            } else if (!$assertionsDisabled && !hasRight) {
                throw new AssertionError();
            } else {
                this.mCompareResult = Result.RIGHT;
            }
            this.mCompareResultIsValid = true;
            return this.mCompareResult;
        }
        throw new AssertionError();
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    private static void populateValues(String[] values, Cursor cursor, int[] columnIndicies, int startingIndex) {
        if (!$assertionsDisabled && startingIndex != 0 && startingIndex != 1) {
            throw new AssertionError();
        }
        for (int i = 0; i < columnIndicies.length; i++) {
            values[startingIndex + (i * 2)] = cursor.getString(columnIndicies[i]);
        }
    }

    private void incrementCursors() {
        if (this.mCompareResultIsValid) {
            switch (this.mCompareResult) {
                case BOTH:
                    this.mCursorLeft.moveToNext();
                    this.mCursorRight.moveToNext();
                    break;
                case LEFT:
                    this.mCursorLeft.moveToNext();
                    break;
                case RIGHT:
                    this.mCursorRight.moveToNext();
                    break;
            }
            this.mCompareResultIsValid = false;
        }
    }

    private static int compareStrings(String... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("you must specify an even number of values");
        }
        for (int index = 0; index < values.length; index += 2) {
            if (values[index] == null) {
                if (values[index + 1] != null) {
                    return -1;
                }
            } else if (values[index + 1] == null) {
                return 1;
            } else {
                int comp = values[index].compareTo(values[index + 1]);
                if (comp != 0) {
                    return comp < 0 ? -1 : 1;
                }
            }
        }
        return 0;
    }
}