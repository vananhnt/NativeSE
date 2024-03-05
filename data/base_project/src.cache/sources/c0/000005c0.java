package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

/* loaded from: ArcShape.class */
public class ArcShape extends RectShape {
    private float mStart;
    private float mSweep;

    public ArcShape(float startAngle, float sweepAngle) {
        this.mStart = startAngle;
        this.mSweep = sweepAngle;
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawArc(rect(), this.mStart, this.mSweep, true, paint);
    }
}