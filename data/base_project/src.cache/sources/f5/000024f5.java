package java.sql;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DriverManager.class */
public class DriverManager {
    DriverManager() {
        throw new RuntimeException("Stub!");
    }

    public static void deregisterDriver(Driver driver) throws SQLException {
        throw new RuntimeException("Stub!");
    }

    public static Connection getConnection(String url) throws SQLException {
        throw new RuntimeException("Stub!");
    }

    public static Connection getConnection(String url, Properties info) throws SQLException {
        throw new RuntimeException("Stub!");
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
        throw new RuntimeException("Stub!");
    }

    public static Driver getDriver(String url) throws SQLException {
        throw new RuntimeException("Stub!");
    }

    public static Enumeration<Driver> getDrivers() {
        throw new RuntimeException("Stub!");
    }

    public static int getLoginTimeout() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static PrintStream getLogStream() {
        throw new RuntimeException("Stub!");
    }

    public static PrintWriter getLogWriter() {
        throw new RuntimeException("Stub!");
    }

    public static void println(String message) {
        throw new RuntimeException("Stub!");
    }

    public static void registerDriver(Driver driver) throws SQLException {
        throw new RuntimeException("Stub!");
    }

    public static void setLoginTimeout(int seconds) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static void setLogStream(PrintStream out) {
        throw new RuntimeException("Stub!");
    }

    public static void setLogWriter(PrintWriter out) {
        throw new RuntimeException("Stub!");
    }
}