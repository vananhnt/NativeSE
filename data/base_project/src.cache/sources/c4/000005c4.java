package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/* loaded from: RoundRectShape.class */
public class RoundRectShape extends RectShape {
    private float[] mOuterRadii;
    private RectF mInset;
    private float[] mInnerRadii;
    private RectF mInnerRect;
    private Path mPath;

    public RoundRectShape(float[] outerRadii, RectF inset, float[] innerRadii) {
        if (outerRadii != null && outerRadii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("outer radii must have >= 8 values");
        }
        if (innerRadii != null && innerRadii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("inner radii must have >= 8 values");
        }
        this.mOuterRadii = outerRadii;
        this.mInset = inset;
        this.mInnerRadii = innerRadii;
        if (inset != null) {
            this.mInnerRect = new RectF();
        }
        this.mPath = new Path();
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(this.mPath, paint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void onResize(float w, float h) {
        super.onResize(w, h);
        RectF r = rect();
        this.mPath.reset();
        if (this.mOuterRadii != null) {
            this.mPath.addRoundRect(r, this.mOuterRadii, Path.Direction.CW);
        } else {
            this.mPath.addRect(r, Path.Direction.CW);
        }
        if (this.mInnerRect != null) {
            this.mInnerRect.set(r.left + this.mInset.left, r.top + this.mInset.top, r.right - this.mInset.right, r.bottom - this.mInset.bottom);
            if (this.mInnerRect.width() < w && this.mInnerRect.height() < h) {
                if (this.mInnerRadii != null) {
                    this.mPath.addRoundRect(this.mInnerRect, this.mInnerRadii, Path.Direction.CCW);
                } else {
                    this.mPath.addRect(this.mInnerRect, Path.Direction.CCW);
                }
            }
        }
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    /* renamed from: clone */
    public RoundRectShape mo226clone() throws CloneNotSupportedException {
        RoundRectShape shape = (RoundRectShape) super.mo226clone();
        shape.mOuterRadii = this.mOuterRadii != null ? (float[]) this.mOuterRadii.clone() : null;
        shape.mInnerRadii = this.mInnerRadii != null ? (float[]) this.mInnerRadii.clone() : null;
        shape.mInset = new RectF(this.mInset);
        shape.mInnerRect = new RectF(this.mInnerRect);
        shape.mPath = new Path(this.mPath);
        return shape;
    }
}