package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;

/* loaded from: GraphicsOperations.class */
public interface GraphicsOperations extends CharSequence {
    void drawText(Canvas canvas, int i, int i2, float f, float f2, Paint paint);

    void drawTextRun(Canvas canvas, int i, int i2, int i3, int i4, float f, float f2, int i5, Paint paint);

    float measureText(int i, int i2, Paint paint);

    int getTextWidths(int i, int i2, float[] fArr, Paint paint);

    float getTextRunAdvances(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6, Paint paint);

    int getTextRunCursor(int i, int i2, int i3, int i4, int i5, Paint paint);
}