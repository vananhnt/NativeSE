package javax.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowSetInternal.class */
public interface RowSetInternal {
    Connection getConnection() throws SQLException;

    ResultSet getOriginal() throws SQLException;

    ResultSet getOriginalRow() throws SQLException;

    Object[] getParams() throws SQLException;

    void setMetaData(RowSetMetaData rowSetMetaData) throws SQLException;
}