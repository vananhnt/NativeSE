package java.util;

import gov.nist.core.Separators;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: WeakHashMap.class */
public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    public WeakHashMap() {
        throw new RuntimeException("Stub!");
    }

    public WeakHashMap(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public WeakHashMap(int capacity, float loadFactor) {
        throw new RuntimeException("Stub!");
    }

    public WeakHashMap(Map<? extends K, ? extends V> map) {
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
    public V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
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
    public int size() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WeakHashMap$Entry.class */
    public static final class Entry<K, V> extends WeakReference<K> implements Map.Entry<K, V> {
        final int hash;
        boolean isNull;
        V value;
        Entry<K, V> next;

        /* loaded from: WeakHashMap$Entry$Type.class */
        interface Type<R, K, V> {
            R get(Map.Entry<K, V> entry);
        }

        Entry(K key, V object, ReferenceQueue<K> queue) {
            super(key, queue);
            this.isNull = key == null;
            this.hash = this.isNull ? 0 : Collections.secondaryHash(key);
            this.value = object;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return (K) super.get();
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V object) {
            V result = this.value;
            this.value = object;
            return result;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object other) {
            if (!(other instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) other;
            Object key = super.get();
            if (key != null ? key.equals(entry.getKey()) : key == entry.getKey()) {
                if (this.value != null ? this.value.equals(entry.getValue()) : this.value == entry.getValue()) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return this.hash + (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return super.get() + Separators.EQUALS + this.value;
        }
    }

    /* loaded from: WeakHashMap$HashIterator.class */
    class HashIterator<R> implements Iterator<R> {
        private int position = 0;
        private int expectedModCount;
        private Entry<K, V> currentEntry;
        private Entry<K, V> nextEntry;
        private K nextKey;
        final Entry.Type<R, K, V> type;

        HashIterator(Entry.Type<R, K, V> type) {
            this.type = type;
            this.expectedModCount = WeakHashMap.this.modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.nextEntry != null && (this.nextKey != null || this.nextEntry.isNull)) {
                return true;
            }
            while (true) {
                if (this.nextEntry == null) {
                    while (this.position < WeakHashMap.this.elementData.length) {
                        Entry<K, V>[] entryArr = WeakHashMap.this.elementData;
                        int i = this.position;
                        this.position = i + 1;
                        Entry<K, V> entry = entryArr[i];
                        this.nextEntry = entry;
                        if (entry != null) {
                            break;
                        }
                    }
                    if (this.nextEntry == null) {
                        return false;
                    }
                }
                this.nextKey = this.nextEntry.get();
                if (this.nextKey != null || this.nextEntry.isNull) {
                    return true;
                }
                this.nextEntry = this.nextEntry.next;
            }
        }

        @Override // java.util.Iterator
        public R next() {
            if (this.expectedModCount == WeakHashMap.this.modCount) {
                if (hasNext()) {
                    this.currentEntry = this.nextEntry;
                    this.nextEntry = this.currentEntry.next;
                    R result = this.type.get(this.currentEntry);
                    this.nextKey = null;
                    return result;
                }
                throw new NoSuchElementException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.expectedModCount == WeakHashMap.this.modCount) {
                if (this.currentEntry != null) {
                    WeakHashMap.this.removeEntry(this.currentEntry);
                    this.currentEntry = null;
                    this.expectedModCount++;
                    return;
                }
                throw new IllegalStateException();
            }
            throw new ConcurrentModificationException();
        }
    }

    /* renamed from: java.util.WeakHashMap$1  reason: invalid class name */
    /* loaded from: WeakHashMap$1.class */
    class AnonymousClass1 extends AbstractSet<Map.Entry<K, V>> {
        AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return WeakHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            WeakHashMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            if (contains(object)) {
                WeakHashMap.this.remove(((Map.Entry) object).getKey());
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            Entry<?, ?> entry;
            if ((object instanceof Map.Entry) && (entry = WeakHashMap.this.getEntry(((Map.Entry) object).getKey())) != null) {
                Object key = entry.get();
                if (key != null || entry.isNull) {
                    return object.equals(entry);
                }
                return false;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K, V>> iterator() {
            return new HashIterator(new Entry.Type<Map.Entry<K, V>, K, V>() { // from class: java.util.WeakHashMap.1.1
                @Override // java.util.WeakHashMap.Entry.Type
                public Map.Entry<K, V> get(Map.Entry<K, V> entry) {
                    return entry;
                }
            });
        }
    }

    /* renamed from: java.util.WeakHashMap$2  reason: invalid class name */
    /* loaded from: WeakHashMap$2.class */
    class AnonymousClass2 extends AbstractSet<K> {
        AnonymousClass2() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return WeakHashMap.this.containsKey(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return WeakHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            WeakHashMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object key) {
            if (WeakHashMap.this.containsKey(key)) {
                WeakHashMap.this.remove(key);
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new HashIterator(new Entry.Type<K, K, V>() { // from class: java.util.WeakHashMap.2.1
                @Override // java.util.WeakHashMap.Entry.Type
                public K get(Map.Entry<K, V> entry) {
                    return entry.getKey();
                }
            });
        }
    }

    /* renamed from: java.util.WeakHashMap$3  reason: invalid class name */
    /* loaded from: WeakHashMap$3.class */
    class AnonymousClass3 extends AbstractCollection<V> {
        AnonymousClass3() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return WeakHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            WeakHashMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return WeakHashMap.this.containsValue(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new HashIterator(new Entry.Type<V, K, V>() { // from class: java.util.WeakHashMap.3.1
                @Override // java.util.WeakHashMap.Entry.Type
                public V get(Map.Entry<K, V> entry) {
                    return entry.getValue();
                }
            });
        }
    }
}