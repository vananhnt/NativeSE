package android.graphics;

import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import java.util.Locale;

/* loaded from: Paint.class */
public class Paint {
    public int mNativePaint;
    private ColorFilter mColorFilter;
    private MaskFilter mMaskFilter;
    private PathEffect mPathEffect;
    private Rasterizer mRasterizer;
    private Shader mShader;
    private Typeface mTypeface;
    private Xfermode mXfermode;
    private boolean mHasCompatScaling;
    private float mCompatScaling;
    private float mInvCompatScaling;
    private Locale mLocale;
    public boolean hasShadow;
    public float shadowDx;
    public float shadowDy;
    public float shadowRadius;
    public int shadowColor;
    public int mBidiFlags;
    static final Style[] sStyleArray = {Style.FILL, Style.STROKE, Style.FILL_AND_STROKE};
    static final Cap[] sCapArray = {Cap.BUTT, Cap.ROUND, Cap.SQUARE};
    static final Join[] sJoinArray = {Join.MITER, Join.ROUND, Join.BEVEL};
    static final Align[] sAlignArray = {Align.LEFT, Align.CENTER, Align.RIGHT};
    public static final int ANTI_ALIAS_FLAG = 1;
    public static final int FILTER_BITMAP_FLAG = 2;
    public static final int DITHER_FLAG = 4;
    public static final int UNDERLINE_TEXT_FLAG = 8;
    public static final int STRIKE_THRU_TEXT_FLAG = 16;
    public static final int FAKE_BOLD_TEXT_FLAG = 32;
    public static final int LINEAR_TEXT_FLAG = 64;
    public static final int SUBPIXEL_TEXT_FLAG = 128;
    public static final int DEV_KERN_TEXT_FLAG = 256;
    public static final int LCD_RENDER_TEXT_FLAG = 512;
    public static final int EMBEDDED_BITMAP_TEXT_FLAG = 1024;
    public static final int AUTO_HINTING_TEXT_FLAG = 2048;
    public static final int VERTICAL_TEXT_FLAG = 4096;
    static final int DEFAULT_PAINT_FLAGS = 1280;
    public static final int HINTING_OFF = 0;
    public static final int HINTING_ON = 1;
    public static final int BIDI_LTR = 0;
    public static final int BIDI_RTL = 1;
    public static final int BIDI_DEFAULT_LTR = 2;
    public static final int BIDI_DEFAULT_RTL = 3;
    public static final int BIDI_FORCE_LTR = 4;
    public static final int BIDI_FORCE_RTL = 5;
    private static final int BIDI_MAX_FLAG_VALUE = 5;
    private static final int BIDI_FLAG_MASK = 7;
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;
    public static final int CURSOR_AFTER = 0;
    public static final int CURSOR_AT_OR_AFTER = 1;
    public static final int CURSOR_BEFORE = 2;
    public static final int CURSOR_AT_OR_BEFORE = 3;
    public static final int CURSOR_AT = 4;
    private static final int CURSOR_OPT_MAX_VALUE = 4;

    /* loaded from: Paint$FontMetrics.class */
    public static class FontMetrics {
        public float top;
        public float ascent;
        public float descent;
        public float bottom;
        public float leading;
    }

    public native int getFlags();

    public native void setFlags(int i);

    public native int getHinting();

    public native void setHinting(int i);

    public native void setAntiAlias(boolean z);

    public native void setDither(boolean z);

    public native void setLinearText(boolean z);

    public native void setSubpixelText(boolean z);

    public native void setUnderlineText(boolean z);

    public native void setStrikeThruText(boolean z);

    public native void setFakeBoldText(boolean z);

    public native void setFilterBitmap(boolean z);

    public native int getColor();

    public native void setColor(int i);

    public native int getAlpha();

    public native void setAlpha(int i);

    public native float getStrokeWidth();

    public native void setStrokeWidth(float f);

    public native float getStrokeMiter();

    public native void setStrokeMiter(float f);

    private native void nSetShadowLayer(float f, float f2, float f3, int i);

    public native float getTextSize();

    public native void setTextSize(float f);

    public native float getTextScaleX();

    public native void setTextScaleX(float f);

    public native float getTextSkewX();

    public native void setTextSkewX(float f);

    public native float ascent();

    public native float descent();

