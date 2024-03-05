package android.graphics;

/* loaded from: PathMeasure.class */
public class PathMeasure {
    private Path mPath;
    public static final int POSITION_MATRIX_FLAG = 1;
    public static final int TANGENT_MATRIX_FLAG = 2;
    private final int native_instance;

    private static native int native_create(int i, boolean z);

    private static native void native_setPath(int i, int i2, boolean z);

    private static native float native_getLength(int i);

    private static native boolean native_getPosTan(int i, float f, float[] fArr, float[] fArr2);

    private static native boolean native_getMatrix(int i, float f, int i2, int i3);

    private static native boolean native_getSegment(int i, float f, float f2, int i2, boolean z);

    private static native boolean native_isClosed(int i);

    private static native boolean native_nextContour(int i);

    private static native void native_destroy(int i);

    public PathMeasure() {
        this.mPath = null;
        this.native_instance = native_create(0, false);
    }

    public PathMeasure(Path path, boolean forceClosed) {
        this.mPath = path;
        this.native_instance = native_create(path != null ? path.ni() : 0, forceClosed);
    }

    public void setPath(Path path, boolean forceClosed) {
        this.mPath = path;
        native_setPath(this.native_instance, path != null ? path.ni() : 0, forceClosed);
    }

    public float getLength() {
        return native_getLength(this.native_instance);
    }

    public boolean getPosTan(float distance, float[] pos, float[] tan) {
        if ((pos != null && pos.length < 2) || (tan != null && tan.length < 2)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return native_getPosTan(this.native_instance, distance, pos, tan);
    }

    public boolean getMatrix(float distance, Matrix matrix, int flags) {
        return native_getMatrix(this.native_instance, distance, matrix.native_instance, flags);
    }

    public boolean getSegment(float startD, float stopD, Path dst, boolean startWithMoveTo) {
        return native_getSegment(this.native_instance, startD, stopD, dst.ni(), startWithMoveTo);
    }

    public boolean isClosed() {
        return native_isClosed(this.native_instance);
    }

    public boolean nextContour() {
        return native_nextContour(this.native_instance);
    }

    protected void finalize() throws Throwable {
        native_destroy(this.native_instance);
    }
}