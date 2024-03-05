package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConcurrentHashMap.class */
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentHashMap(int initialCapacity) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentHashMap() {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
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

    public boolean contains(Object value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K key, V value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
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

    public Enumeration<K> keys() {
        throw new RuntimeException("Stub!");
    }

    public Enumeration<V> elements() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentHashMap$HashEntry.class */
    public static final class HashEntry<K, V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K, V> next;
        static final Unsafe UNSAFE;
        static final long nextOffset;

        HashEntry(int hash, K key, V value, HashEntry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        final void setNext(HashEntry<K, V> n) {
            UNSAFE.putOrderedObject(this, nextOffset, n);
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                nextOffset = UNSAFE.objectFieldOffset(HashEntry.class.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentHashMap$Segment.class */
    public static final class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        static final int MAX_SCAN_RETRIES;
        volatile transient HashEntry<K, V>[] table;
        transient int count;
        transient int modCount;
        transient int threshold;
        final float loadFactor;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.put(K, int, V, boolean):V, file: ConcurrentHashMap$Segment.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        final V put(K r1, int r2, V r3, boolean r4) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.put(K, int, V, boolean):V, file: ConcurrentHashMap$Segment.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.Segment.put(java.lang.Object, int, java.lang.Object, boolean):java.lang.Object");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.remove(java.lang.Object, int, java.lang.Object):V, file: ConcurrentHashMap$Segment.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        final V remove(java.lang.Object r1, int r2, java.lang.Object r3) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.remove(java.lang.Object, int, java.lang.Object):V, file: ConcurrentHashMap$Segment.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.Segment.remove(java.lang.Object, int, java.lang.Object):java.lang.Object");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.replace(K, int, V, V):boolean, file: ConcurrentHashMap$Segment.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        final boolean replace(K r1, int r2, V r3, V r4) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.replace(K, int, V, V):boolean, file: ConcurrentHashMap$Segment.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.Segment.replace(java.lang.Object, int, java.lang.Object, java.lang.Object):boolean");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.replace(K, int, V):V, file: ConcurrentHashMap$Segment.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        final V replace(K r1, int r2, V r3) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.replace(K, int, V):V, file: ConcurrentHashMap$Segment.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.Segment.replace(java.lang.Object, int, java.lang.Object):java.lang.Object");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.clear():void, file: ConcurrentHashMap$Segment.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        final void clear() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.ConcurrentHashMap.Segment.clear():void, file: ConcurrentHashMap$Segment.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.Segment.clear():void");
        }

        static {
            MAX_SCAN_RETRIES = Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1;
        }

        Segment(float lf, int threshold, HashEntry<K, V>[] tab) {
            this.loadFactor = lf;
            this.threshold = threshold;
            this.table = tab;
        }

        private void rehash(HashEntry<K, V> node) {
            HashEntry<K, V>[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            int newCapacity = oldCapacity << 1;
            this.threshold = (int) (newCapacity * this.loadFactor);
            HashEntry<K, V>[] newTable = new HashEntry[newCapacity];
            int sizeMask = newCapacity - 1;
            for (HashEntry<K, V> e : oldTable) {
                if (e != null) {
                    HashEntry<K, V> next = e.next;
                    int idx = e.hash & sizeMask;
                    if (next == null) {
                        newTable[idx] = e;
                    } else {
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        HashEntry<K, V> hashEntry = next;
                        while (true) {
                            HashEntry<K, V> last = hashEntry;
                            if (last == null) {
                                break;
                            }
                            int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                            hashEntry = last.next;
                        }
                        newTable[lastIdx] = lastRun;
                        HashEntry<K, V> hashEntry2 = e;
                        while (true) {
                            HashEntry<K, V> p = hashEntry2;
                            if (p != lastRun) {
                                V v = p.value;
                                int h = p.hash;
                                int k2 = h & sizeMask;
                                HashEntry<K, V> n = newTable[k2];
                                newTable[k2] = new HashEntry<>(h, p.key, v, n);
                                hashEntry2 = p.next;
                            }
                        }
                    }
                }
            }
            int nodeIndex = node.hash & sizeMask;
            node.setNext(newTable[nodeIndex]);
            newTable[nodeIndex] = node;
            this.table = newTable;
        }

        private HashEntry<K, V> scanAndLockForPut(K key, int hash, V value) {
            HashEntry<K, V> f;
            HashEntry<K, V> first = ConcurrentHashMap.entryForHash(this, hash);
            HashEntry<K, V> e = first;
            HashEntry<K, V> node = null;
            int retries = -1;
            while (true) {
                if (tryLock()) {
                    break;
                } else if (retries < 0) {
                    if (e == null) {
                        if (node == null) {
                            node = new HashEntry<>(hash, key, value, null);
                        }
                        retries = 0;
                    } else if (key.equals(e.key)) {
                        retries = 0;
                    } else {
                        e = e.next;
                    }
                } else {
                    retries++;
                    if (retries > MAX_SCAN_RETRIES) {
                        lock();
                        break;
                    } else if ((retries & 1) == 0 && (f = ConcurrentHashMap.entryForHash(this, hash)) != first) {
                        first = f;
                        e = f;
                        retries = -1;
                    }
                }
            }
            return node;
        }

        private void scanAndLock(Object key, int hash) {
            HashEntry<K, V> f;
            HashEntry<K, V> first = ConcurrentHashMap.entryForHash(this, hash);
            HashEntry<K, V> e = first;
            int retries = -1;
            while (!tryLock()) {
                if (retries < 0) {
                    if (e == null || key.equals(e.key)) {
                        retries = 0;
                    } else {
                        e = e.next;
                    }
                } else {
                    retries++;
                    if (retries > MAX_SCAN_RETRIES) {
                        lock();
                        return;
                    } else if ((retries & 1) == 0 && (f = ConcurrentHashMap.entryForHash(this, hash)) != first) {
                        first = f;
                        e = f;
                        retries = -1;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentHashMap$HashIterator.class */
    public abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex = -1;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;

        HashIterator() {
            this.nextSegmentIndex = ConcurrentHashMap.this.segments.length - 1;
            advance();
        }

        final void advance() {
            while (true) {
                if (this.nextTableIndex >= 0) {
                    HashEntry<K, V>[] hashEntryArr = this.currentTable;
                    int i = this.nextTableIndex;
                    this.nextTableIndex = i - 1;
                    HashEntry<K, V> entryAt = ConcurrentHashMap.entryAt(hashEntryArr, i);
                    this.nextEntry = entryAt;
                    if (entryAt != null) {
                        return;
                    }
                } else if (this.nextSegmentIndex >= 0) {
                    Segment[] segmentArr = ConcurrentHashMap.this.segments;
                    int i2 = this.nextSegmentIndex;
                    this.nextSegmentIndex = i2 - 1;
                    Segment<K, V> seg = ConcurrentHashMap.segmentAt(segmentArr, i2);
                    if (seg != null) {
                        HashEntry<K, V>[] hashEntryArr2 = seg.table;
                        this.currentTable = hashEntryArr2;
                        if (hashEntryArr2 != null) {
                            this.nextTableIndex = this.currentTable.length - 1;
                        }
                    }
                } else {
                    return;
                }
            }
        }

        final HashEntry<K, V> nextEntry() {
            HashEntry<K, V> e = this.nextEntry;
            if (e == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = e;
            HashEntry<K, V> hashEntry = e.next;
            this.nextEntry = hashEntry;
            if (hashEntry == null) {
                advance();
            }
            return e;
        }

        public final boolean hasNext() {
            return this.nextEntry != null;
        }

        public final boolean hasMoreElements() {
            return this.nextEntry != null;
        }

        public final void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }

    /* loaded from: ConcurrentHashMap$KeyIterator.class */
    final class KeyIterator extends ConcurrentHashMap<K, V>.HashIterator implements Iterator<K>, Enumeration<K> {
        KeyIterator() {
            super();
        }

        @Override // java.util.Iterator
        public final K next() {
            return super.nextEntry().key;
        }

        @Override // java.util.Enumeration
        public final K nextElement() {
            return super.nextEntry().key;
        }
    }

    /* loaded from: ConcurrentHashMap$ValueIterator.class */
    final class ValueIterator extends ConcurrentHashMap<K, V>.HashIterator implements Iterator<V>, Enumeration<V> {
        ValueIterator() {
            super();
        }

        @Override // java.util.Iterator
        public final V next() {
            return super.nextEntry().value;
        }

        @Override // java.util.Enumeration
        public final V nextElement() {
            return super.nextEntry().value;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentHashMap$WriteThroughEntry.class */
    public final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V> {
        WriteThroughEntry(K k, V v) {
            super(k, v);
        }

        @Override // java.util.AbstractMap.SimpleEntry, java.util.Map.Entry
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = (V) super.setValue(value);
            ConcurrentHashMap.this.put(getKey(), value);
            return v;
        }
    }

    /* loaded from: ConcurrentHashMap$EntryIterator.class */
    final class EntryIterator extends ConcurrentHashMap<K, V>.HashIterator implements Iterator<Map.Entry<K, V>> {
        EntryIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            HashEntry<K, V> e = super.nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }

    /* loaded from: ConcurrentHashMap$KeySet.class */
    final class KeySet extends AbstractSet<K> {
        KeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return ConcurrentHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return ConcurrentHashMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return ConcurrentHashMap.this.containsKey(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            return ConcurrentHashMap.this.remove(o) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            ConcurrentHashMap.this.clear();
        }
    }

    /* loaded from: ConcurrentHashMap$Values.class */
    final class Values extends AbstractCollection<V> {
        Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return ConcurrentHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return ConcurrentHashMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return ConcurrentHashMap.this.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            ConcurrentHashMap.this.clear();
        }
    }

    /* loaded from: ConcurrentHashMap$EntrySet.class */
    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        EntrySet() {
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
            Object obj = ConcurrentHashMap.this.get(e.getKey());
            return obj != null && obj.equals(e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            return ConcurrentHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return ConcurrentHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return ConcurrentHashMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            ConcurrentHashMap.this.clear();
        }
    }
}