package javax.sql;

import java.io.PrintWriter;
import java.sql.SQLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CommonDataSource.class */
public interface CommonDataSource {
    int getLoginTimeout() throws SQLException;

    PrintWriter getLogWriter() throws SQLException;

    void setLoginTimeout(int i) throws SQLException;

    void setLogWriter(PrintWriter printWriter) throws SQLException;
}