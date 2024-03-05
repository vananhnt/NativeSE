package java.util.concurrent.atomic;

import dalvik.system.VMStack;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AtomicReferenceFieldUpdater.class */
public abstract class AtomicReferenceFieldUpdater<T, V> {
    public abstract boolean compareAndSet(T t, V v, V v2);

    public abstract boolean weakCompareAndSet(T t, V v, V v2);

    public abstract void set(T t, V v);

    public abstract void lazySet(T t, V v);

    public abstract V get(T t);

    protected AtomicReferenceFieldUpdater() {
        throw new RuntimeException("Stub!");
    }

    public static <U, W> AtomicReferenceFieldUpdater<U, W> newUpdater(Class<U> tclass, Class<W> vclass, String fieldName) {
        throw new RuntimeException("Stub!");
    }

    public V getAndSet(T obj, V newValue) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: AtomicReferenceFieldUpdater$AtomicReferenceFieldUpdaterImpl.class */
    private static final class AtomicReferenceFieldUpdaterImpl<T, V> extends AtomicReferenceFieldUpdater<T, V> {
        private static final Unsafe unsafe = Unsafe.getUnsafe();
        private final long offset;
        private final Class<T> tclass;
        private final Class<V> vclass;
        private final Class<?> cclass;

        AtomicReferenceFieldUpdaterImpl(Class<T> tclass, Class<V> vclass, String fieldName) {
            try {
                Field field = tclass.getDeclaredField(fieldName);
                Class<?> caller = VMStack.getStackClass2();
                int modifiers = field.getModifiers();
                Class<?> fieldClass = field.getType();
                if (vclass != fieldClass) {
                    throw new ClassCastException();
                }
                if (!Modifier.isVolatile(modifiers)) {
                    throw new IllegalArgumentException("Must be volatile type");
                }
                this.cclass = (!Modifier.isProtected(modifiers) || caller == tclass) ? null : caller;
                this.tclass = tclass;
                if (vclass == Object.class) {
                    this.vclass = null;
                } else {
                    this.vclass = vclass;
                }
                this.offset = unsafe.objectFieldOffset(field);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        void targetCheck(T obj) {
            if (!this.tclass.isInstance(obj)) {
                throw new ClassCastException();
            }
            if (this.cclass != null) {
                ensureProtectedAccess(obj);
            }
        }

        void updateCheck(T obj, V update) {
            if (!this.tclass.isInstance(obj) || (update != null && this.vclass != null && !this.vclass.isInstance(update))) {
                throw new ClassCastException();
            }
            if (this.cclass != null) {
                ensureProtectedAccess(obj);
            }
        }

        @Override // java.util.concurrent.atomic.AtomicReferenceFieldUpdater
        public boolean compareAndSet(T obj, V expect, V update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null || (update != null && this.vclass != null && this.vclass != update.getClass())) {
                updateCheck(obj, update);
            }
            return unsafe.compareAndSwapObject(obj, this.offset, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicReferenceFieldUpdater
        public boolean weakCompareAndSet(T obj, V expect, V update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null || (update != null && this.vclass != null && this.vclass != update.getClass())) {
                updateCheck(obj, update);
            }
            return unsafe.compareAndSwapObject(obj, this.offset, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicReferenceFieldUpdater
        public void set(T obj, V newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null || (newValue != null && this.vclass != null && this.vclass != newValue.getClass())) {
                updateCheck(obj, newValue);
            }
            unsafe.putObjectVolatile(obj, this.offset, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicReferenceFieldUpdater
        public void lazySet(T obj, V newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null || (newValue != null && this.vclass != null && this.vclass != newValue.getClass())) {
                updateCheck(obj, newValue);
            }
            unsafe.putOrderedObject(obj, this.offset, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicReferenceFieldUpdater
        public V get(T obj) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                targetCheck(obj);
            }
            return (V) unsafe.getObjectVolatile(obj, this.offset);
        }

        private void ensureProtectedAccess(T obj) {
            if (this.cclass.isInstance(obj)) {
                return;
            }
            throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + obj.getClass().getName()));
        }
    }
}