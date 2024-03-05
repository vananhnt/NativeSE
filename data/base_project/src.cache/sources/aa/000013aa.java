package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import gov.nist.core.Separators;
import java.lang.reflect.Array;

/* loaded from: SpannableStringBuilder.class */
public class SpannableStringBuilder implements CharSequence, GetChars, Spannable, Editable, Appendable, GraphicsOperations {
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private InputFilter[] mFilters;
    private char[] mText;
    private int mGapStart;
    private int mGapLength;
    private Object[] mSpans;
    private int[] mSpanStarts;
    private int[] mSpanEnds;
    private int[] mSpanFlags;
    private int mSpanCount;
    private int mSpanCountBeforeAdd;
    private static final int MARK = 1;
    private static final int POINT = 2;
    private static final int PARAGRAPH = 3;
    private static final int START_MASK = 240;
    private static final int END_MASK = 15;
    private static final int START_SHIFT = 4;
    private static final int SPAN_START_AT_START = 4096;
    private static final int SPAN_START_AT_END = 8192;
    private static final int SPAN_END_AT_START = 16384;
    private static final int SPAN_END_AT_END = 32768;
    private static final int SPAN_START_END_MASK = 61440;

    public SpannableStringBuilder() {
        this("");
    }

    public SpannableStringBuilder(CharSequence text) {
        this(text, 0, text.length());
    }

    public SpannableStringBuilder(CharSequence text, int start, int end) {
        this.mFilters = NO_FILTERS;
        int srclen = end - start;
        if (srclen < 0) {
            throw new StringIndexOutOfBoundsException();
        }
        int len = ArrayUtils.idealCharArraySize(srclen + 1);
        this.mText = new char[len];
        this.mGapStart = srclen;
        this.mGapLength = len - srclen;
        TextUtils.getChars(text, start, end, this.mText, 0);
        this.mSpanCount = 0;
        int alloc = ArrayUtils.idealIntArraySize(0);
        this.mSpans = new Object[alloc];
        this.mSpanStarts = new int[alloc];
        this.mSpanEnds = new int[alloc];
        this.mSpanFlags = new int[alloc];
        if (text instanceof Spanned) {
            Spanned sp = (Spanned) text;
            Object[] spans = sp.getSpans(start, end, Object.class);
            for (int i = 0; i < spans.length; i++) {
                if (!(spans[i] instanceof NoCopySpan)) {
                    int st = sp.getSpanStart(spans[i]) - start;
                    int en = sp.getSpanEnd(spans[i]) - start;
                    int fl = sp.getSpanFlags(spans[i]);
                    st = st < 0 ? 0 : st;
                    st = st > end - start ? end - start : st;
                    en = en < 0 ? 0 : en;
                    setSpan(false, spans[i], st, en > end - start ? end - start : en, fl);
                }
            }
        }
    }

    public static SpannableStringBuilder valueOf(CharSequence source) {
        if (source instanceof SpannableStringBuilder) {
            return (SpannableStringBuilder) source;
        }
        return new SpannableStringBuilder(source);
    }

    @Override // java.lang.CharSequence
    public char charAt(int where) {
        int len = length();
        if (where < 0) {
            throw new IndexOutOfBoundsException("charAt: " + where + " < 0");
        }
        if (where >= len) {
            throw new IndexOutOfBoundsException("charAt: " + where + " >= length " + len);
        }
        if (where >= this.mGapStart) {
            return this.mText[where + this.mGapLength];
        }
        return this.mText[where];
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.mText.length - this.mGapLength;
    }

    private void resizeFor(int size) {
        int oldLength = this.mText.length;
        int newLength = ArrayUtils.idealCharArraySize(size + 1);
        int delta = newLength - oldLength;
        if (delta == 0) {
            return;
        }
        char[] newText = new char[newLength];
        System.arraycopy(this.mText, 0, newText, 0, this.mGapStart);
        int after = oldLength - (this.mGapStart + this.mGapLength);
        System.arraycopy(this.mText, oldLength - after, newText, newLength - after, after);
        this.mText = newText;
        this.mGapLength += delta;
        if (this.mGapLength < 1) {
            new Exception("mGapLength < 1").printStackTrace();
        }
        for (int i = 0; i < this.mSpanCount; i++) {
            if (this.mSpanStarts[i] > this.mGapStart) {
                int[] iArr = this.mSpanStarts;
                int i2 = i;
                iArr[i2] = iArr[i2] + delta;
            }
            if (this.mSpanEnds[i] > this.mGapStart) {
                int[] iArr2 = this.mSpanEnds;
                int i3 = i;
                iArr2[i3] = iArr2[i3] + delta;
            }
        }
    }

