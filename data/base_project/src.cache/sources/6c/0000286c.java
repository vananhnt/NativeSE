package javax.sql;

import java.sql.SQLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnectionPoolDataSource.class */
public interface ConnectionPoolDataSource extends CommonDataSource {
    PooledConnection getPooledConnection() throws SQLException;

    PooledConnection getPooledConnection(String str, String str2) throws SQLException;
}