package java.util;

import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NavigableMap.class */
public interface NavigableMap<K, V> extends SortedMap<K, V> {
    Map.Entry<K, V> lowerEntry(K k);

    K lowerKey(K k);

    Map.Entry<K, V> floorEntry(K k);

    K floorKey(K k);

    Map.Entry<K, V> ceilingEntry(K k);

    K ceilingKey(K k);

    Map.Entry<K, V> higherEntry(K k);

    K higherKey(K k);

    Map.Entry<K, V> firstEntry();

    Map.Entry<K, V> lastEntry();

    Map.Entry<K, V> pollFirstEntry();

    Map.Entry<K, V> pollLastEntry();

    NavigableMap<K, V> descendingMap();

    NavigableSet<K> navigableKeySet();

    NavigableSet<K> descendingKeySet();

    NavigableMap<K, V> subMap(K k, boolean z, K k2, boolean z2);

    NavigableMap<K, V> headMap(K k, boolean z);

    NavigableMap<K, V> tailMap(K k, boolean z);

    @Override // java.util.SortedMap
    SortedMap<K, V> subMap(K k, K k2);

    @Override // java.util.SortedMap
    SortedMap<K, V> headMap(K k);

    @Override // java.util.SortedMap
    SortedMap<K, V> tailMap(K k);
}