package gov.nist.core;

import java.util.Map;

/* loaded from: NameValue.class */
public class NameValue extends GenericObject implements Map.Entry<String, String> {
    private static final long serialVersionUID = -1857729012596437950L;
    protected boolean isQuotedString;
    protected final boolean isFlagParameter;
    private String separator;
    private String quotes;
    private String name;
    private Object value;

    public NameValue() {
        this.name = null;
        this.value = "";
        this.separator = Separators.EQUALS;
        this.quotes = "";
        this.isFlagParameter = false;
    }

    public NameValue(String n, Object v, boolean isFlag) {
        this.name = n;
        this.value = v;
        this.separator = Separators.EQUALS;
        this.quotes = "";
        this.isFlagParameter = isFlag;
    }

    public NameValue(String n, Object v) {
        this(n, v, false);
    }

    public void setSeparator(String sep) {
        this.separator = sep;
    }

    public void setQuotedValue() {
        this.isQuotedString = true;
        this.quotes = Separators.DOUBLE_QUOTE;
    }

    public boolean isValueQuoted() {
        return this.isQuotedString;
    }

    public String getName() {
        return this.name;
    }

    public Object getValueAsObject() {
        return this.isFlagParameter ? "" : this.value;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setValueAsObject(Object v) {
        this.value = v;
    }

    @Override // gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        if (this.name != null && this.value != null && !this.isFlagParameter) {
            if (GenericObject.isMySubclass(this.value.getClass())) {
                GenericObject gv = (GenericObject) this.value;
                buffer.append(this.name).append(this.separator).append(this.quotes);
                gv.encode(buffer);
                buffer.append(this.quotes);
                return buffer;
            } else if (GenericObjectList.isMySubclass(this.value.getClass())) {
                GenericObjectList gvlist = (GenericObjectList) this.value;
                buffer.append(this.name).append(this.separator).append(gvlist.encode());
                return buffer;
            } else if (this.value.toString().length() == 0) {
                if (this.isQuotedString) {
                    buffer.append(this.name).append(this.separator).append(this.quotes).append(this.quotes);
                    return buffer;
                }
                buffer.append(this.name).append(this.separator);
                return buffer;
            } else {
                buffer.append(this.name).append(this.separator).append(this.quotes).append(this.value.toString()).append(this.quotes);
                return buffer;
            }
        } else if (this.name == null && this.value != null) {
            if (GenericObject.isMySubclass(this.value.getClass())) {
                GenericObject gv2 = (GenericObject) this.value;
                gv2.encode(buffer);
                return buffer;
            } else if (GenericObjectList.isMySubclass(this.value.getClass())) {
                GenericObjectList gvlist2 = (GenericObjectList) this.value;
                buffer.append(gvlist2.encode());
                return buffer;
            } else {
                buffer.append(this.quotes).append(this.value.toString()).append(this.quotes);
                return buffer;
            }
        } else if (this.name != null && (this.value == null || this.isFlagParameter)) {
            buffer.append(this.name);
            return buffer;
        } else {
            return buffer;
        }
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        NameValue retval = (NameValue) super.clone();
        if (this.value != null) {
            retval.value = makeClone(this.value);
        }
        return retval;
    }

    @Override // gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other == null || !other.getClass().equals(getClass())) {
            return false;
        }
        NameValue that = (NameValue) other;
        if (this == that) {
            return true;
        }
        if (this.name != null || that.name == null) {
            if (this.name != null && that.name == null) {
                return false;
            }
            if (this.name != null && that.name != null && this.name.compareToIgnoreCase(that.name) != 0) {
                return false;
            }
            if (this.value == null || that.value != null) {
                if (this.value == null && that.value != null) {
                    return false;
                }
                if (this.value == that.value) {
                    return true;
                }
                if (this.value instanceof String) {
                    if (this.isQuotedString) {
                        return this.value.equals(that.value);
                    }
                    String val = (String) this.value;
                    String val1 = (String) that.value;
                    return val.compareToIgnoreCase(val1) == 0;
                }
                return this.value.equals(that.value);
            }
            return false;
        }
        return false;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map.Entry
    public String getKey() {
        return this.name;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Map.Entry
    public String getValue() {
        if (this.value == null) {
            return null;
        }
        return this.value.toString();
    }

    @Override // java.util.Map.Entry
    public String setValue(String value) {
        String retval = this.value == null ? null : value;
        this.value = value;
        return retval;
    }

    @Override // java.util.Map.Entry
    public int hashCode() {
        return encode().toLowerCase().hashCode();
    }
}