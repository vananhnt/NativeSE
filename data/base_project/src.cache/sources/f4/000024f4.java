package java.sql;

import java.util.Properties;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Driver.class */
public interface Driver {
    boolean acceptsURL(String str) throws SQLException;

    Connection connect(String str, Properties properties) throws SQLException;

    int getMajorVersion();

    int getMinorVersion();

    DriverPropertyInfo[] getPropertyInfo(String str, Properties properties) throws SQLException;

    boolean jdbcCompliant();
}