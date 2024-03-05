package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.FloatMath;
import gov.nist.core.Separators;

/* loaded from: PointF.class */
public class PointF implements Parcelable {
    public float x;
    public float y;
    public static final Parcelable.Creator<PointF> CREATOR = new Parcelable.Creator<PointF>() { // from class: android.graphics.PointF.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PointF createFromParcel(Parcel in) {
            PointF r = new PointF();
            r.readFromParcel(in);
            return r;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PointF[] newArray(int size) {
            return new PointF[size];
        }
    };

    public PointF() {
    }

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public final void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final void set(PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public final void offset(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public final boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointF pointF = (PointF) o;
        return Float.compare(pointF.x, this.x) == 0 && Float.compare(pointF.y, this.y) == 0;
    }

    public int hashCode() {
        int result = this.x != 0.0f ? Float.floatToIntBits(this.x) : 0;
        return (31 * result) + (this.y != 0.0f ? Float.floatToIntBits(this.y) : 0);
    }

    public String toString() {
        return "PointF(" + this.x + ", " + this.y + Separators.RPAREN;
    }

    public final float length() {
        return length(this.x, this.y);
    }

    public static float length(float x, float y) {
        return FloatMath.sqrt((x * x) + (y * y));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
    }

    public void readFromParcel(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();
    }
}