package android.media.effect;

/* loaded from: Effect.class */
public abstract class Effect {
    public abstract String getName();

    public abstract void apply(int i, int i2, int i3, int i4);

    public abstract void setParameter(String str, Object obj);

    public abstract void release();

    public void setUpdateListener(EffectUpdateListener listener) {
    }
}