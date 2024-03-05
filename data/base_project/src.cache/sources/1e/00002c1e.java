package org.w3c.dom.ls;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LSParser.class */
public interface LSParser {
    public static final short ACTION_APPEND_AS_CHILDREN = 1;
    public static final short ACTION_REPLACE_CHILDREN = 2;
    public static final short ACTION_INSERT_BEFORE = 3;
    public static final short ACTION_INSERT_AFTER = 4;
    public static final short ACTION_REPLACE = 5;

    DOMConfiguration getDomConfig();

    LSParserFilter getFilter();

    void setFilter(LSParserFilter lSParserFilter);

    boolean getAsync();

    boolean getBusy();

    Document parse(LSInput lSInput) throws DOMException, LSException;

    Document parseURI(String str) throws DOMException, LSException;

    Node parseWithContext(LSInput lSInput, Node node, short s) throws DOMException, LSException;

    void abort();
}