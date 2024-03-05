package org.apache.harmony.xml.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* loaded from: TextImpl.class */
public class TextImpl extends CharacterDataImpl implements Text {
    public TextImpl(DocumentImpl document, String data) {
        super(document, data);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return "#text";
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 3;
    }

    @Override // org.w3c.dom.Text
    public final Text splitText(int offset) throws DOMException {
        Text newText = this.document.createTextNode(substringData(offset, getLength() - offset));
        deleteData(0, offset);
        Node refNode = getNextSibling();
        if (refNode == null) {
            getParentNode().appendChild(newText);
        } else {
            getParentNode().insertBefore(newText, refNode);
        }
        return this;
    }

    @Override // org.w3c.dom.Text
    public final boolean isElementContentWhitespace() {
        return false;
    }

    @Override // org.w3c.dom.Text
    public final String getWholeText() {
        StringBuilder result = new StringBuilder();
        TextImpl firstTextNodeInCurrentRun = firstTextNodeInCurrentRun();
        while (true) {
            TextImpl n = firstTextNodeInCurrentRun;
            if (n != null) {
                n.appendDataTo(result);
                firstTextNodeInCurrentRun = n.nextTextNode();
            } else {
                return result.toString();
            }
        }
    }

    @Override // org.w3c.dom.Text
    public final Text replaceWholeText(String content) throws DOMException {
        Node parent = getParentNode();
        Text result = null;
        Node n = firstTextNodeInCurrentRun();
        while (n != null) {
            if (n == this && content != null && content.length() > 0) {
                setData(content);
                result = this;
                n = n.nextTextNode();
            } else {
                Node toRemove = n;
                n = n.nextTextNode();
                parent.removeChild(toRemove);
            }
        }
        return result;
    }

    private TextImpl firstTextNodeInCurrentRun() {
        short nodeType;
        TextImpl firstTextInCurrentRun = this;
        Node previousSibling = getPreviousSibling();
        while (true) {
            Node p = previousSibling;
            if (p == null || !((nodeType = p.getNodeType()) == 3 || nodeType == 4)) {
                break;
            }
            firstTextInCurrentRun = (TextImpl) p;
            previousSibling = p.getPreviousSibling();
        }
        return firstTextInCurrentRun;
    }

    private TextImpl nextTextNode() {
        Node nextSibling = getNextSibling();
        if (nextSibling == null) {
            return null;
        }
        short nodeType = nextSibling.getNodeType();
        if (nodeType == 3 || nodeType == 4) {
            return (TextImpl) nextSibling;
        }
        return null;
    }

    public final TextImpl minimize() {
        if (getLength() == 0) {
            this.parent.removeChild(this);
            return null;
        }
        Node previous = getPreviousSibling();
        if (previous == null || previous.getNodeType() != 3) {
            return this;
        }
        TextImpl previousText = (TextImpl) previous;
        previousText.buffer.append(this.buffer);
        this.parent.removeChild(this);
        return previousText;
    }
}