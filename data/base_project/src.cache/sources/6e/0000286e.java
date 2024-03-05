package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PooledConnection.class */
public interface PooledConnection {
    void addConnectionEventListener(ConnectionEventListener connectionEventListener);

    void close() throws SQLException;

    Connection getConnection() throws SQLException;

    void removeConnectionEventListener(ConnectionEventListener connectionEventListener);

    void addStatementEventListener(StatementEventListener statementEventListener);

    void removeStatementEventListener(StatementEventListener statementEventListener);
}