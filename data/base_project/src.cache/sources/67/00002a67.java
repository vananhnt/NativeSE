package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HeaderElement.class */
public interface HeaderElement {
    String getName();

    String getValue();

    NameValuePair[] getParameters();

    NameValuePair getParameterByName(String str);

    int getParameterCount();

    NameValuePair getParameter(int i);
}