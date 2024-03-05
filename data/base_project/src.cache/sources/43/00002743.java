package java.util.logging;

import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LoggingMXBean.class */
public interface LoggingMXBean {
    String getLoggerLevel(String str);

    List<String> getLoggerNames();

    String getParentLoggerName(String str);

    void setLoggerLevel(String str, String str2);
}