package android.opengl;

/* loaded from: EGLDisplay.class */
public class EGLDisplay extends EGLObjectHandle {
    private EGLDisplay(int handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLDisplay) {
            EGLDisplay that = (EGLDisplay) o;
            return getHandle() == that.getHandle();
        }
        return false;
    }
}