    private void moveGapTo(int where) {
        int flag;
        int flag2;
        if (where == this.mGapStart) {
            return;
        }
        boolean atEnd = where == length();
        if (where < this.mGapStart) {
            int overlap = this.mGapStart - where;
            System.arraycopy(this.mText, where, this.mText, (this.mGapStart + this.mGapLength) - overlap, overlap);
        } else {
            int overlap2 = where - this.mGapStart;
            System.arraycopy(this.mText, (where + this.mGapLength) - overlap2, this.mText, this.mGapStart, overlap2);
        }
        for (int i = 0; i < this.mSpanCount; i++) {
            int start = this.mSpanStarts[i];
            int end = this.mSpanEnds[i];
            if (start > this.mGapStart) {
                start -= this.mGapLength;
            }
            if (start > where) {
                start += this.mGapLength;
            } else if (start == where && ((flag = (this.mSpanFlags[i] & 240) >> 4) == 2 || (atEnd && flag == 3))) {
                start += this.mGapLength;
            }
            if (end > this.mGapStart) {
                end -= this.mGapLength;
            }
            if (end > where) {
                end += this.mGapLength;
            } else if (end == where && ((flag2 = this.mSpanFlags[i] & 15) == 2 || (atEnd && flag2 == 3))) {
                end += this.mGapLength;
            }
            this.mSpanStarts[i] = start;
            this.mSpanEnds[i] = end;
        }
        this.mGapStart = where;
    }

    @Override // android.text.Editable
    public SpannableStringBuilder insert(int where, CharSequence tb, int start, int end) {
        return replace(where, where, tb, start, end);
    }

    @Override // android.text.Editable
    public SpannableStringBuilder insert(int where, CharSequence tb) {
        return replace(where, where, tb, 0, tb.length());
    }

    @Override // android.text.Editable
    public SpannableStringBuilder delete(int start, int end) {
        SpannableStringBuilder ret = replace(start, end, "", 0, 0);
        if (this.mGapLength > 2 * length()) {
            resizeFor(length());
        }
        return ret;
    }

    @Override // android.text.Editable
    public void clear() {
        replace(0, length(), "", 0, 0);
    }

