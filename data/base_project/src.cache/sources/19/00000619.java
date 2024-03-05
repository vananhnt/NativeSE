package android.hardware.camera2;

/* loaded from: CaptureFailure.class */
public class CaptureFailure {
    public static final int REASON_ERROR = 0;
    public static final int REASON_FLUSHED = 1;
    private final CaptureRequest mRequest;
    private final int mReason;
    private final boolean mDropped;
    private final int mSequenceId;
    private final int mFrameNumber;

    public CaptureFailure(CaptureRequest request, int reason, boolean dropped, int sequenceId, int frameNumber) {
        this.mRequest = request;
        this.mReason = reason;
        this.mDropped = dropped;
        this.mSequenceId = sequenceId;
        this.mFrameNumber = frameNumber;
    }

    public CaptureRequest getRequest() {
        return this.mRequest;
    }

    public int getFrameNumber() {
        return this.mFrameNumber;
    }

    public int getReason() {
        return this.mReason;
    }

    public boolean wasImageCaptured() {
        return !this.mDropped;
    }

    public int getSequenceId() {
        return this.mSequenceId;
    }
}