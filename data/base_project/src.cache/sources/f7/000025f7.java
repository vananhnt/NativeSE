package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Map.class */
public interface Map<K, V> {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Map$Entry.class */
    public interface Entry<K, V> {
        boolean equals(Object obj);

        K getKey();

        V getValue();

        int hashCode();

        V setValue(V v);
    }

    void clear();

    boolean containsKey(Object obj);

    boolean containsValue(Object obj);

    Set<Entry<K, V>> entrySet();

    boolean equals(Object obj);

    V get(Object obj);

    int hashCode();

    boolean isEmpty();

    Set<K> keySet();

    V put(K k, V v);

    void putAll(Map<? extends K, ? extends V> map);

    V remove(Object obj);

    int size();

    Collection<V> values();
}