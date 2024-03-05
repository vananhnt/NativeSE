package javax.sql;

import java.sql.SQLException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowSetWriter.class */
public interface RowSetWriter {
    boolean writeData(RowSetInternal rowSetInternal) throws SQLException;
}