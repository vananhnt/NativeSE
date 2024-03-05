package android.util;

import gov.nist.core.Separators;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: LruCache.class */
public class LruCache<K, V> {
    private final LinkedHashMap<K, V> map;
    private int size;
    private int maxSize;
    private int putCount;
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<>(0, 0.75f, true);
    }

    public void resize(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        synchronized (this) {
            this.maxSize = maxSize;
        }
        trimToSize(maxSize);
    }

    public final V get(K key) {
        V mapValue;
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            V mapValue2 = this.map.get(key);
            if (mapValue2 != null) {
                this.hitCount++;
                return mapValue2;
            }
            this.missCount++;
            V createdValue = create(key);
            if (createdValue == null) {
                return null;
            }
            synchronized (this) {
                this.createCount++;
                mapValue = this.map.put(key, createdValue);
                if (mapValue != null) {
                    this.map.put(key, mapValue);
                } else {
                    this.size += safeSizeOf(key, createdValue);
                }
            }
            if (mapValue != null) {
                entryRemoved(false, key, createdValue, mapValue);
                return mapValue;
            }
            trimToSize(this.maxSize);
            return createdValue;
        }
    }

    public final V put(K key, V value) {
        V previous;
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.putCount++;
            this.size += safeSizeOf(key, value);
            previous = this.map.put(key, value);
            if (previous != null) {
                this.size -= safeSizeOf(key, previous);
            }
        }
        if (previous != null) {
            entryRemoved(false, key, previous, value);
        }
        trimToSize(this.maxSize);
        return previous;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void trimToSize(int maxSize) {
        K key;
        V value;
        while (true) {
            synchronized (this) {
                if (this.size < 0 || (this.map.isEmpty() && this.size != 0)) {
                    break;
                } else if (this.size > maxSize) {
                    Map.Entry<K, V> toEvict = this.map.eldest();
                    if (toEvict != null) {
                        key = toEvict.getKey();
                        value = toEvict.getValue();
                        this.map.remove(key);
                        this.size -= safeSizeOf(key, value);
                        this.evictionCount++;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            entryRemoved(true, key, value, null);
        }
        throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
    }

    public final V remove(K key) {
        V previous;
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            previous = this.map.remove(key);
            if (previous != null) {
                this.size -= safeSizeOf(key, previous);
            }
        }
        if (previous != null) {
            entryRemoved(false, key, previous, null);
        }
        return previous;
    }

    protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
    }

    protected V create(K key) {
        return null;
    }

    private int safeSizeOf(K key, V value) {
        int result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + Separators.EQUALS + value);
        }
        return result;
    }

    protected int sizeOf(K key, V value) {
        return 1;
    }

    public final void evictAll() {
        trimToSize(-1);
    }

    public final synchronized int size() {
        return this.size;
    }

    public final synchronized int maxSize() {
        return this.maxSize;
    }

    public final synchronized int hitCount() {
        return this.hitCount;
    }

    public final synchronized int missCount() {
        return this.missCount;
    }

    public final synchronized int createCount() {
        return this.createCount;
    }

    public final synchronized int putCount() {
        return this.putCount;
    }

    public final synchronized int evictionCount() {
        return this.evictionCount;
    }

    public final synchronized Map<K, V> snapshot() {
        return new LinkedHashMap(this.map);
    }

    public final synchronized String toString() {
        int accesses = this.hitCount + this.missCount;
        int hitPercent = accesses != 0 ? (100 * this.hitCount) / accesses : 0;
        return String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(hitPercent));
    }
}