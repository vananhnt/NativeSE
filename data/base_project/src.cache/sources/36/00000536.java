package android.graphics;

/* loaded from: Camera.class */
public class Camera {
    private Matrix mMatrix;
    int native_instance;

    public native void save();

    public native void restore();

    public native void translate(float f, float f2, float f3);

    public native void rotateX(float f);

    public native void rotateY(float f);

    public native void rotateZ(float f);

    public native void rotate(float f, float f2, float f3);

    public native float getLocationX();

    public native float getLocationY();

    public native float getLocationZ();

    public native void setLocation(float f, float f2, float f3);

    public native float dotWithNormal(float f, float f2, float f3);

    private native void nativeConstructor();

    private native void nativeDestructor();

    private native void nativeGetMatrix(int i);

    private native void nativeApplyToCanvas(int i);

    public Camera() {
        nativeConstructor();
    }

    public void getMatrix(Matrix matrix) {
        nativeGetMatrix(matrix.native_instance);
    }

    public void applyToCanvas(Canvas canvas) {
        if (canvas.isHardwareAccelerated()) {
            if (this.mMatrix == null) {
                this.mMatrix = new Matrix();
            }
            getMatrix(this.mMatrix);
            canvas.concat(this.mMatrix);
            return;
        }
        nativeApplyToCanvas(canvas.mNativeCanvas);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }
}