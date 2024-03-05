package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Node.class */
public interface Node {
    public static final short ELEMENT_NODE = 1;
    public static final short ATTRIBUTE_NODE = 2;
    public static final short TEXT_NODE = 3;
    public static final short CDATA_SECTION_NODE = 4;
    public static final short ENTITY_REFERENCE_NODE = 5;
    public static final short ENTITY_NODE = 6;
    public static final short PROCESSING_INSTRUCTION_NODE = 7;
    public static final short COMMENT_NODE = 8;
    public static final short DOCUMENT_NODE = 9;
    public static final short DOCUMENT_TYPE_NODE = 10;
    public static final short DOCUMENT_FRAGMENT_NODE = 11;
    public static final short NOTATION_NODE = 12;
    public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
    public static final short DOCUMENT_POSITION_PRECEDING = 2;
    public static final short DOCUMENT_POSITION_FOLLOWING = 4;
    public static final short DOCUMENT_POSITION_CONTAINS = 8;
    public static final short DOCUMENT_POSITION_CONTAINED_BY = 16;
    public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;

    String getNodeName();

    String getNodeValue() throws DOMException;

    void setNodeValue(String str) throws DOMException;

    short getNodeType();

    Node getParentNode();

    NodeList getChildNodes();

    Node getFirstChild();

    Node getLastChild();

    Node getPreviousSibling();

    Node getNextSibling();

    NamedNodeMap getAttributes();

    Document getOwnerDocument();

    Node insertBefore(Node node, Node node2) throws DOMException;

    Node replaceChild(Node node, Node node2) throws DOMException;

    Node removeChild(Node node) throws DOMException;

    Node appendChild(Node node) throws DOMException;

    boolean hasChildNodes();

    Node cloneNode(boolean z);

    void normalize();

    boolean isSupported(String str, String str2);

    String getNamespaceURI();

    String getPrefix();

    void setPrefix(String str) throws DOMException;

    String getLocalName();

    boolean hasAttributes();

    String getBaseURI();

    short compareDocumentPosition(Node node) throws DOMException;

    String getTextContent() throws DOMException;

    void setTextContent(String str) throws DOMException;

    boolean isSameNode(Node node);

    String lookupPrefix(String str);

    boolean isDefaultNamespace(String str);

    String lookupNamespaceURI(String str);

    boolean isEqualNode(Node node);

    Object getFeature(String str, String str2);

    Object setUserData(String str, Object obj, UserDataHandler userDataHandler);

    Object getUserData(String str);
}