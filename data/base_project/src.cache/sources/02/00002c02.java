package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DOMConfiguration.class */
public interface DOMConfiguration {
    void setParameter(String str, Object obj) throws DOMException;

    Object getParameter(String str) throws DOMException;

    boolean canSetParameter(String str, Object obj);

    DOMStringList getParameterNames();
}