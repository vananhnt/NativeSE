package org.w3c.dom.ls;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LSSerializer.class */
public interface LSSerializer {
    DOMConfiguration getDomConfig();

    String getNewLine();

    void setNewLine(String str);

    boolean write(Node node, LSOutput lSOutput) throws LSException;

    boolean writeToURI(Node node, String str) throws LSException;

    String writeToString(Node node) throws DOMException, LSException;
}