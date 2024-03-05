package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;

/* loaded from: ForegroundColorSpan.class */
public class ForegroundColorSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan {
    private final int mColor;

    public ForegroundColorSpan(int color) {
        this.mColor = color;
    }

    public ForegroundColorSpan(Parcel src) {
        this.mColor = src.readInt();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mColor);
    }

    public int getForegroundColor() {
        return this.mColor;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setColor(this.mColor);
    }
}