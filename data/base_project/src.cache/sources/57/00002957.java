package org.apache.commons.logging.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/* loaded from: WeakHashtable.class */
public final class WeakHashtable extends Hashtable {
    private static final int MAX_CHANGES_BEFORE_PURGE = 100;
    private static final int PARTIAL_PURGE_COUNT = 10;
    private ReferenceQueue queue = new ReferenceQueue();
    private int changeCount = 0;

    @Override // java.util.Hashtable, java.util.Map
    public boolean containsKey(Object key) {
        Referenced referenced = new Referenced(key);
        return super.containsKey(referenced);
    }

    @Override // java.util.Hashtable, java.util.Dictionary
    public Enumeration elements() {
        purge();
        return super.elements();
    }

    @Override // java.util.Hashtable, java.util.Map
    public Set entrySet() {
        purge();
        Set<Map.Entry> referencedEntries = super.entrySet();
        Set unreferencedEntries = new HashSet();
        for (Map.Entry entry : referencedEntries) {
            Referenced referencedKey = (Referenced) entry.getKey();
            Object key = referencedKey.getValue();
            Object value = entry.getValue();
            if (key != null) {
                Entry dereferencedEntry = new Entry(key, value);
                unreferencedEntries.add(dereferencedEntry);
            }
        }
        return unreferencedEntries;
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public Object get(Object key) {
        Referenced referenceKey = new Referenced(key);
        return super.get(referenceKey);
    }

    @Override // java.util.Hashtable, java.util.Dictionary
    public Enumeration keys() {
        purge();
        final Enumeration enumer = super.keys();
        return new Enumeration() { // from class: org.apache.commons.logging.impl.WeakHashtable.1
            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return enumer.hasMoreElements();
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                Referenced nextReference = (Referenced) enumer.nextElement();
                return nextReference.getValue();
            }
        };
    }

    @Override // java.util.Hashtable, java.util.Map
    public Set keySet() {
        purge();
        Set<Referenced> referencedKeys = super.keySet();
        Set unreferencedKeys = new HashSet();
        for (Referenced referenceKey : referencedKeys) {
            Object keyValue = referenceKey.getValue();
            if (keyValue != null) {
                unreferencedKeys.add(keyValue);
            }
        }
        return unreferencedKeys;
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public Object put(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("Null keys are not allowed");
        }
        if (value == null) {
            throw new NullPointerException("Null values are not allowed");
        }
        int i = this.changeCount;
        this.changeCount = i + 1;
        if (i > 100) {
            purge();
            this.changeCount = 0;
        } else if (this.changeCount % 10 == 0) {
            purgeOne();
        }
        Referenced keyRef = new Referenced(key, this.queue);
        return super.put(keyRef, value);
    }

    @Override // java.util.Hashtable, java.util.Map
    public void putAll(Map t) {
        if (t != null) {
            Set<Map.Entry> entrySet = t.entrySet();
            for (Map.Entry entry : entrySet) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override // java.util.Hashtable, java.util.Map
    public Collection values() {
        purge();
        return super.values();
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public Object remove(Object key) {
        int i = this.changeCount;
        this.changeCount = i + 1;
        if (i > 100) {
            purge();
            this.changeCount = 0;
        } else if (this.changeCount % 10 == 0) {
            purgeOne();
        }
        return super.remove(new Referenced(key));
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public boolean isEmpty() {
        purge();
        return super.isEmpty();
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public int size() {
        purge();
        return super.size();
    }

    @Override // java.util.Hashtable
    public String toString() {
        purge();
        return super.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.util.Hashtable
    public void rehash() {
        purge();
        super.rehash();
    }

    private void purge() {
        synchronized (this.queue) {
            while (true) {
                WeakKey key = (WeakKey) this.queue.poll();
                if (key != null) {
                    super.remove(key.getReferenced());
                }
            }
        }
    }

    private void purgeOne() {
        synchronized (this.queue) {
            WeakKey key = (WeakKey) this.queue.poll();
            if (key != null) {
                super.remove(key.getReferenced());
            }
        }
    }

    /* loaded from: WeakHashtable$Entry.class */
    private static final class Entry implements Map.Entry {
        private final Object key;
        private final Object value;

        private Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object o) {
            boolean z;
            boolean result = false;
            if (o != null && (o instanceof Map.Entry)) {
                Map.Entry entry = (Map.Entry) o;
                if (getKey() != null ? getKey().equals(entry.getKey()) : entry.getKey() == null) {
                    if (getValue() != null ? getValue().equals(entry.getValue()) : entry.getValue() == null) {
                        z = true;
                        result = z;
                    }
                }
                z = false;
                result = z;
            }
            return result;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
        }

        @Override // java.util.Map.Entry
        public Object setValue(Object value) {
            throw new UnsupportedOperationException("Entry.setValue is not supported.");
        }

        @Override // java.util.Map.Entry
        public Object getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public Object getKey() {
            return this.key;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WeakHashtable$Referenced.class */
    public static final class Referenced {
        private final WeakReference reference;
        private final int hashCode;

        private Referenced(Object referant) {
            this.reference = new WeakReference(referant);
            this.hashCode = referant.hashCode();
        }

        private Referenced(Object key, ReferenceQueue queue) {
            this.reference = new WeakKey(key, queue, this);
            this.hashCode = key.hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Object getValue() {
            return this.reference.get();
        }

        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof Referenced) {
                Referenced otherKey = (Referenced) o;
                Object thisKeyValue = getValue();
                Object otherKeyValue = otherKey.getValue();
                if (thisKeyValue == null) {
                    result = otherKeyValue == null;
                    if (result) {
                        result = hashCode() == otherKey.hashCode();
                    }
                } else {
                    result = thisKeyValue.equals(otherKeyValue);
                }
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WeakHashtable$WeakKey.class */
    public static final class WeakKey extends WeakReference {
        private final Referenced referenced;

        private WeakKey(Object key, ReferenceQueue queue, Referenced referenced) {
            super(key, queue);
            this.referenced = referenced;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Referenced getReferenced() {
            return this.referenced;
        }
    }
}