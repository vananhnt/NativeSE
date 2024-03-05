package java.sql;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SQLData.class */
public interface SQLData {
    String getSQLTypeName() throws SQLException;

    void readSQL(SQLInput sQLInput, String str) throws SQLException;

    void writeSQL(SQLOutput sQLOutput) throws SQLException;
}