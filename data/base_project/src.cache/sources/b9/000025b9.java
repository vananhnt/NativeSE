package java.util;

import gov.nist.core.Separators;
import java.io.Serializable;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HashMap.class */
public class HashMap<K, V> extends AbstractMap<K, V> implements Cloneable, Serializable {
    public HashMap() {
        throw new RuntimeException("Stub!");
    }

    public HashMap(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public HashMap(int capacity, float loadFactor) {
        throw new RuntimeException("Stub!");
    }

    public HashMap(Map<? extends K, ? extends V> map) {
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

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
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
    public void clear() {
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
    public Set<Map.Entry<K, V>> entrySet() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: HashMap$HashMapEntry.class */
    public static class HashMapEntry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        final int hash;
        HashMapEntry<K, V> next;

        /* JADX INFO: Access modifiers changed from: package-private */
        public HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        @Override // java.util.Map.Entry
        public final K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public final V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public final V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return libcore.util.Objects.equal(e.getKey(), this.key) && libcore.util.Objects.equal(e.getValue(), this.value);
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public final String toString() {
            return this.key + Separators.EQUALS + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: HashMap$HashIterator.class */
    public abstract class HashIterator {
        int nextIndex;
        HashMapEntry<K, V> nextEntry;
        HashMapEntry<K, V> lastEntryReturned;
        int expectedModCount;

        HashIterator() {
            HashMapEntry<K, V> next;
            this.nextEntry = HashMap.this.entryForNullKey;
            this.expectedModCount = HashMap.this.modCount;
            if (this.nextEntry == null) {
                HashMapEntry<K, V>[] tab = HashMap.this.table;
                HashMapEntry<K, V> hashMapEntry = null;
                while (true) {
                    next = hashMapEntry;
                    if (next != null || this.nextIndex >= tab.length) {
                        break;
                    }
                    int i = this.nextIndex;
                    this.nextIndex = i + 1;
                    hashMapEntry = tab[i];
                }
                this.nextEntry = next;
            }
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashMapEntry<K, V> nextEntry() {
            HashMapEntry<K, V> next;
            if (HashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            HashMapEntry<K, V> entryToReturn = this.nextEntry;
            HashMapEntry<K, V>[] tab = HashMap.this.table;
            HashMapEntry<K, V> hashMapEntry = entryToReturn.next;
            while (true) {
                next = hashMapEntry;
                if (next != null || this.nextIndex >= tab.length) {
                    break;
                }
                int i = this.nextIndex;
                this.nextIndex = i + 1;
                hashMapEntry = tab[i];
            }
            this.nextEntry = next;
            this.lastEntryReturned = entryToReturn;
            return entryToReturn;
        }

        public void remove() {
            if (this.lastEntryReturned == null) {
                throw new IllegalStateException();
            }
            if (HashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            HashMap.this.remove(this.lastEntryReturned.key);
            this.lastEntryReturned = null;
            this.expectedModCount = HashMap.this.modCount;
        }
    }

    /* loaded from: HashMap$KeyIterator.class */
    private final class KeyIterator extends HashMap<K, V>.HashIterator implements Iterator<K> {
        private KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            return nextEntry().key;
        }
    }

    /* loaded from: HashMap$ValueIterator.class */
    private final class ValueIterator extends HashMap<K, V>.HashIterator implements Iterator<V> {
        private ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().value;
        }
    }

    /* loaded from: HashMap$EntryIterator.class */
    private final class EntryIterator extends HashMap<K, V>.HashIterator implements Iterator<Map.Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    /* loaded from: HashMap$KeySet.class */
    private final class KeySet extends AbstractSet<K> {
        private KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return HashMap.this.newKeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return HashMap.this.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return HashMap.this.size == 0;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return HashMap.this.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            int oldSize = HashMap.this.size;
            HashMap.this.remove(o);
            return HashMap.this.size != oldSize;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            HashMap.this.clear();
        }
    }

    /* loaded from: HashMap$Values.class */
    private final class Values extends AbstractCollection<V> {
        private Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return HashMap.this.newValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return HashMap.this.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return HashMap.this.size == 0;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return HashMap.this.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            HashMap.this.clear();
        }
    }

    /* loaded from: HashMap$EntrySet.class */
    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K, V>> iterator() {
            return HashMap.this.newEntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return HashMap.access$600(HashMap.this, e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return HashMap.access$700(HashMap.this, e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return HashMap.this.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return HashMap.this.size == 0;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            HashMap.this.clear();
        }
    }
}