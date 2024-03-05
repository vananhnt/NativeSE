package sun.misc;

import dalvik.system.VMStack;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/* loaded from: Unsafe.class */
public final class Unsafe {
    private static final Unsafe THE_ONE = new Unsafe();
    private static final Unsafe theUnsafe = THE_ONE;

    private static native long objectFieldOffset0(Field field);

    private static native int arrayBaseOffset0(Class cls);

    private static native int arrayIndexScale0(Class cls);

    public native boolean compareAndSwapInt(Object obj, long j, int i, int i2);

    public native boolean compareAndSwapLong(Object obj, long j, long j2, long j3);

    public native boolean compareAndSwapObject(Object obj, long j, Object obj2, Object obj3);

    public native int getIntVolatile(Object obj, long j);

    public native void putIntVolatile(Object obj, long j, int i);

    public native long getLongVolatile(Object obj, long j);

    public native void putLongVolatile(Object obj, long j, long j2);

    public native Object getObjectVolatile(Object obj, long j);

    public native void putObjectVolatile(Object obj, long j, Object obj2);

    public native int getInt(Object obj, long j);

    public native void putInt(Object obj, long j, int i);

    public native void putOrderedInt(Object obj, long j, int i);

    public native long getLong(Object obj, long j);

    public native void putLong(Object obj, long j, long j2);

    public native void putOrderedLong(Object obj, long j, long j2);

    public native Object getObject(Object obj, long j);

    public native void putObject(Object obj, long j, Object obj2);

    public native void putOrderedObject(Object obj, long j, Object obj2);

    public native Object allocateInstance(Class<?> cls);

    private Unsafe() {
    }

    public static Unsafe getUnsafe() {
        ClassLoader calling = VMStack.getCallingClassLoader();
        if (calling != null && calling != Unsafe.class.getClassLoader()) {
            throw new SecurityException("Unsafe access denied");
        }
        return THE_ONE;
    }

    public long objectFieldOffset(Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("valid for instance fields only");
        }
        return objectFieldOffset0(field);
    }

    public int arrayBaseOffset(Class clazz) {
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("valid for array classes only");
        }
        return arrayBaseOffset0(clazz);
    }

    public int arrayIndexScale(Class clazz) {
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("valid for array classes only");
        }
        return arrayIndexScale0(clazz);
    }

    public void park(boolean absolute, long time) {
        if (absolute) {
            Thread.currentThread().parkUntil(time);
        } else {
            Thread.currentThread().parkFor(time);
        }
    }

    public void unpark(Object obj) {
        if (obj instanceof Thread) {
            ((Thread) obj).unpark();
            return;
        }
        throw new IllegalArgumentException("valid for Threads only");
    }
}