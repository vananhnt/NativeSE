package org.apache.harmony.xml.dom;

import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

/* loaded from: ElementImpl.class */
public class ElementImpl extends InnerNodeImpl implements Element {
    boolean namespaceAware;
    String namespaceURI;
    String prefix;
    String localName;
    private List<AttrImpl> attributes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ElementImpl(DocumentImpl document, String namespaceURI, String qualifiedName) {
        super(document);
        this.attributes = new ArrayList();
        setNameNS(this, namespaceURI, qualifiedName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ElementImpl(DocumentImpl document, String name) {
        super(document);
        this.attributes = new ArrayList();
        setName(this, name);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int indexOfAttribute(String name) {
        for (int i = 0; i < this.attributes.size(); i++) {
            AttrImpl attr = this.attributes.get(i);
            if (Objects.equal(name, attr.getNodeName())) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int indexOfAttributeNS(String namespaceURI, String localName) {
        for (int i = 0; i < this.attributes.size(); i++) {
            AttrImpl attr = this.attributes.get(i);
            if (Objects.equal(namespaceURI, attr.getNamespaceURI()) && Objects.equal(localName, attr.getLocalName())) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.w3c.dom.Element
    public String getAttribute(String name) {
        Attr attr = getAttributeNode(name);
        if (attr == null) {
            return "";
        }
        return attr.getValue();
    }

    @Override // org.w3c.dom.Element
    public String getAttributeNS(String namespaceURI, String localName) {
        Attr attr = getAttributeNodeNS(namespaceURI, localName);
        if (attr == null) {
            return "";
        }
        return attr.getValue();
    }

    @Override // org.w3c.dom.Element
    public AttrImpl getAttributeNode(String name) {
        int i = indexOfAttribute(name);
        if (i == -1) {
            return null;
        }
        return this.attributes.get(i);
    }

    @Override // org.w3c.dom.Element
    public AttrImpl getAttributeNodeNS(String namespaceURI, String localName) {
        int i = indexOfAttributeNS(namespaceURI, localName);
        if (i == -1) {
            return null;
        }
        return this.attributes.get(i);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public NamedNodeMap getAttributes() {
        return new ElementAttrNamedNodeMapImpl();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Element getElementById(String name) {
        Element element;
        for (AttrImpl attr : this.attributes) {
            if (attr.isId() && name.equals(attr.getValue())) {
                return this;
            }
        }
        if (name.equals(getAttribute("id"))) {
            return this;
        }
        for (LeafNodeImpl node : this.children) {
            if (node.getNodeType() == 1 && (element = ((ElementImpl) node).getElementById(name)) != null) {
                return element;
            }
        }
        return null;
    }

    @Override // org.w3c.dom.Element
    public NodeList getElementsByTagName(String name) {
        NodeListImpl result = new NodeListImpl();
        getElementsByTagName(result, name);
        return result;
    }

    @Override // org.w3c.dom.Element
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        NodeListImpl result = new NodeListImpl();
        getElementsByTagNameNS(result, namespaceURI, localName);
        return result;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getLocalName() {
        if (this.namespaceAware) {
            return this.localName;
        }
        return null;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return getTagName();
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 1;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getPrefix() {
        return this.prefix;
    }

    @Override // org.w3c.dom.Element
    public String getTagName() {
        return this.prefix != null ? this.prefix + Separators.COLON + this.localName : this.localName;
    }

    @Override // org.w3c.dom.Element
    public boolean hasAttribute(String name) {
        return indexOfAttribute(name) != -1;
    }

    @Override // org.w3c.dom.Element
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return indexOfAttributeNS(namespaceURI, localName) != -1;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    @Override // org.w3c.dom.Element
    public void removeAttribute(String name) throws DOMException {
        int i = indexOfAttribute(name);
        if (i != -1) {
            this.attributes.remove(i);
        }
    }

    @Override // org.w3c.dom.Element
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        int i = indexOfAttributeNS(namespaceURI, localName);
        if (i != -1) {
            this.attributes.remove(i);
        }
    }

    @Override // org.w3c.dom.Element
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        AttrImpl oldAttrImpl = (AttrImpl) oldAttr;
        if (oldAttrImpl.getOwnerElement() != this) {
            throw new DOMException((short) 8, null);
        }
        this.attributes.remove(oldAttrImpl);
        oldAttrImpl.ownerElement = null;
        return oldAttrImpl;
    }

    @Override // org.w3c.dom.Element
    public void setAttribute(String name, String value) throws DOMException {
        Attr attr = getAttributeNode(name);
        if (attr == null) {
            attr = this.document.createAttribute(name);
            setAttributeNode(attr);
        }
        attr.setValue(value);
    }

    @Override // org.w3c.dom.Element
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        Attr attr = getAttributeNodeNS(namespaceURI, qualifiedName);
        if (attr == null) {
            attr = this.document.createAttributeNS(namespaceURI, qualifiedName);
            setAttributeNodeNS(attr);
        }
        attr.setValue(value);
    }

    @Override // org.w3c.dom.Element
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        AttrImpl newAttrImpl = (AttrImpl) newAttr;
        if (newAttrImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        }
        if (newAttrImpl.getOwnerElement() != null) {
            throw new DOMException((short) 10, null);
        }
        AttrImpl oldAttrImpl = null;
        int i = indexOfAttribute(newAttr.getName());
        if (i != -1) {
            oldAttrImpl = this.attributes.get(i);
            this.attributes.remove(i);
        }
        this.attributes.add(newAttrImpl);
        newAttrImpl.ownerElement = this;
        return oldAttrImpl;
    }

    @Override // org.w3c.dom.Element
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        AttrImpl newAttrImpl = (AttrImpl) newAttr;
        if (newAttrImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        }
        if (newAttrImpl.getOwnerElement() != null) {
            throw new DOMException((short) 10, null);
        }
        AttrImpl oldAttrImpl = null;
        int i = indexOfAttributeNS(newAttr.getNamespaceURI(), newAttr.getLocalName());
        if (i != -1) {
            oldAttrImpl = this.attributes.get(i);
            this.attributes.remove(i);
        }
        this.attributes.add(newAttrImpl);
        newAttrImpl.ownerElement = this;
        return oldAttrImpl;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public void setPrefix(String prefix) {
        this.prefix = validatePrefix(prefix, this.namespaceAware, this.namespaceURI);
    }

    /* loaded from: ElementImpl$ElementAttrNamedNodeMapImpl.class */
    public class ElementAttrNamedNodeMapImpl implements NamedNodeMap {
        public ElementAttrNamedNodeMapImpl() {
        }

        @Override // org.w3c.dom.NamedNodeMap
        public int getLength() {
            return ElementImpl.this.attributes.size();
        }

        private int indexOfItem(String name) {
            return ElementImpl.this.indexOfAttribute(name);
        }

        private int indexOfItemNS(String namespaceURI, String localName) {
            return ElementImpl.this.indexOfAttributeNS(namespaceURI, localName);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node getNamedItem(String name) {
            return ElementImpl.this.getAttributeNode(name);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node getNamedItemNS(String namespaceURI, String localName) {
            return ElementImpl.this.getAttributeNodeNS(namespaceURI, localName);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node item(int index) {
            return (Node) ElementImpl.this.attributes.get(index);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node removeNamedItem(String name) throws DOMException {
            int i = indexOfItem(name);
            if (i != -1) {
                return (Node) ElementImpl.this.attributes.remove(i);
            }
            throw new DOMException((short) 8, null);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
            int i = indexOfItemNS(namespaceURI, localName);
            if (i != -1) {
                return (Node) ElementImpl.this.attributes.remove(i);
            }
            throw new DOMException((short) 8, null);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node setNamedItem(Node arg) throws DOMException {
            if (!(arg instanceof Attr)) {
                throw new DOMException((short) 3, null);
            }
            return ElementImpl.this.setAttributeNode((Attr) arg);
        }

        @Override // org.w3c.dom.NamedNodeMap
        public Node setNamedItemNS(Node arg) throws DOMException {
            if (!(arg instanceof Attr)) {
                throw new DOMException((short) 3, null);
            }
            return ElementImpl.this.setAttributeNodeNS((Attr) arg);
        }
    }

    @Override // org.w3c.dom.Element
    public TypeInfo getSchemaTypeInfo() {
        return NULL_TYPE_INFO;
    }

    @Override // org.w3c.dom.Element
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        AttrImpl attr = getAttributeNode(name);
        if (attr == null) {
            throw new DOMException((short) 8, "No such attribute: " + name);
        }
        attr.isId = isId;
    }

    @Override // org.w3c.dom.Element
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        AttrImpl attr = getAttributeNodeNS(namespaceURI, localName);
        if (attr == null) {
            throw new DOMException((short) 8, "No such attribute: " + namespaceURI + Separators.SP + localName);
        }
        attr.isId = isId;
    }

    @Override // org.w3c.dom.Element
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        ((AttrImpl) idAttr).isId = isId;
    }
}