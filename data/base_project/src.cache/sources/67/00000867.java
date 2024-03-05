package android.media.videoeditor;

/* loaded from: Effect.class */
public abstract class Effect {
    private final String mUniqueId;
    private final MediaItem mMediaItem;
    protected long mDurationMs;
    protected long mStartTimeMs;

    private Effect() {
        this.mMediaItem = null;
        this.mUniqueId = null;
        this.mStartTimeMs = 0L;
        this.mDurationMs = 0L;
    }

    public Effect(MediaItem mediaItem, String effectId, long startTimeMs, long durationMs) {
        if (mediaItem == null) {
            throw new IllegalArgumentException("Media item cannot be null");
        }
        if (startTimeMs < 0 || durationMs < 0) {
            throw new IllegalArgumentException("Invalid start time Or/And Duration");
        }
        if (startTimeMs + durationMs > mediaItem.getDuration()) {
            throw new IllegalArgumentException("Invalid start time and duration");
        }
        this.mMediaItem = mediaItem;
        this.mUniqueId = effectId;
        this.mStartTimeMs = startTimeMs;
        this.mDurationMs = durationMs;
    }

    public String getId() {
        return this.mUniqueId;
    }

    public void setDuration(long durationMs) {
        if (durationMs < 0) {
            throw new IllegalArgumentException("Invalid duration");
        }
        if (this.mStartTimeMs + durationMs > this.mMediaItem.getDuration()) {
            throw new IllegalArgumentException("Duration is too large");
        }
        getMediaItem().getNativeContext().setGeneratePreview(true);
        long oldDurationMs = this.mDurationMs;
        this.mDurationMs = durationMs;
        this.mMediaItem.invalidateTransitions(this.mStartTimeMs, oldDurationMs, this.mStartTimeMs, this.mDurationMs);
    }

    public long getDuration() {
        return this.mDurationMs;
    }

    public void setStartTime(long startTimeMs) {
        if (startTimeMs + this.mDurationMs > this.mMediaItem.getDuration()) {
            throw new IllegalArgumentException("Start time is too large");
        }
        getMediaItem().getNativeContext().setGeneratePreview(true);
        long oldStartTimeMs = this.mStartTimeMs;
        this.mStartTimeMs = startTimeMs;
        this.mMediaItem.invalidateTransitions(oldStartTimeMs, this.mDurationMs, this.mStartTimeMs, this.mDurationMs);
    }

    public long getStartTime() {
        return this.mStartTimeMs;
    }

    public void setStartTimeAndDuration(long startTimeMs, long durationMs) {
        if (startTimeMs + durationMs > this.mMediaItem.getDuration()) {
            throw new IllegalArgumentException("Invalid start time or duration");
        }
        getMediaItem().getNativeContext().setGeneratePreview(true);
        long oldStartTimeMs = this.mStartTimeMs;
        long oldDurationMs = this.mDurationMs;
        this.mStartTimeMs = startTimeMs;
        this.mDurationMs = durationMs;
        this.mMediaItem.invalidateTransitions(oldStartTimeMs, oldDurationMs, this.mStartTimeMs, this.mDurationMs);
    }

    public MediaItem getMediaItem() {
        return this.mMediaItem;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Effect)) {
            return false;
        }
        return this.mUniqueId.equals(((Effect) object).mUniqueId);
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}