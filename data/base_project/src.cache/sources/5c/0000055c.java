package android.graphics;

import android.graphics.Region;

/* loaded from: Path.class */
public class Path {
    public final int mNativePath;
    public boolean isSimplePath;
    public Region rects;
    private boolean mDetectSimplePaths;
    private Direction mLastDirection;
    static final FillType[] sFillTypeArray = {FillType.WINDING, FillType.EVEN_ODD, FillType.INVERSE_WINDING, FillType.INVERSE_EVEN_ODD};

    /* loaded from: Path$Op.class */
    public enum Op {
        DIFFERENCE,
        INTERSECT,
        UNION,
        XOR,
        REVERSE_DIFFERENCE
    }

    private static native int init1();

    private static native int init2(int i);

    private static native void native_reset(int i);

    private static native void native_rewind(int i);

    private static native void native_set(int i, int i2);

    private static native int native_getFillType(int i);

    private static native void native_setFillType(int i, int i2);

    private static native boolean native_isEmpty(int i);

    private static native boolean native_isRect(int i, RectF rectF);

    private static native void native_computeBounds(int i, RectF rectF);

    private static native void native_incReserve(int i, int i2);

    private static native void native_moveTo(int i, float f, float f2);

    private static native void native_rMoveTo(int i, float f, float f2);

    private static native void native_lineTo(int i, float f, float f2);

    private static native void native_rLineTo(int i, float f, float f2);

    private static native void native_quadTo(int i, float f, float f2, float f3, float f4);

    private static native void native_rQuadTo(int i, float f, float f2, float f3, float f4);

    private static native void native_cubicTo(int i, float f, float f2, float f3, float f4, float f5, float f6);

    private static native void native_rCubicTo(int i, float f, float f2, float f3, float f4, float f5, float f6);

    private static native void native_arcTo(int i, RectF rectF, float f, float f2, boolean z);

    private static native void native_close(int i);

    private static native void native_addRect(int i, RectF rectF, int i2);

    private static native void native_addRect(int i, float f, float f2, float f3, float f4, int i2);

    private static native void native_addOval(int i, RectF rectF, int i2);

    private static native void native_addCircle(int i, float f, float f2, float f3, int i2);

    private static native void native_addArc(int i, RectF rectF, float f, float f2);

    private static native void native_addRoundRect(int i, RectF rectF, float f, float f2, int i2);

    private static native void native_addRoundRect(int i, RectF rectF, float[] fArr, int i2);

    private static native void native_addPath(int i, int i2, float f, float f2);

    private static native void native_addPath(int i, int i2);

    private static native void native_addPath(int i, int i2, int i3);

    private static native void native_offset(int i, float f, float f2, int i2);

    private static native void native_offset(int i, float f, float f2);

    private static native void native_setLastPoint(int i, float f, float f2);

    private static native void native_transform(int i, int i2, int i3);

    private static native void native_transform(int i, int i2);

    private static native boolean native_op(int i, int i2, int i3, int i4);

    private static native void finalizer(int i);

    public Path() {
        this.isSimplePath = true;
        this.mLastDirection = null;
        this.mNativePath = init1();
        this.mDetectSimplePaths = android.view.HardwareRenderer.isAvailable();
    }

    public Path(Path src) {
        this.isSimplePath = true;
        this.mLastDirection = null;
        int valNative = 0;
        if (src != null) {
            valNative = src.mNativePath;
            this.isSimplePath = src.isSimplePath;
            if (src.rects != null) {
                this.rects = new Region(src.rects);
            }
        }
        this.mNativePath = init2(valNative);
        this.mDetectSimplePaths = android.view.HardwareRenderer.isAvailable();
    }

    public void reset() {
        this.isSimplePath = true;
        if (this.mDetectSimplePaths) {
            this.mLastDirection = null;
            if (this.rects != null) {
                this.rects.setEmpty();
            }
        }
        FillType fillType = getFillType();
        native_reset(this.mNativePath);
        setFillType(fillType);
    }

