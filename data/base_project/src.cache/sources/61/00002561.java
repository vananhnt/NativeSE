package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Collections.class */
public class Collections {
    public static final List EMPTY_LIST = null;
    public static final Set EMPTY_SET = null;
    public static final Map EMPTY_MAP = null;

    Collections() {
        throw new RuntimeException("Stub!");
    }

    public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T object) {
        throw new RuntimeException("Stub!");
    }

    public static <T> int binarySearch(List<? extends T> list, T object, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static <T> void copy(List<? super T> destination, List<? extends T> source) {
        throw new RuntimeException("Stub!");
    }

    public static <T> Enumeration<T> enumeration(Collection<T> collection) {
        throw new RuntimeException("Stub!");
    }

    public static <T> void fill(List<? super T> list, T object) {
        throw new RuntimeException("Stub!");
    }

    public static <T extends Comparable<? super T>> T max(Collection<? extends T> collection) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T max(Collection<? extends T> collection, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static <T extends Comparable<? super T>> T min(Collection<? extends T> collection) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T min(Collection<? extends T> collection, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static <T> List<T> nCopies(int length, T object) {
        throw new RuntimeException("Stub!");
    }

    public static void reverse(List<?> list) {
        throw new RuntimeException("Stub!");
    }

    public static <T> Comparator<T> reverseOrder() {
        throw new RuntimeException("Stub!");
    }

    public static <T> Comparator<T> reverseOrder(Comparator<T> c) {
        throw new RuntimeException("Stub!");
    }

    public static void shuffle(List<?> list) {
        throw new RuntimeException("Stub!");
    }

    public static void shuffle(List<?> list, Random random) {
        throw new RuntimeException("Stub!");
    }

    public static <E> Set<E> singleton(E object) {
        throw new RuntimeException("Stub!");
    }

    public static <E> List<E> singletonList(E object) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        throw new RuntimeException("Stub!");
    }

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static void swap(List<?> list, int index1, int index2) {
        throw new RuntimeException("Stub!");
    }

    public static <T> boolean replaceAll(List<T> list, T obj, T obj2) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.Collections$1  reason: invalid class name */
    /* loaded from: Collections$1.class */
    static class AnonymousClass1 implements Iterator<Object> {
        AnonymousClass1() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new IllegalStateException();
        }
    }

    public static void rotate(List<?> lst, int dist) {
        throw new RuntimeException("Stub!");
    }

    public static int indexOfSubList(List<?> list, List<?> sublist) {
        throw new RuntimeException("Stub!");
    }

    public static int lastIndexOfSubList(List<?> list, List<?> sublist) {
        throw new RuntimeException("Stub!");
    }

    public static <T> ArrayList<T> list(Enumeration<T> enumeration) {
        throw new RuntimeException("Stub!");
    }

    public static <T> Collection<T> synchronizedCollection(Collection<T> collection) {
        throw new RuntimeException("Stub!");
    }

    public static <T> List<T> synchronizedList(List<T> list) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
        throw new RuntimeException("Stub!");
    }

    public static <E> Set<E> synchronizedSet(Set<E> set) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> map) {
        throw new RuntimeException("Stub!");
    }

    public static <E> SortedSet<E> synchronizedSortedSet(SortedSet<E> set) {
        throw new RuntimeException("Stub!");
    }

    public static <E> Collection<E> unmodifiableCollection(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.Collections$2  reason: invalid class name */
    /* loaded from: Collections$2.class */
    static class AnonymousClass2 implements Enumeration<Object> {
        AnonymousClass2() {
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        @Override // java.util.Enumeration
        public Object nextElement() {
            throw new NoSuchElementException();
        }
    }

    public static <E> List<E> unmodifiableList(List<? extends E> list) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    public static <E> Set<E> unmodifiableSet(Set<? extends E> set) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    public static <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> set) {
        throw new RuntimeException("Stub!");
    }

    public static int frequency(Collection<?> c, Object o) {
        throw new RuntimeException("Stub!");
    }

    public static final <T> List<T> emptyList() {
        throw new RuntimeException("Stub!");
    }

    public static final <T> Set<T> emptySet() {
        throw new RuntimeException("Stub!");
    }

    public static final <K, V> Map<K, V> emptyMap() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Collections$CopiesList.class */
    private static final class CopiesList<E> extends AbstractList<E> implements Serializable {
        private static final long serialVersionUID = 2739099268398711800L;
        private final int n;
        private final E element;

        CopiesList(int length, E object) {
            if (length < 0) {
                throw new IllegalArgumentException("length < 0: " + length);
            }
            this.n = length;
            this.element = object;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.element == null ? object == null : this.element.equals(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.n;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int location) {
            if (location >= 0 && location < this.n) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }
    }

    public static <E> Collection<E> checkedCollection(Collection<E> c, Class<E> type) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> Map<K, V> checkedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
        throw new RuntimeException("Stub!");
    }

    public static <E> List<E> checkedList(List<E> list, Class<E> type) {
        throw new RuntimeException("Stub!");
    }

    public static <E> Set<E> checkedSet(Set<E> s, Class<E> type) {
        throw new RuntimeException("Stub!");
    }

    public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
        throw new RuntimeException("Stub!");
    }

    public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> s, Class<E> type) {
        throw new RuntimeException("Stub!");
    }

    public static <T> boolean addAll(Collection<? super T> c, T... a) {
        throw new RuntimeException("Stub!");
    }

    public static boolean disjoint(Collection<?> c1, Collection<?> c2) {
        throw new RuntimeException("Stub!");
    }

    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        throw new RuntimeException("Stub!");
    }

    public static <T> Queue<T> asLifoQueue(Deque<T> deque) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Collections$EmptyList.class */
    private static final class EmptyList extends AbstractList implements RandomAccess, Serializable {
        private static final long serialVersionUID = 8842843931221139166L;

        private EmptyList() {
        }

        /* synthetic */ EmptyList(AnonymousClass1 x0) {
            this();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return 0;
        }

        @Override // java.util.AbstractList, java.util.List
        public Object get(int location) {
            throw new IndexOutOfBoundsException();
        }

        private Object readResolve() {
            return Collections.EMPTY_LIST;
        }
    }

    /* loaded from: Collections$EmptySet.class */
    private static final class EmptySet extends AbstractSet implements Serializable {
        private static final long serialVersionUID = 1582296315990362920L;

        private EmptySet() {
        }

        /* synthetic */ EmptySet(AnonymousClass1 x0) {
            this();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return 0;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator iterator() {
            return Collections.access$000();
        }

        private Object readResolve() {
            return Collections.EMPTY_SET;
        }
    }

    /* loaded from: Collections$EmptyMap.class */
    private static final class EmptyMap extends AbstractMap implements Serializable {
        private static final long serialVersionUID = 6428348081105594320L;

        private EmptyMap() {
        }

        /* synthetic */ EmptyMap(AnonymousClass1 x0) {
            this();
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            return false;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsValue(Object value) {
            return false;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set entrySet() {
            return Collections.EMPTY_SET;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Object get(Object key) {
            return null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set keySet() {
            return Collections.EMPTY_SET;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Collection values() {
            return Collections.EMPTY_LIST;
        }

        private Object readResolve() {
            return Collections.EMPTY_MAP;
        }
    }

    /* loaded from: Collections$ReverseComparator.class */
    private static final class ReverseComparator<T> implements Comparator<T>, Serializable {
        private static final ReverseComparator<Object> INSTANCE = new ReverseComparator<>();
        private static final long serialVersionUID = 7207038068494060240L;

        private ReverseComparator() {
        }

        @Override // java.util.Comparator
        public int compare(T o1, T o2) {
            Comparable<T> c2 = (Comparable) o2;
            return c2.compareTo(o1);
        }

        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    /* loaded from: Collections$ReverseComparator2.class */
    private static final class ReverseComparator2<T> implements Comparator<T>, Serializable {
        private static final long serialVersionUID = 4374092139857L;
        private final Comparator<T> cmp;

        ReverseComparator2(Comparator<T> comparator) {
            this.cmp = comparator;
        }

        @Override // java.util.Comparator
        public int compare(T o1, T o2) {
            return this.cmp.compare(o2, o1);
        }

        @Override // java.util.Comparator
        public boolean equals(Object o) {
            return (o instanceof ReverseComparator2) && ((ReverseComparator2) o).cmp.equals(this.cmp);
        }

        public int hashCode() {
            return this.cmp.hashCode() ^ (-1);
        }
    }

    /* loaded from: Collections$SingletonSet.class */
    private static final class SingletonSet<E> extends AbstractSet<E> implements Serializable {
        private static final long serialVersionUID = 3193687207550431679L;
        final E element;

        SingletonSet(E object) {
            this.element = object;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.element == null ? object == null : this.element.equals(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return 1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return new Iterator<E>() { // from class: java.util.Collections.SingletonSet.1
                boolean hasNext = true;

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.hasNext;
                }

                @Override // java.util.Iterator
                public E next() {
                    if (this.hasNext) {
                        this.hasNext = false;
                        return SingletonSet.this.element;
                    }
                    throw new NoSuchElementException();
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    /* loaded from: Collections$SingletonList.class */
    private static final class SingletonList<E> extends AbstractList<E> implements Serializable {
        private static final long serialVersionUID = 3093736618740652951L;
        final E element;

        SingletonList(E object) {
            this.element = object;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.element == null ? object == null : this.element.equals(object);
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int location) {
            if (location == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Collections$SingletonMap.class */
    public static final class SingletonMap<K, V> extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = -6979724477215052911L;
        final K k;
        final V v;

        SingletonMap(K key, V value) {
            this.k = key;
            this.v = value;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            return this.k == null ? key == null : this.k.equals(key);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsValue(Object value) {
            return this.v == null ? value == null : this.v.equals(value);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V get(Object key) {
            if (containsKey(key)) {
                return this.v;
            }
            return null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int size() {
            return 1;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            return new AbstractSet<Map.Entry<K, V>>() { // from class: java.util.Collections.SingletonMap.1
                @Override // java.util.AbstractCollection, java.util.Collection
                public boolean contains(Object object) {
                    if (object instanceof Map.Entry) {
                        Map.Entry<?, ?> entry = (Map.Entry) object;
                        return SingletonMap.this.containsKey(entry.getKey()) && SingletonMap.this.containsValue(entry.getValue());
                    }
                    return false;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
                public int size() {
                    return 1;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
                public Iterator<Map.Entry<K, V>> iterator() {
                    return new Iterator<Map.Entry<K, V>>() { // from class: java.util.Collections.SingletonMap.1.1
                        boolean hasNext = true;

                        @Override // java.util.Iterator
                        public boolean hasNext() {
                            return this.hasNext;
                        }

                        @Override // java.util.Iterator
                        public Map.Entry<K, V> next() {
                            if (!this.hasNext) {
                                throw new NoSuchElementException();
                            }
                            this.hasNext = false;
                            return new MapEntry<K, V>(SingletonMap.this.k, SingletonMap.this.v) { // from class: java.util.Collections.SingletonMap.1.1.1
                                @Override // java.util.MapEntry, java.util.Map.Entry
                                public V setValue(V value) {
                                    throw new UnsupportedOperationException();
                                }
                            };
                        }

                        @Override // java.util.Iterator
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        }
    }

    /* loaded from: Collections$SynchronizedCollection.class */
    static class SynchronizedCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 3053995032091335093L;
        final Collection<E> c;
        final Object mutex;

        SynchronizedCollection(Collection<E> collection) {
            this.c = collection;
            this.mutex = this;
        }

        SynchronizedCollection(Collection<E> collection, Object mutex) {
            this.c = collection;
            this.mutex = mutex;
        }

        @Override // java.util.Collection
        public boolean add(E object) {
            boolean add;
            synchronized (this.mutex) {
                add = this.c.add(object);
            }
            return add;
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends E> collection) {
            boolean addAll;
            synchronized (this.mutex) {
                addAll = this.c.addAll(collection);
            }
            return addAll;
        }

        @Override // java.util.Collection
        public void clear() {
            synchronized (this.mutex) {
                this.c.clear();
            }
        }

        @Override // java.util.Collection
        public boolean contains(Object object) {
            boolean contains;
            synchronized (this.mutex) {
                contains = this.c.contains(object);
            }
            return contains;
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (this.mutex) {
                containsAll = this.c.containsAll(collection);
            }
            return containsAll;
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            boolean isEmpty;
            synchronized (this.mutex) {
                isEmpty = this.c.isEmpty();
            }
            return isEmpty;
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            Iterator<E> it;
            synchronized (this.mutex) {
                it = this.c.iterator();
            }
            return it;
        }

        @Override // java.util.Collection
        public boolean remove(Object object) {
            boolean remove;
            synchronized (this.mutex) {
                remove = this.c.remove(object);
            }
            return remove;
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            boolean removeAll;
            synchronized (this.mutex) {
                removeAll = this.c.removeAll(collection);
            }
            return removeAll;
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            boolean retainAll;
            synchronized (this.mutex) {
                retainAll = this.c.retainAll(collection);
            }
            return retainAll;
        }

        @Override // java.util.Collection, java.util.List
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.c.size();
            }
            return size;
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            Object[] array;
            synchronized (this.mutex) {
                array = this.c.toArray();
            }
            return array;
        }

        public String toString() {
            String obj;
            synchronized (this.mutex) {
                obj = this.c.toString();
            }
            return obj;
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] array) {
            T[] tArr;
            synchronized (this.mutex) {
                tArr = (T[]) this.c.toArray(array);
            }
            return tArr;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    /* loaded from: Collections$SynchronizedRandomAccessList.class */
    static class SynchronizedRandomAccessList<E> extends SynchronizedList<E> implements RandomAccess {
        private static final long serialVersionUID = 1530674583602358482L;

        SynchronizedRandomAccessList(List<E> l) {
            super(l);
        }

        SynchronizedRandomAccessList(List<E> l, Object mutex) {
            super(l, mutex);
        }

        @Override // java.util.Collections.SynchronizedList, java.util.List
        public List<E> subList(int start, int end) {
            SynchronizedRandomAccessList synchronizedRandomAccessList;
            synchronized (this.mutex) {
                synchronizedRandomAccessList = new SynchronizedRandomAccessList(this.list.subList(start, end), this.mutex);
            }
            return synchronizedRandomAccessList;
        }

        private Object writeReplace() {
            return new SynchronizedList(this.list);
        }
    }

    /* loaded from: Collections$SynchronizedList.class */
    static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
        private static final long serialVersionUID = -7754090372962971524L;
        final List<E> list;

        SynchronizedList(List<E> l) {
            super(l);
            this.list = l;
        }

        SynchronizedList(List<E> l, Object mutex) {
            super(l, mutex);
            this.list = l;
        }

        @Override // java.util.List
        public void add(int location, E object) {
            synchronized (this.mutex) {
                this.list.add(location, object);
            }
        }

        @Override // java.util.List
        public boolean addAll(int location, Collection<? extends E> collection) {
            boolean addAll;
            synchronized (this.mutex) {
                addAll = this.list.addAll(location, collection);
            }
            return addAll;
        }

        @Override // java.util.Collection
        public boolean equals(Object object) {
            boolean equals;
            synchronized (this.mutex) {
                equals = this.list.equals(object);
            }
            return equals;
        }

        @Override // java.util.List
        public E get(int location) {
            E e;
            synchronized (this.mutex) {
                e = this.list.get(location);
            }
            return e;
        }

        @Override // java.util.Collection
        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.list.hashCode();
            }
            return hashCode;
        }

        @Override // java.util.List
        public int indexOf(Object object) {
            int size;
            Object[] array;
            synchronized (this.mutex) {
                size = this.list.size();
                array = new Object[size];
                this.list.toArray(array);
            }
            if (object != null) {
                for (int i = 0; i < size; i++) {
                    if (object.equals(array[i])) {
                        return i;
                    }
                }
                return -1;
            }
            for (int i2 = 0; i2 < size; i2++) {
                if (array[i2] == null) {
                    return i2;
                }
            }
            return -1;
        }

        @Override // java.util.List
        public int lastIndexOf(Object object) {
            int size;
            Object[] array;
            synchronized (this.mutex) {
                size = this.list.size();
                array = new Object[size];
                this.list.toArray(array);
            }
            if (object != null) {
                for (int i = size - 1; i >= 0; i--) {
                    if (object.equals(array[i])) {
                        return i;
                    }
                }
                return -1;
            }
            for (int i2 = size - 1; i2 >= 0; i2--) {
                if (array[i2] == null) {
                    return i2;
                }
            }
            return -1;
        }

        @Override // java.util.List
        public ListIterator<E> listIterator() {
            ListIterator<E> listIterator;
            synchronized (this.mutex) {
                listIterator = this.list.listIterator();
            }
            return listIterator;
        }

        @Override // java.util.List
        public ListIterator<E> listIterator(int location) {
            ListIterator<E> listIterator;
            synchronized (this.mutex) {
                listIterator = this.list.listIterator(location);
            }
            return listIterator;
        }

        @Override // java.util.List
        public E remove(int location) {
            E remove;
            synchronized (this.mutex) {
                remove = this.list.remove(location);
            }
            return remove;
        }

        @Override // java.util.List
        public E set(int location, E object) {
            E e;
            synchronized (this.mutex) {
                e = this.list.set(location, object);
            }
            return e;
        }

        @Override // java.util.List
        public List<E> subList(int start, int end) {
            SynchronizedList synchronizedList;
            synchronized (this.mutex) {
                synchronizedList = new SynchronizedList(this.list.subList(start, end), this.mutex);
            }
            return synchronizedList;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }

        private Object readResolve() {
            if (this.list instanceof RandomAccess) {
                return new SynchronizedRandomAccessList(this.list, this.mutex);
            }
            return this;
        }
    }

    /* loaded from: Collections$SynchronizedMap.class */
    static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = 1978198479659022715L;
        private final Map<K, V> m;
        final Object mutex;

        SynchronizedMap(Map<K, V> map) {
            this.m = map;
            this.mutex = this;
        }

        SynchronizedMap(Map<K, V> map, Object mutex) {
            this.m = map;
            this.mutex = mutex;
        }

        @Override // java.util.Map
        public void clear() {
            synchronized (this.mutex) {
                this.m.clear();
            }
        }

        @Override // java.util.Map
        public boolean containsKey(Object key) {
            boolean containsKey;
            synchronized (this.mutex) {
                containsKey = this.m.containsKey(key);
            }
            return containsKey;
        }

        @Override // java.util.Map
        public boolean containsValue(Object value) {
            boolean containsValue;
            synchronized (this.mutex) {
                containsValue = this.m.containsValue(value);
            }
            return containsValue;
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            SynchronizedSet synchronizedSet;
            synchronized (this.mutex) {
                synchronizedSet = new SynchronizedSet(this.m.entrySet(), this.mutex);
            }
            return synchronizedSet;
        }

        @Override // java.util.Map
        public boolean equals(Object object) {
            boolean equals;
            synchronized (this.mutex) {
                equals = this.m.equals(object);
            }
            return equals;
        }

        @Override // java.util.Map
        public V get(Object key) {
            V v;
            synchronized (this.mutex) {
                v = this.m.get(key);
            }
            return v;
        }

        @Override // java.util.Map
        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.m.hashCode();
            }
            return hashCode;
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            boolean isEmpty;
            synchronized (this.mutex) {
                isEmpty = this.m.isEmpty();
            }
            return isEmpty;
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            SynchronizedSet synchronizedSet;
            synchronized (this.mutex) {
                synchronizedSet = new SynchronizedSet(this.m.keySet(), this.mutex);
            }
            return synchronizedSet;
        }

        @Override // java.util.Map
        public V put(K key, V value) {
            V put;
            synchronized (this.mutex) {
                put = this.m.put(key, value);
            }
            return put;
        }

        @Override // java.util.Map
        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (this.mutex) {
                this.m.putAll(map);
            }
        }

        @Override // java.util.Map
        public V remove(Object key) {
            V remove;
            synchronized (this.mutex) {
                remove = this.m.remove(key);
            }
            return remove;
        }

        @Override // java.util.Map
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.m.size();
            }
            return size;
        }

        @Override // java.util.Map
        public Collection<V> values() {
            SynchronizedCollection synchronizedCollection;
            synchronized (this.mutex) {
                synchronizedCollection = new SynchronizedCollection(this.m.values(), this.mutex);
            }
            return synchronizedCollection;
        }

        public String toString() {
            String obj;
            synchronized (this.mutex) {
                obj = this.m.toString();
            }
            return obj;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    /* loaded from: Collections$SynchronizedSet.class */
    static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
        private static final long serialVersionUID = 487447009682186044L;

        SynchronizedSet(Set<E> set) {
            super(set);
        }

        SynchronizedSet(Set<E> set, Object mutex) {
            super(set, mutex);
        }

        @Override // java.util.Collection
        public boolean equals(Object object) {
            boolean equals;
            synchronized (this.mutex) {
                equals = this.c.equals(object);
            }
            return equals;
        }

        @Override // java.util.Collection
        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.c.hashCode();
            }
            return hashCode;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    /* loaded from: Collections$SynchronizedSortedMap.class */
    static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
        private static final long serialVersionUID = -8798146769416483793L;
        private final SortedMap<K, V> sm;

        SynchronizedSortedMap(SortedMap<K, V> map) {
            super(map);
            this.sm = map;
        }

        SynchronizedSortedMap(SortedMap<K, V> map, Object mutex) {
            super(map, mutex);
            this.sm = map;
        }

        @Override // java.util.SortedMap
        public Comparator<? super K> comparator() {
            Comparator<? super K> comparator;
            synchronized (this.mutex) {
                comparator = this.sm.comparator();
            }
            return comparator;
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            K firstKey;
            synchronized (this.mutex) {
                firstKey = this.sm.firstKey();
            }
            return firstKey;
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> headMap(K endKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.headMap(endKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            K lastKey;
            synchronized (this.mutex) {
                lastKey = this.sm.lastKey();
            }
            return lastKey;
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> subMap(K startKey, K endKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.subMap(startKey, endKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> tailMap(K startKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.tailMap(startKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    /* loaded from: Collections$SynchronizedSortedSet.class */
    static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
        private static final long serialVersionUID = 8695801310862127406L;
        private final SortedSet<E> ss;

        SynchronizedSortedSet(SortedSet<E> set) {
            super(set);
            this.ss = set;
        }

        SynchronizedSortedSet(SortedSet<E> set, Object mutex) {
            super(set, mutex);
            this.ss = set;
        }

        @Override // java.util.SortedSet
        public Comparator<? super E> comparator() {
            Comparator<? super E> comparator;
            synchronized (this.mutex) {
                comparator = this.ss.comparator();
            }
            return comparator;
        }

        @Override // java.util.SortedSet
        public E first() {
            E first;
            synchronized (this.mutex) {
                first = this.ss.first();
            }
            return first;
        }

        @Override // java.util.SortedSet
        public SortedSet<E> headSet(E end) {
            SynchronizedSortedSet synchronizedSortedSet;
            synchronized (this.mutex) {
                synchronizedSortedSet = new SynchronizedSortedSet(this.ss.headSet(end), this.mutex);
            }
            return synchronizedSortedSet;
        }

        @Override // java.util.SortedSet
        public E last() {
            E last;
            synchronized (this.mutex) {
                last = this.ss.last();
            }
            return last;
        }

        @Override // java.util.SortedSet
        public SortedSet<E> subSet(E start, E end) {
            SynchronizedSortedSet synchronizedSortedSet;
            synchronized (this.mutex) {
                synchronizedSortedSet = new SynchronizedSortedSet(this.ss.subSet(start, end), this.mutex);
            }
            return synchronizedSortedSet;
        }

        @Override // java.util.SortedSet
        public SortedSet<E> tailSet(E start) {
            SynchronizedSortedSet synchronizedSortedSet;
            synchronized (this.mutex) {
                synchronizedSortedSet = new SynchronizedSortedSet(this.ss.tailSet(start), this.mutex);
            }
            return synchronizedSortedSet;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    /* loaded from: Collections$UnmodifiableCollection.class */
    private static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 1820017752578914078L;
        final Collection<E> c;

        UnmodifiableCollection(Collection<E> collection) {
            this.c = collection;
        }

        @Override // java.util.Collection
        public boolean add(E object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean contains(Object object) {
            return this.c.contains(object);
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            return this.c.containsAll(collection);
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            return this.c.isEmpty();
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return new Iterator<E>() { // from class: java.util.Collections.UnmodifiableCollection.1
                Iterator<E> iterator;

                {
                    this.iterator = UnmodifiableCollection.this.c.iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.iterator.hasNext();
                }

                @Override // java.util.Iterator
                public E next() {
                    return this.iterator.next();
                }

                @Override // java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override // java.util.Collection
        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection, java.util.List
        public int size() {
            return this.c.size();
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            return this.c.toArray();
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] array) {
            return (T[]) this.c.toArray(array);
        }

        public String toString() {
            return this.c.toString();
        }
    }

    /* loaded from: Collections$UnmodifiableRandomAccessList.class */
    private static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E> implements RandomAccess {
        private static final long serialVersionUID = -2542308836966382001L;

        UnmodifiableRandomAccessList(List<E> l) {
            super(l);
        }

        @Override // java.util.Collections.UnmodifiableList, java.util.List
        public List<E> subList(int start, int end) {
            return new UnmodifiableRandomAccessList(this.list.subList(start, end));
        }

        private Object writeReplace() {
            return new UnmodifiableList(this.list);
        }
    }

    /* loaded from: Collections$UnmodifiableList.class */
    private static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
        private static final long serialVersionUID = -283967356065247728L;
        final List<E> list;

        UnmodifiableList(List<E> l) {
            super(l);
            this.list = l;
        }

        @Override // java.util.List
        public void add(int location, E object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.List
        public boolean addAll(int location, Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean equals(Object object) {
            return this.list.equals(object);
        }

        @Override // java.util.List
        public E get(int location) {
            return this.list.get(location);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return this.list.hashCode();
        }

        @Override // java.util.List
        public int indexOf(Object object) {
            return this.list.indexOf(object);
        }

        @Override // java.util.List
        public int lastIndexOf(Object object) {
            return this.list.lastIndexOf(object);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator(final int location) {
            return new ListIterator<E>() { // from class: java.util.Collections.UnmodifiableList.1
                ListIterator<E> iterator;

                {
                    this.iterator = UnmodifiableList.this.list.listIterator(location);
                }

                @Override // java.util.ListIterator
                public void add(E object) {
                    throw new UnsupportedOperationException();
                }

                @Override // java.util.ListIterator, java.util.Iterator
                public boolean hasNext() {
                    return this.iterator.hasNext();
                }

                @Override // java.util.ListIterator
                public boolean hasPrevious() {
                    return this.iterator.hasPrevious();
                }

                @Override // java.util.ListIterator, java.util.Iterator
                public E next() {
                    return this.iterator.next();
                }

                @Override // java.util.ListIterator
                public int nextIndex() {
                    return this.iterator.nextIndex();
                }

                @Override // java.util.ListIterator
                public E previous() {
                    return this.iterator.previous();
                }

                @Override // java.util.ListIterator
                public int previousIndex() {
                    return this.iterator.previousIndex();
                }

                @Override // java.util.ListIterator, java.util.Iterator
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override // java.util.ListIterator
                public void set(E object) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override // java.util.List
        public E remove(int location) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.List
        public E set(int location, E object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.List
        public List<E> subList(int start, int end) {
            return new UnmodifiableList(this.list.subList(start, end));
        }

        private Object readResolve() {
            if (this.list instanceof RandomAccess) {
                return new UnmodifiableRandomAccessList(this.list);
            }
            return this;
        }
    }

    /* loaded from: Collections$UnmodifiableMap.class */
    private static class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = -1034234728574286014L;
        private final Map<K, V> m;

        /* loaded from: Collections$UnmodifiableMap$UnmodifiableEntrySet.class */
        private static class UnmodifiableEntrySet<K, V> extends UnmodifiableSet<Map.Entry<K, V>> {
            private static final long serialVersionUID = 7854390611657943733L;

            /* JADX INFO: Access modifiers changed from: private */
            /* loaded from: Collections$UnmodifiableMap$UnmodifiableEntrySet$UnmodifiableMapEntry.class */
            public static class UnmodifiableMapEntry<K, V> implements Map.Entry<K, V> {
                Map.Entry<K, V> mapEntry;

                UnmodifiableMapEntry(Map.Entry<K, V> entry) {
                    this.mapEntry = entry;
                }

                @Override // java.util.Map.Entry
                public boolean equals(Object object) {
                    return this.mapEntry.equals(object);
                }

                @Override // java.util.Map.Entry
                public K getKey() {
                    return this.mapEntry.getKey();
                }

                @Override // java.util.Map.Entry
                public V getValue() {
                    return this.mapEntry.getValue();
                }

                @Override // java.util.Map.Entry
                public int hashCode() {
                    return this.mapEntry.hashCode();
                }

                @Override // java.util.Map.Entry
                public V setValue(V object) {
                    throw new UnsupportedOperationException();
                }

                public String toString() {
                    return this.mapEntry.toString();
                }
            }

            UnmodifiableEntrySet(Set<Map.Entry<K, V>> set) {
                super(set);
            }

            @Override // java.util.Collections.UnmodifiableCollection, java.util.Collection, java.lang.Iterable
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>() { // from class: java.util.Collections.UnmodifiableMap.UnmodifiableEntrySet.1
                    Iterator<Map.Entry<K, V>> iterator;

                    {
                        this.iterator = UnmodifiableEntrySet.this.c.iterator();
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override // java.util.Iterator
                    public Map.Entry<K, V> next() {
                        return new UnmodifiableMapEntry(this.iterator.next());
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override // java.util.Collections.UnmodifiableCollection, java.util.Collection
            public Object[] toArray() {
                int length = this.c.size();
                Object[] result = new Object[length];
                Iterator<?> it = iterator();
                int i = length;
                while (true) {
                    i--;
                    if (i >= 0) {
                        result[i] = it.next();
                    } else {
                        return result;
                    }
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r0v18, types: [java.lang.Object[]] */
            @Override // java.util.Collections.UnmodifiableCollection, java.util.Collection
            public <T> T[] toArray(T[] contents) {
                int size = this.c.size();
                int index = 0;
                Iterator<Map.Entry<K, V>> it = iterator();
                if (size > contents.length) {
                    Class<?> ct = contents.getClass().getComponentType();
                    contents = (Object[]) Array.newInstance(ct, size);
                }
                while (index < size) {
                    int i = index;
                    index++;
                    contents[i] = it.next();
                }
                if (index < contents.length) {
                    contents[index] = null;
                }
                return contents;
            }
        }

        UnmodifiableMap(Map<K, V> map) {
            this.m = map;
        }

        @Override // java.util.Map
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public boolean containsKey(Object key) {
            return this.m.containsKey(key);
        }

        @Override // java.util.Map
        public boolean containsValue(Object value) {
            return this.m.containsValue(value);
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            return new UnmodifiableEntrySet(this.m.entrySet());
        }

        @Override // java.util.Map
        public boolean equals(Object object) {
            return this.m.equals(object);
        }

        @Override // java.util.Map
        public V get(Object key) {
            return this.m.get(key);
        }

        @Override // java.util.Map
        public int hashCode() {
            return this.m.hashCode();
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            return new UnmodifiableSet(this.m.keySet());
        }

        @Override // java.util.Map
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public void putAll(Map<? extends K, ? extends V> map) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public int size() {
            return this.m.size();
        }

        @Override // java.util.Map
        public Collection<V> values() {
            return new UnmodifiableCollection(this.m.values());
        }

        public String toString() {
            return this.m.toString();
        }
    }

    /* loaded from: Collections$UnmodifiableSet.class */
    private static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements Set<E> {
        private static final long serialVersionUID = -9215047833775013803L;

        UnmodifiableSet(Set<E> set) {
            super(set);
        }

        @Override // java.util.Collection
        public boolean equals(Object object) {
            return this.c.equals(object);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return this.c.hashCode();
        }
    }

    /* loaded from: Collections$UnmodifiableSortedMap.class */
    private static class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V> implements SortedMap<K, V> {
        private static final long serialVersionUID = -8806743815996713206L;
        private final SortedMap<K, V> sm;

        UnmodifiableSortedMap(SortedMap<K, V> map) {
            super(map);
            this.sm = map;
        }

        @Override // java.util.SortedMap
        public Comparator<? super K> comparator() {
            return this.sm.comparator();
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            return this.sm.firstKey();
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> headMap(K before) {
            return new UnmodifiableSortedMap(this.sm.headMap(before));
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            return this.sm.lastKey();
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> subMap(K start, K end) {
            return new UnmodifiableSortedMap(this.sm.subMap(start, end));
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> tailMap(K after) {
            return new UnmodifiableSortedMap(this.sm.tailMap(after));
        }
    }

    /* loaded from: Collections$UnmodifiableSortedSet.class */
    private static class UnmodifiableSortedSet<E> extends UnmodifiableSet<E> implements SortedSet<E> {
        private static final long serialVersionUID = -4929149591599911165L;
        private final SortedSet<E> ss;

        UnmodifiableSortedSet(SortedSet<E> set) {
            super(set);
            this.ss = set;
        }

        @Override // java.util.SortedSet
        public Comparator<? super E> comparator() {
            return this.ss.comparator();
        }

        @Override // java.util.SortedSet
        public E first() {
            return this.ss.first();
        }

        @Override // java.util.SortedSet
        public SortedSet<E> headSet(E before) {
            return new UnmodifiableSortedSet(this.ss.headSet(before));
        }

        @Override // java.util.SortedSet
        public E last() {
            return this.ss.last();
        }

        @Override // java.util.SortedSet
        public SortedSet<E> subSet(E start, E end) {
            return new UnmodifiableSortedSet(this.ss.subSet(start, end));
        }

        @Override // java.util.SortedSet
        public SortedSet<E> tailSet(E after) {
            return new UnmodifiableSortedSet(this.ss.tailSet(after));
        }
    }

    /* JADX INFO: Add missing generic type declarations: [T] */
    /* renamed from: java.util.Collections$3  reason: invalid class name */
    /* loaded from: Collections$3.class */
    static class AnonymousClass3<T> implements Enumeration<T> {
        Iterator<T> it;
        final /* synthetic */ Collection val$c;

        AnonymousClass3(Collection collection) {
            this.val$c = collection;
            this.it = this.val$c.iterator();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.it.hasNext();
        }

        @Override // java.util.Enumeration
        public T nextElement() {
            return this.it.next();
        }
    }

    /* loaded from: Collections$SetFromMap.class */
    private static class SetFromMap<E> extends AbstractSet<E> implements Serializable {
        private static final long serialVersionUID = 2454657854757543876L;
        private final Map<E, Boolean> m;
        private transient Set<E> backingSet;

        SetFromMap(Map<E, Boolean> map) {
            this.m = map;
            this.backingSet = map.keySet();
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public boolean equals(Object object) {
            return this.backingSet.equals(object);
        }

        @Override // java.util.AbstractSet, java.util.Collection
        public int hashCode() {
            return this.backingSet.hashCode();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean add(E object) {
            return this.m.put(object, Boolean.TRUE) == null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.m.clear();
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            return this.backingSet.toString();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.backingSet.contains(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            return this.backingSet.containsAll(collection);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            return this.m.remove(object) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            return this.backingSet.retainAll(collection);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return this.backingSet.toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] contents) {
            return (T[]) this.backingSet.toArray(contents);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return this.backingSet.iterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.m.size();
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.backingSet = this.m.keySet();
        }
    }

    /* loaded from: Collections$AsLIFOQueue.class */
    private static class AsLIFOQueue<E> extends AbstractQueue<E> implements Serializable {
        private static final long serialVersionUID = 1802017725587941708L;
        private final Deque<E> q;

        AsLIFOQueue(Deque<E> deque) {
            this.q = deque;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return this.q.iterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.q.size();
        }

        @Override // java.util.Queue
        public boolean offer(E o) {
            return this.q.offerFirst(o);
        }

        @Override // java.util.Queue
        public E peek() {
            return this.q.peekFirst();
        }

        @Override // java.util.Queue
        public E poll() {
            return this.q.pollFirst();
        }

        @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
        public boolean add(E o) {
            this.q.push(o);
            return true;
        }

        @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.q.clear();
        }

        @Override // java.util.AbstractQueue, java.util.Queue
        public E element() {
            return this.q.getFirst();
        }

        @Override // java.util.AbstractQueue, java.util.Queue
        public E remove() {
            return this.q.pop();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.q.contains(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            return this.q.containsAll(collection);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.q.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            return this.q.remove(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            return this.q.removeAll(collection);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            return this.q.retainAll(collection);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return this.q.toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] contents) {
            return (T[]) this.q.toArray(contents);
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            return this.q.toString();
        }
    }

    /* loaded from: Collections$CheckedCollection.class */
    private static class CheckedCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 1578914078182001775L;
        final Collection<E> c;
        final Class<E> type;

        public CheckedCollection(Collection<E> c, Class<E> type) {
            if (c == null) {
                throw new NullPointerException("c == null");
            }
            if (type == null) {
                throw new NullPointerException("type == null");
            }
            this.c = c;
            this.type = type;
        }

        @Override // java.util.Collection, java.util.List
        public int size() {
            return this.c.size();
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            return this.c.isEmpty();
        }

        @Override // java.util.Collection
        public boolean contains(Object obj) {
            return this.c.contains(obj);
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            Iterator<E> i = this.c.iterator();
            if (i instanceof ListIterator) {
                i = new CheckedListIterator<>((ListIterator) i, this.type);
            }
            return i;
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            return this.c.toArray();
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] arr) {
            return (T[]) this.c.toArray(arr);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Collection
        public boolean add(E obj) {
            return this.c.add(Collections.checkType(obj, this.type));
        }

        @Override // java.util.Collection
        public boolean remove(Object obj) {
            return this.c.remove(obj);
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> c1) {
            return this.c.containsAll(c1);
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends E> c1) {
            Object[] array = c1.toArray();
            for (Object o : array) {
                Collections.checkType(o, this.type);
            }
            return this.c.addAll(Arrays.asList(array));
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> c1) {
            return this.c.removeAll(c1);
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> c1) {
            return this.c.retainAll(c1);
        }

        @Override // java.util.Collection
        public void clear() {
            this.c.clear();
        }

        public String toString() {
            return this.c.toString();
        }
    }

    /* loaded from: Collections$CheckedListIterator.class */
    private static class CheckedListIterator<E> implements ListIterator<E> {
        private final ListIterator<E> i;
        private final Class<E> type;

        public CheckedListIterator(ListIterator<E> i, Class<E> type) {
            this.i = i;
            this.type = type;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public E next() {
            return this.i.next();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            this.i.remove();
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override // java.util.ListIterator
        public E previous() {
            return this.i.previous();
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.i.previousIndex();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.ListIterator
        public void set(E obj) {
            this.i.set(Collections.checkType(obj, this.type));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.ListIterator
        public void add(E obj) {
            this.i.add(Collections.checkType(obj, this.type));
        }
    }

    /* loaded from: Collections$CheckedList.class */
    private static class CheckedList<E> extends CheckedCollection<E> implements List<E> {
        private static final long serialVersionUID = 65247728283967356L;
        final List<E> l;

        public CheckedList(List<E> l, Class<E> type) {
            super(l, type);
            this.l = l;
        }

        @Override // java.util.List
        public boolean addAll(int index, Collection<? extends E> c1) {
            Object[] array = c1.toArray();
            for (Object o : array) {
                Collections.checkType(o, this.type);
            }
            return this.l.addAll(index, Arrays.asList(array));
        }

        @Override // java.util.List
        public E get(int index) {
            return this.l.get(index);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.List
        public E set(int index, E obj) {
            return (E) this.l.set(index, Collections.checkType(obj, this.type));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.List
        public void add(int index, E obj) {
            this.l.add(index, Collections.checkType(obj, this.type));
        }

        @Override // java.util.List
        public E remove(int index) {
            return this.l.remove(index);
        }

        @Override // java.util.List
        public int indexOf(Object obj) {
            return this.l.indexOf(obj);
        }

        @Override // java.util.List
        public int lastIndexOf(Object obj) {
            return this.l.lastIndexOf(obj);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator() {
            return new CheckedListIterator(this.l.listIterator(), this.type);
        }

        @Override // java.util.List
        public ListIterator<E> listIterator(int index) {
            return new CheckedListIterator(this.l.listIterator(index), this.type);
        }

        @Override // java.util.List
        public List<E> subList(int fromIndex, int toIndex) {
            return Collections.checkedList(this.l.subList(fromIndex, toIndex), this.type);
        }

        @Override // java.util.Collection
        public boolean equals(Object obj) {
            return this.l.equals(obj);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return this.l.hashCode();
        }
    }

    /* loaded from: Collections$CheckedRandomAccessList.class */
    private static class CheckedRandomAccessList<E> extends CheckedList<E> implements RandomAccess {
        private static final long serialVersionUID = 1638200125423088369L;

        public CheckedRandomAccessList(List<E> l, Class<E> type) {
            super(l, type);
        }
    }

    /* loaded from: Collections$CheckedSet.class */
    private static class CheckedSet<E> extends CheckedCollection<E> implements Set<E> {
        private static final long serialVersionUID = 4694047833775013803L;

        public CheckedSet(Set<E> s, Class<E> type) {
            super(s, type);
        }

        @Override // java.util.Collection
        public boolean equals(Object obj) {
            return this.c.equals(obj);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return this.c.hashCode();
        }
    }

    /* loaded from: Collections$CheckedMap.class */
    private static class CheckedMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = 5742860141034234728L;
        final Map<K, V> m;
        final Class<K> keyType;
        final Class<V> valueType;

        /* synthetic */ CheckedMap(Map x0, Class x1, Class x2, AnonymousClass1 x3) {
            this(x0, x1, x2);
        }

        private CheckedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
            if (m == null) {
                throw new NullPointerException("m == null");
            }
            if (keyType == null) {
                throw new NullPointerException("keyType == null");
            }
            if (valueType == null) {
                throw new NullPointerException("valueType == null");
            }
            this.m = m;
            this.keyType = keyType;
            this.valueType = valueType;
        }

        @Override // java.util.Map
        public int size() {
            return this.m.size();
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override // java.util.Map
        public boolean containsKey(Object key) {
            return this.m.containsKey(key);
        }

        @Override // java.util.Map
        public boolean containsValue(Object value) {
            return this.m.containsValue(value);
        }

        @Override // java.util.Map
        public V get(Object key) {
            return this.m.get(key);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map
        public V put(K key, V value) {
            return (V) this.m.put(Collections.checkType(key, this.keyType), Collections.checkType(value, this.valueType));
        }

        @Override // java.util.Map
        public V remove(Object key) {
            return this.m.remove(key);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map
        public void putAll(Map<? extends K, ? extends V> map) {
            int size = map.size();
            if (size == 0) {
                return;
            }
            Map.Entry<? extends K, ? extends V>[] entries = new Map.Entry[size];
            Iterator<? extends Map.Entry<? extends K, ? extends V>> it = map.entrySet().iterator();
            for (int i = 0; i < size; i++) {
                Map.Entry<? extends K, ? extends V> e = it.next();
                Collections.checkType(e.getKey(), this.keyType);
                Collections.checkType(e.getValue(), this.valueType);
                entries[i] = e;
            }
            for (int i2 = 0; i2 < size; i2++) {
                this.m.put(entries[i2].getKey(), entries[i2].getValue());
            }
        }

        @Override // java.util.Map
        public void clear() {
            this.m.clear();
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            return this.m.keySet();
        }

        @Override // java.util.Map
        public Collection<V> values() {
            return this.m.values();
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            return new CheckedEntrySet(this.m.entrySet(), this.valueType);
        }

        @Override // java.util.Map
        public boolean equals(Object obj) {
            return this.m.equals(obj);
        }

        @Override // java.util.Map
        public int hashCode() {
            return this.m.hashCode();
        }

        public String toString() {
            return this.m.toString();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: Collections$CheckedMap$CheckedEntry.class */
        public static class CheckedEntry<K, V> implements Map.Entry<K, V> {
            final Map.Entry<K, V> e;
            final Class<V> valueType;

            public CheckedEntry(Map.Entry<K, V> e, Class<V> valueType) {
                if (e == null) {
                    throw new NullPointerException("e == null");
                }
                this.e = e;
                this.valueType = valueType;
            }

            @Override // java.util.Map.Entry
            public K getKey() {
                return this.e.getKey();
            }

            @Override // java.util.Map.Entry
            public V getValue() {
                return this.e.getValue();
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Map.Entry
            public V setValue(V obj) {
                return (V) this.e.setValue(Collections.checkType(obj, this.valueType));
            }

            @Override // java.util.Map.Entry
            public boolean equals(Object obj) {
                return this.e.equals(obj);
            }

            @Override // java.util.Map.Entry
            public int hashCode() {
                return this.e.hashCode();
            }
        }

        /* loaded from: Collections$CheckedMap$CheckedEntrySet.class */
        private static class CheckedEntrySet<K, V> implements Set<Map.Entry<K, V>> {
            final Set<Map.Entry<K, V>> s;
            final Class<V> valueType;

            @Override // java.util.Set, java.util.Collection
            public /* bridge */ /* synthetic */ boolean add(Object x0) {
                return add((Map.Entry) ((Map.Entry) x0));
            }

            public CheckedEntrySet(Set<Map.Entry<K, V>> s, Class<V> valueType) {
                this.s = s;
                this.valueType = valueType;
            }

            @Override // java.util.Set, java.util.Collection, java.lang.Iterable
            public Iterator<Map.Entry<K, V>> iterator() {
                return new CheckedEntryIterator(this.s.iterator(), this.valueType);
            }

            @Override // java.util.Set, java.util.Collection
            public Object[] toArray() {
                int thisSize = size();
                Object[] array = new Object[thisSize];
                Iterator<?> it = iterator();
                for (int i = 0; i < thisSize; i++) {
                    array[i] = it.next();
                }
                return array;
            }

            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Type inference failed for: r0v18, types: [java.lang.Object[]] */
            @Override // java.util.Set, java.util.Collection
            public <T> T[] toArray(T[] array) {
                int thisSize = size();
                if (array.length < thisSize) {
                    Class<?> ct = array.getClass().getComponentType();
                    array = (Object[]) Array.newInstance(ct, thisSize);
                }
                Iterator<?> it = iterator();
                for (int i = 0; i < thisSize; i++) {
                    array[i] = it.next();
                }
                if (thisSize < array.length) {
                    array[thisSize] = null;
                }
                return array;
            }

            @Override // java.util.Set, java.util.Collection
            public boolean retainAll(Collection<?> c) {
                return this.s.retainAll(c);
            }

            @Override // java.util.Set, java.util.Collection
            public boolean removeAll(Collection<?> c) {
                return this.s.removeAll(c);
            }

            @Override // java.util.Set, java.util.Collection
            public boolean containsAll(Collection<?> c) {
                return this.s.containsAll(c);
            }

            @Override // java.util.Set, java.util.Collection
            public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.Set, java.util.Collection
            public boolean remove(Object o) {
                return this.s.remove(o);
            }

            @Override // java.util.Set, java.util.Collection
            public boolean contains(Object o) {
                return this.s.contains(o);
            }

            public boolean add(Map.Entry<K, V> o) {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.Set, java.util.Collection
            public boolean isEmpty() {
                return this.s.isEmpty();
            }

            @Override // java.util.Set, java.util.Collection
            public void clear() {
                this.s.clear();
            }

            @Override // java.util.Set, java.util.Collection, java.util.List
            public int size() {
                return this.s.size();
            }

            @Override // java.util.Set, java.util.Collection
            public int hashCode() {
                return this.s.hashCode();
            }

            @Override // java.util.Set, java.util.Collection
            public boolean equals(Object object) {
                return this.s.equals(object);
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* loaded from: Collections$CheckedMap$CheckedEntrySet$CheckedEntryIterator.class */
            public static class CheckedEntryIterator<K, V> implements Iterator<Map.Entry<K, V>> {
                Iterator<Map.Entry<K, V>> i;
                Class<V> valueType;

                public CheckedEntryIterator(Iterator<Map.Entry<K, V>> i, Class<V> valueType) {
                    this.i = i;
                    this.valueType = valueType;
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.i.hasNext();
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.i.remove();
                }

                @Override // java.util.Iterator
                public Map.Entry<K, V> next() {
                    return new CheckedEntry(this.i.next(), this.valueType);
                }
            }
        }
    }

    /* loaded from: Collections$CheckedSortedSet.class */
    private static class CheckedSortedSet<E> extends CheckedSet<E> implements SortedSet<E> {
        private static final long serialVersionUID = 1599911165492914959L;
        private final SortedSet<E> ss;

        public CheckedSortedSet(SortedSet<E> s, Class<E> type) {
            super(s, type);
            this.ss = s;
        }

        @Override // java.util.SortedSet
        public Comparator<? super E> comparator() {
            return this.ss.comparator();
        }

        @Override // java.util.SortedSet
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return new CheckedSortedSet(this.ss.subSet(fromElement, toElement), this.type);
        }

        @Override // java.util.SortedSet
        public SortedSet<E> headSet(E toElement) {
            return new CheckedSortedSet(this.ss.headSet(toElement), this.type);
        }

        @Override // java.util.SortedSet
        public SortedSet<E> tailSet(E fromElement) {
            return new CheckedSortedSet(this.ss.tailSet(fromElement), this.type);
        }

        @Override // java.util.SortedSet
        public E first() {
            return this.ss.first();
        }

        @Override // java.util.SortedSet
        public E last() {
            return this.ss.last();
        }
    }

    /* loaded from: Collections$CheckedSortedMap.class */
    private static class CheckedSortedMap<K, V> extends CheckedMap<K, V> implements SortedMap<K, V> {
        private static final long serialVersionUID = 1599671320688067438L;
        final SortedMap<K, V> sm;

        CheckedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
            super(m, keyType, valueType, null);
            this.sm = m;
        }

        @Override // java.util.SortedMap
        public Comparator<? super K> comparator() {
            return this.sm.comparator();
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return new CheckedSortedMap(this.sm.subMap(fromKey, toKey), this.keyType, this.valueType);
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> headMap(K toKey) {
            return new CheckedSortedMap(this.sm.headMap(toKey), this.keyType, this.valueType);
        }

        @Override // java.util.SortedMap
        public SortedMap<K, V> tailMap(K fromKey) {
            return new CheckedSortedMap(this.sm.tailMap(fromKey), this.keyType, this.valueType);
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            return this.sm.firstKey();
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            return this.sm.lastKey();
        }
    }
}