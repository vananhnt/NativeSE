package android.opengl;

/* loaded from: EGLContext.class */
public class EGLContext extends EGLObjectHandle {
    private EGLContext(int handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLContext) {
            EGLContext that = (EGLContext) o;
            return getHandle() == that.getHandle();
        }
        return false;
    }
}