    public native float getFontMetrics(FontMetrics fontMetrics);

    public native int getFontMetricsInt(FontMetricsInt fontMetricsInt);

    private native float native_measureText(char[] cArr, int i, int i2, int i3);

    private native float native_measureText(String str, int i, int i2, int i3);

    private native float native_measureText(String str, int i);

    private native int native_breakText(char[] cArr, int i, int i2, float f, int i3, float[] fArr);

    private native int native_breakText(String str, boolean z, float f, int i, float[] fArr);

    private static native int native_init();

    private static native int native_initWithPaint(int i);

    private static native void native_reset(int i);

    private static native void native_set(int i, int i2);

    private static native int native_getStyle(int i);

    private static native void native_setStyle(int i, int i2);

    private static native int native_getStrokeCap(int i);

    private static native void native_setStrokeCap(int i, int i2);

    private static native int native_getStrokeJoin(int i);

    private static native void native_setStrokeJoin(int i, int i2);

    private static native boolean native_getFillPath(int i, int i2, int i3);

    private static native int native_setShader(int i, int i2);

    private static native int native_setColorFilter(int i, int i2);

    private static native int native_setXfermode(int i, int i2);

    private static native int native_setPathEffect(int i, int i2);

    private static native int native_setMaskFilter(int i, int i2);

    private static native int native_setTypeface(int i, int i2);

    private static native int native_setRasterizer(int i, int i2);

    private static native int native_getTextAlign(int i);

    private static native void native_setTextAlign(int i, int i2);

    private static native void native_setTextLocale(int i, String str);

    private static native int native_getTextWidths(int i, char[] cArr, int i2, int i3, int i4, float[] fArr);

    private static native int native_getTextWidths(int i, String str, int i2, int i3, int i4, float[] fArr);

    private static native int native_getTextGlyphs(int i, String str, int i2, int i3, int i4, int i5, int i6, char[] cArr);

    private static native float native_getTextRunAdvances(int i, char[] cArr, int i2, int i3, int i4, int i5, int i6, float[] fArr, int i7);

    private static native float native_getTextRunAdvances(int i, String str, int i2, int i3, int i4, int i5, int i6, float[] fArr, int i7);

    private native int native_getTextRunCursor(int i, char[] cArr, int i2, int i3, int i4, int i5, int i6);

    private native int native_getTextRunCursor(int i, String str, int i2, int i3, int i4, int i5, int i6);

    private static native void native_getTextPath(int i, int i2, char[] cArr, int i3, int i4, float f, float f2, int i5);

    private static native void native_getTextPath(int i, int i2, String str, int i3, int i4, float f, float f2, int i5);

    private static native void nativeGetStringBounds(int i, String str, int i2, int i3, int i4, Rect rect);

    private static native void nativeGetCharArrayBounds(int i, char[] cArr, int i2, int i3, int i4, Rect rect);

    private static native void finalizer(int i);

    /* loaded from: Paint$Style.class */
    public enum Style {
        FILL(0),
        STROKE(1),
        FILL_AND_STROKE(2);
        
        final int nativeInt;

