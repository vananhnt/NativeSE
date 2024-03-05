package org.apache.harmony.xml.dom;

import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: InnerNodeImpl.class */
public abstract class InnerNodeImpl extends LeafNodeImpl {
    List<LeafNodeImpl> children;

    /* JADX INFO: Access modifiers changed from: protected */
    public InnerNodeImpl(DocumentImpl document) {
        super(document);
        this.children = new ArrayList();
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node appendChild(Node newChild) throws DOMException {
        return insertChildAt(newChild, this.children.size());
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public NodeList getChildNodes() {
        NodeListImpl list = new NodeListImpl();
        for (LeafNodeImpl node : this.children) {
            list.add(node);
        }
        return list;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node getFirstChild() {
        if (this.children.isEmpty()) {
            return null;
        }
        return this.children.get(0);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node getLastChild() {
        if (this.children.isEmpty()) {
            return null;
        }
        return this.children.get(this.children.size() - 1);
    }

    @Override // org.apache.harmony.xml.dom.LeafNodeImpl, org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node getNextSibling() {
        if (this.parent == null || this.index + 1 >= this.parent.children.size()) {
            return null;
        }
        return this.parent.children.get(this.index + 1);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public boolean hasChildNodes() {
        return this.children.size() != 0;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        LeafNodeImpl refChildImpl = (LeafNodeImpl) refChild;
        if (refChildImpl == null) {
            return appendChild(newChild);
        }
        if (refChildImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        }
        if (refChildImpl.parent != this) {
            throw new DOMException((short) 3, null);
        }
        return insertChildAt(newChild, refChildImpl.index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Node insertChildAt(Node newChild, int index) throws DOMException {
        if (newChild instanceof DocumentFragment) {
            NodeList toAdd = newChild.getChildNodes();
            for (int i = 0; i < toAdd.getLength(); i++) {
                insertChildAt(toAdd.item(i), index + i);
            }
            return newChild;
        }
        LeafNodeImpl toInsert = (LeafNodeImpl) newChild;
        if (toInsert.document != null && this.document != null && toInsert.document != this.document) {
            throw new DOMException((short) 4, null);
        }
        if (toInsert.isParentOf(this)) {
            throw new DOMException((short) 3, null);
        }
        if (toInsert.parent != null) {
            int oldIndex = toInsert.index;
            toInsert.parent.children.remove(oldIndex);
            toInsert.parent.refreshIndices(oldIndex);
        }
        this.children.add(index, toInsert);
        toInsert.parent = this;
        refreshIndices(index);
        return newChild;
    }

    @Override // org.apache.harmony.xml.dom.LeafNodeImpl
    public boolean isParentOf(Node node) {
        LeafNodeImpl leafNodeImpl = (LeafNodeImpl) node;
        while (true) {
            LeafNodeImpl nodeImpl = leafNodeImpl;
            if (nodeImpl != null) {
                if (nodeImpl == this) {
                    return true;
                }
                leafNodeImpl = nodeImpl.parent;
            } else {
                return false;
            }
        }
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public final void normalize() {
        Node firstChild = getFirstChild();
        while (true) {
            Node node = firstChild;
            if (node != null) {
                Node next = node.getNextSibling();
                node.normalize();
                if (node.getNodeType() == 3) {
                    ((TextImpl) node).minimize();
                }
                firstChild = next;
            } else {
                return;
            }
        }
    }

    private void refreshIndices(int fromIndex) {
        for (int i = fromIndex; i < this.children.size(); i++) {
            this.children.get(i).index = i;
        }
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node removeChild(Node oldChild) throws DOMException {
        LeafNodeImpl oldChildImpl = (LeafNodeImpl) oldChild;
        if (oldChildImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        }
        if (oldChildImpl.parent != this) {
            throw new DOMException((short) 3, null);
        }
        int index = oldChildImpl.index;
        this.children.remove(index);
        oldChildImpl.parent = null;
        refreshIndices(index);
        return oldChild;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        int index = ((LeafNodeImpl) oldChild).index;
        removeChild(oldChild);
        insertChildAt(newChild, index);
        return oldChild;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getTextContent() throws DOMException {
        Node child = getFirstChild();
        if (child == null) {
            return "";
        }
        Node next = child.getNextSibling();
        if (next == null) {
            return hasTextContent(child) ? child.getTextContent() : "";
        }
        StringBuilder buf = new StringBuilder();
        getTextContent(buf);
        return buf.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.harmony.xml.dom.NodeImpl
    public void getTextContent(StringBuilder buf) throws DOMException {
        Node firstChild = getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child != null) {
                if (hasTextContent(child)) {
                    ((NodeImpl) child).getTextContent(buf);
                }
                firstChild = child.getNextSibling();
            } else {
                return;
            }
        }
    }

    final boolean hasTextContent(Node child) {
        return (child.getNodeType() == 8 || child.getNodeType() == 7) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getElementsByTagName(NodeListImpl out, String name) {
        for (LeafNodeImpl node : this.children) {
            if (node.getNodeType() == 1) {
                ElementImpl element = (ElementImpl) node;
                if (matchesNameOrWildcard(name, element.getNodeName())) {
                    out.add(element);
                }
                element.getElementsByTagName(out, name);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getElementsByTagNameNS(NodeListImpl out, String namespaceURI, String localName) {
        for (LeafNodeImpl node : this.children) {
            if (node.getNodeType() == 1) {
                ElementImpl element = (ElementImpl) node;
                if (matchesNameOrWildcard(namespaceURI, element.getNamespaceURI()) && matchesNameOrWildcard(localName, element.getLocalName())) {
                    out.add(element);
                }
                element.getElementsByTagNameNS(out, namespaceURI, localName);
            }
        }
    }

    private static boolean matchesNameOrWildcard(String pattern, String s) {
        return "*".equals(pattern) || Objects.equal(pattern, s);
    }
}