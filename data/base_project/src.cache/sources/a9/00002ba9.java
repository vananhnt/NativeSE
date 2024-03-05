package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HeaderValueFormatter.class */
public interface HeaderValueFormatter {
    CharArrayBuffer formatElements(CharArrayBuffer charArrayBuffer, HeaderElement[] headerElementArr, boolean z);

    CharArrayBuffer formatHeaderElement(CharArrayBuffer charArrayBuffer, HeaderElement headerElement, boolean z);

    CharArrayBuffer formatParameters(CharArrayBuffer charArrayBuffer, NameValuePair[] nameValuePairArr, boolean z);

    CharArrayBuffer formatNameValuePair(CharArrayBuffer charArrayBuffer, NameValuePair nameValuePair, boolean z);
}