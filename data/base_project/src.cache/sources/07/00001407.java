package android.text.style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;

/* loaded from: QuoteSpan.class */
public class QuoteSpan implements LeadingMarginSpan, ParcelableSpan {
    private static final int STRIPE_WIDTH = 2;
    private static final int GAP_WIDTH = 2;
    private final int mColor;

    public QuoteSpan() {
        this.mColor = Color.BLUE;
    }

    public QuoteSpan(int color) {
        this.mColor = color;
    }

    public QuoteSpan(Parcel src) {
        this.mColor = src.readInt();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 9;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mColor);
    }

    public int getColor() {
        return this.mColor;
    }

    @Override // android.text.style.LeadingMarginSpan
    public int getLeadingMargin(boolean first) {
        return 4;
    }

    @Override // android.text.style.LeadingMarginSpan
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();
        p.setStyle(Paint.Style.FILL);
        p.setColor(this.mColor);
        c.drawRect(x, top, x + (dir * 2), bottom, p);
        p.setStyle(style);
        p.setColor(color);
    }
}