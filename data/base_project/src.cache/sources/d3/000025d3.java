package java.util;

import gov.nist.core.Separators;
import java.io.Serializable;
import java.util.Map;
import java.util.MapEntry;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: IdentityHashMap.class */
public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable, Cloneable {
    public IdentityHashMap() {
        throw new RuntimeException("Stub!");
    }

    public IdentityHashMap(int maxSize) {
        throw new RuntimeException("Stub!");
    }

    public IdentityHashMap(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
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
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: IdentityHashMap$IdentityHashMapEntry.class */
    public static class IdentityHashMapEntry<K, V> extends MapEntry<K, V> {
        private final IdentityHashMap<K, V> map;

        IdentityHashMapEntry(IdentityHashMap<K, V> map, K theKey, V theValue) {
            super(theKey, theValue);
            this.map = map;
        }

        @Override // java.util.MapEntry
        public Object clone() {
            return super.clone();
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) object;
                return this.key == entry.getKey() && this.value == entry.getValue();
            }
            return false;
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public int hashCode() {
            return System.identityHashCode(this.key) ^ System.identityHashCode(this.value);
        }

        @Override // java.util.MapEntry
        public String toString() {
            return this.key + Separators.EQUALS + this.value;
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public V setValue(V object) {
            V result = (V) super.setValue(object);
            this.map.put(this.key, object);
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: IdentityHashMap$IdentityHashMapIterator.class */
    public static class IdentityHashMapIterator<E, KT, VT> implements Iterator<E> {
        final IdentityHashMap<KT, VT> associatedMap;
        int expectedModCount;
        final MapEntry.Type<E, KT, VT> type;
        private int position = 0;
        private int lastPosition = 0;
        boolean canRemove = false;

        IdentityHashMapIterator(MapEntry.Type<E, KT, VT> value, IdentityHashMap<KT, VT> hm) {
            this.associatedMap = hm;
            this.type = value;
            this.expectedModCount = hm.modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            while (this.position < this.associatedMap.elementData.length) {
                if (this.associatedMap.elementData[this.position] == null) {
                    this.position += 2;
                } else {
                    return true;
                }
            }
            return false;
        }

        void checkConcurrentMod() throws ConcurrentModificationException {
            if (this.expectedModCount != this.associatedMap.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override // java.util.Iterator
        public E next() {
            checkConcurrentMod();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            IdentityHashMapEntry<KT, VT> result = IdentityHashMap.access$000(this.associatedMap, this.position);
            this.lastPosition = this.position;
            this.position += 2;
            this.canRemove = true;
            return this.type.get(result);
        }

        @Override // java.util.Iterator
        public void remove() {
            checkConcurrentMod();
            if (!this.canRemove) {
                throw new IllegalStateException();
            }
            this.canRemove = false;
            this.associatedMap.remove(this.associatedMap.elementData[this.lastPosition]);
            this.position = this.lastPosition;
            this.expectedModCount++;
        }
    }

    /* loaded from: IdentityHashMap$IdentityHashMapEntrySet.class */
    static class IdentityHashMapEntrySet<KT, VT> extends AbstractSet<Map.Entry<KT, VT>> {
        private final IdentityHashMap<KT, VT> associatedMap;

        public IdentityHashMapEntrySet(IdentityHashMap<KT, VT> hm) {
            this.associatedMap = hm;
        }

        IdentityHashMap<KT, VT> hashMap() {
            return this.associatedMap;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.associatedMap.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.associatedMap.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            if (contains(object)) {
                this.associatedMap.remove(((Map.Entry) object).getKey());
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            IdentityHashMapEntry<?, ?> entry;
            return (object instanceof Map.Entry) && (entry = IdentityHashMap.access$100(this.associatedMap, ((Map.Entry) object).getKey())) != null && entry.equals(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<KT, VT>> iterator() {
            return new IdentityHashMapIterator(new MapEntry.Type<Map.Entry<KT, VT>, KT, VT>() { // from class: java.util.IdentityHashMap.IdentityHashMapEntrySet.1
                @Override // java.util.MapEntry.Type
                public Map.Entry<KT, VT> get(MapEntry<KT, VT> entry) {
                    return entry;
                }
            }, this.associatedMap);
        }
    }

    /* renamed from: java.util.IdentityHashMap$1  reason: invalid class name */
    /* loaded from: IdentityHashMap$1.class */
    class AnonymousClass1 extends AbstractSet<K> {
        AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return IdentityHashMap.this.containsKey(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return IdentityHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            IdentityHashMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object key) {
            if (IdentityHashMap.this.containsKey(key)) {
                IdentityHashMap.this.remove(key);
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new IdentityHashMapIterator(new MapEntry.Type<K, K, V>() { // from class: java.util.IdentityHashMap.1.1
                @Override // java.util.MapEntry.Type
                public K get(MapEntry<K, V> entry) {
                    return entry.key;
                }
            }, IdentityHashMap.this);
        }
    }

    /* renamed from: java.util.IdentityHashMap$2  reason: invalid class name */
    /* loaded from: IdentityHashMap$2.class */
    class AnonymousClass2 extends AbstractCollection<V> {
        AnonymousClass2() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return IdentityHashMap.this.containsValue(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return IdentityHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            IdentityHashMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new IdentityHashMapIterator(new MapEntry.Type<V, K, V>() { // from class: java.util.IdentityHashMap.2.1
                @Override // java.util.MapEntry.Type
                public V get(MapEntry<K, V> entry) {
                    return entry.value;
                }
            }, IdentityHashMap.this);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            Iterator<?> it = iterator();
            while (it.hasNext()) {
                if (object == it.next()) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }
    }
}