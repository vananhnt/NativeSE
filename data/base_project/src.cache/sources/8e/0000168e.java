package android.webkit;

/* loaded from: ConsoleMessage.class */
public class ConsoleMessage {
    private MessageLevel mLevel;
    private String mMessage;
    private String mSourceId;
    private int mLineNumber;

    /* loaded from: ConsoleMessage$MessageLevel.class */
    public enum MessageLevel {
        TIP,
        LOG,
        WARNING,
        ERROR,
        DEBUG
    }

    public ConsoleMessage(String message, String sourceId, int lineNumber, MessageLevel msgLevel) {
        this.mMessage = message;
        this.mSourceId = sourceId;
        this.mLineNumber = lineNumber;
        this.mLevel = msgLevel;
    }

    public MessageLevel messageLevel() {
        return this.mLevel;
    }

    public String message() {
        return this.mMessage;
    }

    public String sourceId() {
        return this.mSourceId;
    }

    public int lineNumber() {
        return this.mLineNumber;
    }
}