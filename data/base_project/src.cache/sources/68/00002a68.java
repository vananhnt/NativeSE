package org.apache.http;

import java.util.Iterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HeaderElementIterator.class */
public interface HeaderElementIterator extends Iterator {
    @Override // java.util.Iterator
    boolean hasNext();

    HeaderElement nextElement();
}