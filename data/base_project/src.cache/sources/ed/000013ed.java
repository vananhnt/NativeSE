package android.text.method;

import android.text.Selection;
import android.text.SpannableStringBuilder;
import java.text.BreakIterator;
import java.util.Locale;

/* loaded from: WordIterator.class */
public class WordIterator implements Selection.PositionIterator {
    private static final int WINDOW_WIDTH = 50;
    private String mString;
    private int mOffsetShift;
    private BreakIterator mIterator;

    public WordIterator() {
        this(Locale.getDefault());
    }

    public WordIterator(Locale locale) {
        this.mIterator = BreakIterator.getWordInstance(locale);
    }

    public void setCharSequence(CharSequence charSequence, int start, int end) {
        this.mOffsetShift = Math.max(0, start - 50);
        int windowEnd = Math.min(charSequence.length(), end + 50);
        if (charSequence instanceof SpannableStringBuilder) {
            this.mString = ((SpannableStringBuilder) charSequence).substring(this.mOffsetShift, windowEnd);
        } else {
            this.mString = charSequence.subSequence(this.mOffsetShift, windowEnd).toString();
        }
        this.mIterator.setText(this.mString);
    }

    @Override // android.text.Selection.PositionIterator
    public int preceding(int offset) {
        int shiftedOffset = offset - this.mOffsetShift;
        do {
            shiftedOffset = this.mIterator.preceding(shiftedOffset);
            if (shiftedOffset == -1) {
                return -1;
            }
        } while (!isOnLetterOrDigit(shiftedOffset));
        return shiftedOffset + this.mOffsetShift;
    }

    @Override // android.text.Selection.PositionIterator
    public int following(int offset) {
        int shiftedOffset = offset - this.mOffsetShift;
        do {
            shiftedOffset = this.mIterator.following(shiftedOffset);
            if (shiftedOffset == -1) {
                return -1;
            }
        } while (!isAfterLetterOrDigit(shiftedOffset));
        return shiftedOffset + this.mOffsetShift;
    }

    public int getBeginning(int offset) {
        int shiftedOffset = offset - this.mOffsetShift;
        checkOffsetIsValid(shiftedOffset);
        if (isOnLetterOrDigit(shiftedOffset)) {
            if (this.mIterator.isBoundary(shiftedOffset)) {
                return shiftedOffset + this.mOffsetShift;
            }
            return this.mIterator.preceding(shiftedOffset) + this.mOffsetShift;
        } else if (isAfterLetterOrDigit(shiftedOffset)) {
            return this.mIterator.preceding(shiftedOffset) + this.mOffsetShift;
        } else {
            return -1;
        }
    }

    public int getEnd(int offset) {
        int shiftedOffset = offset - this.mOffsetShift;
        checkOffsetIsValid(shiftedOffset);
        if (isAfterLetterOrDigit(shiftedOffset)) {
            if (this.mIterator.isBoundary(shiftedOffset)) {
                return shiftedOffset + this.mOffsetShift;
            }
            return this.mIterator.following(shiftedOffset) + this.mOffsetShift;
        } else if (isOnLetterOrDigit(shiftedOffset)) {
            return this.mIterator.following(shiftedOffset) + this.mOffsetShift;
        } else {
            return -1;
        }
    }

    private boolean isAfterLetterOrDigit(int shiftedOffset) {
        if (shiftedOffset >= 1 && shiftedOffset <= this.mString.length()) {
            int codePoint = this.mString.codePointBefore(shiftedOffset);
            return Character.isLetterOrDigit(codePoint);
        }
        return false;
    }

    private boolean isOnLetterOrDigit(int shiftedOffset) {
        if (shiftedOffset >= 0 && shiftedOffset < this.mString.length()) {
            int codePoint = this.mString.codePointAt(shiftedOffset);
            return Character.isLetterOrDigit(codePoint);
        }
        return false;
    }

    private void checkOffsetIsValid(int shiftedOffset) {
        if (shiftedOffset < 0 || shiftedOffset > this.mString.length()) {
            throw new IllegalArgumentException("Invalid offset: " + (shiftedOffset + this.mOffsetShift) + ". Valid range is [" + this.mOffsetShift + ", " + (this.mString.length() + this.mOffsetShift) + "]");
        }
    }
}