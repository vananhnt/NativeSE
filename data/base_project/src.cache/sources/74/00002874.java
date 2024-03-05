package javax.sql;

import java.sql.SQLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowSetReader.class */
public interface RowSetReader {
    void readData(RowSetInternal rowSetInternal) throws SQLException;
}