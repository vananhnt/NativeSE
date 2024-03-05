package java.sql;

import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Struct.class */
public interface Struct {
    String getSQLTypeName() throws SQLException;

    Object[] getAttributes() throws SQLException;

    Object[] getAttributes(Map<String, Class<?>> map) throws SQLException;
}