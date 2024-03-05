package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/* loaded from: HardwareCanvas.class */
public abstract class HardwareCanvas extends Canvas {
    private String mName;

    public abstract int onPreDraw(Rect rect);

    public abstract void onPostDraw();

    public abstract int drawDisplayList(DisplayList displayList, Rect rect, int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void outputDisplayList(DisplayList displayList);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void drawHardwareLayer(HardwareLayer hardwareLayer, float f, float f2, Paint paint);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void detachFunctor(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void attachFunctor(int i);

    abstract void pushLayerUpdate(HardwareLayer hardwareLayer);

    abstract void cancelLayerUpdate(HardwareLayer hardwareLayer);

    abstract void flushLayerUpdates();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void clearLayerUpdates();

    @Override // android.graphics.Canvas
    public boolean isHardwareAccelerated() {
        return true;
    }

    @Override // android.graphics.Canvas
    public void setBitmap(Bitmap bitmap) {
        throw new UnsupportedOperationException();
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    public void drawDisplayList(DisplayList displayList) {
        drawDisplayList(displayList, null, 1);
    }

    public int callDrawGLFunction(int drawGLFunction) {
        return 0;
    }

    public int invokeFunctors(Rect dirty) {
        return 0;
    }
}