package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NamedNodeMap.class */
public interface NamedNodeMap {
    Node getNamedItem(String str);

    Node setNamedItem(Node node) throws DOMException;

    Node removeNamedItem(String str) throws DOMException;

    Node item(int i);

    int getLength();

    Node getNamedItemNS(String str, String str2) throws DOMException;

    Node setNamedItemNS(Node node) throws DOMException;

    Node removeNamedItemNS(String str, String str2) throws DOMException;
}