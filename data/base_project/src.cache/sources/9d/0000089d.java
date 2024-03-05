package android.media.videoeditor;

/* loaded from: TransitionFadeBlack.class */
public class TransitionFadeBlack extends Transition {
    private TransitionFadeBlack() {
        this(null, null, null, 0L, 0);
    }

    public TransitionFadeBlack(String transitionId, MediaItem afterMediaItem, MediaItem beforeMediaItem, long durationMs, int behavior) {
        super(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.Transition
    public void generate() {
        super.generate();
    }
}