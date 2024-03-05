package gov.nist.javax.sip.stack;

import gov.nist.core.Separators;
import gov.nist.javax.sip.LogRecord;

/* loaded from: MessageLog.class */
class MessageLog implements LogRecord {
    private String message;
    private String source;
    private String destination;
    private long timeStamp;
    private boolean isSender;
    private String firstLine;
    private String tid;
    private String callId;
    private long timeStampHeaderValue;

    @Override // gov.nist.javax.sip.LogRecord
    public boolean equals(Object other) {
        if (!(other instanceof MessageLog)) {
            return false;
        }
        MessageLog otherLog = (MessageLog) other;
        return otherLog.message.equals(this.message) && otherLog.timeStamp == this.timeStamp;
    }

    public MessageLog(String message, String source, String destination, String timeStamp, boolean isSender, String firstLine, String tid, String callId, long timeStampHeaderValue) {
        if (message == null || message.equals("")) {
            throw new IllegalArgumentException("null msg");
        }
        this.message = message;
        this.source = source;
        this.destination = destination;
        try {
            long ts = Long.parseLong(timeStamp);
            if (ts < 0) {
                throw new IllegalArgumentException("Bad time stamp ");
            }
            this.timeStamp = ts;
            this.isSender = isSender;
            this.firstLine = firstLine;
            this.tid = tid;
            this.callId = callId;
            this.timeStampHeaderValue = timeStampHeaderValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad number format " + timeStamp);
        }
    }

    public MessageLog(String message, String source, String destination, long timeStamp, boolean isSender, String firstLine, String tid, String callId, long timestampVal) {
        if (message == null || message.equals("")) {
            throw new IllegalArgumentException("null msg");
        }
        this.message = message;
        this.source = source;
        this.destination = destination;
        if (timeStamp < 0) {
            throw new IllegalArgumentException("negative ts");
        }
        this.timeStamp = timeStamp;
        this.isSender = isSender;
        this.firstLine = firstLine;
        this.tid = tid;
        this.callId = callId;
        this.timeStampHeaderValue = timestampVal;
    }

    @Override // gov.nist.javax.sip.LogRecord
    public String toString() {
        String log = "<message\nfrom=\"" + this.source + "\" \nto=\"" + this.destination + "\" \ntime=\"" + this.timeStamp + Separators.DOUBLE_QUOTE + (this.timeStampHeaderValue != 0 ? "\ntimeStamp = \"" + this.timeStampHeaderValue + Separators.DOUBLE_QUOTE : "") + "\nisSender=\"" + this.isSender + "\" \ntransactionId=\"" + this.tid + "\" \ncallId=\"" + this.callId + "\" \nfirstLine=\"" + this.firstLine.trim() + Separators.DOUBLE_QUOTE + " \n>\n";
        return (((log + "<![CDATA[") + this.message) + "]]>\n") + "</message>\n";
    }
}