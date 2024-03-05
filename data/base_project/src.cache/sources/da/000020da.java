package gov.nist.core;

import gov.nist.javax.sip.message.SIPMessage;
import java.util.Properties;
import javax.sip.SipStack;

/* loaded from: ServerLogger.class */
public interface ServerLogger extends LogLevels {
    void closeLogFile();

    void logMessage(SIPMessage sIPMessage, String str, String str2, boolean z, long j);

    void logMessage(SIPMessage sIPMessage, String str, String str2, String str3, boolean z, long j);

    void logMessage(SIPMessage sIPMessage, String str, String str2, String str3, boolean z);

    void logException(Exception exc);

    void setStackProperties(Properties properties);

    void setSipStack(SipStack sipStack);
}