package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/* loaded from: NodeIterator.class */
public interface NodeIterator {
    Node getRoot();

    int getWhatToShow();

    NodeFilter getFilter();

    boolean getExpandEntityReferences();

    Node nextNode() throws DOMException;

    Node previousNode() throws DOMException;

    void detach();
}