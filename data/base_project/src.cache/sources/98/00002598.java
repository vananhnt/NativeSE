package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Dictionary.class */
public abstract class Dictionary<K, V> {
    public abstract Enumeration<V> elements();

    public abstract V get(Object obj);

    public abstract boolean isEmpty();

    public abstract Enumeration<K> keys();

    public abstract V put(K k, V v);

    public abstract V remove(Object obj);

    public abstract int size();

    public Dictionary() {
        throw new RuntimeException("Stub!");
    }
}