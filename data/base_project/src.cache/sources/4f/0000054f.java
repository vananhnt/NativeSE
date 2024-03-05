package android.graphics;

import java.io.PrintWriter;

/* loaded from: Matrix.class */
public class Matrix {
    public static final int MSCALE_X = 0;
    public static final int MSKEW_X = 1;
    public static final int MTRANS_X = 2;
    public static final int MSKEW_Y = 3;
    public static final int MSCALE_Y = 4;
    public static final int MTRANS_Y = 5;
    public static final int MPERSP_0 = 6;
    public static final int MPERSP_1 = 7;
    public static final int MPERSP_2 = 8;
    public static Matrix IDENTITY_MATRIX = new Matrix() { // from class: android.graphics.Matrix.1
        void oops() {
            throw new IllegalStateException("Matrix can not be modified");
        }

        @Override // android.graphics.Matrix
        public void set(Matrix src) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void reset() {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setTranslate(float dx, float dy) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setScale(float sx, float sy, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setScale(float sx, float sy) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setRotate(float degrees, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setRotate(float degrees) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSinCos(float sinValue, float cosValue, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSinCos(float sinValue, float cosValue) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSkew(float kx, float ky, float px, float py) {
            oops();
        }

        @Override // android.graphics.Matrix
        public void setSkew(float kx, float ky) {
            oops();
        }

        @Override // android.graphics.Matrix
        public boolean setConcat(Matrix a, Matrix b) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preTranslate(float dx, float dy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preScale(float sx, float sy, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preScale(float sx, float sy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preRotate(float degrees, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preRotate(float degrees) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preSkew(float kx, float ky, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preSkew(float kx, float ky) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean preConcat(Matrix other) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postTranslate(float dx, float dy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postScale(float sx, float sy, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postScale(float sx, float sy) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postRotate(float degrees, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postRotate(float degrees) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postSkew(float kx, float ky, float px, float py) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postSkew(float kx, float ky) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean postConcat(Matrix other) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public boolean setPolyToPoly(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) {
            oops();
            return false;
        }

        @Override // android.graphics.Matrix
        public void setValues(float[] values) {
            oops();
        }
    };
    public int native_instance;

    private static native int native_create(int i);

    private static native boolean native_isIdentity(int i);

    private static native boolean native_rectStaysRect(int i);

    private static native void native_reset(int i);

    private static native void native_set(int i, int i2);

    private static native void native_setTranslate(int i, float f, float f2);

    private static native void native_setScale(int i, float f, float f2, float f3, float f4);

    private static native void native_setScale(int i, float f, float f2);

    private static native void native_setRotate(int i, float f, float f2, float f3);

    private static native void native_setRotate(int i, float f);

    private static native void native_setSinCos(int i, float f, float f2, float f3, float f4);

    private static native void native_setSinCos(int i, float f, float f2);

    private static native void native_setSkew(int i, float f, float f2, float f3, float f4);

    private static native void native_setSkew(int i, float f, float f2);

    private static native boolean native_setConcat(int i, int i2, int i3);

    private static native boolean native_preTranslate(int i, float f, float f2);

    private static native boolean native_preScale(int i, float f, float f2, float f3, float f4);

    private static native boolean native_preScale(int i, float f, float f2);

    private static native boolean native_preRotate(int i, float f, float f2, float f3);

    private static native boolean native_preRotate(int i, float f);

    private static native boolean native_preSkew(int i, float f, float f2, float f3, float f4);

    private static native boolean native_preSkew(int i, float f, float f2);

    private static native boolean native_preConcat(int i, int i2);

    private static native boolean native_postTranslate(int i, float f, float f2);

    private static native boolean native_postScale(int i, float f, float f2, float f3, float f4);

    private static native boolean native_postScale(int i, float f, float f2);

    private static native boolean native_postRotate(int i, float f, float f2, float f3);

    private static native boolean native_postRotate(int i, float f);

    private static native boolean native_postSkew(int i, float f, float f2, float f3, float f4);

    private static native boolean native_postSkew(int i, float f, float f2);

    private static native boolean native_postConcat(int i, int i2);

    private static native boolean native_setRectToRect(int i, RectF rectF, RectF rectF2, int i2);

    private static native boolean native_setPolyToPoly(int i, float[] fArr, int i2, float[] fArr2, int i3, int i4);

    private static native boolean native_invert(int i, int i2);

    private static native void native_mapPoints(int i, float[] fArr, int i2, float[] fArr2, int i3, int i4, boolean z);

    private static native boolean native_mapRect(int i, RectF rectF, RectF rectF2);

    private static native float native_mapRadius(int i, float f);

    private static native void native_getValues(int i, float[] fArr);

    private static native void native_setValues(int i, float[] fArr);

    private static native boolean native_equals(int i, int i2);

    private static native void finalizer(int i);

    public Matrix() {
        this.native_instance = native_create(0);
    }

    public Matrix(Matrix src) {
        this.native_instance = native_create(src != null ? src.native_instance : 0);
    }

    public boolean isIdentity() {
        return native_isIdentity(this.native_instance);
    }

    public boolean rectStaysRect() {
        return native_rectStaysRect(this.native_instance);
    }

    public void set(Matrix src) {
        if (src == null) {
            reset();
        } else {
            native_set(this.native_instance, src.native_instance);
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof Matrix) {
            return native_equals(this.native_instance, ((Matrix) obj).native_instance);
        }
        return false;
    }

    public int hashCode() {
        return 44;
    }

    public void reset() {
        native_reset(this.native_instance);
    }

    public void setTranslate(float dx, float dy) {
        native_setTranslate(this.native_instance, dx, dy);
    }

    public void setScale(float sx, float sy, float px, float py) {
        native_setScale(this.native_instance, sx, sy, px, py);
    }

    public void setScale(float sx, float sy) {
        native_setScale(this.native_instance, sx, sy);
    }

    public void setRotate(float degrees, float px, float py) {
        native_setRotate(this.native_instance, degrees, px, py);
    }

    public void setRotate(float degrees) {
        native_setRotate(this.native_instance, degrees);
    }

    public void setSinCos(float sinValue, float cosValue, float px, float py) {
        native_setSinCos(this.native_instance, sinValue, cosValue, px, py);
    }

    public void setSinCos(float sinValue, float cosValue) {
        native_setSinCos(this.native_instance, sinValue, cosValue);
    }

    public void setSkew(float kx, float ky, float px, float py) {
        native_setSkew(this.native_instance, kx, ky, px, py);
    }

    public void setSkew(float kx, float ky) {
        native_setSkew(this.native_instance, kx, ky);
    }

    public boolean setConcat(Matrix a, Matrix b) {
        return native_setConcat(this.native_instance, a.native_instance, b.native_instance);
    }

    public boolean preTranslate(float dx, float dy) {
        return native_preTranslate(this.native_instance, dx, dy);
    }

    public boolean preScale(float sx, float sy, float px, float py) {
        return native_preScale(this.native_instance, sx, sy, px, py);
    }

    public boolean preScale(float sx, float sy) {
        return native_preScale(this.native_instance, sx, sy);
    }

    public boolean preRotate(float degrees, float px, float py) {
        return native_preRotate(this.native_instance, degrees, px, py);
    }

    public boolean preRotate(float degrees) {
        return native_preRotate(this.native_instance, degrees);
    }

    public boolean preSkew(float kx, float ky, float px, float py) {
        return native_preSkew(this.native_instance, kx, ky, px, py);
    }

    public boolean preSkew(float kx, float ky) {
        return native_preSkew(this.native_instance, kx, ky);
    }

    public boolean preConcat(Matrix other) {
        return native_preConcat(this.native_instance, other.native_instance);
    }

    public boolean postTranslate(float dx, float dy) {
        return native_postTranslate(this.native_instance, dx, dy);
    }

    public boolean postScale(float sx, float sy, float px, float py) {
        return native_postScale(this.native_instance, sx, sy, px, py);
    }

    public boolean postScale(float sx, float sy) {
        return native_postScale(this.native_instance, sx, sy);
    }

    public boolean postRotate(float degrees, float px, float py) {
        return native_postRotate(this.native_instance, degrees, px, py);
    }

    public boolean postRotate(float degrees) {
        return native_postRotate(this.native_instance, degrees);
    }

    public boolean postSkew(float kx, float ky, float px, float py) {
        return native_postSkew(this.native_instance, kx, ky, px, py);
    }

    public boolean postSkew(float kx, float ky) {
        return native_postSkew(this.native_instance, kx, ky);
    }

    public boolean postConcat(Matrix other) {
        return native_postConcat(this.native_instance, other.native_instance);
    }

    /* loaded from: Matrix$ScaleToFit.class */
    public enum ScaleToFit {
        FILL(0),
        START(1),
        CENTER(2),
        END(3);
        
        final int nativeInt;

        ScaleToFit(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public boolean setRectToRect(RectF src, RectF dst, ScaleToFit stf) {
        if (dst == null || src == null) {
            throw new NullPointerException();
        }
        return native_setRectToRect(this.native_instance, src, dst, stf.nativeInt);
    }

    private static void checkPointArrays(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) {
        int srcStop = srcIndex + (pointCount << 1);
        int dstStop = dstIndex + (pointCount << 1);
        if ((pointCount | srcIndex | dstIndex | srcStop | dstStop) < 0 || srcStop > src.length || dstStop > dst.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean setPolyToPoly(float[] src, int srcIndex, float[] dst, int dstIndex, int pointCount) {
        if (pointCount > 4) {
            throw new IllegalArgumentException();
        }
        checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
        return native_setPolyToPoly(this.native_instance, src, srcIndex, dst, dstIndex, pointCount);
    }

    public boolean invert(Matrix inverse) {
        return native_invert(this.native_instance, inverse.native_instance);
    }

    public void mapPoints(float[] dst, int dstIndex, float[] src, int srcIndex, int pointCount) {
        checkPointArrays(src, srcIndex, dst, dstIndex, pointCount);
        native_mapPoints(this.native_instance, dst, dstIndex, src, srcIndex, pointCount, true);
    }

    public void mapVectors(float[] dst, int dstIndex, float[] src, int srcIndex, int vectorCount) {
        checkPointArrays(src, srcIndex, dst, dstIndex, vectorCount);
        native_mapPoints(this.native_instance, dst, dstIndex, src, srcIndex, vectorCount, false);
    }

    public void mapPoints(float[] dst, float[] src) {
        if (dst.length != src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mapPoints(dst, 0, src, 0, dst.length >> 1);
    }

    public void mapVectors(float[] dst, float[] src) {
        if (dst.length != src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mapVectors(dst, 0, src, 0, dst.length >> 1);
    }

    public void mapPoints(float[] pts) {
        mapPoints(pts, 0, pts, 0, pts.length >> 1);
    }

    public void mapVectors(float[] vecs) {
        mapVectors(vecs, 0, vecs, 0, vecs.length >> 1);
    }

    public boolean mapRect(RectF dst, RectF src) {
        if (dst == null || src == null) {
            throw new NullPointerException();
        }
        return native_mapRect(this.native_instance, dst, src);
    }

    public boolean mapRect(RectF rect) {
        return mapRect(rect, rect);
    }

    public float mapRadius(float radius) {
        return native_mapRadius(this.native_instance, radius);
    }

    public void getValues(float[] values) {
        if (values.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_getValues(this.native_instance, values);
    }

    public void setValues(float[] values) {
        if (values.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_setValues(this.native_instance, values);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("Matrix{");
        toShortString(sb);
        sb.append('}');
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder(64);
        toShortString(sb);
        return sb.toString();
    }

    public void toShortString(StringBuilder sb) {
        float[] values = new float[9];
        getValues(values);
        sb.append('[');
        sb.append(values[0]);
        sb.append(", ");
        sb.append(values[1]);
        sb.append(", ");
        sb.append(values[2]);
        sb.append("][");
        sb.append(values[3]);
        sb.append(", ");
        sb.append(values[4]);
        sb.append(", ");
        sb.append(values[5]);
        sb.append("][");
        sb.append(values[6]);
        sb.append(", ");
        sb.append(values[7]);
        sb.append(", ");
        sb.append(values[8]);
        sb.append(']');
    }

    public void printShortString(PrintWriter pw) {
        float[] values = new float[9];
        getValues(values);
        pw.print('[');
        pw.print(values[0]);
        pw.print(", ");
        pw.print(values[1]);
        pw.print(", ");
        pw.print(values[2]);
        pw.print("][");
        pw.print(values[3]);
        pw.print(", ");
        pw.print(values[4]);
        pw.print(", ");
        pw.print(values[5]);
        pw.print("][");
        pw.print(values[6]);
        pw.print(", ");
        pw.print(values[7]);
        pw.print(", ");
        pw.print(values[8]);
        pw.print(']');
    }

    protected void finalize() throws Throwable {
        try {
            finalizer(this.native_instance);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int ni() {
        return this.native_instance;
    }
}