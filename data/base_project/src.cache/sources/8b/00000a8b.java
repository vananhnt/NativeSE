package android.opengl;

/* loaded from: EGLConfig.class */
public class EGLConfig extends EGLObjectHandle {
    private EGLConfig(int handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLConfig) {
            EGLConfig that = (EGLConfig) o;
            return getHandle() == that.getHandle();
        }
        return false;
    }
}