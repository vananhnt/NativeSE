package java.util.concurrent.atomic;

import dalvik.system.VMStack;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AtomicIntegerFieldUpdater.class */
public abstract class AtomicIntegerFieldUpdater<T> {
    public abstract boolean compareAndSet(T t, int i, int i2);

    public abstract boolean weakCompareAndSet(T t, int i, int i2);

    public abstract void set(T t, int i);

    public abstract void lazySet(T t, int i);

    public abstract int get(T t);

    protected AtomicIntegerFieldUpdater() {
        throw new RuntimeException("Stub!");
    }

    public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> tclass, String fieldName) {
        throw new RuntimeException("Stub!");
    }

    public int getAndSet(T obj, int newValue) {
        throw new RuntimeException("Stub!");
    }

    public int getAndIncrement(T obj) {
        throw new RuntimeException("Stub!");
    }

    public int getAndDecrement(T obj) {
        throw new RuntimeException("Stub!");
    }

    public int getAndAdd(T obj, int delta) {
        throw new RuntimeException("Stub!");
    }

    public int incrementAndGet(T obj) {
        throw new RuntimeException("Stub!");
    }

    public int decrementAndGet(T obj) {
        throw new RuntimeException("Stub!");
    }

    public int addAndGet(T obj, int delta) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: AtomicIntegerFieldUpdater$AtomicIntegerFieldUpdaterImpl.class */
    private static class AtomicIntegerFieldUpdaterImpl<T> extends AtomicIntegerFieldUpdater<T> {
        private static final Unsafe unsafe = Unsafe.getUnsafe();
        private final long offset;
        private final Class<T> tclass;
        private final Class<?> cclass;

        AtomicIntegerFieldUpdaterImpl(Class<T> tclass, String fieldName) {
            try {
                Field field = tclass.getDeclaredField(fieldName);
                Class<?> caller = VMStack.getStackClass2();
                int modifiers = field.getModifiers();
                Class<?> fieldt = field.getType();
                if (fieldt != Integer.TYPE) {
                    throw new IllegalArgumentException("Must be integer type");
                }
                if (!Modifier.isVolatile(modifiers)) {
                    throw new IllegalArgumentException("Must be volatile type");
                }
                this.cclass = (!Modifier.isProtected(modifiers) || caller == tclass) ? null : caller;
                this.tclass = tclass;
                this.offset = unsafe.objectFieldOffset(field);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        private void fullCheck(T obj) {
            if (!this.tclass.isInstance(obj)) {
                throw new ClassCastException();
            }
            if (this.cclass != null) {
                ensureProtectedAccess(obj);
            }
        }

        @Override // java.util.concurrent.atomic.AtomicIntegerFieldUpdater
        public boolean compareAndSet(T obj, int expect, int update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            return unsafe.compareAndSwapInt(obj, this.offset, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicIntegerFieldUpdater
        public boolean weakCompareAndSet(T obj, int expect, int update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            return unsafe.compareAndSwapInt(obj, this.offset, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicIntegerFieldUpdater
        public void set(T obj, int newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            unsafe.putIntVolatile(obj, this.offset, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicIntegerFieldUpdater
        public void lazySet(T obj, int newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            unsafe.putOrderedInt(obj, this.offset, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicIntegerFieldUpdater
        public final int get(T obj) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            return unsafe.getIntVolatile(obj, this.offset);
        }

        private void ensureProtectedAccess(T obj) {
            if (this.cclass.isInstance(obj)) {
                return;
            }
            throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + obj.getClass().getName()));
        }
    }
}