package java.sql;

import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Array.class */
public interface Array {
    Object getArray() throws SQLException;

    Object getArray(long j, int i) throws SQLException;

    Object getArray(long j, int i, Map<String, Class<?>> map) throws SQLException;

    Object getArray(Map<String, Class<?>> map) throws SQLException;

    int getBaseType() throws SQLException;

    String getBaseTypeName() throws SQLException;

    ResultSet getResultSet() throws SQLException;

    ResultSet getResultSet(long j, int i) throws SQLException;

    ResultSet getResultSet(long j, int i, Map<String, Class<?>> map) throws SQLException;

    ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException;

    void free() throws SQLException;
}