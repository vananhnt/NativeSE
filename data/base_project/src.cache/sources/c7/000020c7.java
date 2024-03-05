package gov.nist.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: DuplicateNameValueList.class */
public class DuplicateNameValueList implements Serializable, Cloneable {
    private MultiValueMapImpl<NameValue> nameValueMap = new MultiValueMapImpl<>();
    private String separator = Separators.SEMICOLON;
    private static final long serialVersionUID = -5611332957903796952L;

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    public StringBuffer encode(StringBuffer buffer) {
        if (!this.nameValueMap.isEmpty()) {
            Iterator<NameValue> iterator = this.nameValueMap.values().iterator();
            if (iterator.hasNext()) {
                while (true) {
                    Object obj = iterator.next();
                    if (obj instanceof GenericObject) {
                        GenericObject gobj = (GenericObject) obj;
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
        this.nameValueMap.put(nv.getName().toLowerCase(), (String) nv);
    }

    public void set(String name, Object value) {
        NameValue nameValue = new NameValue(name, value);
        this.nameValueMap.put(name.toLowerCase(), (String) nameValue);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0044  */
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
            gov.nist.core.DuplicateNameValueList r0 = (gov.nist.core.DuplicateNameValueList) r0
            r5 = r0
            r0 = r3
            gov.nist.core.MultiValueMapImpl<gov.nist.core.NameValue> r0 = r0.nameValueMap
            int r0 = r0.size()
            r1 = r5
            gov.nist.core.MultiValueMapImpl<gov.nist.core.NameValue> r1 = r1.nameValueMap
            int r1 = r1.size()
            if (r0 == r1) goto L2e
            r0 = 0
            return r0
        L2e:
            r0 = r3
            gov.nist.core.MultiValueMapImpl<gov.nist.core.NameValue> r0 = r0.nameValueMap
            java.util.Set r0 = r0.keySet()
            java.util.Iterator r0 = r0.iterator()
            r6 = r0
        L3b:
            r0 = r6
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L78
            r0 = r6
            java.lang.Object r0 = r0.next()
            java.lang.String r0 = (java.lang.String) r0
            r7 = r0
            r0 = r3
            r1 = r7
            java.util.Collection r0 = r0.getNameValue(r1)
            r8 = r0
            r0 = r5
            gov.nist.core.MultiValueMapImpl<gov.nist.core.NameValue> r0 = r0.nameValueMap
            r1 = r7
            java.util.List r0 = r0.get(r1)
            r9 = r0
            r0 = r9
            if (r0 != 0) goto L69
            r0 = 0
            return r0
        L69:
            r0 = r9
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L75
            r0 = 0
            return r0
        L75:
            goto L3b
        L78:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.core.DuplicateNameValueList.equals(java.lang.Object):boolean");
    }

    public Object getValue(String name) {
        Collection nv = getNameValue(name.toLowerCase());
        if (nv != null) {
            return nv;
        }
        return null;
    }

    public Collection getNameValue(String name) {
        return this.nameValueMap.get((Object) name.toLowerCase());
    }

    public boolean hasNameValue(String name) {
        return this.nameValueMap.containsKey(name.toLowerCase());
    }

    public boolean delete(String name) {
        String lcName = name.toLowerCase();
        if (this.nameValueMap.containsKey(lcName)) {
            this.nameValueMap.remove((Object) lcName);
            return true;
        }
        return false;
    }

    public Object clone() {
        DuplicateNameValueList retval = new DuplicateNameValueList();
        retval.setSeparator(this.separator);
        for (NameValue nameValue : this.nameValueMap.values()) {
            retval.set((NameValue) nameValue.clone());
        }
        return retval;
    }

    public Iterator<NameValue> iterator() {
        return this.nameValueMap.values().iterator();
    }

    public Iterator<String> getNames() {
        return this.nameValueMap.keySet().iterator();
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

    public void clear() {
        this.nameValueMap.clear();
    }

    public boolean isEmpty() {
        return this.nameValueMap.isEmpty();
    }

    public NameValue put(String key, NameValue value) {
        return (NameValue) this.nameValueMap.put(key, (String) value);
    }

    public NameValue remove(Object key) {
        return (NameValue) this.nameValueMap.remove(key);
    }

    public int size() {
        return this.nameValueMap.size();
    }

    public Collection<NameValue> values() {
        return this.nameValueMap.values();
    }

    public int hashCode() {
        return this.nameValueMap.keySet().hashCode();
    }
}