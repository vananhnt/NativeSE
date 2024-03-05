package android.media.videoeditor;

/* loaded from: TransitionSliding.class */
public class TransitionSliding extends Transition {
    public static final int DIRECTION_RIGHT_OUT_LEFT_IN = 0;
    public static final int DIRECTION_LEFT_OUT_RIGHT_IN = 1;
    public static final int DIRECTION_TOP_OUT_BOTTOM_IN = 2;
    public static final int DIRECTION_BOTTOM_OUT_TOP_IN = 3;
    private final int mSlidingDirection;

    private TransitionSliding() {
        this(null, null, null, 0L, 0, 0);
    }

    public TransitionSliding(String transitionId, MediaItem afterMediaItem, MediaItem beforeMediaItem, long durationMs, int behavior, int direction) {
        super(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior);
        switch (direction) {
            case 0:
            case 1:
            case 2:
            case 3:
                this.mSlidingDirection = direction;
                return;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }
    }

    public int getDirection() {
        return this.mSlidingDirection;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.Transition
    public void generate() {
        super.generate();
    }
}