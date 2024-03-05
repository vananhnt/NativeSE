package android.text;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.TabStopSpan;
import android.util.Log;
import com.android.internal.util.ArrayUtils;

/* loaded from: StaticLayout.class */
public class StaticLayout extends Layout {
    static final String TAG = "StaticLayout";
    private int mLineCount;
    private int mTopPadding;
    private int mBottomPadding;
    private int mColumns;
    private int mEllipsizedWidth;
    private static final int COLUMNS_NORMAL = 3;
    private static final int COLUMNS_ELLIPSIZE = 5;
    private static final int START = 0;
    private static final int DIR = 0;
    private static final int TAB = 0;
    private static final int TOP = 1;
    private static final int DESCENT = 2;
    private static final int ELLIPSIS_START = 3;
    private static final int ELLIPSIS_COUNT = 4;
    private int[] mLines;
    private Layout.Directions[] mLineDirections;
    private int mMaximumVisibleLineCount;
    private static final int START_MASK = 536870911;
    private static final int DIR_SHIFT = 30;
    private static final int TAB_MASK = 536870912;
    private static final int TAB_INCREMENT = 20;
    private static final char CHAR_FIRST_CJK = 11904;
    private static final char CHAR_NEW_LINE = '\n';
    private static final char CHAR_TAB = '\t';
    private static final char CHAR_SPACE = ' ';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_HYPHEN = '-';
    private static final char CHAR_ZWSP = 8203;
    private static final double EXTRA_ROUNDING = 0.5d;
    private static final int CHAR_FIRST_HIGH_SURROGATE = 55296;
    private static final int CHAR_LAST_LOW_SURROGATE = 57343;
    private MeasuredText mMeasured;
    private Paint.FontMetricsInt mFontMetricsInt;