    public void rewind() {
        this.isSimplePath = true;
        if (this.mDetectSimplePaths) {
            this.mLastDirection = null;
            if (this.rects != null) {
                this.rects.setEmpty();
            }
        }
        native_rewind(this.mNativePath);
    }

    public void set(Path src) {
        if (this != src) {
            this.isSimplePath = src.isSimplePath;
            native_set(this.mNativePath, src.mNativePath);
        }
    }

    public boolean op(Path path, Op op) {
        return op(this, path, op);
    }

    public boolean op(Path path1, Path path2, Op op) {
        if (native_op(path1.mNativePath, path2.mNativePath, op.ordinal(), this.mNativePath)) {
            this.isSimplePath = false;
            this.rects = null;
            return true;
        }
        return false;
    }

    /* loaded from: Path$FillType.class */
    public enum FillType {
        WINDING(0),
        EVEN_ODD(1),
        INVERSE_WINDING(2),
        INVERSE_EVEN_ODD(3);
        
        final int nativeInt;

        FillType(int ni) {
            this.nativeInt = ni;
        }
    }

    public FillType getFillType() {
        return sFillTypeArray[native_getFillType(this.mNativePath)];
    }

    public void setFillType(FillType ft) {
        native_setFillType(this.mNativePath, ft.nativeInt);
    }

    public boolean isInverseFillType() {
        int ft = native_getFillType(this.mNativePath);
        return (ft & 2) != 0;
    }

    public void toggleInverseFillType() {
        int ft = native_getFillType(this.mNativePath);
        native_setFillType(this.mNativePath, ft ^ 2);
    }

    public boolean isEmpty() {
        return native_isEmpty(this.mNativePath);
    }

    public boolean isRect(RectF rect) {
        return native_isRect(this.mNativePath, rect);
    }

    public void computeBounds(RectF bounds, boolean exact) {
        native_computeBounds(this.mNativePath, bounds);
    }

    public void incReserve(int extraPtCount) {
        native_incReserve(this.mNativePath, extraPtCount);
    }

    public void moveTo(float x, float y) {
        native_moveTo(this.mNativePath, x, y);
    }

    public void rMoveTo(float dx, float dy) {
        native_rMoveTo(this.mNativePath, dx, dy);
    }

    public void lineTo(float x, float y) {
        this.isSimplePath = false;
        native_lineTo(this.mNativePath, x, y);
    }

    public void rLineTo(float dx, float dy) {
        this.isSimplePath = false;
        native_rLineTo(this.mNativePath, dx, dy);
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        this.isSimplePath = false;
        native_quadTo(this.mNativePath, x1, y1, x2, y2);
    }

    public void rQuadTo(float dx1, float dy1, float dx2, float dy2) {
        this.isSimplePath = false;
        native_rQuadTo(this.mNativePath, dx1, dy1, dx2, dy2);
    }

