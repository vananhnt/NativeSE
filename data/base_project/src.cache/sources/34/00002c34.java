package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XMLFilter.class */
public interface XMLFilter extends XMLReader {
    void setParent(XMLReader xMLReader);

    XMLReader getParent();
}