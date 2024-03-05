package gov.nist.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: NameValueList.class */
public class NameValueList implements Serializable, Cloneable, Map<String, NameValue> {
    private static final long serialVersionUID = -6998271876574260243L;
    private Map<String, NameValue> hmap;
    private String separator;

    public NameValueList() {
        this.separator = Separators.SEMICOLON;
        this.hmap = new LinkedHashMap();
    }

    public NameValueList(boolean sync) {
        this.separator = Separators.SEMICOLON;
        if (sync) {
            this.hmap = new ConcurrentHashMap();
        } else {
            this.hmap = new LinkedHashMap();
        }
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    public StringBuffer encode(StringBuffer buffer) {
        if (!this.hmap.isEmpty()) {
            Iterator<NameValue> iterator = this.hmap.values().iterator();
            if (iterator.hasNext()) {
                while (true) {
                    GenericObject obj = iterator.next();
                    if (obj instanceof GenericObject) {
                        GenericObject gobj = obj;
                        gobj.encode(buffer);
                    } else {
                        buffer.append(obj.toString());
                    }
                    if (!iterator.hasNext()) {
                        break;
                    }
                    buffer.append(this.separator);
                }
            }
        }
        return buffer;
    }

    public String toString() {
        return encode();
    }

    public void set(NameValue nv) {
        this.hmap.put(nv.getName().toLowerCase(), nv);
    }

    public void set(String name, Object value) {
        NameValue nameValue = new NameValue(name, value);
        this.hmap.put(name.toLowerCase(), nameValue);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x004a  */
    @Override // java.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean equals(java.lang.Object r4) {
        /*
            r3 = this;
            r0 = r4
            if (r0 != 0) goto L6
            r0 = 0
            return r0
        L6:
            r0 = r4
            java.lang.Class r0 = r0.getClass()
            r1 = r3
            java.lang.Class r1 = r1.getClass()
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L16
            r0 = 0
            return r0
        L16:
            r0 = r4
            gov.nist.core.NameValueList r0 = (gov.nist.core.NameValueList) r0
            r5 = r0
            r0 = r3
            java.util.Map<java.lang.String, gov.nist.core.NameValue> r0 = r0.hmap
            int r0 = r0.size()
            r1 = r5
            java.util.Map<java.lang.String, gov.nist.core.NameValue> r1 = r1.hmap
            int r1 = r1.size()
            if (r0 == r1) goto L32
            r0 = 0
            return r0
        L32:
            r0 = r3
            java.util.Map<java.lang.String, gov.nist.core.NameValue> r0 = r0.hmap
            java.util.Set r0 = r0.keySet()
            java.util.Iterator r0 = r0.iterator()
            r6 = r0
        L41:
            r0 = r6
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L83
            r0 = r6
            java.lang.Object r0 = r0.next()
            java.lang.String r0 = (java.lang.String) r0
            r7 = r0
            r0 = r3
            r1 = r7
            gov.nist.core.NameValue r0 = r0.getNameValue(r1)
            r8 = r0
            r0 = r5
            java.util.Map<java.lang.String, gov.nist.core.NameValue> r0 = r0.hmap
            r1 = r7
            java.lang.Object r0 = r0.get(r1)
            gov.nist.core.NameValue r0 = (gov.nist.core.NameValue) r0
            r9 = r0
            r0 = r9
            if (r0 != 0) goto L74
            r0 = 0
            return r0
        L74:
            r0 = r9
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L80
            r0 = 0
            return r0
        L80:
            goto L41
        L83:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.core.NameValueList.equals(java.lang.Object):boolean");
    }

    public Object getValue(String name) {
        NameValue nv = getNameValue(name.toLowerCase());
        if (nv != null) {
            return nv.getValueAsObject();
        }
        return null;
    }

    public NameValue getNameValue(String name) {
        return this.hmap.get(name.toLowerCase());
    }

    public boolean hasNameValue(String name) {
        return this.hmap.containsKey(name.toLowerCase());
    }

    public boolean delete(String name) {
        String lcName = name.toLowerCase();
        if (this.hmap.containsKey(lcName)) {
            this.hmap.remove(lcName);
            return true;
        }
        return false;
    }

    public Object clone() {
        NameValueList retval = new NameValueList();
        retval.setSeparator(this.separator);
        for (NameValue nameValue : this.hmap.values()) {
            retval.set((NameValue) nameValue.clone());
        }
        return retval;
    }

    @Override // java.util.Map
    public int size() {
        return this.hmap.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.hmap.isEmpty();
    }

    public Iterator<NameValue> iterator() {
        return this.hmap.values().iterator();
    }

    public Iterator<String> getNames() {
        return this.hmap.keySet().iterator();
    }

    public String getParameter(String name) {
        Object val = getValue(name);
        if (val == null) {
            return null;
        }
        if (val instanceof GenericObject) {
            return ((GenericObject) val).encode();
        }
        return val.toString();
    }

    @Override // java.util.Map
    public void clear() {
        this.hmap.clear();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.hmap.containsKey(key.toString().toLowerCase());
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.hmap.containsValue(value);
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, NameValue>> entrySet() {
        return this.hmap.entrySet();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public NameValue get(Object key) {
        return this.hmap.get(key.toString().toLowerCase());
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        return this.hmap.keySet();
    }

    @Override // java.util.Map
    public NameValue put(String name, NameValue nameValue) {
        return this.hmap.put(name, nameValue);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends NameValue> map) {
        this.hmap.putAll(map);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map
    public NameValue remove(Object key) {
        return this.hmap.remove(key.toString().toLowerCase());
    }

    @Override // java.util.Map
    public Collection<NameValue> values() {
        return this.hmap.values();
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.hmap.keySet().hashCode();
    }
}