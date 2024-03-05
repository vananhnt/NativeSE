package org.apache.http;

import java.util.Iterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HeaderIterator.class */
public interface HeaderIterator extends Iterator {
    @Override // java.util.Iterator
    boolean hasNext();

    Header nextHeader();
}