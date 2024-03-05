package android.util;

/* loaded from: FloatProperty.class */
public abstract class FloatProperty<T> extends Property<T, Float> {
    public abstract void setValue(T t, float f);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.util.Property
    public /* bridge */ /* synthetic */ void set(Object x0, Float f) {
        set2((FloatProperty<T>) x0, f);
    }

    public FloatProperty(String name) {
        super(Float.class, name);
    }

    /* renamed from: set  reason: avoid collision after fix types in other method */
    public final void set2(T object, Float value) {
        setValue(object, value.floatValue());
    }
}