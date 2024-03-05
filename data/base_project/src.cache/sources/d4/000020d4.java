package gov.nist.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: MultiValueMapImpl.class */
public class MultiValueMapImpl<V> implements MultiValueMap<String, V>, Cloneable {
    private HashMap<String, ArrayList<V>> map = new HashMap<>();
    private static final long serialVersionUID = 4275505380960964605L;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // gov.nist.core.MultiValueMap
    public /* bridge */ /* synthetic */ Object remove(String str, Object x1) {
        return remove2(str, (String) x1);
    }

    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
        return put((String) x0, (List) ((List) x1));
    }

    public List<V> put(String key, V value) {
        ArrayList<V> keyList = this.map.get(key);
        if (keyList == null) {
            keyList = new ArrayList<>(10);
            this.map.put(key, keyList);
        }
        keyList.add(value);
        return keyList;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        Set pairs = this.map.entrySet();
        if (pairs == null) {
            return false;
        }
        for (Map.Entry<String, ArrayList<V>> keyValuePair : pairs) {
            ArrayList<V> list = keyValuePair.getValue();
            if (list.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public void clear() {
        Set pairs = this.map.entrySet();
        for (Map.Entry<String, ArrayList<V>> keyValuePair : pairs) {
            ArrayList<V> list = keyValuePair.getValue();
            list.clear();
        }
        this.map.clear();
    }

    @Override // java.util.Map
    public Collection values() {
        ArrayList returnList = new ArrayList(this.map.size());
        Set pairs = this.map.entrySet();
        for (Map.Entry<String, ArrayList<V>> keyValuePair : pairs) {
            ArrayList<V> list = keyValuePair.getValue();
            Object[] values = list.toArray();
            for (Object obj : values) {
                returnList.add(obj);
            }
        }
        return returnList;
    }

    public Object clone() {
        MultiValueMapImpl obj = new MultiValueMapImpl();
        obj.map = (HashMap) this.map.clone();
        return obj;
    }

    @Override // java.util.Map
    public int size() {
        return this.map.size();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override // java.util.Map
    public Set entrySet() {
        return this.map.entrySet();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.map.keySet();
    }

    /* renamed from: remove  reason: avoid collision after fix types in other method */
    public Object remove2(String key, V item) {
        ArrayList<V> list = this.map.get(key);
        if (list == null) {
            return null;
        }
        return Boolean.valueOf(list.remove(item));
    }

    @Override // java.util.Map
    public List<V> get(Object key) {
        return this.map.get(key);
    }

    public List<V> put(String key, List<V> value) {
        return this.map.put(key, (ArrayList) value);
    }

    @Override // java.util.Map
    public List<V> remove(Object key) {
        return this.map.remove(key);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends List<V>> mapToPut) {
        for (String k : mapToPut.keySet()) {
            ArrayList<V> al = new ArrayList<>();
            al.addAll(mapToPut.get(k));
            this.map.put(k, al);
        }
    }
}