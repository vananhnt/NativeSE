package gov.nist.javax.sip.stack;

import gov.nist.core.Separators;
import gov.nist.core.ServerLogger;
import gov.nist.core.StackLogger;
import gov.nist.javax.sip.LogRecord;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.message.SIPMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;
import javax.sip.SipStack;
import javax.sip.header.TimeStampHeader;
import javax.sip.message.Request;

/* loaded from: ServerLog.class */
public class ServerLog implements ServerLogger {
    private boolean logContent;
    protected StackLogger stackLogger;
    private String logFileName;
    private PrintWriter printWriter;
    private String auxInfo;
    private String description;
    private String stackIpAddress;
    private SIPTransactionStack sipStack;
    private Properties configurationProperties;
    protected int traceLevel = 16;

    private void setProperties(Properties configurationProperties) {
        int ll;
        this.configurationProperties = configurationProperties;
        this.description = configurationProperties.getProperty("javax.sip.STACK_NAME");
        this.stackIpAddress = configurationProperties.getProperty("javax.sip.IP_ADDRESS");
        this.logFileName = configurationProperties.getProperty("gov.nist.javax.sip.SERVER_LOG");
        String logLevel = configurationProperties.getProperty("gov.nist.javax.sip.TRACE_LEVEL");
        String logContent = configurationProperties.getProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT");
        this.logContent = logContent != null && logContent.equals("true");
        if (logLevel != null && !logLevel.equals("LOG4J")) {
            try {
                if (logLevel.equals("DEBUG")) {
                    ll = 32;
                } else if (logLevel.equals(Request.INFO)) {
                    ll = 16;
                } else if (logLevel.equals("ERROR")) {
                    ll = 4;
                } else if (logLevel.equals("NONE") || logLevel.equals("OFF")) {
                    ll = 0;
                } else {
                    ll = Integer.parseInt(logLevel);
                }
                setTraceLevel(ll);
            } catch (NumberFormatException e) {
                System.out.println("ServerLog: WARNING Bad integer " + logLevel);
                System.out.println("logging dislabled ");
                setTraceLevel(0);
            }
        }
        checkLogFile();
    }

    public void setStackIpAddress(String ipAddress) {
        this.stackIpAddress = ipAddress;
    }

    @Override // gov.nist.core.ServerLogger
    public synchronized void closeLogFile() {
        if (this.printWriter != null) {
            this.printWriter.close();
            this.printWriter = null;
        }
    }

