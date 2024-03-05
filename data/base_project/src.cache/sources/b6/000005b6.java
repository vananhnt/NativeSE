package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: ShapeDrawable.class */
public class ShapeDrawable extends Drawable {
    private ShapeState mShapeState;
    private boolean mMutated;

    /* loaded from: ShapeDrawable$ShaderFactory.class */
    public static abstract class ShaderFactory {
        public abstract Shader resize(int i, int i2);
    }

    public ShapeDrawable() {
        this((ShapeState) null);
    }

    public ShapeDrawable(Shape s) {
        this((ShapeState) null);
        this.mShapeState.mShape = s;
    }

    private ShapeDrawable(ShapeState state) {
        this.mShapeState = new ShapeState(state);
    }

    public Shape getShape() {
        return this.mShapeState.mShape;
    }

    public void setShape(Shape s) {
        this.mShapeState.mShape = s;
        updateShape();
    }

    public void setShaderFactory(ShaderFactory fact) {
        this.mShapeState.mShaderFactory = fact;
    }

    public ShaderFactory getShaderFactory() {
        return this.mShapeState.mShaderFactory;
    }

    public Paint getPaint() {
        return this.mShapeState.mPaint;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        if ((left | top | right | bottom) == 0) {
            this.mShapeState.mPadding = null;
        } else {
            if (this.mShapeState.mPadding == null) {
                this.mShapeState.mPadding = new Rect();
            }
            this.mShapeState.mPadding.set(left, top, right, bottom);
        }
        invalidateSelf();
    }

    public void setPadding(Rect padding) {
        if (padding == null) {
            this.mShapeState.mPadding = null;
        } else {
            if (this.mShapeState.mPadding == null) {
                this.mShapeState.mPadding = new Rect();
            }
            this.mShapeState.mPadding.set(padding);
        }
        invalidateSelf();
    }

    public void setIntrinsicWidth(int width) {
        this.mShapeState.mIntrinsicWidth = width;
        invalidateSelf();
    }

    public void setIntrinsicHeight(int height) {
        this.mShapeState.mIntrinsicHeight = height;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mShapeState.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mShapeState.mIntrinsicHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        if (this.mShapeState.mPadding != null) {
            padding.set(this.mShapeState.mPadding);
            return true;
        }
        return super.getPadding(padding);
    }

    private static int modulateAlpha(int paintAlpha, int alpha) {
        int scale = alpha + (alpha >>> 7);
        return (paintAlpha * scale) >>> 8;
    }

    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        shape.draw(canvas, paint);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect r = getBounds();
        Paint paint = this.mShapeState.mPaint;
        int prevAlpha = paint.getAlpha();
        paint.setAlpha(modulateAlpha(prevAlpha, this.mShapeState.mAlpha));
        if (paint.getAlpha() != 0 || paint.getXfermode() != null || paint.hasShadow) {
            if (this.mShapeState.mShape != null) {
                int count = canvas.save();
                canvas.translate(r.left, r.top);
                onDraw(this.mShapeState.mShape, canvas, paint);
                canvas.restoreToCount(count);
            } else {
                canvas.drawRect(r, paint);
            }
        }
        paint.setAlpha(prevAlpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mShapeState.mChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mShapeState.mAlpha = alpha;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mShapeState.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mShapeState.mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (this.mShapeState.mShape == null) {
            Paint p = this.mShapeState.mPaint;
            if (p.getXfermode() == null) {
                int alpha = p.getAlpha();
                if (alpha == 0) {
                    return -2;
                }
                if (alpha == 255) {
                    return -1;
                }
                return -3;
            }
            return -3;
        }
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        this.mShapeState.mPaint.setDither(dither);
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateShape();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean inflateTag(String name, Resources r, XmlPullParser parser, AttributeSet attrs) {
        if ("padding".equals(name)) {
            TypedArray a = r.obtainAttributes(attrs, R.styleable.ShapeDrawablePadding);
            setPadding(a.getDimensionPixelOffset(0, 0), a.getDimensionPixelOffset(1, 0), a.getDimensionPixelOffset(2, 0), a.getDimensionPixelOffset(3, 0));
            a.recycle();
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        TypedArray a = r.obtainAttributes(attrs, R.styleable.ShapeDrawable);
        int color = this.mShapeState.mPaint.getColor();
        this.mShapeState.mPaint.setColor(a.getColor(3, color));
        boolean dither = a.getBoolean(0, false);
        this.mShapeState.mPaint.setDither(dither);
        setIntrinsicWidth((int) a.getDimension(2, 0.0f));
        setIntrinsicHeight((int) a.getDimension(1, 0.0f));
        a.recycle();
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type == 2) {
                    String name = parser.getName();
                    if (!inflateTag(name, r, parser, attrs)) {
                        Log.w("drawable", "Unknown element: " + name + " for ShapeDrawable " + this);
                    }
                }
            } else {
                return;
            }
        }
    }

    private void updateShape() {
        if (this.mShapeState.mShape != null) {
            Rect r = getBounds();
            int w = r.width();
            int h = r.height();
            this.mShapeState.mShape.resize(w, h);
            if (this.mShapeState.mShaderFactory != null) {
                this.mShapeState.mPaint.setShader(this.mShapeState.mShaderFactory.resize(w, h));
            }
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mShapeState.mChangingConfigurations = getChangingConfigurations();
        return this.mShapeState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            if (this.mShapeState.mPaint != null) {
                this.mShapeState.mPaint = new Paint(this.mShapeState.mPaint);
            } else {
                this.mShapeState.mPaint = new Paint(1);
            }
            if (this.mShapeState.mPadding != null) {
                this.mShapeState.mPadding = new Rect(this.mShapeState.mPadding);
            } else {
                this.mShapeState.mPadding = new Rect();
            }
            try {
                this.mShapeState.mShape = this.mShapeState.mShape.mo226clone();
                this.mMutated = true;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ShapeDrawable$ShapeState.class */
    public static final class ShapeState extends Drawable.ConstantState {
        int mChangingConfigurations;
        Paint mPaint;
        Shape mShape;
        Rect mPadding;
        int mIntrinsicWidth;
        int mIntrinsicHeight;
        int mAlpha;
        ShaderFactory mShaderFactory;

        ShapeState(ShapeState orig) {
            this.mAlpha = 255;
            if (orig != null) {
                this.mPaint = orig.mPaint;
                this.mShape = orig.mShape;
                this.mPadding = orig.mPadding;
                this.mIntrinsicWidth = orig.mIntrinsicWidth;
                this.mIntrinsicHeight = orig.mIntrinsicHeight;
                this.mAlpha = orig.mAlpha;
                this.mShaderFactory = orig.mShaderFactory;
                return;
            }
            this.mPaint = new Paint(1);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ShapeDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new ShapeDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }
}