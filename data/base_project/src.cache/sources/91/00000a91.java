package android.opengl;

/* loaded from: EGLSurface.class */
public class EGLSurface extends EGLObjectHandle {
    private EGLSurface(int handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLSurface) {
            EGLSurface that = (EGLSurface) o;
            return getHandle() == that.getHandle();
        }
        return false;
    }
}