    public void checkLogFile() {
        if (this.logFileName == null || this.traceLevel < 16) {
            return;
        }
        try {
            File logFile = new File(this.logFileName);
            if (!logFile.exists()) {
                logFile.createNewFile();
                this.printWriter = null;
            }
            if (this.printWriter == null) {
                boolean overwrite = Boolean.valueOf(this.configurationProperties.getProperty("gov.nist.javax.sip.SERVER_LOG_OVERWRITE")).booleanValue();
                FileWriter fw = new FileWriter(this.logFileName, !overwrite);
                this.printWriter = new PrintWriter((Writer) fw, true);
                this.printWriter.println("<!-- Use the  Trace Viewer in src/tools/tracesviewer to view this  trace  \nHere are the stack configuration properties \njavax.sip.IP_ADDRESS= " + this.configurationProperties.getProperty("javax.sip.IP_ADDRESS") + Separators.RETURN + "javax.sip.STACK_NAME= " + this.configurationProperties.getProperty("javax.sip.STACK_NAME") + Separators.RETURN + "javax.sip.ROUTER_PATH= " + this.configurationProperties.getProperty("javax.sip.ROUTER_PATH") + Separators.RETURN + "javax.sip.OUTBOUND_PROXY= " + this.configurationProperties.getProperty("javax.sip.OUTBOUND_PROXY") + Separators.RETURN + "-->");
                this.printWriter.println("<description\n logDescription=\"" + this.description + "\"\n name=\"" + this.configurationProperties.getProperty("javax.sip.STACK_NAME") + "\"\n auxInfo=\"" + this.auxInfo + "\"/>\n ");
                if (this.auxInfo != null) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.stackLogger.logDebug("Here are the stack configuration properties \njavax.sip.IP_ADDRESS= " + this.configurationProperties.getProperty("javax.sip.IP_ADDRESS") + Separators.RETURN + "javax.sip.ROUTER_PATH= " + this.configurationProperties.getProperty("javax.sip.ROUTER_PATH") + Separators.RETURN + "javax.sip.OUTBOUND_PROXY= " + this.configurationProperties.getProperty("javax.sip.OUTBOUND_PROXY") + Separators.RETURN + "gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS= " + this.configurationProperties.getProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS") + Separators.RETURN + "gov.nist.javax.sip.CACHE_SERVER_CONNECTIONS= " + this.configurationProperties.getProperty("gov.nist.javax.sip.CACHE_SERVER_CONNECTIONS") + Separators.RETURN + "gov.nist.javax.sip.REENTRANT_LISTENER= " + this.configurationProperties.getProperty("gov.nist.javax.sip.REENTRANT_LISTENER") + "gov.nist.javax.sip.THREAD_POOL_SIZE= " + this.configurationProperties.getProperty("gov.nist.javax.sip.THREAD_POOL_SIZE") + Separators.RETURN);
                        this.stackLogger.logDebug(" ]]> ");
                        this.stackLogger.logDebug("</debug>");
                        this.stackLogger.logDebug("<description\n logDescription=\"" + this.description + "\"\n name=\"" + this.stackIpAddress + "\"\n auxInfo=\"" + this.auxInfo + "\"/>\n ");
                        this.stackLogger.logDebug("<debug>");
                        this.stackLogger.logDebug("<![CDATA[ ");
                    }
                } else if (this.sipStack.isLoggingEnabled()) {
                    this.stackLogger.logDebug("Here are the stack configuration properties \n" + this.configurationProperties + Separators.RETURN);
                    this.stackLogger.logDebug(" ]]>");
                    this.stackLogger.logDebug("</debug>");
                    this.stackLogger.logDebug("<description\n logDescription=\"" + this.description + "\"\n name=\"" + this.stackIpAddress + "\" />\n");
                    this.stackLogger.logDebug("<debug>");
                    this.stackLogger.logDebug("<![CDATA[ ");
                }
            }
        } catch (IOException e) {
        }
    }

    public boolean needsLogging() {
        return this.logFileName != null;
    }

    public void setLogFileName(String name) {
        this.logFileName = name;
    }

    public String getLogFileName() {
        return this.logFileName;
    }

    private void logMessage(String message) {
        checkLogFile();
        if (this.printWriter != null) {
            this.printWriter.println(message);
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.stackLogger.logInfo(message);
        }
    }

    private void logMessage(String message, String from, String to, boolean sender, String callId, String firstLine, String status, String tid, long time, long timestampVal) {
        LogRecord log = this.sipStack.logRecordFactory.createLogRecord(message, from, to, time, sender, firstLine, tid, callId, timestampVal);
        if (log != null) {
            logMessage(log.toString());
        }
    }

    @Override // gov.nist.core.ServerLogger
    public void logMessage(SIPMessage message, String from, String to, boolean sender, long time) {
        checkLogFile();
        if (message.getFirstLine() == null) {
            return;
        }
        CallID cid = (CallID) message.getCallId();
        String callId = null;
        if (cid != null) {
            callId = cid.getCallId();
        }
        String firstLine = message.getFirstLine().trim();
        String inputText = this.logContent ? message.encode() : message.encodeMessage();
        String tid = message.getTransactionId();
        TimeStampHeader tsHdr = (TimeStampHeader) message.getHeader("Timestamp");
        long tsval = tsHdr == null ? 0L : tsHdr.getTime();
        logMessage(inputText, from, to, sender, callId, firstLine, null, tid, time, tsval);
    }

    @Override // gov.nist.core.ServerLogger
    public void logMessage(SIPMessage message, String from, String to, String status, boolean sender, long time) {
        checkLogFile();
        CallID cid = (CallID) message.getCallId();
        String callId = null;
        if (cid != null) {
            callId = cid.getCallId();
        }
        String firstLine = message.getFirstLine().trim();
        String encoded = this.logContent ? message.encode() : message.encodeMessage();
        String tid = message.getTransactionId();
        TimeStampHeader tshdr = (TimeStampHeader) message.getHeader("Timestamp");
        long tsval = tshdr == null ? 0L : tshdr.getTime();
        logMessage(encoded, from, to, sender, callId, firstLine, status, tid, time, tsval);
    }

    @Override // gov.nist.core.ServerLogger
    public void logMessage(SIPMessage message, String from, String to, String status, boolean sender) {
        logMessage(message, from, to, status, sender, System.currentTimeMillis());
    }

    @Override // gov.nist.core.ServerLogger
    public void logException(Exception ex) {
        if (this.traceLevel >= 4) {
            checkLogFile();
            ex.printStackTrace();
            if (this.printWriter != null) {
                ex.printStackTrace(this.printWriter);
            }
        }
    }

    public void setTraceLevel(int level) {
        this.traceLevel = level;
    }

    public int getTraceLevel() {
        return this.traceLevel;
    }

    public void setAuxInfo(String auxInfo) {
        this.auxInfo = auxInfo;
    }

    @Override // gov.nist.core.ServerLogger
    public void setSipStack(SipStack sipStack) {
        if (sipStack instanceof SIPTransactionStack) {
            this.sipStack = (SIPTransactionStack) sipStack;
            this.stackLogger = this.sipStack.getStackLogger();
            return;
        }
        throw new IllegalArgumentException("sipStack must be a SIPTransactionStack");
    }

    @Override // gov.nist.core.ServerLogger
    public void setStackProperties(Properties stackProperties) {
        setProperties(stackProperties);
    }

    public void setLevel(int jsipLoggingLevel) {
    }
}