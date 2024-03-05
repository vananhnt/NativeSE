package android.content;

/* loaded from: MutableContextWrapper.class */
public class MutableContextWrapper extends ContextWrapper {
    public MutableContextWrapper(Context base) {
        super(base);
    }

    public void setBaseContext(Context base) {
        this.mBase = base;
    }
}