package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.media.videoeditor.MediaProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: GradientDrawable.class */
public class GradientDrawable extends Drawable {
    public static final int RECTANGLE = 0;
    public static final int OVAL = 1;
    public static final int LINE = 2;
    public static final int RING = 3;
    public static final int LINEAR_GRADIENT = 0;
    public static final int RADIAL_GRADIENT = 1;
    public static final int SWEEP_GRADIENT = 2;
    private GradientState mGradientState;
    private final Paint mFillPaint;
    private Rect mPadding;
    private Paint mStrokePaint;
    private ColorFilter mColorFilter;
    private int mAlpha;
    private boolean mDither;
    private final Path mPath;
    private final RectF mRect;
    private Paint mLayerPaint;
    private boolean mRectIsDirty;
    private boolean mMutated;
    private Path mRingPath;
    private boolean mPathIsDirty;

    /* loaded from: GradientDrawable$Orientation.class */
    public enum Orientation {
        TOP_BOTTOM,
        TR_BL,
        RIGHT_LEFT,
        BR_TL,
        BOTTOM_TOP,
        BL_TR,
        LEFT_RIGHT,
        TL_BR
    }

    public GradientDrawable() {
        this(new GradientState(Orientation.TOP_BOTTOM, null));
    }

    public GradientDrawable(Orientation orientation, int[] colors) {
        this(new GradientState(orientation, colors));
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        if (this.mPadding != null) {
            padding.set(this.mPadding);
            return true;
        }
        return super.getPadding(padding);
    }

