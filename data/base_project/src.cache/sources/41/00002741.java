package java.util.logging;

import dalvik.system.DalvikLogHandler;
import java.util.ResourceBundle;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Logger.class */
public class Logger {
    public static final String GLOBAL_LOGGER_NAME = "global";
    @Deprecated
    public static final Logger global = null;

    protected Logger(String name, String resourceBundleName) {
        throw new RuntimeException("Stub!");
    }

    public static Logger getAnonymousLogger() {
        throw new RuntimeException("Stub!");
    }

    public static Logger getAnonymousLogger(String resourceBundleName) {
        throw new RuntimeException("Stub!");
    }

    public static Logger getLogger(String name) {
        throw new RuntimeException("Stub!");
    }

    public static Logger getLogger(String name, String resourceBundleName) {
        throw new RuntimeException("Stub!");
    }

    public void addHandler(Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public Handler[] getHandlers() {
        throw new RuntimeException("Stub!");
    }

    public void removeHandler(Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public Filter getFilter() {
        throw new RuntimeException("Stub!");
    }

    public void setFilter(Filter newFilter) {
        throw new RuntimeException("Stub!");
    }

    public Level getLevel() {
        throw new RuntimeException("Stub!");
    }

    public void setLevel(Level newLevel) {
        throw new RuntimeException("Stub!");
    }

    public boolean getUseParentHandlers() {
        throw new RuntimeException("Stub!");
    }

    public void setUseParentHandlers(boolean notifyParentHandlers) {
        throw new RuntimeException("Stub!");
    }

    public Logger getParent() {
        throw new RuntimeException("Stub!");
    }

    public void setParent(Logger parent) {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public ResourceBundle getResourceBundle() {
        throw new RuntimeException("Stub!");
    }

    public String getResourceBundleName() {
        throw new RuntimeException("Stub!");
    }

    public boolean isLoggable(Level l) {
        throw new RuntimeException("Stub!");
    }

    public void entering(String sourceClass, String sourceMethod) {
        throw new RuntimeException("Stub!");
    }

    public void entering(String sourceClass, String sourceMethod, Object param) {
        throw new RuntimeException("Stub!");
    }

    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        throw new RuntimeException("Stub!");
    }

    public void exiting(String sourceClass, String sourceMethod) {
        throw new RuntimeException("Stub!");
    }

    public void exiting(String sourceClass, String sourceMethod, Object result) {
        throw new RuntimeException("Stub!");
    }

    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        throw new RuntimeException("Stub!");
    }

    public void severe(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void warning(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void info(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void config(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void fine(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void finer(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void finest(String msg) {
        throw new RuntimeException("Stub!");
    }

    public void log(Level logLevel, String msg) {
        throw new RuntimeException("Stub!");
    }

    public void log(Level logLevel, String msg, Object param) {
        throw new RuntimeException("Stub!");
    }

    public void log(Level logLevel, String msg, Object[] params) {
        throw new RuntimeException("Stub!");
    }

    public void log(Level logLevel, String msg, Throwable thrown) {
        throw new RuntimeException("Stub!");
    }

    public void log(LogRecord record) {
        throw new RuntimeException("Stub!");
    }

    public void logp(Level logLevel, String sourceClass, String sourceMethod, String msg) {
        throw new RuntimeException("Stub!");
    }

    public void logp(Level logLevel, String sourceClass, String sourceMethod, String msg, Object param) {
        throw new RuntimeException("Stub!");
    }

    public void logp(Level logLevel, String sourceClass, String sourceMethod, String msg, Object[] params) {
        throw new RuntimeException("Stub!");
    }

    public void logp(Level logLevel, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        throw new RuntimeException("Stub!");
    }

    public void logrb(Level logLevel, String sourceClass, String sourceMethod, String bundleName, String msg) {
        throw new RuntimeException("Stub!");
    }

    public void logrb(Level logLevel, String sourceClass, String sourceMethod, String bundleName, String msg, Object param) {
        throw new RuntimeException("Stub!");
    }

    public void logrb(Level logLevel, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
        throw new RuntimeException("Stub!");
    }

    public void logrb(Level logLevel, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.logging.Logger$1  reason: invalid class name */
    /* loaded from: Logger$1.class */
    static class AnonymousClass1 implements DalvikLogHandler {
        AnonymousClass1() {
        }

        public void publish(Logger source, String tag, Level level, String message) {
            LogRecord record = new LogRecord(level, message);
            record.setLoggerName(Logger.access$000(source));
            Logger.access$100(source, record);
            source.log(record);
        }
    }
}