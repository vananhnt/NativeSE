package java.util;

import gov.nist.core.Separators;
import java.io.Serializable;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Hashtable.class */
public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, Serializable {
    public Hashtable() {
        throw new RuntimeException("Stub!");
    }

    public Hashtable(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public Hashtable(int capacity, float loadFactor) {
        throw new RuntimeException("Stub!");
    }

    public Hashtable(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary, java.util.Map
    public synchronized boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary, java.util.Map
    public synchronized int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary, java.util.Map
    public synchronized V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public synchronized boolean containsKey(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public synchronized boolean containsValue(Object value) {
        throw new RuntimeException("Stub!");
    }

    public boolean contains(Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary, java.util.Map
    public synchronized V put(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void putAll(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void rehash() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary, java.util.Map
    public synchronized V remove(Object key) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void clear() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Set<K> keySet() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Collection<V> values() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Set<Map.Entry<K, V>> entrySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary
    public synchronized Enumeration<K> keys() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Dictionary
    public synchronized Enumeration<V> elements() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public synchronized boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Map
    public synchronized int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public synchronized String toString() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Hashtable$HashtableEntry.class */
    public static class HashtableEntry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        final int hash;
        HashtableEntry<K, V> next;

        HashtableEntry(K key, V value, int hash, HashtableEntry<K, V> next) {
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
            if (value == null) {
                throw new NullPointerException("value == null");
            }
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
            return this.key.equals(e.getKey()) && this.value.equals(e.getValue());
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }

        public final String toString() {
            return this.key + Separators.EQUALS + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Hashtable$HashIterator.class */
    public abstract class HashIterator {
        int nextIndex;
        HashtableEntry<K, V> nextEntry;
        HashtableEntry<K, V> lastEntryReturned;
        int expectedModCount;

        HashIterator() {
            HashtableEntry<K, V> next;
            this.expectedModCount = Hashtable.access$500(Hashtable.this);
            HashtableEntry<K, V>[] tab = Hashtable.access$600(Hashtable.this);
            HashtableEntry<K, V> hashtableEntry = null;
            while (true) {
                next = hashtableEntry;
                if (next != null || this.nextIndex >= tab.length) {
                    break;
                }
                int i = this.nextIndex;
                this.nextIndex = i + 1;
                hashtableEntry = tab[i];
            }
            this.nextEntry = next;
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashtableEntry<K, V> nextEntry() {
            HashtableEntry<K, V> next;
            if (Hashtable.access$500(Hashtable.this) != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            HashtableEntry<K, V> entryToReturn = this.nextEntry;
            HashtableEntry<K, V>[] tab = Hashtable.access$600(Hashtable.this);
            HashtableEntry<K, V> hashtableEntry = entryToReturn.next;
            while (true) {
                next = hashtableEntry;
                if (next != null || this.nextIndex >= tab.length) {
                    break;
                }
                int i = this.nextIndex;
                this.nextIndex = i + 1;
                hashtableEntry = tab[i];
            }
            this.nextEntry = next;
            this.lastEntryReturned = entryToReturn;
            return entryToReturn;
        }

        HashtableEntry<K, V> nextEntryNotFailFast() {
            HashtableEntry<K, V> next;
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            HashtableEntry<K, V> entryToReturn = this.nextEntry;
            HashtableEntry<K, V>[] tab = Hashtable.access$600(Hashtable.this);
            HashtableEntry<K, V> hashtableEntry = entryToReturn.next;
            while (true) {
                next = hashtableEntry;
                if (next != null || this.nextIndex >= tab.length) {
                    break;
                }
                int i = this.nextIndex;
                this.nextIndex = i + 1;
                hashtableEntry = tab[i];
            }
            this.nextEntry = next;
            this.lastEntryReturned = entryToReturn;
            return entryToReturn;
        }

        public void remove() {
            if (this.lastEntryReturned == null) {
                throw new IllegalStateException();
            }
            if (Hashtable.access$500(Hashtable.this) != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Hashtable.this.remove(this.lastEntryReturned.key);
            this.lastEntryReturned = null;
            this.expectedModCount = Hashtable.access$500(Hashtable.this);
        }
    }

    /* loaded from: Hashtable$KeyIterator.class */
    private final class KeyIterator extends Hashtable<K, V>.HashIterator implements Iterator<K> {
        private KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public K next() {
            return nextEntry().key;
        }
    }

    /* loaded from: Hashtable$ValueIterator.class */
    private final class ValueIterator extends Hashtable<K, V>.HashIterator implements Iterator<V> {
        private ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().value;
        }
    }

    /* loaded from: Hashtable$EntryIterator.class */
    private final class EntryIterator extends Hashtable<K, V>.HashIterator implements Iterator<Map.Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    /* loaded from: Hashtable$KeyEnumeration.class */
    private final class KeyEnumeration extends Hashtable<K, V>.HashIterator implements Enumeration<K> {
        private KeyEnumeration() {
            super();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return hasNext();
        }

        @Override // java.util.Enumeration
        public K nextElement() {
            return nextEntryNotFailFast().key;
        }
    }

    /* loaded from: Hashtable$ValueEnumeration.class */
    private final class ValueEnumeration extends Hashtable<K, V>.HashIterator implements Enumeration<V> {
        private ValueEnumeration() {
            super();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return hasNext();
        }

        @Override // java.util.Enumeration
        public V nextElement() {
            return nextEntryNotFailFast().value;
        }
    }

    /* loaded from: Hashtable$KeySet.class */
    private final class KeySet extends AbstractSet<K> {
        private KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Hashtable.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return Hashtable.this.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            boolean z;
            synchronized (Hashtable.this) {
                int oldSize = Hashtable.access$800(Hashtable.this);
                Hashtable.this.remove(o);
                z = Hashtable.access$800(Hashtable.this) != oldSize;
            }
            return z;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            Hashtable.this.clear();
        }

        @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            boolean removeAll;
            synchronized (Hashtable.this) {
                removeAll = super.removeAll(collection);
            }
            return removeAll;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            boolean retainAll;
            synchronized (Hashtable.this) {
                retainAll = super.retainAll(collection);
            }
            return retainAll;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (Hashtable.this) {
                containsAll = super.containsAll(collection);
            }
            return containsAll;
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public boolean equals(Object object) {
            boolean equals;
            synchronized (Hashtable.this) {
                equals = super.equals(object);
            }
            return equals;
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public int hashCode() {
            int hashCode;
            synchronized (Hashtable.this) {
                hashCode = super.hashCode();
            }
            return hashCode;
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            String abstractSet;
            synchronized (Hashtable.this) {
                abstractSet = super.toString();
            }
            return abstractSet;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (Hashtable.this) {
                array = super.toArray();
            }
            return array;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] a) {
            T[] tArr;
            synchronized (Hashtable.this) {
                tArr = (T[]) super.toArray(a);
            }
            return tArr;
        }
    }

    /* loaded from: Hashtable$Values.class */
    private final class Values extends AbstractCollection<V> {
        private Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Hashtable.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return Hashtable.this.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            Hashtable.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (Hashtable.this) {
                containsAll = super.containsAll(collection);
            }
            return containsAll;
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            String abstractCollection;
            synchronized (Hashtable.this) {
                abstractCollection = super.toString();
            }
            return abstractCollection;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (Hashtable.this) {
                array = super.toArray();
            }
            return array;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] a) {
            T[] tArr;
            synchronized (Hashtable.this) {
                tArr = (T[]) super.toArray(a);
            }
            return tArr;
        }
    }

    /* loaded from: Hashtable$EntrySet.class */
    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return Hashtable.access$1100(Hashtable.this, e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return Hashtable.access$1200(Hashtable.this, e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return Hashtable.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            Hashtable.this.clear();
        }

        @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            boolean removeAll;
            synchronized (Hashtable.this) {
                removeAll = super.removeAll(collection);
            }
            return removeAll;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            boolean retainAll;
            synchronized (Hashtable.this) {
                retainAll = super.retainAll(collection);
            }
            return retainAll;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (Hashtable.this) {
                containsAll = super.containsAll(collection);
            }
            return containsAll;
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public boolean equals(Object object) {
            boolean equals;
            synchronized (Hashtable.this) {
                equals = super.equals(object);
            }
            return equals;
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public int hashCode() {
            return Hashtable.this.hashCode();
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            String abstractSet;
            synchronized (Hashtable.this) {
                abstractSet = super.toString();
            }
            return abstractSet;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (Hashtable.this) {
                array = super.toArray();
            }
            return array;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] a) {
            T[] tArr;
            synchronized (Hashtable.this) {
                tArr = (T[]) super.toArray(a);
            }
            return tArr;
        }
    }
}