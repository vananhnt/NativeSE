package java.sql;

import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Ref.class */
public interface Ref {
    String getBaseTypeName() throws SQLException;

    Object getObject() throws SQLException;

    Object getObject(Map<String, Class<?>> map) throws SQLException;

    void setObject(Object obj) throws SQLException;
}