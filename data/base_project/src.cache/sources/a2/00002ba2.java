package org.apache.http.message;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicListHeaderIterator.class */
public class BasicListHeaderIterator implements HeaderIterator {
    protected final List allHeaders;
    protected int currentIndex;
    protected int lastIndex;
    protected String headerName;

    public BasicListHeaderIterator(List headers, String name) {
        throw new RuntimeException("Stub!");
    }

    protected int findNext(int from) {
        throw new RuntimeException("Stub!");
    }

    protected boolean filterHeader(int index) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HeaderIterator, java.util.Iterator
    public boolean hasNext() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.HeaderIterator
    public Header nextHeader() throws NoSuchElementException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Iterator
    public final Object next() throws NoSuchElementException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new RuntimeException("Stub!");
    }
}