    public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.isSimplePath = false;
        native_cubicTo(this.mNativePath, x1, y1, x2, y2, x3, y3);
    }

    public void rCubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.isSimplePath = false;
        native_rCubicTo(this.mNativePath, x1, y1, x2, y2, x3, y3);
    }

    public void arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {
        this.isSimplePath = false;
        native_arcTo(this.mNativePath, oval, startAngle, sweepAngle, forceMoveTo);
    }

    public void arcTo(RectF oval, float startAngle, float sweepAngle) {
        this.isSimplePath = false;
        native_arcTo(this.mNativePath, oval, startAngle, sweepAngle, false);
    }

    public void close() {
        this.isSimplePath = false;
        native_close(this.mNativePath);
    }

    /* loaded from: Path$Direction.class */
    public enum Direction {
        CW(1),
        CCW(2);
        
        final int nativeInt;

        Direction(int ni) {
            this.nativeInt = ni;
        }
    }

    private void detectSimplePath(float left, float top, float right, float bottom, Direction dir) {
        if (this.mDetectSimplePaths) {
            if (this.mLastDirection == null) {
                this.mLastDirection = dir;
            }
            if (this.mLastDirection != dir) {
                this.isSimplePath = false;
                return;
            }
            if (this.rects == null) {
                this.rects = new Region();
            }
            this.rects.op((int) left, (int) top, (int) right, (int) bottom, Region.Op.UNION);
        }
    }

    public void addRect(RectF rect, Direction dir) {
        if (rect == null) {
            throw new NullPointerException("need rect parameter");
        }
        detectSimplePath(rect.left, rect.top, rect.right, rect.bottom, dir);
        native_addRect(this.mNativePath, rect, dir.nativeInt);
    }

    public void addRect(float left, float top, float right, float bottom, Direction dir) {
        detectSimplePath(left, top, right, bottom, dir);
        native_addRect(this.mNativePath, left, top, right, bottom, dir.nativeInt);
    }

    public void addOval(RectF oval, Direction dir) {
        if (oval == null) {
            throw new NullPointerException("need oval parameter");
        }
        this.isSimplePath = false;
        native_addOval(this.mNativePath, oval, dir.nativeInt);
    }

    public void addCircle(float x, float y, float radius, Direction dir) {
        this.isSimplePath = false;
        native_addCircle(this.mNativePath, x, y, radius, dir.nativeInt);
    }

    public void addArc(RectF oval, float startAngle, float sweepAngle) {
        if (oval == null) {
            throw new NullPointerException("need oval parameter");
        }
        this.isSimplePath = false;
        native_addArc(this.mNativePath, oval, startAngle, sweepAngle);
    }

    public void addRoundRect(RectF rect, float rx, float ry, Direction dir) {
        if (rect == null) {
            throw new NullPointerException("need rect parameter");
        }
        this.isSimplePath = false;
        native_addRoundRect(this.mNativePath, rect, rx, ry, dir.nativeInt);
    }

    public void addRoundRect(RectF rect, float[] radii, Direction dir) {
        if (rect == null) {
            throw new NullPointerException("need rect parameter");
        }
        if (radii.length < 8) {
            throw new ArrayIndexOutOfBoundsException("radii[] needs 8 values");
        }
        this.isSimplePath = false;
        native_addRoundRect(this.mNativePath, rect, radii, dir.nativeInt);
    }

    public void addPath(Path src, float dx, float dy) {
        this.isSimplePath = false;
        native_addPath(this.mNativePath, src.mNativePath, dx, dy);
    }

    public void addPath(Path src) {
        this.isSimplePath = false;
        native_addPath(this.mNativePath, src.mNativePath);
    }

    public void addPath(Path src, Matrix matrix) {
        if (!src.isSimplePath) {
            this.isSimplePath = false;
        }
        native_addPath(this.mNativePath, src.mNativePath, matrix.native_instance);
    }

    public void offset(float dx, float dy, Path dst) {
        int dstNative = 0;
        if (dst != null) {
            dstNative = dst.mNativePath;
            dst.isSimplePath = false;
        }
        native_offset(this.mNativePath, dx, dy, dstNative);
    }

    public void offset(float dx, float dy) {
        this.isSimplePath = false;
        native_offset(this.mNativePath, dx, dy);
    }

    public void setLastPoint(float dx, float dy) {
        this.isSimplePath = false;
        native_setLastPoint(this.mNativePath, dx, dy);
    }

    public void transform(Matrix matrix, Path dst) {
        int dstNative = 0;
        if (dst != null) {
            dst.isSimplePath = false;
            dstNative = dst.mNativePath;
        }
        native_transform(this.mNativePath, matrix.native_instance, dstNative);
    }

    public void transform(Matrix matrix) {
        this.isSimplePath = false;
        native_transform(this.mNativePath, matrix.native_instance);
    }

    protected void finalize() throws Throwable {
        try {
            finalizer(this.mNativePath);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int ni() {
        return this.mNativePath;
    }
}