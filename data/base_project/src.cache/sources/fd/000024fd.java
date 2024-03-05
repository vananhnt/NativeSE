package java.sql;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowId.class */
public interface RowId {
    boolean equals(Object obj);

    byte[] getBytes();

    String toString();

    int hashCode();
}