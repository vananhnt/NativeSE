package java.util;

import gov.nist.core.Separators;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TreeMap.class */
public class TreeMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V>, NavigableMap<K, V>, Cloneable, Serializable {
    public TreeMap() {
        throw new RuntimeException("Stub!");
    }

    public TreeMap(Map<? extends K, ? extends V> copyFrom) {
        throw new RuntimeException("Stub!");
    }

    public TreeMap(Comparator<? super K> comparator) {
        throw new RuntimeException("Stub!");
    }

    public TreeMap(SortedMap<K, ? extends V> copyFrom) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap
    public Object clone() {
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
    public V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> firstEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> pollFirstEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public K firstKey() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> lastEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public Map.Entry<K, V> pollLastEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public K lastKey() {
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

    @Override // java.util.SortedMap
    public Comparator<? super K> comparator() {
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

    @Override // java.util.NavigableMap
    public NavigableSet<K> navigableKeySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public NavigableMap<K, V> subMap(K from, boolean fromInclusive, K to, boolean toInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> subMap(K fromInclusive, K toExclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public NavigableMap<K, V> headMap(K to, boolean inclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> headMap(K toExclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public NavigableMap<K, V> tailMap(K from, boolean inclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedMap
    public SortedMap<K, V> tailMap(K fromInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public NavigableMap<K, V> descendingMap() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableMap
    public NavigableSet<K> descendingKeySet() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.TreeMap$1  reason: invalid class name */
    /* loaded from: TreeMap$1.class */
    static class AnonymousClass1 implements Comparator<Comparable> {
        AnonymousClass1() {
        }

        @Override // java.util.Comparator
        public int compare(Comparable a, Comparable b) {
            return a.compareTo(b);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TreeMap$Relation.class */
    public enum Relation {
        LOWER,
        FLOOR,
        EQUAL,
        CREATE,
        CEILING,
        HIGHER;

        Relation forOrder(boolean ascending) {
            if (ascending) {
                return this;
            }
            switch (this) {
                case LOWER:
                    return HIGHER;
                case FLOOR:
                    return CEILING;
                case EQUAL:
                    return EQUAL;
                case CEILING:
                    return FLOOR;
                case HIGHER:
                    return LOWER;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TreeMap$Node.class */
    public static class Node<K, V> implements Map.Entry<K, V> {
        Node<K, V> parent;
        Node<K, V> left;
        Node<K, V> right;
        final K key;
        V value;
        int height = 1;

        Node(Node<K, V> parent, K key) {
            this.parent = parent;
            this.key = key;
        }

        Node<K, V> copy(Node<K, V> parent) {
            Node<K, V> result = new Node<>(parent, this.key);
            if (this.left != null) {
                result.left = this.left.copy(result);
            }
            if (this.right != null) {
                result.right = this.right.copy(result);
            }
            result.value = this.value;
            result.height = this.height;
            return result;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry other = (Map.Entry) o;
                if (this.key != null ? this.key.equals(other.getKey()) : other.getKey() == null) {
                    if (this.value != null ? this.value.equals(other.getValue()) : other.getValue() == null) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + Separators.EQUALS + this.value;
        }

        Node<K, V> next() {
            if (this.right != null) {
                return this.right.first();
            }
            Node<K, V> node = this;
            Node<K, V> node2 = node.parent;
            while (true) {
                Node<K, V> parent = node2;
                if (parent != null) {
                    if (parent.left == node) {
                        return parent;
                    }
                    node = parent;
                    node2 = node.parent;
                } else {
                    return null;
                }
            }
        }

        public Node<K, V> prev() {
            if (this.left != null) {
                return this.left.last();
            }
            Node<K, V> node = this;
            Node<K, V> node2 = node.parent;
            while (true) {
                Node<K, V> parent = node2;
                if (parent != null) {
                    if (parent.right == node) {
                        return parent;
                    }
                    node = parent;
                    node2 = node.parent;
                } else {
                    return null;
                }
            }
        }

        public Node<K, V> first() {
            Node<K, V> node = this;
            Node<K, V> node2 = node.left;
            while (true) {
                Node<K, V> child = node2;
                if (child != null) {
                    node = child;
                    node2 = node.left;
                } else {
                    return node;
                }
            }
        }

        public Node<K, V> last() {
            Node<K, V> node = this;
            Node<K, V> node2 = node.right;
            while (true) {
                Node<K, V> child = node2;
                if (child != null) {
                    node = child;
                    node2 = node.right;
                } else {
                    return node;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TreeMap$MapIterator.class */
    public abstract class MapIterator<T> implements Iterator<T> {
        protected Node<K, V> next;
        protected Node<K, V> last;
        protected int expectedModCount;

        MapIterator(Node<K, V> next) {
            this.expectedModCount = TreeMap.this.modCount;
            this.next = next;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        protected Node<K, V> stepForward() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            if (TreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.last = this.next;
            this.next = this.next.next();
            return this.last;
        }

        protected Node<K, V> stepBackward() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            if (TreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.last = this.next;
            this.next = this.next.prev();
            return this.last;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            TreeMap.this.removeInternal(this.last);
            this.expectedModCount = TreeMap.this.modCount;
            this.last = null;
        }
    }

    /* loaded from: TreeMap$EntrySet.class */
    class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return TreeMap.this.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K, V>> iterator() {
            return new TreeMap<K, V>.MapIterator<Map.Entry<K, V>>(TreeMap.this.root == null ? null : TreeMap.this.root.first()) { // from class: java.util.TreeMap.EntrySet.1
                {
                    TreeMap treeMap = TreeMap.this;
                }

                @Override // java.util.Iterator
                public Map.Entry<K, V> next() {
                    return stepForward();
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return (o instanceof Map.Entry) && TreeMap.this.findByEntry((Map.Entry) o) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            Node<K, V> node;
            if (!(o instanceof Map.Entry) || (node = TreeMap.this.findByEntry((Map.Entry) o)) == null) {
                return false;
            }
            TreeMap.this.removeInternal(node);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            TreeMap.this.clear();
        }
    }

    /* loaded from: TreeMap$KeySet.class */
    class KeySet extends AbstractSet<K> implements NavigableSet<K> {
        KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return TreeMap.this.size;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new TreeMap<K, V>.MapIterator<K>(TreeMap.this.root == null ? null : TreeMap.this.root.first()) { // from class: java.util.TreeMap.KeySet.1
                {
                    TreeMap treeMap = TreeMap.this;
                }

                @Override // java.util.Iterator
                public K next() {
                    return stepForward().key;
                }
            };
        }

        @Override // java.util.NavigableSet
        public Iterator<K> descendingIterator() {
            return new TreeMap<K, V>.MapIterator<K>(TreeMap.this.root == null ? null : TreeMap.this.root.last()) { // from class: java.util.TreeMap.KeySet.2
                {
                    TreeMap treeMap = TreeMap.this;
                }

                @Override // java.util.Iterator
                public K next() {
                    return stepBackward().key;
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return TreeMap.this.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object key) {
            return TreeMap.this.removeInternalByKey(key) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            TreeMap.this.clear();
        }

        @Override // java.util.SortedSet
        public Comparator<? super K> comparator() {
            return TreeMap.this.comparator();
        }

        @Override // java.util.SortedSet
        public K first() {
            return (K) TreeMap.this.firstKey();
        }

        @Override // java.util.SortedSet
        public K last() {
            return (K) TreeMap.this.lastKey();
        }

        @Override // java.util.NavigableSet
        public K lower(K key) {
            return (K) TreeMap.this.lowerKey(key);
        }

        @Override // java.util.NavigableSet
        public K floor(K key) {
            return (K) TreeMap.this.floorKey(key);
        }

        @Override // java.util.NavigableSet
        public K ceiling(K key) {
            return (K) TreeMap.this.ceilingKey(key);
        }

        @Override // java.util.NavigableSet
        public K higher(K key) {
            return (K) TreeMap.this.higherKey(key);
        }

        @Override // java.util.NavigableSet
        public K pollFirst() {
            Map.Entry<K, V> entry = TreeMap.access$000(TreeMap.this);
            if (entry != null) {
                return entry.getKey();
            }
            return null;
        }

        @Override // java.util.NavigableSet
        public K pollLast() {
            Map.Entry<K, V> entry = TreeMap.access$100(TreeMap.this);
            if (entry != null) {
                return entry.getKey();
            }
            return null;
        }

        @Override // java.util.NavigableSet
        public NavigableSet<K> subSet(K from, boolean fromInclusive, K to, boolean toInclusive) {
            return TreeMap.this.subMap(from, fromInclusive, to, toInclusive).navigableKeySet();
        }

        @Override // java.util.NavigableSet, java.util.SortedSet
        public SortedSet<K> subSet(K fromInclusive, K toExclusive) {
            return TreeMap.this.subMap(fromInclusive, true, toExclusive, false).navigableKeySet();
        }

        @Override // java.util.NavigableSet
        public NavigableSet<K> headSet(K to, boolean inclusive) {
            return TreeMap.this.headMap(to, inclusive).navigableKeySet();
        }

        @Override // java.util.NavigableSet, java.util.SortedSet
        public SortedSet<K> headSet(K toExclusive) {
            return TreeMap.this.headMap(toExclusive, false).navigableKeySet();
        }

        @Override // java.util.NavigableSet
        public NavigableSet<K> tailSet(K from, boolean inclusive) {
            return TreeMap.this.tailMap(from, inclusive).navigableKeySet();
        }

        @Override // java.util.NavigableSet, java.util.SortedSet
        public SortedSet<K> tailSet(K fromInclusive) {
            return TreeMap.this.tailMap(fromInclusive, true).navigableKeySet();
        }

        @Override // java.util.NavigableSet
        public NavigableSet<K> descendingSet() {
            return new BoundedMap(false, null, Bound.NO_BOUND, null, Bound.NO_BOUND).navigableKeySet();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TreeMap$Bound.class */
    public enum Bound {
        INCLUSIVE { // from class: java.util.TreeMap.Bound.1
            @Override // java.util.TreeMap.Bound
            public String leftCap(Object from) {
                return "[" + from;
            }

            @Override // java.util.TreeMap.Bound
            public String rightCap(Object to) {
                return to + "]";
            }
        },
        EXCLUSIVE { // from class: java.util.TreeMap.Bound.2
            @Override // java.util.TreeMap.Bound
            public String leftCap(Object from) {
                return Separators.LPAREN + from;
            }

            @Override // java.util.TreeMap.Bound
            public String rightCap(Object to) {
                return to + Separators.RPAREN;
            }
        },
        NO_BOUND { // from class: java.util.TreeMap.Bound.3
            @Override // java.util.TreeMap.Bound
            public String leftCap(Object from) {
                return Separators.DOT;
            }

            @Override // java.util.TreeMap.Bound
            public String rightCap(Object to) {
                return Separators.DOT;
            }
        };

        public abstract String leftCap(Object obj);

        public abstract String rightCap(Object obj);

        /* synthetic */ Bound(AnonymousClass1 x2) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TreeMap$BoundedMap.class */
    public final class BoundedMap extends AbstractMap<K, V> implements NavigableMap<K, V>, Serializable {
        private final transient boolean ascending;
        private final transient K from;
        private final transient Bound fromBound;
        private final transient K to;
        private final transient Bound toBound;
        private transient TreeMap<K, V>.BoundedMap.BoundedEntrySet entrySet;
        private transient TreeMap<K, V>.BoundedMap.BoundedKeySet keySet;

        @Override // java.util.NavigableMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ SortedMap tailMap(Object x0) {
            return tailMap((BoundedMap) x0);
        }

        @Override // java.util.NavigableMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ SortedMap headMap(Object x0) {
            return headMap((BoundedMap) x0);
        }

        BoundedMap(boolean ascending, K from, Bound fromBound, K to, Bound toBound) {
            if (fromBound != Bound.NO_BOUND && toBound != Bound.NO_BOUND) {
                if (TreeMap.this.comparator.compare(from, to) > 0) {
                    throw new IllegalArgumentException(from + " > " + to);
                }
            } else if (fromBound != Bound.NO_BOUND) {
                TreeMap.this.comparator.compare(from, from);
            } else if (toBound != Bound.NO_BOUND) {
                TreeMap.this.comparator.compare(to, to);
            }
            this.ascending = ascending;
            this.from = from;
            this.fromBound = fromBound;
            this.to = to;
            this.toBound = toBound;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public int size() {
            return TreeMap.count(entrySet().iterator());
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean isEmpty() {
            return endpoint(true) == null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V get(Object key) {
            if (isInBounds(key)) {
                return (V) TreeMap.this.get(key);
            }
            return null;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            return isInBounds(key) && TreeMap.this.containsKey(key);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V put(K key, V value) {
            if (!isInBounds(key)) {
                throw outOfBounds(key, this.fromBound, this.toBound);
            }
            return (V) TreeMap.this.putInternal(key, value);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public V remove(Object key) {
            if (isInBounds(key)) {
                return (V) TreeMap.this.remove(key);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isInBounds(Object key) {
            return isInBounds(key, this.fromBound, this.toBound);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isInBounds(K key, Bound fromBound, Bound toBound) {
            if (fromBound == Bound.INCLUSIVE) {
                if (TreeMap.this.comparator.compare(key, this.from) < 0) {
                    return false;
                }
            } else if (fromBound == Bound.EXCLUSIVE && TreeMap.this.comparator.compare(key, this.from) <= 0) {
                return false;
            }
            if (toBound == Bound.INCLUSIVE) {
                if (TreeMap.this.comparator.compare(key, this.to) > 0) {
                    return false;
                }
                return true;
            } else if (toBound == Bound.EXCLUSIVE && TreeMap.this.comparator.compare(key, this.to) >= 0) {
                return false;
            } else {
                return true;
            }
        }

        private Node<K, V> bound(Node<K, V> node, Bound fromBound, Bound toBound) {
            if (node == null || !isInBounds(node.getKey(), fromBound, toBound)) {
                return null;
            }
            return node;
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> firstEntry() {
            return TreeMap.access$300(TreeMap.this, endpoint(true));
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> pollFirstEntry() {
            Node<K, V> result = endpoint(true);
            if (result != null) {
                TreeMap.this.removeInternal(result);
            }
            return TreeMap.access$300(TreeMap.this, result);
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            Map.Entry<K, V> entry = endpoint(true);
            if (entry == null) {
                throw new NoSuchElementException();
            }
            return entry.getKey();
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> lastEntry() {
            return TreeMap.access$300(TreeMap.this, endpoint(false));
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> pollLastEntry() {
            Node<K, V> result = endpoint(false);
            if (result != null) {
                TreeMap.this.removeInternal(result);
            }
            return TreeMap.access$300(TreeMap.this, result);
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            Map.Entry<K, V> entry = endpoint(false);
            if (entry == null) {
                throw new NoSuchElementException();
            }
            return entry.getKey();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node<K, V> endpoint(boolean first) {
            Node<K, V> node;
            Node<K, V> node2;
            if (this.ascending == first) {
                switch (this.fromBound) {
                    case NO_BOUND:
                        node2 = TreeMap.this.root == null ? null : TreeMap.this.root.first();
                        break;
                    case INCLUSIVE:
                        node2 = TreeMap.this.find(this.from, Relation.CEILING);
                        break;
                    case EXCLUSIVE:
                        node2 = TreeMap.this.find(this.from, Relation.HIGHER);
                        break;
                    default:
                        throw new AssertionError();
                }
                return bound(node2, Bound.NO_BOUND, this.toBound);
            }
            switch (this.toBound) {
                case NO_BOUND:
                    node = TreeMap.this.root == null ? null : TreeMap.this.root.last();
                    break;
                case INCLUSIVE:
                    node = TreeMap.this.find(this.to, Relation.FLOOR);
                    break;
                case EXCLUSIVE:
                    node = TreeMap.this.find(this.to, Relation.LOWER);
                    break;
                default:
                    throw new AssertionError();
            }
            return bound(node, this.fromBound, Bound.NO_BOUND);
        }

        private Map.Entry<K, V> findBounded(K key, Relation relation) {
            Relation relation2 = relation.forOrder(this.ascending);
            Bound fromBoundForCheck = this.fromBound;
            Bound toBoundForCheck = this.toBound;
            if (this.toBound != Bound.NO_BOUND && (relation2 == Relation.LOWER || relation2 == Relation.FLOOR)) {
                int comparison = TreeMap.this.comparator.compare(this.to, key);
                if (comparison <= 0) {
                    key = this.to;
                    if (this.toBound == Bound.EXCLUSIVE) {
                        relation2 = Relation.LOWER;
                    } else if (comparison < 0) {
                        relation2 = Relation.FLOOR;
                    }
                }
                toBoundForCheck = Bound.NO_BOUND;
            }
            if (this.fromBound != Bound.NO_BOUND && (relation2 == Relation.CEILING || relation2 == Relation.HIGHER)) {
                int comparison2 = TreeMap.this.comparator.compare(this.from, key);
                if (comparison2 >= 0) {
                    key = this.from;
                    if (this.fromBound == Bound.EXCLUSIVE) {
                        relation2 = Relation.HIGHER;
                    } else if (comparison2 > 0) {
                        relation2 = Relation.CEILING;
                    }
                }
                fromBoundForCheck = Bound.NO_BOUND;
            }
            return bound(TreeMap.this.find(key, relation2), fromBoundForCheck, toBoundForCheck);
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> lowerEntry(K key) {
            return TreeMap.access$300(TreeMap.this, findBounded(key, Relation.LOWER));
        }

        @Override // java.util.NavigableMap
        public K lowerKey(K key) {
            Map.Entry<K, V> entry = findBounded(key, Relation.LOWER);
            if (entry != null) {
                return entry.getKey();
            }
            return null;
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> floorEntry(K key) {
            return TreeMap.access$300(TreeMap.this, findBounded(key, Relation.FLOOR));
        }

        @Override // java.util.NavigableMap
        public K floorKey(K key) {
            Map.Entry<K, V> entry = findBounded(key, Relation.FLOOR);
            if (entry != null) {
                return entry.getKey();
            }
            return null;
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> ceilingEntry(K key) {
            return TreeMap.access$300(TreeMap.this, findBounded(key, Relation.CEILING));
        }

        @Override // java.util.NavigableMap
        public K ceilingKey(K key) {
            Map.Entry<K, V> entry = findBounded(key, Relation.CEILING);
            if (entry != null) {
                return entry.getKey();
            }
            return null;
        }

        @Override // java.util.NavigableMap
        public Map.Entry<K, V> higherEntry(K key) {
            return TreeMap.access$300(TreeMap.this, findBounded(key, Relation.HIGHER));
        }

        @Override // java.util.NavigableMap
        public K higherKey(K key) {
            Map.Entry<K, V> entry = findBounded(key, Relation.HIGHER);
            if (entry != null) {
                return entry.getKey();
            }
            return null;
        }

        @Override // java.util.SortedMap
        public Comparator<? super K> comparator() {
            Comparator<? super K> forward = TreeMap.this.comparator();
            if (this.ascending) {
                return forward;
            }
            return Collections.reverseOrder(forward);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            TreeMap<K, V>.BoundedMap.BoundedEntrySet result = this.entrySet;
            if (result != null) {
                return result;
            }
            TreeMap<K, V>.BoundedMap.BoundedEntrySet boundedEntrySet = new BoundedEntrySet();
            this.entrySet = boundedEntrySet;
            return boundedEntrySet;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<K> keySet() {
            return navigableKeySet();
        }

        @Override // java.util.NavigableMap
        public NavigableSet<K> navigableKeySet() {
            TreeMap<K, V>.BoundedMap.BoundedKeySet result = this.keySet;
            if (result != null) {
                return result;
            }
            TreeMap<K, V>.BoundedMap.BoundedKeySet boundedKeySet = new BoundedKeySet();
            this.keySet = boundedKeySet;
            return boundedKeySet;
        }

        @Override // java.util.NavigableMap
        public NavigableMap<K, V> descendingMap() {
            return new BoundedMap(!this.ascending, this.from, this.fromBound, this.to, this.toBound);
        }

        @Override // java.util.NavigableMap
        public NavigableSet<K> descendingKeySet() {
            return new BoundedMap(!this.ascending, this.from, this.fromBound, this.to, this.toBound).navigableKeySet();
        }

        @Override // java.util.NavigableMap
        public NavigableMap<K, V> subMap(K from, boolean fromInclusive, K to, boolean toInclusive) {
            Bound fromBound = fromInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE;
            Bound toBound = toInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE;
            return subMap((Bound) from, fromBound, (Bound) to, toBound);
        }

        @Override // java.util.NavigableMap, java.util.SortedMap
        public NavigableMap<K, V> subMap(K fromInclusive, K toExclusive) {
            return subMap((Bound) fromInclusive, Bound.INCLUSIVE, (Bound) toExclusive, Bound.EXCLUSIVE);
        }

        @Override // java.util.NavigableMap
        public NavigableMap<K, V> headMap(K to, boolean inclusive) {
            Bound toBound = inclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE;
            return subMap((Bound) null, Bound.NO_BOUND, (Bound) to, toBound);
        }

        @Override // java.util.NavigableMap, java.util.SortedMap
        public NavigableMap<K, V> headMap(K toExclusive) {
            return subMap((Bound) null, Bound.NO_BOUND, (Bound) toExclusive, Bound.EXCLUSIVE);
        }

        @Override // java.util.NavigableMap
        public NavigableMap<K, V> tailMap(K from, boolean inclusive) {
            Bound fromBound = inclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE;
            return subMap((Bound) from, fromBound, (Bound) null, Bound.NO_BOUND);
        }

        @Override // java.util.NavigableMap, java.util.SortedMap
        public NavigableMap<K, V> tailMap(K fromInclusive) {
            return subMap((Bound) fromInclusive, Bound.INCLUSIVE, (Bound) null, Bound.NO_BOUND);
        }

        private NavigableMap<K, V> subMap(K from, Bound fromBound, K to, Bound toBound) {
            if (!this.ascending) {
                from = to;
                fromBound = toBound;
                to = from;
                toBound = fromBound;
            }
            if (fromBound == Bound.NO_BOUND) {
                from = this.from;
                fromBound = this.fromBound;
            } else {
                Bound fromBoundToCheck = fromBound == this.fromBound ? Bound.INCLUSIVE : this.fromBound;
                if (!isInBounds(from, fromBoundToCheck, this.toBound)) {
                    throw outOfBounds(to, fromBoundToCheck, this.toBound);
                }
            }
            if (toBound == Bound.NO_BOUND) {
                to = this.to;
                toBound = this.toBound;
            } else {
                Bound toBoundToCheck = toBound == this.toBound ? Bound.INCLUSIVE : this.toBound;
                if (!isInBounds(to, this.fromBound, toBoundToCheck)) {
                    throw outOfBounds(to, this.fromBound, toBoundToCheck);
                }
            }
            return new BoundedMap(this.ascending, from, fromBound, to, toBound);
        }

        private IllegalArgumentException outOfBounds(Object value, Bound fromBound, Bound toBound) {
            return new IllegalArgumentException(value + " not in range " + fromBound.leftCap(this.from) + ".." + toBound.rightCap(this.to));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: TreeMap$BoundedMap$BoundedIterator.class */
        public abstract class BoundedIterator<T> extends TreeMap<K, V>.MapIterator<T> {
            protected BoundedIterator(Node<K, V> next) {
                super(next);
            }

            @Override // java.util.TreeMap.MapIterator
            protected Node<K, V> stepForward() {
                Node<K, V> result = super.stepForward();
                if (this.next != null && !BoundedMap.this.isInBounds(this.next.key, Bound.NO_BOUND, BoundedMap.this.toBound)) {
                    this.next = null;
                }
                return result;
            }

            @Override // java.util.TreeMap.MapIterator
            protected Node<K, V> stepBackward() {
                Node<K, V> result = super.stepBackward();
                if (this.next != null && !BoundedMap.this.isInBounds(this.next.key, BoundedMap.this.fromBound, Bound.NO_BOUND)) {
                    this.next = null;
                }
                return result;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: TreeMap$BoundedMap$BoundedEntrySet.class */
        public final class BoundedEntrySet extends AbstractSet<Map.Entry<K, V>> {
            BoundedEntrySet() {
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return BoundedMap.this.size();
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public boolean isEmpty() {
                return BoundedMap.this.isEmpty();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
            public Iterator<Map.Entry<K, V>> iterator() {
                return new TreeMap<K, V>.BoundedMap.BoundedIterator<Map.Entry<K, V>>(BoundedMap.this.endpoint(true)) { // from class: java.util.TreeMap.BoundedMap.BoundedEntrySet.1
                    {
                        BoundedMap boundedMap = BoundedMap.this;
                    }

                    @Override // java.util.Iterator
                    public Map.Entry<K, V> next() {
                        return BoundedMap.this.ascending ? stepForward() : stepBackward();
                    }
                };
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry<?, ?> entry = (Map.Entry) o;
                return BoundedMap.this.isInBounds(entry.getKey()) && TreeMap.this.findByEntry(entry) != null;
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry<?, ?> entry = (Map.Entry) o;
                return BoundedMap.this.isInBounds(entry.getKey()) && TreeMap.this.entrySet().remove(entry);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: TreeMap$BoundedMap$BoundedKeySet.class */
        public final class BoundedKeySet extends AbstractSet<K> implements NavigableSet<K> {
            BoundedKeySet() {
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return BoundedMap.this.size();
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public boolean isEmpty() {
                return BoundedMap.this.isEmpty();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
            public Iterator<K> iterator() {
                return new TreeMap<K, V>.BoundedMap.BoundedIterator<K>(BoundedMap.this.endpoint(true)) { // from class: java.util.TreeMap.BoundedMap.BoundedKeySet.1
                    {
                        BoundedMap boundedMap = BoundedMap.this;
                    }

                    @Override // java.util.Iterator
                    public K next() {
                        return (BoundedMap.this.ascending ? stepForward() : stepBackward()).key;
                    }
                };
            }

            @Override // java.util.NavigableSet
            public Iterator<K> descendingIterator() {
                return new TreeMap<K, V>.BoundedMap.BoundedIterator<K>(BoundedMap.this.endpoint(false)) { // from class: java.util.TreeMap.BoundedMap.BoundedKeySet.2
                    {
                        BoundedMap boundedMap = BoundedMap.this;
                    }

                    @Override // java.util.Iterator
                    public K next() {
                        return (BoundedMap.this.ascending ? stepBackward() : stepForward()).key;
                    }
                };
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public boolean contains(Object key) {
                return BoundedMap.this.isInBounds(key) && TreeMap.this.findByObject(key) != null;
            }

            @Override // java.util.AbstractCollection, java.util.Collection
            public boolean remove(Object key) {
                return BoundedMap.this.isInBounds(key) && TreeMap.this.removeInternalByKey(key) != null;
            }

            @Override // java.util.SortedSet
            public K first() {
                return (K) BoundedMap.this.firstKey();
            }

            @Override // java.util.NavigableSet
            public K pollFirst() {
                Map.Entry<K, ?> entry = BoundedMap.this.pollFirstEntry();
                if (entry != null) {
                    return entry.getKey();
                }
                return null;
            }

            @Override // java.util.SortedSet
            public K last() {
                return (K) BoundedMap.this.lastKey();
            }

            @Override // java.util.NavigableSet
            public K pollLast() {
                Map.Entry<K, ?> entry = BoundedMap.this.pollLastEntry();
                if (entry != null) {
                    return entry.getKey();
                }
                return null;
            }

            @Override // java.util.NavigableSet
            public K lower(K key) {
                return (K) BoundedMap.this.lowerKey(key);
            }

            @Override // java.util.NavigableSet
            public K floor(K key) {
                return (K) BoundedMap.this.floorKey(key);
            }

            @Override // java.util.NavigableSet
            public K ceiling(K key) {
                return (K) BoundedMap.this.ceilingKey(key);
            }

            @Override // java.util.NavigableSet
            public K higher(K key) {
                return (K) BoundedMap.this.higherKey(key);
            }

            @Override // java.util.SortedSet
            public Comparator<? super K> comparator() {
                return BoundedMap.this.comparator();
            }

            @Override // java.util.NavigableSet
            public NavigableSet<K> subSet(K from, boolean fromInclusive, K to, boolean toInclusive) {
                return BoundedMap.this.subMap((boolean) from, fromInclusive, (boolean) to, toInclusive).navigableKeySet();
            }

            @Override // java.util.NavigableSet, java.util.SortedSet
            public SortedSet<K> subSet(K fromInclusive, K toExclusive) {
                return BoundedMap.this.subMap((Object) fromInclusive, (Object) toExclusive).navigableKeySet();
            }

            @Override // java.util.NavigableSet
            public NavigableSet<K> headSet(K to, boolean inclusive) {
                return BoundedMap.this.headMap(to, inclusive).navigableKeySet();
            }

            @Override // java.util.NavigableSet, java.util.SortedSet
            public SortedSet<K> headSet(K toExclusive) {
                return BoundedMap.this.headMap((BoundedMap) toExclusive).navigableKeySet();
            }

            @Override // java.util.NavigableSet
            public NavigableSet<K> tailSet(K from, boolean inclusive) {
                return BoundedMap.this.tailMap(from, inclusive).navigableKeySet();
            }

            @Override // java.util.NavigableSet, java.util.SortedSet
            public SortedSet<K> tailSet(K fromInclusive) {
                return BoundedMap.this.tailMap((BoundedMap) fromInclusive).navigableKeySet();
            }

            @Override // java.util.NavigableSet
            public NavigableSet<K> descendingSet() {
                return new BoundedMap(!BoundedMap.this.ascending, BoundedMap.this.from, BoundedMap.this.fromBound, BoundedMap.this.to, BoundedMap.this.toBound).navigableKeySet();
            }
        }

        Object writeReplace() throws ObjectStreamException {
            return this.ascending ? new AscendingSubMap(TreeMap.this, this.from, this.fromBound, this.to, this.toBound) : new DescendingSubMap(TreeMap.this, this.from, this.fromBound, this.to, this.toBound);
        }
    }

    /* loaded from: TreeMap$NavigableSubMap.class */
    static abstract class NavigableSubMap<K, V> extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = -2102997345730753016L;
        TreeMap<K, V> m;
        Object lo;
        Object hi;
        boolean fromStart;
        boolean toEnd;
        boolean loInclusive;
        boolean hiInclusive;

        NavigableSubMap(TreeMap<K, V> delegate, K from, Bound fromBound, K to, Bound toBound) {
            this.m = delegate;
            this.lo = from;
            this.hi = to;
            this.fromStart = fromBound == Bound.NO_BOUND;
            this.toEnd = toBound == Bound.NO_BOUND;
            this.loInclusive = fromBound == Bound.INCLUSIVE;
            this.hiInclusive = toBound == Bound.INCLUSIVE;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }

        protected Object readResolve() throws ObjectStreamException {
            Bound fromBound = this.fromStart ? Bound.NO_BOUND : this.loInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE;
            Bound toBound = this.toEnd ? Bound.NO_BOUND : this.hiInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE;
            boolean ascending = !(this instanceof DescendingSubMap);
            TreeMap<K, V> treeMap = this.m;
            treeMap.getClass();
            return new BoundedMap(ascending, this.lo, fromBound, this.hi, toBound);
        }
    }

    /* loaded from: TreeMap$DescendingSubMap.class */
    static class DescendingSubMap<K, V> extends NavigableSubMap<K, V> {
        private static final long serialVersionUID = 912986545866120460L;
        Comparator<K> reverseComparator;

        DescendingSubMap(TreeMap<K, V> delegate, K from, Bound fromBound, K to, Bound toBound) {
            super(delegate, from, fromBound, to, toBound);
        }
    }

    /* loaded from: TreeMap$AscendingSubMap.class */
    static class AscendingSubMap<K, V> extends NavigableSubMap<K, V> {
        private static final long serialVersionUID = 912986545866124060L;

        AscendingSubMap(TreeMap<K, V> delegate, K from, Bound fromBound, K to, Bound toBound) {
            super(delegate, from, fromBound, to, toBound);
        }
    }

    /* loaded from: TreeMap$SubMap.class */
    class SubMap extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = -6520786458950516097L;
        Object fromKey;
        Object toKey;
        boolean fromStart;
        boolean toEnd;

        SubMap() {
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }

        protected Object readResolve() throws ObjectStreamException {
            Bound fromBound = this.fromStart ? Bound.NO_BOUND : Bound.INCLUSIVE;
            Bound toBound = this.toEnd ? Bound.NO_BOUND : Bound.EXCLUSIVE;
            return new BoundedMap(true, this.fromKey, fromBound, this.toKey, toBound);
        }
    }
}