package org.ccil.cowan.tagsoup;

/* loaded from: ElementType.class */
public class ElementType {
    private String theName;
    private String theNamespace;
    private String theLocalName;
    private int theModel;
    private int theMemberOf;
    private int theFlags;
    private AttributesImpl theAtts = new AttributesImpl();
    private ElementType theParent;
    private Schema theSchema;

    public ElementType(String name, int model, int memberOf, int flags, Schema schema) {
        this.theName = name;
        this.theModel = model;
        this.theMemberOf = memberOf;
        this.theFlags = flags;
        this.theSchema = schema;
        this.theNamespace = namespace(name, false);
        this.theLocalName = localName(name);
    }

    public String namespace(String name, boolean attribute) {
        int colon = name.indexOf(58);
        if (colon == -1) {
            return attribute ? "" : this.theSchema.getURI();
        }
        String prefix = name.substring(0, colon);
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        return ("urn:x-prefix:" + prefix).intern();
    }

    public String localName(String name) {
        int colon = name.indexOf(58);
        if (colon == -1) {
            return name;
        }
        return name.substring(colon + 1).intern();
    }

    public String name() {
        return this.theName;
    }

    public String namespace() {
        return this.theNamespace;
    }

    public String localName() {
        return this.theLocalName;
    }

    public int model() {
        return this.theModel;
    }

    public int memberOf() {
        return this.theMemberOf;
    }

    public int flags() {
        return this.theFlags;
    }

    public AttributesImpl atts() {
        return this.theAtts;
    }

    public ElementType parent() {
        return this.theParent;
    }

    public Schema schema() {
        return this.theSchema;
    }

    public boolean canContain(ElementType other) {
        return (this.theModel & other.theMemberOf) != 0;
    }

    public void setAttribute(AttributesImpl atts, String name, String type, String value) {
        if (name.equals("xmlns") || name.startsWith("xmlns:")) {
            return;
        }
        String namespace = namespace(name, true);
        String localName = localName(name);
        int i = atts.getIndex(name);
        if (i == -1) {
            String name2 = name.intern();
            if (type == null) {
                type = "CDATA";
            }
            if (!type.equals("CDATA")) {
                value = normalize(value);
            }
            atts.addAttribute(namespace, localName, name2, type, value);
            return;
        }
        if (type == null) {
            type = atts.getType(i);
        }
        if (!type.equals("CDATA")) {
            value = normalize(value);
        }
        atts.setAttribute(i, namespace, localName, name, type, value);
    }

    public static String normalize(String value) {
        boolean z;
        if (value == null) {
            return value;
        }
        String value2 = value.trim();
        if (value2.indexOf("  ") == -1) {
            return value2;
        }
        boolean space = false;
        int len = value2.length();
        StringBuffer b = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            char v = value2.charAt(i);
            if (v == ' ') {
                if (!space) {
                    b.append(v);
                }
                z = true;
            } else {
                b.append(v);
                z = false;
            }
            space = z;
        }
        return b.toString();
    }

    public void setAttribute(String name, String type, String value) {
        setAttribute(this.theAtts, name, type, value);
    }

    public void setModel(int model) {
        this.theModel = model;
    }

    public void setMemberOf(int memberOf) {
        this.theMemberOf = memberOf;
    }

    public void setFlags(int flags) {
        this.theFlags = flags;
    }

    public void setParent(ElementType parent) {
        this.theParent = parent;
    }
}