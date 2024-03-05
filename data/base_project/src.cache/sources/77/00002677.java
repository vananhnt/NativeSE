package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConcurrentSkipListMap.class */
public class ConcurrentSkipListMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableMap, java.util.SortedMap
    public /* bridge */ /* synthetic */ SortedMap tailMap(Object x0) {
        return tailMap((ConcurrentSkipListMap<K, V>) x0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableMap, java.util.SortedMap
    public /* bridge */ /* synthetic */ SortedMap headMap(Object x0) {
        return headMap((ConcurrentSkipListMap<K, V>) x0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableMap
    public /* bridge */ /* synthetic */ NavigableMap tailMap(Object x0, boolean x1) {
        return tailMap((ConcurrentSkipListMap<K, V>) x0, x1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableMap
    public /* bridge */ /* synthetic */ NavigableMap headMap(Object x0, boolean x1) {
        return headMap((ConcurrentSkipListMap<K, V>) x0, x1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableMap
    public /* bridge */ /* synthetic */ NavigableMap subMap(Object x0, boolean x1, Object x2, boolean x3) {
        return subMap((boolean) x0, x1, (boolean) x2, x3);
    }

    public ConcurrentSkipListMap() {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentSkipListMap(Comparator<? super K> comparator) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentSkipListMap(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentSkipListMap(SortedMap<K, ? extends V> m) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap
    public ConcurrentSkipListMap<K, V> clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public NavigableSet<K> keySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
    public NavigableSet<K> navigableKeySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public ConcurrentNavigableMap<K, V> descendingMap() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
    public NavigableSet<K> descendingKeySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentMap
    public boolean remove(Object key, Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentMap
    public boolean replace(K key, V oldValue, V newValue) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentMap
    public V replace(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public K firstKey() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public K lastKey() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
    public ConcurrentNavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
    public ConcurrentNavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
    public ConcurrentNavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap, java.util.SortedMap
    public ConcurrentNavigableMap<K, V> subMap(K fromKey, K toKey) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap, java.util.SortedMap
    public ConcurrentNavigableMap<K, V> headMap(K toKey) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap, java.util.SortedMap
    public ConcurrentNavigableMap<K, V> tailMap(K fromKey) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> lowerEntry(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public K lowerKey(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> floorEntry(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public K floorKey(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> ceilingEntry(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public K ceilingKey(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> higherEntry(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public K higherKey(K key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> firstEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> lastEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> pollFirstEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> pollLastEntry() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentSkipListMap$Node.class */
    public static final class Node<K, V> {
        final K key;
        volatile Object value;
        volatile Node<K, V> next;
        private static final Unsafe UNSAFE;
        private static final long valueOffset;
        private static final long nextOffset;

        Node(K key, Object value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        Node(Node<K, V> next) {
            this.key = null;
            this.value = this;
            this.next = next;
        }

        boolean casValue(Object cmp, Object val) {
            return UNSAFE.compareAndSwapObject(this, valueOffset, cmp, val);
        }

        boolean casNext(Node<K, V> cmp, Node<K, V> val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        boolean isMarker() {
            return this.value == this;
        }

        boolean isBaseHeader() {
            return this.value == ConcurrentSkipListMap.access$000();
        }

        boolean appendMarker(Node<K, V> f) {
            return casNext(f, new Node<>(f));
        }

        void helpDelete(Node<K, V> b, Node<K, V> f) {
            if (f == this.next && this == b.next) {
                if (f == null || f.value != f) {
                    appendMarker(f);
                } else {
                    b.casNext(this, f.next);
                }
            }
        }

        V getValidValue() {
            V v = (V) this.value;
            if (v == this || v == ConcurrentSkipListMap.access$000()) {
                return null;
            }
            return v;
        }

        AbstractMap.SimpleImmutableEntry<K, V> createSnapshot() {
            V v = getValidValue();
            if (v == null) {
                return null;
            }
            return new AbstractMap.SimpleImmutableEntry<>(this.key, v);
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                valueOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("value"));
                nextOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* loaded from: ConcurrentSkipListMap$Index.class */
    static class Index<K, V> {
        final Node<K, V> node;
        final Index<K, V> down;
        volatile Index<K, V> right;
        private static final Unsafe UNSAFE;
        private static final long rightOffset;

        Index(Node<K, V> node, Index<K, V> down, Index<K, V> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }

        final boolean casRight(Index<K, V> cmp, Index<K, V> val) {
            return UNSAFE.compareAndSwapObject(this, rightOffset, cmp, val);
        }

        final boolean indexesDeletedNode() {
            return this.node.value == null;
        }

        final boolean link(Index<K, V> succ, Index<K, V> newSucc) {
            Node<K, V> n = this.node;
            newSucc.right = succ;
            return n.value != null && casRight(succ, newSucc);
        }

        final boolean unlink(Index<K, V> succ) {
            return !indexesDeletedNode() && casRight(succ, succ.right);
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                rightOffset = UNSAFE.objectFieldOffset(Index.class.getDeclaredField("right"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* loaded from: ConcurrentSkipListMap$HeadIndex.class */
    static final class HeadIndex<K, V> extends Index<K, V> {
        final int level;

        HeadIndex(Node<K, V> node, Index<K, V> down, Index<K, V> right, int level) {
            super(node, down, right);
            this.level = level;
        }
    }

    /* loaded from: ConcurrentSkipListMap$ComparableUsingComparator.class */
    static final class ComparableUsingComparator<K> implements Comparable<K> {
        final K actualKey;
        final Comparator<? super K> cmp;

        ComparableUsingComparator(K key, Comparator<? super K> cmp) {
            this.actualKey = key;
            this.cmp = cmp;
        }

        @Override // java.lang.Comparable
        public int compareTo(K k2) {
            return this.cmp.compare((K) this.actualKey, k2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentSkipListMap$Iter.class */
    public abstract class Iter<T> implements Iterator<T> {
        Node<K, V> lastReturned;
        Node<K, V> next;
        V nextValue;

        Iter() {
            while (true) {
                this.next = ConcurrentSkipListMap.this.findFirst();
                if (this.next != null) {
                    V v = (V) this.next.value;
                    if (v != null && v != this.next) {
                        this.nextValue = v;
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.next != null;
        }

        final void advance() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.next;
            while (true) {
                this.next = this.next.next;
                if (this.next != null) {
                    V v = (V) this.next.value;
                    if (v != null && v != this.next) {
                        this.nextValue = v;
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            Node<K, V> l = this.lastReturned;
            if (l == null) {
                throw new IllegalStateException();
            }
            ConcurrentSkipListMap.this.remove(l.key);
            this.lastReturned = null;
        }
    }

    /* loaded from: ConcurrentSkipListMap$ValueIterator.class */
    final class ValueIterator extends ConcurrentSkipListMap<K, V>.Iter<V> {
        ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            V v = this.nextValue;
            advance();
            return v;
        }
    }

    /* loaded from: ConcurrentSkipListMap$KeyIterator.class */
    final class KeyIterator extends ConcurrentSkipListMap<K, V>.Iter<K> {
        KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            Node<K, V> n = this.next;
            advance();
            return n.key;
        }
    }

    /* loaded from: ConcurrentSkipListMap$EntryIterator.class */
    final class EntryIterator extends ConcurrentSkipListMap<K, V>.Iter<Map.Entry<K, V>> {
        EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            Node<K, V> n = this.next;
            V v = this.nextValue;
            advance();
            return new AbstractMap.SimpleImmutableEntry(n.key, v);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentSkipListMap$KeySet.class */
    public static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final ConcurrentNavigableMap<E, ?> m;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableSet, java.util.SortedSet
        public /* bridge */ /* synthetic */ SortedSet tailSet(Object x0) {
            return tailSet((KeySet<E>) x0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableSet, java.util.SortedSet
        public /* bridge */ /* synthetic */ SortedSet headSet(Object x0) {
            return headSet((KeySet<E>) x0);
        }

        KeySet(ConcurrentNavigableMap<E, ?> map) {
            this.m = map;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.m.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return this.m.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            return this.m.remove(o) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.m.clear();
        }

        @Override // java.util.NavigableSet
        public E lower(E e) {
            return this.m.lowerKey(e);
        }

        @Override // java.util.NavigableSet
        public E floor(E e) {
            return this.m.floorKey(e);
        }

        @Override // java.util.NavigableSet
        public E ceiling(E e) {
            return this.m.ceilingKey(e);
        }

        @Override // java.util.NavigableSet
        public E higher(E e) {
            return this.m.higherKey(e);
        }

        @Override // java.util.SortedSet
        public Comparator<? super E> comparator() {
            return this.m.comparator();
        }

        @Override // java.util.SortedSet
        public E first() {
            return this.m.firstKey();
        }

        @Override // java.util.SortedSet
        public E last() {
            return this.m.lastKey();
        }

        @Override // java.util.NavigableSet
        public E pollFirst() {
            Map.Entry<E, ?> e = this.m.pollFirstEntry();
            if (e == null) {
                return null;
            }
            return e.getKey();
        }

        @Override // java.util.NavigableSet
        public E pollLast() {
            Map.Entry<E, ?> e = this.m.pollLastEntry();
            if (e == null) {
                return null;
            }
            return e.getKey();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            if (this.m instanceof ConcurrentSkipListMap) {
                return ((ConcurrentSkipListMap) this.m).keyIterator();
            }
            return ((SubMap) this.m).keyIterator();
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set)) {
                return false;
            }
            Collection<?> c = (Collection) o;
            try {
                if (containsAll(c)) {
                    if (c.containsAll(this)) {
                        return true;
                    }
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e2) {
                return false;
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return ConcurrentSkipListMap.toList(this).toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] a) {
            return (T[]) ConcurrentSkipListMap.toList(this).toArray(a);
        }

        @Override // java.util.NavigableSet
        public Iterator<E> descendingIterator() {
            return descendingSet().iterator();
        }

        @Override // java.util.NavigableSet
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new KeySet(this.m.subMap((boolean) fromElement, fromInclusive, (boolean) toElement, toInclusive));
        }

        @Override // java.util.NavigableSet
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new KeySet(this.m.headMap((ConcurrentNavigableMap<E, ?>) toElement, inclusive));
        }

        @Override // java.util.NavigableSet
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new KeySet(this.m.tailMap((ConcurrentNavigableMap<E, ?>) fromElement, inclusive));
        }

        @Override // java.util.NavigableSet, java.util.SortedSet
        public NavigableSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        @Override // java.util.NavigableSet, java.util.SortedSet
        public NavigableSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        @Override // java.util.NavigableSet, java.util.SortedSet
        public NavigableSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        @Override // java.util.NavigableSet
        public NavigableSet<E> descendingSet() {
            return new KeySet(this.m.descendingMap());
        }
    }

    /* loaded from: ConcurrentSkipListMap$Values.class */
    static final class Values<E> extends AbstractCollection<E> {
        private final ConcurrentNavigableMap<?, E> m;

        Values(ConcurrentNavigableMap<?, E> map) {
            this.m = map;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            if (this.m instanceof ConcurrentSkipListMap) {
                return ((ConcurrentSkipListMap) this.m).valueIterator();
            }
            return ((SubMap) this.m).valueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.m.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return this.m.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.m.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return ConcurrentSkipListMap.toList(this).toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] a) {
            return (T[]) ConcurrentSkipListMap.toList(this).toArray(a);
        }
    }

    /* loaded from: ConcurrentSkipListMap$EntrySet.class */
    static final class EntrySet<K1, V1> extends AbstractSet<Map.Entry<K1, V1>> {
        private final ConcurrentNavigableMap<K1, V1> m;

        EntrySet(ConcurrentNavigableMap<K1, V1> map) {
            this.m = map;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K1, V1>> iterator() {
            if (this.m instanceof ConcurrentSkipListMap) {
                return ((ConcurrentSkipListMap) this.m).entryIterator();
            }
            return ((SubMap) this.m).entryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            V1 v = this.m.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return this.m.remove(e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.m.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.m.clear();
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set)) {
                return false;
            }
            Collection<?> c = (Collection) o;
            try {
                if (containsAll(c)) {
                    if (c.containsAll(this)) {
                        return true;
                    }
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e2) {
                return false;
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return ConcurrentSkipListMap.toList(this).toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] a) {
            return (T[]) ConcurrentSkipListMap.toList(this).toArray(a);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentSkipListMap$SubMap.class */
    public static final class SubMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
        private static final long serialVersionUID = -7647078645895051609L;
        private final ConcurrentSkipListMap<K, V> m;
        private final K lo;
        private final K hi;
        private final boolean loInclusive;
        private final boolean hiInclusive;
        private final boolean isDescending;
        private transient KeySet<K> keySetView;
        private transient Set<Map.Entry<K, V>> entrySetView;
        private transient Collection<V> valuesView;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ ConcurrentNavigableMap tailMap(Object x0) {
            return tailMap((SubMap<K, V>) x0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ ConcurrentNavigableMap headMap(Object x0) {
            return headMap((SubMap<K, V>) x0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public /* bridge */ /* synthetic */ ConcurrentNavigableMap tailMap(Object x0, boolean x1) {
            return tailMap((SubMap<K, V>) x0, x1);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public /* bridge */ /* synthetic */ ConcurrentNavigableMap headMap(Object x0, boolean x1) {
            return headMap((SubMap<K, V>) x0, x1);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public /* bridge */ /* synthetic */ ConcurrentNavigableMap subMap(Object x0, boolean x1, Object x2, boolean x3) {
            return subMap((boolean) x0, x1, (boolean) x2, x3);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ SortedMap tailMap(Object x0) {
            return tailMap((SubMap<K, V>) x0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ SortedMap headMap(Object x0) {
            return headMap((SubMap<K, V>) x0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableMap
        public /* bridge */ /* synthetic */ NavigableMap tailMap(Object x0, boolean x1) {
            return tailMap((SubMap<K, V>) x0, x1);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableMap
        public /* bridge */ /* synthetic */ NavigableMap headMap(Object x0, boolean x1) {
            return headMap((SubMap<K, V>) x0, x1);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.NavigableMap
        public /* bridge */ /* synthetic */ NavigableMap subMap(Object x0, boolean x1, Object x2, boolean x3) {
            return subMap((boolean) x0, x1, (boolean) x2, x3);
        }

        SubMap(ConcurrentSkipListMap<K, V> map, K fromKey, boolean fromInclusive, K toKey, boolean toInclusive, boolean isDescending) {
            if (fromKey != null && toKey != null && map.compare(fromKey, toKey) > 0) {
                throw new IllegalArgumentException("inconsistent range");
            }
            this.m = map;
            this.lo = fromKey;
            this.hi = toKey;
            this.loInclusive = fromInclusive;
            this.hiInclusive = toInclusive;
            this.isDescending = isDescending;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean tooLow(K key) {
            if (this.lo != null) {
                int c = this.m.compare(key, this.lo);
                if (c >= 0) {
                    if (c == 0 && !this.loInclusive) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean tooHigh(K key) {
            if (this.hi != null) {
                int c = this.m.compare(key, this.hi);
                if (c <= 0) {
                    if (c == 0 && !this.hiInclusive) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean inBounds(K key) {
            return (tooLow(key) || tooHigh(key)) ? false : true;
        }

        private void checkKeyBounds(K key) throws IllegalArgumentException {
            if (key == null) {
                throw new NullPointerException();
            }
            if (!inBounds(key)) {
                throw new IllegalArgumentException("key out of range");
            }
        }

        private boolean isBeforeEnd(Node<K, V> n) {
            K k;
            if (n == null) {
                return false;
            }
            if (this.hi == null || (k = n.key) == null) {
                return true;
            }
            int c = this.m.compare(k, this.hi);
            if (c <= 0) {
                if (c == 0 && !this.hiInclusive) {
                    return false;
                }
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node<K, V> loNode() {
            if (this.lo == null) {
                return this.m.findFirst();
            }
            if (this.loInclusive) {
                return this.m.findNear(this.lo, 1);
            }
            return this.m.findNear(this.lo, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node<K, V> hiNode() {
            if (this.hi == null) {
                return this.m.findLast();
            }
            if (this.hiInclusive) {
                return this.m.findNear(this.hi, 3);
            }
            return this.m.findNear(this.hi, 2);
        }

        private K lowestKey() {
            Node<K, V> n = loNode();
            if (isBeforeEnd(n)) {
                return n.key;
            }
            throw new NoSuchElementException();
        }

        private K highestKey() {
            Node<K, V> n = hiNode();
            if (n != null) {
                K last = n.key;
                if (inBounds(last)) {
                    return last;
                }
            }
            throw new NoSuchElementException();
        }

        private Map.Entry<K, V> lowestEntry() {
            Map.Entry<K, V> e;
            do {
                Node<K, V> n = loNode();
                if (!isBeforeEnd(n)) {
                    return null;
                }
                e = n.createSnapshot();
            } while (e == null);
            return e;
        }

        private Map.Entry<K, V> highestEntry() {
            Map.Entry<K, V> e;
            do {
                Node<K, V> n = hiNode();
                if (n == null || !inBounds(n.key)) {
                    return null;
                }
                e = n.createSnapshot();
            } while (e == null);
            return e;
        }

        private Map.Entry<K, V> removeLowest() {
            K k;
            Object doRemove;
            do {
                Node<K, V> n = loNode();
                if (n == null) {
                    return null;
                }
                k = n.key;
                if (!inBounds(k)) {
                    return null;
                }
                doRemove = this.m.doRemove(k, null);
            } while (doRemove == null);
            return new AbstractMap.SimpleImmutableEntry(k, doRemove);
        }

        private Map.Entry<K, V> removeHighest() {
            K k;
            Object doRemove;
            do {
                Node<K, V> n = hiNode();
                if (n == null) {
                    return null;
                }
                k = n.key;
                if (!inBounds(k)) {
                    return null;
                }
                doRemove = this.m.doRemove(k, null);
            } while (doRemove == null);
            return new AbstractMap.SimpleImmutableEntry(k, doRemove);
        }

        private Map.Entry<K, V> getNearEntry(K key, int rel) {
            K k;
            V v;
            if (this.isDescending) {
                if ((rel & 2) == 0) {
                    rel |= 2;
                } else {
                    rel &= -3;
                }
            }
            if (tooLow(key)) {
                if ((rel & 2) != 0) {
                    return null;
                }
                return lowestEntry();
            } else if (tooHigh(key)) {
                if ((rel & 2) != 0) {
                    return highestEntry();
                }
                return null;
            } else {
                do {
                    Node<K, V> n = this.m.findNear(key, rel);
                    if (n == null || !inBounds(n.key)) {
                        return null;
                    }
                    k = n.key;
                    v = n.getValidValue();
                } while (v == null);
                return new AbstractMap.SimpleImmutableEntry(k, v);
            }
        }

        private K getNearKey(K key, int rel) {
            K k;
            V v;
            Node<K, V> n;
            if (this.isDescending) {
                if ((rel & 2) == 0) {
                    rel |= 2;
                } else {
                    rel &= -3;
                }
            }
            if (tooLow(key)) {
                if ((rel & 2) == 0) {
                    Node<K, V> n2 = loNode();
                    if (isBeforeEnd(n2)) {
                        return n2.key;
                    }
                    return null;
                }
                return null;
            } else if (tooHigh(key)) {
                if ((rel & 2) != 0 && (n = hiNode()) != null) {
                    K last = n.key;
                    if (inBounds(last)) {
                        return last;
                    }
                    return null;
                }
                return null;
            } else {
                do {
                    Node<K, V> n3 = this.m.findNear(key, rel);
                    if (n3 == null || !inBounds(n3.key)) {
                        return null;
                    }
                    k = n3.key;
                    v = n3.getValidValue();
                } while (v == null);
                return k;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            if (key == 0) {
                throw new NullPointerException();
            }
            return inBounds(key) && this.m.containsKey(key);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractMap, java.util.Map
        public V get(Object key) {
            if (key == 0) {
                throw new NullPointerException();
            }
            if (inBounds(key)) {
                return this.m.get(key);
            }
            return null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V put(K key, V value) {
            checkKeyBounds(key);
            return this.m.put(key, value);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractMap, java.util.Map
        public V remove(Object key) {
            if (inBounds(key)) {
                return this.m.remove(key);
            }
            return null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int size() {
            long count = 0;
            Node<K, V> loNode = loNode();
            while (true) {
                Node<K, V> n = loNode;
                if (!isBeforeEnd(n)) {
                    break;
                }
                if (n.getValidValue() != null) {
                    count++;
                }
                loNode = n.next;
            }
            if (count >= 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) count;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean isEmpty() {
            return !isBeforeEnd(loNode());
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsValue(Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            Node<K, V> loNode = loNode();
            while (true) {
                Node<K, V> n = loNode;
                if (isBeforeEnd(n)) {
                    V v = n.getValidValue();
                    if (v == null || !value.equals(v)) {
                        loNode = n.next;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

        @Override // java.util.AbstractMap, java.util.Map
        public void clear() {
            Node<K, V> loNode = loNode();
            while (true) {
                Node<K, V> n = loNode;
                if (isBeforeEnd(n)) {
                    if (n.getValidValue() != null) {
                        this.m.remove(n.key);
                    }
                    loNode = n.next;
                } else {
                    return;
                }
            }
        }

        @Override // java.util.concurrent.ConcurrentMap
        public V putIfAbsent(K key, V value) {
            checkKeyBounds(key);
            return this.m.putIfAbsent(key, value);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.concurrent.ConcurrentMap
        public boolean remove(Object key, Object value) {
            return inBounds(key) && this.m.remove(key, value);
        }

        @Override // java.util.concurrent.ConcurrentMap
        public boolean replace(K key, V oldValue, V newValue) {
            checkKeyBounds(key);
            return this.m.replace(key, oldValue, newValue);
        }

        @Override // java.util.concurrent.ConcurrentMap
        public V replace(K key, V value) {
            checkKeyBounds(key);
            return this.m.replace(key, value);
        }

        @Override // java.util.SortedMap
        public Comparator<? super K> comparator() {
            Comparator<? super K> cmp = this.m.comparator();
            if (this.isDescending) {
                return Collections.reverseOrder(cmp);
            }
            return cmp;
        }

        private SubMap<K, V> newSubMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            if (this.isDescending) {
                fromKey = toKey;
                toKey = fromKey;
                fromInclusive = toInclusive;
                toInclusive = fromInclusive;
            }
            if (this.lo != null) {
                if (fromKey == null) {
                    fromKey = this.lo;
                    fromInclusive = this.loInclusive;
                } else {
                    int c = this.m.compare(fromKey, this.lo);
                    if (c < 0 || (c == 0 && !this.loInclusive && fromInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            if (this.hi != null) {
                if (toKey == null) {
                    toKey = this.hi;
                    toInclusive = this.hiInclusive;
                } else {
                    int c2 = this.m.compare(toKey, this.hi);
                    if (c2 > 0 || (c2 == 0 && !this.hiInclusive && toInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            return new SubMap<>(this.m, fromKey, fromInclusive, toKey, toInclusive, this.isDescending);
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public SubMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            if (fromKey == null || toKey == null) {
                throw new NullPointerException();
            }
            return newSubMap(fromKey, fromInclusive, toKey, toInclusive);
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public SubMap<K, V> headMap(K toKey, boolean inclusive) {
            if (toKey == null) {
                throw new NullPointerException();
            }
            return newSubMap(null, false, toKey, inclusive);
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public SubMap<K, V> tailMap(K fromKey, boolean inclusive) {
            if (fromKey == null) {
                throw new NullPointerException();
            }
            return newSubMap(fromKey, inclusive, null, false);
        }

        @Override // java.util.NavigableMap, java.util.SortedMap
        public SubMap<K, V> subMap(K fromKey, K toKey) {
            return subMap((boolean) fromKey, true, (boolean) toKey, false);
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap, java.util.SortedMap
        public SubMap<K, V> headMap(K toKey) {
            return headMap((SubMap<K, V>) toKey, false);
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap, java.util.SortedMap
        public SubMap<K, V> tailMap(K fromKey) {
            return tailMap((SubMap<K, V>) fromKey, true);
        }

        @Override // java.util.NavigableMap
        public SubMap<K, V> descendingMap() {
            return new SubMap<>(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive, !this.isDescending);
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> ceilingEntry(K key) {
            return getNearEntry(key, 1);
        }

        @Override // java.util.NavigableMap
        public K ceilingKey(K key) {
            return getNearKey(key, 1);
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> lowerEntry(K key) {
            return getNearEntry(key, 2);
        }

        @Override // java.util.NavigableMap
        public K lowerKey(K key) {
            return getNearKey(key, 2);
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> floorEntry(K key) {
            return getNearEntry(key, 3);
        }

        @Override // java.util.NavigableMap
        public K floorKey(K key) {
            return getNearKey(key, 3);
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> higherEntry(K key) {
            return getNearEntry(key, 0);
        }

        @Override // java.util.NavigableMap
        public K higherKey(K key) {
            return getNearKey(key, 0);
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            return this.isDescending ? highestKey() : lowestKey();
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            return this.isDescending ? lowestKey() : highestKey();
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> firstEntry() {
            return this.isDescending ? highestEntry() : lowestEntry();
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> lastEntry() {
            return this.isDescending ? lowestEntry() : highestEntry();
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> pollFirstEntry() {
            return this.isDescending ? removeHighest() : removeLowest();
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> pollLastEntry() {
            return this.isDescending ? removeLowest() : removeHighest();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public NavigableSet<K> keySet() {
            KeySet<K> ks = this.keySetView;
            if (ks != null) {
                return ks;
            }
            KeySet<K> keySet = new KeySet<>(this);
            this.keySetView = keySet;
            return keySet;
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public NavigableSet<K> navigableKeySet() {
            KeySet<K> ks = this.keySetView;
            if (ks != null) {
                return ks;
            }
            KeySet<K> keySet = new KeySet<>(this);
            this.keySetView = keySet;
            return keySet;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Collection<V> values() {
            Collection<V> vs = this.valuesView;
            if (vs != null) {
                return vs;
            }
            Values values = new Values(this);
            this.valuesView = values;
            return values;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            Set<Map.Entry<K, V>> es = this.entrySetView;
            if (es != null) {
                return es;
            }
            EntrySet entrySet = new EntrySet(this);
            this.entrySetView = entrySet;
            return entrySet;
        }

        @Override // java.util.concurrent.ConcurrentNavigableMap, java.util.NavigableMap
        public NavigableSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        Iterator<K> keyIterator() {
            return new SubMapKeyIterator();
        }

        Iterator<V> valueIterator() {
            return new SubMapValueIterator();
        }

        Iterator<Map.Entry<K, V>> entryIterator() {
            return new SubMapEntryIterator();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ConcurrentSkipListMap$SubMap$SubMapIter.class */
        public abstract class SubMapIter<T> implements Iterator<T> {
            Node<K, V> lastReturned;
            Node<K, V> next;
            V nextValue;

            SubMapIter() {
                while (true) {
                    this.next = SubMap.this.isDescending ? SubMap.this.hiNode() : SubMap.this.loNode();
                    if (this.next != null) {
                        V v = (V) this.next.value;
                        if (v != null && v != this.next) {
                            if (!SubMap.this.inBounds(this.next.key)) {
                                this.next = null;
                                return;
                            } else {
                                this.nextValue = v;
                                return;
                            }
                        }
                    } else {
                        return;
                    }
                }
            }

            @Override // java.util.Iterator
            public final boolean hasNext() {
                return this.next != null;
            }

            final void advance() {
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.next;
                if (SubMap.this.isDescending) {
                    descend();
                } else {
                    ascend();
                }
            }

            private void ascend() {
                while (true) {
                    this.next = this.next.next;
                    if (this.next != null) {
                        V v = (V) this.next.value;
                        if (v != null && v != this.next) {
                            if (SubMap.this.tooHigh(this.next.key)) {
                                this.next = null;
                                return;
                            } else {
                                this.nextValue = v;
                                return;
                            }
                        }
                    } else {
                        return;
                    }
                }
            }

            private void descend() {
                while (true) {
                    this.next = SubMap.this.m.findNear(this.lastReturned.key, 2);
                    if (this.next != null) {
                        V v = (V) this.next.value;
                        if (v != null && v != this.next) {
                            if (SubMap.this.tooLow(this.next.key)) {
                                this.next = null;
                                return;
                            } else {
                                this.nextValue = v;
                                return;
                            }
                        }
                    } else {
                        return;
                    }
                }
            }

            @Override // java.util.Iterator
            public void remove() {
                Node<K, V> l = this.lastReturned;
                if (l != null) {
                    SubMap.this.m.remove(l.key);
                    this.lastReturned = null;
                    return;
                }
                throw new IllegalStateException();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ConcurrentSkipListMap$SubMap$SubMapValueIterator.class */
        public final class SubMapValueIterator extends SubMap<K, V>.SubMapIter<V> {
            SubMapValueIterator() {
                super();
            }

            @Override // java.util.Iterator
            public V next() {
                V v = this.nextValue;
                advance();
                return v;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ConcurrentSkipListMap$SubMap$SubMapKeyIterator.class */
        public final class SubMapKeyIterator extends SubMap<K, V>.SubMapIter<K> {
            SubMapKeyIterator() {
                super();
            }

            @Override // java.util.Iterator
            public K next() {
                Node<K, V> n = this.next;
                advance();
                return n.key;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: ConcurrentSkipListMap$SubMap$SubMapEntryIterator.class */
        public final class SubMapEntryIterator extends SubMap<K, V>.SubMapIter<Map.Entry<K, V>> {
            SubMapEntryIterator() {
                super();
            }

            @Override // java.util.Iterator
            public Map.Entry<K, V> next() {
                Node<K, V> n = this.next;
                V v = this.nextValue;
                advance();
                return new AbstractMap.SimpleImmutableEntry(n.key, v);
            }
        }
    }
}