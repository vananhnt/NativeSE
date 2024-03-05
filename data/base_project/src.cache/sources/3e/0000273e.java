package java.util.logging;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LogManager.class */
public class LogManager {
    public static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";

    protected LogManager() {
        throw new RuntimeException("Stub!");
    }

    public static LoggingMXBean getLoggingMXBean() {
        throw new RuntimeException("Stub!");
    }

    public void checkAccess() {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean addLogger(Logger logger) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Logger getLogger(String name) {
        throw new RuntimeException("Stub!");
    }

    public synchronized Enumeration<String> getLoggerNames() {
        throw new RuntimeException("Stub!");
    }

    public static LogManager getLogManager() {
        throw new RuntimeException("Stub!");
    }

    public String getProperty(String name) {
        throw new RuntimeException("Stub!");
    }

    public void readConfiguration() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void readConfiguration(InputStream ins) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void reset() {
        throw new RuntimeException("Stub!");
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        throw new RuntimeException("Stub!");
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.logging.LogManager$1  reason: invalid class name */
    /* loaded from: LogManager$1.class */
    class AnonymousClass1 extends Thread {
        AnonymousClass1() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            LogManager.this.reset();
        }
    }
}