    @Override // android.text.Editable
    public void clearSpans() {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            Object what = this.mSpans[i];
            int ostart = this.mSpanStarts[i];
            int oend = this.mSpanEnds[i];
            if (ostart > this.mGapStart) {
                ostart -= this.mGapLength;
            }
            if (oend > this.mGapStart) {
                oend -= this.mGapLength;
            }
            this.mSpanCount = i;
            this.mSpans[i] = null;
            sendSpanRemoved(what, ostart, oend);
        }
    }

    @Override // java.lang.Appendable
    public SpannableStringBuilder append(CharSequence text) {
        int length = length();
        return replace(length, length, text, 0, text.length());
    }

    @Override // java.lang.Appendable
    public SpannableStringBuilder append(CharSequence text, int start, int end) {
        int length = length();
        return replace(length, length, text, start, end);
    }

    @Override // java.lang.Appendable
    public SpannableStringBuilder append(char text) {
        return append((CharSequence) String.valueOf(text));
    }

    private void change(int start, int end, CharSequence cs, int csStart, int csEnd) {
        int replacedLength = end - start;
        int replacementLength = csEnd - csStart;
        int nbNewChars = replacementLength - replacedLength;
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            int spanStart = this.mSpanStarts[i];
            if (spanStart > this.mGapStart) {
                spanStart -= this.mGapLength;
            }
            int spanEnd = this.mSpanEnds[i];
            if (spanEnd > this.mGapStart) {
                spanEnd -= this.mGapLength;
            }
            if ((this.mSpanFlags[i] & 51) == 51) {
                int ost = spanStart;
                int oen = spanEnd;
                int clen = length();
                if (spanStart > start && spanStart <= end) {
                    spanStart = end;
                    while (spanStart < clen && (spanStart <= end || charAt(spanStart - 1) != '\n')) {
                        spanStart++;
                    }
                }
                if (spanEnd > start && spanEnd <= end) {
                    spanEnd = end;
                    while (spanEnd < clen && (spanEnd <= end || charAt(spanEnd - 1) != '\n')) {
                        spanEnd++;
                    }
                }
                if (spanStart != ost || spanEnd != oen) {
                    setSpan(false, this.mSpans[i], spanStart, spanEnd, this.mSpanFlags[i]);
                }
            }
            int flags = 0;
            if (spanStart == start) {
                flags = 0 | 4096;
            } else if (spanStart == end + nbNewChars) {
                flags = 0 | 8192;
            }
            if (spanEnd == start) {
                flags |= 16384;
            } else if (spanEnd == end + nbNewChars) {
                flags |= 32768;
            }
            int[] iArr = this.mSpanFlags;
            int i2 = i;
            iArr[i2] = iArr[i2] | flags;
        }
        moveGapTo(end);
        if (nbNewChars >= this.mGapLength) {
            resizeFor((this.mText.length + nbNewChars) - this.mGapLength);
        }
        boolean textIsRemoved = replacementLength == 0;
        if (replacedLength > 0) {
            int i3 = 0;
            while (i3 < this.mSpanCount) {
                if ((this.mSpanFlags[i3] & 33) == 33 && this.mSpanStarts[i3] >= start && this.mSpanStarts[i3] < this.mGapStart + this.mGapLength && this.mSpanEnds[i3] >= start && this.mSpanEnds[i3] < this.mGapStart + this.mGapLength && (textIsRemoved || this.mSpanStarts[i3] > start || this.mSpanEnds[i3] < this.mGapStart)) {
                    removeSpan(i3);
                } else {
                    i3++;
                }
            }
        }
        this.mGapStart += nbNewChars;
        this.mGapLength -= nbNewChars;
        if (this.mGapLength < 1) {
            new Exception("mGapLength < 1").printStackTrace();
        }
        TextUtils.getChars(cs, csStart, csEnd, this.mText, start);
        if (replacedLength > 0) {
            boolean atEnd = this.mGapStart + this.mGapLength == this.mText.length;
            for (int i4 = 0; i4 < this.mSpanCount; i4++) {
                int startFlag = (this.mSpanFlags[i4] & 240) >> 4;
                this.mSpanStarts[i4] = updatedIntervalBound(this.mSpanStarts[i4], start, nbNewChars, startFlag, atEnd, textIsRemoved);
                int endFlag = this.mSpanFlags[i4] & 15;
                this.mSpanEnds[i4] = updatedIntervalBound(this.mSpanEnds[i4], start, nbNewChars, endFlag, atEnd, textIsRemoved);
            }
        }
        this.mSpanCountBeforeAdd = this.mSpanCount;
        if (cs instanceof Spanned) {
            Spanned sp = (Spanned) cs;
            Object[] spans = sp.getSpans(csStart, csEnd, Object.class);
            for (int i5 = 0; i5 < spans.length; i5++) {
                int st = sp.getSpanStart(spans[i5]);
                int en = sp.getSpanEnd(spans[i5]);
                if (st < csStart) {
                    st = csStart;
                }
                if (en > csEnd) {
                    en = csEnd;
                }
                if (getSpanStart(spans[i5]) < 0) {
                    setSpan(false, spans[i5], (st - csStart) + start, (en - csStart) + start, sp.getSpanFlags(spans[i5]));
                }
            }
        }
    }

    private int updatedIntervalBound(int offset, int start, int nbNewChars, int flag, boolean atEnd, boolean textIsRemoved) {
        if (offset >= start && offset < this.mGapStart + this.mGapLength) {
            if (flag == 2) {
                if (textIsRemoved || offset > start) {
                    return this.mGapStart + this.mGapLength;
                }
            } else if (flag == 3) {
                if (atEnd) {
                    return this.mGapStart + this.mGapLength;
                }
            } else if (textIsRemoved || offset < this.mGapStart - nbNewChars) {
                return start;
            } else {
                return this.mGapStart;
            }
        }
        return offset;
    }

    private void removeSpan(int i) {
        Object object = this.mSpans[i];
        int start = this.mSpanStarts[i];
        int end = this.mSpanEnds[i];
        if (start > this.mGapStart) {
            start -= this.mGapLength;
        }
        if (end > this.mGapStart) {
            end -= this.mGapLength;
        }
        int count = this.mSpanCount - (i + 1);
        System.arraycopy(this.mSpans, i + 1, this.mSpans, i, count);
        System.arraycopy(this.mSpanStarts, i + 1, this.mSpanStarts, i, count);
        System.arraycopy(this.mSpanEnds, i + 1, this.mSpanEnds, i, count);
        System.arraycopy(this.mSpanFlags, i + 1, this.mSpanFlags, i, count);
        this.mSpanCount--;
        this.mSpans[this.mSpanCount] = null;
        sendSpanRemoved(object, start, end);
    }

    @Override // android.text.Editable
    public SpannableStringBuilder replace(int start, int end, CharSequence tb) {
        return replace(start, end, tb, 0, tb.length());
    }

    @Override // android.text.Editable
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        checkRange("replace", start, end);
        int filtercount = this.mFilters.length;
        for (int i = 0; i < filtercount; i++) {
            CharSequence repl = this.mFilters[i].filter(tb, tbstart, tbend, this, start, end);
            if (repl != null) {
                tb = repl;
                tbstart = 0;
                tbend = repl.length();
            }
        }
        int origLen = end - start;
        int newLen = tbend - tbstart;
        if (origLen == 0 && newLen == 0 && !hasNonExclusiveExclusiveSpanAt(tb, tbstart)) {
            return this;
        }
        TextWatcher[] textWatchers = (TextWatcher[]) getSpans(start, start + origLen, TextWatcher.class);
        sendBeforeTextChanged(textWatchers, start, origLen, newLen);
        boolean adjustSelection = (origLen == 0 || newLen == 0) ? false : true;
        int selectionStart = 0;
        int selectionEnd = 0;
        if (adjustSelection) {
            selectionStart = Selection.getSelectionStart(this);
            selectionEnd = Selection.getSelectionEnd(this);
        }
        change(start, end, tb, tbstart, tbend);
        if (adjustSelection) {
            if (selectionStart > start && selectionStart < end) {
                int offset = ((selectionStart - start) * newLen) / origLen;
                int selectionStart2 = start + offset;
                setSpan(false, Selection.SELECTION_START, selectionStart2, selectionStart2, 34);
            }
            if (selectionEnd > start && selectionEnd < end) {
                int offset2 = ((selectionEnd - start) * newLen) / origLen;
                int selectionEnd2 = start + offset2;
                setSpan(false, Selection.SELECTION_END, selectionEnd2, selectionEnd2, 34);
            }
        }
        sendTextChanged(textWatchers, start, origLen, newLen);
        sendAfterTextChanged(textWatchers);
        sendToSpanWatchers(start, end, newLen - origLen);
        return this;
    }

    private static boolean hasNonExclusiveExclusiveSpanAt(CharSequence text, int offset) {
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            Object[] spans = spanned.getSpans(offset, offset, Object.class);
            for (Object span : spans) {
                int flags = spanned.getSpanFlags(span);
                if (flags != 33) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void sendToSpanWatchers(int replaceStart, int replaceEnd, int nbNewChars) {
        for (int i = 0; i < this.mSpanCountBeforeAdd; i++) {
            int spanStart = this.mSpanStarts[i];
            int spanEnd = this.mSpanEnds[i];
            if (spanStart > this.mGapStart) {
                spanStart -= this.mGapLength;
            }
            if (spanEnd > this.mGapStart) {
                spanEnd -= this.mGapLength;
            }
            int spanFlags = this.mSpanFlags[i];
            int newReplaceEnd = replaceEnd + nbNewChars;
            boolean spanChanged = false;
            int previousSpanStart = spanStart;
            if (spanStart > newReplaceEnd) {
                if (nbNewChars != 0) {
                    previousSpanStart -= nbNewChars;
                    spanChanged = true;
                }
            } else if (spanStart >= replaceStart && ((spanStart != replaceStart || (spanFlags & 4096) != 4096) && (spanStart != newReplaceEnd || (spanFlags & 8192) != 8192))) {
                spanChanged = true;
            }
            int previousSpanEnd = spanEnd;
            if (spanEnd > newReplaceEnd) {
                if (nbNewChars != 0) {
                    previousSpanEnd -= nbNewChars;
                    spanChanged = true;
                }
            } else if (spanEnd >= replaceStart && ((spanEnd != replaceStart || (spanFlags & 16384) != 16384) && (spanEnd != newReplaceEnd || (spanFlags & 32768) != 32768))) {
                spanChanged = true;
            }
            if (spanChanged) {
                sendSpanChanged(this.mSpans[i], previousSpanStart, previousSpanEnd, spanStart, spanEnd);
            }
            int[] iArr = this.mSpanFlags;
            int i2 = i;
            iArr[i2] = iArr[i2] & (-61441);
        }
        for (int i3 = this.mSpanCountBeforeAdd; i3 < this.mSpanCount; i3++) {
            int spanStart2 = this.mSpanStarts[i3];
            int spanEnd2 = this.mSpanEnds[i3];
            if (spanStart2 > this.mGapStart) {
                spanStart2 -= this.mGapLength;
            }
            if (spanEnd2 > this.mGapStart) {
                spanEnd2 -= this.mGapLength;
            }
            sendSpanAdded(this.mSpans[i3], spanStart2, spanEnd2);
        }
    }

    @Override // android.text.Spannable
    public void setSpan(Object what, int start, int end, int flags) {
        setSpan(true, what, start, end, flags);
    }

    private void setSpan(boolean send, Object what, int start, int end, int flags) {
        checkRange("setSpan", start, end);
        int flagsStart = (flags & 240) >> 4;
        if (flagsStart == 3 && start != 0 && start != length()) {
            char c = charAt(start - 1);
            if (c != '\n') {
                throw new RuntimeException("PARAGRAPH span must start at paragraph boundary");
            }
        }
        int flagsEnd = flags & 15;
        if (flagsEnd == 3 && end != 0 && end != length()) {
            char c2 = charAt(end - 1);
            if (c2 != '\n') {
                throw new RuntimeException("PARAGRAPH span must end at paragraph boundary");
            }
        }
        if (flagsStart == 2 && flagsEnd == 1 && start == end) {
            if (send) {
                Log.e("SpannableStringBuilder", "SPAN_EXCLUSIVE_EXCLUSIVE spans cannot have a zero length");
                return;
            }
            return;
        }
        if (start > this.mGapStart) {
            start += this.mGapLength;
        } else if (start == this.mGapStart && (flagsStart == 2 || (flagsStart == 3 && start == length()))) {
            start += this.mGapLength;
        }
        if (end > this.mGapStart) {
            end += this.mGapLength;
        } else if (end == this.mGapStart && (flagsEnd == 2 || (flagsEnd == 3 && end == length()))) {
            end += this.mGapLength;
        }
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = 0; i < count; i++) {
            if (spans[i] == what) {
                int ostart = this.mSpanStarts[i];
                int oend = this.mSpanEnds[i];
                if (ostart > this.mGapStart) {
                    ostart -= this.mGapLength;
                }
                if (oend > this.mGapStart) {
                    oend -= this.mGapLength;
                }
                this.mSpanStarts[i] = start;
                this.mSpanEnds[i] = end;
                this.mSpanFlags[i] = flags;
                if (send) {
                    sendSpanChanged(what, ostart, oend, start, end);
                    return;
                }
                return;
            }
        }
        if (this.mSpanCount + 1 >= this.mSpans.length) {
            int newsize = ArrayUtils.idealIntArraySize(this.mSpanCount + 1);
            Object[] newspans = new Object[newsize];
            int[] newspanstarts = new int[newsize];
            int[] newspanends = new int[newsize];
            int[] newspanflags = new int[newsize];
            System.arraycopy(this.mSpans, 0, newspans, 0, this.mSpanCount);
            System.arraycopy(this.mSpanStarts, 0, newspanstarts, 0, this.mSpanCount);
            System.arraycopy(this.mSpanEnds, 0, newspanends, 0, this.mSpanCount);
            System.arraycopy(this.mSpanFlags, 0, newspanflags, 0, this.mSpanCount);
            this.mSpans = newspans;
            this.mSpanStarts = newspanstarts;
            this.mSpanEnds = newspanends;
            this.mSpanFlags = newspanflags;
        }
        this.mSpans[this.mSpanCount] = what;
        this.mSpanStarts[this.mSpanCount] = start;
        this.mSpanEnds[this.mSpanCount] = end;
        this.mSpanFlags[this.mSpanCount] = flags;
        this.mSpanCount++;
        if (send) {
            sendSpanAdded(what, start, end);
        }
    }

    @Override // android.text.Spannable
    public void removeSpan(Object what) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            if (this.mSpans[i] == what) {
                removeSpan(i);
                return;
            }
        }
    }

    @Override // android.text.Spanned
    public int getSpanStart(Object what) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                int where = this.mSpanStarts[i];
                if (where > this.mGapStart) {
                    where -= this.mGapLength;
                }
                return where;
            }
        }
        return -1;
    }

    @Override // android.text.Spanned
    public int getSpanEnd(Object what) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                int where = this.mSpanEnds[i];
                if (where > this.mGapStart) {
                    where -= this.mGapLength;
                }
                return where;
            }
        }
        return -1;
    }

    @Override // android.text.Spanned
    public int getSpanFlags(Object what) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                return this.mSpanFlags[i];
            }
        }
        return 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.text.Spanned
    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        if (kind == null) {
            return (T[]) ArrayUtils.emptyArray(kind);
        }
        int spanCount = this.mSpanCount;
        Object[] spans = this.mSpans;
        int[] starts = this.mSpanStarts;
        int[] ends = this.mSpanEnds;
        int[] flags = this.mSpanFlags;
        int gapstart = this.mGapStart;
        int gaplen = this.mGapLength;
        int count = 0;
        T[] ret = null;
        Object obj = null;
        int i = 0;
        while (i < spanCount) {
            int spanStart = starts[i];
            if (spanStart > gapstart) {
                spanStart -= gaplen;
            }
            if (spanStart <= queryEnd) {
                int spanEnd = ends[i];
                if (spanEnd > gapstart) {
                    spanEnd -= gaplen;
                }
                if (spanEnd >= queryStart && ((spanStart == spanEnd || queryStart == queryEnd || (spanStart != queryEnd && spanEnd != queryStart)) && kind.isInstance(spans[i]))) {
                    if (count == 0) {
                        obj = spans[i];
                        count++;
                    } else {
                        ret = ret;
                        if (count == 1) {
                            Object[] objArr = (Object[]) Array.newInstance((Class<?>) kind, (spanCount - i) + 1);
                            objArr[0] = obj;
                            ret = objArr;
                        }
                        int prio = flags[i] & Spanned.SPAN_PRIORITY;
                        if (prio != 0) {
                            int j = 0;
                            while (j < count) {
                                int p = getSpanFlags(ret[j]) & Spanned.SPAN_PRIORITY;
                                if (prio > p) {
                                    break;
                                }
                                j++;
                            }
                            System.arraycopy(ret, j, ret, j + 1, count - j);
                            ret[j] = spans[i];
                            count++;
                        } else {
                            int i2 = count;
                            count++;
                            ret[i2] = spans[i];
                        }
                    }
                }
            }
            i++;
            ret = ret;
        }
        if (count == 0) {
            return (T[]) ArrayUtils.emptyArray(kind);
        }
        if (count == 1) {
            T[] ret2 = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, 1));
            ret2[0] = obj;
            return ret2;
        } else if (count == ret.length) {
            return ret;
        } else {
            T[] nret = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, count));
            System.arraycopy(ret, 0, nret, 0, count);
            return nret;
        }
    }

    @Override // android.text.Spanned
    public int nextSpanTransition(int start, int limit, Class kind) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        int[] starts = this.mSpanStarts;
        int[] ends = this.mSpanEnds;
        int gapstart = this.mGapStart;
        int gaplen = this.mGapLength;
        if (kind == null) {
            kind = Object.class;
        }
        for (int i = 0; i < count; i++) {
            int st = starts[i];
            int en = ends[i];
            if (st > gapstart) {
                st -= gaplen;
            }
            if (en > gapstart) {
                en -= gaplen;
            }
            if (st > start && st < limit && kind.isInstance(spans[i])) {
                limit = st;
            }
            if (en > start && en < limit && kind.isInstance(spans[i])) {
                limit = en;
            }
        }
        return limit;
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return new SpannableStringBuilder(this, start, end);
    }

    @Override // android.text.GetChars
    public void getChars(int start, int end, char[] dest, int destoff) {
        checkRange("getChars", start, end);
        if (end <= this.mGapStart) {
            System.arraycopy(this.mText, start, dest, destoff, end - start);
        } else if (start >= this.mGapStart) {
            System.arraycopy(this.mText, start + this.mGapLength, dest, destoff, end - start);
        } else {
            System.arraycopy(this.mText, start, dest, destoff, this.mGapStart - start);
            System.arraycopy(this.mText, this.mGapStart + this.mGapLength, dest, destoff + (this.mGapStart - start), end - this.mGapStart);
        }
    }

    @Override // java.lang.CharSequence
    public String toString() {
        int len = length();
        char[] buf = new char[len];
        getChars(0, len, buf, 0);
        return new String(buf);
    }

    public String substring(int start, int end) {
        char[] buf = new char[end - start];
        getChars(start, end, buf, 0);
        return new String(buf);
    }

    private void sendBeforeTextChanged(TextWatcher[] watchers, int start, int before, int after) {
        for (TextWatcher textWatcher : watchers) {
            textWatcher.beforeTextChanged(this, start, before, after);
        }
    }

    private void sendTextChanged(TextWatcher[] watchers, int start, int before, int after) {
        for (TextWatcher textWatcher : watchers) {
            textWatcher.onTextChanged(this, start, before, after);
        }
    }

    private void sendAfterTextChanged(TextWatcher[] watchers) {
        for (TextWatcher textWatcher : watchers) {
            textWatcher.afterTextChanged(this);
        }
    }

    private void sendSpanAdded(Object what, int start, int end) {
        SpanWatcher[] recip = (SpanWatcher[]) getSpans(start, end, SpanWatcher.class);
        for (SpanWatcher spanWatcher : recip) {
            spanWatcher.onSpanAdded(this, what, start, end);
        }
    }

    private void sendSpanRemoved(Object what, int start, int end) {
        SpanWatcher[] recip = (SpanWatcher[]) getSpans(start, end, SpanWatcher.class);
        for (SpanWatcher spanWatcher : recip) {
            spanWatcher.onSpanRemoved(this, what, start, end);
        }
    }

    private void sendSpanChanged(Object what, int oldStart, int oldEnd, int start, int end) {
        SpanWatcher[] spanWatchers = (SpanWatcher[]) getSpans(Math.min(oldStart, start), Math.min(Math.max(oldEnd, end), length()), SpanWatcher.class);
        for (SpanWatcher spanWatcher : spanWatchers) {
            spanWatcher.onSpanChanged(this, what, oldStart, oldEnd, start, end);
        }
    }

    private static String region(int start, int end) {
        return Separators.LPAREN + start + " ... " + end + Separators.RPAREN;
    }

    private void checkRange(String operation, int start, int end) {
        if (end < start) {
            throw new IndexOutOfBoundsException(operation + Separators.SP + region(start, end) + " has end before start");
        }
        int len = length();
        if (start > len || end > len) {
            throw new IndexOutOfBoundsException(operation + Separators.SP + region(start, end) + " ends beyond length " + len);
        }
        if (start < 0 || end < 0) {
            throw new IndexOutOfBoundsException(operation + Separators.SP + region(start, end) + " starts before 0");
        }
    }

    @Override // android.text.GraphicsOperations
    public void drawText(Canvas c, int start, int end, float x, float y, Paint p) {
        checkRange("drawText", start, end);
        if (end <= this.mGapStart) {
            c.drawText(this.mText, start, end - start, x, y, p);
        } else if (start >= this.mGapStart) {
            c.drawText(this.mText, start + this.mGapLength, end - start, x, y, p);
        } else {
            char[] buf = TextUtils.obtain(end - start);
            getChars(start, end, buf, 0);
            c.drawText(buf, 0, end - start, x, y, p);
            TextUtils.recycle(buf);
        }
    }

    @Override // android.text.GraphicsOperations
    public void drawTextRun(Canvas c, int start, int end, int contextStart, int contextEnd, float x, float y, int flags, Paint p) {
        checkRange("drawTextRun", start, end);
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        if (contextEnd <= this.mGapStart) {
            c.drawTextRun(this.mText, start, len, contextStart, contextLen, x, y, flags, p);
        } else if (contextStart >= this.mGapStart) {
            c.drawTextRun(this.mText, start + this.mGapLength, len, contextStart + this.mGapLength, contextLen, x, y, flags, p);
        } else {
            char[] buf = TextUtils.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            c.drawTextRun(buf, start - contextStart, len, 0, contextLen, x, y, flags, p);
            TextUtils.recycle(buf);
        }
    }

    @Override // android.text.GraphicsOperations
    public float measureText(int start, int end, Paint p) {
        float ret;
        checkRange("measureText", start, end);
        if (end <= this.mGapStart) {
            ret = p.measureText(this.mText, start, end - start);
        } else if (start >= this.mGapStart) {
            ret = p.measureText(this.mText, start + this.mGapLength, end - start);
        } else {
            char[] buf = TextUtils.obtain(end - start);
            getChars(start, end, buf, 0);
            ret = p.measureText(buf, 0, end - start);
            TextUtils.recycle(buf);
        }
        return ret;
    }

    @Override // android.text.GraphicsOperations
    public int getTextWidths(int start, int end, float[] widths, Paint p) {
        int ret;
        checkRange("getTextWidths", start, end);
        if (end <= this.mGapStart) {
            ret = p.getTextWidths(this.mText, start, end - start, widths);
        } else if (start >= this.mGapStart) {
            ret = p.getTextWidths(this.mText, start + this.mGapLength, end - start, widths);
        } else {
            char[] buf = TextUtils.obtain(end - start);
            getChars(start, end, buf, 0);
            ret = p.getTextWidths(buf, 0, end - start, widths);
            TextUtils.recycle(buf);
        }
        return ret;
    }

    @Override // android.text.GraphicsOperations
    public float getTextRunAdvances(int start, int end, int contextStart, int contextEnd, int flags, float[] advances, int advancesPos, Paint p) {
        float ret;
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        if (end <= this.mGapStart) {
            ret = p.getTextRunAdvances(this.mText, start, len, contextStart, contextLen, flags, advances, advancesPos);
        } else if (start >= this.mGapStart) {
            ret = p.getTextRunAdvances(this.mText, start + this.mGapLength, len, contextStart + this.mGapLength, contextLen, flags, advances, advancesPos);
        } else {
            char[] buf = TextUtils.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            ret = p.getTextRunAdvances(buf, start - contextStart, len, 0, contextLen, flags, advances, advancesPos);
            TextUtils.recycle(buf);
        }
        return ret;
    }

    @Override // android.text.GraphicsOperations
    @Deprecated
    public int getTextRunCursor(int contextStart, int contextEnd, int flags, int offset, int cursorOpt, Paint p) {
        int ret;
        int contextLen = contextEnd - contextStart;
        if (contextEnd <= this.mGapStart) {
            ret = p.getTextRunCursor(this.mText, contextStart, contextLen, flags, offset, cursorOpt);
        } else if (contextStart >= this.mGapStart) {
            ret = p.getTextRunCursor(this.mText, contextStart + this.mGapLength, contextLen, flags, offset + this.mGapLength, cursorOpt) - this.mGapLength;
        } else {
            char[] buf = TextUtils.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            ret = p.getTextRunCursor(buf, 0, contextLen, flags, offset - contextStart, cursorOpt) + contextStart;
            TextUtils.recycle(buf);
        }
        return ret;
    }

    @Override // android.text.Editable
    public void setFilters(InputFilter[] filters) {
        if (filters == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = filters;
    }

    @Override // android.text.Editable
    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    public boolean equals(Object o) {
        if ((o instanceof Spanned) && toString().equals(o.toString())) {
            Spanned other = (Spanned) o;
            Object[] otherSpans = other.getSpans(0, other.length(), Object.class);
            if (this.mSpanCount == otherSpans.length) {
                for (int i = 0; i < this.mSpanCount; i++) {
                    Object thisSpan = this.mSpans[i];
                    Object otherSpan = otherSpans[i];
                    if (thisSpan == this) {
                        if (other != otherSpan || getSpanStart(thisSpan) != other.getSpanStart(otherSpan) || getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) || getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
                            return false;
                        }
                    } else if (!thisSpan.equals(otherSpan) || getSpanStart(thisSpan) != other.getSpanStart(otherSpan) || getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) || getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int hash = toString().hashCode();
        int hash2 = (hash * 31) + this.mSpanCount;
        for (int i = 0; i < this.mSpanCount; i++) {
            Object span = this.mSpans[i];
            if (span != this) {
                hash2 = (hash2 * 31) + span.hashCode();
            }
            hash2 = (((((hash2 * 31) + getSpanStart(span)) * 31) + getSpanEnd(span)) * 31) + getSpanFlags(span);
        }
        return hash2;
    }
}