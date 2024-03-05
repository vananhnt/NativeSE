package android.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import com.android.internal.util.ArrayUtils;

/* loaded from: TextLine.class */
class TextLine {
    private static final boolean DEBUG = false;
    private TextPaint mPaint;
    private CharSequence mText;
    private int mStart;
    private int mLen;
    private int mDir;
    private Layout.Directions mDirections;
    private boolean mHasTabs;
    private Layout.TabStops mTabs;
    private char[] mChars;
    private boolean mCharsValid;
    private Spanned mSpanned;
    private final TextPaint mWorkPaint = new TextPaint();
    private final SpanSet<MetricAffectingSpan> mMetricAffectingSpanSpanSet = new SpanSet<>(MetricAffectingSpan.class);
    private final SpanSet<CharacterStyle> mCharacterStyleSpanSet = new SpanSet<>(CharacterStyle.class);
    private final SpanSet<ReplacementSpan> mReplacementSpanSpanSet = new SpanSet<>(ReplacementSpan.class);
    private static final TextLine[] sCached = new TextLine[3];
    private static final int TAB_INCREMENT = 20;

    TextLine() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TextLine obtain() {
        synchronized (sCached) {
            int i = sCached.length;
            do {
                i--;
                if (i < 0) {
                    TextLine tl = new TextLine();
                    return tl;
                }
            } while (sCached[i] == null);
            TextLine tl2 = sCached[i];
            sCached[i] = null;
            return tl2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TextLine recycle(TextLine tl) {
        tl.mText = null;
        tl.mPaint = null;
        tl.mDirections = null;
        tl.mMetricAffectingSpanSpanSet.recycle();
        tl.mCharacterStyleSpanSet.recycle();
        tl.mReplacementSpanSpanSet.recycle();
        synchronized (sCached) {
            int i = 0;
            while (true) {
                if (i >= sCached.length) {
                    break;
                } else if (sCached[i] != null) {
                    i++;
                } else {
                    sCached[i] = tl;
                    break;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void set(TextPaint paint, CharSequence text, int start, int limit, int dir, Layout.Directions directions, boolean hasTabs, Layout.TabStops tabStops) {
        this.mPaint = paint;
        this.mText = text;
        this.mStart = start;
        this.mLen = limit - start;
        this.mDir = dir;
        this.mDirections = directions;
        if (this.mDirections == null) {
            throw new IllegalArgumentException("Directions cannot be null");
        }
        this.mHasTabs = hasTabs;
        this.mSpanned = null;
        boolean hasReplacement = false;
        if (text instanceof Spanned) {
            this.mSpanned = (Spanned) text;
            this.mReplacementSpanSpanSet.init(this.mSpanned, start, limit);
            hasReplacement = this.mReplacementSpanSpanSet.numberOfSpans > 0;
        }
        this.mCharsValid = hasReplacement || hasTabs || directions != Layout.DIRS_ALL_LEFT_TO_RIGHT;
        if (this.mCharsValid) {
            if (this.mChars == null || this.mChars.length < this.mLen) {
                this.mChars = new char[ArrayUtils.idealCharArraySize(this.mLen)];
            }
            TextUtils.getChars(text, start, limit, this.mChars, 0);
            if (hasReplacement) {
                char[] chars = this.mChars;
                int i = start;
                while (true) {
                    int i2 = i;
                    if (i2 >= limit) {
                        break;
                    }
                    int inext = this.mReplacementSpanSpanSet.getNextTransition(i2, limit);
                    if (this.mReplacementSpanSpanSet.hasSpansIntersecting(i2, inext)) {
                        chars[i2 - start] = 65532;
                        int e = inext - start;
                        for (int j = (i2 - start) + 1; j < e; j++) {
                            chars[j] = 65279;
                        }
                    }
                    i = inext;
                }
            }
        }
        this.mTabs = tabStops;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public void draw(Canvas c, float x, int top, int y, int bottom) {
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                drawRun(c, 0, this.mLen, false, x, top, y, bottom, false);
                return;
            } else if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                drawRun(c, 0, this.mLen, true, x, top, y, bottom, false);
                return;
            }
        }
        float h = 0.0f;
        int[] runs = this.mDirections.mDirections;
        RectF emojiRect = null;
        int lastRunIndex = runs.length - 2;
        int i = 0;
        while (i < runs.length) {
            int runStart = runs[i];
            int runLimit = runStart + (runs[i + 1] & 67108863);
            if (runLimit > this.mLen) {
                runLimit = this.mLen;
            }
            boolean runIsRtl = (runs[i + 1] & 67108864) != 0;
            int segstart = runStart;
            int j = this.mHasTabs ? runStart : runLimit;
            while (j <= runLimit) {
                int codept = 0;
                Bitmap bm = null;
                if (this.mHasTabs && j < runLimit) {
                    codept = this.mChars[j];
                    if (codept >= 55296 && codept < 56320 && j + 1 < runLimit) {
                        codept = Character.codePointAt(this.mChars, j);
                        if (codept >= Layout.MIN_EMOJI && codept <= Layout.MAX_EMOJI) {
                            bm = Layout.EMOJI_FACTORY.getBitmapFromAndroidPua(codept);
                        } else if (codept > 65535) {
                            j++;
                            j++;
                        }
                    }
                }
                if (j == runLimit || codept == 9 || bm != null) {
                    h += drawRun(c, segstart, j, runIsRtl, x + h, top, y, bottom, (i == lastRunIndex && j == this.mLen) ? false : true);
                    if (codept == 9) {
                        h = this.mDir * nextTab(h * this.mDir);
                    } else if (bm != null) {
                        float bmAscent = ascent(j);
                        float bitmapHeight = bm.getHeight();
                        float scale = (-bmAscent) / bitmapHeight;
                        float width = bm.getWidth() * scale;
                        if (emojiRect == null) {
                            emojiRect = new RectF();
                        }
                        emojiRect.set(x + h, y + bmAscent, x + h + width, y);
                        c.drawBitmap(bm, (Rect) null, emojiRect, this.mPaint);
                        h += width;
                        j++;
                    }
                    segstart = j + 1;
                }
                j++;
            }
            i += 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float metrics(Paint.FontMetricsInt fmi) {
        return measure(this.mLen, false, fmi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public float measure(int offset, boolean trailing, Paint.FontMetricsInt fmi) {
        int target = trailing ? offset - 1 : offset;
        if (target < 0) {
            return 0.0f;
        }
        float h = 0.0f;
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                return measureRun(0, offset, this.mLen, false, fmi);
            }
            if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                return measureRun(0, offset, this.mLen, true, fmi);
            }
        }
        char[] chars = this.mChars;
        int[] runs = this.mDirections.mDirections;
        for (int i = 0; i < runs.length; i += 2) {
            int runStart = runs[i];
            int runLimit = runStart + (runs[i + 1] & 67108863);
            if (runLimit > this.mLen) {
                runLimit = this.mLen;
            }
            boolean runIsRtl = (runs[i + 1] & 67108864) != 0;
            int segstart = runStart;
            int j = this.mHasTabs ? runStart : runLimit;
            while (j <= runLimit) {
                int codept = 0;
                Bitmap bm = null;
                if (this.mHasTabs && j < runLimit) {
                    codept = chars[j];
                    if (codept >= 55296 && codept < 56320 && j + 1 < runLimit) {
                        codept = Character.codePointAt(chars, j);
                        if (codept >= Layout.MIN_EMOJI && codept <= Layout.MAX_EMOJI) {
                            bm = Layout.EMOJI_FACTORY.getBitmapFromAndroidPua(codept);
                        } else if (codept > 65535) {
                            j++;
                            j++;
                        }
                    }
                }
                if (j == runLimit || codept == 9 || bm != null) {
                    boolean inSegment = target >= segstart && target < j;
                    boolean advance = (this.mDir == -1) == runIsRtl;
                    if (inSegment && advance) {
                        return h + measureRun(segstart, offset, j, runIsRtl, fmi);
                    }
                    float w = measureRun(segstart, j, j, runIsRtl, fmi);
                    h += advance ? w : -w;
                    if (inSegment) {
                        return h + measureRun(segstart, offset, j, runIsRtl, null);
                    }
                    if (codept == 9) {
                        if (offset == j) {
                            return h;
                        }
                        h = this.mDir * nextTab(h * this.mDir);
                        if (target == j) {
                            return h;
                        }
                    }
                    if (bm != null) {
                        float bmAscent = ascent(j);
                        float wid = (bm.getWidth() * (-bmAscent)) / bm.getHeight();
                        h += this.mDir * wid;
                        j++;
                    }
                    segstart = j + 1;
                }
                j++;
            }
        }
        return h;
    }

    private float drawRun(Canvas c, int start, int limit, boolean runIsRtl, float x, int top, int y, int bottom, boolean needWidth) {
        if ((this.mDir == 1) == runIsRtl) {
            float w = -measureRun(start, limit, limit, runIsRtl, null);
            handleRun(start, limit, limit, runIsRtl, c, x + w, top, y, bottom, null, false);
            return w;
        }
        return handleRun(start, limit, limit, runIsRtl, c, x, top, y, bottom, null, needWidth);
    }

    private float measureRun(int start, int offset, int limit, boolean runIsRtl, Paint.FontMetricsInt fmi) {
        return handleRun(start, offset, limit, runIsRtl, null, 0.0f, 0, 0, 0, fmi, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0244, code lost:
        if (r19 != (-1)) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x0249, code lost:
        if (r21 == false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x024c, code lost:
        r0 = r8.mLen + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x0255, code lost:
        r0 = -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x0256, code lost:
        r19 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x025f, code lost:
        if (r19 > r0) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:116:0x0264, code lost:
        if (r21 == false) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x0267, code lost:
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x026c, code lost:
        r0 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x026d, code lost:
        r19 = r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getOffsetToLeftRightOf(int r9, boolean r10) {
        /*
            Method dump skipped, instructions count: 629
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextLine.getOffsetToLeftRightOf(int, boolean):int");
    }

    private int getOffsetBeforeAfter(int runIndex, int runStart, int runLimit, boolean runIsRtl, int offset, boolean after) {
        int spanLimit;
        if (runIndex >= 0) {
            if (offset != (after ? this.mLen : 0)) {
                TextPaint wp = this.mWorkPaint;
                wp.set(this.mPaint);
                int spanStart = runStart;
                if (this.mSpanned == null) {
                    spanLimit = runLimit;
                } else {
                    int target = after ? offset + 1 : offset;
                    int limit = this.mStart + runLimit;
                    while (true) {
                        spanLimit = this.mSpanned.nextSpanTransition(this.mStart + spanStart, limit, MetricAffectingSpan.class) - this.mStart;
                        if (spanLimit >= target) {
                            break;
                        }
                        spanStart = spanLimit;
                    }
                    MetricAffectingSpan[] spans = (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) this.mSpanned.getSpans(this.mStart + spanStart, this.mStart + spanLimit, MetricAffectingSpan.class), this.mSpanned, MetricAffectingSpan.class);
                    if (spans.length > 0) {
                        ReplacementSpan replacement = null;
                        for (MetricAffectingSpan span : spans) {
                            if (span instanceof ReplacementSpan) {
                                replacement = (ReplacementSpan) span;
                            } else {
                                span.updateMeasureState(wp);
                            }
                        }
                        if (replacement != null) {
                            return after ? spanLimit : spanStart;
                        }
                    }
                }
                int flags = runIsRtl ? 1 : 0;
                int cursorOpt = after ? 0 : 2;
                if (this.mCharsValid) {
                    return wp.getTextRunCursor(this.mChars, spanStart, spanLimit - spanStart, flags, offset, cursorOpt);
                }
                return wp.getTextRunCursor(this.mText, this.mStart + spanStart, this.mStart + spanLimit, flags, this.mStart + offset, cursorOpt) - this.mStart;
            }
        }
        if (after) {
            return TextUtils.getOffsetAfter(this.mText, offset + this.mStart) - this.mStart;
        }
        return TextUtils.getOffsetBefore(this.mText, offset + this.mStart) - this.mStart;
    }

    private static void expandMetricsFromPaint(Paint.FontMetricsInt fmi, TextPaint wp) {
        int previousTop = fmi.top;
        int previousAscent = fmi.ascent;
        int previousDescent = fmi.descent;
        int previousBottom = fmi.bottom;
        int previousLeading = fmi.leading;
        wp.getFontMetricsInt(fmi);
        updateMetrics(fmi, previousTop, previousAscent, previousDescent, previousBottom, previousLeading);
    }

    static void updateMetrics(Paint.FontMetricsInt fmi, int previousTop, int previousAscent, int previousDescent, int previousBottom, int previousLeading) {
        fmi.top = Math.min(fmi.top, previousTop);
        fmi.ascent = Math.min(fmi.ascent, previousAscent);
        fmi.descent = Math.max(fmi.descent, previousDescent);
        fmi.bottom = Math.max(fmi.bottom, previousBottom);
        fmi.leading = Math.max(fmi.leading, previousLeading);
    }

    private float handleText(TextPaint wp, int start, int end, int contextStart, int contextEnd, boolean runIsRtl, Canvas c, float x, int top, int y, int bottom, Paint.FontMetricsInt fmi, boolean needWidth) {
        if (fmi != null) {
            expandMetricsFromPaint(fmi, wp);
        }
        int runLen = end - start;
        if (runLen == 0) {
            return 0.0f;
        }
        float ret = 0.0f;
        int contextLen = contextEnd - contextStart;
        if (needWidth || (c != null && (wp.bgColor != 0 || wp.underlineColor != 0 || runIsRtl))) {
            int flags = runIsRtl ? 1 : 0;
            if (this.mCharsValid) {
                ret = wp.getTextRunAdvances(this.mChars, start, runLen, contextStart, contextLen, flags, (float[]) null, 0);
            } else {
                int delta = this.mStart;
                ret = wp.getTextRunAdvances(this.mText, delta + start, delta + end, delta + contextStart, delta + contextEnd, flags, (float[]) null, 0);
            }
        }
        if (c != null) {
            if (runIsRtl) {
                x -= ret;
            }
            if (wp.bgColor != 0) {
                int previousColor = wp.getColor();
                Paint.Style previousStyle = wp.getStyle();
                wp.setColor(wp.bgColor);
                wp.setStyle(Paint.Style.FILL);
                c.drawRect(x, top, x + ret, bottom, wp);
                wp.setStyle(previousStyle);
                wp.setColor(previousColor);
            }
            if (wp.underlineColor != 0) {
                float underlineTop = y + wp.baselineShift + (0.11111111f * wp.getTextSize());
                int previousColor2 = wp.getColor();
                Paint.Style previousStyle2 = wp.getStyle();
                boolean previousAntiAlias = wp.isAntiAlias();
                wp.setStyle(Paint.Style.FILL);
                wp.setAntiAlias(true);
                wp.setColor(wp.underlineColor);
                c.drawRect(x, underlineTop, x + ret, underlineTop + wp.underlineThickness, wp);
                wp.setStyle(previousStyle2);
                wp.setColor(previousColor2);
                wp.setAntiAlias(previousAntiAlias);
            }
            drawTextRun(c, wp, start, end, contextStart, contextEnd, runIsRtl, x, y + wp.baselineShift);
        }
        return runIsRtl ? -ret : ret;
    }

    private float handleReplacement(ReplacementSpan replacement, TextPaint wp, int start, int limit, boolean runIsRtl, Canvas c, float x, int top, int y, int bottom, Paint.FontMetricsInt fmi, boolean needWidth) {
        float ret = 0.0f;
        int textStart = this.mStart + start;
        int textLimit = this.mStart + limit;
        if (needWidth || (c != null && runIsRtl)) {
            int previousTop = 0;
            int previousAscent = 0;
            int previousDescent = 0;
            int previousBottom = 0;
            int previousLeading = 0;
            boolean needUpdateMetrics = fmi != null;
            if (needUpdateMetrics) {
                previousTop = fmi.top;
                previousAscent = fmi.ascent;
                previousDescent = fmi.descent;
                previousBottom = fmi.bottom;
                previousLeading = fmi.leading;
            }
            ret = replacement.getSize(wp, this.mText, textStart, textLimit, fmi);
            if (needUpdateMetrics) {
                updateMetrics(fmi, previousTop, previousAscent, previousDescent, previousBottom, previousLeading);
            }
        }
        if (c != null) {
            if (runIsRtl) {
                x -= ret;
            }
            replacement.draw(c, this.mText, textStart, textLimit, x, top, y, bottom, wp);
        }
        return runIsRtl ? -ret : ret;
    }

    private float handleRun(int start, int measureLimit, int limit, boolean runIsRtl, Canvas c, float x, int top, int y, int bottom, Paint.FontMetricsInt fmi, boolean needWidth) {
        if (start == measureLimit) {
            TextPaint wp = this.mWorkPaint;
            wp.set(this.mPaint);
            if (fmi != null) {
                expandMetricsFromPaint(fmi, wp);
                return 0.0f;
            }
            return 0.0f;
        } else if (this.mSpanned == null) {
            TextPaint wp2 = this.mWorkPaint;
            wp2.set(this.mPaint);
            return handleText(wp2, start, measureLimit, start, limit, runIsRtl, c, x, top, y, bottom, fmi, needWidth || measureLimit < measureLimit);
        } else {
            this.mMetricAffectingSpanSpanSet.init(this.mSpanned, this.mStart + start, this.mStart + limit);
            this.mCharacterStyleSpanSet.init(this.mSpanned, this.mStart + start, this.mStart + limit);
            int i = start;
            while (true) {
                int i2 = i;
                if (i2 >= measureLimit) {
                    return x - x;
                }
                TextPaint wp3 = this.mWorkPaint;
                wp3.set(this.mPaint);
                int inext = this.mMetricAffectingSpanSpanSet.getNextTransition(this.mStart + i2, this.mStart + limit) - this.mStart;
                int mlimit = Math.min(inext, measureLimit);
                ReplacementSpan replacement = null;
                for (int j = 0; j < this.mMetricAffectingSpanSpanSet.numberOfSpans; j++) {
                    if (this.mMetricAffectingSpanSpanSet.spanStarts[j] < this.mStart + mlimit && this.mMetricAffectingSpanSpanSet.spanEnds[j] > this.mStart + i2) {
                        MetricAffectingSpan span = this.mMetricAffectingSpanSpanSet.spans[j];
                        if (span instanceof ReplacementSpan) {
                            replacement = (ReplacementSpan) span;
                        } else {
                            span.updateDrawState(wp3);
                        }
                    }
                }
                if (replacement != null) {
                    x += handleReplacement(replacement, wp3, i2, mlimit, runIsRtl, c, x, top, y, bottom, fmi, needWidth || mlimit < measureLimit);
                } else {
                    int i3 = i2;
                    while (true) {
                        int j2 = i3;
                        if (j2 < mlimit) {
                            int jnext = this.mCharacterStyleSpanSet.getNextTransition(this.mStart + j2, this.mStart + mlimit) - this.mStart;
                            wp3.set(this.mPaint);
                            for (int k = 0; k < this.mCharacterStyleSpanSet.numberOfSpans; k++) {
                                if (this.mCharacterStyleSpanSet.spanStarts[k] < this.mStart + jnext && this.mCharacterStyleSpanSet.spanEnds[k] > this.mStart + j2) {
                                    this.mCharacterStyleSpanSet.spans[k].updateDrawState(wp3);
                                }
                            }
                            x += handleText(wp3, j2, jnext, i2, inext, runIsRtl, c, x, top, y, bottom, fmi, needWidth || jnext < measureLimit);
                            i3 = jnext;
                        }
                    }
                }
                i = inext;
            }
        }
    }

    private void drawTextRun(Canvas c, TextPaint wp, int start, int end, int contextStart, int contextEnd, boolean runIsRtl, float x, int y) {
        int flags = runIsRtl ? 1 : 0;
        if (this.mCharsValid) {
            int count = end - start;
            int contextCount = contextEnd - contextStart;
            c.drawTextRun(this.mChars, start, count, contextStart, contextCount, x, y, flags, wp);
            return;
        }
        int delta = this.mStart;
        c.drawTextRun(this.mText, delta + start, delta + end, delta + contextStart, delta + contextEnd, x, y, flags, wp);
    }

    float ascent(int pos) {
        if (this.mSpanned == null) {
            return this.mPaint.ascent();
        }
        int pos2 = pos + this.mStart;
        MetricAffectingSpan[] spans = (MetricAffectingSpan[]) this.mSpanned.getSpans(pos2, pos2 + 1, MetricAffectingSpan.class);
        if (spans.length == 0) {
            return this.mPaint.ascent();
        }
        TextPaint wp = this.mWorkPaint;
        wp.set(this.mPaint);
        for (MetricAffectingSpan span : spans) {
            span.updateMeasureState(wp);
        }
        return wp.ascent();
    }

    float nextTab(float h) {
        if (this.mTabs != null) {
            return this.mTabs.nextTab(h);
        }
        return Layout.TabStops.nextDefaultStop(h, 20);
    }
}