    public void setCornerRadii(float[] radii) {
        this.mGradientState.setCornerRadii(radii);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setCornerRadius(float radius) {
        this.mGradientState.setCornerRadius(radius);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setStroke(int width, int color) {
        setStroke(width, color, 0.0f, 0.0f);
    }

    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        this.mGradientState.setStroke(width, color, dashWidth, dashGap);
        if (this.mStrokePaint == null) {
            this.mStrokePaint = new Paint(1);
            this.mStrokePaint.setStyle(Paint.Style.STROKE);
        }
        this.mStrokePaint.setStrokeWidth(width);
        this.mStrokePaint.setColor(color);
        DashPathEffect e = null;
        if (dashWidth > 0.0f) {
            e = new DashPathEffect(new float[]{dashWidth, dashGap}, 0.0f);
        }
        this.mStrokePaint.setPathEffect(e);
        invalidateSelf();
    }

    public void setSize(int width, int height) {
        this.mGradientState.setSize(width, height);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setShape(int shape) {
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mGradientState.setShape(shape);
        invalidateSelf();
    }

    public void setGradientType(int gradient) {
        this.mGradientState.setGradientType(gradient);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setGradientCenter(float x, float y) {
        this.mGradientState.setGradientCenter(x, y);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setGradientRadius(float gradientRadius) {
        this.mGradientState.setGradientRadius(gradientRadius);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setUseLevel(boolean useLevel) {
        this.mGradientState.mUseLevel = useLevel;
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    private int modulateAlpha(int alpha) {
        int scale = this.mAlpha + (this.mAlpha >> 7);
        return (alpha * scale) >> 8;
    }

    public Orientation getOrientation() {
        return this.mGradientState.mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        this.mGradientState.mOrientation = orientation;
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    public void setColors(int[] colors) {
        this.mGradientState.setColors(colors);
        this.mRectIsDirty = true;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (!ensureValidRect()) {
            return;
        }
        int prevFillAlpha = this.mFillPaint.getAlpha();
        int prevStrokeAlpha = this.mStrokePaint != null ? this.mStrokePaint.getAlpha() : 0;
        int currFillAlpha = modulateAlpha(prevFillAlpha);
        int currStrokeAlpha = modulateAlpha(prevStrokeAlpha);
        boolean haveStroke = currStrokeAlpha > 0 && this.mStrokePaint != null && this.mStrokePaint.getStrokeWidth() > 0.0f;
        boolean haveFill = currFillAlpha > 0;
        GradientState st = this.mGradientState;
        boolean useLayer = haveStroke && haveFill && st.mShape != 2 && currStrokeAlpha < 255 && (this.mAlpha < 255 || this.mColorFilter != null);
        if (useLayer) {
            if (this.mLayerPaint == null) {
                this.mLayerPaint = new Paint();
            }
            this.mLayerPaint.setDither(this.mDither);
            this.mLayerPaint.setAlpha(this.mAlpha);
            this.mLayerPaint.setColorFilter(this.mColorFilter);
            float rad = this.mStrokePaint.getStrokeWidth();
            canvas.saveLayer(this.mRect.left - rad, this.mRect.top - rad, this.mRect.right + rad, this.mRect.bottom + rad, this.mLayerPaint, 4);
            this.mFillPaint.setColorFilter(null);
            this.mStrokePaint.setColorFilter(null);
        } else {
            this.mFillPaint.setAlpha(currFillAlpha);
            this.mFillPaint.setDither(this.mDither);
            this.mFillPaint.setColorFilter(this.mColorFilter);
            if (this.mColorFilter != null && !this.mGradientState.mHasSolidColor) {
                this.mFillPaint.setColor(this.mAlpha << 24);
            }
            if (haveStroke) {
                this.mStrokePaint.setAlpha(currStrokeAlpha);
                this.mStrokePaint.setDither(this.mDither);
                this.mStrokePaint.setColorFilter(this.mColorFilter);
            }
        }
        switch (st.mShape) {
            case 0:
                if (st.mRadiusArray != null) {
                    if (this.mPathIsDirty || this.mRectIsDirty) {
                        this.mPath.reset();
                        this.mPath.addRoundRect(this.mRect, st.mRadiusArray, Path.Direction.CW);
                        this.mRectIsDirty = false;
                        this.mPathIsDirty = false;
                    }
                    canvas.drawPath(this.mPath, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawPath(this.mPath, this.mStrokePaint);
                        break;
                    }
                } else if (st.mRadius > 0.0f) {
                    float rad2 = st.mRadius;
                    float r = Math.min(this.mRect.width(), this.mRect.height()) * 0.5f;
                    if (rad2 > r) {
                        rad2 = r;
                    }
                    canvas.drawRoundRect(this.mRect, rad2, rad2, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawRoundRect(this.mRect, rad2, rad2, this.mStrokePaint);
                        break;
                    }
                } else {
                    if (this.mFillPaint.getColor() != 0 || this.mColorFilter != null || this.mFillPaint.getShader() != null) {
                        canvas.drawRect(this.mRect, this.mFillPaint);
                    }
                    if (haveStroke) {
                        canvas.drawRect(this.mRect, this.mStrokePaint);
                        break;
                    }
                }
                break;
            case 1:
                canvas.drawOval(this.mRect, this.mFillPaint);
                if (haveStroke) {
                    canvas.drawOval(this.mRect, this.mStrokePaint);
                    break;
                }
                break;
            case 2:
                RectF r2 = this.mRect;
                float y = r2.centerY();
                canvas.drawLine(r2.left, y, r2.right, y, this.mStrokePaint);
                break;
            case 3:
                Path path = buildRing(st);
                canvas.drawPath(path, this.mFillPaint);
                if (haveStroke) {
                    canvas.drawPath(path, this.mStrokePaint);
                    break;
                }
                break;
        }
        if (useLayer) {
            canvas.restore();
            return;
        }
        this.mFillPaint.setAlpha(prevFillAlpha);
        if (haveStroke) {
            this.mStrokePaint.setAlpha(prevStrokeAlpha);
        }
    }

    private Path buildRing(GradientState st) {
        if (this.mRingPath == null || (st.mUseLevelForShape && this.mPathIsDirty)) {
            this.mPathIsDirty = false;
            float sweep = st.mUseLevelForShape ? (360.0f * getLevel()) / 10000.0f : 360.0f;
            RectF bounds = new RectF(this.mRect);
            float x = bounds.width() / 2.0f;
            float y = bounds.height() / 2.0f;
            float thickness = st.mThickness != -1 ? st.mThickness : bounds.width() / st.mThicknessRatio;
            float radius = st.mInnerRadius != -1 ? st.mInnerRadius : bounds.width() / st.mInnerRadiusRatio;
            RectF innerBounds = new RectF(bounds);
            innerBounds.inset(x - radius, y - radius);
            RectF bounds2 = new RectF(innerBounds);
            bounds2.inset(-thickness, -thickness);
            if (this.mRingPath == null) {
                this.mRingPath = new Path();
            } else {
                this.mRingPath.reset();
            }
            Path ringPath = this.mRingPath;
            if (sweep < 360.0f && sweep > -360.0f) {
                ringPath.setFillType(Path.FillType.EVEN_ODD);
                ringPath.moveTo(x + radius, y);
                ringPath.lineTo(x + radius + thickness, y);
                ringPath.arcTo(bounds2, 0.0f, sweep, false);
                ringPath.arcTo(innerBounds, sweep, -sweep, false);
                ringPath.close();
            } else {
                ringPath.addOval(bounds2, Path.Direction.CW);
                ringPath.addOval(innerBounds, Path.Direction.CCW);
            }
            return ringPath;
        }
        return this.mRingPath;
    }

    public void setColor(int argb) {
        this.mGradientState.setSolidColor(argb);
        this.mFillPaint.setColor(argb);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mGradientState.mChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (alpha != this.mAlpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        if (dither != this.mDither) {
            this.mDither = dither;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        if (cf != this.mColorFilter) {
            this.mColorFilter = cf;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mGradientState.mOpaque ? -1 : -3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mRectIsDirty = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        super.onLevelChange(level);
        this.mRectIsDirty = true;
        this.mPathIsDirty = true;
        invalidateSelf();
        return true;
    }

    private boolean ensureValidRect() {
        float x0;
        float y0;
        float x1;
        float y1;
        if (this.mRectIsDirty) {
            this.mRectIsDirty = false;
            Rect bounds = getBounds();
            float inset = 0.0f;
            if (this.mStrokePaint != null) {
                inset = this.mStrokePaint.getStrokeWidth() * 0.5f;
            }
            GradientState st = this.mGradientState;
            this.mRect.set(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset);
            int[] colors = st.mColors;
            if (colors != null) {
                RectF r = this.mRect;
                if (st.mGradient == 0) {
                    float level = st.mUseLevel ? getLevel() / 10000.0f : 1.0f;
                    switch (st.mOrientation) {
                        case TOP_BOTTOM:
                            x0 = r.left;
                            y0 = r.top;
                            x1 = x0;
                            y1 = level * r.bottom;
                            break;
                        case TR_BL:
                            x0 = r.right;
                            y0 = r.top;
                            x1 = level * r.left;
                            y1 = level * r.bottom;
                            break;
                        case RIGHT_LEFT:
                            x0 = r.right;
                            y0 = r.top;
                            x1 = level * r.left;
                            y1 = y0;
                            break;
                        case BR_TL:
                            x0 = r.right;
                            y0 = r.bottom;
                            x1 = level * r.left;
                            y1 = level * r.top;
                            break;
                        case BOTTOM_TOP:
                            x0 = r.left;
                            y0 = r.bottom;
                            x1 = x0;
                            y1 = level * r.top;
                            break;
                        case BL_TR:
                            x0 = r.left;
                            y0 = r.bottom;
                            x1 = level * r.right;
                            y1 = level * r.top;
                            break;
                        case LEFT_RIGHT:
                            x0 = r.left;
                            y0 = r.top;
                            x1 = level * r.right;
                            y1 = y0;
                            break;
                        default:
                            x0 = r.left;
                            y0 = r.top;
                            x1 = level * r.right;
                            y1 = level * r.bottom;
                            break;
                    }
                    this.mFillPaint.setShader(new LinearGradient(x0, y0, x1, y1, colors, st.mPositions, Shader.TileMode.CLAMP));
                } else if (st.mGradient == 1) {
                    float x02 = r.left + ((r.right - r.left) * st.mCenterX);
                    float y02 = r.top + ((r.bottom - r.top) * st.mCenterY);
                    float level2 = st.mUseLevel ? getLevel() / 10000.0f : 1.0f;
                    this.mFillPaint.setShader(new RadialGradient(x02, y02, level2 * st.mGradientRadius, colors, (float[]) null, Shader.TileMode.CLAMP));
                } else if (st.mGradient == 2) {
                    float x03 = r.left + ((r.right - r.left) * st.mCenterX);
                    float y03 = r.top + ((r.bottom - r.top) * st.mCenterY);
                    int[] tempColors = colors;
                    float[] tempPositions = null;
                    if (st.mUseLevel) {
                        tempColors = st.mTempColors;
                        int length = colors.length;
                        if (tempColors == null || tempColors.length != length + 1) {
                            int[] iArr = new int[length + 1];
                            st.mTempColors = iArr;
                            tempColors = iArr;
                        }
                        System.arraycopy(colors, 0, tempColors, 0, length);
                        tempColors[length] = colors[length - 1];
                        tempPositions = st.mTempPositions;
                        float fraction = 1.0f / (length - 1);
                        if (tempPositions == null || tempPositions.length != length + 1) {
                            float[] fArr = new float[length + 1];
                            st.mTempPositions = fArr;
                            tempPositions = fArr;
                        }
                        float level3 = getLevel() / 10000.0f;
                        for (int i = 0; i < length; i++) {
                            tempPositions[i] = i * fraction * level3;
                        }
                        tempPositions[length] = 1.0f;
                    }
                    this.mFillPaint.setShader(new SweepGradient(x03, y03, tempColors, tempPositions));
                }
                if (!st.mHasSolidColor) {
                    this.mFillPaint.setColor(-16777216);
                }
            }
        }
        return !this.mRect.isEmpty();
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        int depth;
        GradientState st = this.mGradientState;
        TypedArray a = r.obtainAttributes(attrs, R.styleable.GradientDrawable);
        super.inflateWithAttributes(r, parser, a, 1);
        int shapeType = a.getInt(2, 0);
        boolean dither = a.getBoolean(0, false);
        if (shapeType == 3) {
            st.mInnerRadius = a.getDimensionPixelSize(6, -1);
            if (st.mInnerRadius == -1) {
                st.mInnerRadiusRatio = a.getFloat(3, 3.0f);
            }
            st.mThickness = a.getDimensionPixelSize(7, -1);
            if (st.mThickness == -1) {
                st.mThicknessRatio = a.getFloat(4, 9.0f);
            }
            st.mUseLevelForShape = a.getBoolean(5, true);
        }
        a.recycle();
        setShape(shapeType);
        setDither(dither);
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type = parser.next();
            if (type != 1 && ((depth = parser.getDepth()) >= innerDepth || type != 3)) {
                if (type == 2 && depth <= innerDepth) {
                    String name = parser.getName();
                    if (name.equals("size")) {
                        TypedArray a2 = r.obtainAttributes(attrs, R.styleable.GradientDrawableSize);
                        int width = a2.getDimensionPixelSize(1, -1);
                        int height = a2.getDimensionPixelSize(0, -1);
                        a2.recycle();
                        setSize(width, height);
                    } else if (name.equals("gradient")) {
                        TypedArray a3 = r.obtainAttributes(attrs, R.styleable.GradientDrawableGradient);
                        int startColor = a3.getColor(0, 0);
                        boolean hasCenterColor = a3.hasValue(8);
                        int centerColor = a3.getColor(8, 0);
                        int endColor = a3.getColor(1, 0);
                        int gradientType = a3.getInt(4, 0);
                        st.mCenterX = getFloatOrFraction(a3, 5, 0.5f);
                        st.mCenterY = getFloatOrFraction(a3, 6, 0.5f);
                        st.mUseLevel = a3.getBoolean(2, false);
                        st.mGradient = gradientType;
                        if (gradientType == 0) {
                            int angle = ((int) a3.getFloat(3, 0.0f)) % MediaProperties.HEIGHT_360;
                            if (angle % 45 != 0) {
                                throw new XmlPullParserException(a3.getPositionDescription() + "<gradient> tag requires 'angle' attribute to be a multiple of 45");
                            }
                            switch (angle) {
                                case 0:
                                    st.mOrientation = Orientation.LEFT_RIGHT;
                                    break;
                                case 45:
                                    st.mOrientation = Orientation.BL_TR;
                                    break;
                                case 90:
                                    st.mOrientation = Orientation.BOTTOM_TOP;
                                    break;
                                case 135:
                                    st.mOrientation = Orientation.BR_TL;
                                    break;
                                case 180:
                                    st.mOrientation = Orientation.RIGHT_LEFT;
                                    break;
                                case 225:
                                    st.mOrientation = Orientation.TR_BL;
                                    break;
                                case R.styleable.Theme_findOnPagePreviousDrawable /* 270 */:
                                    st.mOrientation = Orientation.TOP_BOTTOM;
                                    break;
                                case 315:
                                    st.mOrientation = Orientation.TL_BR;
                                    break;
                            }
                        } else {
                            TypedValue tv = a3.peekValue(7);
                            if (tv != null) {
                                boolean radiusRel = tv.type == 6;
                                st.mGradientRadius = radiusRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
                            } else if (gradientType == 1) {
                                throw new XmlPullParserException(a3.getPositionDescription() + "<gradient> tag requires 'gradientRadius' attribute with radial type");
                            }
                        }
                        a3.recycle();
                        if (hasCenterColor) {
                            st.mColors = new int[3];
                            st.mColors[0] = startColor;
                            st.mColors[1] = centerColor;
                            st.mColors[2] = endColor;
                            st.mPositions = new float[3];
                            st.mPositions[0] = 0.0f;
                            st.mPositions[1] = st.mCenterX != 0.5f ? st.mCenterX : st.mCenterY;
                            st.mPositions[2] = 1.0f;
                        } else {
                            st.mColors = new int[2];
                            st.mColors[0] = startColor;
                            st.mColors[1] = endColor;
                        }
                    } else if (name.equals("solid")) {
                        TypedArray a4 = r.obtainAttributes(attrs, R.styleable.GradientDrawableSolid);
                        int argb = a4.getColor(0, 0);
                        a4.recycle();
                        setColor(argb);
                    } else if (name.equals("stroke")) {
                        TypedArray a5 = r.obtainAttributes(attrs, R.styleable.GradientDrawableStroke);
                        int width2 = a5.getDimensionPixelSize(0, 0);
                        int color = a5.getColor(1, 0);
                        float dashWidth = a5.getDimension(2, 0.0f);
                        if (dashWidth != 0.0f) {
                            float dashGap = a5.getDimension(3, 0.0f);
                            setStroke(width2, color, dashWidth, dashGap);
                        } else {
                            setStroke(width2, color);
                        }
                        a5.recycle();
                    } else if (name.equals("corners")) {
                        TypedArray a6 = r.obtainAttributes(attrs, R.styleable.DrawableCorners);
                        int radius = a6.getDimensionPixelSize(0, 0);
                        setCornerRadius(radius);
                        int topLeftRadius = a6.getDimensionPixelSize(1, radius);
                        int topRightRadius = a6.getDimensionPixelSize(2, radius);
                        int bottomLeftRadius = a6.getDimensionPixelSize(3, radius);
                        int bottomRightRadius = a6.getDimensionPixelSize(4, radius);
                        if (topLeftRadius != radius || topRightRadius != radius || bottomLeftRadius != radius || bottomRightRadius != radius) {
                            setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
                        }
                        a6.recycle();
                    } else if (name.equals("padding")) {
                        TypedArray a7 = r.obtainAttributes(attrs, R.styleable.GradientDrawablePadding);
                        this.mPadding = new Rect(a7.getDimensionPixelOffset(0, 0), a7.getDimensionPixelOffset(1, 0), a7.getDimensionPixelOffset(2, 0), a7.getDimensionPixelOffset(3, 0));
                        a7.recycle();
                        this.mGradientState.mPadding = this.mPadding;
                    } else {
                        Log.w("drawable", "Bad element under <shape>: " + name);
                    }
                }
            }
        }
        this.mGradientState.computeOpacity();
    }

    private static float getFloatOrFraction(TypedArray a, int index, float defaultValue) {
        TypedValue tv = a.peekValue(index);
        float v = defaultValue;
        if (tv != null) {
            boolean vIsFraction = tv.type == 6;
            v = vIsFraction ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        }
        return v;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mGradientState.mWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mGradientState.mHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mGradientState.mChangingConfigurations = getChangingConfigurations();
        return this.mGradientState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mGradientState = new GradientState(this.mGradientState);
            initializeWithState(this.mGradientState);
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: GradientDrawable$GradientState.class */
    public static final class GradientState extends Drawable.ConstantState {
        public int mChangingConfigurations;
        public int mShape;
        public int mGradient;
        public Orientation mOrientation;
        public int[] mColors;
        public int[] mTempColors;
        public float[] mTempPositions;
        public float[] mPositions;
        public boolean mHasSolidColor;
        public int mSolidColor;
        public int mStrokeWidth;
        public int mStrokeColor;
        public float mStrokeDashWidth;
        public float mStrokeDashGap;
        public float mRadius;
        public float[] mRadiusArray;
        public Rect mPadding;
        public int mWidth;
        public int mHeight;
        public float mInnerRadiusRatio;
        public float mThicknessRatio;
        public int mInnerRadius;
        public int mThickness;
        private float mCenterX;
        private float mCenterY;
        private float mGradientRadius;
        private boolean mUseLevel;
        private boolean mUseLevelForShape;
        private boolean mOpaque;

        GradientState(Orientation orientation, int[] colors) {
            this.mShape = 0;
            this.mGradient = 0;
            this.mStrokeWidth = -1;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mOrientation = orientation;
            setColors(colors);
        }

        public GradientState(GradientState state) {
            this.mShape = 0;
            this.mGradient = 0;
            this.mStrokeWidth = -1;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mChangingConfigurations = state.mChangingConfigurations;
            this.mShape = state.mShape;
            this.mGradient = state.mGradient;
            this.mOrientation = state.mOrientation;
            if (state.mColors != null) {
                this.mColors = (int[]) state.mColors.clone();
            }
            if (state.mPositions != null) {
                this.mPositions = (float[]) state.mPositions.clone();
            }
            this.mHasSolidColor = state.mHasSolidColor;
            this.mSolidColor = state.mSolidColor;
            this.mStrokeWidth = state.mStrokeWidth;
            this.mStrokeColor = state.mStrokeColor;
            this.mStrokeDashWidth = state.mStrokeDashWidth;
            this.mStrokeDashGap = state.mStrokeDashGap;
            this.mRadius = state.mRadius;
            if (state.mRadiusArray != null) {
                this.mRadiusArray = (float[]) state.mRadiusArray.clone();
            }
            if (state.mPadding != null) {
                this.mPadding = new Rect(state.mPadding);
            }
            this.mWidth = state.mWidth;
            this.mHeight = state.mHeight;
            this.mInnerRadiusRatio = state.mInnerRadiusRatio;
            this.mThicknessRatio = state.mThicknessRatio;
            this.mInnerRadius = state.mInnerRadius;
            this.mThickness = state.mThickness;
            this.mCenterX = state.mCenterX;
            this.mCenterY = state.mCenterY;
            this.mGradientRadius = state.mGradientRadius;
            this.mUseLevel = state.mUseLevel;
            this.mUseLevelForShape = state.mUseLevelForShape;
            this.mOpaque = state.mOpaque;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new GradientDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new GradientDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public void setShape(int shape) {
            this.mShape = shape;
            computeOpacity();
        }

        public void setGradientType(int gradient) {
            this.mGradient = gradient;
        }

        public void setGradientCenter(float x, float y) {
            this.mCenterX = x;
            this.mCenterY = y;
        }

        public void setColors(int[] colors) {
            this.mHasSolidColor = false;
            this.mColors = colors;
            computeOpacity();
        }

        public void setSolidColor(int argb) {
            this.mHasSolidColor = true;
            this.mSolidColor = argb;
            this.mColors = null;
            computeOpacity();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void computeOpacity() {
            if (this.mShape != 0) {
                this.mOpaque = false;
            } else if (this.mRadius > 0.0f || this.mRadiusArray != null) {
                this.mOpaque = false;
            } else if (this.mStrokeWidth > 0 && !isOpaque(this.mStrokeColor)) {
                this.mOpaque = false;
            } else if (this.mHasSolidColor) {
                this.mOpaque = isOpaque(this.mSolidColor);
            } else {
                if (this.mColors != null) {
                    for (int i = 0; i < this.mColors.length; i++) {
                        if (!isOpaque(this.mColors[i])) {
                            this.mOpaque = false;
                            return;
                        }
                    }
                }
                this.mOpaque = true;
            }
        }

        private static boolean isOpaque(int color) {
            return ((color >> 24) & 255) == 255;
        }

        public void setStroke(int width, int color) {
            this.mStrokeWidth = width;
            this.mStrokeColor = color;
            computeOpacity();
        }

        public void setStroke(int width, int color, float dashWidth, float dashGap) {
            this.mStrokeWidth = width;
            this.mStrokeColor = color;
            this.mStrokeDashWidth = dashWidth;
            this.mStrokeDashGap = dashGap;
            computeOpacity();
        }

        public void setCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mRadius = radius;
            this.mRadiusArray = null;
        }

        public void setCornerRadii(float[] radii) {
            this.mRadiusArray = radii;
            if (radii == null) {
                this.mRadius = 0.0f;
            }
        }

        public void setSize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
        }

        public void setGradientRadius(float gradientRadius) {
            this.mGradientRadius = gradientRadius;
        }
    }

    private GradientDrawable(GradientState state) {
        this.mFillPaint = new Paint(1);
        this.mAlpha = 255;
        this.mPath = new Path();
        this.mRect = new RectF();
        this.mPathIsDirty = true;
        this.mGradientState = state;
        initializeWithState(state);
        this.mRectIsDirty = true;
        this.mMutated = false;
    }

    private void initializeWithState(GradientState state) {
        if (state.mHasSolidColor) {
            this.mFillPaint.setColor(state.mSolidColor);
        } else if (state.mColors == null) {
            this.mFillPaint.setColor(0);
        } else {
            this.mFillPaint.setColor(-16777216);
        }
        this.mPadding = state.mPadding;
        if (state.mStrokeWidth >= 0) {
            this.mStrokePaint = new Paint(1);
            this.mStrokePaint.setStyle(Paint.Style.STROKE);
            this.mStrokePaint.setStrokeWidth(state.mStrokeWidth);
            this.mStrokePaint.setColor(state.mStrokeColor);
            if (state.mStrokeDashWidth != 0.0f) {
                DashPathEffect e = new DashPathEffect(new float[]{state.mStrokeDashWidth, state.mStrokeDashGap}, 0.0f);
                this.mStrokePaint.setPathEffect(e);
            }
        }
    }
}