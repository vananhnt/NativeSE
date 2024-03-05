package android.hardware.camera2;

import android.widget.ExpandableListView;

/* loaded from: Size.class */
public final class Size {
    private final int mWidth;
    private final int mHeight;

    public Size(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public final int getHeight() {
        return this.mHeight;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Size) {
            Size other = (Size) obj;
            return this.mWidth == other.mWidth && this.mHeight == other.mHeight;
        }
        return false;
    }

    public String toString() {
        return this.mWidth + "x" + this.mHeight;
    }

    public int hashCode() {
        long asLong = ExpandableListView.PACKED_POSITION_VALUE_NULL & this.mWidth;
        return Long.valueOf((asLong << 32) | (ExpandableListView.PACKED_POSITION_VALUE_NULL & this.mHeight)).hashCode();
    }
}