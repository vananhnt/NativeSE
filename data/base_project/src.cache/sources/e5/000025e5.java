package java.util;

import java.util.HashMap;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LinkedHashMap.class */
public class LinkedHashMap<K, V> extends HashMap<K, V> {
    public LinkedHashMap() {
        throw new RuntimeException("Stub!");
    }

    public LinkedHashMap(int initialCapacity) {
        throw new RuntimeException("Stub!");
    }

    public LinkedHashMap(int initialCapacity, float loadFactor) {
        throw new RuntimeException("Stub!");
    }

    public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        throw new RuntimeException("Stub!");
    }

    public LinkedHashMap(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LinkedHashMap$LinkedEntry.class */
    public static class LinkedEntry<K, V> extends HashMap.HashMapEntry<K, V> {
        LinkedEntry<K, V> nxt;
        LinkedEntry<K, V> prv;

        LinkedEntry() {
            super(null, null, 0, null);
            this.prv = this;
            this.nxt = this;
        }

        LinkedEntry(K key, V value, int hash, HashMap.HashMapEntry<K, V> next, LinkedEntry<K, V> nxt, LinkedEntry<K, V> prv) {
            super(key, value, hash, next);
            this.nxt = nxt;
            this.prv = prv;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LinkedHashMap$LinkedHashIterator.class */
    public abstract class LinkedHashIterator<T> implements Iterator<T> {
        LinkedEntry<K, V> next;
        LinkedEntry<K, V> lastReturned;
        int expectedModCount;

        private LinkedHashIterator() {
            this.next = LinkedHashMap.this.header.nxt;
            this.lastReturned = null;
            this.expectedModCount = LinkedHashMap.this.modCount;
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.next != LinkedHashMap.this.header;
        }

        final LinkedEntry<K, V> nextEntry() {
            if (LinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkedEntry<K, V> e = this.next;
            if (e == LinkedHashMap.this.header) {
                throw new NoSuchElementException();
            }
            this.next = e.nxt;
            this.lastReturned = e;
            return e;
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (LinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            LinkedHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
            this.expectedModCount = LinkedHashMap.this.modCount;
        }
    }

    /* loaded from: LinkedHashMap$KeyIterator.class */
    private final class KeyIterator extends LinkedHashMap<K, V>.LinkedHashIterator<K> {
        private KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public final K next() {
            return nextEntry().key;
        }
    }

    /* loaded from: LinkedHashMap$ValueIterator.class */
    private final class ValueIterator extends LinkedHashMap<K, V>.LinkedHashIterator<V> {
        private ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public final V next() {
            return nextEntry().value;
        }
    }

    /* loaded from: LinkedHashMap$EntryIterator.class */
    private final class EntryIterator extends LinkedHashMap<K, V>.LinkedHashIterator<Map.Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public final Map.Entry<K, V> next() {
            return nextEntry();
        }
    }
}