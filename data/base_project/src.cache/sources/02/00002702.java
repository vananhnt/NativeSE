package java.util.concurrent.atomic;

import dalvik.system.VMStack;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AtomicLongFieldUpdater.class */
public abstract class AtomicLongFieldUpdater<T> {
    public abstract boolean compareAndSet(T t, long j, long j2);

    public abstract boolean weakCompareAndSet(T t, long j, long j2);

    public abstract void set(T t, long j);

    public abstract void lazySet(T t, long j);

    public abstract long get(T t);

    protected AtomicLongFieldUpdater() {
        throw new RuntimeException("Stub!");
    }

    public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> tclass, String fieldName) {
        throw new RuntimeException("Stub!");
    }

    public long getAndSet(T obj, long newValue) {
        throw new RuntimeException("Stub!");
    }

    public long getAndIncrement(T obj) {
        throw new RuntimeException("Stub!");
    }

    public long getAndDecrement(T obj) {
        throw new RuntimeException("Stub!");
    }

    public long getAndAdd(T obj, long delta) {
        throw new RuntimeException("Stub!");
    }

    public long incrementAndGet(T obj) {
        throw new RuntimeException("Stub!");
    }

    public long decrementAndGet(T obj) {
        throw new RuntimeException("Stub!");
    }

    public long addAndGet(T obj, long delta) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: AtomicLongFieldUpdater$CASUpdater.class */
    private static class CASUpdater<T> extends AtomicLongFieldUpdater<T> {
        private static final Unsafe unsafe = Unsafe.getUnsafe();
        private final long offset;
        private final Class<T> tclass;
        private final Class<?> cclass;

        CASUpdater(Class<T> tclass, String fieldName) {
            try {
                Field field = tclass.getDeclaredField(fieldName);
                Class<?> caller = VMStack.getStackClass2();
                int modifiers = field.getModifiers();
                Class<?> fieldt = field.getType();
                if (fieldt != Long.TYPE) {
                    throw new IllegalArgumentException("Must be long type");
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

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public boolean compareAndSet(T obj, long expect, long update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            return unsafe.compareAndSwapLong(obj, this.offset, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public boolean weakCompareAndSet(T obj, long expect, long update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            return unsafe.compareAndSwapLong(obj, this.offset, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public void set(T obj, long newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            unsafe.putLongVolatile(obj, this.offset, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public void lazySet(T obj, long newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            unsafe.putOrderedLong(obj, this.offset, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public long get(T obj) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            return unsafe.getLongVolatile(obj, this.offset);
        }

        private void ensureProtectedAccess(T obj) {
            if (this.cclass.isInstance(obj)) {
                return;
            }
            throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + obj.getClass().getName()));
        }
    }

    /* loaded from: AtomicLongFieldUpdater$LockedUpdater.class */
    private static class LockedUpdater<T> extends AtomicLongFieldUpdater<T> {
        private static final Unsafe unsafe = Unsafe.getUnsafe();
        private final long offset;
        private final Class<T> tclass;
        private final Class<?> cclass;

        LockedUpdater(Class<T> tclass, String fieldName) {
            try {
                Field field = tclass.getDeclaredField(fieldName);
                Class<?> caller = VMStack.getStackClass2();
                int modifiers = field.getModifiers();
                Class<?> fieldt = field.getType();
                if (fieldt != Long.TYPE) {
                    throw new IllegalArgumentException("Must be long type");
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

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public boolean compareAndSet(T obj, long expect, long update) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            synchronized (this) {
                long v = unsafe.getLong(obj, this.offset);
                if (v != expect) {
                    return false;
                }
                unsafe.putLong(obj, this.offset, update);
                return true;
            }
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public boolean weakCompareAndSet(T obj, long expect, long update) {
            return compareAndSet(obj, expect, update);
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public void set(T obj, long newValue) {
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            synchronized (this) {
                unsafe.putLong(obj, this.offset, newValue);
            }
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public void lazySet(T obj, long newValue) {
            set(obj, newValue);
        }

        @Override // java.util.concurrent.atomic.AtomicLongFieldUpdater
        public long get(T obj) {
            long j;
            if (obj == null || obj.getClass() != this.tclass || this.cclass != null) {
                fullCheck(obj);
            }
            synchronized (this) {
                j = unsafe.getLong(obj, this.offset);
            }
            return j;
        }

        private void ensureProtectedAccess(T obj) {
            if (this.cclass.isInstance(obj)) {
                return;
            }
            throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + obj.getClass().getName()));
        }
    }
}