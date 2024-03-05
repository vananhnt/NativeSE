package java.sql;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SQLException.class */
public class SQLException extends Exception implements Serializable, Iterable<Throwable> {
    public SQLException() {
        throw new RuntimeException("Stub!");
    }

    public SQLException(String theReason) {
        throw new RuntimeException("Stub!");
    }

    public SQLException(String theReason, String theSQLState) {
        throw new RuntimeException("Stub!");
    }

    public SQLException(String theReason, String theSQLState, int theErrorCode) {
        throw new RuntimeException("Stub!");
    }

    public SQLException(Throwable theCause) {
        throw new RuntimeException("Stub!");
    }

    public SQLException(String theReason, Throwable theCause) {
        throw new RuntimeException("Stub!");
    }

    public SQLException(String theReason, String theSQLState, Throwable theCause) {
        throw new RuntimeException("Stub!");
    }

    public SQLException(String theReason, String theSQLState, int theErrorCode, Throwable theCause) {
        throw new RuntimeException("Stub!");
    }

    public int getErrorCode() {
        throw new RuntimeException("Stub!");
    }

    public SQLException getNextException() {
        throw new RuntimeException("Stub!");
    }

    public String getSQLState() {
        throw new RuntimeException("Stub!");
    }

    public void setNextException(SQLException ex) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Iterable
    public Iterator<Throwable> iterator() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: SQLException$InternalIterator.class */
    private static class InternalIterator implements Iterator<Throwable> {
        private SQLException current;

        InternalIterator(SQLException e) {
            this.current = e;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Throwable next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            SQLException ret = this.current;
            this.current = SQLException.access$000(this.current);
            return ret;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}