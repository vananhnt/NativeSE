package javax.xml.transform.dom;

import javax.xml.transform.Result;
import org.w3c.dom.Node;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DOMResult.class */
public class DOMResult implements Result {
    public static final String FEATURE = "http://javax.xml.transform.dom.DOMResult/feature";
    private Node node = null;
    private Node nextSibling = null;
    private String systemId = null;

    public DOMResult() {
        setNode(null);
        setNextSibling(null);
        setSystemId(null);
    }

    public DOMResult(Node node) {
        setNode(node);
        setNextSibling(null);
        setSystemId(null);
    }

    public DOMResult(Node node, String systemId) {
        setNode(node);
        setNextSibling(null);
        setSystemId(systemId);
    }

    public DOMResult(Node node, Node nextSibling) {
        if (nextSibling != null) {
            if (node == null) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((node.compareDocumentPosition(nextSibling) & 16) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        setNode(node);
        setNextSibling(nextSibling);
        setSystemId(null);
    }

    public DOMResult(Node node, Node nextSibling, String systemId) {
        if (nextSibling != null) {
            if (node == null) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((node.compareDocumentPosition(nextSibling) & 16) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        setNode(node);
        setNextSibling(nextSibling);
        setSystemId(systemId);
    }

    public void setNode(Node node) {
        if (this.nextSibling != null) {
            if (node == null) {
                throw new IllegalStateException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((node.compareDocumentPosition(this.nextSibling) & 16) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        this.node = node;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNextSibling(Node nextSibling) {
        if (nextSibling != null) {
            if (this.node == null) {
                throw new IllegalStateException("Cannot create a DOMResult when the nextSibling is contained by the \"null\" node.");
            }
            if ((this.node.compareDocumentPosition(nextSibling) & 16) == 0) {
                throw new IllegalArgumentException("Cannot create a DOMResult when the nextSibling is not contained by the node.");
            }
        }
        this.nextSibling = nextSibling;
    }

    public Node getNextSibling() {
        return this.nextSibling;
    }

    @Override // javax.xml.transform.Result
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override // javax.xml.transform.Result
    public String getSystemId() {
        return this.systemId;
    }
}