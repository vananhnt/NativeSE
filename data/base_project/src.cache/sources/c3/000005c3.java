package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/* loaded from: RectShape.class */
public class RectShape extends Shape {
    private RectF mRect = new RectF();

    @Override // android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(this.mRect, paint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.shapes.Shape
    public void onResize(float width, float height) {
        this.mRect.set(0.0f, 0.0f, width, height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RectF rect() {
        return this.mRect;
    }

    @Override // android.graphics.drawable.shapes.Shape
    /* renamed from: clone */
    public RectShape mo226clone() throws CloneNotSupportedException {
        RectShape shape = (RectShape) super.mo226clone();
        shape.mRect = new RectF(this.mRect);
        return shape;
    }
}