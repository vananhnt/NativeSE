package org.apache.http;

import java.util.Iterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TokenIterator.class */
public interface TokenIterator extends Iterator {
    @Override // java.util.Iterator
    boolean hasNext();

    String nextToken();
}