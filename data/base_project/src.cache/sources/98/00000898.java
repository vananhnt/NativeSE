package android.media.videoeditor;

import java.util.HashMap;
import java.util.Map;

/* loaded from: Overlay.class */
public abstract class Overlay {
    private final String mUniqueId;
    private final MediaItem mMediaItem;
    private final Map<String, String> mUserAttributes;
    protected long mStartTimeMs;
    protected long mDurationMs;

    private Overlay() {
        this(null, null, 0L, 0L);
    }

    public Overlay(MediaItem mediaItem, String overlayId, long startTimeMs, long durationMs) {
        if (mediaItem == null) {
            throw new IllegalArgumentException("Media item cannot be null");
        }
        if (startTimeMs < 0 || durationMs < 0) {
            throw new IllegalArgumentException("Invalid start time and/OR duration");
        }
        if (startTimeMs + durationMs > mediaItem.getDuration()) {
            throw new IllegalArgumentException("Invalid start time and duration");
        }
        this.mMediaItem = mediaItem;
        this.mUniqueId = overlayId;
        this.mStartTimeMs = startTimeMs;
        this.mDurationMs = durationMs;
        this.mUserAttributes = new HashMap();
    }

    public String getId() {
        return this.mUniqueId;
    }

    public long getDuration() {
        return this.mDurationMs;
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

    public long getStartTime() {
        return this.mStartTimeMs;
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

    public void setUserAttribute(String name, String value) {
        this.mUserAttributes.put(name, value);
    }

    public Map<String, String> getUserAttributes() {
        return this.mUserAttributes;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Overlay)) {
            return false;
        }
        return this.mUniqueId.equals(((Overlay) object).mUniqueId);
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}