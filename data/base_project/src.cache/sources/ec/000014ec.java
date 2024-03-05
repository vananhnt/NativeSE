package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: HardwareLayer.class */
public abstract class HardwareLayer {
    static final int DIMENSION_UNDEFINED = -1;
    int mWidth;
    int mHeight;
    DisplayList mDisplayList;
    boolean mOpaque;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setOpaque(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean isValid();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean resize(int i, int i2);

    abstract HardwareCanvas getCanvas();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void destroy();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract HardwareCanvas start(Canvas canvas);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract HardwareCanvas start(Canvas canvas, Rect rect);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void end(Canvas canvas);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean copyInto(Bitmap bitmap);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setTransform(Matrix matrix);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void redrawLater(DisplayList displayList, Rect rect);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void clearStorage();

    /* JADX INFO: Access modifiers changed from: package-private */
    public HardwareLayer() {
        this(-1, -1, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HardwareLayer(int width, int height, boolean isOpaque) {
        this.mWidth = width;
        this.mHeight = height;
        this.mOpaque = isOpaque;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLayerPaint(Paint paint) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWidth() {
        return this.mWidth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHeight() {
        return this.mHeight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayList getDisplayList() {
        return this.mDisplayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDisplayList(DisplayList displayList) {
        this.mDisplayList = displayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOpaque() {
        return this.mOpaque;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void update(int width, int height, boolean isOpaque) {
        this.mWidth = width;
        this.mHeight = height;
        this.mOpaque = isOpaque;
    }
}