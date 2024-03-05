package java.sql;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Savepoint.class */
public interface Savepoint {
    int getSavepointId() throws SQLException;

    String getSavepointName() throws SQLException;
}