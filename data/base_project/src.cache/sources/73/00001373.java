package android.text;

import android.graphics.Paint;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.UpdateLayout;
import com.android.internal.util.ArrayUtils;
import java.lang.ref.WeakReference;

/* loaded from: DynamicLayout.class */
public class DynamicLayout extends Layout {
    private static final int PRIORITY = 128;
    private static final int BLOCK_MINIMUM_CHARACTER_LENGTH = 400;
    private CharSequence mBase;
    private CharSequence mDisplay;
    private ChangeWatcher mWatcher;
    private boolean mIncludePad;
    private boolean mEllipsize;
    private int mEllipsizedWidth;
    private TextUtils.TruncateAt mEllipsizeAt;
    private PackedIntVector mInts;
    private PackedObjectVector<Layout.Directions> mObjects;
    public static final int INVALID_BLOCK_INDEX = -1;
    private int[] mBlockEndLines;
    private int[] mBlockIndices;
    private int mNumberOfBlocks;
    private int mIndexFirstChangedBlock;
    private int mTopPadding;
    private int mBottomPadding;
    private static StaticLayout sStaticLayout = new StaticLayout(null);
    private static final Object[] sLock = new Object[0];
    private static final int START = 0;
    private static final int DIR = 0;
    private static final int TAB = 0;
    private static final int TOP = 1;
    private static final int DESCENT = 2;
    private static final int COLUMNS_NORMAL = 3;
    private static final int ELLIPSIS_START = 3;
    private static final int ELLIPSIS_COUNT = 4;
    private static final int COLUMNS_ELLIPSIZE = 5;
    private static final int START_MASK = 536870911;
    private static final int DIR_SHIFT = 30;
    private static final int TAB_MASK = 536870912;
    private static final int ELLIPSIS_UNDEFINED = Integer.MIN_VALUE;

