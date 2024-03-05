package java.util.concurrent;

import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConcurrentMap.class */
public interface ConcurrentMap<K, V> extends Map<K, V> {
    V putIfAbsent(K k, V v);

    boolean remove(Object obj, Object obj2);

    boolean replace(K k, V v, V v2);

    V replace(K k, V v);
}