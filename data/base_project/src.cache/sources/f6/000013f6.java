package android.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spanned;

/* loaded from: DrawableMarginSpan.class */
public class DrawableMarginSpan implements LeadingMarginSpan, LineHeightSpan {
    private Drawable mDrawable;
    private int mPad;

    public DrawableMarginSpan(Drawable b) {
        this.mDrawable = b;
    }

    public DrawableMarginSpan(Drawable b, int pad) {
        this.mDrawable = b;
        this.mPad = pad;
    }

    @Override // android.text.style.LeadingMarginSpan
    public int getLeadingMargin(boolean first) {
        return this.mDrawable.getIntrinsicWidth() + this.mPad;
    }

    @Override // android.text.style.LeadingMarginSpan
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        int st = ((Spanned) text).getSpanStart(this);
        int itop = layout.getLineTop(layout.getLineForOffset(st));
        int dw = this.mDrawable.getIntrinsicWidth();
        int dh = this.mDrawable.getIntrinsicHeight();
        this.mDrawable.setBounds(x, itop, x + dw, itop + dh);
        this.mDrawable.draw(c);
    }

    @Override // android.text.style.LineHeightSpan
    public void chooseHeight(CharSequence text, int start, int end, int istartv, int v, Paint.FontMetricsInt fm) {
        if (end == ((Spanned) text).getSpanEnd(this)) {
            int ht = this.mDrawable.getIntrinsicHeight();
            int need = ht - (((v + fm.descent) - fm.ascent) - istartv);
            if (need > 0) {
                fm.descent += need;
            }
            int need2 = ht - (((v + fm.bottom) - fm.top) - istartv);
            if (need2 > 0) {
                fm.bottom += need2;
            }
        }
    }
}