    public DynamicLayout(CharSequence base, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(base, base, paint, width, align, spacingmult, spacingadd, includepad);
    }

    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(base, display, paint, width, align, spacingmult, spacingadd, includepad, null, 0);
    }

    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        this(base, display, paint, width, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth);
    }

    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Layout.Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        super(ellipsize == null ? display : display instanceof Spanned ? new Layout.SpannedEllipsizer(display) : new Layout.Ellipsizer(display), paint, width, align, textDir, spacingmult, spacingadd);
        int[] start;
        this.mBase = base;
        this.mDisplay = display;
        if (ellipsize != null) {
            this.mInts = new PackedIntVector(5);
            this.mEllipsizedWidth = ellipsizedWidth;
            this.mEllipsizeAt = ellipsize;
        } else {
            this.mInts = new PackedIntVector(3);
            this.mEllipsizedWidth = width;
            this.mEllipsizeAt = null;
        }
        this.mObjects = new PackedObjectVector<>(1);
        this.mIncludePad = includepad;
        if (ellipsize != null) {
            Layout.Ellipsizer e = (Layout.Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = ellipsizedWidth;
            e.mMethod = ellipsize;
            this.mEllipsize = true;
        }
        if (ellipsize != null) {
            start = new int[5];
            start[3] = Integer.MIN_VALUE;
        } else {
            start = new int[3];
        }
        Layout.Directions[] dirs = {DIRS_ALL_LEFT_TO_RIGHT};
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int asc = fm.ascent;
        int desc = fm.descent;
        start[0] = 1073741824;
        start[1] = 0;
        start[2] = desc;
        this.mInts.insertAt(0, start);
        start[1] = desc - asc;
        this.mInts.insertAt(1, start);
        this.mObjects.insertAt(0, dirs);
        reflow(base, 0, 0, base.length());
        if (base instanceof Spannable) {
            if (this.mWatcher == null) {
                this.mWatcher = new ChangeWatcher(this);
            }
            Spannable sp = (Spannable) base;
            ChangeWatcher[] spans = (ChangeWatcher[]) sp.getSpans(0, sp.length(), ChangeWatcher.class);
            for (ChangeWatcher changeWatcher : spans) {
                sp.removeSpan(changeWatcher);
            }
            sp.setSpan(this.mWatcher, 0, base.length(), 8388626);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0134  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x014b  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0159 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void reflow(java.lang.CharSequence r15, int r16, int r17, int r18) {
        /*
            Method dump skipped, instructions count: 868
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.DynamicLayout.reflow(java.lang.CharSequence, int, int, int):void");
    }

    private void createBlocks() {
        int offset = 400;
        this.mNumberOfBlocks = 0;
        CharSequence text = this.mDisplay;
        while (true) {
            int offset2 = TextUtils.indexOf(text, '\n', offset);
            if (offset2 < 0) {
                break;
            }
            addBlockAtOffset(offset2);
            offset = offset2 + 400;
        }
        addBlockAtOffset(text.length());
        this.mBlockIndices = new int[this.mBlockEndLines.length];
        for (int i = 0; i < this.mBlockEndLines.length; i++) {
            this.mBlockIndices[i] = -1;
        }
    }

    private void addBlockAtOffset(int offset) {
        int line = getLineForOffset(offset);
        if (this.mBlockEndLines == null) {
            this.mBlockEndLines = new int[ArrayUtils.idealIntArraySize(1)];
            this.mBlockEndLines[this.mNumberOfBlocks] = line;
            this.mNumberOfBlocks++;
            return;
        }
        int previousBlockEndLine = this.mBlockEndLines[this.mNumberOfBlocks - 1];
        if (line > previousBlockEndLine) {
            if (this.mNumberOfBlocks == this.mBlockEndLines.length) {
                int[] blockEndLines = new int[ArrayUtils.idealIntArraySize(this.mNumberOfBlocks + 1)];
                System.arraycopy(this.mBlockEndLines, 0, blockEndLines, 0, this.mNumberOfBlocks);
                this.mBlockEndLines = blockEndLines;
            }
            this.mBlockEndLines[this.mNumberOfBlocks] = line;
            this.mNumberOfBlocks++;
        }
    }

    void updateBlocks(int startLine, int endLine, int newLineCount) {
        int newFirstChangedBlock;
        if (this.mBlockEndLines == null) {
            createBlocks();
            return;
        }
        int firstBlock = -1;
        int lastBlock = -1;
        int i = 0;
        while (true) {
            if (i < this.mNumberOfBlocks) {
                if (this.mBlockEndLines[i] < startLine) {
                    i++;
                } else {
                    firstBlock = i;
                    break;
                }
            } else {
                break;
            }
        }
        int i2 = firstBlock;
        while (true) {
            if (i2 < this.mNumberOfBlocks) {
                if (this.mBlockEndLines[i2] < endLine) {
                    i2++;
                } else {
                    lastBlock = i2;
                    break;
                }
            } else {
                break;
            }
        }
        int lastBlockEndLine = this.mBlockEndLines[lastBlock];
        boolean createBlockBefore = startLine > (firstBlock == 0 ? 0 : this.mBlockEndLines[firstBlock - 1] + 1);
        boolean createBlock = newLineCount > 0;
        boolean createBlockAfter = endLine < this.mBlockEndLines[lastBlock];
        int numAddedBlocks = createBlockBefore ? 0 + 1 : 0;
        if (createBlock) {
            numAddedBlocks++;
        }
        if (createBlockAfter) {
            numAddedBlocks++;
        }
        int numRemovedBlocks = (lastBlock - firstBlock) + 1;
        int newNumberOfBlocks = (this.mNumberOfBlocks + numAddedBlocks) - numRemovedBlocks;
        if (newNumberOfBlocks == 0) {
            this.mBlockEndLines[0] = 0;
            this.mBlockIndices[0] = -1;
            this.mNumberOfBlocks = 1;
            return;
        }
        if (newNumberOfBlocks > this.mBlockEndLines.length) {
            int newSize = ArrayUtils.idealIntArraySize(newNumberOfBlocks);
            int[] blockEndLines = new int[newSize];
            int[] blockIndices = new int[newSize];
            System.arraycopy(this.mBlockEndLines, 0, blockEndLines, 0, firstBlock);
            System.arraycopy(this.mBlockIndices, 0, blockIndices, 0, firstBlock);
            System.arraycopy(this.mBlockEndLines, lastBlock + 1, blockEndLines, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
            System.arraycopy(this.mBlockIndices, lastBlock + 1, blockIndices, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
            this.mBlockEndLines = blockEndLines;
            this.mBlockIndices = blockIndices;
        } else {
            System.arraycopy(this.mBlockEndLines, lastBlock + 1, this.mBlockEndLines, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
            System.arraycopy(this.mBlockIndices, lastBlock + 1, this.mBlockIndices, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
        }
        this.mNumberOfBlocks = newNumberOfBlocks;
        int deltaLines = newLineCount - ((endLine - startLine) + 1);
        if (deltaLines != 0) {
            newFirstChangedBlock = firstBlock + numAddedBlocks;
            for (int i3 = newFirstChangedBlock; i3 < this.mNumberOfBlocks; i3++) {
                int[] iArr = this.mBlockEndLines;
                int i4 = i3;
                iArr[i4] = iArr[i4] + deltaLines;
            }
        } else {
            newFirstChangedBlock = this.mNumberOfBlocks;
        }
        this.mIndexFirstChangedBlock = Math.min(this.mIndexFirstChangedBlock, newFirstChangedBlock);
        int blockIndex = firstBlock;
        if (createBlockBefore) {
            this.mBlockEndLines[blockIndex] = startLine - 1;
            this.mBlockIndices[blockIndex] = -1;
            blockIndex++;
        }
        if (createBlock) {
            this.mBlockEndLines[blockIndex] = (startLine + newLineCount) - 1;
            this.mBlockIndices[blockIndex] = -1;
            blockIndex++;
        }
        if (createBlockAfter) {
            this.mBlockEndLines[blockIndex] = lastBlockEndLine + deltaLines;
            this.mBlockIndices[blockIndex] = -1;
        }
    }

    void setBlocksDataForTest(int[] blockEndLines, int[] blockIndices, int numberOfBlocks) {
        this.mBlockEndLines = new int[blockEndLines.length];
        this.mBlockIndices = new int[blockIndices.length];
        System.arraycopy(blockEndLines, 0, this.mBlockEndLines, 0, blockEndLines.length);
        System.arraycopy(blockIndices, 0, this.mBlockIndices, 0, blockIndices.length);
        this.mNumberOfBlocks = numberOfBlocks;
    }

    public int[] getBlockEndLines() {
        return this.mBlockEndLines;
    }

    public int[] getBlockIndices() {
        return this.mBlockIndices;
    }

    public int getNumberOfBlocks() {
        return this.mNumberOfBlocks;
    }

    public int getIndexFirstChangedBlock() {
        return this.mIndexFirstChangedBlock;
    }

    public void setIndexFirstChangedBlock(int i) {
        this.mIndexFirstChangedBlock = i;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mInts.size() - 1;
    }

    @Override // android.text.Layout
    public int getLineTop(int line) {
        return this.mInts.getValue(line, 1);
    }

    @Override // android.text.Layout
    public int getLineDescent(int line) {
        return this.mInts.getValue(line, 2);
    }

    @Override // android.text.Layout
    public int getLineStart(int line) {
        return this.mInts.getValue(line, 0) & 536870911;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int line) {
        return (this.mInts.getValue(line, 0) & 536870912) != 0;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int line) {
        return this.mInts.getValue(line, 0) >> 30;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int line) {
        return this.mObjects.getValue(line, 0);
    }

    @Override // android.text.Layout
    public int getTopPadding() {
        return this.mTopPadding;
    }

    @Override // android.text.Layout
    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    /* loaded from: DynamicLayout$ChangeWatcher.class */
    private static class ChangeWatcher implements TextWatcher, SpanWatcher {
        private WeakReference<DynamicLayout> mLayout;

        public ChangeWatcher(DynamicLayout layout) {
            this.mLayout = new WeakReference<>(layout);
        }

        private void reflow(CharSequence s, int where, int before, int after) {
            DynamicLayout ml = this.mLayout.get();
            if (ml != null) {
                ml.reflow(s, where, before, after);
            } else if (s instanceof Spannable) {
                ((Spannable) s).removeSpan(this);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int where, int before, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int where, int before, int after) {
            reflow(s, where, before, after);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable s, Object o, int start, int end) {
            if (o instanceof UpdateLayout) {
                reflow(s, start, end - start, end - start);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable s, Object o, int start, int end) {
            if (o instanceof UpdateLayout) {
                reflow(s, start, end - start, end - start);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable s, Object o, int start, int end, int nstart, int nend) {
            if (o instanceof UpdateLayout) {
                reflow(s, start, end - start, end - start);
                reflow(s, nstart, nend - nstart, nend - nstart);
            }
        }
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int line) {
        if (this.mEllipsizeAt == null) {
            return 0;
        }
        return this.mInts.getValue(line, 3);
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int line) {
        if (this.mEllipsizeAt == null) {
            return 0;
        }
        return this.mInts.getValue(line, 4);
    }
}