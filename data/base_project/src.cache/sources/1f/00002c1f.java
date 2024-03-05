package org.w3c.dom.ls;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LSParserFilter.class */
public interface LSParserFilter {
    public static final short FILTER_ACCEPT = 1;
    public static final short FILTER_REJECT = 2;
    public static final short FILTER_SKIP = 3;
    public static final short FILTER_INTERRUPT = 4;

    short startElement(Element element);

    short acceptNode(Node node);

    int getWhatToShow();
}