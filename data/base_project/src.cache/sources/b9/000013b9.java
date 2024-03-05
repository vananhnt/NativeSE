package android.text;

import android.graphics.Paint;

/* loaded from: TextPaint.class */
public class TextPaint extends Paint {
    public int bgColor;
    public int baselineShift;
    public int linkColor;
    public int[] drawableState;
    public float density;
    public int underlineColor;
    public float underlineThickness;

    public TextPaint() {
        this.density = 1.0f;
        this.underlineColor = 0;
    }

    public TextPaint(int flags) {
        super(flags);
        this.density = 1.0f;
        this.underlineColor = 0;
    }

    public TextPaint(Paint p) {
        super(p);
        this.density = 1.0f;
        this.underlineColor = 0;
    }

    public void set(TextPaint tp) {
        super.set((Paint) tp);
        this.bgColor = tp.bgColor;
        this.baselineShift = tp.baselineShift;
        this.linkColor = tp.linkColor;
        this.drawableState = tp.drawableState;
        this.density = tp.density;
        this.underlineColor = tp.underlineColor;
        this.underlineThickness = tp.underlineThickness;
    }

    public void setUnderlineText(int color, float thickness) {
        this.underlineColor = color;
        this.underlineThickness = thickness;
    }
}