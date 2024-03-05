package org.apache.harmony.xml.dom;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: NodeListImpl.class */
public class NodeListImpl implements NodeList {
    private List<NodeImpl> children;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NodeListImpl() {
        this.children = new ArrayList();
    }

    NodeListImpl(List<NodeImpl> list) {
        this.children = list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void add(NodeImpl node) {
        this.children.add(node);
    }

    @Override // org.w3c.dom.NodeList
    public int getLength() {
        return this.children.size();
    }

    @Override // org.w3c.dom.NodeList
    public Node item(int index) {
        if (index >= this.children.size()) {
            return null;
        }
        return this.children.get(index);
    }
}