package org.apache.harmony.xml.dom;

import gov.nist.core.Separators;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

/* loaded from: AttrImpl.class */
public final class AttrImpl extends NodeImpl implements Attr {
    ElementImpl ownerElement;
    boolean isId;
    boolean namespaceAware;
    String namespaceURI;
    String prefix;
    String localName;
    private String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AttrImpl(DocumentImpl document, String namespaceURI, String qualifiedName) {
        super(document);
        this.value = "";
        setNameNS(this, namespaceURI, qualifiedName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AttrImpl(DocumentImpl document, String name) {
        super(document);
        this.value = "";
        setName(this, name);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getLocalName() {
        if (this.namespaceAware) {
            return this.localName;
        }
        return null;
    }

    @Override // org.w3c.dom.Attr
    public String getName() {
        return this.prefix != null ? this.prefix + Separators.COLON + this.localName : this.localName;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return getName();
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 2;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeValue() {
        return getValue();
    }

    @Override // org.w3c.dom.Attr
    public Element getOwnerElement() {
        return this.ownerElement;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getPrefix() {
        return this.prefix;
    }

    @Override // org.w3c.dom.Attr
    public boolean getSpecified() {
        return this.value != null;
    }

    @Override // org.w3c.dom.Attr
    public String getValue() {
        return this.value;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public void setPrefix(String prefix) {
        this.prefix = validatePrefix(prefix, this.namespaceAware, this.namespaceURI);
    }

    @Override // org.w3c.dom.Attr
    public void setValue(String value) throws DOMException {
        this.value = value;
    }

    @Override // org.w3c.dom.Attr
    public TypeInfo getSchemaTypeInfo() {
        return NULL_TYPE_INFO;
    }

    @Override // org.w3c.dom.Attr
    public boolean isId() {
        return this.isId;
    }
}