package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

/* loaded from: OvalShape.class */
public class OvalShape extends RectShape {
    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawOval(rect(), paint);
    }
}