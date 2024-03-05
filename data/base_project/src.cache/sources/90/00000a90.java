package android.opengl;

/* loaded from: EGLObjectHandle.class */
public abstract class EGLObjectHandle {
    private final int mHandle;

    /* JADX INFO: Access modifiers changed from: protected */
    public EGLObjectHandle(int handle) {
        this.mHandle = handle;
    }

    public int getHandle() {
        return this.mHandle;
    }

    public int hashCode() {
        return getHandle();
    }
}