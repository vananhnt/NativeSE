package android.sax;

import gov.nist.core.Separators;
import java.util.ArrayList;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/* loaded from: Element.class */
public class Element {
    final String uri;
    final String localName;
    final int depth;
    final Element parent;
    Children children;
    ArrayList<Element> requiredChilden;
    boolean visited;
    StartElementListener startElementListener;
    EndElementListener endElementListener;
    EndTextElementListener endTextElementListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Element(Element parent, String uri, String localName, int depth) {
        this.parent = parent;
        this.uri = uri;
        this.localName = localName;
        this.depth = depth;
    }

    public Element getChild(String localName) {
        return getChild("", localName);
    }

    public Element getChild(String uri, String localName) {
        if (this.endTextElementListener != null) {
            throw new IllegalStateException("This element already has an end text element listener. It cannot have children.");
        }
        if (this.children == null) {
            this.children = new Children();
        }
        return this.children.getOrCreate(this, uri, localName);
    }

    public Element requireChild(String localName) {
        return requireChild("", localName);
    }

    public Element requireChild(String uri, String localName) {
        Element child = getChild(uri, localName);
        if (this.requiredChilden == null) {
            this.requiredChilden = new ArrayList<>();
            this.requiredChilden.add(child);
        } else if (!this.requiredChilden.contains(child)) {
            this.requiredChilden.add(child);
        }
        return child;
    }

    public void setElementListener(ElementListener elementListener) {
        setStartElementListener(elementListener);
        setEndElementListener(elementListener);
    }

    public void setTextElementListener(TextElementListener elementListener) {
        setStartElementListener(elementListener);
        setEndTextElementListener(elementListener);
    }

    public void setStartElementListener(StartElementListener startElementListener) {
        if (this.startElementListener != null) {
            throw new IllegalStateException("Start element listener has already been set.");
        }
        this.startElementListener = startElementListener;
    }

    public void setEndElementListener(EndElementListener endElementListener) {
        if (this.endElementListener != null) {
            throw new IllegalStateException("End element listener has already been set.");
        }
        this.endElementListener = endElementListener;
    }

    public void setEndTextElementListener(EndTextElementListener endTextElementListener) {
        if (this.endTextElementListener != null) {
            throw new IllegalStateException("End text element listener has already been set.");
        }
        if (this.children != null) {
            throw new IllegalStateException("This element already has children. It cannot have an end text element listener.");
        }
        this.endTextElementListener = endTextElementListener;
    }

    public String toString() {
        return toString(this.uri, this.localName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String toString(String uri, String localName) {
        return Separators.QUOTE + (uri.equals("") ? localName : uri + Separators.COLON + localName) + Separators.QUOTE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetRequiredChildren() {
        ArrayList<Element> requiredChildren = this.requiredChilden;
        if (requiredChildren != null) {
            for (int i = requiredChildren.size() - 1; i >= 0; i--) {
                requiredChildren.get(i).visited = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkRequiredChildren(Locator locator) throws SAXParseException {
        ArrayList<Element> requiredChildren = this.requiredChilden;
        if (requiredChildren != null) {
            for (int i = requiredChildren.size() - 1; i >= 0; i--) {
                Element child = requiredChildren.get(i);
                if (!child.visited) {
                    throw new BadXmlException("Element named " + this + " is missing required child element named " + child + Separators.DOT, locator);
                }
            }
        }
    }
}