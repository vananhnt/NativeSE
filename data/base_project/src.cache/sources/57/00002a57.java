package org.apache.harmony.xml.dom;

import org.w3c.dom.Node;

/* loaded from: LeafNodeImpl.class */
public abstract class LeafNodeImpl extends NodeImpl {
    InnerNodeImpl parent;
    int index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LeafNodeImpl(DocumentImpl document) {
        super(document);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node getNextSibling() {
        if (this.parent == null || this.index + 1 >= this.parent.children.size()) {
            return null;
        }
        return this.parent.children.get(this.index + 1);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node getParentNode() {
        return this.parent;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public Node getPreviousSibling() {
        if (this.parent == null || this.index == 0) {
            return null;
        }
        return this.parent.children.get(this.index - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isParentOf(Node node) {
        return false;
    }
}