package org.w3c.dom.ls;

import org.w3c.dom.DOMException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DOMImplementationLS.class */
public interface DOMImplementationLS {
    public static final short MODE_SYNCHRONOUS = 1;
    public static final short MODE_ASYNCHRONOUS = 2;

    LSParser createLSParser(short s, String str) throws DOMException;

    LSSerializer createLSSerializer();

    LSInput createLSInput();

    LSOutput createLSOutput();
}