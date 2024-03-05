package java.util;

import gov.nist.core.Separators;
import java.io.Serializable;
import java.lang.Enum;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.MapEntry;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: EnumMap.class */
public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements Serializable, Cloneable, Map<K, V> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
        return put((EnumMap<K, V>) ((Enum) x0), (Enum) x1);
    }

    public EnumMap(Class<K> keyType) {
        throw new RuntimeException("Stub!");
    }

    public EnumMap(EnumMap<K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    public EnumMap(Map<K, ? extends V> map) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap
    public EnumMap<K, V> clone() {
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
    public Set<Map.Entry<K, V>> entrySet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        throw new RuntimeException("Stub!");
    }

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

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: EnumMap$Entry.class */
    private static class Entry<KT extends Enum<KT>, VT> extends MapEntry<KT, VT> {
        private final EnumMap<KT, VT> enumMap;
        private final int ordinal;

        Entry(KT theKey, VT theValue, EnumMap<KT, VT> em) {
            super(theKey, theValue);
            this.enumMap = em;
            this.ordinal = theKey.ordinal();
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public boolean equals(Object object) {
            if (!this.enumMap.hasMapping[this.ordinal]) {
                return false;
            }
            boolean isEqual = false;
            if (object instanceof Map.Entry) {
                Map.Entry<KT, VT> entry = (Map.Entry) object;
                Object enumKey = entry.getKey();
                if (((Enum) this.key).equals(enumKey)) {
                    Object theValue = entry.getValue();
                    if (this.enumMap.values[this.ordinal] == null) {
                        isEqual = theValue == null;
                    } else {
                        isEqual = this.enumMap.values[this.ordinal].equals(theValue);
                    }
                }
            }
            return isEqual;
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public int hashCode() {
            return (this.enumMap.keys[this.ordinal] == null ? 0 : this.enumMap.keys[this.ordinal].hashCode()) ^ (this.enumMap.values[this.ordinal] == null ? 0 : this.enumMap.values[this.ordinal].hashCode());
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public KT getKey() {
            checkEntryStatus();
            return (KT) this.enumMap.keys[this.ordinal];
        }

        @Override // java.util.MapEntry, java.util.Map.Entry
        public VT getValue() {
            checkEntryStatus();
            return (VT) this.enumMap.values[this.ordinal];
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.MapEntry, java.util.Map.Entry
        public VT setValue(VT value) {
            checkEntryStatus();
            return (VT) this.enumMap.put((EnumMap<KT, VT>) this.enumMap.keys[this.ordinal], (Enum) value);
        }

        @Override // java.util.MapEntry
        public String toString() {
            StringBuilder result = new StringBuilder(this.enumMap.keys[this.ordinal].toString());
            result.append(Separators.EQUALS);
            result.append(this.enumMap.values[this.ordinal] == null ? "null" : this.enumMap.values[this.ordinal].toString());
            return result.toString();
        }

        private void checkEntryStatus() {
            if (!this.enumMap.hasMapping[this.ordinal]) {
                throw new IllegalStateException();
            }
        }
    }

    /* loaded from: EnumMap$EnumMapIterator.class */
    private static class EnumMapIterator<E, KT extends Enum<KT>, VT> implements Iterator<E> {
        int position = 0;
        int prePosition = -1;
        final EnumMap<KT, VT> enumMap;
        final MapEntry.Type<E, KT, VT> type;

        EnumMapIterator(MapEntry.Type<E, KT, VT> value, EnumMap<KT, VT> em) {
            this.enumMap = em;
            this.type = value;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            int length = this.enumMap.enumSize;
            while (this.position < length && !this.enumMap.hasMapping[this.position]) {
                this.position++;
            }
            return this.position != length;
        }

        @Override // java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int i = this.position;
            this.position = i + 1;
            this.prePosition = i;
            return this.type.get(new MapEntry<>(this.enumMap.keys[this.prePosition], this.enumMap.values[this.prePosition]));
        }

        @Override // java.util.Iterator
        public void remove() {
            checkStatus();
            if (this.enumMap.hasMapping[this.prePosition]) {
                this.enumMap.remove(this.enumMap.keys[this.prePosition]);
            }
            this.prePosition = -1;
        }

        public String toString() {
            if (-1 == this.prePosition) {
                return super.toString();
            }
            return this.type.get(new MapEntry<>(this.enumMap.keys[this.prePosition], this.enumMap.values[this.prePosition])).toString();
        }

        private void checkStatus() {
            if (-1 == this.prePosition) {
                throw new IllegalStateException();
            }
        }
    }

    /* loaded from: EnumMap$EnumMapKeySet.class */
    private static class EnumMapKeySet<KT extends Enum<KT>, VT> extends AbstractSet<KT> {
        private final EnumMap<KT, VT> enumMap;

        EnumMapKeySet(EnumMap<KT, VT> em) {
            this.enumMap = em;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.enumMap.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.enumMap.containsKey(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator iterator() {
            return new EnumMapIterator(new MapEntry.Type<KT, KT, VT>() { // from class: java.util.EnumMap.EnumMapKeySet.1
                @Override // java.util.MapEntry.Type
                public /* bridge */ /* synthetic */ Object get(MapEntry x0) {
                    return get((MapEntry<Enum, VT>) x0);
                }

                @Override // java.util.MapEntry.Type
                public KT get(MapEntry<KT, VT> entry) {
                    return entry.key;
                }
            }, this.enumMap);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            if (contains(object)) {
                this.enumMap.remove(object);
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.enumMap.size();
        }
    }

    /* loaded from: EnumMap$EnumMapValueCollection.class */
    private static class EnumMapValueCollection<KT extends Enum<KT>, VT> extends AbstractCollection<VT> {
        private final EnumMap<KT, VT> enumMap;

        EnumMapValueCollection(EnumMap<KT, VT> em) {
            this.enumMap = em;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.enumMap.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return this.enumMap.containsValue(object);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator iterator() {
            return new EnumMapIterator(new MapEntry.Type<VT, KT, VT>() { // from class: java.util.EnumMap.EnumMapValueCollection.1
                @Override // java.util.MapEntry.Type
                public VT get(MapEntry<KT, VT> entry) {
                    return entry.value;
                }
            }, this.enumMap);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            if (object == null) {
                for (int i = 0; i < this.enumMap.enumSize; i++) {
                    if (this.enumMap.hasMapping[i] && this.enumMap.values[i] == null) {
                        this.enumMap.remove(this.enumMap.keys[i]);
                        return true;
                    }
                }
                return false;
            }
            for (int i2 = 0; i2 < this.enumMap.enumSize; i2++) {
                if (this.enumMap.hasMapping[i2] && object.equals(this.enumMap.values[i2])) {
                    this.enumMap.remove(this.enumMap.keys[i2]);
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.enumMap.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: EnumMap$EnumMapEntryIterator.class */
    public static class EnumMapEntryIterator<E, KT extends Enum<KT>, VT> extends EnumMapIterator<E, KT, VT> {
        EnumMapEntryIterator(MapEntry.Type<E, KT, VT> value, EnumMap<KT, VT> em) {
            super(value, em);
        }

        @Override // java.util.EnumMap.EnumMapIterator, java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int i = this.position;
            this.position = i + 1;
            this.prePosition = i;
            return this.type.get(new Entry(this.enumMap.keys[this.prePosition], this.enumMap.values[this.prePosition], this.enumMap));
        }
    }

    /* loaded from: EnumMap$EnumMapEntrySet.class */
    private static class EnumMapEntrySet<KT extends Enum<KT>, VT> extends AbstractSet<Map.Entry<KT, VT>> {
        private final EnumMap<KT, VT> enumMap;

        EnumMapEntrySet(EnumMap<KT, VT> em) {
            this.enumMap = em;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            this.enumMap.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            boolean isEqual = false;
            if (object instanceof Map.Entry) {
                Object enumKey = ((Map.Entry) object).getKey();
                Object enumValue = ((Map.Entry) object).getValue();
                if (this.enumMap.containsKey(enumKey)) {
                    VT value = this.enumMap.get(enumKey);
                    if (value == null) {
                        isEqual = enumValue == null;
                    } else {
                        isEqual = value.equals(enumValue);
                    }
                }
            }
            return isEqual;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<KT, VT>> iterator() {
            return new EnumMapEntryIterator(new MapEntry.Type<Map.Entry<KT, VT>, KT, VT>() { // from class: java.util.EnumMap.EnumMapEntrySet.1
                @Override // java.util.MapEntry.Type
                public Map.Entry<KT, VT> get(MapEntry<KT, VT> entry) {
                    return entry;
                }
            }, this.enumMap);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            if (contains(object)) {
                this.enumMap.remove(((Map.Entry) object).getKey());
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.enumMap.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            Object[] entryArray = new Object[this.enumMap.size()];
            return toArray(entryArray);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray(Object[] array) {
            int size = this.enumMap.size();
            int index = 0;
            Object[] entryArray = array;
            if (size > array.length) {
                Class<?> clazz = array.getClass().getComponentType();
                entryArray = (Object[]) Array.newInstance(clazz, size);
            }
            Iterator<Map.Entry<KT, VT>> iter = iterator();
            while (index < size) {
                Map.Entry<KT, VT> entry = iter.next();
                entryArray[index] = new MapEntry(entry.getKey(), entry.getValue());
                index++;
            }
            if (index < array.length) {
                entryArray[index] = null;
            }
            return entryArray;
        }
    }
}