        Style(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    /* loaded from: Paint$Cap.class */
    public enum Cap {
        BUTT(0),
        ROUND(1),
        SQUARE(2);
        
        final int nativeInt;

        Cap(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    /* loaded from: Paint$Join.class */
    public enum Join {
        MITER(0),
        ROUND(1),
        BEVEL(2);
        
        final int nativeInt;

        Join(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    /* loaded from: Paint$Align.class */
    public enum Align {
        LEFT(0),
        CENTER(1),
        RIGHT(2);
        
        final int nativeInt;

        Align(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public Paint() {
        this(0);
    }

    public Paint(int flags) {
        this.mBidiFlags = 2;
        this.mNativePaint = native_init();
        setFlags(flags | 1280);
        this.mInvCompatScaling = 1.0f;
        this.mCompatScaling = 1.0f;
        setTextLocale(Locale.getDefault());
    }

    public Paint(Paint paint) {
        this.mBidiFlags = 2;
        this.mNativePaint = native_initWithPaint(paint.mNativePaint);
        setClassVariablesFrom(paint);
    }

    public void reset() {
        native_reset(this.mNativePaint);
        setFlags(1280);
        this.mColorFilter = null;
        this.mMaskFilter = null;
        this.mPathEffect = null;
        this.mRasterizer = null;
        this.mShader = null;
        this.mTypeface = null;
        this.mXfermode = null;
        this.mHasCompatScaling = false;
        this.mCompatScaling = 1.0f;
        this.mInvCompatScaling = 1.0f;
        this.hasShadow = false;
        this.shadowDx = 0.0f;
        this.shadowDy = 0.0f;
        this.shadowRadius = 0.0f;
        this.shadowColor = 0;
        this.mBidiFlags = 2;
        setTextLocale(Locale.getDefault());
    }

    public void set(Paint src) {
        if (this != src) {
            native_set(this.mNativePaint, src.mNativePaint);
            setClassVariablesFrom(src);
        }
    }

    private void setClassVariablesFrom(Paint paint) {
        this.mColorFilter = paint.mColorFilter;
        this.mMaskFilter = paint.mMaskFilter;
        this.mPathEffect = paint.mPathEffect;
        this.mRasterizer = paint.mRasterizer;
        if (paint.mShader != null) {
            this.mShader = paint.mShader.copy();
        } else {
            this.mShader = null;
        }
        this.mTypeface = paint.mTypeface;
        this.mXfermode = paint.mXfermode;
        this.mHasCompatScaling = paint.mHasCompatScaling;
        this.mCompatScaling = paint.mCompatScaling;
        this.mInvCompatScaling = paint.mInvCompatScaling;
        this.hasShadow = paint.hasShadow;
        this.shadowDx = paint.shadowDx;
        this.shadowDy = paint.shadowDy;
        this.shadowRadius = paint.shadowRadius;
        this.shadowColor = paint.shadowColor;
        this.mBidiFlags = paint.mBidiFlags;
        this.mLocale = paint.mLocale;
    }

    public void setCompatibilityScaling(float factor) {
        if (factor == 1.0d) {
            this.mHasCompatScaling = false;
            this.mInvCompatScaling = 1.0f;
            this.mCompatScaling = 1.0f;
            return;
        }
        this.mHasCompatScaling = true;
        this.mCompatScaling = factor;
        this.mInvCompatScaling = 1.0f / factor;
    }

    public int getBidiFlags() {
        return this.mBidiFlags;
    }

    public void setBidiFlags(int flags) {
        int flags2 = flags & 7;
        if (flags2 > 5) {
            throw new IllegalArgumentException("unknown bidi flag: " + flags2);
        }
        this.mBidiFlags = flags2;
    }

    public final boolean isAntiAlias() {
        return (getFlags() & 1) != 0;
    }

    public final boolean isDither() {
        return (getFlags() & 4) != 0;
    }

    public final boolean isLinearText() {
        return (getFlags() & 64) != 0;
    }

    public final boolean isSubpixelText() {
        return (getFlags() & 128) != 0;
    }

    public final boolean isUnderlineText() {
        return (getFlags() & 8) != 0;
    }

    public final boolean isStrikeThruText() {
        return (getFlags() & 16) != 0;
    }

    public final boolean isFakeBoldText() {
        return (getFlags() & 32) != 0;
    }

    public final boolean isFilterBitmap() {
        return (getFlags() & 2) != 0;
    }

    public Style getStyle() {
        return sStyleArray[native_getStyle(this.mNativePaint)];
    }

    public void setStyle(Style style) {
        native_setStyle(this.mNativePaint, style.nativeInt);
    }

    public void setARGB(int a, int r, int g, int b) {
        setColor((a << 24) | (r << 16) | (g << 8) | b);
    }

    public Cap getStrokeCap() {
        return sCapArray[native_getStrokeCap(this.mNativePaint)];
    }

    public void setStrokeCap(Cap cap) {
        native_setStrokeCap(this.mNativePaint, cap.nativeInt);
    }

    public Join getStrokeJoin() {
        return sJoinArray[native_getStrokeJoin(this.mNativePaint)];
    }

    public void setStrokeJoin(Join join) {
        native_setStrokeJoin(this.mNativePaint, join.nativeInt);
    }

    public boolean getFillPath(Path src, Path dst) {
        return native_getFillPath(this.mNativePaint, src.ni(), dst.ni());
    }

    public Shader getShader() {
        return this.mShader;
    }

    public Shader setShader(Shader shader) {
        int shaderNative = 0;
        if (shader != null) {
            shaderNative = shader.native_instance;
        }
        native_setShader(this.mNativePaint, shaderNative);
        this.mShader = shader;
        return shader;
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public ColorFilter setColorFilter(ColorFilter filter) {
        int filterNative = 0;
        if (filter != null) {
            filterNative = filter.native_instance;
        }
        native_setColorFilter(this.mNativePaint, filterNative);
        this.mColorFilter = filter;
        return filter;
    }

    public Xfermode getXfermode() {
        return this.mXfermode;
    }

    public Xfermode setXfermode(Xfermode xfermode) {
        int xfermodeNative = 0;
        if (xfermode != null) {
            xfermodeNative = xfermode.native_instance;
        }
        native_setXfermode(this.mNativePaint, xfermodeNative);
        this.mXfermode = xfermode;
        return xfermode;
    }

    public PathEffect getPathEffect() {
        return this.mPathEffect;
    }

    public PathEffect setPathEffect(PathEffect effect) {
        int effectNative = 0;
        if (effect != null) {
            effectNative = effect.native_instance;
        }
        native_setPathEffect(this.mNativePaint, effectNative);
        this.mPathEffect = effect;
        return effect;
    }

    public MaskFilter getMaskFilter() {
        return this.mMaskFilter;
    }

    public MaskFilter setMaskFilter(MaskFilter maskfilter) {
        int maskfilterNative = 0;
        if (maskfilter != null) {
            maskfilterNative = maskfilter.native_instance;
        }
        native_setMaskFilter(this.mNativePaint, maskfilterNative);
        this.mMaskFilter = maskfilter;
        return maskfilter;
    }

    public Typeface getTypeface() {
        return this.mTypeface;
    }

    public Typeface setTypeface(Typeface typeface) {
        int typefaceNative = 0;
        if (typeface != null) {
            typefaceNative = typeface.native_instance;
        }
        native_setTypeface(this.mNativePaint, typefaceNative);
        this.mTypeface = typeface;
        return typeface;
    }

    public Rasterizer getRasterizer() {
        return this.mRasterizer;
    }

    public Rasterizer setRasterizer(Rasterizer rasterizer) {
        int rasterizerNative = 0;
        if (rasterizer != null) {
            rasterizerNative = rasterizer.native_instance;
        }
        native_setRasterizer(this.mNativePaint, rasterizerNative);
        this.mRasterizer = rasterizer;
        return rasterizer;
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.hasShadow = radius > 0.0f;
        this.shadowRadius = radius;
        this.shadowDx = dx;
        this.shadowDy = dy;
        this.shadowColor = color;
        nSetShadowLayer(radius, dx, dy, color);
    }

    public void clearShadowLayer() {
        this.hasShadow = false;
        nSetShadowLayer(0.0f, 0.0f, 0.0f, 0);
    }

    public Align getTextAlign() {
        return sAlignArray[native_getTextAlign(this.mNativePaint)];
    }

    public void setTextAlign(Align align) {
        native_setTextAlign(this.mNativePaint, align.nativeInt);
    }

    public Locale getTextLocale() {
        return this.mLocale;
    }

    public void setTextLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        if (locale.equals(this.mLocale)) {
            return;
        }
        this.mLocale = locale;
        native_setTextLocale(this.mNativePaint, locale.toString());
    }

    public FontMetrics getFontMetrics() {
        FontMetrics fm = new FontMetrics();
        getFontMetrics(fm);
        return fm;
    }

    /* loaded from: Paint$FontMetricsInt.class */
    public static class FontMetricsInt {
        public int top;
        public int ascent;
        public int descent;
        public int bottom;
        public int leading;

        public String toString() {
            return "FontMetricsInt: top=" + this.top + " ascent=" + this.ascent + " descent=" + this.descent + " bottom=" + this.bottom + " leading=" + this.leading;
        }
    }

    public FontMetricsInt getFontMetricsInt() {
        FontMetricsInt fm = new FontMetricsInt();
        getFontMetricsInt(fm);
        return fm;
    }

    public float getFontSpacing() {
        return getFontMetrics(null);
    }

    public float measureText(char[] text, int index, int count) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((index | count) < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (text.length == 0 || count == 0) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            return (float) Math.ceil(native_measureText(text, index, count, this.mBidiFlags));
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        float w = native_measureText(text, index, count, this.mBidiFlags);
        setTextSize(oldSize);
        return (float) Math.ceil(w * this.mInvCompatScaling);
    }

    public float measureText(String text, int start, int end) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (text.length() == 0 || start == end) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            return (float) Math.ceil(native_measureText(text, start, end, this.mBidiFlags));
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        float w = native_measureText(text, start, end, this.mBidiFlags);
        setTextSize(oldSize);
        return (float) Math.ceil(w * this.mInvCompatScaling);
    }

    public float measureText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (text.length() == 0) {
            return 0.0f;
        }
        if (!this.mHasCompatScaling) {
            return (float) Math.ceil(native_measureText(text, this.mBidiFlags));
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        float w = native_measureText(text, this.mBidiFlags);
        setTextSize(oldSize);
        return (float) Math.ceil(w * this.mInvCompatScaling);
    }

    public float measureText(CharSequence text, int start, int end) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (text.length() == 0 || start == end) {
            return 0.0f;
        }
        if (text instanceof String) {
            return measureText((String) text, start, end);
        }
        if ((text instanceof SpannedString) || (text instanceof SpannableString)) {
            return measureText(text.toString(), start, end);
        }
        if (text instanceof GraphicsOperations) {
            return ((GraphicsOperations) text).measureText(start, end, this);
        }
        char[] buf = TemporaryBuffer.obtain(end - start);
        TextUtils.getChars(text, start, end, buf, 0);
        float result = measureText(buf, 0, end - start);
        TemporaryBuffer.recycle(buf);
        return result;
    }

    public int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (index < 0 || text.length - index < Math.abs(count)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (text.length == 0 || count == 0) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_breakText(text, index, count, maxWidth, this.mBidiFlags, measuredWidth);
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        int res = native_breakText(text, index, count, maxWidth * this.mCompatScaling, this.mBidiFlags, measuredWidth);
        setTextSize(oldSize);
        if (measuredWidth != null) {
            measuredWidth[0] = measuredWidth[0] * this.mInvCompatScaling;
        }
        return res;
    }

    public int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth) {
        int result;
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (text.length() == 0 || start == end) {
            return 0;
        }
        if (start == 0 && (text instanceof String) && end == text.length()) {
            return breakText((String) text, measureForwards, maxWidth, measuredWidth);
        }
        char[] buf = TemporaryBuffer.obtain(end - start);
        TextUtils.getChars(text, start, end, buf, 0);
        if (measureForwards) {
            result = breakText(buf, 0, end - start, maxWidth, measuredWidth);
        } else {
            result = breakText(buf, 0, -(end - start), maxWidth, measuredWidth);
        }
        TemporaryBuffer.recycle(buf);
        return result;
    }

    public int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (text.length() == 0) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_breakText(text, measureForwards, maxWidth, this.mBidiFlags, measuredWidth);
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        int res = native_breakText(text, measureForwards, maxWidth * this.mCompatScaling, this.mBidiFlags, measuredWidth);
        setTextSize(oldSize);
        if (measuredWidth != null) {
            measuredWidth[0] = measuredWidth[0] * this.mInvCompatScaling;
        }
        return res;
    }

    public int getTextWidths(char[] text, int index, int count, float[] widths) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((index | count) < 0 || index + count > text.length || count > widths.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (text.length == 0 || count == 0) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_getTextWidths(this.mNativePaint, text, index, count, this.mBidiFlags, widths);
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        int res = native_getTextWidths(this.mNativePaint, text, index, count, this.mBidiFlags, widths);
        setTextSize(oldSize);
        for (int i = 0; i < res; i++) {
            int i2 = i;
            widths[i2] = widths[i2] * this.mInvCompatScaling;
        }
        return res;
    }

    public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (end - start > widths.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (text.length() == 0 || start == end) {
            return 0;
        }
        if (text instanceof String) {
            return getTextWidths((String) text, start, end, widths);
        }
        if ((text instanceof SpannedString) || (text instanceof SpannableString)) {
            return getTextWidths(text.toString(), start, end, widths);
        }
        if (text instanceof GraphicsOperations) {
            return ((GraphicsOperations) text).getTextWidths(start, end, widths, this);
        }
        char[] buf = TemporaryBuffer.obtain(end - start);
        TextUtils.getChars(text, start, end, buf, 0);
        int result = getTextWidths(buf, 0, end - start, widths);
        TemporaryBuffer.recycle(buf);
        return result;
    }

    public int getTextWidths(String text, int start, int end, float[] widths) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (end - start > widths.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (text.length() == 0 || start == end) {
            return 0;
        }
        if (!this.mHasCompatScaling) {
            return native_getTextWidths(this.mNativePaint, text, start, end, this.mBidiFlags, widths);
        }
        float oldSize = getTextSize();
        setTextSize(oldSize * this.mCompatScaling);
        int res = native_getTextWidths(this.mNativePaint, text, start, end, this.mBidiFlags, widths);
        setTextSize(oldSize);
        for (int i = 0; i < res; i++) {
            int i2 = i;
            widths[i2] = widths[i2] * this.mInvCompatScaling;
        }
        return res;
    }

    public int getTextWidths(String text, float[] widths) {
        return getTextWidths(text, 0, text.length(), widths);
    }

    public int getTextGlyphs(String text, int start, int end, int contextStart, int contextEnd, int flags, char[] glyphs) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (flags != 0 && flags != 1) {
            throw new IllegalArgumentException("unknown flags value: " + flags);
        }
        if ((start | end | contextStart | contextEnd | (end - start) | (start - contextStart) | (contextEnd - end) | (text.length() - end) | (text.length() - contextEnd)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (end - start > glyphs.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return native_getTextGlyphs(this.mNativePaint, text, start, end, contextStart, contextEnd, flags, glyphs);
    }

    public float getTextRunAdvances(char[] chars, int index, int count, int contextIndex, int contextCount, int flags, float[] advances, int advancesIndex) {
        if (chars == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (flags == 0 || flags == 1) {
            if ((index | count | contextIndex | contextCount | advancesIndex | (index - contextIndex) | (contextCount - count) | ((contextIndex + contextCount) - (index + count)) | (chars.length - (contextIndex + contextCount)) | (advances == null ? 0 : advances.length - (advancesIndex + count))) < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (chars.length == 0 || count == 0) {
                return 0.0f;
            }
            if (!this.mHasCompatScaling) {
                return native_getTextRunAdvances(this.mNativePaint, chars, index, count, contextIndex, contextCount, flags, advances, advancesIndex);
            }
            float oldSize = getTextSize();
            setTextSize(oldSize * this.mCompatScaling);
            float res = native_getTextRunAdvances(this.mNativePaint, chars, index, count, contextIndex, contextCount, flags, advances, advancesIndex);
            setTextSize(oldSize);
            if (advances != null) {
                int i = advancesIndex;
                int e = i + count;
                while (i < e) {
                    int i2 = i;
                    advances[i2] = advances[i2] * this.mInvCompatScaling;
                    i++;
                }
            }
            return res * this.mInvCompatScaling;
        }
        throw new IllegalArgumentException("unknown flags value: " + flags);
    }

    public float getTextRunAdvances(CharSequence text, int start, int end, int contextStart, int contextEnd, int flags, float[] advances, int advancesIndex) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((start | end | contextStart | contextEnd | advancesIndex | (end - start) | (start - contextStart) | (contextEnd - end) | (text.length() - contextEnd) | (advances == null ? 0 : (advances.length - advancesIndex) - (end - start))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (text instanceof String) {
            return getTextRunAdvances((String) text, start, end, contextStart, contextEnd, flags, advances, advancesIndex);
        }
        if ((text instanceof SpannedString) || (text instanceof SpannableString)) {
            return getTextRunAdvances(text.toString(), start, end, contextStart, contextEnd, flags, advances, advancesIndex);
        }
        if (text instanceof GraphicsOperations) {
            return ((GraphicsOperations) text).getTextRunAdvances(start, end, contextStart, contextEnd, flags, advances, advancesIndex, this);
        }
        if (text.length() == 0 || end == start) {
            return 0.0f;
        }
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        char[] buf = TemporaryBuffer.obtain(contextLen);
        TextUtils.getChars(text, contextStart, contextEnd, buf, 0);
        float result = getTextRunAdvances(buf, start - contextStart, len, 0, contextLen, flags, advances, advancesIndex);
        TemporaryBuffer.recycle(buf);
        return result;
    }

    public float getTextRunAdvances(String text, int start, int end, int contextStart, int contextEnd, int flags, float[] advances, int advancesIndex) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (flags == 0 || flags == 1) {
            if ((start | end | contextStart | contextEnd | advancesIndex | (end - start) | (start - contextStart) | (contextEnd - end) | (text.length() - contextEnd) | (advances == null ? 0 : (advances.length - advancesIndex) - (end - start))) < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (text.length() == 0 || start == end) {
                return 0.0f;
            }
            if (!this.mHasCompatScaling) {
                return native_getTextRunAdvances(this.mNativePaint, text, start, end, contextStart, contextEnd, flags, advances, advancesIndex);
            }
            float oldSize = getTextSize();
            setTextSize(oldSize * this.mCompatScaling);
            float totalAdvance = native_getTextRunAdvances(this.mNativePaint, text, start, end, contextStart, contextEnd, flags, advances, advancesIndex);
            setTextSize(oldSize);
            if (advances != null) {
                int i = advancesIndex;
                int e = i + (end - start);
                while (i < e) {
                    int i2 = i;
                    advances[i2] = advances[i2] * this.mInvCompatScaling;
                    i++;
                }
            }
            return totalAdvance * this.mInvCompatScaling;
        }
        throw new IllegalArgumentException("unknown flags value: " + flags);
    }

    public int getTextRunCursor(char[] text, int contextStart, int contextLength, int flags, int offset, int cursorOpt) {
        int contextEnd = contextStart + contextLength;
        if ((contextStart | contextEnd | offset | (contextEnd - contextStart) | (offset - contextStart) | (contextEnd - offset) | (text.length - contextEnd) | cursorOpt) < 0 || cursorOpt > 4) {
            throw new IndexOutOfBoundsException();
        }
        return native_getTextRunCursor(this.mNativePaint, text, contextStart, contextLength, flags, offset, cursorOpt);
    }

    public int getTextRunCursor(CharSequence text, int contextStart, int contextEnd, int flags, int offset, int cursorOpt) {
        if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
            return getTextRunCursor(text.toString(), contextStart, contextEnd, flags, offset, cursorOpt);
        }
        if (text instanceof GraphicsOperations) {
            return ((GraphicsOperations) text).getTextRunCursor(contextStart, contextEnd, flags, offset, cursorOpt, this);
        }
        int contextLen = contextEnd - contextStart;
        char[] buf = TemporaryBuffer.obtain(contextLen);
        TextUtils.getChars(text, contextStart, contextEnd, buf, 0);
        int result = getTextRunCursor(buf, 0, contextLen, flags, offset - contextStart, cursorOpt);
        TemporaryBuffer.recycle(buf);
        return result;
    }

    public int getTextRunCursor(String text, int contextStart, int contextEnd, int flags, int offset, int cursorOpt) {
        if ((contextStart | contextEnd | offset | (contextEnd - contextStart) | (offset - contextStart) | (contextEnd - offset) | (text.length() - contextEnd) | cursorOpt) < 0 || cursorOpt > 4) {
            throw new IndexOutOfBoundsException();
        }
        return native_getTextRunCursor(this.mNativePaint, text, contextStart, contextEnd, flags, offset, cursorOpt);
    }

    public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
        if ((index | count) < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_getTextPath(this.mNativePaint, this.mBidiFlags, text, index, count, x, y, path.ni());
    }

    public void getTextPath(String text, int start, int end, float x, float y, Path path) {
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_getTextPath(this.mNativePaint, this.mBidiFlags, text, start, end, x, y, path.ni());
    }

    public void getTextBounds(String text, int start, int end, Rect bounds) {
        if ((start | end | (end - start) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (bounds == null) {
            throw new NullPointerException("need bounds Rect");
        }
        nativeGetStringBounds(this.mNativePaint, text, start, end, this.mBidiFlags, bounds);
    }

    public void getTextBounds(char[] text, int index, int count, Rect bounds) {
        if ((index | count) < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (bounds == null) {
            throw new NullPointerException("need bounds Rect");
        }
        nativeGetCharArrayBounds(this.mNativePaint, text, index, count, this.mBidiFlags, bounds);
    }

    protected void finalize() throws Throwable {
        try {
            finalizer(this.mNativePaint);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }
}