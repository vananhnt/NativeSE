package android.util;

/* loaded from: Pools.class */
public final class Pools {

    /* loaded from: Pools$Pool.class */
    public interface Pool<T> {
        T acquire();

        boolean release(T t);
    }

    private Pools() {
    }

    /* loaded from: Pools$SimplePool.class */
    public static class SimplePool<T> implements Pool<T> {
        private final Object[] mPool;
        private int mPoolSize;

        public SimplePool(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            this.mPool = new Object[maxPoolSize];
        }

        @Override // android.util.Pools.Pool
        public T acquire() {
            if (this.mPoolSize > 0) {
                int lastPooledIndex = this.mPoolSize - 1;
                T instance = (T) this.mPool[lastPooledIndex];
                this.mPool[lastPooledIndex] = null;
                this.mPoolSize--;
                return instance;
            }
            return null;
        }

        @Override // android.util.Pools.Pool
        public boolean release(T instance) {
            if (isInPool(instance)) {
                throw new IllegalStateException("Already in the pool!");
            }
            if (this.mPoolSize < this.mPool.length) {
                this.mPool[this.mPoolSize] = instance;
                this.mPoolSize++;
                return true;
            }
            return false;
        }

        private boolean isInPool(T instance) {
            for (int i = 0; i < this.mPoolSize; i++) {
                if (this.mPool[i] == instance) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: Pools$SynchronizedPool.class */
    public static class SynchronizedPool<T> extends SimplePool<T> {
        private final Object mLock;

        public SynchronizedPool(int maxPoolSize) {
            super(maxPoolSize);
            this.mLock = new Object();
        }

        @Override // android.util.Pools.SimplePool, android.util.Pools.Pool
        public T acquire() {
            T t;
            synchronized (this.mLock) {
                t = (T) super.acquire();
            }
            return t;
        }

        @Override // android.util.Pools.SimplePool, android.util.Pools.Pool
        public boolean release(T element) {
            boolean release;
            synchronized (this.mLock) {
                release = super.release(element);
            }
            return release;
        }
    }
}