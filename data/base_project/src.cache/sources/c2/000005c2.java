package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/* loaded from: PathShape.class */
public class PathShape extends Shape {
    private Path mPath;
    private float mStdWidth;
    private float mStdHeight;
    private float mScaleX;
    private float mScaleY;

    public PathShape(Path path, float stdWidth, float stdHeight) {
        this.mPath = path;
        this.mStdWidth = stdWidth;
        this.mStdHeight = stdHeight;
    }

    @Override // android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY);
        canvas.drawPath(this.mPath, paint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.shapes.Shape
    protected void onResize(float width, float height) {
        this.mScaleX = width / this.mStdWidth;
        this.mScaleY = height / this.mStdHeight;
    }

    @Override // android.graphics.drawable.shapes.Shape
    /* renamed from: clone */
    public PathShape mo226clone() throws CloneNotSupportedException {
        PathShape shape = (PathShape) super.mo226clone();
        shape.mPath = new Path(this.mPath);
        return shape;
    }
}