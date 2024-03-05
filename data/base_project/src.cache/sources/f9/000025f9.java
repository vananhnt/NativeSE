package java.util;

import gov.nist.core.Separators;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MapEntry.class */
public class MapEntry<K, V> implements Map.Entry<K, V>, Cloneable {
    K key;
    V value;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MapEntry$Type.class */
    public interface Type<RT, KT, VT> {
        RT get(MapEntry<KT, VT> mapEntry);
    }

    MapEntry(K theKey) {
        this.key = theKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MapEntry(K theKey, V theValue) {
        this.key = theKey;
        this.value = theValue;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override // java.util.Map.Entry
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry) object;
            if (this.key != null ? this.key.equals(entry.getKey()) : entry.getKey() == null) {
                if (this.value != null ? this.value.equals(entry.getValue()) : entry.getValue() == null) {
                    return true;
                }
            }
            return false;
        }
        return false;
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
    public int hashCode() {
        return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
    }

    @Override // java.util.Map.Entry
    public V setValue(V object) {
        V result = this.value;
        this.value = object;
        return result;
    }

    public String toString() {
        return this.key + Separators.EQUALS + this.value;
    }
}