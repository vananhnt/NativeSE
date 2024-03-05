package gov.nist.javax.sip.message;

import gov.nist.javax.sip.header.SIPHeader;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/* loaded from: HeaderIterator.class */
public class HeaderIterator implements ListIterator {
    private boolean toRemove;
    private int index;
    private SIPMessage sipMessage;
    private SIPHeader sipHeader;

    /* JADX INFO: Access modifiers changed from: protected */
    public HeaderIterator(SIPMessage sipMessage, SIPHeader sipHeader) {
        this.sipMessage = sipMessage;
        this.sipHeader = sipHeader;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public Object next() throws NoSuchElementException {
        if (this.sipHeader == null || this.index == 1) {
            throw new NoSuchElementException();
        }
        this.toRemove = true;
        this.index = 1;
        return this.sipHeader;
    }

    @Override // java.util.ListIterator
    public Object previous() throws NoSuchElementException {
        if (this.sipHeader == null || this.index == 0) {
            throw new NoSuchElementException();
        }
        this.toRemove = true;
        this.index = 0;
        return this.sipHeader;
    }

    @Override // java.util.ListIterator
    public int nextIndex() {
        return 1;
    }

    @Override // java.util.ListIterator
    public int previousIndex() {
        return this.index == 0 ? -1 : 0;
    }

    @Override // java.util.ListIterator
    public void set(Object header) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator
    public void add(Object header) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public void remove() throws IllegalStateException {
        if (this.sipHeader == null) {
            throw new IllegalStateException();
        }
        if (this.toRemove) {
            this.sipHeader = null;
            this.sipMessage.removeHeader(this.sipHeader.getName());
            return;
        }
        throw new IllegalStateException();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public boolean hasNext() {
        return this.index == 0;
    }

    @Override // java.util.ListIterator
    public boolean hasPrevious() {
        return this.index == 1;
    }
}