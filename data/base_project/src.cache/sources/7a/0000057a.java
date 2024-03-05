package android.graphics;

/* loaded from: Shader.class */
public class Shader {
    public int native_instance;
    public int native_shader;
    private Matrix mLocalMatrix;

    private static native void nativeDestructor(int i, int i2);

    private static native void nativeSetLocalMatrix(int i, int i2, int i3);

    /* loaded from: Shader$TileMode.class */
    public enum TileMode {
        CLAMP(0),
        REPEAT(1),
        MIRROR(2);
        
        final int nativeInt;

        TileMode(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public boolean getLocalMatrix(Matrix localM) {
        if (this.mLocalMatrix != null) {
            localM.set(this.mLocalMatrix);
            return !this.mLocalMatrix.isIdentity();
        }
        return false;
    }

    public void setLocalMatrix(Matrix localM) {
        this.mLocalMatrix = localM;
        nativeSetLocalMatrix(this.native_instance, this.native_shader, localM == null ? 0 : localM.native_instance);
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
            nativeDestructor(this.native_instance, this.native_shader);
        } catch (Throwable th) {
            nativeDestructor(this.native_instance, this.native_shader);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Shader copy() {
        Shader copy = new Shader();
        copyLocalMatrix(copy);
        return copy;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyLocalMatrix(Shader dest) {
        if (this.mLocalMatrix != null) {
            Matrix lm = new Matrix();
            getLocalMatrix(lm);
            dest.setLocalMatrix(lm);
            return;
        }
        dest.setLocalMatrix(null);
    }
}