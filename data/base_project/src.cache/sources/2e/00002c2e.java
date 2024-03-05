package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Locator.class */
public interface Locator {
    String getPublicId();

    String getSystemId();

    int getLineNumber();

    int getColumnNumber();
}