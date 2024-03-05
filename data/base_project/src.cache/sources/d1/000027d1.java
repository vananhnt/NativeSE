package javax.net.ssl;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLEngineResult.class */
public class SSLEngineResult {
    private final Status status;
    private final HandshakeStatus handshakeStatus;
    private final int bytesConsumed;
    private final int bytesProduced;

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: SSLEngineResult$HandshakeStatus.class */
    public enum HandshakeStatus {
        NOT_HANDSHAKING,
        FINISHED,
        NEED_TASK,
        NEED_WRAP,
        NEED_UNWRAP
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: SSLEngineResult$Status.class */
    public enum Status {
        BUFFER_OVERFLOW,
        BUFFER_UNDERFLOW,
        CLOSED,
        OK
    }

    public SSLEngineResult(Status status, HandshakeStatus handshakeStatus, int bytesConsumed, int bytesProduced) {
        if (status == null) {
            throw new IllegalArgumentException("status is null");
        }
        if (handshakeStatus == null) {
            throw new IllegalArgumentException("handshakeStatus is null");
        }
        if (bytesConsumed < 0) {
            throw new IllegalArgumentException("bytesConsumed is negative");
        }
        if (bytesProduced < 0) {
            throw new IllegalArgumentException("bytesProduced is negative");
        }
        this.status = status;
        this.handshakeStatus = handshakeStatus;
        this.bytesConsumed = bytesConsumed;
        this.bytesProduced = bytesProduced;
    }

    public final Status getStatus() {
        return this.status;
    }

    public final HandshakeStatus getHandshakeStatus() {
        return this.handshakeStatus;
    }

    public final int bytesConsumed() {
        return this.bytesConsumed;
    }

    public final int bytesProduced() {
        return this.bytesProduced;
    }

    public String toString() {
        return "SSLEngineReport: Status = " + this.status + "  HandshakeStatus = " + this.handshakeStatus + "\n                 bytesConsumed = " + this.bytesConsumed + " bytesProduced = " + this.bytesProduced;
    }
}