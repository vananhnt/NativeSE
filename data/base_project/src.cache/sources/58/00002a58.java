package org.apache.harmony.xml.dom;

import gov.nist.core.Separators;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/* loaded from: NodeImpl.class */
public abstract class NodeImpl implements Node {
    private static final NodeList EMPTY_LIST = new NodeListImpl();
    static final TypeInfo NULL_TYPE_INFO = new TypeInfo() { // from class: org.apache.harmony.xml.dom.NodeImpl.1
        @Override // org.w3c.dom.TypeInfo
        public String getTypeName() {
            return null;
        }

        @Override // org.w3c.dom.TypeInfo
        public String getTypeNamespace() {
            return null;
        }

        @Override // org.w3c.dom.TypeInfo
        public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
            return false;
        }
    };
    DocumentImpl document;

    @Override // org.w3c.dom.Node
    public abstract short getNodeType();

    /* JADX INFO: Access modifiers changed from: package-private */
    public NodeImpl(DocumentImpl document) {
        this.document = document;
    }

    @Override // org.w3c.dom.Node
    public Node appendChild(Node newChild) throws DOMException {
        throw new DOMException((short) 3, null);
    }

    @Override // org.w3c.dom.Node
    public final Node cloneNode(boolean deep) {
        return this.document.cloneOrImportNode((short) 1, this, deep);
    }

    @Override // org.w3c.dom.Node
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public NodeList getChildNodes() {
        return EMPTY_LIST;
    }

    @Override // org.w3c.dom.Node
    public Node getFirstChild() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public Node getLastChild() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public String getLocalName() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public String getNamespaceURI() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public Node getNextSibling() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public String getNodeName() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override // org.w3c.dom.Node
    public final Document getOwnerDocument() {
        if (this.document == this) {
            return null;
        }
        return this.document;
    }

    @Override // org.w3c.dom.Node
    public Node getParentNode() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public String getPrefix() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public Node getPreviousSibling() {
        return null;
    }

    @Override // org.w3c.dom.Node
    public boolean hasAttributes() {
        return false;
    }

    @Override // org.w3c.dom.Node
    public boolean hasChildNodes() {
        return false;
    }

    @Override // org.w3c.dom.Node
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new DOMException((short) 3, null);
    }

    @Override // org.w3c.dom.Node
    public boolean isSupported(String feature, String version) {
        return DOMImplementationImpl.getInstance().hasFeature(feature, version);
    }

    @Override // org.w3c.dom.Node
    public void normalize() {
    }

    @Override // org.w3c.dom.Node
    public Node removeChild(Node oldChild) throws DOMException {
        throw new DOMException((short) 3, null);
    }

    @Override // org.w3c.dom.Node
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new DOMException((short) 3, null);
    }

    @Override // org.w3c.dom.Node
    public final void setNodeValue(String nodeValue) throws DOMException {
        switch (getNodeType()) {
            case 1:
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
                return;
            case 2:
                ((Attr) this).setValue(nodeValue);
                return;
            case 3:
            case 4:
            case 8:
                ((CharacterData) this).setData(nodeValue);
                return;
            case 7:
                ((ProcessingInstruction) this).setData(nodeValue);
                return;
            default:
                throw new DOMException((short) 9, "Unsupported node type " + ((int) getNodeType()));
        }
    }

    @Override // org.w3c.dom.Node
    public void setPrefix(String prefix) throws DOMException {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String validatePrefix(String prefix, boolean namespaceAware, String namespaceURI) {
        if (!namespaceAware) {
            throw new DOMException((short) 14, prefix);
        }
        if (prefix != null && (namespaceURI == null || !DocumentImpl.isXMLIdentifier(prefix) || (("xml".equals(prefix) && !"http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) || ("xmlns".equals(prefix) && !"http://www.w3.org/2000/xmlns/".equals(namespaceURI))))) {
            throw new DOMException((short) 14, prefix);
        }
        return prefix;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setNameNS(NodeImpl node, String namespaceURI, String qualifiedName) {
        if (qualifiedName == null) {
            throw new DOMException((short) 14, qualifiedName);
        }
        String prefix = null;
        int p = qualifiedName.lastIndexOf(Separators.COLON);
        if (p != -1) {
            prefix = validatePrefix(qualifiedName.substring(0, p), true, namespaceURI);
            qualifiedName = qualifiedName.substring(p + 1);
        }
        if (!DocumentImpl.isXMLIdentifier(qualifiedName)) {
            throw new DOMException((short) 5, qualifiedName);
        }
        switch (node.getNodeType()) {
            case 1:
                ElementImpl element = (ElementImpl) node;
                element.namespaceAware = true;
                element.namespaceURI = namespaceURI;
                element.prefix = prefix;
                element.localName = qualifiedName;
                return;
            case 2:
                if ("xmlns".equals(qualifiedName) && !"http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
                    throw new DOMException((short) 14, qualifiedName);
                }
                AttrImpl attr = (AttrImpl) node;
                attr.namespaceAware = true;
                attr.namespaceURI = namespaceURI;
                attr.prefix = prefix;
                attr.localName = qualifiedName;
                return;
            default:
                throw new DOMException((short) 9, "Cannot rename nodes of type " + ((int) node.getNodeType()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setName(NodeImpl node, String name) {
        int prefixSeparator = name.lastIndexOf(Separators.COLON);
        if (prefixSeparator != -1) {
            String prefix = name.substring(0, prefixSeparator);
            String localName = name.substring(prefixSeparator + 1);
            if (!DocumentImpl.isXMLIdentifier(prefix) || !DocumentImpl.isXMLIdentifier(localName)) {
                throw new DOMException((short) 5, name);
            }
        } else if (!DocumentImpl.isXMLIdentifier(name)) {
            throw new DOMException((short) 5, name);
        }
        switch (node.getNodeType()) {
            case 1:
                ElementImpl element = (ElementImpl) node;
                element.namespaceAware = false;
                element.localName = name;
                return;
            case 2:
                AttrImpl attr = (AttrImpl) node;
                attr.namespaceAware = false;
                attr.localName = name;
                return;
            default:
                throw new DOMException((short) 9, "Cannot rename nodes of type " + ((int) node.getNodeType()));
        }
    }

    @Override // org.w3c.dom.Node
    public final String getBaseURI() {
        switch (getNodeType()) {
            case 1:
                Element element = (Element) this;
                String uri = element.getAttributeNS("http://www.w3.org/XML/1998/namespace", "base");
                if (uri != null) {
                    try {
                        if (!uri.isEmpty()) {
                            if (new URI(uri).isAbsolute()) {
                                return uri;
                            }
                            String parentUri = getParentBaseUri();
                            if (parentUri == null) {
                                return null;
                            }
                            return new URI(parentUri).resolve(uri).toString();
                        }
                    } catch (URISyntaxException e) {
                        return null;
                    }
                }
                return getParentBaseUri();
            case 2:
            case 3:
            case 4:
            case 8:
            case 10:
            case 11:
                return null;
            case 5:
                return null;
            case 6:
            case 12:
                return null;
            case 7:
                return getParentBaseUri();
            case 9:
                return sanitizeUri(((Document) this).getDocumentURI());
            default:
                throw new DOMException((short) 9, "Unsupported node type " + ((int) getNodeType()));
        }
    }

    private String getParentBaseUri() {
        Node parentNode = getParentNode();
        if (parentNode != null) {
            return parentNode.getBaseURI();
        }
        return null;
    }

    private String sanitizeUri(String uri) {
        if (uri == null || uri.length() == 0) {
            return null;
        }
        try {
            return new URI(uri).toString();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override // org.w3c.dom.Node
    public short compareDocumentPosition(Node other) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.Node
    public String getTextContent() throws DOMException {
        return getNodeValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getTextContent(StringBuilder buf) throws DOMException {
        String content = getNodeValue();
        if (content != null) {
            buf.append(content);
        }
    }

    @Override // org.w3c.dom.Node
    public final void setTextContent(String textContent) throws DOMException {
        switch (getNodeType()) {
            case 1:
            case 5:
            case 6:
            case 11:
                break;
            case 2:
            case 3:
            case 4:
            case 7:
            case 8:
            case 12:
                setNodeValue(textContent);
                return;
            case 9:
            case 10:
                return;
            default:
                throw new DOMException((short) 9, "Unsupported node type " + ((int) getNodeType()));
        }
        while (true) {
            Node child = getFirstChild();
            if (child != null) {
                removeChild(child);
            } else if (textContent != null && textContent.length() != 0) {
                appendChild(this.document.createTextNode(textContent));
                return;
            } else {
                return;
            }
        }
    }

    @Override // org.w3c.dom.Node
    public boolean isSameNode(Node other) {
        return this == other;
    }

    private NodeImpl getNamespacingElement() {
        switch (getNodeType()) {
            case 1:
                return this;
            case 2:
                return (NodeImpl) ((Attr) this).getOwnerElement();
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
                return getContainingElement();
            case 6:
            case 10:
            case 11:
            case 12:
                return null;
            case 9:
                return (NodeImpl) ((Document) this).getDocumentElement();
            default:
                throw new DOMException((short) 9, "Unsupported node type " + ((int) getNodeType()));
        }
    }

    private NodeImpl getContainingElement() {
        Node parentNode = getParentNode();
        while (true) {
            Node p = parentNode;
            if (p != null) {
                if (p.getNodeType() != 1) {
                    parentNode = p.getParentNode();
                } else {
                    return (NodeImpl) p;
                }
            } else {
                return null;
            }
        }
    }

    @Override // org.w3c.dom.Node
    public final String lookupPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            return null;
        }
        NodeImpl target = getNamespacingElement();
        NodeImpl nodeImpl = target;
        while (true) {
            NodeImpl node = nodeImpl;
            if (node != null) {
                if (namespaceURI.equals(node.getNamespaceURI()) && target.isPrefixMappedToUri(node.getPrefix(), namespaceURI)) {
                    return node.getPrefix();
                }
                if (node.hasAttributes()) {
                    NamedNodeMap attributes = node.getAttributes();
                    int length = attributes.getLength();
                    for (int i = 0; i < length; i++) {
                        Node attr = attributes.item(i);
                        if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI()) && "xmlns".equals(attr.getPrefix()) && namespaceURI.equals(attr.getNodeValue()) && target.isPrefixMappedToUri(attr.getLocalName(), namespaceURI)) {
                            return attr.getLocalName();
                        }
                    }
                    continue;
                }
                nodeImpl = node.getContainingElement();
            } else {
                return null;
            }
        }
    }

    boolean isPrefixMappedToUri(String prefix, String uri) {
        if (prefix == null) {
            return false;
        }
        String actual = lookupNamespaceURI(prefix);
        return uri.equals(actual);
    }

    @Override // org.w3c.dom.Node
    public final boolean isDefaultNamespace(String namespaceURI) {
        String actual = lookupNamespaceURI(null);
        return namespaceURI == null ? actual == null : namespaceURI.equals(actual);
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a5, code lost:
        r0 = r0.getNodeValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b3, code lost:
        if (r0.length() <= 0) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00bb, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:?, code lost:
        return r0;
     */
    @Override // org.w3c.dom.Node
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.String lookupNamespaceURI(java.lang.String r4) {
        /*
            r3 = this;
            r0 = r3
            org.apache.harmony.xml.dom.NodeImpl r0 = r0.getNamespacingElement()
            r5 = r0
            r0 = r5
            r6 = r0
        L7:
            r0 = r6
            if (r0 == 0) goto Lcb
            r0 = r6
            java.lang.String r0 = r0.getPrefix()
            r7 = r0
            r0 = r6
            java.lang.String r0 = r0.getNamespaceURI()
            if (r0 == 0) goto L32
            r0 = r4
            if (r0 != 0) goto L24
            r0 = r7
            if (r0 != 0) goto L32
            goto L2d
        L24:
            r0 = r4
            r1 = r7
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L32
        L2d:
            r0 = r6
            java.lang.String r0 = r0.getNamespaceURI()
            return r0
        L32:
            r0 = r6
            boolean r0 = r0.hasAttributes()
            if (r0 != 0) goto L3c
            goto Lc3
        L3c:
            r0 = r6
            org.w3c.dom.NamedNodeMap r0 = r0.getAttributes()
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = r8
            int r0 = r0.getLength()
            r10 = r0
        L4e:
            r0 = r9
            r1 = r10
            if (r0 >= r1) goto Lc3
            r0 = r8
            r1 = r9
            org.w3c.dom.Node r0 = r0.item(r1)
            r11 = r0
            java.lang.String r0 = "http://www.w3.org/2000/xmlns/"
            r1 = r11
            java.lang.String r1 = r1.getNamespaceURI()
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L72
            goto Lbd
        L72:
            r0 = r4
            if (r0 != 0) goto L88
            java.lang.String r0 = "xmlns"
            r1 = r11
            java.lang.String r1 = r1.getNodeName()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto Lbd
            goto La5
        L88:
            java.lang.String r0 = "xmlns"
            r1 = r11
            java.lang.String r1 = r1.getPrefix()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto Lbd
            r0 = r4
            r1 = r11
            java.lang.String r1 = r1.getLocalName()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto Lbd
        La5:
            r0 = r11
            java.lang.String r0 = r0.getNodeValue()
            r12 = r0
            r0 = r12
            int r0 = r0.length()
            if (r0 <= 0) goto Lbb
            r0 = r12
            goto Lbc
        Lbb:
            r0 = 0
        Lbc:
            return r0
        Lbd:
            int r9 = r9 + 1
            goto L4e
        Lc3:
            r0 = r6
            org.apache.harmony.xml.dom.NodeImpl r0 = r0.getContainingElement()
            r6 = r0
            goto L7
        Lcb:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.NodeImpl.lookupNamespaceURI(java.lang.String):java.lang.String");
    }

    private static List<Object> createEqualityKey(Node node) {
        List<Object> values = new ArrayList<>();
        values.add(Short.valueOf(node.getNodeType()));
        values.add(node.getNodeName());
        values.add(node.getLocalName());
        values.add(node.getNamespaceURI());
        values.add(node.getPrefix());
        values.add(node.getNodeValue());
        Node firstChild = node.getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child == null) {
                break;
            }
            values.add(child);
            firstChild = child.getNextSibling();
        }
        switch (node.getNodeType()) {
            case 1:
                Element element = (Element) node;
                values.add(element.getAttributes());
                break;
            case 10:
                DocumentTypeImpl doctype = (DocumentTypeImpl) node;
                values.add(doctype.getPublicId());
                values.add(doctype.getSystemId());
                values.add(doctype.getInternalSubset());
                values.add(doctype.getEntities());
                values.add(doctype.getNotations());
                break;
        }
        return values;
    }

    @Override // org.w3c.dom.Node
    public final boolean isEqualNode(Node arg) {
        if (arg == this) {
            return true;
        }
        List<Object> listA = createEqualityKey(this);
        List<Object> listB = createEqualityKey(arg);
        if (listA.size() != listB.size()) {
            return false;
        }
        for (int i = 0; i < listA.size(); i++) {
            Object a = listA.get(i);
            Object b = listB.get(i);
            if (a != b) {
                if (a == null || b == null) {
                    return false;
                }
                if ((a instanceof String) || (a instanceof Short)) {
                    if (!a.equals(b)) {
                        return false;
                    }
                } else if (a instanceof NamedNodeMap) {
                    if (!(b instanceof NamedNodeMap) || !namedNodeMapsEqual((NamedNodeMap) a, (NamedNodeMap) b)) {
                        return false;
                    }
                } else if (a instanceof Node) {
                    if (!(b instanceof Node) || !((Node) a).isEqualNode((Node) b)) {
                        return false;
                    }
                } else {
                    throw new AssertionError();
                }
            }
        }
        return true;
    }

    private boolean namedNodeMapsEqual(NamedNodeMap a, NamedNodeMap b) {
        if (a.getLength() != b.getLength()) {
            return false;
        }
        for (int i = 0; i < a.getLength(); i++) {
            Node aNode = a.item(i);
            Node bNode = aNode.getLocalName() == null ? b.getNamedItem(aNode.getNodeName()) : b.getNamedItemNS(aNode.getNamespaceURI(), aNode.getLocalName());
            if (bNode == null || !aNode.isEqualNode(bNode)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.w3c.dom.Node
    public final Object getFeature(String feature, String version) {
        if (isSupported(feature, version)) {
            return this;
        }
        return null;
    }

    @Override // org.w3c.dom.Node
    public final Object setUserData(String key, Object data, UserDataHandler handler) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        Map<String, UserData> map = this.document.getUserDataMap(this);
        UserData previous = data == null ? map.remove(key) : map.put(key, new UserData(data, handler));
        if (previous != null) {
            return previous.value;
        }
        return null;
    }

    @Override // org.w3c.dom.Node
    public final Object getUserData(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        Map<String, UserData> map = this.document.getUserDataMapForRead(this);
        UserData userData = map.get(key);
        if (userData != null) {
            return userData.value;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: NodeImpl$UserData.class */
    public static class UserData {
        final Object value;
        final UserDataHandler handler;

        UserData(Object value, UserDataHandler handler) {
            this.value = value;
            this.handler = handler;
        }
    }
}