    public StaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad);
    }

    public StaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad) {
        this(source, 0, source.length(), paint, width, align, textDir, spacingmult, spacingadd, includepad);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad, null, 0);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad) {
        this(source, bufstart, bufend, paint, outerwidth, align, textDir, spacingmult, spacingadd, includepad, null, 0, Integer.MAX_VALUE);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        this(source, bufstart, bufend, paint, outerwidth, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth, Integer.MAX_VALUE);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth, int maxLines) {
        super(ellipsize == null ? source : source instanceof Spanned ? new Layout.SpannedEllipsizer(source) : new Layout.Ellipsizer(source), paint, outerwidth, align, textDir, spacingmult, spacingadd);
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mFontMetricsInt = new Paint.FontMetricsInt();
        if (ellipsize != null) {
            Layout.Ellipsizer e = (Layout.Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = ellipsizedWidth;
            e.mMethod = ellipsize;
            this.mEllipsizedWidth = ellipsizedWidth;
            this.mColumns = 5;
        } else {
            this.mColumns = 3;
            this.mEllipsizedWidth = outerwidth;
        }
        this.mLines = new int[ArrayUtils.idealIntArraySize(2 * this.mColumns)];
        this.mLineDirections = new Layout.Directions[ArrayUtils.idealIntArraySize(2 * this.mColumns)];
        this.mMaximumVisibleLineCount = maxLines;
        this.mMeasured = MeasuredText.obtain();
        generate(source, bufstart, bufend, paint, outerwidth, textDir, spacingmult, spacingadd, includepad, includepad, ellipsizedWidth, ellipsize);
        this.mMeasured = MeasuredText.recycle(this.mMeasured);
        this.mFontMetricsInt = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaticLayout(CharSequence text) {
        super(text, null, 0, null, 0.0f, 0.0f);
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mFontMetricsInt = new Paint.FontMetricsInt();
        this.mColumns = 5;
        this.mLines = new int[ArrayUtils.idealIntArraySize(2 * this.mColumns)];
        this.mLineDirections = new Layout.Directions[ArrayUtils.idealIntArraySize(2 * this.mColumns)];
        this.mMeasured = MeasuredText.obtain();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void generate(CharSequence source, int bufStart, int bufEnd, TextPaint paint, int outerWidth, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, boolean trackpad, float ellipsizedWidth, TextUtils.TruncateAt ellipsize) {
        int paraEnd;
        int spanEnd;
        int endPos;
        int above;
        int below;
        int top;
        int bottom;
        float currentTextWidth;
        Paint whichPaint;
        this.mLineCount = 0;
        int v = 0;
        boolean needMultiply = (spacingmult == 1.0f && spacingadd == 0.0f) ? false : true;
        Paint.FontMetricsInt fm = this.mFontMetricsInt;
        int[] chooseHtv = null;
        MeasuredText measured = this.mMeasured;
        Spanned spanned = null;
        if (source instanceof Spanned) {
            spanned = (Spanned) source;
        }
        int i = bufStart;
        while (true) {
            int paraStart = i;
            if (paraStart > bufEnd) {
                break;
            }
            int paraEnd2 = TextUtils.indexOf(source, '\n', paraStart, bufEnd);
            if (paraEnd2 < 0) {
                paraEnd = bufEnd;
            } else {
                paraEnd = paraEnd2 + 1;
            }
            int firstWidthLineLimit = this.mLineCount + 1;
            int firstWidth = outerWidth;
            int restWidth = outerWidth;
            LineHeightSpan[] chooseHt = null;
            if (spanned != null) {
                LeadingMarginSpan[] sp = (LeadingMarginSpan[]) getParagraphSpans(spanned, paraStart, paraEnd, LeadingMarginSpan.class);
                for (int i2 = 0; i2 < sp.length; i2++) {
                    LeadingMarginSpan lms = sp[i2];
                    firstWidth -= sp[i2].getLeadingMargin(true);
                    restWidth -= sp[i2].getLeadingMargin(false);
                    if (lms instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                        LeadingMarginSpan.LeadingMarginSpan2 lms2 = (LeadingMarginSpan.LeadingMarginSpan2) lms;
                        int lmsFirstLine = getLineForOffset(spanned.getSpanStart(lms2));
                        firstWidthLineLimit = lmsFirstLine + lms2.getLeadingMarginLineCount();
                    }
                }
                chooseHt = (LineHeightSpan[]) getParagraphSpans(spanned, paraStart, paraEnd, LineHeightSpan.class);
                if (chooseHt.length != 0) {
                    if (chooseHtv == null || chooseHtv.length < chooseHt.length) {
                        chooseHtv = new int[ArrayUtils.idealIntArraySize(chooseHt.length)];
                    }
                    for (int i3 = 0; i3 < chooseHt.length; i3++) {
                        int o = spanned.getSpanStart(chooseHt[i3]);
                        if (o < paraStart) {
                            chooseHtv[i3] = getLineTop(getLineForOffset(o));
                        } else {
                            chooseHtv[i3] = v;
                        }
                    }
                }
            }
            measured.setPara(source, paraStart, paraEnd, textDir);
            char[] chs = measured.mChars;
            float[] widths = measured.mWidths;
            byte[] chdirs = measured.mLevels;
            int dir = measured.mDir;
            boolean easy = measured.mEasy;
            int width = firstWidth;
            float w = 0.0f;
            int here = paraStart;
            int ok = paraStart;
            float okWidth = 0.0f;
            int okAscent = 0;
            int okDescent = 0;
            int okTop = 0;
            int okBottom = 0;
            int fit = paraStart;
            float fitWidth = 0.0f;
            int fitAscent = 0;
            int fitDescent = 0;
            int fitTop = 0;
            int fitBottom = 0;
            boolean hasTabOrEmoji = false;
            boolean hasTab = false;
            Layout.TabStops tabStops = null;
            int i4 = paraStart;
            while (true) {
                int spanStart = i4;
                if (spanStart >= paraEnd) {
                    break;
                }
                if (spanned == null) {
                    spanEnd = paraEnd;
                    int spanLen = spanEnd - spanStart;
                    measured.addStyleRun(paint, spanLen, fm);
                } else {
                    spanEnd = spanned.nextSpanTransition(spanStart, paraEnd, MetricAffectingSpan.class);
                    int spanLen2 = spanEnd - spanStart;
                    measured.addStyleRun(paint, (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) spanned.getSpans(spanStart, spanEnd, MetricAffectingSpan.class), spanned, MetricAffectingSpan.class), spanLen2, fm);
                }
                int fmTop = fm.top;
                int fmBottom = fm.bottom;
                int fmAscent = fm.ascent;
                int fmDescent = fm.descent;
                int j = spanStart;
                while (true) {
                    if (j >= spanEnd) {
                        break;
                    }
                    char c = chs[j - paraStart];
                    if (c != '\n') {
                        if (c == '\t') {
                            if (!hasTab) {
                                hasTab = true;
                                hasTabOrEmoji = true;
                                if (spanned != null) {
                                    TabStopSpan[] spans = (TabStopSpan[]) getParagraphSpans(spanned, paraStart, paraEnd, TabStopSpan.class);
                                    if (spans.length > 0) {
                                        tabStops = new Layout.TabStops(20, spans);
                                    }
                                }
                            }
                            if (tabStops != null) {
                                w = tabStops.nextTab(w);
                            } else {
                                w = Layout.TabStops.nextDefaultStop(w, 20);
                            }
                        } else if (c >= 55296 && c <= 57343 && j + 1 < spanEnd) {
                            int emoji = Character.codePointAt(chs, j - paraStart);
                            if (emoji >= MIN_EMOJI && emoji <= MAX_EMOJI) {
                                Bitmap bm = EMOJI_FACTORY.getBitmapFromAndroidPua(emoji);
                                if (bm != null) {
                                    if (spanned == null) {
                                        whichPaint = paint;
                                    } else {
                                        whichPaint = this.mWorkPaint;
                                    }
                                    float wid = (bm.getWidth() * (-whichPaint.ascent())) / bm.getHeight();
                                    w += wid;
                                    hasTabOrEmoji = true;
                                    j++;
                                } else {
                                    w += widths[j - paraStart];
                                }
                            } else {
                                w += widths[j - paraStart];
                            }
                        } else {
                            w += widths[j - paraStart];
                        }
                    }
                    boolean isSpaceOrTab = c == ' ' || c == '\t' || c == 8203;
                    if (w <= width || isSpaceOrTab) {
                        fitWidth = w;
                        fit = j + 1;
                        if (fmTop < fitTop) {
                            fitTop = fmTop;
                        }
                        if (fmAscent < fitAscent) {
                            fitAscent = fmAscent;
                        }
                        if (fmDescent > fitDescent) {
                            fitDescent = fmDescent;
                        }
                        if (fmBottom > fitBottom) {
                            fitBottom = fmBottom;
                        }
                        boolean isLineBreak = isSpaceOrTab || ((c == '/' || c == '-') && (j + 1 >= spanEnd || !Character.isDigit(chs[(j + 1) - paraStart]))) || (c >= CHAR_FIRST_CJK && isIdeographic(c, true) && j + 1 < spanEnd && isIdeographic(chs[(j + 1) - paraStart], false));
                        if (isLineBreak) {
                            okWidth = w;
                            ok = j + 1;
                            if (fitTop < okTop) {
                                okTop = fitTop;
                            }
                            if (fitAscent < okAscent) {
                                okAscent = fitAscent;
                            }
                            if (fitDescent > okDescent) {
                                okDescent = fitDescent;
                            }
                            if (fitBottom > okBottom) {
                                okBottom = fitBottom;
                            }
                        }
                    } else {
                        boolean moreChars = j + 1 < spanEnd;
                        if (ok != here) {
                            endPos = ok;
                            above = okAscent;
                            below = okDescent;
                            top = okTop;
                            bottom = okBottom;
                            currentTextWidth = okWidth;
                        } else if (fit != here) {
                            endPos = fit;
                            above = fitAscent;
                            below = fitDescent;
                            top = fitTop;
                            bottom = fitBottom;
                            currentTextWidth = fitWidth;
                        } else {
                            endPos = here + 1;
                            above = fm.ascent;
                            below = fm.descent;
                            top = fm.top;
                            bottom = fm.bottom;
                            currentTextWidth = widths[here - paraStart];
                        }
                        v = out(source, here, endPos, above, below, top, bottom, v, spacingmult, spacingadd, chooseHt, chooseHtv, fm, hasTabOrEmoji, needMultiply, chdirs, dir, easy, bufEnd, includepad, trackpad, chs, widths, paraStart, ellipsize, ellipsizedWidth, currentTextWidth, paint, moreChars);
                        here = endPos;
                        j = here - 1;
                        fit = here;
                        ok = here;
                        w = 0.0f;
                        fitBottom = 0;
                        fitTop = 0;
                        fitDescent = 0;
                        fitAscent = 0;
                        okBottom = 0;
                        okTop = 0;
                        okDescent = 0;
                        okAscent = 0;
                        firstWidthLineLimit--;
                        if (firstWidthLineLimit <= 0) {
                            width = restWidth;
                        }
                        if (here < spanStart) {
                            measured.setPos(here);
                            spanEnd = here;
                            break;
                        } else if (this.mLineCount >= this.mMaximumVisibleLineCount) {
                            break;
                        }
                    }
                    j++;
                }
                i4 = spanEnd;
            }
            if (paraEnd != here && this.mLineCount < this.mMaximumVisibleLineCount) {
                if ((fitTop | fitBottom | fitDescent | fitAscent) == 0) {
                    paint.getFontMetricsInt(fm);
                    fitTop = fm.top;
                    fitBottom = fm.bottom;
                    fitAscent = fm.ascent;
                    fitDescent = fm.descent;
                }
                v = out(source, here, paraEnd, fitAscent, fitDescent, fitTop, fitBottom, v, spacingmult, spacingadd, chooseHt, chooseHtv, fm, hasTabOrEmoji, needMultiply, chdirs, dir, easy, bufEnd, includepad, trackpad, chs, widths, paraStart, ellipsize, ellipsizedWidth, w, paint, paraEnd != bufEnd);
            }
            if (paraEnd == bufEnd) {
                break;
            }
            i = paraEnd;
        }
        if ((bufEnd == bufStart || source.charAt(bufEnd - 1) == '\n') && this.mLineCount < this.mMaximumVisibleLineCount) {
            measured.setPara(source, bufStart, bufEnd, textDir);
            paint.getFontMetricsInt(fm);
            out(source, bufEnd, bufEnd, fm.ascent, fm.descent, fm.top, fm.bottom, v, spacingmult, spacingadd, null, null, fm, false, needMultiply, measured.mLevels, measured.mDir, measured.mEasy, bufEnd, includepad, trackpad, null, null, bufStart, ellipsize, ellipsizedWidth, 0.0f, paint, false);
        }
    }

    private static final boolean isIdeographic(char c, boolean includeNonStarters) {
        if ((c >= CHAR_FIRST_CJK && c <= 12287) || c == 12288) {
            return true;
        }
        if (c >= 12352 && c <= 12447) {
            if (!includeNonStarters) {
                switch (c) {
                    case EGL14.EGL_MATCH_NATIVE_PIXMAP /* 12353 */:
                    case 12355:
                    case 12357:
                    case 12359:
                    case 12361:
                    case 12387:
                    case EGL14.EGL_MIPMAP_LEVEL /* 12419 */:
                    case 12421:
                    case 12423:
                    case 12430:
                    case EGL14.EGL_BUFFER_DESTROYED /* 12437 */:
                    case EGL14.EGL_OPENVG_IMAGE /* 12438 */:
                    case EGL14.EGL_MULTISAMPLE_RESOLVE_BOX /* 12443 */:
                    case 12444:
                    case 12445:
                    case 12446:
                        return false;
                    default:
                        return true;
                }
            }
            return true;
        } else if (c >= 12448 && c <= 12543) {
            if (!includeNonStarters) {
                switch (c) {
                    case EGL14.EGL_OPENGL_ES_API /* 12448 */:
                    case EGL14.EGL_OPENVG_API /* 12449 */:
                    case 12451:
                    case 12453:
                    case 12455:
                    case 12457:
                    case 12483:
                    case 12515:
                    case 12517:
                    case 12519:
                    case 12526:
                    case 12533:
                    case 12534:
                    case EGLExt.EGL_CONTEXT_MINOR_VERSION_KHR /* 12539 */:
                    case EGLExt.EGL_CONTEXT_FLAGS_KHR /* 12540 */:
                    case 12541:
                    case 12542:
                        return false;
                    default:
                        return true;
                }
            }
            return true;
        } else if (c >= 13312 && c <= 19893) {
            return true;
        } else {
            if (c >= 19968 && c <= 40891) {
                return true;
            }
            if (c >= 63744 && c <= 64217) {
                return true;
            }
            if (c >= 40960 && c <= 42127) {
                return true;
            }
            if (c >= 42128 && c <= 42191) {
                return true;
            }
            if (c >= 65122 && c <= 65126) {
                return true;
            }
            if (c >= 65296 && c <= 65305) {
                return true;
            }
            return false;
        }
    }

    private int out(CharSequence text, int start, int end, int above, int below, int top, int bottom, int v, float spacingmult, float spacingadd, LineHeightSpan[] chooseHt, int[] chooseHtv, Paint.FontMetricsInt fm, boolean hasTabOrEmoji, boolean needMultiply, byte[] chdirs, int dir, boolean easy, int bufEnd, boolean includePad, boolean trackPad, char[] chs, float[] widths, int widthStart, TextUtils.TruncateAt ellipsize, float ellipsisWidth, float textWidth, TextPaint paint, boolean moreChars) {
        int extra;
        int j = this.mLineCount;
        int off = j * this.mColumns;
        int want = off + this.mColumns + 1;
        int[] lines = this.mLines;
        if (want >= lines.length) {
            int nlen = ArrayUtils.idealIntArraySize(want + 1);
            int[] grow = new int[nlen];
            System.arraycopy(lines, 0, grow, 0, lines.length);
            this.mLines = grow;
            lines = grow;
            Layout.Directions[] grow2 = new Layout.Directions[nlen];
            System.arraycopy(this.mLineDirections, 0, grow2, 0, this.mLineDirections.length);
            this.mLineDirections = grow2;
        }
        if (chooseHt != null) {
            fm.ascent = above;
            fm.descent = below;
            fm.top = top;
            fm.bottom = bottom;
            for (int i = 0; i < chooseHt.length; i++) {
                if (chooseHt[i] instanceof LineHeightSpan.WithDensity) {
                    ((LineHeightSpan.WithDensity) chooseHt[i]).chooseHeight(text, start, end, chooseHtv[i], v, fm, paint);
                } else {
                    chooseHt[i].chooseHeight(text, start, end, chooseHtv[i], v, fm);
                }
            }
            above = fm.ascent;
            below = fm.descent;
            top = fm.top;
            bottom = fm.bottom;
        }
        if (j == 0) {
            if (trackPad) {
                this.mTopPadding = top - above;
            }
            if (includePad) {
                above = top;
            }
        }
        if (end == bufEnd) {
            if (trackPad) {
                this.mBottomPadding = bottom - below;
            }
            if (includePad) {
                below = bottom;
            }
        }
        if (needMultiply) {
            double ex = ((below - above) * (spacingmult - 1.0f)) + spacingadd;
            if (ex >= 0.0d) {
                extra = (int) (ex + EXTRA_ROUNDING);
            } else {
                extra = -((int) ((-ex) + EXTRA_ROUNDING));
            }
        } else {
            extra = 0;
        }
        lines[off + 0] = start;
        lines[off + 1] = v;
        lines[off + 2] = below + extra;
        int v2 = v + (below - above) + extra;
        lines[off + this.mColumns + 0] = end;
        lines[off + this.mColumns + 1] = v2;
        if (hasTabOrEmoji) {
            int[] iArr = lines;
            int i2 = off + 0;
            iArr[i2] = iArr[i2] | 536870912;
        }
        int[] iArr2 = lines;
        int i3 = off + 0;
        iArr2[i3] = iArr2[i3] | (dir << 30);
        Layout.Directions linedirs = DIRS_ALL_LEFT_TO_RIGHT;
        if (easy) {
            this.mLineDirections[j] = linedirs;
        } else {
            this.mLineDirections[j] = AndroidBidi.directions(dir, chdirs, start - widthStart, chs, start - widthStart, end - start);
        }
        if (ellipsize != null) {
            boolean firstLine = j == 0;
            boolean currentLineIsTheLastVisibleOne = j + 1 == this.mMaximumVisibleLineCount;
            boolean forceEllipsis = moreChars && this.mLineCount + 1 == this.mMaximumVisibleLineCount;
            boolean doEllipsis = (((this.mMaximumVisibleLineCount == 1 && moreChars) || (firstLine && !moreChars)) && ellipsize != TextUtils.TruncateAt.MARQUEE) || (!firstLine && ((currentLineIsTheLastVisibleOne || !moreChars) && ellipsize == TextUtils.TruncateAt.END));
            if (doEllipsis) {
                calculateEllipsis(start, end, widths, widthStart, ellipsisWidth, ellipsize, j, textWidth, paint, forceEllipsis);
            }
        }
        this.mLineCount++;
        return v2;
    }

    private void calculateEllipsis(int lineStart, int lineEnd, float[] widths, int widthStart, float avail, TextUtils.TruncateAt where, int line, float textWidth, TextPaint paint, boolean forceEllipsis) {
        if (textWidth <= avail && !forceEllipsis) {
            this.mLines[(this.mColumns * line) + 3] = 0;
            this.mLines[(this.mColumns * line) + 4] = 0;
            return;
        }
        float ellipsisWidth = paint.measureText(where == TextUtils.TruncateAt.END_SMALL ? ELLIPSIS_TWO_DOTS : ELLIPSIS_NORMAL, 0, 1);
        int ellipsisStart = 0;
        int ellipsisCount = 0;
        int len = lineEnd - lineStart;
        if (where == TextUtils.TruncateAt.START) {
            if (this.mMaximumVisibleLineCount == 1) {
                float sum = 0.0f;
                int i = len;
                while (i >= 0) {
                    float w = widths[((i - 1) + lineStart) - widthStart];
                    if (w + sum + ellipsisWidth > avail) {
                        break;
                    }
                    sum += w;
                    i--;
                }
                ellipsisStart = 0;
                ellipsisCount = i;
            } else if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Start Ellipsis only supported with one line");
            }
        } else if (where == TextUtils.TruncateAt.END || where == TextUtils.TruncateAt.MARQUEE || where == TextUtils.TruncateAt.END_SMALL) {
            float sum2 = 0.0f;
            int i2 = 0;
            while (i2 < len) {
                float w2 = widths[(i2 + lineStart) - widthStart];
                if (w2 + sum2 + ellipsisWidth > avail) {
                    break;
                }
                sum2 += w2;
                i2++;
            }
            ellipsisStart = i2;
            ellipsisCount = len - i2;
            if (forceEllipsis && ellipsisCount == 0 && len > 0) {
                ellipsisStart = len - 1;
                ellipsisCount = 1;
            }
        } else if (this.mMaximumVisibleLineCount == 1) {
            float lsum = 0.0f;
            float rsum = 0.0f;
            float ravail = (avail - ellipsisWidth) / 2.0f;
            int right = len;
            while (right >= 0) {
                float w3 = widths[((right - 1) + lineStart) - widthStart];
                if (w3 + rsum > ravail) {
                    break;
                }
                rsum += w3;
                right--;
            }
            float lavail = (avail - ellipsisWidth) - rsum;
            int left = 0;
            while (left < right) {
                float w4 = widths[(left + lineStart) - widthStart];
                if (w4 + lsum > lavail) {
                    break;
                }
                lsum += w4;
                left++;
            }
            ellipsisStart = left;
            ellipsisCount = right - left;
        } else if (Log.isLoggable(TAG, 5)) {
            Log.w(TAG, "Middle Ellipsis only supported with one line");
        }
        this.mLines[(this.mColumns * line) + 3] = ellipsisStart;
        this.mLines[(this.mColumns * line) + 4] = ellipsisCount;
    }

    @Override // android.text.Layout
    public int getLineForVertical(int vertical) {
        int high = this.mLineCount;
        int low = -1;
        int[] lines = this.mLines;
        while (high - low > 1) {
            int guess = (high + low) >> 1;
            if (lines[(this.mColumns * guess) + 1] > vertical) {
                high = guess;
            } else {
                low = guess;
            }
        }
        if (low < 0) {
            return 0;
        }
        return low;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mLineCount;
    }

    @Override // android.text.Layout
    public int getLineTop(int line) {
        int top = this.mLines[(this.mColumns * line) + 1];
        if (this.mMaximumVisibleLineCount > 0 && line >= this.mMaximumVisibleLineCount && line != this.mLineCount) {
            top += getBottomPadding();
        }
        return top;
    }

    @Override // android.text.Layout
    public int getLineDescent(int line) {
        int descent = this.mLines[(this.mColumns * line) + 2];
        if (this.mMaximumVisibleLineCount > 0 && line >= this.mMaximumVisibleLineCount - 1 && line != this.mLineCount) {
            descent += getBottomPadding();
        }
        return descent;
    }

    @Override // android.text.Layout
    public int getLineStart(int line) {
        return this.mLines[(this.mColumns * line) + 0] & 536870911;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int line) {
        return this.mLines[(this.mColumns * line) + 0] >> 30;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int line) {
        return (this.mLines[(this.mColumns * line) + 0] & 536870912) != 0;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int line) {
        return this.mLineDirections[line];
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
        if (this.mColumns < 5) {
            return 0;
        }
        return this.mLines[(this.mColumns * line) + 4];
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int line) {
        if (this.mColumns < 5) {
            return 0;
        }
        return this.mLines[(this.mColumns * line) + 3];
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prepare() {
        this.mMeasured = MeasuredText.obtain();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finish() {
        this.mMeasured = MeasuredText.recycle(this.mMeasured);
    }
}