package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.util.FloatMath;

/* loaded from: BoringLayout.class */
public class BoringLayout extends Layout implements TextUtils.EllipsizeCallback {
    private static final char FIRST_RIGHT_TO_LEFT = 1424;
    private String mDirect;
    private Paint mPaint;
    int mBottom;
    int mDesc;
    private int mTopPadding;
    private int mBottomPadding;
    private float mMax;
    private int mEllipsizedWidth;
    private int mEllipsizedStart;
    private int mEllipsizedCount;
    private static final TextPaint sTemp = new TextPaint();

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad) {
        return new BoringLayout(source, paint, outerwidth, align, spacingmult, spacingadd, metrics, includepad);
    }

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        return new BoringLayout(source, paint, outerwidth, align, spacingmult, spacingadd, metrics, includepad, ellipsize, ellipsizedWidth);
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad) {
        replaceWith(source, paint, outerwidth, align, spacingmult, spacingadd);
        this.mEllipsizedWidth = outerwidth;
        this.mEllipsizedStart = 0;
        this.mEllipsizedCount = 0;
        init(source, paint, outerwidth, align, spacingmult, spacingadd, metrics, includepad, true);
        return this;
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        boolean trust;
        if (ellipsize == null || ellipsize == TextUtils.TruncateAt.MARQUEE) {
            replaceWith(source, paint, outerwidth, align, spacingmult, spacingadd);
            this.mEllipsizedWidth = outerwidth;
            this.mEllipsizedStart = 0;
            this.mEllipsizedCount = 0;
            trust = true;
        } else {
            replaceWith(TextUtils.ellipsize(source, paint, ellipsizedWidth, ellipsize, true, this), paint, outerwidth, align, spacingmult, spacingadd);
            this.mEllipsizedWidth = ellipsizedWidth;
            trust = false;
        }
        init(getText(), paint, outerwidth, align, spacingmult, spacingadd, metrics, includepad, trust);
        return this;
    }

    public BoringLayout(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad) {
        super(source, paint, outerwidth, align, spacingmult, spacingadd);
        this.mEllipsizedWidth = outerwidth;
        this.mEllipsizedStart = 0;
        this.mEllipsizedCount = 0;
        init(source, paint, outerwidth, align, spacingmult, spacingadd, metrics, includepad, true);
    }

    public BoringLayout(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        super(source, paint, outerwidth, align, spacingmult, spacingadd);
        boolean trust;
        if (ellipsize == null || ellipsize == TextUtils.TruncateAt.MARQUEE) {
            this.mEllipsizedWidth = outerwidth;
            this.mEllipsizedStart = 0;
            this.mEllipsizedCount = 0;
            trust = true;
        } else {
            replaceWith(TextUtils.ellipsize(source, paint, ellipsizedWidth, ellipsize, true, this), paint, outerwidth, align, spacingmult, spacingadd);
            this.mEllipsizedWidth = ellipsizedWidth;
            trust = false;
        }
        init(getText(), paint, outerwidth, align, spacingmult, spacingadd, metrics, includepad, trust);
    }

    void init(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includepad, boolean trustWidth) {
        int spacing;
        if ((source instanceof String) && align == Layout.Alignment.ALIGN_NORMAL) {
            this.mDirect = source.toString();
        } else {
            this.mDirect = null;
        }
        this.mPaint = paint;
        if (includepad) {
            spacing = metrics.bottom - metrics.top;
        } else {
            spacing = metrics.descent - metrics.ascent;
        }
        if (spacingmult != 1.0f || spacingadd != 0.0f) {
            spacing = (int) ((spacing * spacingmult) + spacingadd + 0.5f);
        }
        this.mBottom = spacing;
        if (includepad) {
            this.mDesc = spacing + metrics.top;
        } else {
            this.mDesc = spacing + metrics.ascent;
        }
        if (trustWidth) {
            this.mMax = metrics.width;
        } else {
            TextLine line = TextLine.obtain();
            line.set(paint, source, 0, source.length(), 1, Layout.DIRS_ALL_LEFT_TO_RIGHT, false, null);
            this.mMax = (int) FloatMath.ceil(line.metrics(null));
            TextLine.recycle(line);
        }
        if (includepad) {
            this.mTopPadding = metrics.top - metrics.ascent;
            this.mBottomPadding = metrics.bottom - metrics.descent;
        }
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint) {
        return isBoring(text, paint, TextDirectionHeuristics.FIRSTSTRONG_LTR, null);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, TextDirectionHeuristic textDir) {
        return isBoring(text, paint, textDir, null);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, Metrics metrics) {
        return isBoring(text, paint, TextDirectionHeuristics.FIRSTSTRONG_LTR, metrics);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, TextDirectionHeuristic textDir, Metrics metrics) {
        char[] temp = TextUtils.obtain(500);
        int length = text.length();
        boolean boring = true;
        int i = 0;
        loop0: while (true) {
            if (i >= length) {
                break;
            }
            int j = i + 500;
            if (j > length) {
                j = length;
            }
            TextUtils.getChars(text, i, j, temp, 0);
            int n = j - i;
            for (int a = 0; a < n; a++) {
                char c = temp[a];
                if (c == '\n' || c == '\t' || c >= FIRST_RIGHT_TO_LEFT) {
                    break loop0;
                }
            }
            if (textDir == null || !textDir.isRtl(temp, 0, n)) {
                i += 500;
            } else {
                boring = false;
                break;
            }
        }
        boring = false;
        TextUtils.recycle(temp);
        if (boring && (text instanceof Spanned)) {
            Spanned sp = (Spanned) text;
            Object[] styles = sp.getSpans(0, length, ParagraphStyle.class);
            if (styles.length > 0) {
                boring = false;
            }
        }
        if (boring) {
            Metrics fm = metrics;
            if (fm == null) {
                fm = new Metrics();
            }
            TextLine line = TextLine.obtain();
            line.set(paint, text, 0, length, 1, Layout.DIRS_ALL_LEFT_TO_RIGHT, false, null);
            fm.width = (int) FloatMath.ceil(line.metrics(fm));
            TextLine.recycle(line);
            return fm;
        }
        return null;
    }

    @Override // android.text.Layout
    public int getHeight() {
        return this.mBottom;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return 1;
    }

    @Override // android.text.Layout
    public int getLineTop(int line) {
        if (line == 0) {
            return 0;
        }
        return this.mBottom;
    }

    @Override // android.text.Layout
    public int getLineDescent(int line) {
        return this.mDesc;
    }

    @Override // android.text.Layout
    public int getLineStart(int line) {
        if (line == 0) {
            return 0;
        }
        return getText().length();
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int line) {
        return 1;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int line) {
        return false;
    }

    @Override // android.text.Layout
    public float getLineMax(int line) {
        return this.mMax;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int line) {
        return Layout.DIRS_ALL_LEFT_TO_RIGHT;
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
    public int getEllipsisCount(int line) {
        return this.mEllipsizedCount;
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int line) {
        return this.mEllipsizedStart;
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    @Override // android.text.Layout
    public void draw(Canvas c, Path highlight, Paint highlightpaint, int cursorOffset) {
        if (this.mDirect != null && highlight == null) {
            c.drawText(this.mDirect, 0.0f, this.mBottom - this.mDesc, this.mPaint);
        } else {
            super.draw(c, highlight, highlightpaint, cursorOffset);
        }
    }

    @Override // android.text.TextUtils.EllipsizeCallback
    public void ellipsized(int start, int end) {
        this.mEllipsizedStart = start;
        this.mEllipsizedCount = end - start;
    }

    /* loaded from: BoringLayout$Metrics.class */
    public static class Metrics extends Paint.FontMetricsInt {
        public int width;

        @Override // android.graphics.Paint.FontMetricsInt
        public String toString() {
            return super.toString() + " width=" + this.width;
        }
    }
}