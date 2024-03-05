package gov.nist.core;

import java.util.Properties;

/* loaded from: StackLogger.class */
public interface StackLogger extends LogLevels {
    void logStackTrace();

    void logStackTrace(int i);

    int getLineCount();

    void logException(Throwable th);

    void logDebug(String str);

    void logTrace(String str);

    void logFatalError(String str);

    void logError(String str);

    boolean isLoggingEnabled();

    boolean isLoggingEnabled(int i);

    void logError(String str, Exception exc);

    void logWarning(String str);

    void logInfo(String str);

    void disableLogging();

    void enableLogging();

    void setBuildTimeStamp(String str);

    void setStackProperties(Properties properties);

    String getLoggerName();
}