package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Header.class */
public interface Header {
    String getName();

    String getValue();

    HeaderElement[] getElements() throws ParseException;
}