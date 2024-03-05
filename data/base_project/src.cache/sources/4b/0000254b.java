package java.util;

import java.io.Serializable;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractMap.class */
public abstract class AbstractMap<K, V> implements Map<K, V> {
    @Override // java.util.Map
    public abstract Set<Map.Entry<K, V>> entrySet();

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: AbstractMap$SimpleImmutableEntry.class */
    public static class SimpleImmutableEntry<K, V> implements Map.Entry<K, V>, Serializable {
        public SimpleImmutableEntry(K theKey, V theValue) {
            throw new RuntimeException("Stub!");
        }

        public SimpleImmutableEntry(Map.Entry<? extends K, ? extends V> copyFrom) {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public V setValue(V object) {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object object) {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: AbstractMap$SimpleEntry.class */
    public static class SimpleEntry<K, V> implements Map.Entry<K, V>, Serializable {
        public SimpleEntry(K theKey, V theValue) {
            throw new RuntimeException("Stub!");
        }

        public SimpleEntry(Map.Entry<? extends K, ? extends V> copyFrom) {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public V setValue(V object) {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object object) {
            throw new RuntimeException("Stub!");
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMap() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public V remove(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public int size() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public Collection<V> values() {
        throw new RuntimeException("Stub!");
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.AbstractMap$1  reason: invalid class name */
    /* loaded from: AbstractMap$1.class */
    class AnonymousClass1 extends AbstractSet<K> {
        AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return AbstractMap.this.containsKey(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return AbstractMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new Iterator<K>() { // from class: java.util.AbstractMap.1.1
                Iterator<Map.Entry<K, V>> setIterator;

                {
                    this.setIterator = AbstractMap.this.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.setIterator.hasNext();
                }

                @Override // java.util.Iterator
                public K next() {
                    return this.setIterator.next().getKey();
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.setIterator.remove();
                }
            };
        }
    }

    /* renamed from: java.util.AbstractMap$2  reason: invalid class name */
    /* loaded from: AbstractMap$2.class */
    class AnonymousClass2 extends AbstractCollection<V> {
        AnonymousClass2() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return AbstractMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return AbstractMap.this.containsValue(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new Iterator<V>() { // from class: java.util.AbstractMap.2.1
                Iterator<Map.Entry<K, V>> setIterator;

                {
                    this.setIterator = AbstractMap.this.entrySet().iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.setIterator.hasNext();
                }

                @Override // java.util.Iterator
                public V next() {
                    return this.setIterator.next().getValue();
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.setIterator.remove();
                }
            };
        }
    }
}