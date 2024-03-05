package libcore.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: BasicLruCache.class */
public class BasicLruCache<K, V> {
    private final LinkedHashMap<K, V> map;
    private final int maxSize;

    public BasicLruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<>(0, 0.75f, true);
    }

    public final synchronized V get(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        V result = this.map.get(key);
        if (result != null) {
            return result;
        }
        V result2 = create(key);
        if (result2 != null) {
            this.map.put(key, result2);
            trimToSize(this.maxSize);
        }
        return result2;
    }

    public final synchronized V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        V previous = this.map.put(key, value);
        trimToSize(this.maxSize);
        return previous;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void trimToSize(int maxSize) {
        while (this.map.size() > maxSize) {
            Map.Entry<K, V> toEvict = this.map.eldest();
            K key = toEvict.getKey();
            V value = toEvict.getValue();
            this.map.remove(key);
            entryEvicted(key, value);
        }
    }

    protected void entryEvicted(K key, V value) {
    }

    protected V create(K key) {
        return null;
    }

    public final synchronized Map<K, V> snapshot() {
        return new LinkedHashMap(this.map);
    }

    public final synchronized void evictAll() {
        trimToSize(0);
    }
}